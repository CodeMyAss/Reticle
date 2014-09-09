package org.spigot.mcbot.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sql.rowset.serial.SerialException;

import org.spigot.mcbot.events.JoinGameEvent;

public class JoinGamePacket extends packet {
	private ByteBuffer sock;
	public static final int ID=1;
	
	public JoinGamePacket(ByteBuffer sock) {
		this.sock=sock;
	}
	
	public JoinGameEvent Read() throws IOException, SerialException {
		super.input=sock;
		//Our entity ID
		int id=super.readInt();
		//Our gamemode
		byte gm=super.readByte();
		//Our dimension (world)
		byte dim=super.readByte();
		//Difficulty
		byte diff=super.readByte();
		//Max players
		Byte maxplayers=super.readByte();
		//Level type
		String leveltype=super.readString();
		return new JoinGameEvent(id, gm, dim, diff, maxplayers, leveltype);
	}
	
}
