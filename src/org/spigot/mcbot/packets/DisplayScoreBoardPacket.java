package org.spigot.mcbot.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

public class DisplayScoreBoardPacket extends packet {
	public static final int ID=61;
	private ByteBuffer sock;

	public DisplayScoreBoardPacket(ByteBuffer sock) {
		this.sock=sock;
	}
	
	public void Read() throws IOException {
		super.input=sock;
		//Pos
		super.readByte();
		//Scoreboard name
		super.readString();
	}
}
