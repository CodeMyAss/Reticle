package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import javax.sql.rowset.serial.SerialException;

public class LoginSuccessPacket extends packet {
	public static final int ID = 0x2;
	private packet reader;

	public LoginSuccessPacket(ByteBuffer sock,packet reader, int protocolversion) throws IOException {
		this.reader=reader;
		this.reader.input=sock;
	}

	public String[] Read() throws IOException, SerialException {
		// UUID
		String uuid = reader.readString();
		// Username
		String username = null;
		try {
		username=reader.readString();
		} catch (BufferUnderflowException e) {
		}
		return new String[] {uuid,username};
	}
}
