package org.spigot.reticle.packets;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

import javax.sql.rowset.serial.SerialException;

import org.spigot.reticle.events.ChatEvent;

public class ChatPacket extends packet {
	private Socket sock;
	private ByteBuffer buff;

	public static final int ID = 0x2;
	public static final int ID_out = 0x1;
	private int protocolversion;

	public ChatPacket(ByteBuffer buff, Socket sock, int protocolversion) {
		this.sock = sock;
		this.buff = buff;
		this.protocolversion=protocolversion;
	}

	public ChatEvent Read() throws IOException, SerialException {
		super.input = buff;
		String msg=super.readString();
		if(protocolversion>=47) {
			byte pos=super.readByte();
			return new ChatEvent(msg,pos);
		}
		return new ChatEvent(msg);
	}

	public void Write(String message) throws IOException {
		super.setOutputStream(super.getStringLength(message) + super.getVarntCount(ChatPacket.ID_out));
		// Packet ID
		super.writeVarInt(ChatPacket.ID_out);
		super.writeString(message);
		super.Send(sock.getOutputStream());
	}

}
