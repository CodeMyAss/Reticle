package org.spigot.mcbot.packets;

import java.io.IOException;
import java.net.Socket;

import org.spigot.mcbot.sockets.connector;

public class LoginSuccessPacket extends packet {
	connector con;
	public static final int ID=2;
	
	public LoginSuccessPacket(Socket sock, connector connector) throws IOException {
		super.sockinput=sock.getInputStream();
		this.con=connector;
	}

	public void read() throws IOException {
		//Length
		super.readInnerVarInt();
		//Pid
		super.readInnerVarInt();
		//UUID
		String uuid = super.readInnerString();
		//Username		
		super.readInnerString();
		con.sendmessage("§bReceived UUID: §2§n"+uuid);
	}
}
