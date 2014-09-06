package org.spigot.mcbot.settings;

import java.awt.BorderLayout;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import javax.swing.JLabel;

import org.spigot.mcbot.storage;

import java.awt.Font;
import java.awt.Color;

public class aboutwin extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			aboutwin dialog = new aboutwin();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public aboutwin() {
		setTitle("About");
		setBounds(100, 100, 268, 151);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblReticle = new JLabel("Reticle");
			lblReticle.setBounds(62, 11, 163, 66);
			lblReticle.setForeground(Color.BLUE);
			lblReticle.setFont(new Font("Tempus Sans ITC", Font.BOLD, 50));
			contentPanel.add(lblReticle);
		}
		{
			JPanel panel = new JPanel();
			panel.setBounds(12, 82, 213, 27);
			contentPanel.add(panel);
			panel.setLayout(new MigLayout("", "[64px][30px]", "[23px]"));
			{
				JLabel lblNewLabel = new JLabel("Version:");
				lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
				panel.add(lblNewLabel, "cell 0 0,alignx right,aligny top");
			}
			{
				JLabel label = new JLabel(storage.version);
				label.setFont(new Font("Swis721 Blk BT", Font.PLAIN, 18));
				panel.add(label, "cell 1 0,alignx left,aligny top");
			}
		}
		
		JLabel lblEncorns = new JLabel("Encorn's");
		lblEncorns.setFont(new Font("X-Files", Font.PLAIN, 18));
		lblEncorns.setForeground(Color.RED);
		lblEncorns.setBounds(12, 0, 88, 31);
		contentPanel.add(lblEncorns);
	}
}