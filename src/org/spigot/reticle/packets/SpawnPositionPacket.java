package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sql.rowset.serial.SerialException;

public class SpawnPositionPacket extends packet {
	public static final int ID = 0x5;
	private int protocolversion;
	private packet reader;

	public SpawnPositionPacket(ByteBuffer sock,packet reader, int protocolversion) {
		this.reader=reader;
		this.reader.input = sock;
		this.protocolversion = protocolversion;
	}

	public void Read() throws IOException, SerialException {
		if (protocolversion >= 47) {
			reader.readLong();
		} else {
			// X
			reader.readInt();
			// Y
			reader.readInt();
			// Z
			reader.readInt();
		}
	}
}
