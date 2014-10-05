package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sql.rowset.serial.SerialException;

public class PlayerStreamLoginStartPacket extends AbstractPacket {
	private packet reader;
	private String username;

	public PlayerStreamLoginStartPacket(ByteBuffer buf, packet pack) {
		pack.input = buf;
		this.reader = pack;
	}
	
	public String Read() throws SerialException, IOException {
		username=reader.readString();
		return username;
	}
	
	public void WriteLoginSuccess(String xUUID, String xusername) throws IOException {
		reader.setOutputStream(1+reader.getStringLength(xUUID)+reader.getStringLength(xusername));
		reader.writeVarInt(2);
		reader.writeString(xUUID);
		reader.writeString(xusername);
		reader.Send();
	}
}
