package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sql.rowset.serial.SerialException;

public class EntityPacket extends AbstractPacket {
	public static final int ID=0x14;
	private packet reader;

	public EntityPacket(ByteBuffer buf, packet reader) {
		reader.input = buf;
		this.reader = reader;
	}
	
	
	public int Read() throws SerialException, IOException {
		if(reader.ProtocolVersion>=47) {
			return reader.readVarInt();
		} else {
			return reader.readInt();
		}
	}
}
