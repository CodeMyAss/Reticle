package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import javax.sql.rowset.serial.SerialException;

public class PlayerListItemPacket extends packet {
	public static final int ID = 0x38;
	private ByteBuffer sock;
	private String name;
	private boolean online;
	private int protocolversion;

	public PlayerListItemPacket(ByteBuffer sock, int protocolversion) {
		this.sock = sock;
		this.protocolversion = protocolversion;

	}

	public void Read() throws IOException, SerialException {
		super.input = sock;
		if (protocolversion >= 47) {
TODO
			
		} else {
			name = super.readString();
			online = super.readBoolean();
			// Ping
			super.readShort();
		}
	}

	// Meaning of true return value is to update tablist
	public boolean Serve(List<String> tablist) {
		if (tablist.contains(name)) {
			// We are already in tablist
			if (online) {
				// And online (Correct)
			} else {
				// Bot not online (Suicide)
				tablist.remove(name);
				return true;
			}
		} else {
			// We are not in tablist yet
			if (online) {
				// But online (Must add)
				tablist.add(name);
				return true;
			} else {
				// And not online (correct)
			}
		}
		return false;
	}
}
