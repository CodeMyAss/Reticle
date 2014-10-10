package org.spigot.reticle.settings;

import java.util.ArrayList;
import java.util.List;

public class team_struct {
	public String teamName;
	protected String color = "";
	protected String prefix = "";
	protected String suffix = "";

	public List<String> players = new ArrayList<String>();
	public List<String> formatedplayers = new ArrayList<String>();

	protected String format;

	private void reFormat() {
		String fp = this.prefix + this.color;
		String lp = this.suffix;
		this.formatedplayers = new ArrayList<String>();
		for (String player : players) {
			formatedplayers.add(fp + player + lp);
		}
	}

	/**
	 * Removes players from Team
	 * @param playerList
	 */
	public void RemovePlayers(List<String> playerList) {
		for (String player : playerList) {
			if (players.contains(player)) {
				players.remove(player);
			}
		}
	}

	/**
	 * Add Players to Team
	 * @param playerList
	 */
	public void AddPlayers(List<String> playerList) {
		for (String player : playerList) {
			if (!players.contains(player)) {
				players.add(player);
			}
		}
		reFormat();
	}

	/**
	 * Returns string representation of player in team
	 * @param name Name of player to be formated
	 * @return Returns String representation of player
	 */
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

	/**
	 * Change display format
	 * Not safe to use
	 * @param prefix
	 * @param suffix
	 * @param color
	 */
	public void setDisplayFormat(String prefix, String suffix, String color) {
		this.prefix = prefix;
		this.suffix = suffix;
		this.color = color;
		reFormat();
	}

	public team_struct(String teamName) {
		this.teamName = teamName;
	}
}
