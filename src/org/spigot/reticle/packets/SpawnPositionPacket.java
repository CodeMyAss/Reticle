package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sql.rowset.serial.SerialException;

public class SpawnPositionPacket extends AbstractPacket {
	public static final int ID = 0x5;
	private packet reader;

	public SpawnPositionPacket(ByteBuffer sock,packet reader) {
		this.reader=reader;
		this.reader.input = sock;
	}

	public void Read() throws IOException, SerialException {
		if (reader.ProtocolVersion >= 47) {
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
