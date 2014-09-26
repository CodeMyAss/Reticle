package org.spigot.reticle.events;

import org.spigot.reticle.botfactory.mcbot;

public class PluginMessageReceiveEvent extends PluginMessageEvent {

	public PluginMessageReceiveEvent(mcbot bot, String channel, byte[] message) {
		super(bot, channel, message);
	}

}
