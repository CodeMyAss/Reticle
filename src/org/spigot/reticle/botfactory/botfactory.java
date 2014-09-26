package org.spigot.reticle.botfactory;

import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;

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
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import net.miginfocom.swing.MigLayout;

import org.spigot.reticle.storage;

public class botfactory {

	private static JTextPane txtpnText;

	protected static void makenewtab(final mcbot bot) {

		JPanel panel = new JPanel();
		panel.setBackground(bot.backgroundcolor);
		panel.setForeground(bot.foregroundcolor);
		JTabbedPane tabbedPane = storage.getInstance().tabbedPane;

		tabbedPane.addTab(bot.gettabname(), storage.icon_dis, panel, bot.gettabname());

		panel.setLayout(new MigLayout("", "[615px,grow]", "[340px,grow]"));

		JPanel panel_1 = new JPanel();
		// panel.add(panel_1, "cell 0 0,grow");
		panel_1.setLayout(new MigLayout("", "[grow]", "[grow][]0"));

		JPanel panel_2 = new JPanel();
		// panel.add(panel_1, "cell 0 0,grow");
		panel_2.setLayout(new MigLayout("", "[grow]", "[grow][]0"));

		panel_1.setBackground(bot.backgroundcolor);

		panel_2.setBackground(bot.backgroundcolor);

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
		messagecount.setText("1");

		JButton btnNewButton = new JButton("Send");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (bot.sendtoserver(txtPrefix.getText() + txtCommands.getText() + txtSuffix.getText())) {
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
				if (e.getKeyCode() == KeyEvent.VK_UP) {
					bot.arrowuppressed();
				}
				if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					bot.arrowdownpressed();
				}
				if (e.getKeyCode() == KeyEvent.VK_TAB) {
					e.consume();
					bot.tabpressed((JTextField) e.getComponent(), ((JTextField) e.getComponent()).getText());
				} else {
					if (bot.connector != null) {
						bot.connector.unlocktabpress();
					}
				}
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (bot.sendtoserver(txtPrefix.getText() + txtCommands.getText() + txtSuffix.getText())) {
						txtCommands.setText("");
						bot.setMessageCount(0, true);
					}
				} else {
					String total = txtPrefix.getText() + txtCommands.getText() + txtSuffix.getText();
					int len = total.length() / 100 + 1;
					if (total.startsWith("/") && len > 1) {
						bot.setMessageCount(len, false);
					} else {
						bot.setMessageCount(len, true);
					}
				}
			}
		};

		txtCommands.addKeyListener(scom);
		txtPrefix.addKeyListener(scom);
		txtSuffix.addKeyListener(scom);

		txtCommands.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.<AWTKeyStroke> emptySet());
		txtPrefix.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.<AWTKeyStroke> emptySet());
		txtSuffix.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.<AWTKeyStroke> emptySet());

		if (!bot.ismain) {
			AbstractDocument doc = (AbstractDocument) txtpnText.getStyledDocument();
			doc.setDocumentFilter(new MaxLenFilter(txtpnText, bot.getChatFilterLength()));
		}

		if (bot.tablistdisplayed) {
			Color color = UIManager.getColor("Table.gridColor");
			MatteBorder border = new MatteBorder(1, 1, 1, 1, color);
			DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
			rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
			panel_1.add(txtPrefix, "flowx,cell 0 1");
			panel_1.add(txtCommands, "flowx,cell 0 1,growx");
			panel_1.add(txtSuffix, "flowx,cell 0 1");
			panel_1.add(autostroll, "cell 0 1");
			panel_1.add(messagecount, "cell 0 1");
			panel_1.add(btnNewButton, "cell 0 1");
			txtCommands.setBackground(bot.backgroundcolor);
			txtCommands.setForeground(bot.foregroundcolor);
			JTable tableinfo = new JTable();
			tableinfo.setModel(new DefaultTableModel(new Object[][] { { "Health:", "", "X:" }, { "Food:", "", "Y:" }, { "Saturation:", "", "Z:" } }, new Object[4]));
			tableinfo.getColumnModel().getColumn(0).setCellRenderer(rightRenderer);
			tableinfo.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
			tableinfo.setBackground(bot.backgroundcolor);
			tableinfo.setForeground(bot.foregroundcolor);
			tableinfo.setVisible(false);
			JTable table = new JTable();
			table.setModel(new DefaultTableModel(new Object[0][0], new Object[0]));
			table.setBackground(bot.backgroundcolor);
			table.setForeground(bot.foregroundcolor);
			table.setEnabled(false);
			tableinfo.setEnabled(false);
			tableinfo.setBorder(border);
			table.setBorder(border);
			panel_2.add(table, "cell 0 0, grow");
			panel_2.add(tableinfo, "cell 0 1, growx");
			JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panel_1, panel_2);
			splitPane.setDividerSize(5);
			panel.add(splitPane, "cell 0 0,grow");
			bot.setconfig(txtpnText, table, panel_1, autostroll, messagecount, tableinfo, txtCommands);
			splitPane.setDividerLocation(0.9);
			splitPane.setResizeWeight(0.7);
		} else {
			panel.add(panel_1, "cell 0 0,grow");
			panel_1.add(txtCommands, "flowx,cell 0 1,growx");
			panel_1.add(btnNewButton, "cell 0 1");
			bot.setconfig(txtpnText, null, panel_1, autostroll, messagecount, null, null);
		}
	}
}

class contextmenu extends JPopupMenu {
	private static final long serialVersionUID = 1L;
	JMenuItem anItem;

	protected contextmenu(final JTextPane txt, boolean main) {

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
				} else if (event.getActionCommand().equals("Add to ignore list")) {
					String text = txt.getSelectedText();
					storage.addtoignoreforcurrentbot(text);
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
		} else {
			JMenuItem item4 = new JMenuItem("Add to ignore list");
			item4.addActionListener(menuListener);
			add(item4);
		}
	}
}

class MaxLenFilter extends DocumentFilter {

	private JTextPane area;
	private int max;

	protected MaxLenFilter(JTextPane area, int max) {
		this.area = area;
		this.max = max;
	}

	@Override
	public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
		super.insertString(fb, offset, string, attr);
		int lines = area.getText().split("\\n").length;
		if (lines > max) {
			int linesToRemove = lines - max - 1;
			int lengthToRemove = area.getText().indexOf("\n", linesToRemove);
			remove(fb, 0, lengthToRemove);
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