package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sql.rowset.serial.SerialException;

import org.spigot.reticle.events.ChatEvent;

public class ChatPacket extends AbstractPacket {
	private ByteBuffer buff;
	private packet reader;

	public static final int ID = 0x2;
	public static final int ID_out = 0x1;

	public ChatPacket(ByteBuffer buff, packet pack) {
		this.reader=pack;
		this.buff = buff;
	}

	public ChatEvent Read() throws IOException, SerialException {
		reader.input = buff;
		String msg=reader.readString();
		if(reader.ProtocolVersion>=47) {
			byte pos=reader.readByte();
			return new ChatEvent(reader.bot,msg,pos,false);
		}
		return new ChatEvent(reader.bot,msg,false);
	}

	public void Write(String message) throws IOException {
		reader.setOutputStream(reader.getStringLength(message) + reader.getVarntCount(ChatPacket.ID_out));
		// Packet ID
		reader.writeVarInt(ChatPacket.ID_out);
		reader.writeString(message);
		reader.Send();
	}

}
