package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

public class PlayerStreamSpawnPositionPacket extends AbstractPacket {

	private packet reader;

	public PlayerStreamSpawnPositionPacket(ByteBuffer buf, packet pack) {
		pack.input = buf;
		this.reader = pack;
	}
	
	public void Write(int x, int y, int z) throws IOException {
		reader.setOutputStream(13);
		reader.writeVarInt(5);
		reader.writeInt(x);
		reader.writeInt(y);
		reader.writeInt(z);
		reader.Send();
	}
}
