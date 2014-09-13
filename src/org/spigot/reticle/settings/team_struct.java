package org.spigot.reticle.settings;

import java.util.ArrayList;
import java.util.List;

public class team_struct {
	public String teamName;
	public String color = "";
	public String prefix = "";
	public String suffix = "";

	public List<String> players = new ArrayList<String>();
	public List<String> formatedplayers = new ArrayList<String>();

	public String format;

	private void reFormat() {
		String fp = this.prefix + this.color;
		String lp = this.suffix;
		this.formatedplayers = new ArrayList<String>();
		for (String player : players) {
			formatedplayers.add(fp + player + lp);
		}
	}

	public void RemovePlayers(List<String> pl) {
		for (String player : pl) {
			if (players.contains(player)) {
				players.remove(player);
			}
		}
	}

	public void AddPlayers(List<String> pl) {
		for (String player : pl) {
			if (!players.contains(player)) {
				players.add(player);
			}
		}
		reFormat();
	}

	public String getFormatedPlayer(String name) {
		int i = 0;
		for (String player : players) {
			if (player.equals(name)) {
				return formatedplayers.get(i);
			}
			i++;
		}
		return "???";
	}

	public void setDisplayFormat(String prefix, String suffix, String color) {
		this.prefix = prefix;
		this.suffix = suffix;
		this.color = color;
		reFormat();
	}

	public team_struct(String teamname) {
		this.teamName = teamname;
	}
}
