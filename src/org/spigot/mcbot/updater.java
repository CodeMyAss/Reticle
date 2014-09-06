package org.spigot.mcbot;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import org.apache.commons.io.FileUtils;

public class updater extends Thread {

	@Override
	public void run() {
		try {
			String updateurl = "http://reticle.mc-atlantida.eu/update.php?version=" + storage.version;
			String changlogurl = "http://reticle.mc-atlantida.eu/versions/current/changelog.txt";
			String currentversionurl="http://reticle.mc-atlantida.eu/versions/current/Reticle.jar";
			String res = readurl(updateurl);
			if (res.startsWith("NV:")) {
				// New version is found!
				String newver = res.substring(3);
				sendmsg("Found new version !");
				sendmsg("Current version: §n" + storage.version);
				sendmsg("Latest version: §n" + newver);
				// Lets read and display Changelog
				String chlog = readurl(changlogurl);
				sendmsg("Changelog: \n=================================\n" + chlog + "\n=================================");

				// Ask user if he wants to update
				JTabbedPane contentPane = storage.gettabbedpane();
				int n = JOptionPane.showConfirmDialog(contentPane, "New version was found!\n\nCurrent version: " + storage.version + "\nLatest version :" + newver + "\n\nUpdate?", "Update", JOptionPane.YES_NO_OPTION);
				
				if (n == JOptionPane.YES_OPTION) {
					String destfile="Reticle_"+newver+".jar";
					sendmsg("Download started");
					getfilefromurl(currentversionurl,destfile);
					storage.alert("Update", "Update was saved as "+destfile);
					sendmsg("Update was saved as "+destfile);
				}

			} else if (res.startsWith("CV")) {
				// Current version latest
				sendmsg("Current version is latest");
			}
			storage.getInstance().updater = null;
		} catch (IOException e) {
			sendmsg("Update service not available!");
			e.printStackTrace();
		}

	}

	public void getfilefromurl(String url,String destfile) throws IOException {
		FileUtils.copyURLToFile(new URL(url), new File(destfile));
	}

	public String readurl(String str) throws IOException {
		URL url = new URL(str);
		URLConnection con = url.openConnection();
		InputStream in = con.getInputStream();
		StringBuilder sb = new StringBuilder();
		int chr;
		while ((chr = in.read()) != -1) {
			sb.append((char) chr);
		}
		return sb.toString();
	}

	public void sendmsg(String msg) {
		storage.conlog("§b[§4Updater§b] §f" + msg);
	}
}
