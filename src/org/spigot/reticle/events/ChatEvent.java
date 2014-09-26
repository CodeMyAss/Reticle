package org.spigot.reticle.events;

public class ChatEvent extends CancellableEvent {
	private String message;
	private int pos=0;
	
	public ChatEvent(String message) {
		this.message=message;
	}
	
	public ChatEvent(String message, int pos) {
		this.message=message;
		this.pos=pos;
	}
	
	/**
	 * Returns message
	 * @return
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Returns chat position
	 * @return
	 */
	public int getPosition() {
		return pos;
	}
	
}
