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
	public JTextField txtservername;
	public JTextField textserverip;
	public JTextField textserverport;
	public JCheckBox checkBox;
	public JTextField txtNick;
	public JCheckBox checkactiv;
	public JCheckBox checkconcom;
	public JCheckBox checkdisccom;
	public JCheckBox checkafkcom;
	public JTextArea textlogincom;
	public JTextArea textlogoutcom;
	public JTextArea textafkcom;
	public JTextField textcurtabname;
	public JTextField textantiafkdelay;
	public JCheckBox checkautoreconnect;
	public JTextField textreconnectdelay;
	public JTextArea textignore;
	public JComboBox<String> protocolversion;

	// Global settings window
	public JCheckBox autoupdate;
	public JCheckBox autodebug;
	public JCheckBox autoplugins;

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

	public void setglobals(JCheckBox b1, JCheckBox b2, JCheckBox b3) {
		this.autoupdate = b1;
		this.autodebug = b2;
		this.autoplugins = b3;
	}

	public void setsettings(botsettings set) {
		this.txtservername.setText(set.servername);
		this.textserverip.setText(set.serverip);
		this.textserverport.setText(set.serverport + "");
		this.checkBox.setSelected(set.autoconnect);
		this.txtNick.setText(set.nick);
		this.checkactiv.setSelected(set.activenotify);
		this.checkconcom.setSelected(set.autologin);
		this.checkdisccom.setSelected(set.autologout);
		this.checkafkcom.setSelected(set.autoantiafk);
		this.textlogincom.setText(implode("\n", set.autologincmd));
		this.textlogoutcom.setText(implode("\n", set.autologoutcmd));
		this.textafkcom.setText(implode("\n", set.autoantiafkcmd));
		this.textcurtabname.setText(set.gettabname());
		this.textantiafkdelay.setText(set.afkperiod + "");
		this.checkautoreconnect.setSelected(set.autoreconnect);
		this.textreconnectdelay.setText(set.autoreconnectdelay + "");
		this.textignore.setText(implode("\n", set.ignored));
		this.protocolversion.setSelectedIndex(protocolversiontoindex(set.protocolversion));
		this.textmpassword.setText(set.mpassword);
		this.musernames.setModel(set.musernames);
		if (set.mcurrentusername != null) {
			set.musernames.setSelectedItem(set.mcurrentusername);
		}
		this.checkmoj.setSelected(set.mojangusername);
		if (set.mojangusername) {
			this.txtNick.setEnabled(false);
			this.musernames.setEnabled(true);
		}
		this.savemojpass.setSelected(set.savemojangpass);
		this.saveaccess.setSelected(set.saveaccess);
		this.textmusername.setText(set.mojangloginusername);
		this.messagedelay.setText(set.messagedelay+"");
		this.chatlog.setSelected(set.chatlog);
		this.textmaxlines.setText(set.maxlines+"");
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

	public botsettings getsettings(String accesstoken, String playertoken, HashMap<String, String> mojangusernamelist, String mcurrentusername, String mojangloginusernameid) {
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

}
