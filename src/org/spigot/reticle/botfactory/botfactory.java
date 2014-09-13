package org.spigot.reticle.botfactory;

import java.awt.Color;
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

	public static void makenewtab(mcbot bot) {

		JPanel panel = new JPanel();
		panel.setBackground(Color.BLACK);
		panel.setForeground(Color.BLUE);
		JTabbedPane tabbedPane = storage.getInstance().tabbedPane;

		tabbedPane.addTab(bot.gettabname(), storage.icon_dis, panel, bot.gettabname());

		panel.setLayout(new MigLayout("", "[615px,grow]", "[340px,grow]"));

		JPanel panel_1 = new JPanel();
		panel.add(panel_1, "cell 0 0,grow");
		panel_1.setLayout(new MigLayout("", "[grow]", "[grow][]0"));

		if (bot.ismain) {
			panel_1.setBackground(Color.BLUE);
		} else {

			panel_1.setBackground(Color.BLACK);
		}
		JScrollPane scrollPane = new JScrollPane();

		txtpnText = new JTextPane();
		txtpnText.setText("");
		if (bot.ismain) {
			txtpnText.setBackground(Color.BLUE);
			txtpnText.setForeground(Color.WHITE);
		} else {
			txtpnText.setBackground(Color.BLACK);
			txtpnText.setForeground(Color.WHITE);
		}
		panel_1.add(scrollPane, "cell 0 0,grow");
		scrollPane.add(txtpnText);
		scrollPane.setViewportView(txtpnText);

		final JTextField txtPrefix = new JTextField();
		txtPrefix.setText("");
		Dimension pref = new Dimension();
		pref.setSize(70, 20);
		txtPrefix.setPreferredSize(pref);
		txtPrefix.setBackground(Color.BLACK);
		txtPrefix.setForeground(Color.WHITE);

		

		final JTextField txtCommands = new JTextField();
		txtCommands.setText("");
		
		txtCommands.setColumns(10);

		final JTextField txtSuffix = new JTextField();
		txtSuffix.setText("");
		txtSuffix.setPreferredSize(pref);
		txtSuffix.setBackground(Color.BLACK);
		txtSuffix.setForeground(Color.WHITE);



		JCheckBox autostroll = new JCheckBox();
		autostroll.setBackground(txtpnText.getBackground());
		autostroll.setSelected(true);
		


		JButton btnNewButton = new JButton("Send");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (storage.sendmessagetoactivebot(txtPrefix.getText() + txtCommands.getText() + txtSuffix.getText())) {
					txtCommands.setText("");
				}
			}
		});

		PopClickListener listener = new PopClickListener();
		listener.main = bot.ismain;
		txtpnText.addMouseListener(listener);

		KeyAdapter scom=new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == '\n') {
					if (storage.sendmessagetoactivebot(txtPrefix.getText() + txtCommands.getText() + txtSuffix.getText())) {
						txtCommands.setText("");
					}
				}
			}
		};
		
		txtCommands.addKeyListener(scom);
		txtPrefix.addKeyListener(scom);
		txtSuffix.addKeyListener(scom);
		


		if (bot.ismain) {
			panel.add(panel_1, "cell 0 0,grow");
			panel_1.add(txtCommands, "flowx,cell 0 1,growx");
			panel_1.add(btnNewButton, "cell 0 1");
			bot.setconfig(txtpnText, null, panel_1, autostroll);
		} else {
			panel_1.add(txtPrefix, "flowx,cell 0 1");
			panel_1.add(txtCommands, "flowx,cell 0 1,growx");
			panel_1.add(txtSuffix, "flowx,cell 0 1");
			panel_1.add(autostroll, "cell 0 1");
			panel_1.add(btnNewButton, "cell 0 1");
			txtCommands.setBackground(Color.BLACK);
			txtCommands.setForeground(Color.WHITE);
			JTable table = new JTable();
			table.setModel(new DefaultTableModel(new Object[0][0], new Object[0]));
			table.setBackground(Color.BLACK);
			table.setForeground(Color.WHITE);
			table.setEnabled(false);
			JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panel_1, table);
			splitPane.setDividerSize(5);
			panel.add(splitPane, "cell 0 0,grow");
			bot.setconfig(txtpnText, table, panel_1, autostroll);
			splitPane.setDividerLocation(0.9);
			splitPane.setResizeWeight(0.7);
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