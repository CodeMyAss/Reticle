package org.spigot.mcbot;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JTabbedPane;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.spigot.mcbot.botfactory.mcbot;
import org.spigot.mcbot.settings.botsettings;
import org.spigot.mcbot.settings.settings;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class mcbotapp {

	private JFrame frmReticle;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					mcbotapp window = new mcbotapp();
					window.frmReticle.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public mcbotapp() {
		initialize(storage.version);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(String version) {
		frmReticle = new JFrame();
		frmReticle.setFont(new Font("Bodoni MT", Font.PLAIN, 12));
		frmReticle.setForeground(Color.BLACK);
		frmReticle.setTitle("Reticle " + version);
		frmReticle.setBounds(100, 100, 642, 438);
		frmReticle.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		frmReticle.setJMenuBar(menuBar);

		JMenu mnNewMenu = new JMenu("Main");
		menuBar.add(mnNewMenu);

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
				bot.connect();
			}
		});
		mnNewMenu_1.add(mntmNewMenuItem_2);

		JMenuItem mntmNewMenuItem_3 = new JMenuItem("Disconnect");
		mntmNewMenuItem_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String botname = storage.getselectedtabtitle();
				mcbot bot = storage.getInstance().settin.bots.get(botname);
				bot.disconnect();
			}
		});
		mnNewMenu_1.add(mntmNewMenuItem_3);

		JMenuItem mntmNewMenuItem_4 = new JMenuItem("Settings");
		mntmNewMenuItem_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				storage.opensettingswindow();
			}
		});
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

		//redirectSystemStreams();
		
		storage.getInstance().tabbedPane = tabbedPane;
		storage.getInstance().settings = new settings();
		storage.getInstance().menu_con = mntmNewMenuItem_2;
		storage.getInstance().menu_dis = mntmNewMenuItem_3;
		storage.getInstance().menu_set = mntmNewMenuItem_4;


		storage.loadsettings();
		mcbot main = new mcbot(new botsettings("Main"), true);
		storage.getInstance().mainer = main;
		storage.changemenuitems();
		storage.savesettings();
		storage.firsttabload();

	}

	public void redirectSystemStreams() {
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
