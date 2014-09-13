package org.spigot.reticle.events;

public class ChatEvent extends CancellableEvent {
	private String message;
	private int pos;
	
	public ChatEvent(String message) {
		this.message=message;
	}
	
	public ChatEvent(String message, int pos) {
		this.message=message;
		this.pos=pos;
	}
	
	public String getMessage() {
		return message;
	}
	
	public int getPosition() {
		return pos;
	}
	
}
