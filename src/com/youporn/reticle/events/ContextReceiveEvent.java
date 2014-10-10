package org.spigot.reticle.events;

import org.spigot.reticle.botfactory.mcbot;

public class ContextReceiveEvent extends Event {
	private final String cmd;
	private final String text;
	
	public ContextReceiveEvent(mcbot bot, String commandname, String text) {
		super(bot);
		this.cmd=commandname;
		this.text=text;
	}

	public String getCommandName() {
		return cmd;
	}
	
	public String getText() {
		return text;
	}
	
}
