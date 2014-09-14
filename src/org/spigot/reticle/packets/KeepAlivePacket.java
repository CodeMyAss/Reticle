package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sql.rowset.serial.SerialException;

public class KeepAlivePacket extends packet {
	public static final int ID = 0;

	private packet reader;

	private int protocolversion;

	public KeepAlivePacket(packet reader, int protocolversion, ByteBuffer buff) {
		this.reader = reader;
		this.reader.input = buff;
		this.protocolversion = protocolversion;
	}

	public int Read(int len) throws IOException, SerialException {
		if (protocolversion >= 47) {
			return reader.readVarInt();
		} else {
			return reader.readShort();
		}
		// return reader.readBytes(len);
	}

	public void Write(int i) throws IOException {
		// Packet id
		reader.writeVarInt(KeepAlivePacket.ID);
		if (protocolversion >= 47) {
			reader.setOutputStream(reader.getVarntCount(KeepAlivePacket.ID) + (reader.getVarntCount(i)));
			reader.writeVarInt(i);
		} else {
			reader.setOutputStream(reader.getVarntCount(KeepAlivePacket.ID) + 2);
			reader.writeShort((short) i);
		}
		reader.Send();
	}
}
