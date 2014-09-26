package org.spigot.reticle.settings;

import java.util.HashMap;

import javax.sql.rowset.serial.SerialException;

import org.spigot.reticle.botfactory.mcbot;

public class struct_settings {
	public HashMap<String, botsettings> settings;
	public HashMap<String, mcbot> bots = new HashMap<String, mcbot>();
	public HashMap<String, String> globalsettings = new HashMap<String, String>();

	/**
	 * Returns string representation of settings
	 * @return
	 */
	public String saveToString() {
		return saveToString(false);
	}

	/**
	 * Returns string representation of settings
	 * If sensitive is true, final string will not include personal data
	 * @param sensitive
	 * @return
	 */
	public String saveToString(boolean sensitive) {
		StringBuilder sb = new StringBuilder();
		for (String key : globalsettings.keySet()) {
			String val = globalsettings.get(key);
			sb.append(key + ": " + val + "\r\n");
		}
		for (String key : settings.keySet()) {
			sb.append("\t" + key + "\r\n");
			botsettings set = settings.get(key);
			sb.append("\t\tServername: " + set.servername + "\r\n");
			sb.append("\t\tServer ip: " + set.serverip + "\r\n");
			sb.append("\t\tServer port: " + set.serverport + "\r\n");
			sb.append("\t\tAutoconnect: " + set.autoconnect + "\r\n");
			sb.append("\t\tNick: " + set.nick + "\r\n");
			sb.append("\t\tChat logger: " + set.chatlog + "\r\n");
			sb.append("\t\tMojang Nick: " + set.mcurrentusername + "\r\n");
			sb.append("\t\tUse Mojang Authentication: " + set.mojangusername + "\r\n");
			sb.append("\t\tUse Mojang Login Username: " + set.mojangloginusername + "\r\n");
			sb.append("\t\tUsername ID: " + set.mojangloginusernameid + "\r\n");
			sb.append("\t\tSave Mojang Password: " + set.savemojangpass + "\r\n");
			if (!sensitive && set.savemojangpass) {
				sb.append("\t\tMojang Password: " + set.mpassword + "\r\n");
			}
			sb.append("\t\tSave Mojang Access Token: " + set.saveaccess + "\r\n");
			if (!sensitive && set.saveaccess && set.maccesstoken != null && set.mplayertoken != null) {
				sb.append("\t\tMojang Access Token: " + set.maccesstoken + "\r\n");
				sb.append("\t\tMojang Client Token: " + set.mplayertoken + "\r\n");
			}
			sb.append("\t\tProtocol version: " + set.protocolversion + "\r\n");
			sb.append("\t\tAutologin: " + set.autologin + "\r\n");
			sb.append("\t\tAutologout: " + set.autologout + "\r\n");
			sb.append("\t\tAutoreconnect: " + set.autoreconnect + "\r\n");
			sb.append("\t\tMessage delay: " + set.messagedelay + "\r\n");
			sb.append("\t\tAutoreconnect delay: " + set.autoreconnectdelay + "\r\n");
			sb.append("\t\tAutoanti-afk: " + set.autoantiafk + "\r\n");
			sb.append("\t\tAutoanti-afk period: " + set.afkperiod + "\r\n");
			sb.append("\t\tAutonotify: " + set.activenotify + "\r\n");
			sb.append("\t\tMax lines: " + set.maxlines + "\r\n");
			sb.append("\t\tAutologin commands:\r\n");
			if (!sensitive) {
				for (String com : set.autologincmd) {
					sb.append("\t\t\t" + com + "\r\n");
				}
			}
			sb.append("\t\tAutologout commands:\r\n");
			if (!sensitive) {
				for (String com : set.autologoutcmd) {
					sb.append("\t\t\t" + com + "\r\n");
				}
			}
			sb.append("\t\tAutoantiafk commands:\r\n");
			if (!sensitive) {
				for (String com : set.autoantiafkcmd) {
					sb.append("\t\t\t" + com + "\r\n");
				}
			}
			sb.append("\t\tIgnored messages:\r\n");
			if (!sensitive) {
				for (String com : set.ignored) {
					sb.append("\t\t\t" + com + "\r\n");
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Parse settings from string representation
	 * @param stringSettings
	 * @throws SerialException
	 */
	public void loadFromString(String stringSettings) throws SerialException {
		String[] lines = stringSettings.split("\r\n");
		if (settings == null) {
			settings = new HashMap<String, botsettings>();
		}
		botsettings bot = null;
		boolean saved = false;
		int pos = 0;

		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		StringBuilder sb3 = new StringBuilder();
		StringBuilder sb4 = new StringBuilder();

		for (String line : lines) {
			if (line.equals("") || line.equals("\r\n") || line.startsWith("#") || line.equals("\n")) {
				continue;
			}

			if (line.startsWith("\t\t\t")) {
				saved = false;
				// Login/Logout/Antiafk commands
				if (pos == 1) {
					// We are getting list of login commands
					sb1.append("\r\n" + line.substring(3));
				} else if (pos == 2) {
					// We are getting list of logout commands
					sb2.append("\r\n" + line.substring(3));
				} else if (pos == 3) {
					// We are getting list of anti-afk commands
					sb3.append("\r\n" + line.substring(3));
				} else if (pos == 4) {
					// We are getting list of messages to ignore
					sb4.append("\r\n" + line.substring(3));
				}
			} else if (line.startsWith("\t\t")) {
				saved = false;
				// Regular local bot settings
				// What exactly is the settings
				String op = line.substring(2).split(":")[0];
				// What is the parameter (+2 because 1 for \t and 1 for
				// spawn between delimiter and value)
				String param = line.substring(op.length() + 3);
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
					case "Ignored messages":
						pos = 4;
					break;
					case "Servername":
						if (param.toLowerCase().equals("reticle")) {
							throw new SerialException();
						}
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
					case "Mojang Nick":
						bot.mcurrentusername = param;
					break;

					case "Use Mojang Authentication":
						bot.mojangusername = Boolean.parseBoolean(param);
					break;
					case "Use Mojang Login Username":
						bot.mojangloginusername = param;
					break;
					case "Username ID":
						bot.mojangloginusernameid = param;
					break;
					case "Mojang Password":
						bot.mpassword = param;
					break;
					case "Mojang Access Token":
						bot.maccesstoken = param;
					break;
					case "Mojang Client Token":
						bot.mplayertoken = param;
					break;
					case "Save Mojang Access Token":
						bot.saveaccess = Boolean.parseBoolean(param);
					break;

					case "Save Mojang Password":
						bot.savemojangpass = Boolean.parseBoolean(param);
					break;
					case "Protocol version":
						bot.protocolversion = Integer.parseInt(param);
					break;
					case "Autologin":
						bot.autologin = Boolean.parseBoolean(param);
					break;
					case "Autologout":
						bot.autologout = Boolean.parseBoolean(param);
					break;
					case "Autoreconnect delay":
						bot.autoreconnectdelay = Integer.parseInt(param);
					break;
					case "Autoreconnect":
						bot.autoreconnect = Boolean.parseBoolean(param);
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
					case "Message delay":
						bot.messagedelay = Integer.parseInt(param);
					break;
					case "Chat logger":
						bot.chatlog = Boolean.parseBoolean(param);
					break;
					case "Max lines":
						bot.maxlines = Integer.parseInt(param);
					break;
				}

			} else if (line.startsWith("\t")) {
				saved = true;
				// Bot name here
				if (bot != null) {
					// Previous bot exists, not saved yet
					if (sb1.toString().length() > 2) {
						bot.autologincmd = sb1.toString().substring(2).split("\r\n");
					}
					if (sb2.toString().length() > 2) {
						bot.autologoutcmd = sb2.toString().substring(2).split("\r\n");
					}
					if (sb3.toString().length() > 2) {
						bot.autoantiafkcmd = sb3.toString().substring(2).split("\r\n");
					}
					if (sb4.toString().length() > 2) {
						bot.ignored = sb4.toString().substring(2).split("\r\n");
					}
					settings.put(bot.getTabName(), bot);
					bot = new botsettings(null);
					sb1 = new StringBuilder();
					sb2 = new StringBuilder();
					sb3 = new StringBuilder();
					sb4 = new StringBuilder();
				} else {
					// We are first bot ever
					bot = new botsettings(null);
					sb1 = new StringBuilder();
					sb2 = new StringBuilder();
					sb3 = new StringBuilder();
					sb4 = new StringBuilder();
				}
			} else {
				// Global options
				String op = line.split(":")[0];
				// What is the parameter (+2 because 1 for \t and 1 for
				// spawn between delimiter and value)
				String param = line.substring(op.length() + 2);
				globalsettings.put(op, param);
			}
		}
		// Last bot to be saved
		if (!saved && bot != null) {
			if (sb1.toString().length() > 2) {
				bot.autologincmd = sb1.toString().substring(2).split("\r\n");
			}
			if (sb2.toString().length() > 2) {
				bot.autologoutcmd = sb2.toString().substring(2).split("\r\n");
			}
			if (sb3.toString().length() > 2) {
				bot.autoantiafkcmd = sb3.toString().substring(2).split("\r\n");
			}
			if (sb4.toString().length() > 2) {
				bot.ignored = sb4.toString().substring(2).split("\r\n");
			}
			settings.put(bot.getTabName(), bot);
		}
	}
}
