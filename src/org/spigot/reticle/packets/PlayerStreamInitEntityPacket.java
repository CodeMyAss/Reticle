package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

public class PlayerStreamInitEntityPacket extends AbstractPacket {
	private packet reader;

	public PlayerStreamInitEntityPacket(ByteBuffer buf, packet reader) {
		this.reader = reader;
		this.reader.input = buf;
	}
	
	public void Write(int entity) throws IOException {
		reader.setOutputStream(5);
		reader.writeVarInt(0x14);
		reader.writeInt(entity);
		reader.Send();
	}

}
