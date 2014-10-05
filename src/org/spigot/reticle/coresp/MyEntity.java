package org.spigot.reticle.coresp;

import org.spigot.reticle.packets.packetStruct;

public class MyEntity {
	public double x;
	public double y;
	public double z;
	public float yaw;
	public float pitch;
	public String UUID;
	public String Username;
	public int EntityId;
	public byte Gamemode;
	public byte Difficulty;
	public byte Dimension;
	public byte MaxPlayers;
	public String levelType;
	public boolean onGround;
	public byte AbilitiesFlag;
	public float WalkSpeed;
	public float FlyingSpeed;
	public packetStruct AbilitiesPacket;
}
