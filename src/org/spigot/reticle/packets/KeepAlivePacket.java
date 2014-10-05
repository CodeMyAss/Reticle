package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sql.rowset.serial.SerialException;

public class KeepAlivePacket extends AbstractPacket {
	public static final int ID = 0;

	private packet reader;
	private int code;


	public KeepAlivePacket(packet reader, ByteBuffer buff) {
		this.reader = reader;
		this.reader.input = buff;
	}

	public void Read() throws IOException, SerialException {
		if (reader.ProtocolVersion >= 47) {
			code=reader.readVarInt();
		} else {
			code=reader.readInt();
		}
	}

	public void Write() throws IOException {
		// Packet id
		reader.writeVarInt(KeepAlivePacket.ID);
		if (reader.ProtocolVersion >= 47) {
			reader.setOutputStream(reader.getVarIntCount(KeepAlivePacket.ID) + (reader.getVarIntCount(code)));
			reader.writeVarInt(ID);
			reader.writeVarInt(code);
		} else {
			reader.setOutputStream(reader.getVarIntCount(KeepAlivePacket.ID) + 4);
			reader.writeVarInt(ID);
			reader.writeInt(code);
		}
		reader.Send();
	}
}
