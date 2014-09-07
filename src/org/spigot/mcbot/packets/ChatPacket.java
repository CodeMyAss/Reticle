package org.spigot.mcbot.packets;

import java.io.IOException;
import java.net.Socket;

import org.spigot.mcbot.events.ChatEvent;

public class ChatPacket extends packet {
	private Socket sock;

	public ChatPacket(Socket sock) {
		this.sock = sock;
	}
	
	public ChatEvent Read() throws IOException {
		super.input=sock.getInputStream();
		//The string itself
		String chat=super.readString();
		//Position byte
		//super.readByte();
		return new ChatEvent(chat);
	}
	
	public void Write(String message)  throws IOException {
		super.setOutputStream(super.getStringLength(message)+super.getVarntCount(1));
		//Packet ID
		super.writeVarInt(1);
		super.writeString(message);
		super.Send(sock.getOutputStream());
	}
	
}
