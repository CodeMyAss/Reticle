package org.spigot.reticle.events;

import java.util.List;

import org.spigot.reticle.botfactory.mcbot;

public class TeamEvent extends Event {
private final String team;
private final byte mode;
private final String prefix;
private final String suffix;
private final String teamdname;
private final String nametag;
private final byte ffire;
private final byte color;
private final List<String> players;
	
	
	
	public TeamEvent(mcbot bot,String team,byte mode,String prefix,String suffix,String name,byte ffire,String nametag,byte color,List<String> players) {
		super(bot);
		this.team=team;
		this.mode=mode;
		this.prefix=prefix;
		this.suffix=suffix;
		this.teamdname=name;
		this.nametag=nametag;
		this.ffire=ffire;
		this.color=color;
		this.players=players;
	}
	
	public List<String> getPlayers() {
		return players;
	}
	
	public byte getColor() {
		return color;
	}
	
	public String getColorAsFormatedString() {
		return "§"+color;
	}
	
	public String getColorAsString() {
		return ""+color;
	}
	
	
	public String getNameTag() {
		return nametag;
	}
	
	public boolean NameTagVisibleAlways() {
		return (nametag.equals("always"));
	}
	public boolean NameTagVisibleNever() {
		return (nametag.equals("never"));
	}
	public boolean NameTagHideForOwnTeam() {
		return (nametag.equals("hideForOwnTeam"));
	}
	public boolean NameTagHideForOtherTeams() {
		return (nametag.equals("hideForOtherTeams"));
	}
	
	public boolean getFriendlyFire() {
		return (ffire==1);
	}
	
	public boolean getFriendlyFireSeeingInvisibles() {
		return (ffire==3);
	}
	
	public String getTeamName() {
		return this.team;
	}
	
	public String getTeamDisplayName() {
		return this.teamdname;
	}
	
	public String getPrefix() {
		return this.prefix;
	}
	
	public String getSuffix() {
		return this.suffix;
	}
	
	public boolean PlayersBeingAdded() {
		return (mode==3);
	}
	
	public boolean PlayersBeingRemoved() {
		return (mode==4);
	}
	
	public boolean TeamIsBeingCreated() {
		return (mode==0);
	}
	
	public boolean TeamIsBeingRemoved() {
		return (mode==1);
	}
	
	public boolean TeamInformationAreBeingUpdated() {
		return (mode==2);
	}
	
	public boolean PlayersManipulation() {
		return (mode==3 || mode==4);
	}
	
	public boolean TeamManipulation() {
		return (mode==3 || mode==4);
	}
	
}
