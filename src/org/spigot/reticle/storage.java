package org.spigot.reticle;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Frame;
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
import java.util.Set;

import javax.sql.rowset.serial.SerialException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.spigot.reticle.botfactory.mcbot;
import org.spigot.reticle.botfactory.mcbot.ICONSTATE;
import org.spigot.reticle.resources.resources;
import org.spigot.reticle.settings.aboutwin;
import org.spigot.reticle.settings.botsettings;
import org.spigot.reticle.settings.optionswin;
import org.spigot.reticle.settings.set_obj_struct;
import org.spigot.reticle.settings.settings;
import org.spigot.reticle.settings.struct_settings;
import org.spigot.reticle.sockets.Reporter;

public class storage {
	public static String version = "1.02 beta";

	protected EventHandler handler = new EventHandler();

	private static storage instance = null;

	public final static String homepage = "http://reticle.mc-atlantida.eu/";

	private static String settingfile = "settings.ini";

	public struct_settings settin;

	public settings settings;

	// Main win
	public mcbot mainer;

	// Main win tabs
	public JTabbedPane tabbedPane;

	// Main win menu
	public JMenuItem menu_con;
	public JMenuItem menu_dis;
	public JMenuItem menu_set;

	// Settings window
	public settings setwin;

	// About window
	public aboutwin aboutwin;

	// Updating thread (Single)
	public updater updater;

	public Frame winobj;

	public JTextField setwin_txtservername;

	public set_obj_struct setobj = new set_obj_struct();

	// Global settings window
	private optionswin optwin;

	// Main window object
	public JFrame mainframe;

	// It apparently does not depend on the logics itself but on the compilers
	// mood

	final static Class<?> thisClass = resources.class;
	public static Icon icon_off = new ImageIcon(thisClass.getResource("icon_off.png"));
	public static Icon icon_on = new ImageIcon(thisClass.getResource("icon_on.png"));
	public static Icon icon_dis = new ImageIcon(thisClass.getResource("icon_dis.png"));
	public static Icon icon_con = new ImageIcon(thisClass.getResource("icon_con.png"));
	public static ImageIcon icon_loader = new ImageIcon(thisClass.getResource("logo.png"));
	public static String icon_loader_path = thisClass.getResource("logo.png").getFile();
	public static ImageIcon winicon = new ImageIcon(thisClass.getResource("mainicon.png"));
	

	public synchronized static void closeoptionswin() {
		if (storage.getInstance().optwin != null) {
			storage.getInstance().optwin.dispose();
			storage.getInstance().optwin = null;
		}
	}

	public static String getconsoletext() {
		mcbot bot = storage.getInstance().mainer;
		return bot.getmsg(5000);
	}

	public static void sendissue() {
		Reporter rp = new Reporter(Reporter.ACTION.REPORTISSUE);
		rp.issue = storage.getconsoletext();
		rp.start();
	}

	public static void sendissue(String issue) {
		Reporter rp = new Reporter(Reporter.ACTION.REPORTISSUE);
		rp.issue = issue;
		rp.start();
	}

	public synchronized static void checkforupdates() {
		if (storage.getInstance().updater == null) {
			storage.getInstance().updater = new updater();
			storage.conlog("Updater service is now running");
			storage.getInstance().updater.start();
		} else {
			storage.conlog("Updater service is already running");
		}
	}

