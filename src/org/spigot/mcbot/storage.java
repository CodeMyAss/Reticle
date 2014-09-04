package org.spigot.mcbot;

import java.awt.Component;
import java.awt.Frame;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.spigot.mcbot.botfactory.mcbot;
import org.spigot.mcbot.botfactory.mcbot.ICONSTATE;
import org.spigot.mcbot.settings.botsettings;
import org.spigot.mcbot.settings.set_obj_struct;
import org.spigot.mcbot.settings.struct_settings;
import org.spigot.mcbot.settings.settings;

public class storage {
	private static storage instance = null;

	private static String settingfile = "settings.ini";

	public struct_settings settin;

	public settings settings;

	// Main win tabs
	public JTabbedPane tabbedPane;

	// Settings window
	public settings setwin;

	public Frame winobj;

	public JTextField setwin_txtservername;

	public set_obj_struct setobj = new set_obj_struct();

	public static Icon icon_off = new ImageIcon("resources/icon_off.PNG");
	public static Icon icon_on = new ImageIcon("resources/icon_on.PNG");
	public static Icon icon_dis = new ImageIcon("resources/icon_dis.PNG");
	public static Icon icon_con = new ImageIcon("resources/icon_con.PNG");

	public static JTabbedPane gettabbedpane() {
		return storage.getInstance().tabbedPane;
	}

	public static Frame getsettingwin() {
		return storage.getInstance().winobj;
	}
	
	
	
	public static boolean sendmessagetoactivebot(String message) {
		mcbot bot=storage.getcurrentselectedbot();
		return bot.sendtoserver(message);
	}

	public static void setsetvis(boolean vis) {
		storage.getInstance().winobj.setVisible(vis);
	}

	public static set_obj_struct getsettingsobj() {
		return storage.getInstance().setobj;
	}

	public static boolean settingwindowopened() {
		return storage.getInstance().winobj.isVisible();
	}

	public static void opensettingswindow() {
		if (settingwindowopened()) {
			storage.setsetvis(true);
		} else {
			botsettings set = getcurrenttabsettings();
			if (set != null) {
				Frame swin = getsettingwin();
				swin.setVisible(true);
				set_obj_struct sobj = storage.getsettingsobj();
				sobj.setsettings(set);
			}
		}
	}

	public static void getselectedtab() {
		//Component component = storage.getInstance().tabbedPane.getSelectedComponent();
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
			storage.getInstance().settin.settings.put(bot.bottabname, bot);
			storage.getInstance().settin.bots.put(mbot.gettabname(), mbot);
			mbot.seticon(ICONSTATE.DISCONNECTED);
			savesettings();
		}
	}

	public static void firsttabload() {
		HashMap<String, botsettings> tabs = storage.getInstance().settin.settings;
		HashMap<String, mcbot> bots = storage.getInstance().settin.bots;
		if (tabs != null) {
			for (String key : tabs.keySet()) {
				botsettings set = tabs.get(key);
				set.bottabname = key;
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

	public static void loadsettings() {
		try {
			String setraw = new String(Files.readAllBytes(Paths.get(settingfile)));
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
		String nact=bs.gettabname();
		setting.settings.remove(acti);
		setting.settings.put(nact, bs);
		mcbot mbot=setting.bots.get(acti);
		setting.bots.put(nact, mbot);
		setting.bots.remove(acti);
		storage.gettabbedpane().setTitleAt(actnum, nact);
		storage.savesettings();
	}

	
	public static boolean verifysettings(botsettings bot) {
		if(!bot.isDoubleExclusive()) {
			storage.alert("Configuration error", "There is another bot with this servername and nickname");
			return false;
		}
		return true;
	}
}
