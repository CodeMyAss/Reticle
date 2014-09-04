package org.spigot.mcbot.packets;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class Ignored_Packet extends packet {
	private Socket sock;
	private int len;

	public Ignored_Packet(int id, int len, Socket sock) throws IOException {
		this.len = len;
		this.sock = sock;
		// super.Send(sock.getOutputStream());
	}

	public void Read() throws IOException {
		super.input = sock.getInputStream();
		super.readAndIgnore(len);
	}

	public Ignored_Packet(int len, InputStream input) throws IOException {
		super(len, input);
	}

}
