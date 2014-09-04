package org.spigot.mcbot.packets;

import java.io.IOException;
import java.io.InputStream;


public class ConnectionResetPacket extends packet {
	private InputStream input;
	
	public ConnectionResetPacket(InputStream s) {
		this.input=s;
	}
	
	public String read() throws IOException {
		super.input=input;
		return super.readString();
	}
}
