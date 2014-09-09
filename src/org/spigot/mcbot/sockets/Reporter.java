package org.spigot.mcbot.sockets;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.spigot.mcbot.storage;

public class Reporter extends Thread {

	private ACTION act;
	public String issue = "";

	public Reporter(ACTION action) {
		this.act = action;
	}

	@Override
	public void run() {
		String url = "http://atlantida.php5.cz/report.php";
		String[] data = new String[4];
		/*
		 * 0 for version 1 for type 2 for config 3 for data
		 */
		try {
			data[0] = URLEncoder.encode(storage.version, "UTF-8");
			data[1] = URLEncoder.encode(act.id+"", "UTF-8");
			//only send config if reporting error
			data[2] = "";
			data[3] = "";
			switch (act.id) {
				case 1:
					data[3] = URLEncoder.encode(this.issue, "UTF-8");
					data[2] = URLEncoder.encode(storage.getInstance().settin.saveToString(), "UTF-8");
					storage.conlog("Thank you for reporting problem!");
				break;
				
				case 2:
					data[3] = URLEncoder.encode("Bots: "+storage.getInstance().settin.bots.size(), "UTF-8");
				break;
				
				case 3:
					data[3] = URLEncoder.encode("Unused", "UTF-8");
				break;
			}
			senddata(url,data);
		} catch (IOException e) {
			return;
		}
	}

	public static enum ACTION {
		REPORTISSUE(1), REPORTUSAGE(2), UNUSED(3);
		public final int id;
		ACTION(int i) {
			this.id = i;
		}
	}

	public void senddata(String urll, String[] data) throws IOException {
		URL url = new URL(urll + "?version="+data[0]+"&type="+data[1]+"&config="+data[2]+"&data=" + data[3]);
		URLConnection con = url.openConnection();
		con.getInputStream();
	}
}
