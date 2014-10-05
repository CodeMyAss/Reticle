package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sql.rowset.serial.SerialException;

import org.spigot.reticle.coresp.ChunkDataInfo;

public class ChunkDataPacket extends AbstractPacket {
	public static final int ID = 0x21;
	private packet reader;

	public ChunkDataPacket(ByteBuffer buf, packet reader) {
		reader.input = buf;
		this.reader = reader;
	}

	/**
	 * @return Returns True if the packet should be added/updated, False if
	 *         deleted
	 * @throws IOException
	 * @throws SerialException
	 */
	public ChunkDataInfo Read() throws SerialException, IOException {
		int x = reader.readInt();
		int z = reader.readInt();
		boolean a = reader.readBoolean();
		byte map = reader.readByte();
		return new ChunkDataInfo(x, z, !a || map != 0);
	}

}
