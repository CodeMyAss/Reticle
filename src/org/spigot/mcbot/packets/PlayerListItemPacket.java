package org.spigot.mcbot.packets;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

public class PlayerListItemPacket extends packet {
	public static final int ID=56;
	private ByteBuffer sock;
	private String name;
	private boolean online;

	public PlayerListItemPacket(ByteBuffer sock) {
		this.sock = sock;
		
	}

	public void Read() throws IOException {
		super.input = sock;
		name = super.readString();
		online = super.readBoolean();
		//Ping
		super.readShort();
	}
	
	// Meaning of true return value is to update tablist
	public boolean Serve(List<String> tablist) {
		if(tablist.contains(name)) {
			//We are already in tablist
			if(online) {
				//And online (Correct)
			} else {
				//Bot not online (Suicide)
				tablist.remove(name);
				return true;
			}
		} else {
			//We are not in tablist yet
			if(online) {
				//But online (Must add)
				tablist.add(name);
				return true;
			} else {
				//And not online (correct)
			}
		}
		return false;
	}
}
