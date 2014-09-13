package org.spigot.reticle.packets;

import java.io.IOException;
import java.io.InputStream;

public class Ignored_Packet extends packet {
	private int len;
	public Ignored_Packet(int len, int id, InputStream input) throws IOException {
		this.len = len - super.getVarntCount(id);
		super.sockinput = input;
	}

	public void Read() throws IOException {
		super.readAndIgnore(len);

	}
}
