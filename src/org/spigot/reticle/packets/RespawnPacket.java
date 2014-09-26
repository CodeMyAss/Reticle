package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sql.rowset.serial.SerialException;

public class RespawnPacket extends AbstractPacket {
	public static final int ID=0x7;
	private packet reader;

	public RespawnPacket(ByteBuffer sock, packet reader) {
		this.reader=reader;
		this.reader.input=sock;
	}

	public void Read() throws IOException, SerialException {
		//Dimension
		reader.readInt();
		//Difficulty
		reader.readByte();
		//Gamemode
		reader.readByte();
		//Level name
		reader.readString();
	}
}
