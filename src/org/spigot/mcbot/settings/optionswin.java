package org.spigot.mcbot.settings;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import javax.swing.JLabel;
import javax.swing.JCheckBox;

import org.spigot.mcbot.storage;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.HashMap;

public class optionswin extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JCheckBox chckbxNewCheckBox;
	private JCheckBox checkBox_1;
	private JCheckBox checkBox;

	/**
	 * Launch the application.
	 */
	/*
	public static void main(String[] args) {
		try {
			optionswin dialog = new optionswin();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
*/
	/**
	 * Create the dialog.
	 */
	public optionswin() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Options");
		setBounds(100, 100, 310, 168);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[][]", "[][][]"));

		JLabel lblNewLabel = new JLabel("Check for updates automatically after start:");
		contentPanel.add(lblNewLabel, "cell 0 0,alignx right");
		chckbxNewCheckBox = new JCheckBox("");
		chckbxNewCheckBox.setSelected(true);
		contentPanel.add(chckbxNewCheckBox, "cell 1 0");

		JLabel lblNewLabel_1 = new JLabel("Send usage and error reports automatically:");
		contentPanel.add(lblNewLabel_1, "cell 0 1,alignx right");

		checkBox_1 = new JCheckBox("");
		checkBox_1.setSelected(true);
		contentPanel.add(checkBox_1, "cell 1 1");

		JLabel lblNewLabel_2 = new JLabel("Load plugins automatically:");
		contentPanel.add(lblNewLabel_2, "cell 0 2,alignx right");

		checkBox = new JCheckBox("");
		contentPanel.add(checkBox, "cell 1 2");

		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				HashMap<String, String> set = new HashMap<String, String>();
				set.put("autoupdate", chckbxNewCheckBox.isSelected() + "");
				set.put("autosenddebug", checkBox_1.isSelected() + "");
				set.put("loadplugins", checkBox.isSelected() + "");
				storage.setglobalsettings(set);
				storage.savesettings();
				storage.closeoptionswin();
			}
		});
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				storage.closeoptionswin();
			}
		});
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);
		chckbxNewCheckBox.setSelected(storage.getAutodebug());
		checkBox_1.setSelected(storage.getAutoupdate());
		checkBox.setSelected(storage.getAutoplugin());
	}
}
