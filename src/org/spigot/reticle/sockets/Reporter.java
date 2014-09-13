package org.spigot.reticle.sockets;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.spigot.reticle.storage;

public class Reporter extends Thread {

	private ACTION act;
	public String issue = "";

	public Reporter(ACTION action) {
		this.act = action;
	}

	@Override
	public void run() {
		String url = "http://reticle.mc-atlantida.eu/report.php";
		String[] data = new String[4];
		/*
		 * 0 for version 1 for type 2 for config 3 for data
		 */
		try {
			data[0] = URLEncoder.encode(storage.version, "UTF-8");
			data[1] = URLEncoder.encode(act.id + "", "UTF-8");
			// only send config if reporting error
			data[2] = "";
			data[3] = "";
			switch (act.id) {
				case 1:
					data[3] = URLEncoder.encode(this.issue, "UTF-8");
					data[2] = URLEncoder.encode(storage.getInstance().settin.saveToString(), "UTF-8");
					storage.conlog("Thank you for reporting problem!");
				break;

				case 2:
					data[3] = URLEncoder.encode("Bots: " + storage.getInstance().settin.bots.size(), "UTF-8");
				break;

				case 3:
					data[3] = URLEncoder.encode("Unused", "UTF-8");
				break;
			}
			senddata(url, data);
		} catch (Exception e) {
			storage.conlog("§4 Reporting operation failed");
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

		private void senddata(String url,String[] data) throws Exception {
		String param = "version=" + data[0] + "&type=" + data[1] + "&config=" + data[2] + "&data=" + data[3];
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", "Reticle");
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(param);
		wr.flush();
		wr.close();
		con.getResponseCode();
	}
}
