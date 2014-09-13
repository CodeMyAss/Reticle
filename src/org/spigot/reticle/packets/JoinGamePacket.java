package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sql.rowset.serial.SerialException;

import org.spigot.reticle.events.JoinGameEvent;

public class JoinGamePacket extends packet {
	private ByteBuffer sock;
	public static final int ID=0x1;
	private int protocolversion;
	
	public JoinGamePacket(ByteBuffer sock,int protocolversion) {
		this.sock=sock;
		this.protocolversion=protocolversion;
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
		if(protocolversion>=47) {
			boolean rdebug=super.readBoolean();
			return new JoinGameEvent(id, gm, dim, diff, maxplayers, leveltype, rdebug);
		}
		return new JoinGameEvent(id, gm, dim, diff, maxplayers, leveltype);
	}
	
}
