package org.spigot.reticle;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.sql.rowset.serial.SerialException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import org.spigot.reticle.botfactory.mcbot;
import org.spigot.reticle.botfactory.mcbot.ICONSTATE;
import org.spigot.reticle.botfactory.selector;
import org.spigot.reticle.resources.resources;
import org.spigot.reticle.settings.aboutwin;
import org.spigot.reticle.settings.botsettings;
import org.spigot.reticle.settings.optionswin;
import org.spigot.reticle.settings.set_obj_struct;
import org.spigot.reticle.settings.settings;
import org.spigot.reticle.settings.struct_settings;
import org.spigot.reticle.sockets.PlayerStream;
import org.spigot.reticle.sockets.Reporter;
import org.spigot.reticle.sockets.ChatThread;

public class storage {
	public static final String version = "1.04.8 beta";

	/**
	 * Selector object. Used for internap purposes Not safe to use
	 */
	public static final selector sel = new selector();

	/**
	 * Default text to be displayed for authentication
	 */
	public static final String default_online_nick = "Authenticate first";

	/**
	 * Many uses
	 */
	public static final String LineSeparator = System.lineSeparator();

	/**
	 * Thread for dealing with chat messages
	 */
	public static final ChatThread ChatThread = new ChatThread();

	/**
	 * Main instance
	 */
	private static storage instance = null;

	/**
	 * Link to project website
	 */
	public final static String homepage = "http://reticle.mc-atlantida.eu/";

	/**
	 * Link to news service website
	 */
	public final static String news = "http://reticle.mc-atlantida.eu/news.php";

	/**
	 * Mojang authenticazion server URL
	 */
	public final static String AuthURL = "https://authserver.mojang.com/";

	/**
	 * Mojang authenticazion server URL
	 */
	public final static String joinURL = "https://sessionserver.mojang.com/session/minecraft/join";

	public final static PlayerStream playerStream = new PlayerStream();

	/**
	 * Mojang authenticazion server URL
	 */
	public final static String joinURLalter = "http://session.minecraft.net/game/joinserver.jsp";

	/**
	 * Help commands keeper
	 */
	public final static helpCommandsKeeper helper = new helpCommandsKeeper();

	/**
	 * Structure of settings
	 */
	public struct_settings settin;

	/**
	 * Settings window handler
	 */
	public settings settings;

	/**
	 * Support tab handler
	 */
	public mcbot support;

	/**
	 * Main tab handler
	 */
	protected mcbot mainer;

	/**
	 * Main tabbed pane (indexing supported)
	 */
	public JTabbedPane tabbedPane;

	/**
	 * God knows what this is good for
	 */
	protected JMenuItem menu_con;
	protected JMenuItem menu_dis;
	protected JMenuItem menu_set;
	protected JMenuItem menu_rec;
	protected JMenuItem menu_info;

	/**
	 * About window handler
	 */
	public aboutwin aboutwin;

	/**
	 * Updating thread
	 */
	public updater updater;

	/**
	 * Settings frame structure
	 */
	public Frame winobj;

	/**
	 * Main settings structure (This is where saving and loading happens)
	 */
	public set_obj_struct setobj = new set_obj_struct();

	/**
	 * Global options window
	 */
	private optionswin optwin;

	/**
	 * Main window object
	 */
	public JFrame mainframe;

	private final static Class<?> thisClass = resources.class;

	private final static File CurrentDirectory = new File(System.getProperty("user.dir"));

	/**
	 * Indicates current working directory
	 */
	public final static String CurrentDir = curentdir(CurrentDirectory.getAbsolutePath());

	public final static String DirectoryDelim = System.lineSeparator();

	private static String curentdir(String s) {
		if (!s.endsWith("/") && !s.endsWith("\\")) {
			return s + "/";
		} else {
			return s;
		}
	}

