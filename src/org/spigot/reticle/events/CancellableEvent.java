package org.spigot.reticle.events;

public class CancellableEvent extends event {
	
	
	private boolean cancelled=false;
	

	public boolean isCancelled() {
		if(cancelled) {
			return true;
		} else {
			return false;
		}
	}
	
	public void setCancelled(boolean set) {
		if(set) {
			cancelled=true;
		} else {
			cancelled=false;
		}
	}
	
}
