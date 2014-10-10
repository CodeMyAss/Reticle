package org.spigot.reticle.events;

import org.spigot.reticle.botfactory.mcbot;

public class CancellableEvent extends Event {
	
	
	public CancellableEvent(mcbot bot) {
		super(bot);
	}

	private boolean cancelled=false;
	

	/**
	 * Returns true if event was cancelled
	 * @return Returns True if cancelled, False if otherwise
	 */
	public boolean isCancelled() {
		if(cancelled) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Cancel event
	 * @param set
	 */
	public void setCancelled(boolean set) {
		if(set) {
			cancelled=true;
		} else {
			cancelled=false;
		}
	}
	
}
