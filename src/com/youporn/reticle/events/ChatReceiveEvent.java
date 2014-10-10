package org.spigot.reticle.events;

import org.spigot.reticle.botfactory.mcbot;


public class ChatReceiveEvent extends ChatEvent {

	public ChatReceiveEvent(mcbot bot,String message) {
		super(bot,message, false);
	}
}