	public static final Icon icon_off = new ImageIcon(thisClass.getResource("icon_off.png"));
	public static final Icon icon_on = new ImageIcon(thisClass.getResource("icon_on.png"));
	public static final Icon icon_dis = new ImageIcon(thisClass.getResource("icon_dis.png"));
	public static final Icon icon_con = new ImageIcon(thisClass.getResource("icon_con.png"));
	public static final ImageIcon icon_loader = new ImageIcon(thisClass.getResource("logo.png"));
	public static final String icon_loader_path = thisClass.getResource("logo.png").getFile();
	public static final ImageIcon winicon = new ImageIcon(thisClass.getResource("mainicon.png"));

	private final static String settingfile = CurrentDir + "settings.ini";
	/**
	 * Support channel
	 */
	public static final String supportserver = "irc.freenode.net";

	/**
	 * Support channel
	 */
	public static final String supportchannel = "#ReticleSupport2";

	/**
	 * Main plugin manager (The only one in fact)
	 */
	public static final PluginManager pluginManager = new PluginManager();

	protected static void loadPlugins() {
		storage.pluginManager.loadAllPlugins();
	}

	/**
	 * Returns bot by given tab name
	 * 
	 * @param tabName
	 *            Tab name
	 * @return Returns bot by given tab name
	 */
	public static mcbot getBotbyTabName(String tabName) {
		if (storage.getInstance().settin.bots.containsKey(tabName)) {
			return storage.getInstance().settin.bots.get(tabName);
		} else {
			return null;
		}
	}

	/**
	 * When invoke, option window is closed
	 */
	public synchronized static void closeoptionswin() {
		if (storage.getInstance().optwin != null) {
			storage.getInstance().optwin.dispose();
			storage.getInstance().optwin = null;
		}
	}

	private static String getconsoletext() {
		mcbot bot = storage.getInstance().mainer;
		return bot.getmsg(5000);
	}

	/**
	 * Sets system clipboard (Ctrl+c/Ctrl+v)
	 * 
	 * @param Text
	 *            - Text to be put into clipboard
	 */
	public static void setClipboard(String Text) {
		StringSelection stringSelection = new StringSelection(Text);
		Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
		clpbrd.setContents(stringSelection, null);
	}

	/**
	 * Invoked when error happens and should be reported automatically Indicates
	 * the error is in main tab
	 */
	public static void sendissue() {
		Reporter rp = new Reporter(Reporter.ACTION.REPORTISSUE);
		rp.issue = storage.getconsoletext();
		rp.start();
	}

	/**
	 * Invokes when error happens and should be reported automatically
	 * 
	 * @param issue
	 *            - Text to be sent
	 */
	public static void sendissue(String issue) {
		Reporter rp = new Reporter(Reporter.ACTION.REPORTISSUE);
		rp.issue = issue;
		rp.start();
	}

	/**
	 * Invoked when checking for updates is being performed
	 */
	protected synchronized static void checkforupdates() {
		if (storage.getInstance().updater == null) {
			storage.getInstance().updater = new updater();
			storage.conlog("Updater service is now running");
			storage.getInstance().updater.start();
		} else {
			storage.conlog("Updater service is already running");
		}
	}

	/**
	 * Invoked when about window should be opened
	 */
	protected synchronized static void openaboutwin() {
		if (storage.getInstance().aboutwin == null) {
			// Does not exist, must be created
			storage.getInstance().aboutwin = new aboutwin();
			storage.getInstance().aboutwin.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			storage.getInstance().aboutwin.setVisible(true);
		} else {
			// Options dialog already exists
			storage.getInstance().aboutwin.requestFocus();
		}
	}

	/**
	 * Invoked when about window should be closed
	 */
	public synchronized static void closeaboutwin() {
		if (storage.getInstance().aboutwin != null) {
			storage.getInstance().aboutwin.dispose();
			storage.getInstance().aboutwin = null;
		}
	}

	/**
	 * Causes Operating system to open webpage
	 * 
	 * @param url
	 *            - URL to be opened
	 */
	protected static void openweb(String url) {
		try {
			Desktop.getDesktop().browse(new URL(url).toURI());
		} catch (Exception e) {
			storage.conlog("Opening URL operation is not supported by this system");
		}
	}

