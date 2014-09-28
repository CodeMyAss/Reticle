package org.spigot.reticle.events;

import org.spigot.reticle.botfactory.mcbot;

public class Event {
	private final mcbot bot;
	
	/**
	 * Returns bot of origin
	 * @return Returns bot of origin
	 */
	public final mcbot getBot() {
		return bot;
	}
	
	
	protected Event(mcbot bot) {
		this.bot=bot;
	}
	
	/**
	 * Returns event name
	 * @return Returns event name
	 */
	protected String getEventName() {
		return "Event";
	}
	
}
