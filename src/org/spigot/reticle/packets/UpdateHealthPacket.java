package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sql.rowset.serial.SerialException;

import org.spigot.reticle.events.UpdateHealthEvent;

public class UpdateHealthPacket extends packet {
	public static final int ID = 0x06;
	private packet reader;

	public UpdateHealthPacket(packet pack, ByteBuffer buf) {
		this.reader = pack;
		this.reader.input = buf;
	}
	
	public UpdateHealthEvent Read() throws IOException, SerialException {
		float h=reader.readFloat();
		int food=reader.readVarInt();
		float s=reader.readFloat();
		return new UpdateHealthEvent(h,food,s);
	}
	
}
