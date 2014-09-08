package org.spigot.mcbot.packets;

import java.net.Socket;

import org.spigot.mcbot.events.PluginMessageEvent;

public class PluginMessagePacket extends packet {
	private Socket sock;
	public static final int ID=63;
	
	public PluginMessagePacket(Socket sock) {
		this.sock=sock;
	}
	
	public PluginMessageEvent Read() throws Exception {
		super.input=sock.getInputStream();
		String channel=super.readString();
		byte[] data=super.readArray();
		return new PluginMessageEvent(channel,data);
	}
	
}
