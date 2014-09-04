package org.spigot.mcbot.packets;

import java.io.IOException;
import java.net.Socket;

public class JoinGamePacket extends packet {
	private Socket sock;
	
	public JoinGamePacket(Socket sock) {
		this.sock=sock;
	}
	
	public void Read() throws IOException {
		super.input=sock.getInputStream();
		//Our entity ID
		super.readInt();
		//Our gamemode
		super.readByte();
		//Our dimension (world)
		super.readByte();
		//Max players
		super.readByte();
		//Level type
		super.readString();
		//Reduced debug info
		super.readBoolean();
	}
	
}
