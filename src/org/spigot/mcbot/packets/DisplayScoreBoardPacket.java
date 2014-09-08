package org.spigot.mcbot.packets;

import java.io.IOException;
import java.net.Socket;

public class DisplayScoreBoardPacket extends packet {
	public static final int ID=61;
	private Socket sock;

	public DisplayScoreBoardPacket(Socket sock) {
		this.sock=sock;
	}
	
	public void Read() throws IOException {
		super.input=sock.getInputStream();
		//Pos
		super.readByte();
		//Scoreboard name
		super.readString();
	}
}
