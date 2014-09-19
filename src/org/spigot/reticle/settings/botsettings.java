package org.spigot.reticle.settings;

import java.util.HashMap;

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
	public int autoreconnectdelay = 2;
	public String[] autologincmd = new String[0];
	public String[] autologoutcmd = new String[0];
	public String[] autoantiafkcmd = new String[0];
	public String[] ignored = new String[0];
	public boolean isMain = false;
	public int protocolversion = 4;
	public String mpassword="";
	public ComboBoxModel<String> musernames=new DefaultComboBoxModel<String>(new String[] { storage.default_online_nick });
	public boolean mojangusername=false;
	public boolean savemojangpass=false;
	public boolean saveaccess=true;
	public String mcurrentusername="";
	public String maccesstoken;
	public String mplayertoken;
	public HashMap<String,String> mojangusernamelist=new HashMap<String,String>();
	public String mojangloginusername;
	public String mojangloginusernameid;
	public int messagedelay;
	public boolean chatlog;

	public String getMojangID(String username) {
		return mojangloginusernameid;
	}

	public botsettings(String name) {
		this.nick = name;
	}

	public String gettabname() {
		if (isMain) {
			return this.nick + "@" + "Reticle";
		} else {
			return this.nick + "@" + this.servername;
		}
	}

	public boolean isExclusive() {
		if (this.servername.toLowerCase().equals("reticle") && !isMain) {
			return false;
		}
		HashMap<String, botsettings> bots = storage.getInstance().settin.settings;
		String bottabname = gettabname().toLowerCase();
		for (String bot : bots.keySet()) {
			if (bot.toLowerCase().equals(bottabname.toLowerCase())) {
				return false;
			}
		}
		return true;
	}

	public boolean isDoubleExclusive(boolean hasthis) {
		if (this.servername.toLowerCase().equals("reticle") && !isMain) {
			return false;
		}
		HashMap<String, botsettings> bots = storage.getInstance().settin.settings;
		String bottabname = gettabname().toLowerCase();
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

	public void registerbot() {
		storage.getInstance().settin.settings.put(gettabname(), this);
	}

	public void unregisterbot() {
		storage.getInstance().settin.settings.remove(gettabname());
	}

	public void renamebot(String newname) {
		unregisterbot();
		registerbot();
	}

}
