package org.spigot.mcbot.events;

import org.spigot.mcbot.botfactory.mcbot;

public class ChatSendEvent extends ChatEvent {
	private mcbot sender;
	
	
	public ChatSendEvent(String message, mcbot sender) {
		super(message);
		this.sender=sender;
	}

	public mcbot getSender() {
		return this.sender;
	}
	
}
