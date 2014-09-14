package org.spigot.reticle.sockets;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.sql.rowset.serial.SerialException;

import org.spigot.reticle.storage;
import org.spigot.reticle.botfactory.mcbot;
import org.spigot.reticle.botfactory.mcbot.ICONSTATE;
import org.spigot.reticle.events.ChatEvent;
import org.spigot.reticle.events.JoinGameEvent;
import org.spigot.reticle.events.PluginMessageEvent;
import org.spigot.reticle.events.TeamEvent;
import org.spigot.reticle.packets.ChatPacket;
import org.spigot.reticle.packets.ConnectionResetPacket;
import org.spigot.reticle.packets.DisplayScoreBoardPacket;
import org.spigot.reticle.packets.EncryptionRequestPacket;
import org.spigot.reticle.packets.HandShakePacket;
import org.spigot.reticle.packets.Ignored_Packet;
import org.spigot.reticle.packets.JoinGamePacket;
import org.spigot.reticle.packets.KeepAlivePacket;
import org.spigot.reticle.packets.LoginStartPacket;
import org.spigot.reticle.packets.LoginSuccessPacket;
import org.spigot.reticle.packets.PlayerListItemPacket;
import org.spigot.reticle.packets.PluginMessagePacket;
import org.spigot.reticle.packets.RespawnPacket;
import org.spigot.reticle.packets.SetCompressionPacket;
import org.spigot.reticle.packets.SpawnPositionPacket;
import org.spigot.reticle.packets.TeamPacket;
import org.spigot.reticle.packets.packet;
import org.spigot.reticle.settings.team_struct;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class connector extends Thread {
	private mcbot bot;
	private Socket sock;
	private AntiAFK afkter;
	private boolean haslogged = false;
	private boolean hasloggedin = false;
	private boolean encryptiondecided = false;
	private boolean compressiondecided = false;

	public boolean reconnect = false;
	private List<String> Tablist = new ArrayList<String>();
	private HashMap<String, String> Tablist_nicks = new HashMap<String, String>();

	private HashMap<String, String> playerTeams = new HashMap<String, String>();
	private HashMap<String, team_struct> TeamsByNames = new HashMap<String, team_struct>();

	private int maxpacketid = 0x40;
	private int protocolversion = 4;
	// private boolean compression = false;
	private packet reader;
	private CipherInputStream cis;
	private CipherOutputStream cos;
	private boolean encryption = false;
	private boolean communicationavailable = true;

	public connector(mcbot bot) throws UnknownHostException, IOException {
		this.bot = bot;
		this.protocolversion = bot.getprotocolversion();
	}

	public int getprotocolversion() {
		return this.protocolversion;
	}

	private void definepackets(packet packet) {
		// Define served packets
		// packet.ValidPackets.add(PluginMessagePacket.ID);
		packet.ValidPackets.add(ChatPacket.ID);
		packet.ValidPackets.add(KeepAlivePacket.ID);
		packet.ValidPackets.add(JoinGamePacket.ID);
		packet.ValidPackets.add(PlayerListItemPacket.ID);
		// packet.ValidPackets.add(RespawnPacket.ID);
		packet.ValidPackets.add(TeamPacket.ID);
		// packet.ValidPackets.add(SpawnPositionPacket.ID);
		packet.ValidPackets.add(ConnectionResetPacket.ID);
		packet.ValidPackets.add(SetCompressionPacket.ID);
		packet.ValidPackets.add(SetCompressionPacket.ID2);

	}

	public int getantiafkperiod() {
		return this.bot.getantiafkperiod();
	}

	public String[] getignoredmessages() {
		return this.bot.getignoredmessages();
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

	public void setEncryption(byte[] keystr, packet reader) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IOException {
		SecretKeySpec key = new SecretKeySpec(keystr, "AES");
		IvParameterSpec ivr = new IvParameterSpec(keystr);
		Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
		cipher.init(Cipher.DECRYPT_MODE, key, ivr);
		cis = new CipherInputStream(sock.getInputStream(), cipher);
		cos = new CipherOutputStream(sock.getOutputStream(), cipher);
		reader.setEncryptedStreams(cis, cos);
		reader.setEncrypted();
		this.encryption = true;
		sendmsg("Encryption activated");
	}

	@Override
	public synchronized void run() {
		storage.changemenuitems();
		do {
			bot.seticon(ICONSTATE.CONNECTING);
			mainloop();
			if (reconnect) {
				bot.seticon(ICONSTATE.CONNECTING);
				try {
					wait(bot.getautoreconnectdelay() * 1000);
				} catch (InterruptedException e) {
				}
			}
		} while (reconnect);
		bot.seticon(ICONSTATE.DISCONNECTED);
		storage.changemenuitems();
		bot.connector = null;
	}

	private void mainloop() {
		// Main loop
		try {
			communicationavailable = true;
			sendmsg("§2Connecting");
			if (protocolversion == 4 || protocolversion == 5) {
				this.maxpacketid = 0x40;
			} else if (protocolversion == 47) {
				this.maxpacketid = 0x49;
			}
			haslogged = false;
			hasloggedin = false;
			encryptiondecided = false;
			sock = null;
			bot.seticon(ICONSTATE.CONNECTING);
			sock = new Socket(bot.serverip, bot.serverport);
			InputStream input = sock.getInputStream();
			reader = new packet(sock.getInputStream(), sock.getOutputStream());
			definepackets(reader);
			storage.changemenuitems();
			// First, we must send HandShake and hope for good response
			new HandShakePacket(reader, protocolversion).Write(bot.serverip, bot.serverport);
			new LoginStartPacket(reader, protocolversion).Write(bot.username);

			// Init routine
			int pid;
			int len = 0;
			int[] pack = new int[2];
			boolean connectedicon = true;
			// Connection established, time to create AntiAFK
			this.afkter = new AntiAFK(this);
			this.afkter.start();
			byte[] bytes = null;
			// The loop
			while (communicationavailable) {
				if (reader.compression) {
					bytes = reader.readNextCompressed();
					pid = reader.getCompressedID(bytes);
					len = reader.getCompressedLen(bytes);
					bytes = Arrays.copyOfRange(bytes, reader.getVarntCount(pid), bytes.length);
				} else {
					pack = reader.readNext();
					len = pack[0];
					pid = pack[1];
				}
				if (pid > maxpacketid) {
					sendmsg("Received packet id " + pid + " (Length: " + len + ",Compression: " + reader.compression + ", Encryption: " + encryption + ")");
					sendmsg("§4Malformed communication");
					break;
				}
				if (connectedicon) {
					bot.seticon(ICONSTATE.CONNECTED);
				}
				if (reader.ValidPackets.contains(pid)) {

					// We shall serve this one
					if (reader.compression) {
						int len2 = len - reader.getVarntCount(pid);
						ByteBuffer buf = ByteBuffer.wrap(bytes);
						processpacket(pid, len2, buf);
					} else {
						int len2 = len - reader.getVarntCount(pid);
						if (len2 > 0) {
							byte[] b = new byte[len2];
							sock.getInputStream().read(b, 0, len2);
							ByteBuffer buf = ByteBuffer.wrap(b);
							processpacket(pid, len2, buf);
						}
					}
					if (!encryptiondecided) {
						encryptiondecided = true;
					}
				} else {
					if (reader.compression) {
					} else {
						// We decided to ignore this one
						new Ignored_Packet(len, pid, input).Read();
					}
				}
			}
		} catch (UnknownHostException e) {
			sendmsg("§4No such host is known.");
		} catch (SerialException e) {
		} catch (IllegalArgumentException e) {
			if (!storage.reportthis(e)) {
				sendmsg("§4Data stream error happened. Error log written into main tab. Please report this.");
				e.printStackTrace();
			}
		} catch (NullPointerException e) {
			if (!storage.reportthis(e)) {
				sendmsg("§4Strange error happened. Please report this.");
				e.printStackTrace();
			}
		} catch (SocketException e) {
		} catch (IOException e) {
			if (!storage.reportthis(e)) {
				e.printStackTrace();
			}
		} catch (RuntimeException e) {
			if (!storage.reportthis(e)) {
				sendmsg("§4Error happened. Error log written into main tab. Please report this.");
				e.printStackTrace();
			}
		} catch (Exception e) {
			if (!storage.reportthis(e)) {
				sendmsg("§4Error happened. Error log written into main tab. Please report this.");
				e.printStackTrace();
			}
		} finally {
			sendmsg("§4Connection has been closed");

		}
		try {
			stopMe();
		} catch (BufferUnderflowException e) {
		}
	}

	@SuppressWarnings("deprecation")
	public void stopMe() {
		communicationavailable = false;
		// Send logout commands
		if (bot.sendlogoutcommands()) {
			String[] cmds = bot.getlogoutcommands();
			for (String cmd : cmds) {
				bot.sendtoserver(cmd);
			}
		}
		// Stop afkter process
		if (this.afkter != null) {
			this.afkter.stop();
			this.afkter = null;
		}
		// Reset tablist
		bot.resettablist();
		// Deal with socket
		if (sock != null) {
			try {
				sock.close();
			} catch (IOException e) {
			}
			sock = null;
		}
	}

	public boolean sendtoserver(String msg) {
		if (msg.length() > 0) {
			if (this.sock != null) {
				try {
					new ChatPacket(null, reader, protocolversion).Write(msg);
					return true;
				} catch (IOException e) {
					return false;
				}
			}
		}
		return false;
	}

	private void processpacket(int pid, int len, ByteBuffer buf) throws Exception {
		packet pack = null;
		switch (pid) {
			default:
				sendmsg("§4§l§nUnhandled packet " + pid);
				new Ignored_Packet(len, pid, sock.getInputStream()).Read();
			break;

			case KeepAlivePacket.ID:
				if (len > 20) {
					// This is probably not keep alive
					String reason = new ConnectionResetPacket(buf, reader).Read();
					sendmsg("§4Server closed connection. Reason:\n" + reason);
					this.stopMe();
				} else {
					// Keep us alive
					KeepAlivePacket keepalivepack = new KeepAlivePacket(reader, protocolversion, buf);
					keepalivepack.Read();
					keepalivepack.Write();
				}
			break;

			case JoinGamePacket.ID:
				if (encryptiondecided) {
					// join game
					JoinGameEvent joingameevent = new JoinGamePacket(buf, reader, protocolversion).Read();
					if (joingameevent.getMaxPlayers() > 25 && joingameevent.getMaxPlayers() < 50) {
						// 2 Columns 20 rows
						settablesize(2, 20);
					} else if (joingameevent.getMaxPlayers() >= 50 && joingameevent.getMaxPlayers() < 70) {
						// 3 Columns 20 rows
						settablesize(3, 20);
					} else if (joingameevent.getMaxPlayers() >= 70) {
						// 4 Columns 20 rows
						settablesize(4, 20);
					} else {
						// 1 Columns 20 rows
						settablesize(1, 20);
					}
					this.reconnect = bot.getautoreconnect();
					encryptiondecided = true;

				} else {
					// Maybe the server wants some encryption
					EncryptionRequestPacket encr = new EncryptionRequestPacket(buf, reader);
					encr.Read();
					encr.Write();
					setEncryption(encr.getSecret(), reader);

					// Join game packet expected
					int[] tmppack = reader.readNext();
					len = tmppack[0];
					pid = tmppack[1];
					if (pid == 0) {
						int len2 = len - reader.getVarntCount(pid);
						byte[] b = new byte[len2];
						cis.read(b, 0, len2);
						ByteBuffer buff = ByteBuffer.wrap(b);
						LoginSuccessPacket lsp = new LoginSuccessPacket(buff, reader, len2);
						String[] data = lsp.Read();
						if (data[1] == null) {
							sendmsg("§4Receive abnormal message: " + data[0]);
							stopMe();
							return;
						} else {
							encryptiondecided = true;
						}
					} else {
						sendmsg("§4 Server did not confirm encryption :(");
						stopMe();
					}
				}
			break;

			case ChatPacket.ID:
				if (hasloggedin) {
					// Chat
					ChatEvent event = new ChatPacket(buf, reader, protocolversion).Read();
					String msg = parsechat(event.getMessage());
					if (!isMessageIgnored(msg)) {
						sendchatmsg(msg);
					}
					tryandsendlogin();
				} else {
					String uuid = new LoginSuccessPacket(buf, reader, protocolversion).Read()[0];
					hasloggedin = true;
					sendmsg("§2Received UUID: §n" + uuid);
				}
			break;

			case SpawnPositionPacket.ID:
				// Spawn position
				new SpawnPositionPacket(buf, reader, protocolversion).Read();
			break;

			case RespawnPacket.ID:
				// Respawn
				pack = new RespawnPacket(buf, reader);
				((RespawnPacket) pack).Read();
			break;

			case PlayerListItemPacket.ID:
				// We got tablist update (yay)
				PlayerListItemPacket playerlistitem = new PlayerListItemPacket(buf, reader, protocolversion);
				playerlistitem.Read();
				if (playerlistitem.Serve(Tablist, Tablist_nicks)) {
					// Tablist needs to be refreshed
					this.refreshTablist();
				}
			break;

			case DisplayScoreBoardPacket.ID:
				// Scoreboard display
				new DisplayScoreBoardPacket(buf, reader).Read();
			break;

			case SetCompressionPacket.ID:
			case SetCompressionPacket.ID2:

				if (!compressiondecided) {
					SetCompressionPacket compack = new SetCompressionPacket(buf, reader, protocolversion);
					compack.Read();
					sendmsg("Compression activated");
					// compack.Write();
					compressiondecided = true;
				}
			break;

			case TeamPacket.ID:
				// Teams
				this.handleteam(new TeamPacket(buf, reader, protocolversion).Read());
			break;

			case PluginMessagePacket.ID:
				// Plugin message
				PluginMessageEvent plmsge = new PluginMessagePacket(buf, reader).Read();
				sendmsg("Channel: " + plmsge.getChannel() + " Message: " + plmsge.getMessageAsString());
			break;

			case ConnectionResetPacket.ID:
				// Server closed connection
				String reason = parsechat(new ConnectionResetPacket(buf, reader).Read());
				sendmsg("§4Server closed connection. (" + reason + ")");
			break;
		}
	}

	private boolean isMessageIgnored(String msg) {
		if (msg == null) {
			return false;
		}
		String parsedmsg = storage.stripcolors(msg);
		String[] ignored = getignoredmessages();
		for (String str : ignored) {
			if (parsedmsg.equals(str)) {
				return true;
			}
		}
		return false;
	}

	public void settablesize(int x, int y) {
		// int[] dim = new int[2];
		// dim[0] = x;
		// dim[1] = y;
		// bot.tablistsize = dim;
		bot.setTabSize(y, x);
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

	public static String parsechat(String str) {
		JsonParser parser = new JsonParser();
		try {
			JsonObject obf = parser.parse(str).getAsJsonObject();
			return jsonreparse(obf);
		} catch (IllegalStateException e) {
			return parser.parse(str).getAsString();
		} catch (JsonSyntaxException e) {
			return null;
		}
	}

	private static String jsonreparse(JsonObject obj) {
		StringBuilder sb = new StringBuilder();
		String text = "";
		// JsonArray extra;
		if (obj.has("text")) {
			text = obj.getAsJsonPrimitive("text").getAsString();
			sb.append(text);
		}
		// And will also contain extras
		if (obj.has("extra")) {
			JsonArray extra = obj.getAsJsonArray("extra");
			for (JsonElement extr : extra) {
				if (extr.isJsonPrimitive()) {
					// There is string only
					sb.append(extr.getAsString());
				} else {
					// There is something out there
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
		}

		return sb.toString();
	}

	public void refreshTablist() {
		if (protocolversion >= 47) {
			bot.refreshtablist(Tablist, Tablist_nicks, playerTeams, TeamsByNames);
		} else {
			bot.refreshtablist(Tablist, playerTeams, TeamsByNames);
		}
	}

	public enum MCCOLOR {
		black("§0"), dark_blue("§1"), dark_green("§2"), dark_aqua("§3"), dark_red("§4"), dark_purple("§5"), gold("§6"), gray("§7"), dark_gray("§8"), blue("§9"), green("§a"), aqua("§b"), red("§c"), light_purple("§d"), yellow("§e"), white("§f");
		public String val;

		MCCOLOR(String val) {
			this.val = val;
		}
	}

	public boolean isConnected(boolean forced) {
		return (sock != null);
	}

	public boolean isConnected() {
		return (sock != null || this.reconnect);
	}

	public boolean isConnectedAllowReconnect() {
		return (sock != null);
	}

	public void sendchatmsg(String message) {
		if (message != null) {
			sendrawmsg("[Server] " + message);
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