	/**
	 * Returns whether or not automatic updates are enabled
	 * 
	 * @return true if automatical updates are enabled false if otherwise
	 */
	public static boolean getAutoupdate() {
		HashMap<String, String> setting = storage.getInstance().settin.globalsettings;
		if (setting.containsKey("autoupdate")) {
			Boolean bool = Boolean.parseBoolean(setting.get("autoupdate"));
			if (bool != null) {
				return bool;
			}
		}
		return true;
	}

	/**
	 * Returns whether or not automatic reports are enabled
	 * 
	 * @return true if automatical reports are enabled false if otherwise
	 */
	public static boolean getAutodebug() {
		HashMap<String, String> setting = storage.getInstance().settin.globalsettings;
		if (setting.containsKey("autosenddebug")) {
			Boolean bool = Boolean.parseBoolean(setting.get("autosenddebug"));
			if (bool != null) {
				return bool;
			}
		}
		return true;
	}

	/**
	 * @return True if special tabs are logged, false if otherwise
	 */
	public static boolean getSpecialLoggerEnabled() {
		HashMap<String, String> setting = storage.getInstance().settin.globalsettings;
		if (setting.containsKey("speciallogger")) {
			Boolean bool = Boolean.parseBoolean(setting.get("speciallogger"));
			if (bool != null) {
				return bool;
			}
		}
		return false;
	}

	/**
	 * Returns whether or not automatic plugins loading is enabled
	 * 
	 * @return true if automatical plugin loading is enabled false if otherwise
	 */
	public static boolean getAutoplugin() {
		HashMap<String, String> setting = storage.getInstance().settin.globalsettings;
		if (setting.containsKey("loadplugins")) {
			Boolean bool = Boolean.parseBoolean(setting.get("loadplugins"));
			if (bool != null) {
				return bool;
			}
		}
		return false;
	}

