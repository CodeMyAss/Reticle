package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sql.rowset.serial.SerialException;

public class LoginSuccessPacket extends packet {
	private ByteBuffer sock;
	public static final int ID = 0x2;

	public LoginSuccessPacket(ByteBuffer sock, int protocolversion) throws IOException {
		this.sock = sock;
	}

	public String Read() throws IOException, SerialException {
		super.input = sock;
		// UUID
		String uuid = super.readString();
		// Username
		super.readString();
		return uuid;
	}
}
