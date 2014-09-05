package org.spigot.mcbot.settings;

import java.util.HashMap;
import org.spigot.mcbot.botfactory.mcbot;

public class struct_settings {
	public HashMap<String, botsettings> settings;
	public HashMap<String, mcbot> bots = new HashMap<String, mcbot>();

	public String saveToString() {
		StringBuilder sb = new StringBuilder();
		for (String key : settings.keySet()) {
			sb.append(key + "\r\n");
			botsettings set = settings.get(key);
			sb.append("\tServername: " + set.servername + "\r\n");
			sb.append("\tServer ip: " + set.serverip + "\r\n");
			sb.append("\tServer port: " + set.serverport + "\r\n");
			sb.append("\tAutoconnect: " + set.autoconnect + "\r\n");
			sb.append("\tNick: " + set.nick + "\r\n");
			sb.append("\tAutologin: " + set.autologin + "\r\n");
			sb.append("\tAutologout: " + set.autologout + "\r\n");
			sb.append("\tAutoreconnect: " + set.autoreconnect + "\r\n");
			sb.append("\tAutoreconnect delay: " + set.autoreconnectdelay + "\r\n");
			sb.append("\tAutoanti-afk: " + set.autoantiafk + "\r\n");
			sb.append("\tAutoanti-afk period: " + set.afkperiod + "\r\n");
			sb.append("\tAutonotify: " + set.activenotify + "\r\n");
			sb.append("\tAutologin commands:\r\n");
			for (String com : set.autologincmd) {
				sb.append("\t\t" + com + "\r\n");
			}
			sb.append("\tAutologout commands:\r\n");
			for (String com : set.autologoutcmd) {
				sb.append("\t\t" + com + "\r\n");
			}
			sb.append("\tAutoantiafk commands:\r\n");
			for (String com : set.autoantiafkcmd) {
				sb.append("\t\t" + com + "\r\n");
			}
		}
		return sb.toString();
	}

	public void loadFromString(String str) {
		String[] lines = str.split("\r\n");
		if (settings == null) {
			settings = new HashMap<String, botsettings>();
		}
		botsettings bot = null;
		boolean saved = false;
		int pos = 0;

		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		StringBuilder sb3 = new StringBuilder();

		for (String line : lines) {
			if (line.equals("") || line.equals("\r\n") || line.startsWith("#")) {
				continue;
			}
			if (!line.startsWith("\t")) {
				// Bot name here

				if (bot != null) {
					// Previous bot exists, not saved yet
					bot.bottabname = bot.gettabname();
					if (sb1.toString().length() > 2) {
						bot.autologincmd = sb1.toString().substring(2).split("\r\n");
						System.out.println("ALC: "+sb1.toString());
					}
					if (sb2.toString().length() > 2) {
						bot.autologoutcmd = sb2.toString().substring(2).split("\r\n");
					}
					if (sb3.toString().length() > 2) {
						bot.autoantiafkcmd = sb3.toString().substring(2).split("\r\n");
					}
					settings.put(bot.bottabname, bot);
					saved = true;
					bot = new botsettings(null);
					sb1 = new StringBuilder();
					sb2 = new StringBuilder();
					sb3 = new StringBuilder();
				} else {
					// We are first bot ever
					bot = new botsettings(null);
					saved = true;
					sb1 = new StringBuilder();
					sb2 = new StringBuilder();
					sb3 = new StringBuilder();
				}

			} else {
				saved = false;
				if (line.startsWith("\t\t")) {
					if (pos == 1) {
						// We are getting list of login commands
						sb1.append("\r\n" + line.substring(2));
					} else if (pos == 2) {
						// We are getting list of logout commands
						sb2.append("\r\n" + line.substring(2));
					} else if (pos == 3) {
						// We are getting list of anti-afk commands
						sb3.append("\r\n" + line.substring(2));
					}
				} else if (line.startsWith("\t")) {
					// What exactly is the settings
					String op = line.substring(1).split(":")[0];
					// What is the parameter (+2 because 1 for \t and 1 for
					// spawn between delimiter and value)
					String param = line.substring(op.length() + 2);
					if (param.startsWith(" ")) {
						param = param.substring(1);
					}
					switch (op) {
						case "Autologin commands":
							pos = 1;
						break;
						case "Autologout commands":
							pos = 2;
						break;
						case "Autoantiafk commands":
							pos = 3;
						break;
						case "Servername":
							bot.servername = param;
						break;
						case "Server ip":
							bot.serverip = param;
						break;
						case "Server port":
							bot.serverport = Integer.parseInt(param);
						break;
						case "Autoconnect":
							bot.autoconnect = Boolean.parseBoolean(param);
						break;
						case "Nick":
							bot.nick = param;
						break;
						case "Autologin":
							bot.autologin = Boolean.parseBoolean(param);
						break;
						case "Autologout":
							bot.autologout = Boolean.parseBoolean(param);
						break;
						case "Autoreconnect delay":
							bot.autoreconnectdelay=Integer.parseInt(param);
						break;
						case "Autoreconnect":
							bot.autoreconnect=Boolean.parseBoolean(param);
						break;
						case "Autoanti-afk":
							bot.autoantiafk = Boolean.parseBoolean(param);
						break;
						case "Autoanti-afk period":
							bot.afkperiod = Integer.parseInt(param);
						break;
						case "Autonotify":
							bot.activenotify = Boolean.parseBoolean(param);
						break;
					}
				}
			}
		}
		//Last bot to be saved
		if (!saved && bot != null) {
			if (sb1.toString().length() > 2) {
				bot.autologincmd = sb1.toString().substring(2).split("\r\n");
				System.out.println("ALC: "+sb1.toString());
			}
			if (sb2.toString().length() > 2) {
				bot.autologoutcmd = sb2.toString().substring(2).split("\r\n");
			}
			if (sb3.toString().length() > 2) {
				bot.autoantiafkcmd = sb3.toString().substring(2).split("\r\n");
			}
			bot.bottabname = bot.gettabname();
			settings.put(bot.bottabname, bot);
		}
	}
}
