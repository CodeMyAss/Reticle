package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sql.rowset.serial.SerialException;

import org.spigot.reticle.coresp.PlayerStreamHandshakeResponse;

public class PlayerStreamHandShakePacket extends AbstractPacket {
	private packet reader;

	public PlayerStreamHandShakePacket(ByteBuffer buf, packet pack) {
		pack.input=buf;
		this.reader=pack;
	}
	
	public PlayerStreamHandshakeResponse Read() throws SerialException, IOException {
		int ver=reader.readVarInt();
		//Server address
		reader.readString();
		//Server port
		reader.readShort();
		int state=reader.readVarInt();
		return new PlayerStreamHandshakeResponse(ver,state);
	}
	
	public void WriteStatus(String status) throws IOException {
		reader.setOutputStream(1+reader.getStringLength(status));
		reader.writeVarInt(0);
		reader.writeString(status);
		reader.Send();
	}
}

