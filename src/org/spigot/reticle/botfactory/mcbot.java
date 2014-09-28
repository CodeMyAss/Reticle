package org.spigot.reticle.botfactory;

import java.awt.Color;
import java.awt.Font;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.spigot.reticle.PluginInfo;
import org.spigot.reticle.storage;
import org.spigot.reticle.API.Plugin;
import org.spigot.reticle.events.ConsoleCommandEvent;
import org.spigot.reticle.settings.botsettings;
import org.spigot.reticle.settings.team_struct;
import org.spigot.reticle.sockets.Authenticator;
import org.spigot.reticle.sockets.connector;
import org.spigot.reticle.supporter.supportconnector;
import org.spigot.reticle.sockets.ChatLogger;

public class mcbot {
	private JTextPane chatlog;
	private JTable tabler;
	private JCheckBox autoscroll;
	private JLabel messagecount;
	private JTable tableinfo;
	private botsettings rawbot;
	/**
	 * Indicates state of bot
	 */
	protected boolean isconnected = false;
	protected boolean autoconnect = false;
	protected boolean exists = false;
	public boolean ismain = false;
	private HashMap<String, Style> styles = new HashMap<String, Style>();
	public connector connector;
	public String serverip;
	public int serverport;
	public String username;
	protected int[] tablistsize = new int[2];
	protected boolean tablistdisplayed = true;
	protected Color backgroundcolor = Color.black;
	protected Color foregroundcolor = Color.white;
	protected boolean allowreport = false;
	public boolean allowconnects = true;
	private supportconnector supportconnector;
	private boolean onlinemode = false;
	private ChatLogger ChatLogger;
	private UpListKeeper uplist = new UpListKeeper();
	private JTextField textcommands;

	/**
	 * Returns true if messages are delayed
	 * 
	 * @return Returns True if messages should be delayed, False if otherwise
	 */
	public boolean messagesDelayed() {
		return this.rawbot.messagedelay != 0;
	}

