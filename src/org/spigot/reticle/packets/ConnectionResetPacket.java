package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sql.rowset.serial.SerialException;



public class ConnectionResetPacket extends packet {
	private packet reader;
	public static final int ID=0x40;
	
	public ConnectionResetPacket(ByteBuffer s,packet reader) {
		reader.input=s;
		this.reader=reader;
	}
	
	public String Read() throws IOException, SerialException {
		return reader.readString();
	}
}
