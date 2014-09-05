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
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmReticle = new JFrame();
		frmReticle.setFont(new Font("Bodoni MT", Font.PLAIN, 12));
		frmReticle.setForeground(Color.BLACK);
		frmReticle.setTitle("Reticle");
		frmReticle.setBounds(100, 100, 642, 438);
		frmReticle.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		frmReticle.setJMenuBar(menuBar);

		JMenu mnNewMenu = new JMenu("Main");
		menuBar.add(mnNewMenu);

		JMenuItem mntmNewMenuItem = new JMenuItem("Options");
		mnNewMenu.add(mntmNewMenuItem);
		
		JMenuItem mntmNewMenuItem_8 = new JMenuItem("Add bot");
		mntmNewMenuItem_8.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(storage.settingwindowopened()) {
					storage.alert("Error","Please close settings dialog before creating new bot.");
				} else {
					storage.addbot();
				}
			}
		});
		mnNewMenu.add(mntmNewMenuItem_8);

		JMenuItem mntmNewMenuItem_1 = new JMenuItem("Quit");
		mnNewMenu.add(mntmNewMenuItem_1);

		JMenu mnNewMenu_1 = new JMenu("Bot");
		menuBar.add(mnNewMenu_1);

		JMenuItem mntmNewMenuItem_2 = new JMenuItem("Connect");
		mntmNewMenuItem_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mcbot bot=storage.getcurrentselectedbot();
				bot.connect();
			}
		});
		mnNewMenu_1.add(mntmNewMenuItem_2);

		JMenuItem mntmNewMenuItem_3 = new JMenuItem("Disconnect");
		mntmNewMenuItem_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String botname=storage.getselectedtabtitle();
				mcbot bot=storage.getInstance().settin.bots.get(botname);
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
		mnNewMenu_2.add(mntmNewMenuItem_5);

		JMenuItem mntmNewMenuItem_6 = new JMenuItem("Check for updates");
		mnNewMenu_2.add(mntmNewMenuItem_6);

		JMenuItem mntmNewMenuItem_7 = new JMenuItem("About");
		mnNewMenu_2.add(mntmNewMenuItem_7);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				storage.changemenuitems();
			}
		});
		frmReticle.getContentPane().add(tabbedPane, BorderLayout.CENTER);

		storage.getInstance().tabbedPane = tabbedPane;
		/*
		 * 
		 * JPanel panel = new JPanel(); tabbedPane.addTab("New tab", null,
		 * panel, null); panel.setLayout(new MigLayout("", "[615px,grow]",
		 * "[344px,grow]"));
		 * 
		 * JPanel panel_1 = new JPanel(); panel.add(panel_1, "cell 0 0,grow");
		 * panel_1.setLayout(new MigLayout("", "[grow]", "[grow][]"));
		 * 
		 * JScrollPane scrollPane = new JScrollPane(); panel_1.add(scrollPane,
		 * "cell 0 0,grow");
		 * 
		 * JTextPane txtpnText = new JTextPane(); txtpnText.setText("TExt");
		 * scrollPane.setViewportView(txtpnText);
		 * 
		 * txtCommands = new JTextField(); txtCommands.setText("Commands");
		 * panel_1.add(txtCommands, "flowx,cell 0 1,growx");
		 * txtCommands.setColumns(10);
		 * 
		 * JButton btnNewButton = new JButton("Odeslat");
		 * panel_1.add(btnNewButton, "cell 0 1");
		 * 
		 * table = new JTable();
		 * 
		 * JSplitPane splitPane = new
		 * JSplitPane(JSplitPane.HORIZONTAL_SPLIT,panel_1, table);
		 * panel.add(splitPane, "cell 0 0,grow");
		 */
		
		storage.getInstance().settings=new settings();
		
		storage.getInstance().menu_con=mntmNewMenuItem_2;
		storage.getInstance().menu_dis=mntmNewMenuItem_3;
		storage.getInstance().menu_set=mntmNewMenuItem_4;
		
		storage.loadsettings();
		new mcbot(new botsettings("Main"),true);
		
		storage.changemenuitems();
		
		storage.savesettings();
		storage.firsttabload();
	}

}
