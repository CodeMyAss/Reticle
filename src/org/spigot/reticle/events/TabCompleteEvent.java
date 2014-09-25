package org.spigot.reticle.events;

public class TabCompleteEvent extends event {
	private String[] names;

	public TabCompleteEvent(String[] sugg) {
		this.names = sugg;
	}

	public String[] getNames() {
		return this.names;
	}

}
