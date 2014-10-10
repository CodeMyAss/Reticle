package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;
import javax.sql.rowset.serial.SerialException;

import org.spigot.reticle.events.TabCompleteEvent;

public class TabCompletePacket extends AbstractPacket {
	private packet reader;
	public static final int ID = 0x3A;
	public static final int ID_OUT = 0x14;

	public TabCompletePacket(packet reader, ByteBuffer buf) {
		reader.input = buf;
		this.reader = reader;
	}

	public TabCompleteEvent Read() throws SerialException, IOException {
		int count = reader.readVarInt();
		String[] names = new String[count];
		for (int i = 0; i < count; i++) {
			names[i] = reader.readString();
		}
		return new TabCompleteEvent(reader.bot,names);
	}

	public void Write(String name) throws IOException {
		if (reader.ProtocolVersion >= 47) {
			reader.setOutputStream(reader.getVarIntCount(ID_OUT) + 1 + reader.getStringLength(name));
			reader.writeVarInt(ID_OUT);
			reader.writeString(name);
			reader.writeBoolean(false);
		} else {
			reader.setOutputStream(reader.getVarIntCount(ID_OUT) + reader.getStringLength(name));
			reader.writeVarInt(ID_OUT);
			reader.writeString(name);
		}
		reader.Send();
	}
}
