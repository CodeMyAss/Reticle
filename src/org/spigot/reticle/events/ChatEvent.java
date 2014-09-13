package org.spigot.reticle.events;

public class ChatEvent extends CancellableEvent {
	private String message;
	
	public ChatEvent(String message) {
		this.message=message;
	}
	
	public String getMessage() {
		return message;
	}
	
}
