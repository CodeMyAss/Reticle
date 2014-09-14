package org.spigot.reticle.packets;

import java.nio.ByteBuffer;

import org.spigot.reticle.events.PluginMessageEvent;

public class PluginMessagePacket extends packet {
	private packet reader;
	public static final int ID=0x3F;
	
	public PluginMessagePacket(ByteBuffer sock, packet reader) {
		this.reader=reader;
		this.reader.input=sock;
	}
	
	public PluginMessageEvent Read() throws Exception {
		String channel=reader.readString();
		byte[] data=reader.readArray();
		return new PluginMessageEvent(channel,data);
	}
}
