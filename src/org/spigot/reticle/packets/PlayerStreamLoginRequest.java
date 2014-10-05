package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sql.rowset.serial.SerialException;

public class PlayerStreamLoginRequest extends AbstractPacket {
	private packet reader;
	private long ping;

	public PlayerStreamLoginRequest(ByteBuffer buf,packet pack) {
		pack.input=buf;
		this.reader=pack;
	}
	
	
	public void Read() throws SerialException, IOException {
		ping=reader.readLong();
	}
	
	public void Write() throws IOException {
		reader.setOutputStream(9);
		reader.writeVarInt(1);
		reader.writeLong(ping);
		reader.Send();
	}
}
