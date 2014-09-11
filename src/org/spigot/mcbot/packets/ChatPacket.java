package org.spigot.mcbot.packets;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

import javax.sql.rowset.serial.SerialException;

import org.spigot.mcbot.events.ChatEvent;

public class ChatPacket extends packet {
	private Socket sock;
	private ByteBuffer buff;

	public static final int ID = 2;
	public static final int ID_out = 1;

	public ChatPacket(ByteBuffer buff, Socket sock) {
		this.sock = sock;
		this.buff = buff;
	}

	public ChatEvent Read() throws IOException, SerialException {
		super.input = buff;
		return new ChatEvent(super.readString());
	}

	public void Write(String message) throws IOException {
		super.setOutputStream(super.getStringLength(message) + super.getVarntCount(ChatPacket.ID_out));
		// Packet ID
		super.writeVarInt(ChatPacket.ID_out);
		super.writeString(message);
		super.Send(sock.getOutputStream());
	}

}
