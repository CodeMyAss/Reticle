package org.spigot.reticle.settings;

import java.util.HashMap;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.spigot.reticle.storage;

public class set_obj_struct {

	// Settings window
	protected JTextField txtservername;
	protected JTextField textserverip;
	protected JTextField textserverport;
	protected JCheckBox checkBox;
	protected JTextField txtNick;
	protected JCheckBox checkactiv;
	protected JCheckBox checkconcom;
	protected JCheckBox checkdisccom;
	protected JCheckBox checkafkcom;
	protected JTextArea textlogincom;
	protected JTextArea textlogoutcom;
	protected JTextArea textafkcom;
	protected JTextField textcurtabname;
	protected JTextField textantiafkdelay;
	protected JCheckBox checkautoreconnect;
	protected JTextField textreconnectdelay;
	protected JTextArea textignore;
	protected JComboBox<String> protocolversion;

	// Global settings window
	protected JCheckBox autoupdate;
	protected JCheckBox autodebug;
	protected JCheckBox autoplugins;

	// Mojang stuff in settings
	protected JPasswordField textmpassword;
	protected JCheckBox checkmoj;
	protected JCheckBox savemojpass;
	protected JCheckBox saveaccess;
	protected JComboBox<String> musernames;
	protected JTextField textmusername;
	protected JTextField messagedelay;
	protected JCheckBox chatlog;
	protected JTextField textmaxlines;

	protected void setglobals(JCheckBox b1, JCheckBox b2, JCheckBox b3) {
		this.autoupdate = b1;
		this.autodebug = b2;
		this.autoplugins = b3;
	}

	/**
	 * 
	 * @param botSettings
	 */
	protected void setsettings(botsettings botSettings) {
		this.txtservername.setText(botSettings.servername);
		this.textserverip.setText(botSettings.serverip);
		this.textserverport.setText(botSettings.serverport + "");
		this.checkBox.setSelected(botSettings.autoconnect);
		this.txtNick.setText(botSettings.nick);
		this.checkactiv.setSelected(botSettings.activenotify);
		this.checkconcom.setSelected(botSettings.autologin);
		this.checkdisccom.setSelected(botSettings.autologout);
		this.checkafkcom.setSelected(botSettings.autoantiafk);
		this.textlogincom.setText(implode("\n", botSettings.autologincmd));
		this.textlogoutcom.setText(implode("\n", botSettings.autologoutcmd));
		this.textafkcom.setText(implode("\n", botSettings.autoantiafkcmd));
		this.textcurtabname.setText(botSettings.getTabName());
		this.textantiafkdelay.setText(botSettings.afkperiod + "");
		this.checkautoreconnect.setSelected(botSettings.autoreconnect);
		this.textreconnectdelay.setText(botSettings.autoreconnectdelay + "");
		this.textignore.setText(implode("\n", botSettings.ignored));
		this.protocolversion.setSelectedIndex(protocolversiontoindex(botSettings.protocolversion));
		this.textmpassword.setText(botSettings.mpassword);
		this.musernames.setModel(botSettings.musernames);
		if (botSettings.mcurrentusername != null) {
			botSettings.musernames.setSelectedItem(botSettings.mcurrentusername);
		}
		this.checkmoj.setSelected(botSettings.mojangusername);
		if (botSettings.mojangusername) {
			this.txtNick.setEnabled(false);
			this.musernames.setEnabled(true);
		}
		this.savemojpass.setSelected(botSettings.savemojangpass);
		this.saveaccess.setSelected(botSettings.saveaccess);
		this.textmusername.setText(botSettings.mojangloginusername);
		this.messagedelay.setText(botSettings.messagedelay+"");
		this.chatlog.setSelected(botSettings.chatlog);
		this.textmaxlines.setText(botSettings.maxlines+"");
	}

	private int protocolversiontoindex(int ver) {
		if (ver == 4) {
			return 0;
		} else if (ver == 5) {
			return 1;
		} else if (ver == 47) {
			return 2;
		}
		return 0;
	}

	private int indextoprococolversion(int index) {
		if (index == 0) {
			return 4;
		} else if (index == 1) {
			return 5;
		} else if (index == 2) {
			return 47;
		}
		return 4;
	}

	protected botsettings getsettings(String accesstoken, String playertoken, HashMap<String, String> mojangusernamelist, String mcurrentusername, String mojangloginusernameid) {
		botsettings struct = new botsettings(null);
		struct.servername = this.txtservername.getText();
		struct.serverip = this.textserverip.getText();
		struct.serverport = Integer.parseInt(this.textserverport.getText());
		struct.autoconnect = this.checkBox.isSelected();
		struct.nick = this.txtNick.getText();
		struct.activenotify = this.checkactiv.isSelected();
		struct.autologin = this.checkconcom.isSelected();
		struct.autologout = this.checkdisccom.isSelected();
		struct.autoantiafk = this.checkafkcom.isSelected();
		struct.autoantiafkcmd = this.textafkcom.getText().split("\n");
		struct.autologincmd = this.textlogincom.getText().split("\n");
		struct.autologoutcmd = this.textlogoutcom.getText().split("\n");
		struct.afkperiod = Integer.parseInt(this.textantiafkdelay.getText());
		struct.autoreconnect = this.checkautoreconnect.isSelected();
		struct.autoreconnectdelay = Integer.parseInt(this.textreconnectdelay.getText());
		struct.ignored = this.textignore.getText().split("\n");
		struct.protocolversion = this.indextoprococolversion(this.protocolversion.getSelectedIndex());
		struct.mpassword = new String(this.textmpassword.getPassword());
		struct.musernames = this.musernames.getModel();
		struct.mojangusername = this.checkmoj.isSelected();
		struct.savemojangpass = this.savemojpass.isSelected();
		struct.saveaccess = this.saveaccess.isSelected();
		if (this.textmusername.getText() != null && this.textmusername.getText() != storage.default_online_nick) {
			struct.mojangloginusername = this.textmusername.getText();
		}
		if (accesstoken != null) {
			struct.maccesstoken = accesstoken;
		}
		if (playertoken != null) {
			struct.mplayertoken = playertoken;
		}
		if (mojangusernamelist != null) {
			struct.mojangusernamelist = mojangusernamelist;
		}
		if (mcurrentusername != null && !mcurrentusername.equals(storage.default_online_nick) && !mcurrentusername.equals("")) {
			struct.mcurrentusername = mcurrentusername;
		}
		if (mojangloginusernameid != null) {
			struct.mojangloginusernameid = mojangloginusernameid;
		}
		struct.messagedelay=Integer.parseInt(this.messagedelay.getText());
		struct.chatlog=this.chatlog.isSelected();
		struct.maxlines=Integer.parseInt(this.textmaxlines.getText());
		return struct;
	}

	private static String implode(String pattern, String[] res) {
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
}
