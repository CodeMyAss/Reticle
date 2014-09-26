package org.spigot.reticle.events;

import org.spigot.reticle.botfactory.mcbot;

public class ChatSendEvent extends ChatEvent {
	private mcbot sender;
	
	
	public ChatSendEvent(mcbot bot,String message, mcbot sender) {
		super(bot,message);
		this.sender=sender;
	}

	public mcbot getSender() {
		return this.sender;
	}
	
}
