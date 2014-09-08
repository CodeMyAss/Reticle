package org.spigot.mcbot.packets;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.spigot.mcbot.events.TeamEvent;

public class TeamPacket extends packet {

	private Socket sock;

	public TeamPacket(Socket sock) {
		this.sock = sock;
	}

	public TeamEvent Read(int len) throws IOException {
		super.input = sock.getInputStream();
		String teamdisplayname = null, prefix = null, suffix = null;
		String nametag = null;
		byte ffire = 0, color = 0;
		List<String> players=new ArrayList<String>();
		String teamname = super.readString();
		byte mode = super.readByte();
		switch (mode) {
			case 0: // Team is created
				teamdisplayname = super.readString();
				prefix = super.readString();
				suffix = super.readString();
				ffire = super.readByte();
				color = super.readByte();
				int pcount = super.readVarInt();
				for (int i = 0; i < pcount; i++) {
					players.add(super.readString());
				}
			break;

			case 2: // Team is being updated
				teamdisplayname = super.readString();
				prefix = super.readString();
				suffix = super.readString();
				color = super.readByte();
			break;

			case 3: // New players are added
				int pcount0 = super.readVarInt();
				for (int i = 0; i < pcount0; i++) {
					players.add(super.readString());
				}
			break;

			case 4: // Players are removed from team
				int pcount1 = super.readVarInt();
				for (int i = 0; i < pcount1; i++) {
					players.add(super.readString());
				}
			break;
		}
		return new TeamEvent(teamname, mode, prefix, suffix, teamdisplayname, ffire, nametag, color, players);
	}

}
