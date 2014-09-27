package org.spigot.reticle.sockets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import java.util.StringTokenizer;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.sql.rowset.serial.SerialException;
import javax.swing.JTextField;

import org.spigot.reticle.storage;
import org.spigot.reticle.botfactory.mcbot;
import org.spigot.reticle.botfactory.mcbot.ICONSTATE;
import org.spigot.reticle.events.ChatEvent;
import org.spigot.reticle.events.Event;
import org.spigot.reticle.events.JoinGameEvent;
import org.spigot.reticle.events.PlayerPositionAndLookEvent;
import org.spigot.reticle.events.PluginMessageEvent;
import org.spigot.reticle.events.TabCompleteEvent;
import org.spigot.reticle.events.TeamEvent;
import org.spigot.reticle.events.UpdateHealthEvent;
import org.spigot.reticle.packets.*;
import org.spigot.reticle.packets.ClientStatusPacket.CLIENT_STATUS;
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
	private boolean encryptiondecided = false;
	private boolean compressiondecided = false;
	private long lastmessagetime = 0;
	private long lastmessagetimewithoutautomessage = 0;

	public boolean reconnect = false;
	private List<String> Tablist = new ArrayList<String>();
	private HashMap<String, String> Tablist_nicks = new HashMap<String, String>();

	private HashMap<String, String> playerTeams = new HashMap<String, String>();
	private HashMap<String, team_struct> TeamsByNames = new HashMap<String, team_struct>();

	private TabCompleteHandler tabcomp = new TabCompleteHandler();

	private int maxpacketid = 0x40; // Default limit
	private int protocolversion = 4; // Default to 1.7.2
	private packet reader;
	private InputStream cis;
	private OutputStream cos;
	private boolean communicationavailable = true;

	private float Health;
	private int pos_x;
	private int pos_y;
	private int pos_z;
	private int Food;
	private float Satur;

	public connector(mcbot bot) throws UnknownHostException, IOException {
		this.bot = bot;
		this.protocolversion = bot.getprotocolversion();
	}

	/**
	 * @return Timestamp of last outgoing message Note that this includes
	 *         automatically sent messages
	 */
	public long getLastOutgoingMessageTime() {
		return lastmessagetime;
	}

	/**
	 * @return Timestamp of last sent message Note that this only includes
	 *         messages sent manually
	 */
	public long getLastMessageSentTime() {
		return lastmessagetimewithoutautomessage;
	}

	protected int getprotocolversion() {
		return this.protocolversion;
	}

	private long delaymessage(boolean automessage) {
		long current = System.currentTimeMillis() / 1000;
		if (bot.messagesDelayed()) {
			int delay = bot.getMessageDelay();
			int towait = (int) (current - lastmessagetime);
			if (towait <= 0) {
				towait = -(towait) + delay;
				lastmessagetime = towait + current;
				if (!automessage) {
					lastmessagetimewithoutautomessage = lastmessagetime;
				}
				return (towait + current);
			} else if (towait <= delay && towait >= 0) {
				lastmessagetime = towait + current;
				if (!automessage) {
					lastmessagetimewithoutautomessage = lastmessagetime;
				}
				return (towait + current);
			} else {
				lastmessagetime = current;
				if (!automessage) {
					lastmessagetimewithoutautomessage = lastmessagetime;
				}
				return 0;
			}
		} else {
			lastmessagetime = current;
			if (!automessage) {
				lastmessagetimewithoutautomessage = lastmessagetime;
			}
			return 0;
		}
	}

	private void definepackets(packet packet) {
		// Define served packets
		// packet.ValidPackets.add(PluginMessagePacket.ID);
		packet.ValidPackets.add(ChatPacket.ID);
		packet.ValidPackets.add(KeepAlivePacket.ID);
		packet.ValidPackets.add(JoinGamePacket.ID);
		packet.ValidPackets.add(PlayerListItemPacket.ID);
		packet.ValidPackets.add(RespawnPacket.ID);
		packet.ValidPackets.add(TeamPacket.ID);
		packet.ValidPackets.add(SpawnPositionPacket.ID);
		packet.ValidPackets.add(ConnectionResetPacket.ID);
		packet.ValidPackets.add(SetCompressionPacket.ID);
		packet.ValidPackets.add(SetCompressionPacket.ID2);
		packet.ValidPackets.add(UpdateHealthPacket.ID);
		packet.ValidPackets.add(PlayerPositionAndLookPacket.ID);
		packet.ValidPackets.add(EntityStatusPacket.ID);
		packet.ValidPackets.add(TabCompletePacket.ID);
	}

	protected int getantiafkperiod() {
		return this.bot.getantiafkperiod();
	}

	protected String[] getignoredmessages() {
		return this.bot.getignoredmessages();
	}

	protected String[] getlogincommands() {
		return this.bot.getlogincommands();
	}

	protected String[] getlogoutcommands() {
		return this.bot.getlogoutcommands();
	}

	protected String[] getafkcommands() {
		return this.bot.getafkcommands();
	}

	protected boolean sendlogincommands() {
		return this.bot.sendlogincommands();
	}

	protected boolean sendlogoutcommands() {
		return this.bot.sendlogoutcommands();
	}

	protected boolean sendafkcommands() {
		return this.bot.sendafkcommands();
	}

	protected void setEncryption(SecretKey keystr, packet reader) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IOException {
		IvParameterSpec ivr = new IvParameterSpec(keystr.getEncoded());
		Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
		cipher.init(Cipher.DECRYPT_MODE, keystr, ivr);
		cis = new CipherInputStream(sock.getInputStream(), cipher);
		cos = new CipherOutputStream(sock.getOutputStream(), cipher);
		reader.setEncryptedStreams(cis, cos);
		reader.setEncrypted();
		sendmsg("§2Encryption activated");
	}

	@Override
	public synchronized void run() {
		storage.changemenuitems();
		do {
			bot.seticon(ICONSTATE.CONNECTING);
			bot.refreshOnlineMode();
			if (!this.bot.verifyonlinesettings()) {
				sendmsg("§4Fatal error: Bot needs Mojang Authentication!");
				break;
			}
			mainloop();
			bot.hideInfoTable();
			sendmsg("§4Connection has been closed");
			stopMe();
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
			sendmsg("§2Connecting");
			if (protocolversion == 4 || protocolversion == 5) {
				this.maxpacketid = 0x40;
			} else if (protocolversion == 47) {
				this.maxpacketid = 0x49;
			}

			Tablist = new ArrayList<String>();
			Tablist_nicks = new HashMap<String, String>();
			playerTeams = new HashMap<String, String>();
			TeamsByNames = new HashMap<String, team_struct>();

			haslogged = false;
			encryptiondecided = false;
			sock = null;
			bot.seticon(ICONSTATE.CONNECTING);
			sock = new Socket(bot.serverip, bot.serverport);
			reader = new packet(bot, sock.getInputStream(), sock.getOutputStream());
			reader.ProtocolVersion = protocolversion;
			definepackets(reader);
			storage.changemenuitems();
			// First, we must send HandShake and hope for good response
			new HandShakePacket(reader).Write(bot.serverip, bot.serverport);
			new LoginStartPacket(reader).Write(bot.username);

			// Init routine
			communicationavailable = true;
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
					if (bytes == null) {
						continue;
					}
					pid = reader.getCompressedID(bytes);
					len = reader.getCompressedLen(bytes);
					bytes = Arrays.copyOfRange(bytes, reader.getVarntCount(pid), bytes.length);
				} else {
					pack = reader.readNext();
					if (pack == null) {
						continue;
					}
					len = pack[0];
					pid = pack[1];
				}
				// TODO: Debug part
				// System.out.println("PID: "+Integer.toHexString(pid)+" LEN: "+len);
				if (pid > maxpacketid) {
					sendmsg("Received packet id " + pid + " (Length: " + len + ",Compression: " + reader.compression + ", Encryption: " + reader.isEncrypted() + ")");
					sendmsg("§4Malformed communication");
					break;
				}
				if (connectedicon) {
					bot.seticon(ICONSTATE.CONNECTED);
				}
				int len2 = len - reader.getVarntCount(pid);
				if (reader.ValidPackets.contains(pid)) {
					// We shall serve this one
					if (reader.compression) {
						ByteBuffer buf = ByteBuffer.wrap(bytes);
						if (encryptiondecided) {
							processpacket(pid, len2, buf);
						} else {
							processpreloginpacket(pid, len2, buf);
						}
					} else {
						if (len2 > 0) {
							byte[] b = reader.readInnerBytes(len2);
							ByteBuffer buf = ByteBuffer.wrap(b);
							if (encryptiondecided) {
								processpacket(pid, len2, buf);
							} else {
								processpreloginpacket(pid, len2, buf);
							}
						}
					}
				} else {
					if (reader.compression) {
						// Compressed packet already ignored
					} else {
						// We decided to ignore this one
						new Ignored_Packet(len2, pid, reader).Read();
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
		}
	}

	/**
	 * Stop the connector (Disconnect)
	 */
	@SuppressWarnings("deprecation")
	public void stopMe() {
		communicationavailable = false;
		// Send logout commands
		if (bot.sendlogoutcommands()) {
			String[] cmds = bot.getlogoutcommands();
			for (String cmd : cmds) {
				sendToServer(cmd, true);
			}
		}
		// Stop afkter process
		if (this.afkter != null) {
			this.afkter.stop();
			this.afkter = null;
		}
		// Reset tablist
		bot.resetPlayerList();
		// Reset info table
		bot.resetInfoTable();
		bot.hideInfoTable();
		// Deal with socket
		if (sock != null) {
			try {
				sock.close();
			} catch (IOException e) {
			}
			sock = null;
		}
	}

	private void sendToServerRaw(String msg, boolean isauto) {
		long delay = delaymessage(isauto);
		if (delay == 0) {
			sendToServerNow(msg);
		} else {
			storage.ChatThread.addJob(new ChatJob(this, msg, delay));
		}
	}

	/**
	 * Sends message to server
	 * 
	 * @param Message
	 * @param isAutomatic
	 * @return
	 */
	public boolean sendToServer(String Message, boolean isAutomatic) {
		if (Message.length() > 0) {
			if (Message.toLowerCase().equals("/revive")) {
				try {
					new ClientStatusPacket(reader).Write(CLIENT_STATUS.PERFORM_RESPAWN);
					sendmsg("§2Respawn requested");
					return true;
				} catch (IOException e) {
					return false;
				}
			} else {
				if (this.sock != null) {
					if (Message.length() >= 100) {
						String[] msgs = splitter(Message, 100);
						for (String m : msgs) {
							sendToServerRaw(m, isAutomatic);
						}
						return true;
					} else {
						sendToServerRaw(Message, isAutomatic);
						return true;
					}
				}
			}
		}
		return false;
	}

	protected void sendToServerNow(String msg) {
		try {
			new ChatPacket(null, reader).Write(msg);
		} catch (IOException e) {
		}
	}

	private String[] splitter(String input, int maxlen) {
		StringTokenizer tok = new StringTokenizer(input, " ");
		StringBuilder output = new StringBuilder(input.length());
		int lineLen = 0;
		while (tok.hasMoreTokens()) {
			String word = tok.nextToken() + " ";
			if (lineLen + word.length() > maxlen) {
				output.append("\n");
				lineLen = 0;
			}
			output.append(word);
			lineLen += word.length();
		}
		return output.toString().split("\n");
	}

	private void processpreloginpacket(int pid, int len, ByteBuffer buf) throws Exception {
		switch (pid) {
			case ConnectionResetPacket.ID2: {
				String reason = new ConnectionResetPacket(buf, reader).Read();
				sendmsg("§4Server closed connection. Reason:\n" + reason);
				communicationavailable = false;
			}
			break;

			case SetCompressionPacket.ID:
				SetCompressionPacket compack = new SetCompressionPacket(buf, reader);
				compack.Read();
				sendmsg("§2Compression activated");
				compressiondecided = true;
			break;

			case LoginSuccessPacket.ID:
				String[] resp = new LoginSuccessPacket(buf, reader).Read();
				String uuid = resp[0];
				String username = resp[1];
				sendmsg("§2Received UUID: §n" + uuid);
				sendmsg("§2Received Nick: §n" + username);
				nowConnected();
				encryptiondecided = true;
				if (!compressiondecided && reader.isEncrypted()) {
					sendmsg("§4Server responded without compressioin enabled. This might be very buggy!");
				}
			break;

			// TODO: Reparse legacy array
			case EncryptionRequestPacket.ID: {
				// Maybe the server wants some encryption
				EncryptionRequestPacket encr = new EncryptionRequestPacket(buf, reader);
				try {
					encr.Read();
					encr.Write(bot);
					setEncryption(encr.getSecret(), reader);
				} catch (BufferUnderflowException e) {
				}
			}
			break;

		}
	}

	private void processpacket(int pid, int len, ByteBuffer buf) throws Exception {
		Event e = null;
		switch (pid) {
			default:
				sendmsg("§4§l§nUnhandled packet " + pid);
				new Ignored_Packet(len, pid, reader).Read();
			break;

			case EntityStatusPacket.ID:
				new EntityStatusPacket(reader, buf).Read();
			break;

			case TimeUpdatePacket.ID:
				new TimeUpdatePacket(buf, reader).Read();
			break;

			case UpdateHealthPacket.ID:
				UpdateHealthEvent upheal = new UpdateHealthPacket(reader, buf).Read();
				this.Health = upheal.getHealth();
				this.Food = upheal.getFood();
				this.Satur = upheal.getSaturation();
				bot.updateHealth(this.Health, this.Food, this.Satur);
			break;

			case PlayerPositionAndLookPacket.ID:
				PlayerPositionAndLookEvent ppal = new PlayerPositionAndLookPacket(reader, buf).Read();
				this.pos_x = (int) ppal.getX();
				this.pos_y = (int) ppal.getY();
				this.pos_z = (int) ppal.getZ();
				bot.updateposition(this.pos_x, this.pos_y, this.pos_z);
			break;

			// TODO: Tab-Complete
			case TabCompletePacket.ID:
				TabCompletePacket tabpack = new TabCompletePacket(reader, buf);
				TabCompleteEvent tabev = tabpack.Read();
				tabcomp.setNames(tabev.getNames());
				tabcomp.getNext();
				e = tabev;
			// handle
			break;

			case KeepAlivePacket.ID:
				// Keep us alive
				KeepAlivePacket keepalivepack = new KeepAlivePacket(reader, buf);
				keepalivepack.Read();
				keepalivepack.Write();
			break;

			case JoinGamePacket.ID:
				// join game
				JoinGameEvent joingameevent = new JoinGamePacket(buf, reader).Read();
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
				e = joingameevent;
			break;

			case ChatPacket.ID:
				// Chat
				ChatEvent event = new ChatPacket(buf, reader).Read();
				if (!event.isIgnored()) {
					String msg = event.getFormatedMessage();
					sendChatMsg(msg);
					tryandsendlogin();
				}
				e = event;
			break;

			case SpawnPositionPacket.ID:
				// Spawn position
				new SpawnPositionPacket(buf, reader).Read();
			break;

			case RespawnPacket.ID:
				// Respawn
				new RespawnPacket(buf, reader).Read();
			break;

			case PlayerListItemPacket.ID:
				// We got tablist update (yay)
				PlayerListItemPacket playerlistitem = new PlayerListItemPacket(buf, reader);
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

			case SetCompressionPacket.ID2:
				new SetCompressionPacket(buf, reader).Read();
				sendmsg("Compression activated");
				// compack.Write();
				compressiondecided = true;
			break;

			case TeamPacket.ID:
				// Teams
				this.handleteam(new TeamPacket(buf, reader).Read());
			break;

			case PluginMessagePacket.ID:
				// Plugin message
				PluginMessageEvent plmsge = new PluginMessagePacket(buf, reader).Read();
				e = plmsge;
				sendmsg("Channel: " + plmsge.getChannel() + " Message: " + plmsge.getMessageAsString());
			break;

			case ConnectionResetPacket.ID:
				// Server closed connection
				String reason = parsechat(new ConnectionResetPacket(buf, reader).Read());
				sendmsg("§4Server closed connection. (" + reason + ")");
			break;
		}
		if (e != null) {
			storage.pluginManager.invokeEvent(e);
		}
	}

	private void nowConnected() {
		this.reconnect = bot.getautoreconnect();
		bot.showInfoTable();
	}

	/**
	 * Checks if message should be ignored
	 * 
	 * @param message
	 *            - Message to be checked
	 * @return True if message should be ignored, false if otherwise
	 */
	public boolean isMessageIgnored(String message) {
		if (message == null) {
			return false;
		}
		String parsedmsg = storage.stripcolors(message);
		String[] ignored = getignoredmessages();
		for (String str : ignored) {
			if (parsedmsg.equals(str)) {
				return true;
			}
		}
		return false;
	}

	protected void settablesize(int x, int y) {
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
				this.sendMessage("§bSending login commands");
				String[] cmds = bot.getlogincommands();
				for (String cmd : cmds) {
					this.sendToServer(cmd, true);
				}
			}
		}
	}

	/**
	 * Parse JSON chat format
	 * 
	 * @param String
	 * @return
	 */
	public static String parsechat(String String) {
		JsonParser parser = new JsonParser();
		try {
			JsonObject obf = parser.parse(String).getAsJsonObject();
			return jsonreparse(obf);
		} catch (IllegalStateException e) {
			if (parser.parse(String).isJsonPrimitive()) {
				return parser.parse(String).getAsString();
			} else {
				return null;
			}
		} catch (JsonSyntaxException e) {
			return null;
		}
	}

	private static String jsonreparse(JsonObject obj) {
		StringBuilder sb = new StringBuilder();
		String text = "";
		if (obj.has("translate")) {
			String who = null, message = null;
			boolean playerchat = false;
			boolean announcement = false;
			boolean playerjoin = false;
			boolean playerleft = false;
			boolean dotty = true;
			// Translatable component
			String type = obj.get("translate").getAsString();
			if (type.equals("chat.type.text")) {
				// Player chat
				playerchat = true;
			} else if (type.equals("chat.type.announcement")) {
				// Announcement
				announcement = true;
			} else if (type.equals("multiplayer.player.joined")) {
				playerjoin = true;
			} else if (type.equals("multiplayer.player.left")) {
				playerleft = true;
			}
			if (obj.has("with")) {
				JsonArray with = obj.get("with").getAsJsonArray();
				for (JsonElement key : with) {
					// Text
					if (key.isJsonPrimitive()) {
						if (playerchat) {
							message = key.getAsString();
						} else if (announcement) {
							who = "§d[" + key.getAsString() + "]";
						} else {
							// Unknown token
							return "";
						}
					} else if (key.isJsonObject()) {
						JsonObject jobj = key.getAsJsonObject();
						if (jobj.has("extra")) {
							message = jsonreparse(jobj);
						} else {
							who = jsonreparse(jobj);
							if (playerjoin) {
								dotty = false;
								who = "§e" + who;
								message = "has joined the game.";
							} else if (playerleft) {
								dotty = false;
								who = "§e" + who;
								message = "has left the game.";
							}
						}
					}
				}
			}
			if (who == null) {
				who = "";
			} else {
				if (dotty) {
					who = who + ": ";
				} else {
					who = who + " ";
				}
			}
			if (message == null) {
				message = "";
			}
			return who + message;
		}
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

	protected void refreshTablist() {
		if (protocolversion >= 47) {
			bot.refreshtablist(Tablist, Tablist_nicks, playerTeams, TeamsByNames);
		} else {
			bot.refreshtablist(Tablist, playerTeams, TeamsByNames);
		}
	}

	protected enum MCCOLOR {
		black("§0"), dark_blue("§1"), dark_green("§2"), dark_aqua("§3"), dark_red("§4"), dark_purple("§5"), gold("§6"), gray("§7"), dark_gray("§8"), blue("§9"), green("§a"), aqua("§b"), red("§c"), light_purple("§d"), yellow("§e"), white("§f");
		public String val;

		MCCOLOR(String val) {
			this.val = val;
		}
	}

	protected boolean isConnected(boolean forced) {
		return (sock != null);
	}

	/**
	 * Returns true if bot is connected
	 * 
	 * @return
	 */
	public boolean isConnected() {
		return (sock != null || this.reconnect);
	}

	/**
	 * Sends message to chat box (Prefix '[Server]')
	 * 
	 * @param Message
	 * @throws IOException
	 */
	public void sendChatMsg(String Message) throws IOException {
		if (Message != null) {
			sendrawmsg("[Server] " + Message);
		}
	}

	/**
	 * Sends message to chat box
	 * 
	 * @param Message
	 */
	public void sendMessage(String Message) {
		if (Message != null) {
			sendmsg(Message);
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

	/**
	 * Invoked when Tab-complete should be invoked
	 * 
	 * @param textArea
	 * @param text
	 */
	public void tabpressed(JTextField textArea, String text) {
		TabCompletePacket pack = new TabCompletePacket(reader, null);
		tabcomp.setComponent(textArea);
		try {
			if (tabcomp.getOriginalMessage() == null) {
				pack.Write(text);
			} else {
				pack.Write(tabcomp.getOriginalMessage());
			}
		} catch (IOException e) {
		}
	}

	/**
	 * Unlock Tab-Complete handler
	 */
	public void unlocktabpress() {
		tabcomp.setOriginal();
	}

}
