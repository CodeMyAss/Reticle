package org.spigot.reticle.events;

public class TabCompleteEvent extends Event {
	private String[] names;

	public TabCompleteEvent(String[] sugg) {
		this.names = sugg;
	}

	public String[] getNames() {
		return this.names;
	}

}
