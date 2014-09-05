package org.spigot.mcbot.settings;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JTextArea;

import org.spigot.mcbot.storage;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class settings extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textcurtabname;
	private JTextField txtservername;
	private JTextField textserverip;
	private JTextField textserverport;
	private JTextField txtNick;
	private JPanel panel_3;
	private JCheckBox checkactiv;
	private JCheckBox checkdisccom;
	private JCheckBox checkconcom;
	private JCheckBox checkafkcom;
	private JTextArea textlogoutcom;
	private JTextArea textlogincom;
	private JTextArea textafkcom;
	private JCheckBox checkBox;

	public settings thisobj;

	/**
	 * Launch the application.
	 */
	/*
	 * public static void main(String[] args) { EventQueue.invokeLater(new
	 * Runnable() { public void run() { try { settings frame = new settings();
	 * frame.setVisible(true); } catch (Exception e) { e.printStackTrace(); } }
	 * }); }
	 */

	public settings() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					settings frame = new settings(1);
					frame.setVisible(false);
					frame.thisobj = frame;
					/*
					 * frame.addWindowListener(new
					 * java.awt.event.WindowAdapter() {
					 * 
					 * @Override public void
					 * windowClosing(java.awt.event.WindowEvent windowEvent) {
					 * windowEvent.getComponent().setVisible(false); } });
					 */
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public settings(int r) {
		setType(Type.UTILITY);
		setTitle("Settings");
		// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 529, 385);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		contentPane.add(tabbedPane, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		tabbedPane.addTab("General", null, panel, null);
		panel.setLayout(new MigLayout("", "[][][grow][][]", "[][][][][][][][][][][]"));

		JLabel lblNewLabel = new JLabel("Current tab name:");
		panel.add(lblNewLabel, "cell 1 0,alignx trailing");

		textcurtabname = new JTextField();
		textcurtabname.setEditable(false);
		panel.add(textcurtabname, "cell 2 0,growx");
		textcurtabname.setColumns(10);

		JButton btnNewButton = new JButton("Save settings");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				botsettings bs = storage.getInstance().setobj.getsettings();
				String acti = storage.getsettingsobj().textcurtabname.getText();
				// String acti = storage.getselectedtabtitle();
				int anum = storage.getselectedtabindex();
				if (storage.verifysettings(acti, bs)) {
					storage.resetset(bs, acti, anum);
					storage.getsettingwin().setVisible(false);
				}
			}
		});
		panel.add(btnNewButton, "cell 1 2");

		JButton btnNewButton_1 = new JButton("Restore settings");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				botsettings nset = storage.getInstance().settin.settings.get(storage.getselectedtabtitle());
				storage.getInstance().setobj.setsettings(nset);
			}
		});
		panel.add(btnNewButton_1, "cell 2 2");

		JButton btnNewButton_2 = new JButton("Remove this bot");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int n = JOptionPane.showConfirmDialog(contentPane, "Are you sure you want to do this?", "Warning", JOptionPane.YES_NO_OPTION);
				if (n == JOptionPane.YES_OPTION) {
					storage.getsettingwin().setVisible(false);
					storage.removecurrentbot();
				}
			}
		});
		panel.add(btnNewButton_2, "cell 1 10");

		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Server", null, panel_1, null);
		panel_1.setLayout(new MigLayout("", "[][][grow]", "[][][][][][][]"));

		JLabel lblNewLabel_5 = new JLabel("Server name:");
		panel_1.add(lblNewLabel_5, "cell 1 0,alignx trailing");

		txtservername = new JTextField();
		txtservername.setText("Unnamed Server");
		panel_1.add(txtservername, "cell 2 0,growx");
		txtservername.setColumns(10);

		JLabel lblsvrip = new JLabel("Server ip:");
		panel_1.add(lblsvrip, "cell 1 1,alignx trailing");

		textserverip = new JTextField();
		panel_1.add(textserverip, "cell 2 1,growx");
		textserverip.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("Server port:");
		panel_1.add(lblNewLabel_1, "cell 1 2,alignx trailing");

		textserverport = new JTextField();
		textserverport.setText("25565");
		panel_1.add(textserverport, "cell 2 2,growx");
		textserverport.setColumns(10);

		JLabel lblNewLabel_2 = new JLabel("Connect automatically:");
		panel_1.add(lblNewLabel_2, "cell 1 3,alignx right");

		checkBox = new JCheckBox("");
		panel_1.add(checkBox, "cell 2 3");

		JLabel lblNewLabel_3 = new JLabel("Nickname:");
		panel_1.add(lblNewLabel_3, "cell 1 5,alignx trailing");

		txtNick = new JTextField();
		txtNick.setText("Reticle");
		panel_1.add(txtNick, "cell 2 5,growx");
		txtNick.setColumns(10);

		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("Scripting", null, panel_2, null);
		panel_2.setLayout(new MigLayout("", "[][][]", "[][][]"));

		JLabel lblUnderHeavyDevelopment = new JLabel("Under heavy development");
		panel_2.add(lblUnderHeavyDevelopment, "cell 2 2");

		panel_3 = new JPanel();
		tabbedPane.addTab("Behavior", null, panel_3, null);
		panel_3.setLayout(new MigLayout("", "[][][grow]", "[][][][][][][][][][][]"));

		JLabel lblActivateTabOn = new JLabel("Activate tab on new message:");
		panel_3.add(lblActivateTabOn, "cell 1 1,alignx right");

		checkactiv = new JCheckBox("");
		panel_3.add(checkactiv, "cell 2 1");

		JLabel lblUseLogoutCommands = new JLabel("Use logout commands upon disconnect:");
		panel_3.add(lblUseLogoutCommands, "cell 1 2,alignx right");

		checkdisccom = new JCheckBox("");
		panel_3.add(checkdisccom, "cell 2 2");

		JLabel lblSendLoginCommands = new JLabel("Send login commands upon connect:");
		panel_3.add(lblSendLoginCommands, "cell 1 3,alignx right");

		checkconcom = new JCheckBox("");
		panel_3.add(checkconcom, "cell 2 3");

		JLabel lblSendAntiafkCommands = new JLabel("Send anti-afk commands periodically:");
		panel_3.add(lblSendAntiafkCommands, "cell 1 4,alignx right");

		checkafkcom = new JCheckBox("");
		panel_3.add(checkafkcom, "cell 2 4");

		JLabel lblNewLabel_4 = new JLabel("Logout commands:");
		panel_3.add(lblNewLabel_4, "cell 1 7,alignx right,aligny top");

		textlogoutcom = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(textlogoutcom);
		scrollPane.setMinimumSize(new Dimension(21, 50));
		panel_3.add(scrollPane, "cell 2 7,grow");

		JLabel lblNewLabel_6 = new JLabel("Login commands:");
		panel_3.add(lblNewLabel_6, "cell 1 8,alignx trailing,aligny top");

		JScrollPane scrollPane_1 = new JScrollPane((Component) null);
		scrollPane_1.setMinimumSize(new Dimension(21, 50));
		panel_3.add(scrollPane_1, "cell 2 8,grow");

		textlogincom = new JTextArea();
		scrollPane_1.setViewportView(textlogincom);

		JLabel lblNewLabel_7 = new JLabel("Anti-afk commands:");
		panel_3.add(lblNewLabel_7, "cell 1 9,alignx right,aligny top");

		JScrollPane scrollPane_2 = new JScrollPane((Component) null);
		scrollPane_2.setMinimumSize(new Dimension(21, 50));
		panel_3.add(scrollPane_2, "cell 2 9,grow");

		textafkcom = new JTextArea();
		scrollPane_2.setViewportView(textafkcom);
		storage.getInstance().winobj = getFrames()[2];
		if (this.txtservername != null) {
			set_obj_struct sobj = storage.getsettingsobj();
			sobj.txtservername = txtservername;
			sobj.textserverip = textserverip;
			sobj.textserverport = textserverport;
			sobj.checkBox = checkBox;
			sobj.txtNick = txtNick;
			sobj.checkactiv = checkactiv;
			sobj.checkconcom = checkconcom;
			sobj.checkdisccom = checkdisccom;
			sobj.checkafkcom = checkafkcom;
			sobj.textlogincom = textlogincom;
			sobj.textlogoutcom = textlogoutcom;
			sobj.textafkcom = textafkcom;
			sobj.textcurtabname = textcurtabname;
		}
	}

}
