package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sql.rowset.serial.SerialException;

import org.spigot.reticle.events.SignUpdateEvent;

public class SignUpdatePacket extends AbstractPacket {
	public static final int ID = 0x33;
	private packet reader;

	public SignUpdatePacket(ByteBuffer buf, packet reader) {
		reader.input = buf;
		this.reader = reader;
	}


	public SignUpdateEvent Read() throws SerialException, IOException {
		int x = 0;
		int y = 0;
		int z = 0;
		String[] lines = new String[4];
		if (reader.ProtocolVersion >= 47) {
			long val = reader.readLong();
			x = (int) (val >> 38);
			y = (int) ((val >> 26) & 0xFFF);
			z = (int) (val << 38 >> 38);
			lines[0] = reader.readString();
			lines[1] = reader.readString();
			lines[2] = reader.readString();
			lines[3] = reader.readString();
		} else {
			x = reader.readInt();
			y = reader.readShort();
			z = reader.readInt();
			lines[0] = reader.readString();
			lines[1] = reader.readString();
			lines[2] = reader.readString();
			lines[3] = reader.readString();
		}
		return new SignUpdateEvent(reader.bot, x, y, z, lines);
	}
}
