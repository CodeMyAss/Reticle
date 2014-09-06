package org.spigot.mcbot;

public class MAIN {

	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		loader runner;
		Thread loader = new Thread(runner=new loader());
		loader.start();
		mcbotapp mainwindow = new mcbotapp();
		runner.frame.dispose();
		loader.stop();
		mainwindow.frmReticle.setVisible(true);
	}
	
}
