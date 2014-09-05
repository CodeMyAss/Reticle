package org.spigot.mcbot.sockets;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;

import org.spigot.mcbot.storage;
import org.spigot.mcbot.botfactory.mcbot;
import org.spigot.mcbot.botfactory.mcbot.ICONSTATE;
import org.spigot.mcbot.packets.ChatPacket;
import org.spigot.mcbot.packets.ConnectionResetPacket;
import org.spigot.mcbot.packets.DisplayScoreBoardPacket;
import org.spigot.mcbot.packets.HandShakePacket;
import org.spigot.mcbot.packets.Ignored_Packet;
import org.spigot.mcbot.packets.JoinGamePacket;
import org.spigot.mcbot.packets.KeepAlivePacket;
import org.spigot.mcbot.packets.LoginStartPacket;
import org.spigot.mcbot.packets.LoginSuccessPacket;
import org.spigot.mcbot.packets.PlayerListItemPacket;
import org.spigot.mcbot.packets.packet;
import org.spigot.mcbot.sockets.chatparse.chatclass;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class connector extends Thread {
	private mcbot bot;
	private Socket sock;
	private AntiAFK afkter;
	public boolean reconnect=false;
	private HashMap<String, Short> Tablist = new HashMap<String, Short>();

	public connector(mcbot bot) throws UnknownHostException, IOException {
		sock = new Socket(bot.serverip, bot.serverport);
		this.bot = bot;
		sendmsg("§2Connecting");
	}

	public int getantiafkperiod() {
		return this.bot.getantiafkperiod();
	}

	public String[] getlogincommands() {
		return this.bot.getlogincommands();
	}

	public String[] getlogoutcommands() {
		return this.bot.getlogoutcommands();
	}

	public String[] getafkcommands() {
		return this.bot.getafkcommands();
	}

	public boolean sendlogincommands() {
		return this.bot.sendlogincommands();
	}

	public boolean sendlogoutcommands() {
		return this.bot.sendlogoutcommands();
	}

	public boolean sendafkcommands() {
		return this.bot.sendafkcommands();
	}

	@Override
	public void run() {
		// Main loop
		try {
			InputStream input = sock.getInputStream();
			packet reader = new packet(input);
			bot.seticon(ICONSTATE.CONNECTING);
			storage.changemenuitems();
			// First, we must send HandShake and hope for good response
			new HandShakePacket(sock).Write(bot.serverip, bot.serverport);
			new LoginStartPacket(sock).Write(bot.username);
			new LoginSuccessPacket(sock, this).read();
			bot.seticon(ICONSTATE.CONNECTED);
			int pid;
			int len;
			int[] pack = new int[2];

			// Connection established, time to create AntiAFK
			this.afkter = new AntiAFK(this);
			this.afkter.start();

			while (true) {
				pack = reader.readNext();
				len = pack[0];
				pid = pack[1];

				while (pid > packet.MAXPACKETID) {
					pack = reader.readNext();
					len = pack[0];
					pid = pack[1];
				}

				// sendmsg("Received packet id " + pid + " LEN: " + len);
				if (pid > packet.MAXPACKETID) {
					sendmsg("§4Malformed communication");
					break;
				}

				if (packet.ValidPackets.contains(pid)) {
					if (len > 1) {
						// We shall serve this one
						processpacket(pid, len);
					}
					// We have good one
				} else {
					// We decided to ignore this one
					new Ignored_Packet(len, pid, input).Read();
				}
			}
		} catch (NullPointerException e) {
		} catch (IOException e) {
			sendmsg("§4Disconnected");
		} catch (RuntimeException e) {
			sendmsg("§4Error happened. Error log written into main tab. Please report this.");
			e.printStackTrace();
		}
		stopMe();
	}

	public synchronized boolean sendtoserver(String msg) {
		try {
			new ChatPacket(this.sock).Write(msg);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	@SuppressWarnings("deprecation")
	public synchronized void stopMe() {
		//Send logout commands
		if(bot.sendlogoutcommands()) {
			String[] cmds=bot.getlogoutcommands();
			for(String cmd:cmds) {
				bot.sendtoserver(cmd);
			}
		}
		bot.seticon(ICONSTATE.DISCONNECTED);
		this.afkter.stop();
		try {
			sock.close();
		} catch (IOException e) {
		} catch (NullPointerException e) {
		}
		sock = null;
		storage.changemenuitems();
		//If we are intended to restart, we wait and do so
		if(this.reconnect) {
			Object sync=new Object();
			synchronized(sync) {
				try {
					sync.wait(bot.getautoreconnectdelay()*1000);
				} catch (InterruptedException e) {
				}
			}
			//And the magic of restart
			run();
		}
	}

	private packet processpacket(int pid, int len) throws IOException {
		packet pack = null;
		switch (pid) {
			default:
				sendmsg("§4§l§nUnhandled packet " + pid);
				new Ignored_Packet(len, pid, sock.getInputStream()).Read();
			break;

			case 0:
				// Keep us alive
				pack = new KeepAlivePacket(sock);
				byte[] resp = ((KeepAlivePacket) pack).Read(len - 1);
				((KeepAlivePacket) pack).Write(resp);
			break;

			// Never served (We don't really care about the data here (yet)
			case 1:
				// join game
				pack = new JoinGamePacket(sock);
				((JoinGamePacket) pack).Read(this);
				// In reaction to this packet, send commands are sent (If
				// enabled)
				if (bot.sendlogincommands()) {
					String[] cmds = bot.getlogincommands();
					for(String cmd:cmds) {
						this.sendtoserver(cmd);
					}
				}

			break;

			case 2:
				pack = new ChatPacket(sock);
				String msg = ((ChatPacket) pack).Read();
				msg = parsechat(msg);
				sendmsg(msg);
			break;

			case 56:
				// We got tablist update (yay)
				pack = new PlayerListItemPacket(sock);
				((PlayerListItemPacket) pack).Read();
				((PlayerListItemPacket) pack).Serve(Tablist);
				bot.refreshtablist(Tablist);
			break;

			case 61:
				// Scoreboard display
				new DisplayScoreBoardPacket(sock).Read();
			break;

			case 64:
				// Server closed connection
				String reason = new ConnectionResetPacket(sock.getInputStream()).read();
				sendmsg("§4Server closed connection. (" + reason + ")");
		}
		return pack;
	}

	public void settablesize(int x, int y) {
		int[] dim = new int[2];
		dim[0] = x;
		dim[1] = y;
		bot.tablistsize = dim;
	}

	private String reparser(Collection<chatclass> extra) {
		StringBuilder sb = new StringBuilder();
		boolean finalreset = false;
		for (chatclass obg : extra) {
			// this should never happen but...
			if (obg.color == null) {
				obg.color = "none";
			}
			String color = obg.color.toLowerCase();
			if (finalreset) {
				sb.append("§r");
				finalreset = false;
			}
			if (obg.bold) {
				sb.append("§l");
				finalreset = true;
			}
			if (obg.strikethrough) {
				sb.append("§m");
				finalreset = true;
			}
			if (obg.italic) {
				sb.append("§o");
				finalreset = true;
			}
			if (obg.underlined) {
				sb.append("§n");
				finalreset = true;
			}
			if (!color.equals("none")) {
				sb.append(MCCOLOR.valueOf(color).val);
			}
			if (obg.reset) {
				sb.append("§r");
			}
			sb.append(obg.text);
			if (obg.extra != null) {
				sb.append(reparser(extra));
			}
		}
		return sb.toString();
	}

	public String parsechat(String str) {
		Gson obj = new Gson();
		chatparse ob = null;
		try {
			ob = obj.fromJson(str, chatparse.class);
		} catch (JsonSyntaxException e) {
		}
		if (ob != null) {
			String strr = reparser(ob.extra);
			return strr;
		} else {
			return null;
		}
	}

	public enum MCCOLOR {
		black("§0"), dark_blue("§1"), dark_green("§2"), dark_aqua("§3"), dark_red("§4"), dark_purple("§5"), gold("§6"), gray("§7"), dark_gray("§8"), blue("§9"), green("§a"), aqua("§b"), red("§c"), light_purple("§d"), yellow("§e"), white("§f");
		public String val;

		MCCOLOR(String val) {
			this.val = val;
		}
	}

	public boolean isConnected() {
		if (sock == null) {
			return false;
		} else {
			return sock.isConnected();
		}
	}

	public void sendmessage(String message) {
		if (message != null) {
			sendmsg(message);
		}
	}

	private void sendmsg(String message) {
		if (message != null) {
			sendrawmsg("[Connector] " + message);
		}
	}

	private void sendrawmsg(String message) {
		bot.logmsg(message);
	}

}