	/**
	 * Generates random string
	 * 
	 * @param len
	 *            - Length is string to be generated
	 * @return random string by length
	 */
	public static String randomString(int len) {
		String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random rnd = new Random();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++)
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		return sb.toString();
	}

	public static int getBundlePort() {
		HashMap<String, String> setting = storage.getInstance().settin.globalsettings;
		if (setting.containsKey("bundleport")) {
			String bool = setting.get("bundleport");
			if (bool != null) {
				try {
					int i = Integer.parseInt(bool);
					return i;
				} catch (Exception e) {
					return 25565;
				}
			}
		}
		return 25565;
	}

	/**
	 * Generates or loads nick to be used on support channel
	 * 
	 * @return nick to be used
	 */
	public static String getSupportNick() {
		HashMap<String, String> setting = storage.getInstance().settin.globalsettings;
		if (setting.containsKey("supportnick")) {
			String bool = setting.get("supportnick");
			if (bool != null) {
				return bool;
			}
		}
		return "Unknown_" + storage.randomString(5);
	}

	/**
	 * Checks if support is enabled
	 * 
	 * @return true if support is enabled false if otherwise
	 */
	public static boolean getSupportEnabled() {
		HashMap<String, String> setting = storage.getInstance().settin.globalsettings;
		if (setting.containsKey("support")) {
			Boolean bool = Boolean.parseBoolean(setting.get("support"));
			if (bool != null) {
				return bool;
			}
		}
		return true;
	}

	protected static void displayoptionswin() {
		if (storage.getInstance().optwin == null) {
			// Options dialog does not exist (yet)
			storage.getInstance().optwin = new optionswin();
			storage.getInstance().optwin.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			storage.getInstance().optwin.setVisible(true);
		} else {
			// Options dialog already exists
			storage.getInstance().optwin.requestFocus();
		}
	}

	/**
	 * Invoked when options are changed
	 * 
	 * @param map
	 *            New options
	 */
	public static void setglobalsettings(HashMap<String, String> map) {
		storage.getInstance().settin.globalsettings = map;
		List<mcbot> specialbots = storage.getSpecialBots();
		for (mcbot bot : specialbots) {
			bot.updateChatLogger();
		}
	}

	private static List<mcbot> getSpecialBots() {
		return storage.getInstance().settin.specialbots;
	}

	/**
	 * Returns global options
	 */
	public static HashMap<String, String> getglobalsettings() {
		return storage.getInstance().settin.globalsettings;
	}

	/**
	 * Returns tabbed pane
	 */
	public static JTabbedPane gettabbedpane() {
		return storage.getInstance().tabbedPane;
	}

	/**
	 * Disconnects all bots
	 */
	protected static void killall() {
		HashMap<String, mcbot> bots = storage.getInstance().settin.bots;
		for (String name : bots.keySet()) {
			mcbot bot = bots.get(name);
			bot.disconnect();
		}
	}

	/**
	 * Invoked when bot state is changed Affects bot menu
	 */
	public static void changemenuitems() {
		mcbot bot = storage.getcurrentselectedbot();
		if (bot == null) {
			bot = storage.getspecialbot();
			if (bot != null) {
				if (bot.allowconnects) {
					if (bot.isConnected()) {
						storage.setconnected();
					} else {
						storage.setdisconnected();
					}
				} else {
					storage.setdisabled();
				}
			} else {
				storage.setdisabled();
			}
		} else if (!bot.allowconnects) {
			storage.setdisabled();
		} else if (bot.isConnected()) {
			storage.setconnected();
		} else {
			storage.setdisconnected();
		}
	}

	/**
	 * Activates tab based by it's name
	 * 
	 * @param TabName
	 */
	public static void setselectedtable(String TabName) {
		int i = storage.gettabbyname(TabName);
		storage.getInstance().tabbedPane.setSelectedIndex(i);
	}

	/**
	 * Deprecated. Will be removed soon
	 * 
	 * @param message
	 * @return Returns True if successful, False if otherwise
	 * @deprecated
	 */
	@Deprecated
	public static boolean sendmessagetoactivebot(String message) {
		mcbot bot = storage.getcurrentselectedbot();
		if (bot != null) {
			return bot.sendtoserver(message);
		} else {
			bot = storage.getspecialbot();
			if (bot != null) {
				if (bot.isConnected()) {
					return bot.sendtoserver(message);
				}
				return false;
			} else {
				return false;
			}
		}
	}

	private static void setconnected() {
		storage.getInstance().menu_con.setEnabled(false);
		storage.getInstance().menu_dis.setEnabled(true);
		storage.getInstance().menu_set.setEnabled(true);
		storage.getInstance().menu_rec.setEnabled(true);
		storage.getInstance().menu_info.setEnabled(true);
	}

	private static void setdisconnected() {
		storage.getInstance().menu_con.setEnabled(true);
		storage.getInstance().menu_dis.setEnabled(false);
		storage.getInstance().menu_set.setEnabled(true);
		storage.getInstance().menu_rec.setEnabled(false);
		storage.getInstance().menu_info.setEnabled(true);
	}

	private static void setdisabled() {
		storage.getInstance().menu_con.setEnabled(false);
		storage.getInstance().menu_dis.setEnabled(false);
		storage.getInstance().menu_set.setEnabled(false);
		storage.getInstance().menu_rec.setEnabled(false);
		storage.getInstance().menu_info.setEnabled(false);
	}

	/**
	 * Returns setting object
	 * 
	 * @return Returns setting object
	 */
	public static set_obj_struct getsettingsobj() {
		return storage.getInstance().setobj;
	}

	protected static boolean settingwindowopened() {
		return (storage.getInstance().winobj != null);
	}

	protected synchronized static void opensettingswindowforcurrentbot() {
		botsettings set = getcurrenttabsettings();
		opensettingswindow(set);
	}

	/**
	 * Opens bot settings window
	 * 
	 * @param BotSettings
	 *            Botsettings to edit
	 */
	public synchronized static void opensettingswindow(botsettings BotSettings) {
		if (settingwindowopened()) {
			storage.getInstance().winobj.requestFocus();
		} else {
			if (BotSettings != null) {
				storage.getInstance().winobj = new settings(BotSettings);
			}
		}
	}

	private static int getselectedtabindex() {
		int i = storage.getInstance().tabbedPane.getSelectedIndex();
		if (i == -1) {
			i = 0;
		}
		return i;
	}

	protected static String getselectedtabtitle() {
		return storage.getInstance().tabbedPane.getTitleAt(getselectedtabindex());
	}

	protected static mcbot getcurrentselectedbot() {
		return storage.getInstance().settin.bots.get(storage.getselectedtabtitle());
	}

	/**
	 * Displays alert window
	 * 
	 * @param Title
	 * @param Message
	 */
	public static void alert(String Title, String Message) {
		Component comp = storage.gettabbedpane();
		JOptionPane.showMessageDialog(comp, Message, Title, JOptionPane.ERROR_MESSAGE);
	}

	public static mcbot addbot() {
		botsettings bot = new botsettings("Untitled");
		if (!bot.isExclusive()) {
			storage.alert("Error", "Cannot add new bot. There might be one not configured yet.");
			return null;
		} else {
			mcbot mbot = new mcbot(bot);
			mbot.ismain = false;
			storage.getInstance().settin.settings.put(bot.getTabName(), bot);
			storage.getInstance().settin.bots.put(mbot.gettabname(), mbot);
			mbot.seticon(ICONSTATE.DISCONNECTED);
			savesettings();
			return mbot;
		}
	}

	/**
	 * Returns String stripped of formating
	 * 
	 * @param String
	 *            String to be stripped
	 * @return Returns Stripped string
	 */
	public static String stripcolors(String String) {
		return String.replaceAll("(\\§.)", "");
	}

	/**
	 * Sends message to Main tab
	 * 
	 * @param Message
	 */
	public static void conlog(String Message) {
		if (Message.length() > 0) {
			mcbot bot = storage.getInstance().mainer;
			bot.logmsg(Message);
		}
	}

	protected static void firsttabload() {
		init();
		HashMap<String, botsettings> tabs = storage.getInstance().settin.settings;
		HashMap<String, mcbot> bots = storage.getInstance().settin.bots;
		if (tabs != null) {
			for (String key : tabs.keySet()) {
				botsettings set = tabs.get(key);
				mcbot bot = new mcbot(set);
				bot.ismain = false;
				bots.put(key, bot);
			}
			storage.gettabbedpane().setSelectedIndex(0);
			for (String key : tabs.keySet()) {
				botsettings set = tabs.get(key);
				mcbot bot = bots.get(key);
				if (set.autoconnect) {
					bot.connect();
				}
			}
		}
	}

	private static void init() {
		helper.addEntry("help", "Contains all registered commands", new String[] {});
		helper.addEntry("pl", "Displays all currently loaded plugins", new String[] {});
		helper.addEntry("plugins", "same as pl", new String[] {});
		helper.addEntry("plugin", "same as pl", new String[] { "<load|unload|info|enabledall|disableall>", "<plugin filename|plugin name>" });
	}

	@Deprecated
	private static botsettings getcurrenttabsettings() {
		return storage.getInstance().settin.settings.get(getselectedtabtitle());
	}

	/**
	 * Invoked whenever the settings are changed and need to be saved
	 */
	public static final synchronized void savesettings() {
		try {
			FileOutputStream f = new FileOutputStream(settingfile);
			f.write(storage.getInstance().settin.saveToString().getBytes());
			f.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Invoked whenever the settings are loaded
	 * 
	 * @throws SerialException
	 */
	public static final synchronized void loadsettings() throws SerialException {
		try {
			String setraw = new String(Files.readAllBytes(Paths.get(settingfile)));
			// If this is initial settings load
			if (storage.getInstance().settin == null) {
				storage.getInstance().settin = new struct_settings();
			}
			storage.getInstance().settin.loadFromString(setraw);
		} catch (NoSuchFileException e) {
			storage.getInstance().settin = new struct_settings();
			storage.getInstance().settin.settings = new HashMap<String, botsettings>();
			savesettings();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Returns static access to Storage object Everything related to bots is
	 * find here
	 * 
	 * @return Returns storage instance
	 */
	public static storage getInstance() {
		if (instance == null) {
			instance = new storage();
		}
		return instance;
	}

	/**
	 * Called whenever bot settings are changed Affects tab and settings itself
	 * 
	 * @param BotSettings
	 * @param OldTabName
	 * @param TabIndex
	 */
	public static void resetset(botsettings BotSettings, String OldTabName, int TabIndex) {
		struct_settings setting = storage.getInstance().settin;
		String nact = BotSettings.getTabName();
		mcbot mbot = setting.bots.get(OldTabName);
		setting.settings.remove(OldTabName);
		setting.settings.put(nact, BotSettings);
		if (BotSettings.mojangusername) {
			mbot.setipandport(BotSettings.serverip, BotSettings.serverport, BotSettings.servername, BotSettings.mcurrentusername);
		} else {
			mbot.setipandport(BotSettings.serverip, BotSettings.serverport, BotSettings.servername, BotSettings.nick);
		}
		mbot.updaterawbot(BotSettings);
		setting.bots.remove(OldTabName);
		setting.bots.put(nact, mbot);
		storage.gettabbedpane().setTitleAt(TabIndex, nact);
		storage.savesettings();
	}

	/**
	 * Returns true if settings provided in BotSettings are correct
	 * 
	 * @param OldTabName
	 * @param BotSettings
	 * @return Returns True if successful, False if otherwise
	 */
	public static boolean verifysettings(String OldTabName, botsettings BotSettings) {
		boolean namecorrection = !(OldTabName.toLowerCase().equals(BotSettings.getTabName().toLowerCase()));
		// To use in double check
		if (!BotSettings.isDoubleExclusive(namecorrection)) {
			storage.alert("Configuration error", "There is another bot with this servername and nickname");
			return false;
		}
		return true;
	}

	/**
	 * Removes bot based on Tab name
	 * 
	 * @param BotTabName
	 */
	public static void removebotbytabname(String BotTabName) {
		int id = storage.gettabbyname(BotTabName);
		mcbot bot = storage.getcurrentselectedbot();
		if (bot != null) {
			bot.disconnect();
		}
		storage.getInstance().settin.bots.remove(BotTabName);
		storage.getInstance().settin.settings.remove(BotTabName);
		storage.getInstance().tabbedPane.remove(id);
		storage.savesettings();
	}

	private static String getcolorfromtypeashex(String type) {
		switch (type) {
			case "0":
				return "#333333";
			case "1":
				return "#0000aa";
			case "2":
				return "#00aa00";
			case "3":
				return "#00aaaa";
			case "4":
				return "#aa0000";
			case "5":
				return "#aa00aa";
			case "6":
				return "#ffaa00";
			case "7":
				return "#aaaaaa";
			case "8":
				return "#555555";
			case "9":
				return "#5555ff";
			case "a":
				return "#55ff55";
			case "b":
				return "#55ffff";
			case "c":
				return "#ff5555";
			case "d":
				return "#ff55ff";
			case "e":
				return "#ffff55";
			case "f":
				return "#ffffff";
		}
		return "#ffffff";
	}

	/**
	 * Returns HTML formated Message
	 * 
	 * @param Message
	 *            Message to be parsed
	 * @return Returns parsed message
	 */
	public static String parsecolorashtml(String Message) {
		if (Message == null) {
			return Message;
		}
		if (Message.length() > 0) {
			StringBuilder sb = new StringBuilder();
			Message = " " + Message;
			String bold = "";
			String rebold = "";
			String underline = "";
			String reunderline = "";
			String strike = "";
			String restrike = "";
			String italic = "";
			String reitalic = "";
			String color = "<font color=#ffffff>";
			String recolor = "</font>";
			String[] msgs = (Message + "\n").split("§");
			for (String msg : msgs) {
				String tmg = msg.substring(0, 1).toLowerCase();
				if (tmg.equals("l")) {
					bold = "<b>";
					rebold = "</b>";
				} else if (tmg.equals("m")) {
					strike = "<strike>";
					restrike = "</strike>";
				} else if (tmg.equals("n")) {
					underline = "<u>";
					reunderline = "</u>";
				} else if (tmg.equals("o")) {
					italic = "<i>";
					reitalic = "</i>";
				} else if (tmg.equals("r")) {
					bold = "";
					rebold = "";
					strike = "";
					restrike = "";
					underline = "";
					reunderline = "";
					italic = "";
					reitalic = "";
					color = "";
					recolor = "";
				} else {
					color = "<font color=" + getcolorfromtypeashex(tmg) + ">";
				}
				String restmsg = msg.substring(1);
				if (restmsg.length() > 0) {
					restmsg = restmsg.replace("<", "&lt;").replace(">", "&gt;");
					sb.append(italic + underline + strike + bold + color + restmsg + recolor + rebold + restrike + reunderline + reitalic);
				}
			}
			return "<html>" + sb.toString() + "</html>";
		} else {
			return "";
		}
	}

	/**
	 * Strips html formated strings, such as used in Player List
	 * 
	 * @param str
	 *            Contains HTML to be parsed
	 * @return Returns stripped string or original string
	 */
	public static String striphtml(String str) {
		return str.replaceAll("<[^>]*>", "");
		/*
		 * if(!str.contains("<")) { return str; } StringBuilder sb=new
		 * StringBuilder(); int len=str.length(); int i=0; while(i<len && i>=0)
		 * { if(str.indexOf("<",i)<0) { sb.append(str.substring(i)); break; }
		 * sb.append(str.substring(i,str.indexOf("<",i))); i=str.indexOf(">",i);
		 * } return sb.toString();
		 */
	}

	/**
	 * Reports exception
	 * 
	 * @param e
	 *            The exception to be reported
	 * @return Returns True if successful, False if otherwise
	 */
	public static boolean reportthis(Exception e) {
		if (storage.getAutodebug()) {
			storage.conlog("Reporting error...");
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String error = sw.toString();
			storage.sendissue(error);
			return true;
		} else {
			return false;
		}
	}

	protected static mcbot getspecialbot() {
		mcbot bot = null;
		String tab = storage.getselectedtabtitle();
		if (tab.equals("Main@Reticle")) {
			bot = storage.getInstance().mainer;
		} else if (tab.equals("Support@Reticle")) {
			bot = storage.getInstance().support;
		}
		return bot;
	}

	/**
	 * Returns bot tab index based by name
	 * 
	 * @param BotTabName
	 *            Bot identification
	 * @return Returns index of bot
	 */
	public static int gettabbyname(String BotTabName) {
		int count = storage.gettabbedpane().getTabCount();
		for (int i = 0; i < count; i++) {
			if (storage.gettabbedpane().getTitleAt(i).equals(BotTabName)) {
				return i;
			}
		}
		return -1;
	}

	protected static void connectall() {
		HashMap<String, mcbot> bots = storage.getInstance().settin.bots;
		for (String botname : bots.keySet()) {
			mcbot bot = bots.get(botname);
			bot.connect();
		}
	}

	protected static void disconnectall() {
		HashMap<String, mcbot> bots = storage.getInstance().settin.bots;
		for (String botname : bots.keySet()) {
			mcbot bot = bots.get(botname);
			bot.disconnect();
		}
	}

	protected static void reconnectall() {
		HashMap<String, mcbot> bots = storage.getInstance().settin.bots;
		for (String botname : bots.keySet()) {
			mcbot bot = bots.get(botname);
			bot.softReconnect();
		}
	}

	/**
	 * Inserts Message into ignored message list Messages in this list are not
	 * displayed in chat
	 * 
	 * @param Message
	 */
	public static void addtoignoreforcurrentbot(String Message) {
		if (Message != null) {
			if (Message.length() > 0) {
				mcbot bot = storage.getcurrentselectedbot();
				try {
					bot.addToIgnoreList(Message);
				} catch (NullPointerException e) {
				}
			}
		}
	}

	/**
	 * PHP - like implode
	 * 
	 * @param pattern
	 * @param res
	 * @return Imploded string
	 */
	public static String implode(String pattern, String[] res) {
		int len = res.length;
		if (len == 0) {
			return "";
		} else if (len == 1) {
			return res[0];
		} else {
			int patlen = pattern.length();
			String result = "";
			for (String s : res) {
				result = result + pattern + s;
			}
			return result.substring(patlen);
		}
	}

	protected static void closing() {
		storage.pluginManager.unloadAllPlugins();
	}
}
