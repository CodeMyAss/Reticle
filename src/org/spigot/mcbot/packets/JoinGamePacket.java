package org.spigot.mcbot.packets;

import java.io.IOException;
import java.net.Socket;

import org.spigot.mcbot.sockets.connector;

public class JoinGamePacket extends packet {
	private Socket sock;
	
	public JoinGamePacket(Socket sock) {
		this.sock=sock;
	}
	
	public void Read(connector connector) throws IOException {
		super.input=sock.getInputStream();
		//Our entity ID
		super.readInt();
		//Our gamemode
		super.readByte();
		//Our dimension (world)
		super.readByte();
		//Difficulty
		super.readByte();
		//Max players
		Byte maxplayers=super.readByte();
		//Level type
		super.readString();
		//Reduced debug info (1.8)
		//super.readBoolean();
		if(maxplayers>30 && maxplayers <50) {
			//2 Columns 20 rows
			connector.settablesize(2, 20);
		} else if(maxplayers >= 50) {
			//3 Columns 20 rows
			connector.settablesize(3, 20);
		} else {
			//1 Columns 20 rows
			connector.settablesize(1, 20);
		}
	}
	
}
