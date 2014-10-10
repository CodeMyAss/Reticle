package org.spigot.reticle.events;

import org.spigot.reticle.botfactory.mcbot;

public class TabCompleteEvent extends Event {
	private final String[] names;

	public TabCompleteEvent(mcbot bot,String[] sugg) {
		super(bot);
		this.names = sugg;
	}

	public String[] getNames() {
		return this.names;
	}

}
