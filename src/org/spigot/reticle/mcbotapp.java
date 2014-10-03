package org.spigot.reticle;

import javax.sql.rowset.serial.SerialException;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JTabbedPane;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.spigot.reticle.botfactory.mcbot;
import org.spigot.reticle.settings.botsettings;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class mcbotapp {

	protected JFrame frmReticle;

	protected mcbotapp() throws SerialException {
		initialize(storage.version);
	}

	private void initialize(String version) throws SerialException {
		frmReticle = new JFrame();
		frmReticle.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				storage.closing();
			}
		});
		frmReticle.setFont(new Font("Bodoni MT", Font.PLAIN, 12));
		frmReticle.setForeground(Color.BLACK);
		frmReticle.setTitle("Reticle " + version);
		frmReticle.setBounds(100, 100, 642, 500);
		frmReticle.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		frmReticle.setJMenuBar(menuBar);

		JMenu mnNewMenu = new JMenu("Main");
		menuBar.add(mnNewMenu);

		JMenuItem mntmConnectAll = new JMenuItem("Connect all");
		mntmConnectAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				storage.connectall();
			}
		});
		mnNewMenu.add(mntmConnectAll);

		JMenuItem mntmDisconnectAll = new JMenuItem("Disconnect all");
		mntmDisconnectAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				storage.disconnectall();
			}
		});
		mnNewMenu.add(mntmDisconnectAll);

		JMenuItem mntmReconnectAll = new JMenuItem("Reconnect all");
		mntmReconnectAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				storage.reconnectall();
			}
		});
		mnNewMenu.add(mntmReconnectAll);

		JMenuItem mntmNewMenuItem = new JMenuItem("Options");
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				storage.displayoptionswin();
			}
		});

		mnNewMenu.add(mntmNewMenuItem);

		JMenuItem mntmNewMenuItem_8 = new JMenuItem("Add bot");
		mntmNewMenuItem_8.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (storage.settingwindowopened()) {
					storage.alert("Error", "Please close settings dialog before creating new bot.");
				} else {
					storage.addbot();
				}
			}
		});
		mnNewMenu.add(mntmNewMenuItem_8);

		JMenuItem mntmNewMenuItem_1 = new JMenuItem("Quit");
		mntmNewMenuItem_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				storage.killall();
				System.exit(0);
			}
		});
		mnNewMenu.add(mntmNewMenuItem_1);

		JMenu mnNewMenu_1 = new JMenu("Bot");
		menuBar.add(mnNewMenu_1);

		JMenuItem mntmNewMenuItem_2 = new JMenuItem("Connect");
		mntmNewMenuItem_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mcbot bot = storage.getcurrentselectedbot();
				if (bot == null) {
					bot = storage.getspecialbot();
					if (bot != null) {
						bot.connect();
					}
				}
				bot.connect();
			}
		});
		mnNewMenu_1.add(mntmNewMenuItem_2);

		JMenuItem mntmNewMenuItem_3 = new JMenuItem("Disconnect");
		mntmNewMenuItem_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String botname = storage.getselectedtabtitle();
				mcbot bot = storage.getInstance().settin.bots.get(botname);
				if (bot == null) {
					bot = storage.getspecialbot();
					if (bot != null) {
						bot.specialdisconnect();
					}
				} else {
					bot.disconnect();
				}
			}
		});
		mnNewMenu_1.add(mntmNewMenuItem_3);

		JMenuItem mntmNewMenuItem_10 = new JMenuItem("Reconnect");
		mntmNewMenuItem_10.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mcbot bot = storage.getcurrentselectedbot();
				bot.softReconnect();
			}
		});
		mnNewMenu_1.add(mntmNewMenuItem_10);

		JMenuItem mntmNewMenuItem_4 = new JMenuItem("Settings");
		mntmNewMenuItem_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				storage.opensettingswindow();
			}
		});
		
		JMenuItem mntmCopyServerInfo = new JMenuItem("Copy Server info");
		mntmCopyServerInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String str=storage.getcurrentselectedbot().serverip+":"+storage.getcurrentselectedbot().serverport;
				storage.setClipboard(str);
			}
		});
		mnNewMenu_1.add(mntmCopyServerInfo);
		mnNewMenu_1.add(mntmNewMenuItem_4);

		JMenu mnNewMenu_2 = new JMenu("Help");
		menuBar.add(mnNewMenu_2);

		JMenuItem mntmNewMenuItem_5 = new JMenuItem("Website");
		mntmNewMenuItem_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				storage.openweb(storage.homepage);
			}
		});
		mnNewMenu_2.add(mntmNewMenuItem_5);

		JMenuItem mntmNewMenuItem_6 = new JMenuItem("Check for updates");
		mntmNewMenuItem_6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				storage.checkforupdates();
			}
		});
		mnNewMenu_2.add(mntmNewMenuItem_6);

		JMenuItem mntmNewMenuItem_7 = new JMenuItem("About");
		mntmNewMenuItem_7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				storage.openaboutwin();
			}
		});
		mnNewMenu_2.add(mntmNewMenuItem_7);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				storage.changemenuitems();
			}
		});
		frmReticle.getContentPane().add(tabbedPane, BorderLayout.CENTER);

		frmReticle.setIconImage(storage.winicon.getImage());
		// TODO: Stream redirection
		redirectSystemStreams();

		storage.getInstance().tabbedPane = tabbedPane;
		storage.getInstance().menu_con = mntmNewMenuItem_2;
		storage.getInstance().menu_dis = mntmNewMenuItem_3;
		storage.getInstance().menu_set = mntmNewMenuItem_4;
		storage.getInstance().menu_rec = mntmNewMenuItem_10;
		storage.getInstance().menu_info = mntmCopyServerInfo;
		

		storage.loadsettings();
		mcbot main = new mcbot(new botsettings("Main"), true, false, true, false, Color.BLUE, Color.WHITE);

		storage.getInstance().mainer = main;
		if (storage.getSupportEnabled()) {
			mcbot support = new mcbot(new botsettings("Support"), true, true, false, true, Color.getColor(null, 0xF7FE2E), Color.BLACK);
			storage.getInstance().support = support;
			support.connect();
		}
		storage.changemenuitems();
		storage.savesettings();
		// storage.conlog("§6Current version is still very bugy so §l§nplease§r§6 check for updates frequently.");
		storage.conlog("§fRunning §2§nReticle§r §fversion §4§n" + storage.version);
	}

	protected void redirectSystemStreams() {
		OutputStream out = new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				storage.conlog(String.valueOf((char) b));
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {

				storage.conlog(new String(b, off, len));
			}

			@Override
			public void write(byte[] b) throws IOException {
				write(b, 0, b.length);
			}
		};
		System.setOut(new PrintStream(out, true));
		System.setErr(new PrintStream(out, true));
	}

}
