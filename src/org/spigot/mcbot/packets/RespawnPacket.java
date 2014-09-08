package org.spigot.mcbot.packets;

import java.io.IOException;
import java.net.Socket;

public class RespawnPacket extends packet {
	public static final int ID=7;
	private Socket sock;

	public RespawnPacket(Socket sock) {
		this.sock = sock;
	}

	public void Read() throws IOException {
		super.input = sock.getInputStream();
		//Dimension
		super.readInt();
		//Difficulty
		super.readByte();
		//Gamemode
		super.readByte();
		//Level name
		super.readString();
	}
}
