package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

public class PlayerStreamPlayerPositionAndLookPacket extends AbstractPacket {

	private packet reader;

	public PlayerStreamPlayerPositionAndLookPacket(ByteBuffer buf, packet pack) {
		pack.input = buf;
		this.reader = pack;
	}

	public void Write(double x, double y, double z, float p, float ya, boolean og) throws IOException {
		reader.setOutputStream(34);
		reader.writeVarInt(8);
		reader.writeDouble(x);
		reader.writeDouble(y);
		reader.writeDouble(z);
		reader.writeFloat(p);
		reader.writeFloat(ya);
		reader.writeBoolean(og);
		reader.Send();
	}
}
