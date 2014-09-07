package org.spigot.mcbot.events;

public class PluginMessageReceiveEvent extends PluginMessageEvent {

	public PluginMessageReceiveEvent(String channel, byte[] message) {
		super(channel, message);
	}

}
