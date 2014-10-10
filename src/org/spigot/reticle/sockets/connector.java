package org.spigot.reticle.sockets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
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
import org.spigot.reticle.events.BotInitEvent;
import org.spigot.reticle.events.ChatEvent;
import org.spigot.reticle.events.ConnectionResetEvent;
import org.spigot.reticle.events.Event;
import org.spigot.reticle.events.JoinGameEvent;
import org.spigot.reticle.events.LoginSuccessEvent;
import org.spigot.reticle.events.PacketReceiveEvent;
import org.spigot.reticle.events.PlayerListEvent;
import org.spigot.reticle.events.PlayerPositionAndLookEvent;
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
	// private boolean compressiondecided = false;
	private long lastmessagetime = 0;
	private long lastmessagetimewithoutautomessage = 0;

	public boolean reconnect = false;
	private List<String> Tablist = new ArrayList<String>();
	private HashMap<String, String> Tablist_nicks = new HashMap<String, String>();

	private HashMap<String, String> playerTeams = new HashMap<String, String>();
	private HashMap<String, team_struct> TeamsByNames = new HashMap<String, team_struct>();

	private long connecttime;

	private TabCompleteHandler tabcomp = new TabCompleteHandler();

	private int maxpacketid = 0x40; // Default limit
	private int protocolversion = 4; // Default to 1.7.2
	private packet reader;
	private InputStream cis;
	private OutputStream cos;
	private boolean communicationavailable = true;

	private InputStream cin;
	private OutputStream cout;

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
	 * 
	 * @return Returns timestamp of moment when connection is created
	 */
	public long getConnectTime() {
		return connecttime;
	}

	/**
	 * @return Returns bot tab name
	 */
	public String getTabName() {
		return bot.gettabname();
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
		Cipher cipher2 = Cipher.getInstance("AES/CFB8/NoPadding");
		cipher2.init(Cipher.ENCRYPT_MODE, keystr, ivr);
		cos = new CipherOutputStream(sock.getOutputStream(), cipher2);
		reader.setEncryptedStreams(cis, cos);
		cin = cis;
		cout = cos;
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

	/**
	 * Interrupts communication so next packet is not processed and connection
	 * is closed
	 */
	public void interruptCommunication() {
		communicationavailable = false;
	}

	/**
	 * Sends packet to server, if available
	 * @param packet
	 * @throws IOException
	 */
	public void sendIfAvailable(packetStruct packet) throws IOException {
		reader.Send(packet);
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

			BotInitEvent botinitevent = new BotInitEvent(bot);
			storage.pluginManager.invokeEvent(botinitevent, bot.getAllowedPlugins());

			Tablist = new ArrayList<String>();
			Tablist_nicks = new HashMap<String, String>();
			playerTeams = new HashMap<String, String>();
			TeamsByNames = new HashMap<String, team_struct>();

			haslogged = false;
			encryptiondecided = false;
			sock = null;
			bot.seticon(ICONSTATE.CONNECTING);

			if (bot.useProxy()) {
				Proxy proxy = bot.getProxyAddress();
				if (proxy != null) {
					sock = new Socket(proxy);
					sock.connect(bot.getServerAddress());
				} else {
					bot.logmsg("§4Invalid proxy");
					this.reconnect = false;
					return;
				}
			} else {
				sock = new Socket(bot.serverip, bot.serverport);
			}
			cin = sock.getInputStream();
			cout = sock.getOutputStream();
			reader = new packet(bot, cin, cout);
			reader.ProtocolVersion = protocolversion;
			// definepackets(reader);
			storage.changemenuitems();
			// First, we must send HandShake and hope for good response
			new HandShakePacket(reader).Write(bot.serverip, bot.serverport);
			new LoginStartPacket(reader).Write(bot.username);
			connecttime = System.currentTimeMillis() / 1000;
			// Init routine
			communicationavailable = true;
			int pid;
			int len = 0;
			boolean connectedicon = true;
			// Connection established, time to create AntiAFK
			this.afkter = new AntiAFK(this);
			this.afkter.start();
			// The loop
			while (communicationavailable) {
				packetStruct packet = reader.readNexter();
				if (packet == null) {
					continue;
				}
				len = packet.packetLength;
				pid = packet.packetID;
				int datalen = packet.dataLength;
				// TODO: Debug part
				// System.out.println("PID: " + Integer.toHexString(pid) +
				// " LEN: " + len);
				if (pid > maxpacketid) {
					sendmsg("Received packet id " + pid + " (Length: " + len + ",Compression: " + reader.compression + ", Encryption: " + reader.isEncrypted() + ")");
					sendmsg("§4Malformed communication");
					break;
				}
				if (connectedicon) {
					bot.seticon(ICONSTATE.CONNECTED);
				}
				PacketReceiveEvent packevent = new PacketReceiveEvent(bot, packet, !encryptiondecided);
				storage.pluginManager.invokeEvent(packevent, bot.getAllowedPlugins());
				if (packevent.isCancelled()) {
					continue;
				}
				if (datalen > 0) {
					ByteBuffer buf = packet.generateBuffer();
					if (encryptiondecided) {
						/*
						 * if (packet.packetID == 0x02) { if
						 * (storage.playerStream.isBundleChat(this)) {
						 * storage.playerStream.sendIfAvailable(packet); } }
						 * else { if (bot.canBundle()) {
						 * storage.playerStream.sendIfAvailable(packet); } }
						 */
						processpacket(pid, datalen, buf, packet);
					} else {
						processpreloginpacket(pid, datalen, buf);
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
	 * @return Returns True if successful, False if otherwise
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
					ChatEvent e = new ChatEvent(bot, Message, true);
					storage.pluginManager.invokeEvent(e, bot.getAllowedPlugins());
					if (e.isCancelled()) {
						return true;
					}
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
		Event e = null;
		switch (pid) {
			case ConnectionResetPacket.ID2: {
				ConnectionResetEvent dcevent = new ConnectionResetPacket(buf, reader).Read();
				sendmsg("§4Server closed connection. Reason:\n" + dcevent.getReason());
				communicationavailable = false;
			}
			break;

			case SetCompressionPacket.ID:
				SetCompressionPacket compack = new SetCompressionPacket(buf, reader);
				compack.Read();
				sendmsg("§2Compression activated");
				reader.compression = true;
			break;

			case LoginSuccessPacket.ID:
				LoginSuccessEvent logse = new LoginSuccessPacket(buf, reader).Read();
				String uuid = logse.getUUID();
				String username = logse.getUsername();
				sendmsg("§2Received UUID: §n" + uuid);
				sendmsg("§2Received Nick: §n" + username);
				nowConnected();
				e = logse;
				encryptiondecided = true;
			break;

			case EncryptionRequestPacket.ID:
				// Maybe the server wants some encryption
				EncryptionRequestPacket encr = new EncryptionRequestPacket(buf, reader);
				try {
					encr.Read();
					encr.Write(bot);
					setEncryption(encr.getSecret(), reader);
				} catch (BufferUnderflowException e1) {
				}
			break;
		}
		if (e != null) {
			storage.pluginManager.invokeEvent(e, bot.getAllowedPlugins());
		}
	}

	private void processpacket(int pid, int len, ByteBuffer buf, packetStruct packet) throws Exception {
		Event e = null;
		switch (pid) {
		// default:
		// sendmsg("§4§l§nUnhandled packet " + pid);
		// new Ignored_Packet(len, reader).Read();
		// break;
		/*
		 * case EntityStatusPacket.ID: e = new EntityStatusPacket(reader,
		 * buf).Read(); break;
		 */

			


			case SignUpdatePacket.ID:
				e = new SignUpdatePacket(buf, reader).Read();
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

			case TabCompletePacket.ID:
				TabCompletePacket tabpack = new TabCompletePacket(reader, buf);
				TabCompleteEvent tabev = tabpack.Read();
				tabcomp.setNames(tabev.getNames());
				tabcomp.getNext();
				e = tabev;
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
				new SpawnPositionPacket(buf, reader).Read();
			break;

			case RespawnPacket.ID:
				new RespawnPacket(buf, reader).Read();
			break;

			case PlayerListItemPacket.ID:
				PlayerListItemPacket playerlistitem = new PlayerListItemPacket(buf, reader);
				PlayerListEvent plevent = playerlistitem.Read(Tablist, Tablist_nicks);
				if (servePlayerList(plevent, Tablist, Tablist_nicks)) {
					this.refreshTablist();
				}
				e = plevent;
			break;

			case DisplayScoreBoardPacket.ID:
				// Scoreboard display
				new DisplayScoreBoardPacket(buf, reader).Read();
			break;

			case SetCompressionPacket.ID2:
				new SetCompressionPacket(buf, reader).Read();
				sendmsg("Compression activated");
				// compack.Write();
				reader.compression = true;
			break;

			case TeamPacket.ID:
				// Teams
				this.handleteam(new TeamPacket(buf, reader).Read());
			break;

			case PluginMessagePacket.ID:
			// Plugin message
			// PluginMessageEvent plmsge = new PluginMessagePacket(buf,
			// reader).Read();
			// e = plmsge;
			// sendmsg("Channel: " + plmsge.getChannel() + " Message: " +
			// plmsge.getMessageAsString());
			break;

			case ConnectionResetPacket.ID:
				ConnectionResetEvent dcevent = new ConnectionResetPacket(buf, reader).Read();
				sendmsg("§4Server closed connection. (" + dcevent.getReason() + ")");
			break;
		}
		if (e != null) {
			storage.pluginManager.invokeEvent(e, bot.getAllowedPlugins());
		}
	}

	private boolean servePlayerList(PlayerListEvent event, List<String> tablist, HashMap<String, String> tablistnick) {
		if (reader.ProtocolVersion >= 47) {
			boolean ret = false;
			for (int i = 0, o = event.UUIDS.size(); i < o; i++) {
				String xUUID = event.UUIDS.get(i);
				if (!event.Onlines.get(o)) {
					if (tablist.contains(xUUID)) {
						tablist.remove(xUUID);
					}
					if (tablistnick.containsKey(xUUID)) {
						tablistnick.remove(xUUID);
					}
				}
			}

			for (int i = 0, o = event.UUIDS.size(); i < o; i++) {
				String xUUID = event.UUIDS.get(i);
				String xname = event.Nicks.get(i);
				boolean xonline = event.Onlines.get(i);
				boolean xchanged = event.Changed.get(i);
				if (tablist.contains(xUUID)) {
					// Already in tablist
					if (xchanged) {
						// Display name changed
						tablistnick.put(xUUID, xname);
						ret = true;
					} else if (!xonline) {
						// Remove us
						tablist.remove(xUUID);
						if (tablistnick.containsKey(xUUID)) {
							tablistnick.remove(xUUID);
						}
						ret = true;
					}
				} else {
					// We are not in tablist yet
					if (xchanged) {
						tablist.add(xUUID);
						tablistnick.put(xUUID, xname);
						ret = true;
					}
				}
			}
			return ret;
		} else {
			if (tablist.contains(event.name)) {
				// We are already in tablist
				if (event.online) {
					// And online (Correct)
				} else {
					// Bot not online (Suicide)
					tablist.remove(event.name);
					return true;
				}
			} else {
				// We are not in tablist yet
				if (event.online) {
					// But online (Must add)
					tablist.add(event.name);
					return true;
				} else {
					// And not online (correct)
				}
			}
		}
		return false;
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
	 * @return Returns formated string
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

	/**
	 * Returns bot associated with this connection
	 * @return Returns bot associated with this connection
	 */
	public mcbot getBot() {
		return bot;
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

					sb.append(bold + underline + strike + italic + color + reset + text + "§r");
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
	 * @return Returns True if connected, False if not connected
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
