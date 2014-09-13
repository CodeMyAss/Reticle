package org.spigot.reticle.botfactory;

import java.awt.Color;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.spigot.reticle.storage;
import org.spigot.reticle.settings.botsettings;
import org.spigot.reticle.settings.team_struct;
import org.spigot.reticle.sockets.connector;

public class mcbot {
	private JTextPane chatlog;
	private JTable tabler;
	private JCheckBox autoscroll;
	private botsettings rawbot;
	public boolean isconnected = false;
	public boolean autoconnect = false;
	public boolean exists = false;
	public boolean ismain = false;
	private HashMap<String, Style> styles = new HashMap<String, Style>();
	public connector connector;
	public String serverip;
	public int serverport;
	public String username;
	public int[] tablistsize = new int[2];

	

	public mcbot(botsettings bot, boolean ismain) {
		this.ismain = ismain;
		bot.isMain = ismain;
		this.rawbot = bot;
		this.tablistsize[0] = 1;
		this.tablistsize[1] = 20;
		initwin();
	}

	public mcbot(botsettings bot) {
		this.rawbot = bot;
		initwin();
	}

	private void initwin() {
		this.serverip = this.rawbot.serverip;
		this.serverport = this.rawbot.serverport;
		this.username = this.rawbot.nick;
		botfactory.makenewtab(this);
		if (ismain) {
			seticon(ICONSTATE.MAIN);
		} else {
			seticon(ICONSTATE.DISCONNECTED);
		}
	}

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

	public void seticon(final ICONSTATE state) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				storage.gettabbedpane().setIconAt(gettabid(), state.icon);
			}
		});
	}

	public void setconfig(JTextPane chatlog, JTable tablist, JPanel panel, JCheckBox autoscroll) {
		this.chatlog = chatlog;
		this.tabler = tablist;
		this.autoscroll = autoscroll;
		this.exists = true;
	}

	public String gettabname() {
		return this.rawbot.gettabname();
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

	public int getantiafkperiod() {
		return this.rawbot.afkperiod;
	}

	public String[] getignoredmessages() {
		return this.rawbot.ignored;
	}

	public String[] getlogincommands() {
		return this.rawbot.autologincmd;
	}

	public String[] getlogoutcommands() {
		return this.rawbot.autologoutcmd;
	}

	public String[] getafkcommands() {
		return this.rawbot.autoantiafkcmd;
	}

	public boolean sendlogincommands() {
		return this.rawbot.autologin;
	}

	public boolean sendlogoutcommands() {
		return this.rawbot.autologout;
	}

	public boolean sendafkcommands() {
		return this.rawbot.autoantiafk;
	}

	public boolean getautoreconnect() {
		return this.rawbot.autoreconnect;
	}

	public int getautoreconnectdelay() {
		return this.rawbot.autoreconnectdelay;
	}

	public connector getConnector() {
		return this.connector;
	}

	private boolean goscroll() {
		return this.autoscroll.isSelected();
	}

	public boolean isConnected() {
		if (this.connector == null) {
			// Initial state
			return false;
		} else {
			return this.connector.isConnected();
		}
	}

	public boolean isConnectedAllowReconnect() {
		if (this.connector == null) {
			// Initial state
			return false;
		} else {
			return this.connector.isConnectedAllowReconnect();
		}
	}

	public boolean sendtoserver(String message) {
		if (this.isConnected()) {
			return this.connector.sendtoserver(message);
		} else {
			return false;
		}
	}

	@SuppressWarnings("deprecation")
	public void reconnect(boolean reconnect) {
		if (this.rawbot.serverip != null) {
			if (!this.isConnectedAllowReconnect()) {
				try {
					if(connector!=null) {
						connector.stop();
					}
					this.serverip = this.rawbot.serverip;
					this.serverport = this.rawbot.serverport;
					this.connector = new connector(this);
					connector.reconnect=reconnect;
					connector.start();
				} catch (UnknownHostException e) {
					this.logmsg("§4 Invalid IP or hostname");
				} catch (IOException e) {
					if (!storage.reportthis(e)) {
						e.printStackTrace();	
					}
				}
			}
		}
	}

	public void connect() {
		if (this.rawbot.serverip != null && this.connector == null) {
			try {
				if (!this.isConnected()) {
					this.serverip = this.rawbot.serverip;
					this.serverport = this.rawbot.serverport;
					this.connector = new connector(this);
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

	public synchronized void logmsg(String message) {
		if (message.length() > 0) {
			// Extra space because of the split method and following loop
			message = " [" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + message;
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

	public void disconnect() {
		this.connector.endreconnectwaiting();
		if (this.isConnected()) {
			// To prevent automatic restart
			this.connector.reconnect = false;
			// Go for it
			this.connector.stopMe();
			seticon(ICONSTATE.DISCONNECTED);
			storage.changemenuitems();
		}
	}

	public void setTabSize(int cols, int rows) {
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

	public void refreshtablist(List<String> tablist, HashMap<String, String> playerteams, HashMap<String, team_struct> teams) {
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
			int imax = tablist.size();
			for (int i = 0; i < max; i++) {
				String name;
				if (i < imax) {
					name = tablist.get(i);
				} else {
					name = "";
				}
				final int locx = i % x;
				final int locy = i / x;
				// Now we should parse player name by his team
				String realnamer = name;
				if (playerteams.containsKey(name)) {
					// He is in a team
					String teamname = playerteams.get(name);
					if (teams.containsKey(teamname)) {
						// His team exists
						realnamer = teams.get(teamname).getFormatedPlayer(realnamer);
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

	public void updaterawbot(botsettings bs) {
		// To make it reconnect if this change is necessary
		if (bs != null) {
			this.rawbot = bs;
			if (this.connector != null) {
				this.connector.reconnect = bs.autoreconnect;
			}
		}
	}

	public void resettablist() {
		this.setTabSize(0, 0);
		refreshtablist(new ArrayList<String>(), new HashMap<String, String>(), new HashMap<String, team_struct>());
	}
}
