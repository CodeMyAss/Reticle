package org.spigot.reticle.botfactory;

import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.DefaultSingleSelectionModel;
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
import org.spigot.reticle.API.ContextMenuItem;
import org.spigot.reticle.botfactory.selector.tabselector;
import org.spigot.reticle.events.ContextReceiveEvent;

public class botfactory {

	private static JTextPane txtpnText;

	protected static void makenewtab(final mcbot bot) {

		JPanel panel = new JPanel();
		panel.setBackground(bot.backgroundcolor);
		panel.setForeground(bot.foregroundcolor);
		final JTabbedPane tabbedPane = storage.getInstance().tabbedPane;

		tabselector sel = storage.sel.sel;

		botpopup botpop = new botpopup(bot, sel);
		tabbedPane.addMouseListener(botpop);

		tabselectorlistener resel = new tabselectorlistener(sel);
		tabbedPane.setModel(resel);

		tabbedPane.addTab(bot.gettabname(), storage.icon_dis, panel, bot.gettabname());
		// storage.reFreshTabs();
		tabbedPane.setSelectedIndex(sel.index);

		panel.setLayout(new MigLayout("", "[615px,grow]", "[340px,grow]"));

		JPanel panel_1 = new JPanel();
		panel_1.setLayout(new MigLayout("", "[grow]", "[grow][]0"));

		JPanel panel_2 = new JPanel();
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
		/*
		 * txtPrefix.setFont(bot.getFont()); txtSuffix.setFont(bot.getFont());
		 * txtCommands.setFont(bot.getFont());
		 * messagecount.setFont(bot.getFont());
		 */
		JButton btnNewButton = new JButton("Send");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (bot.sendtoserver(txtPrefix.getText() + txtCommands.getText() + txtSuffix.getText(), false)) {
					txtCommands.setText("");
				}
			}
		});

		chatlogpopup listener = new chatlogpopup(bot);
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
					if (bot.sendtoserver((txtPrefix.getText() + txtCommands.getText() + txtSuffix.getText()), false)) {
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

		txtpnText.setFont(bot.getFont());

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
			table.setFont(bot.getFont());
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
			// Tablist popup menu

			tablepopup adapter = new tablepopup(bot);

			table.addMouseListener(adapter);
			/*
			 * JPopupMenu popupMenu = new JPopupMenu(); JMenuItem deleteItem =
			 * new JMenuItem("Delete"); deleteItem.addActionListener(new
			 * ActionListener() {
			 * 
			 * @Override public void actionPerformed(ActionEvent e) { Component
			 * c = (Component) e.getSource(); JPopupMenu popup = (JPopupMenu)
			 * c.getParent(); JTable table = (JTable) popup.getInvoker();
			 * MouseListener[] list = table.getMouseListeners(); tablepopup
			 * adaap = null; for (MouseListener tmp : list) { if (tmp instanceof
			 * tablepopup) { adaap = (tablepopup) tmp; } } String text =
			 * (String) table.getValueAt(adaap.row, adaap.column);
			 * bot.contextattable(text); } }); popupMenu.add(deleteItem);
			 * table.setComponentPopupMenu(popupMenu);
			 */
		} else {
			panel.add(panel_1, "cell 0 0,grow");
			panel_1.add(txtCommands, "flowx,cell 0 1,growx");
			panel_1.add(btnNewButton, "cell 0 1");
			bot.setconfig(txtpnText, null, panel_1, autostroll, messagecount, null, txtCommands);
		}
	}
}

class tablepopup extends MouseAdapter {
	public int row, column;
	public final mcbot bot;

	public tablepopup(mcbot bot) {
		this.bot = bot;
	}

	public void mouseReleased(MouseEvent e) {
		JTable source = (JTable) e.getSource();
		row = source.rowAtPoint(e.getPoint());
		column = source.columnAtPoint(e.getPoint());
		if (e.isPopupTrigger()) {
			pop(e);
		}
	}

	public void mousePressed(MouseEvent e) {
		JTable source = (JTable) e.getSource();
		row = source.rowAtPoint(e.getPoint());
		column = source.columnAtPoint(e.getPoint());
		if (e.isPopupTrigger()) {
			pop(e);
		}
	}

	public void pop(MouseEvent e) {
		JTable txt = (JTable) e.getSource();
		String ts = storage.striphtml(txt.getValueAt(row, column).toString());
		ts = ts.replaceAll("\n", "");
		tablecontextmenu menu = new tablecontextmenu((JTable) e.getComponent(), bot, ts);
		menu.show(e.getComponent(), e.getX(), e.getY());
	}
}

class tablecontextmenu extends JPopupMenu {
	private static final long serialVersionUID = 1L;
	JMenuItem anItem;
	private HashMap<String, ContextMenuItem> methods = new HashMap<String, ContextMenuItem>();