	private boolean StringArrayContains(String str, String[] arr) {
		for (String element : arr) {
			if (element.equals(str)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Soft reconnect (Does not affect reconnect mechanism)
	 */
	public void softReconnect() {
		if (this.isConnected()) {
			this.connector.interruptCommunication();
		} else if (connector != null) {
			boolean reconnect = connector.reconnect;
			this.disconnect();
			this.connect(reconnect);
		}
	}

	/**
	 * Hard reconnect (Reset reconnect mechanism)
	 */
	public void hardReconnect() {
		this.disconnect();
		this.connect();
	}

	/**
	 * @return True if bot tab is active
	 */
	public boolean hasFocus() {
		return storage.gettabbedpane().getSelectedIndex() == storage.gettabbyname(gettabname());
	}

	/**
	 * Add message to list of ignored messages Ignored messages are not
	 * displayed in chat
	 * 
	 * @param Message
	 */
	public void addToIgnoreList(String Message) {
		if (!StringArrayContains(Message, this.rawbot.ignored)) {
			int len = this.rawbot.ignored.length;
			String[] newarr = new String[len + 1];
			for (int i = 0; i < len; i++) {
				newarr[i] = this.rawbot.ignored[i];
			}
			newarr[len] = Message;
			this.rawbot.ignored = newarr;
			storage.savesettings();
			this.logmsg("§2Added message to ignore list.");
		} else {
			this.logmsg("§4Selected message is already in ignore list.");
		}
	}

	protected boolean isMainTab() {
		return (gettabname().equals("Main@Reticle"));
	}

	protected boolean isSupportTab() {
		return (gettabname().equals("Support@Reticle"));
	}

	protected boolean isSpecialTab() {
		return (gettabname().endsWith("@Reticle"));
	}

	/**
	 * Returns delay between messages
	 * 
	 * @return delay
	 */
	public int getMessageDelay() {
		return this.rawbot.messagedelay;
	}

	/**
	 * Returns true if online mode is enabled
	 * 
	 * @return Returns True if online mode is active, False if otherwise
	 */
	public boolean isOnlineMode() {
		return this.onlinemode;
	}

	/**
	 * Refresh username in structures Do not use unless you know what exactly
	 * this does
	 */
	public void refreshOnlineMode() {
		this.onlinemode = this.rawbot.mojangusername;
	}

	/**
	 * Requests focus (Make bot's tab active)
	 */
	public void requestFocus() {
		storage.setselectedtable(gettabname());
	}

	/**
	 * Returns access token used for authentication
	 * 
	 * @return Returns True if successful, False if otherwise
	 */
	protected boolean hasAccessToken() {
		return this.rawbot.maccesstoken != null;
	}

	/**
	 * Returns true if mojang username is available
	 * 
	 * @return Returns True if successful, False if otherwise
	 */
	protected boolean hasMUsername() {
		return this.rawbot.mcurrentusername != null;
	}

	/**
	 * @return Returns Mojang username
	 */
	public String getMUsername() {
		return this.rawbot.mcurrentusername;
	}

	protected boolean hasMPassword() {
		return this.rawbot.mpassword != null;
	}

	protected String getMUsernameID() {
		return getMUsernameID(username);
	}

	protected String getMUsernameID(String username) {
		return this.rawbot.getMojangID(username);
	}

	protected String getMPassword() {
		return this.rawbot.mpassword;
	}

	protected boolean hasPlayerToken() {
		return this.rawbot.mplayertoken != null;
	}

	protected String getPlayerToken() {
		return this.rawbot.mplayertoken;
	}

	public String getAccessToken() {
		return this.rawbot.maccesstoken;
	}

	protected String getSelectedUsername() {
		return this.rawbot.mcurrentusername;
	}

	protected void setMessageCount(int c, boolean valid) {
		if (valid) {
			this.messagecount.setText(storage.parsecolorashtml("§2" + c));
		} else {
			this.messagecount.setText(storage.parsecolorashtml("§4" + c));
		}
	}

	/**
	 * Creates new bot Do not use this constructor
	 * 
	 * @param BotSettings
	 */
	public mcbot(botsettings BotSettings) {
		initbot(BotSettings, false, true, false, true, Color.BLACK, Color.WHITE);
	}

	/**
	 * Creates new bot Do not use this constructor
	 * 
	 * @param bot
	 * @param main
	 * @param tablist
	 * @param allowreport
	 * @param allowconnects
	 * @param backgroundcolor
	 * @param foregroundcolor
	 */
	public mcbot(botsettings bot, boolean main, boolean tablist, boolean allowreport, boolean allowconnects, Color backgroundcolor, Color foregroundcolor) {
		initbot(bot, main, tablist, allowreport, allowconnects, backgroundcolor, foregroundcolor);
	}

	public void killsupportconnector() {
		supportconnector = null;
	}

	protected void setHealth(float health) {
		this.tableinfo.setValueAt(health + "", 0, 1);
	}

	/**
	 * Verify online settings
	 * 
	 * @return Returns True if successful, False if otherwise
	 */
	public boolean verifyonlinesettings() {
		if (this.isOnlineMode()) {
			if (hasAccessToken() && hasPlayerToken()) {
				this.connector.sendMessage("§bRefreshing Mojang session");
				String access = getAccessToken();
				String player = getPlayerToken();
				Authenticator auth = Authenticator.fromAccessToken(access, player);
				auth.setBot(this.rawbot);
				if (auth.refresh()) {
					return true;
				} else {
					this.connector.sendMessage("§bSession lost");
				}
			}
			if (hasMUsername() && hasMPassword()) {
				this.connector.sendMessage("§bLogging to Mojang");
				String username = getMUsername();
				String password = getMPassword();
				Authenticator auth = Authenticator.fromUsernameAndPassword(username, password);
				auth.setBot(this.rawbot);
				return auth.tryLogin();
			}
		}
		return true;
	}

	private void initbot(botsettings bot, boolean main, boolean tablist, boolean yallowreport, boolean yallowconnects, Color ybackgroundcolor, Color yforegroundcolor) {
		this.backgroundcolor = ybackgroundcolor;
		this.foregroundcolor = yforegroundcolor;
		this.allowconnects = yallowconnects;
		this.allowreport = yallowreport;
		this.ismain = main;
		this.tablistdisplayed = tablist;
		bot.isMain = main;
		this.rawbot = bot;
		this.tablistsize[0] = 1;
		this.tablistsize[1] = 20;
		updateChatLogger();
		if (ismain) {
			storage.getInstance().settin.specialbots.add(this);
		}
		initwin();
	}

	protected boolean isChatLoggerEnabled() {
		if (this.ismain) {
			return storage.getSpecialLoggerEnabled();
		} else {
			return this.rawbot.chatlog;
		}
	}

	protected boolean isChatFilterEnabled() {
		return this.rawbot.maxlines > 0;
	}

	protected int getChatFilterLength() {
		return this.rawbot.maxlines;
	}

	protected void updateChatFilter() {
		if (this.isChatFilterEnabled()) {
			AbstractDocument doc = (AbstractDocument) chatlog.getStyledDocument();
			doc.setDocumentFilter(new MaxLenFilter(chatlog, getChatFilterLength()));
		} else {
			AbstractDocument doc = (AbstractDocument) chatlog.getStyledDocument();
			doc.setDocumentFilter(new DocumentFilter());
		}
	}

	/**
	 * Updates state of logger. Invoked when state is changed
	 */
	public void updateChatLogger() {
		if (ChatLogger != null) {
			try {
				ChatLogger.Close();
			} catch (IOException e) {
			}
		}
		if (this.isSpecialTab()) {
			if (!storage.getSpecialLoggerEnabled()) {
				return;
			}
		}
		if (this.isChatLoggerEnabled()) {
			try {
				this.ChatLogger = new ChatLogger(this.gettabname());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	private void initwin() {
		this.serverip = this.rawbot.serverip;
		this.serverport = this.rawbot.serverport;
		if (this.rawbot.mojangusername) {
			this.username = this.rawbot.mcurrentusername;
		} else {
			this.username = this.rawbot.nick;
		}
		botfactory.makenewtab(this);
		if (ismain) {
			seticon(ICONSTATE.MAIN);
		} else {
			seticon(ICONSTATE.DISCONNECTED);
		}
	}

	/**
	 * Returns current protocol version
	 * 
	 * @return Returns current protocol version
	 */
	public int getprotocolversion() {
		return this.rawbot.protocolversion;
	}

	private int gettabid() {
		JTabbedPane cpanel = storage.gettabbedpane();
		int len = cpanel.getComponentCount();
		int i;
		for (i = 0; i < len; i++) {
			if (cpanel.getTitleAt(i).equals(this.gettabname())) {
				return i;
			}
		}
		return i;
	}

	/**
	 * Change bot icon
	 * 
	 * @param Icon
	 */
	public void seticon(final ICONSTATE Icon) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				storage.gettabbedpane().setIconAt(gettabid(), Icon.icon);
			}
		});
	}

	protected void setconfig(JTextPane chatlog, JTable tablist, JPanel panel, JCheckBox autoscroll, JLabel messagecount, JTable tableinfo, JTextField txtCommands) {
		this.chatlog = chatlog;
		this.tabler = tablist;
		this.autoscroll = autoscroll;
		this.messagecount = messagecount;
		this.tableinfo = tableinfo;
		this.exists = true;
		this.textcommands = txtCommands;
	}

	/**
	 * Returns bot's tab name
	 * 
	 * @return Returns Tab name as String
	 */
	public String gettabname() {
		return this.rawbot.getTabName();
	}

	private Style getstyle(String combo) {
		combo = combo.toLowerCase();
		if (styles.containsKey(combo)) {
			return styles.get(combo);
		} else {
			Style style = this.chatlog.addStyle("@" + combo, null);
			if (combo.contains("l")) {
				StyleConstants.setBold(style, true);
			}
			if (combo.contains("m")) {
				StyleConstants.setStrikeThrough(style, true);
			}
			if (combo.contains("n")) {
				StyleConstants.setUnderline(style, true);
			}
			if (combo.contains("o")) {
				StyleConstants.setItalic(style, true);
			}
			if (combo.contains("0")) {
				StyleConstants.setForeground(style, new Color(0, 0, 0));
			}
			if (combo.contains("1")) {
				StyleConstants.setForeground(style, new Color(0, 0, 170));
			} else if (combo.contains("2")) {
				StyleConstants.setForeground(style, new Color(0, 170, 0));
			} else if (combo.contains("3")) {
				StyleConstants.setForeground(style, new Color(0, 170, 170));
			} else if (combo.contains("4")) {
				StyleConstants.setForeground(style, new Color(170, 0, 0));
			} else if (combo.contains("5")) {
				StyleConstants.setForeground(style, new Color(170, 0, 170));
			} else if (combo.contains("6")) {
				StyleConstants.setForeground(style, new Color(255, 170, 0));
			} else if (combo.contains("7")) {
				StyleConstants.setForeground(style, new Color(170, 170, 170));
			} else if (combo.contains("8")) {
				StyleConstants.setForeground(style, new Color(85, 85, 85));
			} else if (combo.contains("9")) {
				StyleConstants.setForeground(style, new Color(85, 85, 255));
			} else if (combo.contains("a")) {
				StyleConstants.setForeground(style, new Color(85, 255, 85));
			} else if (combo.contains("b")) {
				StyleConstants.setForeground(style, new Color(85, 255, 255));
			} else if (combo.contains("c")) {
				StyleConstants.setForeground(style, new Color(255, 85, 85));
			} else if (combo.contains("d")) {
				StyleConstants.setForeground(style, new Color(255, 85, 255));
			} else if (combo.contains("e")) {
				StyleConstants.setForeground(style, new Color(255, 255, 85));
			} else if (combo.contains("f")) {
				StyleConstants.setForeground(style, new Color(255, 255, 255));
			}
			styles.put(combo, style);
			chatlog.addStyle(combo, style);
			return style;
		}
	}

	/**
	 * @return  Returns seconds between sending afk commands
	 */
	public int getantiafkperiod() {
		return this.rawbot.afkperiod;
	}

	/**
	 * Returns array of ignored messages
	 * 
	 * @return Returns array of ignored messages
	 */
	public String[] getignoredmessages() {
		return this.rawbot.ignored;
	}

	/**
	 * @return Returns array of commands to be send after login
	 */
	public String[] getlogincommands() {
		return this.rawbot.autologincmd;
	}

	/**
	 * Returns array of commands to be send before disconnect
	 * 
	 * @return Returns array of commands to be send before disconnect
	 */
	public String[] getlogoutcommands() {
		return this.rawbot.autologoutcmd;
	}

	/**
	 * @return Returns array of commands to be send to prevent afk state
	 */
	public String[] getafkcommands() {
		return this.rawbot.autoantiafkcmd;
	}

	/**
	 * Sends login commands to server
	 * 
	 * @return Returns True if successful, False if otherwise
	 */
	public boolean sendlogincommands() {
		return this.rawbot.autologin;
	}

	/**
	 * Sends logout commands to server
	 * 
	 * @return Returns True if successful, False if otherwise
	 */
	public boolean sendlogoutcommands() {
		return this.rawbot.autologout;
	}

	/**
	 * Sends afk commands to server
	 * 
	 * @return Returns True if successful, False if otherwise
	 */
	public boolean sendafkcommands() {
		return this.rawbot.autoantiafk;
	}

	/**
	 * Returns true if autoreconnect is enabled
	 * 
	 * @return Returns true if autoreconnect is enabled
	 */
	public boolean getautoreconnect() {
		return this.rawbot.autoreconnect;
	}

	/**
	 * @return Returns seconds between reconnecting as defined in settings
	 */
	public int getautoreconnectdelay() {
		return this.rawbot.autoreconnectdelay;
	}

	private boolean goscroll() {
		return this.autoscroll.isSelected();
	}

	/**
	 * @return Returns true if bot is connected and ready
	 */
	public boolean isConnected() {
		if (this.isSpecialTab()) {
			if (this.isSupportTab()) {
				if (this.supportconnector != null) {
					return this.supportconnector.isConnected();
				} else {
					return false;
				}
			} else if (this.isMainTab()) {
				return true;
			}
		} else {
			if (this.connector == null) {
				// Initial state
				return false;
			} else {
				return this.connector.isConnected();
			}
		}
		return false;
	}

	// TODO: Manage main commands
	private void manageMainCommand(String command) {
		ConsoleCommandEvent event = new ConsoleCommandEvent(this,command);
		String cmd = event.getCommandName().toLowerCase();
		String[] params=event.getParams();
		switch (cmd) {
			default:
				storage.pluginManager.invokeEvent(event, true);
				if(!event.isCancelled()) {
					storage.conlog("Command not recognized. Use §o§6help§r for list of all commands");
				}
				
			break;

			case "help":
				storage.conlog("§nReticle help§r\n" + storage.helper.getHelpString());
			break;

			case "pl":
			case "plugins":
				StringBuilder sb = new StringBuilder();
				Collection<PluginInfo> infos = storage.pluginManager.getPluginInfos();
				for (PluginInfo pl : infos) {
					sb.append(", " + pl.Name);
				}
				if (infos.size() == 0) {
					sb.append("  ");
				}
				storage.conlog("Loaded plugins (" + infos.size() + "): " + sb.toString().substring(2));
			break;

			case "plugin":
				if (params.length == 3) {
					if (params[1].equalsIgnoreCase("unload")) {
						String plname = params[2];
						Plugin pl = storage.pluginManager.getPluginByName(plname);
						if (pl == null) {
							storage.conlog("This plugin is not loaded");
						} else {
							storage.pluginManager.unloadPlugin(pl);
							storage.conlog("Plugin unloaded successfully");
						}
					} else if (params[1].equalsIgnoreCase("load")) {
						String plname = params[2];
						if (storage.pluginManager.getPluginByFileName(plname) == null) {
							if (storage.pluginManager.loadPlugin(plname)) {
								storage.conlog("Plugin loaded successfully");
							} else {
								storage.conlog("Failed to load plugin");
							}
						} else {
							storage.conlog("This plugin is already loaded");
						}
					} else if (params[1].equalsIgnoreCase("info")) {
						String plname = params[2];
						Plugin pl = storage.pluginManager.getPluginByName(plname);
						if (pl == null) {
							storage.conlog("This plugin is not loaded");
						} else {
							PluginInfo plinfo = storage.pluginManager.getPluginInfo(pl);
							storage.conlog("§oPlugin info\nName: §6" + plinfo.Name + "\n§fAuthor: §6" + plinfo.Author + "§f\nVersion: §6" + plinfo.Version);
						}
					} else if (params[1].equalsIgnoreCase("enabledall")) {
						String plname = params[2];
						Plugin pl = storage.pluginManager.getPluginByName(plname);
						if (pl == null) {
							storage.conlog("This plugin is not loaded");
						} else {
							PluginInfo plinfo = storage.pluginManager.getPluginInfo(pl);
							HashMap<String, mcbot> bots = storage.getInstance().settin.bots;
							for (mcbot bott : bots.values()) {
								bott.enablePluginHere(plinfo);
							}
							storage.conlog("Plugin §6" + plinfo.Name + "§r§f has been enabled on all servers");
						}
					} else if (params[1].equalsIgnoreCase("disableall")) {
						String plname = params[2];
						Plugin pl = storage.pluginManager.getPluginByName(plname);
						if (pl == null) {
							storage.conlog("This plugin is not loaded");
						} else {
							PluginInfo plinfo = storage.pluginManager.getPluginInfo(pl);
							HashMap<String, mcbot> bots = storage.getInstance().settin.bots;
							for (mcbot bott : bots.values()) {
								bott.disablePluginHere(plinfo);
							}
							storage.conlog("Plugin §6" + plinfo.Name + "§r§f has been disabled on all servers");
						}
					}
				} else {
					storage.conlog("Usage: /plugin <load|unload> <pluginname>\nTo list all loaded plugins use /plugins");
				}
			break;
		}
	}

	private void disablePluginHere(PluginInfo pl) {
		if (this.rawbot.plugins.contains(pl.Name)) {
			this.rawbot.plugins.remove(pl.Name);
		}
	}

	private void enablePluginHere(PluginInfo pl) {
		if (!this.rawbot.plugins.contains(pl.Name)) {
			this.rawbot.plugins.add(pl.Name);
		}
	}

	public boolean sendtoserver(String Message, boolean automessage) {
		uplist.addMessage(Message);
		if (this.isSpecialTab()) {
			if (this.isMainTab()) {
				manageMainCommand(Message);
				return true;
			} else if (this.isSupportTab()) {
				if (supportconnector != null) {
					if (supportconnector.isConnected()) {
						supportconnector.SendMessage(Message);
						return true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			}
		} else {
			if (this.isConnected()) {
				return this.connector.sendToServer(Message, automessage);
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * Sends text to server (note that to send command, you must send text
	 * prefixed with '/')
	 * 
	 * @param Message Message to be send to server
	 * @return Returns True if successful, False if otherwise
	 */
	public boolean sendtoserver(String Message) {
		return sendtoserver(Message, true);
	}

	protected void specialconnect() {
		if (this.supportconnector == null) {
			this.supportconnector = new supportconnector(this);
			storage.changemenuitems();
		} else if (!this.supportconnector.isConnected()) {
			this.supportconnector.Connect();
			storage.changemenuitems();
		}
	}

	/**
	 * Connects the bot
	 */
	public void connect() {
		connect(false);
	}

	public void connect(boolean autoreconnect) {
		if (this.gettabname().endsWith(("@Reticle"))) {
			specialconnect();
			return;
		}
		if (this.rawbot.serverip != null && this.connector == null) {
			try {
				if (!this.isConnected()) {
					this.serverip = this.rawbot.serverip;
					this.serverport = this.rawbot.serverport;
					this.connector = new connector(this);
					connector.reconnect = autoreconnect;
					connector.start();
				} else {
					this.logmsg("§4§lAlready connected");
				}
			} catch (UnknownHostException e) {
				this.logmsg("§4 Invalid IP or hostname");
			} catch (IOException e) {
				if (!storage.reportthis(e)) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Redefine settings Do not use this
	 * 
	 * @param ip
	 * @param port
	 * @param name
	 * @param nick
	 */
	public void setipandport(String ip, int port, String name, String nick) {
		this.rawbot.serverip = ip;
		this.rawbot.serverport = port;
		this.rawbot.servername = name;
		this.rawbot.nick = nick;
		this.serverip = ip;
		this.serverport = port;
		this.username = nick;
	}

	public enum ICONSTATE {
		CONNECTED(storage.icon_on), DISCONNECTED(storage.icon_off), CONNECTING(storage.icon_con), MAIN(storage.icon_dis);

		public Icon icon;

		ICONSTATE(Icon ico) {
			this.icon = ico;
		}
	}

	/**
	 * Returns text from chat box
	 * 
	 * @param len Length of message to return
	 * @return Returns message from chat box
	 */
	public String getmsg(int len) {
		int lenn = chatlog.getText().length();
		if (lenn < len) {
			return chatlog.getText();
		}
		try {
			return this.chatlog.getText(lenn - len, len);
		} catch (BadLocationException e) {
		}
		return "";
	}

	/**
	 * Logs message to chat box
	 * 
	 * @param message
	 */
	public synchronized void logmsg(String message) {
		if (message.endsWith("§")) {
			message = message + " ";
		}
		if (message.length() > 0) {
			// Extra space because of the split method and following loop
			message = " [" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + message;
			chatLog(message);
			String bold = "";
			String underline = "";
			String strike = "";
			String italic = "";
			String color = "f";
			StyledDocument doc = this.chatlog.getStyledDocument();
			String[] msgs = (message + "\n").split("§");
			for (String msg : msgs) {
				String tmg = msg.substring(0, 1).toLowerCase();
				if (tmg.equals("l")) {
					bold = "l";
				} else if (tmg.equals("m")) {
					strike = "m";
				} else if (tmg.equals("n")) {
					underline = "n";
				} else if (tmg.equals("o")) {
					italic = "o";
				} else if (tmg.equals("r")) {
					bold = "";
					strike = "";
					underline = "";
					italic = "";
					color = "f";
				} else {
					color = tmg;
				}
				String combo = bold + strike + underline + italic + color;
				String restmsg = msg.substring(1);
				Style style = getstyle(combo);
				try {
					doc.insertString(doc.getLength(), restmsg, style);
				} catch (BadLocationException e) {
					e.printStackTrace();
				} catch (Error e) {
					return;
				}
			}
			if (this.goscroll()) {
				this.chatlog.setCaretPosition(this.chatlog.getDocument().getLength());
			}
			if (this.rawbot.activenotify) {
				storage.setselectedtable(this.gettabname());
			}
		}
	}

	/**
	 * Disconnect main bot (Like support connector) Do not use this
	 */
	public void specialdisconnect() {
		this.supportconnector.Disconnect();
		storage.changemenuitems();
	}

	/**
	 * Disconnects bot
	 */
	public void disconnect() {
		if (this.connector != null) {
			this.connector.reconnect = false;
			this.connector.interrupt();
		}
		if (this.isConnected()) {
			// To prevent automatic restart
			this.connector.reconnect = false;
			// Go for it
			this.connector.stopMe();
			this.connector = null;
			seticon(ICONSTATE.DISCONNECTED);
			storage.changemenuitems();
		}
	}

	/**
	 * Set Player list dimension Do not use this
	 * 
	 * @param cols
	 * @param rows
	 */
	public final void setTabSize(int cols, int rows) {
		this.tablistsize[0] = rows; // y
		this.tablistsize[1] = cols; // x
		int max = rows * cols;
		String[][] redim = new String[rows][cols];
		for (int i = 0; i < max; i++) {
			int locx = i % cols;
			int locy = i / cols;
			redim[locy][locx] = new String(" ");
		}
		final DefaultTableModel model = new DefaultTableModel(redim, cols);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				tabler.setModel(model);
			}
		});
	}

	/**
	 * Refresh displayed Player list Note that Player list is already handled
	 * 
	 * @param TabList
	 * @param TabListNicks
	 * @param PlayerTeams
	 * @param Teams
	 */
	public final void refreshtablist(List<String> TabList, HashMap<String, String> TabListNicks, HashMap<String, String> PlayerTeams, HashMap<String, team_struct> Teams) {
		// Just replace the text on slots
		int x = this.tablistsize[0];
		int y = this.tablistsize[1];

		int realy = tabler.getRowCount();
		int realx = tabler.getColumnCount();

		if (x != realx || y != realy) {
			setTabSize(y, x);
		}

		int max = x * y;
		if (max != 0) {
			int imax = TabList.size();
			for (int i = 0; i < max; i++) {
				String name;
				if (i < imax) {
					name = TabList.get(i);
				} else {
					name = "";
				}
				final int locx = i % x;
				final int locy = i / x;
				// Now we should parse player name by his team
				if (TabListNicks.containsKey(name)) {
					name = TabListNicks.get(name);
				}
				String realnamer = name;
				if (PlayerTeams.containsKey(name)) {
					// He is in a team
					String teamname = PlayerTeams.get(name);
					if (Teams.containsKey(teamname)) {
						// His team exists
						realnamer = Teams.get(teamname).getFormatedPlayer(realnamer);
					}
				}
				final String realname = storage.parsecolorashtml(realnamer);
				String oldval = null;
				try {
					oldval = (String) tabler.getValueAt(locy, locx);
				} catch (ArrayIndexOutOfBoundsException e) {
				}
				if (oldval == null) {
					// Initial set
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							tabler.setValueAt(realname, locy, locx);
						}
					});
				} else if (oldval != realname) {
					// Value has changed
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							tabler.setValueAt(realname, locy, locx);
						}
					});
				}
			}
		}
	}

	/**
	 * Refresh displayed Player list Note that Player list is already handled
	 * 
	 * @param TabList
	 * @param PlayerTeams
	 * @param Teams
	 */
	public void refreshtablist(List<String> TabList, HashMap<String, String> PlayerTeams, HashMap<String, team_struct> Teams) {
		// Just replace the text on slots
		int x = this.tablistsize[0];
		int y = this.tablistsize[1];

		int realy = tabler.getRowCount();
		int realx = tabler.getColumnCount();

		if (x != realx || y != realy) {
			setTabSize(y, x);
		}

		int max = x * y;
		if (max != 0) {
			int imax = TabList.size();
			for (int i = 0; i < max; i++) {
				String name;
				if (i < imax) {
					name = TabList.get(i);
				} else {
					name = "";
				}
				final int locx = i % x;
				final int locy = i / x;
				// Now we should parse player name by his team
				String realnamer = name;
				if (PlayerTeams.containsKey(name)) {
					// He is in a team
					String teamname = PlayerTeams.get(name);
					if (Teams.containsKey(teamname)) {
						// His team exists
						realnamer = Teams.get(teamname).getFormatedPlayer(realnamer);
					}
				}
				final String realname = storage.parsecolorashtml(realnamer);
				String oldval = null;
				try {
					oldval = (String) tabler.getValueAt(locy, locx);
				} catch (ArrayIndexOutOfBoundsException e) {
				}
				if (oldval == null) {
					// Initial set
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							tabler.setValueAt(realname, locy, locx);
						}
					});
				} else if (oldval != realname) {
					// Value has changed
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							tabler.setValueAt(realname, locy, locx);
						}
					});
				}
			}
		}
	}

	/**
	 * To update settings This method is not safe to use
	 * 
	 * @param BotSettings
	 */
	public void updaterawbot(botsettings BotSettings) {
		// To make it reconnect if this change is necessary
		if (BotSettings != null) {
			this.rawbot = BotSettings;
			if (this.connector != null) {
				this.connector.reconnect = BotSettings.autoreconnect;
			}
			this.rawbot.maccesstoken = BotSettings.maccesstoken;
			this.rawbot.mplayertoken = BotSettings.mplayertoken;
			this.rawbot.mcurrentusername = BotSettings.mcurrentusername;
			this.rawbot.mojangloginusernameid = BotSettings.mojangloginusernameid;
			this.rawbot.font = BotSettings.font;
			this.rawbot.plugins = BotSettings.plugins;
		}
		updateChatLogger();
		updateChatFilter();
		updateChatFont();
		updatePlugins();
	}

	private void updatePlugins() {

	}

	private void updateChatFont() {
		this.chatlog.setFont(this.rawbot.font);
		this.tabler.setFont(this.rawbot.font);
	}

	/**
	 * Reset Player list to default
	 */
	public void resetPlayerList() {
		this.setTabSize(0, 0);
		refreshtablist(new ArrayList<String>(), new HashMap<String, String>(), new HashMap<String, team_struct>());
	}

	/**
	 * Updates health on info table
	 * 
	 * @param Health
	 * @param Food
	 * @param Saturation
	 */
	public void updateHealth(float Health, int Food, float Saturation) {
		String healt = String.format("%.2f", Health);
		String sat = String.format("%.2f", Saturation);
		this.tableinfo.setValueAt(healt, 0, 1);
		this.tableinfo.setValueAt("" + Food, 1, 1);
		this.tableinfo.setValueAt(sat, 2, 1);
	}

	/**
	 * Updates coordinates on info table
	 * 
	 * @param X
	 * @param Y
	 * @param Z
	 */
	public void updateposition(int X, int Y, int Z) {
		this.tableinfo.setValueAt(X + "", 0, 3);
		this.tableinfo.setValueAt(Y + "", 1, 3);
		this.tableinfo.setValueAt(Z + "", 2, 3);

	}

	/**
	 * Display info table
	 */
	public void showInfoTable() {
		this.tableinfo.setVisible(true);
	}

	/**
	 * Hide info table
	 */
	public void hideInfoTable() {
		this.tableinfo.setVisible(false);
	}

	/**
	 * Reset info table to default values
	 */
	public void resetInfoTable() {
		this.tableinfo.setValueAt("", 0, 1);
		this.tableinfo.setValueAt("", 1, 1);
		this.tableinfo.setValueAt("", 2, 1);
		this.tableinfo.setValueAt("", 0, 3);
		this.tableinfo.setValueAt("", 1, 3);
		this.tableinfo.setValueAt("", 2, 3);
	}

	/**
	 * Log message to file
	 * 
	 * @param Message
	 */
	public void chatLog(String Message) {
		if (this.isChatLoggerEnabled()) {
			try {
				this.ChatLogger.Log(storage.stripcolors(Message.substring(1)));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
			}
		}
	}

	protected void setTextAtMessageBox(String msg) {
		this.textcommands.setText(msg);
	}

	protected void arrowuppressed() {
		setTextAtMessageBox(uplist.getPrevious());
	}

	protected void arrowdownpressed() {
		setTextAtMessageBox(uplist.getNext());
	}

	protected void tabpressed(JTextField area, String text) {
		if (this.connector != null) {
			this.connector.tabpressed(area, text);
		}
	}

	public Font getFont() {
		return rawbot.font;
	}

	public List<String> getAllowedPlugins() {
		return rawbot.plugins;
	}
}
