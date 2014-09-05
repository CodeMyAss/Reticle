package org.spigot.mcbot.packets;

import java.io.IOException;
import java.net.Socket;

public class SpawnPositionPacket extends packet {

	private Socket sock;
	private int len;

	public SpawnPositionPacket(Socket sock, int len) {
		this.sock = sock;
	}

	public void Read() throws IOException {
		super.input = sock.getInputStream();
		if (len == 16) {
			// Old packet
			// X
			super.readInt();
			// Y
			super.readInt();
			// Z
			super.readInt();
		} else if (len == 12) {
			// New packet
			// Data
			super.readLong();
		}
	}
}
