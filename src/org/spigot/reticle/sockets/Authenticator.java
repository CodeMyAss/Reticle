package org.spigot.reticle.sockets;

import java.util.ArrayList;
import java.util.List;

import org.spigot.reticle.storage;
import org.spigot.reticle.API.POST;
import org.spigot.reticle.API.POST.POSTMETHOD;
import org.spigot.reticle.settings.botsettings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class Authenticator {
	private String username, password, access, client;
	private botsettings bot;

	private Authenticator(String username, String password) {
		this.username = username;
		this.password = password;
	}

	private Authenticator(String token, String player, byte b) {
		this.access = token;
		this.client = player;
	}
	
	protected Authenticator(String username, String id, String access, int b) {
		//String hex=javaHexDigest(id);
		this.username=username;
		this.password=id;
		//this.password=new String(id);
		this.access=access;
		//this.mj = new minecraftjoin(access,id,hex);
	}
	
	/**
	 * Send join game data to Mojang authentication servers
	 * Returns true if successful
	 * @return
	 */
	public boolean sendJoin() {
		POST form = new POST(storage.joinURLalt, true);
		form.setMethod(POSTMETHOD.GET);
		form.addField("user", username);
		form.addField("sessionId", access);
		form.addField("serverId", password);
		if (form.Execute()) {
			return true;
		} else {
			return false;
		}
	}

	public static Authenticator forJoinPurpose(String username, String ID, String accessToken) {
		return new Authenticator(username,ID,accessToken,1);
	}
	
	/**
	 * Refresh Authenticator settings
	 * Not safe to use
	 * @param botSettings
	 */
	public void setBot(botsettings botSettings) {
		this.bot = botSettings;
	}

	/**
	 * Refresh access token
	 * Returns true if sucessful
	 * @return
	 */
	public boolean refresh() {
		POST form = new POST(storage.AuthURL + "refresh", true);
		Gson gson = new GsonBuilder().create();
		minecraftrefresh log = new minecraftrefresh(access, client);
		String data = gson.toJson(log);
		form.setSingleData(data);
		if (form.Execute()) {
			minecraftrefresh acc = getNewAcc(form.getResponse());
			if (bot != null) {
				bot.maccesstoken = acc.getAccessToken();
				bot.mplayertoken = acc.getClientToken();
				storage.savesettings();
			}
			return true;
		} else {
			return false;
		}
	}

	private minecraftrefresh getNewAcc(String str) {
		JsonParser parser = new JsonParser();
		JsonObject obf = parser.parse(str).getAsJsonObject();
		String access = null;
		String client = null;
		if (obf.has("accessToken")) {
			access = obf.get("accessToken").getAsString();
		}
		if (obf.has("clientToken")) {
			client = obf.get("clientToken").getAsString();
		}
		return new minecraftrefresh(access, client);
	}

	public static Authenticator fromUsernameAndPassword(String username, String password) {
		return new Authenticator(username, password);
	}

	public static Authenticator fromAccessToken(String accessToken, String player) {
		return new Authenticator(accessToken, player, (byte) 0);
	}

	/**
	 * Send login request to Mojang authenticator server
	 * @return
	 */
	public boolean tryLogin() {
		accounts acc = getProfiles();
		if (acc == null) {
			return false;
		} else {
			if (bot != null) {
				bot.maccesstoken=acc.getAccessToken();
				bot.mplayertoken=acc.getClientToken();
				storage.savesettings();
			}
			return true;
		}
	}

	/**
	 * Get available profiles. Must be logged in
	 * @return
	 */
	public accounts getProfiles() {
		POST form = new POST(storage.AuthURL + "authenticate", true);
		Gson gson = new GsonBuilder().create();
		minecraftlogin log = new minecraftlogin(username, password);
		String data = gson.toJson(log);
		form.setSingleData(data);
		if (form.Execute()) {
			if (form.getResponseCode() == 200) {
				accounts acc = getAccount(form.getResponse());
				return acc;
			} else {
				storage.alert("Authentication", "Authentication failed");
			}
		} else {
			storage.alert("Authentication", "Authentication failed");
		}
		return null;
	}

	protected accounts getAccount(String response) {
		JsonParser parser = new JsonParser();
		JsonObject obf = parser.parse(response).getAsJsonObject();
		accounts acc;
		if (obf.has("accessToken")) {
			String access = obf.get("accessToken").getAsString();
			acc = new accounts(access);
			if (obf.has("clientToken")) {
				acc.setClientToken(obf.get("clientToken").getAsString());
			}
			if (obf.has("selectedProfile")) {
				String id = obf.get("selectedProfile").getAsJsonObject().get("id").getAsString();
				String name = obf.get("selectedProfile").getAsJsonObject().get("name").getAsString();
				acc.setSelectedProfile(name, id);
			}
			if (obf.has("availableProfiles")) {
				JsonArray ar = obf.get("availableProfiles").getAsJsonArray();
				for (JsonElement a : ar) {
					String id = a.getAsJsonObject().get("id").getAsString();
					String name = a.getAsJsonObject().get("name").getAsString();
					acc.addProfile(name, id);
				}
			}
			return acc;
		}
		return null;
	}

	/**
	 * Account structure
	 * @author Encorn
	 *
	 */
	public class accounts {
		private String access;
		private String client;
		private List<profile> availableprofiles = new ArrayList<profile>();
		private profile selected;

		public void addProfile(String name, String id) {
			this.availableprofiles.add(new profile(name, id));
		}

		public profile getSelectedProfile() {
			return selected;
		}

		public String getClientToken() {
			return client;
		}

		public void setSelectedProfile(String name, String id) {
			this.selected = new profile(name, id);
		}

		public List<profile> getAllProfiles() {
			return availableprofiles;
		}

		public void setClientToken(String token) {
			this.client = token;
		}

		public String getAccessToken() {
			return this.access;
		}

		public accounts(String access) {
			this.access = access;
		}


	}
	
	/**
	 * Profile structure
	 * @author Encorn
	 *
	 */
	public class profile {
		private String id;
		private String name;

		
		public String getID() {
			return id;
		}

		public String getUsername() {
			return name;
		}

		public profile(String username, String id) {
			this.id = id;
			this.name = username;
		}
	}
	
	@SuppressWarnings("unused")
	private class minecraftjoin {
		private String accessToken;
		private String selectedProfile;
		private String serverId;
		public minecraftjoin(String acc,String id, String serv) {
			this.accessToken=acc;
			this.selectedProfile=id;
			this.serverId=serv;
		}
	}

	private class minecraftrefresh {
		private String accessToken;
		private String clientToken;
		@SuppressWarnings("unused")
		private String selectedProfile;

		public minecraftrefresh(String acc, String cl) {
			this.accessToken = acc;
			this.clientToken = cl;
		}

		public String getAccessToken() {
			return accessToken;
		}

		public String getClientToken() {
			return clientToken;
		}
	}

	@SuppressWarnings("unused")
	private class minecraftlogin {
		public agent agent;
		private String username;
		private String password;

		public minecraftlogin(String username, String password) {
			this.agent = new agent("Minecraft", 1);
			this.username = username;
			this.password = password;
		}

		private class agent {
			private String name;
			private int version;

			public agent(String name, int version) {
				this.name = name;
				this.version = version;
			}
		}
	}
}
