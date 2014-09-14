package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.serial.SerialException;

import org.spigot.reticle.events.TeamEvent;

public class TeamPacket extends packet {
	public static final int ID = 0x3E;
	private int protocolversion;
	private packet reader;

	public TeamPacket(ByteBuffer buffer,packet reader, int protocolversion) {
		this.reader=reader;
		this.reader.input = buffer;
		this.protocolversion = protocolversion;
	}

	public TeamEvent Read() throws IOException, SerialException {
		String teamdisplayname = null, prefix = null, suffix = null;
		String nametag = null;
		byte ffire = 0, color = 0;
		List<String> players = new ArrayList<String>();
		String teamname = reader.readString();
		byte mode = reader.readByte();
		switch (mode) {
			case 0: // Team is created
				teamdisplayname = reader.readString();
				prefix = reader.readString();
				suffix = reader.readString();
				ffire = reader.readByte();
				int pcount;
				if (protocolversion >= 47) {
					nametag = reader.readString();
					color = reader.readByte();
					pcount = reader.readVarInt();
				} else {
					pcount = reader.readShort();
				}
				for (int i = 0; i < pcount; i++) {
					players.add(reader.readString());
				}
			break;

			case 2: // Team is being updated
				teamdisplayname = reader.readString();
				prefix = reader.readString();
				suffix = reader.readString();
				ffire = reader.readByte();
				if (protocolversion >= 47) {
					nametag = reader.readString();
					color = reader.readByte();
				}
			break;

			case 3: // New players are added
				int pcount0;
				if (protocolversion >= 47) {
					pcount0 = reader.readVarInt();
				} else {
					pcount0 = reader.readShort();
				}
				for (int i = 0; i < pcount0; i++) {
					players.add(reader.readString());
				}
			break;

			case 4: // Players are removed from team
				int pcount1;
				if (protocolversion >= 47) {
					pcount1 = reader.readVarInt();
				} else {
					pcount1 = reader.readShort();
				}
				pcount1 = reader.readShort();
				for (int i = 0; i < pcount1; i++) {
					players.add(reader.readString());
				}
			break;
		}
		return new TeamEvent(teamname, mode, prefix, suffix, teamdisplayname, ffire, nametag, color, players);
	}

}
