package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.spigot.reticle.coresp.MyEntity;

public class PlayerStreamRespawnPacket  extends AbstractPacket {

	private packet reader;

	public PlayerStreamRespawnPacket(ByteBuffer buf, packet pack) {
		pack.input = buf;
		this.reader = pack;
	}

	public void Write(MyEntity entity) throws IOException {
		reader.setOutputStream(7+reader.getStringLength(entity.levelType));
		reader.writeVarInt(7);
		reader.writeInt(entity.Dimension);
		reader.writeByte(entity.Difficulty);
		reader.writeByte(entity.Gamemode);
		reader.writeString(entity.levelType);
		reader.Send();
	}
}
