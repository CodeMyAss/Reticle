package org.spigot.reticle.events;

import org.spigot.reticle.botfactory.mcbot;

public class Event {
	private final mcbot bot;
	private final long time;
	
	/**
	 * Returns bot of origin
	 * @return Returns bot of origin
	 */
	public final mcbot getBot() {
		return bot;
	}
	
	/**
	 * Returns invocation time
	 * @return Returns timestamp of invocation
	 */
	public final long getTime() {
		return time;
	}
	
	protected Event(mcbot bot) {
		this.bot=bot;
		this.time=System.currentTimeMillis() / 1000;
	}
	
	/**
	 * Returns event name
	 * @return Returns event name
	 */
	protected String getEventName() {
		return "Event";
	}
	
	
	
}
