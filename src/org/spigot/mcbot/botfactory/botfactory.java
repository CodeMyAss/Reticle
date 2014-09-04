package org.spigot.mcbot.botfactory;

import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

import org.spigot.mcbot.storage;

public class botfactory {

	public static void makenewtab(mcbot bot) {
		/*
		 * JPanel panel = new JPanel(); JTabbedPane tabbedPane =
		 * storage.gettabbedpane();
		 * 
		 * 
		 * table.setModel(new DefaultTableModel( new Object[][] { {null, null,
		 * null}, {null, null, null}, {null, null, null}, {null, null, null},
		 * {null, null, null}, {null, null, null}, {null, null, null}, {null,
		 * null, null}, {null, null, null}, {null, null, null}, {null, null,
		 * null}, {null, null, null}, {null, null, null}, {null, null, null},
		 * {null, null, null}, {null, null, null}, {null, null, null}, {null,
		 * null, null}, {null, null, null}, {null, null, null}, }, new String[]
		 * { "New column", "New column", "New column" } ));
		 * table.setBackground(Color.BLACK); table.setForeground(Color.WHITE);
		 * panel.add(table, "cell 2 1 1 4,grow");
		 */
		
		//storage.getInstance().settin.settings.put(har, bot.)

		JPanel panel = new JPanel();
		panel.setBackground(Color.BLACK);
		panel.setForeground(Color.BLUE);
		JTabbedPane tabbedPane = storage.getInstance().tabbedPane;
		
		tabbedPane.addTab(bot.gettabname(), storage.icon_dis, panel, bot.gettabname());
		
		panel.setLayout(new MigLayout("", "[615px,grow]", "[34px]"));

		JPanel panel_1 = new JPanel();
		panel.add(panel_1, "cell 0 0,grow");
		panel_1.setLayout(new MigLayout("", "[grow]", "[grow][]"));

		panel_1.setBackground(Color.BLACK);
		
		JScrollPane scrollPane = new JScrollPane();
		panel_1.add(scrollPane, "cell 0 0,grow");

		JTextPane txtpnText = new JTextPane();
		txtpnText.setText("");
		txtpnText.setBackground(Color.BLACK);
		txtpnText.setForeground(Color.WHITE);
		
		scrollPane.setViewportView(txtpnText);

		JTextField txtCommands = new JTextField();
		txtCommands.setText("Commands");
		panel_1.add(txtCommands, "flowx,cell 0 1,growx");
		txtCommands.setColumns(10);

		JButton btnNewButton = new JButton("Odeslat");
		panel_1.add(btnNewButton, "cell 0 1");

		JTable table = new JTable();
		table.setModel(new DefaultTableModel(new Object[][] { { null, null, null }, { null, null, null }, { null, null, null }, { null, null, null }, { null, null, null }, { null, null, null }, { null, null, null }, { null, null, null }, { null, null, null }, { null, null, null }, { null, null, null }, { null, null, null }, { null, null, null }, { null, null, null }, { null, null, null }, { null, null, null }, { null, null, null }, { null, null, null }, { null, null, null }, { null, null, null }, }, new String[] { "New column", "New column", "New column" }));
		table.setBackground(Color.BLACK);
		table.setForeground(Color.WHITE);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panel_1, table);
		splitPane.setDividerSize(5);
		splitPane.setDividerLocation(500);
		panel.add(splitPane, "cell 0 0,grow");
		bot.setconfig(txtpnText, table, panel_1);
	}

}
