package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sql.rowset.serial.SerialException;

public class SetCompressionPacket extends packet {
	
	public static final int ID=0x46;

	private ByteBuffer buff;
	
	public SetCompressionPacket(ByteBuffer buf) {
		this.buff=buf;
	}
	
	public int Read() throws SerialException, IOException {
		super.input=buff;
		int treshhold=super.readVarInt();
		return treshhold;
	}
	
}