	public synchronized static void openaboutwin() {
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

	public synchronized static void closeaboutwin() {
		if (storage.getInstance().aboutwin != null) {
			storage.getInstance().aboutwin.dispose();
			storage.getInstance().aboutwin = null;
		}
	}

	public static void openweb(String url) {
		try {
			Desktop.getDesktop().browse(new URL(url).toURI());
		} catch (Exception e) {
			storage.conlog("Opening URL operation is not supported by this system");
		}
	}

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

	public synchronized static void displayoptionswin() {
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

	public static void setglobalsettings(HashMap<String, String> map) {
		storage.getInstance().settin.globalsettings = map;
	}

	public static HashMap<String, String> getglobalsettings() {
		return storage.getInstance().settin.globalsettings;
	}

	public static JTabbedPane gettabbedpane() {
		return storage.getInstance().tabbedPane;
	}

	public static void killall() {
		HashMap<String, mcbot> bots = storage.getInstance().settin.bots;
		for (String name : bots.keySet()) {
			mcbot bot = bots.get(name);
			bot.disconnect();
		}
	}

	public static void changemenuitems() {
		mcbot bot = storage.getcurrentselectedbot();
		if (bot == null) {
			storage.setdisabled();
		} else if (bot.ismain) {
			storage.setdisabled();
		} else if (bot.isConnected()) {
			storage.setconnected();
		} else {
			storage.setdisconnected();
		}
	}

	public static void setselectedtable(int i) {
		// +1 because main is not in settin
		storage.getInstance().tabbedPane.setSelectedIndex(i + 1);
	}

	public static void setselectedtable(String str) {
		Set<String> indexes = storage.getInstance().settin.bots.keySet();
		int i = 0;
		for (String index : indexes) {
			if (index.equals(str)) {
				setselectedtable(i);
				break;
			}
			i++;
		}
	}

	public static boolean sendmessagetoactivebot(String message) {
		mcbot bot = storage.getcurrentselectedbot();
		if (bot != null) {
			return bot.sendtoserver(message);
		} else {
			// Main
			return false;
		}
	}

	public static void closesettingswindow() {
		storage.getInstance().winobj.dispose();
		storage.getInstance().winobj = null;
	}

	public static void setconnected() {
		storage.getInstance().menu_con.setEnabled(false);
		storage.getInstance().menu_dis.setEnabled(true);
		storage.getInstance().menu_set.setEnabled(true);

	}

	public static void setdisconnected() {
		storage.getInstance().menu_con.setEnabled(true);
		storage.getInstance().menu_dis.setEnabled(false);
		storage.getInstance().menu_set.setEnabled(true);
	}

	public static void setdisabled() {
		storage.getInstance().menu_con.setEnabled(false);
		storage.getInstance().menu_dis.setEnabled(false);
		storage.getInstance().menu_set.setEnabled(false);
	}

	public static set_obj_struct getsettingsobj() {
		return storage.getInstance().setobj;
	}

	public static boolean settingwindowopened() {
		return (storage.getInstance().winobj != null);
	}

	public synchronized static void opensettingswindow() {
		if (settingwindowopened()) {
			storage.getInstance().winobj.requestFocus();
		} else {
			botsettings set = getcurrenttabsettings();
			if (set != null) {
				storage.getInstance().winobj = new settings(set);
			}
		}
	}

	public static int getselectedtabindex() {
		return storage.getInstance().tabbedPane.getSelectedIndex();
	}

	public static String getselectedtabtitle() {
		return storage.getInstance().tabbedPane.getTitleAt(getselectedtabindex());
	}

	public static mcbot getcurrentselectedbot() {
		return storage.getInstance().settin.bots.get(storage.getselectedtabtitle());
	}

	public static void alert(String title, String message) {
		Component comp = storage.gettabbedpane();
		JOptionPane.showMessageDialog(comp, message, title, JOptionPane.ERROR_MESSAGE);
	}

	public static void addbot() {
		botsettings bot = new botsettings("Untitled");
		if (!bot.isExclusive()) {
			storage.alert("Error", "Cannot add new bot. There might be one not configured yet.");
			return;
		} else {
			mcbot mbot = new mcbot(bot);
			mbot.ismain = false;
			storage.getInstance().settin.settings.put(bot.gettabname(), bot);
			storage.getInstance().settin.bots.put(mbot.gettabname(), mbot);
			mbot.seticon(ICONSTATE.DISCONNECTED);
			savesettings();
		}
	}

	public static String stripcolors(String str) {
		return str.replaceAll("(\\§.)", "");
	}

	public static void conlog(String message) {
		if (message.length() > 0) {
			mcbot bot = storage.getInstance().mainer;
			bot.logmsg(message);
		}
	}

	public static void firsttabload() {
		HashMap<String, botsettings> tabs = storage.getInstance().settin.settings;
		HashMap<String, mcbot> bots = storage.getInstance().settin.bots;
		if (tabs != null) {
			for (String key : tabs.keySet()) {
				botsettings set = tabs.get(key);
				mcbot bot = new mcbot(set);
				bot.ismain = false;
				if (set.autoconnect) {
					bot.connect();
				}
				bots.put(key, bot);
			}
		}
	}

	public static botsettings getcurrenttabsettings() {
		return storage.getInstance().settin.settings.get(getselectedtabtitle());
	}

	public static void savesettings() {
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

	public static void loadsettings() throws SerialException {
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

	public static storage getInstance() {
		if (instance == null) {
			instance = new storage();
		}
		return instance;
	}

	public static void resetset(botsettings bs, String acti, int actnum) {
		struct_settings setting = storage.getInstance().settin;
		String nact = bs.gettabname();
		setting.settings.remove(acti);
		setting.settings.put(nact, bs);
		mcbot mbot = setting.bots.get(acti);
		mbot.setipandport(bs.serverip, bs.serverport, bs.servername, bs.nick);
		mbot.updaterawbot(bs);
		setting.bots.remove(acti);
		setting.bots.put(nact, mbot);
		storage.gettabbedpane().setTitleAt(actnum, nact);
		storage.savesettings();
	}

	public static boolean verifysettings(String acti, botsettings bot) {
		boolean namecorrection = !(acti.toLowerCase().equals(bot.gettabname().toLowerCase()));
		// To use in double check
		if (!bot.isDoubleExclusive(namecorrection)) {
			storage.alert("Configuration error", "There is another bot with this servername and nickname");
			return false;
		}
		return true;
	}

	public static void removecurrentbot() {
		int id = storage.getselectedtabindex();
		String name = storage.getselectedtabtitle();
		mcbot bot = storage.getcurrentselectedbot();
		bot.disconnect();
		storage.getInstance().settin.bots.remove(name);
		storage.getInstance().settin.settings.remove(name);
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

	public static String parsecolorashtml(String message) {
		if (message.length() > 0) {
			StringBuilder sb = new StringBuilder();
			message = " " + message;
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
			String[] msgs = (message + "\n").split("§");
			for (String msg : msgs) {
				String tmg = msg.substring(0, 1).toLowerCase();
				if (tmg.equals("l")) {
					bold = "<b>";
					rebold = "</b>";
				} else if (tmg.equals("m")) {
					strike = "<strike>";
					restrike = "</strike>";
				} else if (tmg.equals("n")) {
					underline = "<n>";
					reunderline = "</n>";
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

}
