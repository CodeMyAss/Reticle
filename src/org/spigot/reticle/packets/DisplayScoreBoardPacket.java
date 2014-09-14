package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sql.rowset.serial.SerialException;

public class DisplayScoreBoardPacket extends packet {
	public static final int ID=0x3D;
	private packet reader;

	public DisplayScoreBoardPacket(ByteBuffer sock,packet reader) {
		this.reader=reader;
		reader.input=sock;
	}
	
	public void Read() throws IOException, SerialException {
		//Pos
		reader.readByte();
		//Scoreboard name
		reader.readString();
	}
}
