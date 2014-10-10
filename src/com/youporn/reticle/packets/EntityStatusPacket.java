package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sql.rowset.serial.SerialException;

import org.spigot.reticle.events.EntityStatusEvent;

public class EntityStatusPacket extends AbstractPacket {
	public static final int ID = 0x1A;
	private packet reader;

	public EntityStatusPacket(packet packet, ByteBuffer buf) {
		this.reader = packet;
		this.reader.input = buf;
	}

	public EntityStatusEvent Read() throws SerialException, IOException {
		int id=reader.readInt();
		byte status = reader.readByte();
		return new EntityStatusEvent(reader.bot,status,id);
	}
}
