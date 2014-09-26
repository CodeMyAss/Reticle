package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sql.rowset.serial.SerialException;

public class MapChunkBulkPacket extends AbstractPacket {
	public static final int ID = 0x26;
	private packet reader;
    int x;
    int z;

	public MapChunkBulkPacket(ByteBuffer buf, packet reader) {
		reader.input = buf;
		this.reader = reader;
	}

	@SuppressWarnings("unused")
	public void Read() throws SerialException, IOException {
		x = reader.readInt();
		z = reader.readInt();
		boolean isGroundUp = reader.readBoolean();
		short a = reader.readShort();
		int bitmask = a & 0xffff;
		short as =reader.readShort();
		int addmask = as & 0xffff;
		int datalength = reader.readInt();
		byte[] data = reader.readBytes(datalength);
	}
}
