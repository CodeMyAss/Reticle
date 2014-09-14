package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sql.rowset.serial.SerialException;

public class KeepAlivePacket extends packet {
	public static final int ID = 0;

	private packet reader;
	private int code;

	private int protocolversion;

	public KeepAlivePacket(packet reader, int protocolversion, ByteBuffer buff) {
		this.reader = reader;
		this.reader.input = buff;
		this.protocolversion = protocolversion;
	}

	public void Read() throws IOException, SerialException {
		if (protocolversion >= 47) {
			code=reader.readVarInt();
		} else {
			code=reader.readInt();
		}
	}

	public void Write() throws IOException {
		// Packet id
		reader.writeVarInt(KeepAlivePacket.ID);
		if (protocolversion >= 47) {
			reader.setOutputStream(reader.getVarntCount(KeepAlivePacket.ID) + (reader.getVarntCount(code)));
			reader.writeVarInt(ID);
			reader.writeVarInt(code);
		} else {
			reader.setOutputStream(reader.getVarntCount(KeepAlivePacket.ID) + 4);
			reader.writeVarInt(ID);
			reader.writeInt(code);
		}
		reader.Send();
	}
}
