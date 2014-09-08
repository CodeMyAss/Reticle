package org.spigot.mcbot.packets;

import java.io.IOException;
import java.net.Socket;

import org.spigot.mcbot.sockets.connector;

public class LoginSuccessPacket extends packet {
	connector con;
	public static final int ID=2;
	
	public LoginSuccessPacket(Socket sock, connector connector) throws IOException {
		super(sock.getInputStream());
		this.con=connector;
	}

	public void read() throws IOException {
		//Length
		super.readVarInt();
		//Pid
		super.readVarInt();
		//UUID
		String uuid = super.readString();
		//Username		
		super.readString();
		con.sendmessage("§bReceived UUID: §2§n"+uuid);
	}
}
