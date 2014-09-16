package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sql.rowset.serial.SerialException;

public class EntityStatusPacket extends packet {
	public static final int ID = 0x1A;
	private packet reader;

	public EntityStatusPacket(packet packet, ByteBuffer buf) {
		this.reader = packet;
		this.reader.input = buf;
	}

	public byte Read() throws SerialException, IOException {
		// Entity id
		reader.readInt();
		byte status = reader.readByte();
		return status;
	}
}
