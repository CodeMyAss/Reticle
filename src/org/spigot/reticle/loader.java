package org.spigot.reticle;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class loader extends JFrame implements Runnable {
	protected loader() {
	}
	private static final long serialVersionUID = 1L;

	protected JFrame frame;
	
	protected static void centre(JFrame frame) {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
		frame.setLocation(x, y);
	}

	@Override
	public void run() {
		frame = new JFrame();
		frame.setUndecorated(true);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Reticle loader");
		ImagePanel panel = new ImagePanel(storage.icon_loader.getImage());
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		frame.setSize(storage.icon_loader.getIconWidth(), storage.icon_loader.getIconHeight());
		centre(frame);
		frame.setIconImage(storage.winicon.getImage());
		frame.setVisible(true);
	}
}

class ImagePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private Image img;

	protected ImagePanel(String img) {
		this(new ImageIcon(img).getImage());
	}

	protected ImagePanel(Image img) {
		this.img = img;
		Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setSize(size);
		setLayout(null);
	}

	protected void paintComponent(Graphics g) {
		g.drawImage(img, 0, 0, null);
	}

}
