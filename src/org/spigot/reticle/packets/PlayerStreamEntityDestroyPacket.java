package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

public class PlayerStreamEntityDestroyPacket extends AbstractPacket {
	private packet reader;

	public PlayerStreamEntityDestroyPacket(ByteBuffer buf, packet reader) {
		this.reader = reader;
		this.reader.input = buf;
	}

	public void Write(int[] entityid) throws IOException {
		if (entityid.length > 0) {
			byte bytelen=(byte) (entityid.length & 0xff);
			reader.setOutputStream(4 * entityid.length + 2);
			reader.writeVarInt(0x13);
			reader.writeByte(bytelen);
			for (int ent : entityid) {
				reader.writeInt(ent);
			}
			reader.Send();
		}
	}

}
