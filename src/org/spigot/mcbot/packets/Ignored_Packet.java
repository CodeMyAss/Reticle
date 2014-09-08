package org.spigot.mcbot.packets;

import java.io.IOException;
import java.io.InputStream;

public class Ignored_Packet extends packet {
	private InputStream input;
	private int len;
	public Ignored_Packet(int len, int id, InputStream input) throws IOException {
		this.len = len - super.getVarntCount(id);
		this.input = input;
	}

	public void Read() throws IOException {
		super.input = input;
			super.readAndIgnore(len);

	}
}
