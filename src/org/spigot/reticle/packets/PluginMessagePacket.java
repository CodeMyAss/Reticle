package org.spigot.reticle.packets;

import java.nio.ByteBuffer;

import org.spigot.reticle.events.PluginMessageEvent;

public class PluginMessagePacket extends packet {
	private ByteBuffer sock;
	public static final int ID=63;
	
	public PluginMessagePacket(ByteBuffer sock) {
		this.sock=sock;
	}
	
	public PluginMessageEvent Read() throws Exception {
		super.input=sock;
		String channel=super.readString();
		byte[] data=super.readArray();
		return new PluginMessageEvent(channel,data);
	}
	
}
