package org.spigot.mcbot.packets;

import java.io.IOException;
import java.net.Socket;

public class SpawnPositionPacket extends packet {
	public static final int ID = 5;
	private Socket sock;

	public SpawnPositionPacket(Socket sock) {
		this.sock = sock;
	}

	public void Read() throws IOException {
		super.input = sock.getInputStream();
		// Old packet
		// X
		super.readInt();
		// Y
		super.readInt();
		// Z
		super.readInt();
	}
}
