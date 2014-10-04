package org.spigot.reticle.packets;

import java.io.IOException;

public class Ignored_Packet extends AbstractPacket {
	private int len;
	private packet reader;
	public Ignored_Packet(int len, packet reader) throws IOException {
		this.len = len;
		this.reader=reader;
	}

	public void Read() throws IOException {
		reader.readAndIgnore(len);
	}
}
