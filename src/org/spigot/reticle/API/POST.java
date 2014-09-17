package org.spigot.reticle.API;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;

public class POST {
	private String url;
	private HashMap<String, String> fields = new HashMap<String, String>();
	private boolean resp = false;
	private String content = "application/json";
	private int respcode;
	private String response;
	private boolean single=false;
	private String singledata;

	public POST(String URL, boolean resp) {
		this.url = URL;
		this.resp = resp;
	}

	public void setContentType(String type) {
		this.content = type;
	}

	public int getResponseCode() {
		return respcode;
	}

	public String getResponse() {
		return response;
	}

	public boolean Execute() {
		try {
			String param;
			if (single) {
				param = singledata;
			} else {
				StringBuilder sb = new StringBuilder();
				sb.append("");
				for (String key : fields.keySet()) {
					sb.append("&" + key + "=" + fields.get(key));
				}
				param = sb.toString().substring(1);
			}
			URL obj;
			obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", content);
			con.setRequestProperty("Content-Length", "" + param.getBytes("UTF-8").length);
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(param);
			wr.flush();
			wr.close();
			respcode = con.getResponseCode();
			if (resp) {
				if (respcode == 200) {
					response = IOUtils.toString(con.getInputStream(), "UTF-8");
				} else {
					response = IOUtils.toString(con.getErrorStream(), "UTF-8");
				}
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void setSingleData(String data) {
		this.single = true;
		this.singledata = data;
	}

	public void addField(String name, String value) {
		fields.put(name, value);
	}

}
