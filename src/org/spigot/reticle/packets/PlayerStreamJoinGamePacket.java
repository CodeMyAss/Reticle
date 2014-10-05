package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

public class PlayerStreamJoinGamePacket extends AbstractPacket {

	private packet reader;

	public PlayerStreamJoinGamePacket(ByteBuffer buf, packet pack) {
		pack.input = buf;
		this.reader = pack;
	}

	public void Write(int entityid, byte gm, byte dim, byte dif, byte mp, String lvl) throws IOException {
		int len=9 + reader.getStringLength(lvl);
		reader.setOutputStream(len);
		reader.writeVarInt(1);
		reader.writeInt(entityid);
		reader.writeByte(gm);
		reader.writeByte(dim);
		reader.writeByte(dif);
		reader.writeByte(mp);
		reader.writeString(lvl);
		reader.Send();
	}

}
