package org.spigot.reticle.settings;

import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;

import org.spigot.reticle.storage;

public class botsettings {
	public String servername = "Unknown";
	public String serverip = "127.0.0.1";
	public int serverport = 25565;
	public String nick = "Reticle";
	public int afkperiod = 60;
	public boolean autoconnect = false;
	public boolean autologin = false;
	public boolean autologout = false;
	public boolean autoantiafk = true;
	public boolean activenotify = false;
	public boolean autoreconnect = true;
	public int autoreconnectdelay = 5;
	public String[] autologincmd = new String[0];
	public String[] autologoutcmd = new String[0];
	public String[] autoantiafkcmd = new String[0];
	public String[] ignored = new String[0];
	public boolean isMain = false;
	public int protocolversion = 4;
	public String mpassword = "";
	public ComboBoxModel<String> musernames = new DefaultComboBoxModel<String>(new String[] { storage.default_online_nick });
	public boolean mojangusername = false;
	public boolean savemojangpass = false;
	public boolean saveaccess = true;
	public String mcurrentusername = "";
	public String maccesstoken;
	public String mplayertoken;
	public HashMap<String, String> mojangusernamelist = new HashMap<String, String>();
	public String mojangloginusername;
	public String mojangloginusernameid;
	public int messagedelay = 0;
	public boolean chatlog = true;
	public int maxlines = 500;
	public Font font = new Font("Arial", Font.PLAIN, 12);
	public List<String> plugins = new ArrayList<String>();
	public boolean useproxy = false;
	public String proxyip = "";
	public int proxyport = 0;

	/**
	 * Returns ID Not safe to use
	 * @param username Username to get ID from
	 * @return Returns Mojang username ID
	 */
	public String getMojangID(String username) {
		return mojangloginusernameid;
	}

	public botsettings(String name) {
		this.nick = name;
	}

	/**
	 * Returns tab name
	 * 
	 * @return Returns tab name
	 */
	public String getTabName() {
		if (isMain) {
			return this.nick + "@" + "Reticle";
		} else {
			return this.nick + "@" + this.servername;
		}
	}

	/**
	 * Returns true if it is the only bot of this name
	 * 
	 * @return Returns true if it is the only bot of this name
	 */
	public boolean isExclusive() {
		if (this.servername.toLowerCase().equals("reticle") && !isMain) {
			return false;
		}
		HashMap<String, botsettings> bots = storage.getInstance().settin.settings;
		String bottabname = getTabName().toLowerCase();
		for (String bot : bots.keySet()) {
			if (bot.toLowerCase().equals(bottabname.toLowerCase())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Not safe to use
	 * 
	 * @param hasthis Internal structure handler
	 * @return True if bot is exclusive
	 */
	public boolean isDoubleExclusive(boolean hasthis) {
		if (this.servername.toLowerCase().equals("reticle") && !isMain) {
			return false;
		}
		HashMap<String, botsettings> bots = storage.getInstance().settin.settings;
		String bottabname = getTabName().toLowerCase();
		for (String bot : bots.keySet()) {
			if (bot.toLowerCase().equals(bottabname.toLowerCase())) {
				/*
				 * if(!bots.get(bot).equals(this)) { return false; }
				 */
				if (hasthis) {
					return false;
				} else if (bots.get(bot).equals(this)) {
					hasthis = true;
				}

			}
		}
		return true;
	}
	
	/*
	 * protected void registerbot() { if(isMain) {
	 * storage.getInstance().settin.specialbots.add(getTabName()); }
	 * storage.getInstance().settin.settings.put(getTabName(), this); }
	 * 
	 * protected void unregisterbot() { if(isMain) {
	 * storage.getInstance().settin.specialbots.remove(getTabName()); }
	 * storage.getInstance().settin.settings.remove(getTabName()); } protected
	 * void renamebot(String newname) { unregisterbot(); registerbot(); }
	 */
}
