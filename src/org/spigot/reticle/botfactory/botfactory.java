package org.spigot.reticle.botfactory;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

import org.spigot.reticle.storage;

public class botfactory {

	private static JTextPane txtpnText;

	public static void makenewtab(final mcbot bot) {

		JPanel panel = new JPanel();
		panel.setBackground(bot.backgroundcolor);
		panel.setForeground(bot.foregroundcolor);
		JTabbedPane tabbedPane = storage.getInstance().tabbedPane;

		tabbedPane.addTab(bot.gettabname(), storage.icon_dis, panel, bot.gettabname());

		panel.setLayout(new MigLayout("", "[615px,grow]", "[340px,grow]"));

		JPanel panel_1 = new JPanel();
		panel.add(panel_1, "cell 0 0,grow");
		panel_1.setLayout(new MigLayout("", "[grow]", "[grow][]0"));

		panel_1.setBackground(bot.backgroundcolor);

		JScrollPane scrollPane = new JScrollPane();

		txtpnText = new JTextPane();
		txtpnText.setText("");
		txtpnText.setEditable(false);

		txtpnText.setBackground(bot.backgroundcolor);
		txtpnText.setForeground(bot.foregroundcolor);

		panel_1.add(scrollPane, "cell 0 0,grow");
		scrollPane.add(txtpnText);
		scrollPane.setViewportView(txtpnText);

		final JTextField txtPrefix = new JTextField();
		txtPrefix.setText("");
		Dimension pref = new Dimension();
		pref.setSize(70, 20);
		txtPrefix.setPreferredSize(pref);
		txtPrefix.setBackground(bot.backgroundcolor);
		txtPrefix.setForeground(bot.foregroundcolor);

		final JTextField txtCommands = new JTextField();
		txtCommands.setText("");

		txtCommands.setColumns(10);

		final JTextField txtSuffix = new JTextField();
		txtSuffix.setText("");
		txtSuffix.setPreferredSize(pref);
		txtSuffix.setBackground(bot.backgroundcolor);
		txtSuffix.setForeground(bot.foregroundcolor);

		JCheckBox autostroll = new JCheckBox();
		autostroll.setBackground(txtpnText.getBackground());
		autostroll.setSelected(true);

		JLabel messagecount = new JLabel();
		messagecount.setText("0");

		JButton btnNewButton = new JButton("Send");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (storage.sendmessagetoactivebot(txtPrefix.getText() + txtCommands.getText() + txtSuffix.getText())) {
					txtCommands.setText("");
				}
			}
		});

		PopClickListener listener = new PopClickListener();
		listener.main = bot.allowreport;
		txtpnText.addMouseListener(listener);

		KeyAdapter scom = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == '\n') {
					if (bot.sendtoserver(txtPrefix.getText() + txtCommands.getText() + txtSuffix.getText())) {
						txtCommands.setText("");
						bot.setMessageCount(0,true);
					}
				} else {
					String total = txtPrefix.getText() + txtCommands.getText() + txtSuffix.getText();
					int len=total.length()/100+1;
					if (total.startsWith("/") && len>1) {
						bot.setMessageCount(len,false);
					} else {
						bot.setMessageCount(len,true);
					}
				}
			}
		};

		txtCommands.addKeyListener(scom);
		txtPrefix.addKeyListener(scom);
		txtSuffix.addKeyListener(scom);

		if (bot.tablistdisplayed) {
			panel_1.add(txtPrefix, "flowx,cell 0 1");
			panel_1.add(txtCommands, "flowx,cell 0 1,growx");
			panel_1.add(txtSuffix, "flowx,cell 0 1");
			panel_1.add(autostroll, "cell 0 1");
			panel_1.add(messagecount, "cell 0 1");
			panel_1.add(btnNewButton, "cell 0 1");
			txtCommands.setBackground(bot.backgroundcolor);
			txtCommands.setForeground(bot.foregroundcolor);
			JTable table = new JTable();
			table.setModel(new DefaultTableModel(new Object[0][0], new Object[0]));
			table.setBackground(bot.backgroundcolor);
			table.setForeground(bot.foregroundcolor);
			table.setEnabled(false);
			JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panel_1, table);
			splitPane.setDividerSize(5);
			panel.add(splitPane, "cell 0 0,grow");
			bot.setconfig(txtpnText, table, panel_1, autostroll, messagecount);
			splitPane.setDividerLocation(0.9);
			splitPane.setResizeWeight(0.7);
		} else {
			panel.add(panel_1, "cell 0 0,grow");
			panel_1.add(txtCommands, "flowx,cell 0 1,growx");
			panel_1.add(btnNewButton, "cell 0 1");
			bot.setconfig(txtpnText, null, panel_1, autostroll, messagecount);
		}
	}
}

class contextmenu extends JPopupMenu {
	private static final long serialVersionUID = 1L;
	JMenuItem anItem;

	public contextmenu(final JTextPane txt, boolean main) {

		ActionListener menuListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (event.getActionCommand().equals("Select all")) {
					txt.requestFocus();
					txt.setSelectionStart(0);
					txt.setSelectionEnd(txt.getText().length());
				} else if (event.getActionCommand().equals("Copy")) {
					String text = txt.getSelectedText();
					StringSelection stringSelection = new StringSelection(text);
					Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
					clpbrd.setContents(stringSelection, null);
				} else if (event.getActionCommand().equals("Clear")) {
					txt.setText("");
				} else if (event.getActionCommand().equals("Report")) {
					storage.sendissue();
					storage.conlog("Reporting");
				}
			}
		};
		JMenuItem item1 = new JMenuItem("Select all");
		JMenuItem item2 = new JMenuItem("Copy");
		JMenuItem item3 = new JMenuItem("Clear");
		item1.addActionListener(menuListener);
		item2.addActionListener(menuListener);
		item3.addActionListener(menuListener);
		add(item1);
		add(item2);
		add(item3);
		if (main) {
			JMenuItem item4 = new JMenuItem("Report");
			item4.addActionListener(menuListener);
			add(item4);
		}

	}
}

class PopClickListener extends MouseAdapter {
	protected boolean main;

	public void mousePressed(MouseEvent e) {
		if (e.isPopupTrigger()) {
			doPop(e);
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger()) {
			doPop(e);
		}
	}

	private void doPop(MouseEvent e) {
		contextmenu menu = new contextmenu((JTextPane) e.getComponent(), main);
		menu.show(e.getComponent(), e.getX(), e.getY());
	}
}