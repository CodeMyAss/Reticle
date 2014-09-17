package org.spigot.reticle.settings;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

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
	protected JComboBox<String> textmusername;
	protected JCheckBox checkmoj;
	protected JCheckBox savemojpass;
	protected JCheckBox saveaccess;

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
		this.textmusername.setModel(set.musernames);
		this.checkmoj.setSelected(set.mojangusername);
		this.savemojpass.setSelected(set.savemojangpass);
		this.saveaccess.setSelected(set.saveaccess);
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
		if(index==0) {
			return 4;
		} else if(index==1) {
			return 5;
		} else if(index==2) {
			return 47;
		}
		return 4;
	}

	public botsettings getsettings() {
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
		struct.protocolversion=this.indextoprococolversion(this.protocolversion.getSelectedIndex());
		struct.mpassword=new String(this.textmpassword.getPassword());
		struct.musernames=this.textmusername.getModel();
		struct.mojangusername=this.checkmoj.isSelected();
		struct.savemojangpass=this.savemojpass.isSelected();
		struct.saveaccess=this.saveaccess.isSelected();
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
