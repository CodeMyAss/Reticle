package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sql.rowset.serial.SerialException;

import org.spigot.reticle.events.ChatEvent;

public class ChatPacket extends packet {
	private ByteBuffer buff;
	private packet reader;

	public static final int ID = 0x2;
	public static final int ID_out = 0x1;
	private int protocolversion;

	public ChatPacket(ByteBuffer buff, packet pack, int protocolversion) {
		this.reader=pack;
		this.buff = buff;
		this.protocolversion=protocolversion;
	}

	public ChatEvent Read() throws IOException, SerialException {
		reader.input = buff;
		String msg=reader.readString();
		if(protocolversion>=47) {
			byte pos=reader.readByte();
			return new ChatEvent(msg,pos);
		}
		return new ChatEvent(msg);
	}

	public void Write(String message) throws IOException {
		reader.setOutputStream(reader.getStringLength(message) + reader.getVarntCount(ChatPacket.ID_out));
		// Packet ID
		reader.writeVarInt(ChatPacket.ID_out);
		reader.writeString(message);
		reader.Send();
	}

}
