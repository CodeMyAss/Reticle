package org.spigot.reticle.sockets;

import java.util.ArrayList;
import java.util.List;

import org.spigot.reticle.storage;
import org.spigot.reticle.API.POST;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Authenticator {
	private String username, password;

	public Authenticator(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public accounts getProfiles() {
		POST form = new POST(storage.AuthURL + "authenticate", true);
		Gson gson = new GsonBuilder().create();
		minecraftlogin log = new minecraftlogin(username, password);
		String data = gson.toJson(log);
		System.out.println("Data: " + data);
		form.setSingleData(data);
		if (form.Execute()) {
			if (form.getResponseCode() == 200) {
				System.out.println("Response code:" + form.getResponseCode());
				System.out.println("Response:" + form.getResponse());
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
	

	public accounts getAccount(String response) {
		JsonParser parser = new JsonParser();
		JsonObject obf = parser.parse(response).getAsJsonObject();
		accounts acc;
		if(obf.has("accessToken")) {
			String access=obf.get("accessToken").getAsString();
			acc=new accounts(access);
			if(obf.has("clientToken")) {
				acc.setClientToken(obf.get("clientToken").getAsString());
			}
			if(obf.has("selectedProfile")) {
				String id=obf.get("selectedProfile").getAsJsonObject().get("id").getAsString();
				String name=obf.get("selectedProfile").getAsJsonObject().get("name").getAsString();
				acc.setSelectedProfile(name, id);
			}
			if(obf.has("availableProfiles")) {
				JsonArray ar = obf.get("availableProfiles").getAsJsonArray();
				for(JsonElement a:ar) {
					String id=a.getAsJsonObject().get("id").getAsString();
					String name=a.getAsJsonObject().get("name").getAsString();
					acc.addProfile(name, id);				
				}
			}
			return acc;
		}
		return null;
	}
	
	
	@SuppressWarnings("unused")
	public class accounts {
		private String access;
		private String client;
		private List<profile> availableprofiles=new ArrayList<profile>();
		private profile selected;
		
		public void addProfile(String name, String id) {
			this.availableprofiles.add(new profile(name,id));
		}
		
		public profile getSelectedProfile() {
			return selected;
		}
		
		public void setSelectedProfile(String name, String id) {
			this.selected=new profile(name,id);
		}
		
		public List<profile> getAllProfiles() {
			return availableprofiles;
		}
		
		public void setClientToken(String token) {
			this.client=token;
		}
		
		public accounts(String access) {
			this.access=access;
		}
		
		private class profile {
			private String id;
			private String username;
			public profile(String username,String id) {
				this.id=id;
				this.username=username;
			}
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