	protected tablecontextmenu(final JTable txt, final mcbot bot, final String str) {
		ActionListener menuListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String selection = event.getActionCommand();
				if (methods.containsKey(selection)) {
					ContextMenuItem m = methods.get(selection);
					if (m.m != null) {
						try {
							ContextReceiveEvent e = new ContextReceiveEvent(bot,selection,str);
							m.m.invoke(m.o, e);
						} catch (Exception e) {
						}
					}
				}
			}
		};
		methods = bot.contextattable(str);
		for (String m : methods.keySet()) {
			JMenuItem item = new JMenuItem(m);
			item.addActionListener(menuListener);
			add(item);
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

class chatlogpopup extends MouseAdapter {
	public String selection;
	public final mcbot bot;

	public chatlogpopup(mcbot bot) {
		this.bot = bot;
	}

	public void mouseReleased(MouseEvent e) {
		JTextPane source = (JTextPane) e.getSource();
		selection = source.getSelectedText();
		if (e.isPopupTrigger()) {
			pop(e);
		}
	}

	public void mousePressed(MouseEvent e) {
		JTextPane source = (JTextPane) e.getSource();
		selection = source.getSelectedText();
		if (e.isPopupTrigger()) {
			pop(e);
		}
	}

	public void pop(MouseEvent e) {
		chatlogcontextmenu menu = new chatlogcontextmenu((JTextPane) e.getComponent(), bot, selection);
		menu.show(e.getComponent(), e.getX(), e.getY());
	}

}

class chatlogcontextmenu extends JPopupMenu {
	private static final long serialVersionUID = 1L;
	JMenuItem anItem;
	private HashMap<String, ContextMenuItem> methods = new HashMap<String, ContextMenuItem>();

	protected chatlogcontextmenu(final JTextPane txt, final mcbot bot, final String str) {
		ActionListener menuListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String selection = event.getActionCommand();
				String str = txt.getSelectedText();
				if (methods.containsKey(selection)) {
					ContextMenuItem m = methods.get(selection);
					if (m.m != null) {
						try {
							ContextReceiveEvent e = new ContextReceiveEvent(bot,selection,str);
							m.m.invoke(m.o, e);
						} catch (Exception e) {
						}
					}
				}
			}
		};
		methods = bot.contextchatlog(str);
		for (String m : methods.keySet()) {
			JMenuItem item = new JMenuItem(m);
			item.addActionListener(menuListener);
			add(item);
		}
	}
}

class botpopup extends MouseAdapter {
	public String selection;
	public final mcbot bot;
	private tabselector sel;

	public botpopup(mcbot bot, tabselector sel) {
		this.bot = bot;
		this.sel = sel;
	}

	public void mouseReleased(MouseEvent e) {
		JTabbedPane source = (JTabbedPane) e.getSource();
		sel.Change(!e.isPopupTrigger());
		sel.setRead(true);
		if (e.isPopupTrigger()) {
			sel.Change(false);
			pop(e);
		} else {
			sel.Change(true);
		}
		source.setSelectedIndex(source.indexAtLocation(e.getX(), e.getY()));
		sel.setRead(false);
	}

	public void mousePressed(MouseEvent e) {
		JTabbedPane source = (JTabbedPane) e.getSource();
		sel.Change(!e.isPopupTrigger());
		sel.setRead(true);
		if (e.isPopupTrigger()) {
			sel.Change(false);
			pop(e);
		} else {
			sel.Change(false);
		}
		source.setSelectedIndex(source.indexAtLocation(e.getX(), e.getY()));
		sel.setRead(false);
	}

	public void pop(MouseEvent e) {
		JTabbedPane source = (JTabbedPane) e.getSource();
		int xo = source.indexAtLocation(e.getX(), e.getY());
		if (xo != -1) {
			selection = source.getTitleAt(xo);
			botcontextmenu menu = new botcontextmenu(bot, selection);
			menu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

}

class botcontextmenu extends JPopupMenu {
	private static final long serialVersionUID = 1L;
	JMenuItem anItem;
	private HashMap<String, ContextMenuItem> methods = new HashMap<String, ContextMenuItem>();

	protected botcontextmenu(final mcbot bot, final String str) {
		ActionListener menuListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String selection = event.getActionCommand();
				if (methods.containsKey(selection)) {
					ContextMenuItem m = methods.get(selection);
					if (m.m != null) {
						try {
							ContextReceiveEvent e = new ContextReceiveEvent(bot,selection,str);
							m.m.invoke(m.o, e);
						} catch (Exception e) {
						}
					}
				}
			}
		};
		methods = bot.botcontextmenu(str);
		for (String m : methods.keySet()) {
			JMenuItem item = new JMenuItem(m);
			item.addActionListener(menuListener);
			add(item);
		}

	}
}

class tabselectorlistener extends DefaultSingleSelectionModel {

	private static final long serialVersionUID = 1L;
	private tabselector sel;

	public tabselectorlistener(tabselector sel) {
		this.sel = sel;
	}

	@Override
	public void setSelectedIndex(int pindex) {
		sel.setPane();
		if (pindex < sel.getTabCount()) {
			if (pindex != -1) {
				if (sel.isReady()) {
					boolean can = sel.canChange();
					if (can) {
						super.setSelectedIndex(pindex);
						sel.index = pindex;
					} else {
						if (sel.index >= sel.getTabCount()) {
							sel.index = pindex;
						}
						super.setSelectedIndex(sel.index);
					}
				} else {
					super.setSelectedIndex(pindex);
				}
			} else {
				if (!(sel.index > 0 && sel.index < sel.getTabCount())) {
					super.setSelectedIndex(0);
				}
			}
		}
	}
}
