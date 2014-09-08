package org.spigot.mcbot.sockets;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.spigot.mcbot.storage;
import org.spigot.mcbot.botfactory.mcbot;
import org.spigot.mcbot.botfactory.mcbot.ICONSTATE;
import org.spigot.mcbot.events.ChatEvent;
import org.spigot.mcbot.events.JoinGameEvent;
import org.spigot.mcbot.events.PluginMessageEvent;
import org.spigot.mcbot.events.TeamEvent;
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
import org.spigot.mcbot.packets.PluginMessagePacket;
import org.spigot.mcbot.packets.RespawnPacket;
import org.spigot.mcbot.packets.SpawnPositionPacket;
import org.spigot.mcbot.packets.TeamPacket;
import org.spigot.mcbot.packets.packet;
import org.spigot.mcbot.settings.team_struct;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class connector extends Thread {
	private mcbot bot;
	private Socket sock;
	private AntiAFK afkter;
	private boolean haslogged = false;
	public boolean reconnect = false;
	private List<String> Tablist = new ArrayList<String>();
	private HashMap<String, String> playerTeams = new HashMap<String, String>();
	private HashMap<String, team_struct> TeamsByNames = new HashMap<String, team_struct>();

	public connector(mcbot bot) throws UnknownHostException, IOException {
		this.bot = bot;
		sendmsg("§2Connecting");
		//Define served packets
		packet.ValidPackets.add(ChatPacket.ID);
		packet.ValidPackets.add(KeepAlivePacket.ID);
		packet.ValidPackets.add(JoinGamePacket.ID);
		packet.ValidPackets.add(PlayerListItemPacket.ID);
		packet.ValidPackets.add(RespawnPacket.ID);
		packet.ValidPackets.add(TeamPacket.ID);
		packet.ValidPackets.add(SpawnPositionPacket.ID);
		packet.ValidPackets.add(ConnectionResetPacket.ID);
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
			sock = new Socket(bot.serverip, bot.serverport);
			InputStream input = sock.getInputStream();
			packet reader = new packet(input);
			bot.seticon(ICONSTATE.CONNECTING);
			storage.changemenuitems();
			// First, we must send HandShake and hope for good response
			new HandShakePacket(sock).Write(bot.serverip, bot.serverport);
			new LoginStartPacket(sock).Write(bot.username);
			new LoginSuccessPacket(sock, this).read();
			bot.seticon(ICONSTATE.CONNECTED);
			// Init routine

			this.reconnect = bot.getautoreconnect();
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
		} catch (IllegalArgumentException e) {
			sendmsg("§4Data stream error happened. Error log written into main tab. Please report this.");
			e.printStackTrace();
		} catch (NullPointerException e) {
		} catch (IOException e) {
			sendmsg("§4Disconnected");
			e.printStackTrace();
		} catch (RuntimeException e) {
			sendmsg("§4Error happened. Error log written into main tab. Please report this.");
			e.printStackTrace();
		} catch (Exception e) {
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
		// Send logout commands
		if (bot.sendlogoutcommands()) {
			String[] cmds = bot.getlogoutcommands();
			for (String cmd : cmds) {
				bot.sendtoserver(cmd);
			}
		}
		// Change icon
		bot.seticon(ICONSTATE.DISCONNECTED);
		// Stop afkter process
		if (this.afkter != null) {
			this.afkter.stop();
		}
		// Reset tablist
		bot.resettablist();
		// Deal with socket
		try {
			sock.close();
			sock = null;
		} catch (IOException e) {
		} catch (NullPointerException e) {
		}
		// Modify menu items
		storage.changemenuitems();
		// If we are intended to restart, we wait and do so
		if (this.reconnect) {
			Object sync = new Object();
			synchronized (sync) {
				try {
					sync.wait(bot.getautoreconnectdelay() * 1000);
				} catch (InterruptedException e) {
				}
			}
			// And the magic of restart
			this.sock = null;
			bot.connect(this.reconnect);
		} else {
			storage.changemenuitems();
		}
	}

	private void processpacket(int pid, int len) throws Exception {
		packet pack = null;
		switch (pid) {
			default:
				sendmsg("§4§l§nUnhandled packet " + pid);
				new Ignored_Packet(len, pid, sock.getInputStream()).Read();
			break;

			case KeepAlivePacket.ID:
				// Keep us alive
				pack = new KeepAlivePacket(sock);
				byte[] resp = ((KeepAlivePacket) pack).Read(len - 1);
				((KeepAlivePacket) pack).Write(resp);
			break;

			case JoinGamePacket.ID:
				// join game
				JoinGameEvent joingameevent=new JoinGamePacket(sock).Read();
				if(joingameevent.getMaxPlayers()>30 && joingameevent.getMaxPlayers() <50) {
					//2 Columns 20 rows
					settablesize(2, 20);
				} else if(joingameevent.getMaxPlayers() >= 50) {
					//3 Columns 20 rows
					settablesize(3, 20);
				} else {
					//1 Columns 20 rows
					settablesize(1, 20);
				}
			break;

			case ChatPacket.ID:
				// Chat
				pack = new ChatPacket(sock);
				ChatEvent event = ((ChatPacket) pack).Read();
				String msg = parsechat(event.getMessage());
				sendchatmsg(msg);
				tryandsendlogin();
			break;

			case SpawnPositionPacket.ID:
				// Spawn position
				new SpawnPositionPacket(sock).Read();
			break;

			case RespawnPacket.ID:
				// Respawn
				pack = new RespawnPacket(sock);
				((RespawnPacket) pack).Read();
			break;

			case PlayerListItemPacket.ID:
				// We got tablist update (yay)
				pack = new PlayerListItemPacket(sock);
				((PlayerListItemPacket) pack).Read();
				if (((PlayerListItemPacket) pack).Serve(Tablist)) {
					// Tablist needs to be refreshed
					this.refreshTablist();
				}
			break;

			case DisplayScoreBoardPacket.ID:
				// Scoreboard display
				new DisplayScoreBoardPacket(sock).Read();
			break;

			case TeamPacket.ID:
				// Teams
				this.handleteam(new TeamPacket(sock).Read());
			break;

			case PluginMessagePacket.ID:
				// Plugin message
				new PluginMessagePacket(sock).Read();
			break;
			case ConnectionResetPacket.ID:
				// Server closed connection
				String reason = parsechat(new ConnectionResetPacket(sock.getInputStream()).read());
				sendmsg("§4Server closed connection. (" + reason + ")");
			break;
		}
	}

	public void settablesize(int x, int y) {
		int[] dim = new int[2];
		dim[0] = x;
		dim[1] = y;
		bot.tablistsize = dim;
	}

	private void handleteam(TeamEvent teamevent) {
		String teamname = teamevent.getTeamName();
		if (teamevent.TeamIsBeingCreated()) {
			if (this.TeamsByNames.containsKey(teamname)) {
				// Team already exists in structure
			} else {
				team_struct Team = new team_struct(teamname);
				List<String> players = teamevent.getPlayers();
				Team.players = players;
				Team.teamName = teamevent.getTeamDisplayName();
				Team.setDisplayFormat(teamevent.getPrefix(), teamevent.getSuffix(), teamevent.getColorAsFormatedString());
				// Add to structure
				this.TeamsByNames.put(teamname, Team);
				// Add this team to every listed player
				for (String player : players) {
					this.playerTeams.put(player, teamname);
				}
			}
		} else if (teamevent.TeamIsBeingRemoved()) {
			if (this.TeamsByNames.containsKey(teamname)) {
				// Remove this team from all existing players
				List<String> players = this.TeamsByNames.get(teamname).players;
				for (String player : players) {
					this.playerTeams.remove(player);
				}
				// Remove team from structure
				this.TeamsByNames.remove(teamname);
			}
		} else if (teamevent.TeamInformationAreBeingUpdated()) {
			if (this.TeamsByNames.containsKey(teamname)) {
				this.TeamsByNames.get(teamname).setDisplayFormat(teamevent.getPrefix(), teamevent.getSuffix(), teamevent.getColorAsFormatedString());
			}
		} else if (teamevent.PlayersBeingAdded()) {
			if (this.TeamsByNames.containsKey(teamname)) {
				List<String> players = teamevent.getPlayers();
				this.TeamsByNames.get(teamname).AddPlayers(players);
			}
		} else if (teamevent.PlayersBeingRemoved()) {
			if (this.TeamsByNames.containsKey(teamname)) {
				List<String> players = teamevent.getPlayers();
				this.TeamsByNames.get(teamname).RemovePlayers(players);
			}
		}
		refreshTablist();
	}

	private void tryandsendlogin() {
		// First invocation gets it
		if (!this.haslogged) {
			this.haslogged = true;
			if (bot.sendlogincommands()) {
				this.sendmessage("§bSending login commands");
				String[] cmds = bot.getlogincommands();
				for (String cmd : cmds) {
					this.sendtoserver(cmd);
				}
			}
		}
	}

	@SuppressWarnings({})
	public String parsechat(String str) {
		JsonParser parser = new JsonParser();
		try {
			JsonObject obf = parser.parse(str).getAsJsonObject();

			return jsonreparse(obf);
		} catch (IllegalStateException e) {
			return null;
		}
	}

	private String jsonreparse(JsonObject obj) {
		StringBuilder sb = new StringBuilder();
		// It will always contain text
		String text = obj.getAsJsonPrimitive("text").getAsString();
		sb.append(text);
		// And will also contain extras
		JsonArray extra = obj.getAsJsonArray("extra");
		for (JsonElement extr : extra) {
			if (extr.isJsonPrimitive()) {
				// There is string only
				sb.append(extr.getAsString());
			} else {
				//There is something out there
				text = "";
				String bold = "";
				String reset = "";
				String underline = "";
				String strike = "";
				String color = "";
				String italic = "";
				// Not just string
				JsonObject nobj = extr.getAsJsonObject();
				if (nobj.has("bold")) {
					if (nobj.get("bold").getAsBoolean()) {
						bold = "§l";
					}
				}
				if (nobj.has("strikethrough")) {
					if (nobj.get("strikethrough").getAsBoolean()) {
						strike = "§m";
					}
				}
				if (nobj.has("underlined")) {
					if (nobj.get("underlined").getAsBoolean()) {
						underline = "§n";
					}
				}
				if (nobj.has("reset")) {
					if (nobj.get("reset").getAsBoolean()) {
						reset = "§r";
					}
				}
				if (nobj.has("italic")) {
					if (nobj.get("italic").getAsBoolean()) {
						italic = "§o";
					}
				}
				if (nobj.has("color")) {
					color = MCCOLOR.valueOf(nobj.get("color").getAsString()).val;
				}
				if (nobj.has("text")) {
					text = nobj.get("text").getAsString();
				}

				sb.append(bold + underline + strike + italic + color + reset + text);
			}
		}

		return sb.toString();
	}

	public void refreshTablist() {
		bot.refreshtablist(Tablist, playerTeams, TeamsByNames);
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
	
	public void sendchatmsg(String message) {
		if (message != null) {
			sendrawmsg("[Server] "+message);
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
