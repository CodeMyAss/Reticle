package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sql.rowset.serial.SerialException;

import org.spigot.reticle.events.JoinGameEvent;

public class JoinGamePacket extends AbstractPacket {
	public static final int ID=0x1;
	private packet reader;
	
	public JoinGamePacket(ByteBuffer sock,packet reader) {
		this.reader=reader;
		this.reader.input=sock;
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
		if(reader.ProtocolVersion>=47) {
			boolean rdebug=reader.readBoolean();
			return new JoinGameEvent(reader.bot,id, gm, dim, diff, maxplayers, leveltype, rdebug);
		}
		return new JoinGameEvent(reader.bot,id, gm, dim, diff, maxplayers, leveltype);
	}
	
}
