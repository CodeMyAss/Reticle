package org.spigot.mcbot.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sql.rowset.serial.SerialException;



public class ConnectionResetPacket extends packet {
	private ByteBuffer input;
	public static final int ID=64;
	
	public ConnectionResetPacket(ByteBuffer s) {
		this.input=s;
	}
	
	public String read() throws IOException, SerialException {
		super.input=input;
		return super.readString();
	}
}
