package org.spigot.mcbot;

import org.spigot.mcbot.sockets.Reporter;

public class MAIN {
	public static void main(String[] args) {
		loader runner;
		Thread loader = new Thread(runner = new loader());
		loader.start();
		mcbotapp mainwindow = null;
		try {
			mainwindow = new mcbotapp();
			runner.frame.dispose();
			mainwindow.frmReticle.setVisible(true);
			if(storage.getAutoupdate()) {
				storage.checkforupdates();
			}
			if(storage.getAutodebug()) {
				new Reporter(Reporter.ACTION.REPORTUSAGE).start();
			}
		} catch (NumberFormatException e) {
			storage.alert("Configuration error", "Failed to load configuration\n\nReason: Numeric field contains illegal characters.\nPlease fix your config!");
			runner.frame.dispose();
			System.exit(1);
			if (mainwindow != null) {
				if (mainwindow.frmReticle != null) {
					mainwindow.frmReticle.dispose();
				}
			}
		} catch (StringIndexOutOfBoundsException e) {
			storage.alert("Configuration error", "Failed to load configuration\n\nReason: Malformed configuration format.\nPlease fix your config!");
			runner.frame.dispose();
			if (mainwindow != null) {
				if (mainwindow.frmReticle != null) {
					mainwindow.frmReticle.dispose();
				}
			}
			System.exit(1);
		}
	}
}
