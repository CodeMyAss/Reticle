package org.spigot.reticle.events;

import org.spigot.reticle.botfactory.mcbot;

public class PluginMessageSendEvent extends PluginMessageEvent {

	public PluginMessageSendEvent(mcbot bot,String channel, byte[] message) {
		super(bot, channel, message);
	}

}
