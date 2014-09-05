package org.spigot.mcbot.sockets;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.spigot.mcbot.storage;
import org.spigot.mcbot.botfactory.mcbot;
import org.spigot.mcbot.botfactory.mcbot.ICONSTATE;
import org.spigot.mcbot.packets.ChatPacket;
import org.spigot.mcbot.packets.ConnectionResetPacket;
import org.spigot.mcbot.packets.HandShakePacket;
import org.spigot.mcbot.packets.Ignored_Packet;
import org.spigot.mcbot.packets.JoinGamePacket;
import org.spigot.mcbot.packets.KeepAlivePacket;
import org.spigot.mcbot.packets.LoginStartPacket;
import org.spigot.mcbot.packets.LoginSuccessPacket;
import org.spigot.mcbot.packets.packet;
import org.spigot.mcbot.sockets.chatparse.chatclass;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class connector extends Thread {
	private mcbot bot;
	private Socket sock;

	public connector(mcbot bot) throws UnknownHostException, IOException {
		sock = new Socket(bot.serverip, bot.serverport);
		this.bot = bot;
		sendmsg("§2Connecting");
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

	public synchronized void stopMe() {
		bot.seticon(ICONSTATE.DISCONNECTED);
		try {
			sock.close();
		} catch (IOException e) {
		} catch (NullPointerException e) {
		}
		sock = null;
		storage.changemenuitems();
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
				((JoinGamePacket) pack).Read();
			break;

			case 2:
				pack = new ChatPacket(sock);
				String msg = ((ChatPacket) pack).Read();
				msg = parsechat(msg);
				sendmsg(msg);
			break;

			case 64:
				// Server closed connection
				String reason = new ConnectionResetPacket(sock.getInputStream()).read();
				sendmsg("§4Server closed connection. (" + reason + ")");
		}
		return pack;
	}

	public String parsechat(String str) {
		Gson obj = new Gson();
		chatparse ob = null;
		try {
			ob = obj.fromJson(str, chatparse.class);
		} catch (JsonSyntaxException e) {

		}
		if (ob != null) {
			StringBuilder sb = new StringBuilder();
			for (chatclass obg : ob.extra) {
				//this should never happen but...
				if(obg.color==null) {
					obg.color="none";
				}
				String color = obg.color.toLowerCase();
				if (!color.equals("none")) {
					sb.append(MCCOLOR.valueOf(color).val);
				}
				if (obg.bold) {
					sb.append("§l");
				}
				if (obg.strikethrough) {
					sb.append("§m");
				}
				if (obg.italic) {
					sb.append("§n");
				}
				if (obg.reset) {
					sb.append("§r");
				}
				sb.append(obg.text);
			}
			return sb.toString();
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
