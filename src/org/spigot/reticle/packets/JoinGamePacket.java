package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sql.rowset.serial.SerialException;

import org.spigot.reticle.events.JoinGameEvent;

public class JoinGamePacket extends packet {
	public static final int ID=0x1;
	private int protocolversion;
	private packet reader;
	
	public JoinGamePacket(ByteBuffer sock,packet reader,int protocolversion) {
		reader.input=sock;
		this.protocolversion=protocolversion;
		this.reader=reader;
	}
	
	public JoinGameEvent Read() throws IOException, SerialException {
		//Our entity ID
		int id=reader.readInt();
		//Our gamemode
		byte gm=reader.readByte();
		//Our dimension (world)
		byte dim=reader.readByte();
		//Difficulty
		byte diff=reader.readByte();
		//Max players
		Byte maxplayers=reader.readByte();
		//Level type
		String leveltype=reader.readString();
		if(protocolversion>=47) {
			boolean rdebug=reader.readBoolean();
			return new JoinGameEvent(id, gm, dim, diff, maxplayers, leveltype, rdebug);
		}
		return new JoinGameEvent(id, gm, dim, diff, maxplayers, leveltype);
	}
	
}
