package org.spigot.reticle.events;

import org.spigot.reticle.botfactory.mcbot;

public class PlayerPositionAndLookEvent extends Event {
	private final double x;
	private final double y;
	private final double z;
	private final float yaw;
	private final float pitch;
	private final byte flags;

	public PlayerPositionAndLookEvent(mcbot bot,double x, double y, double z, float yaw, float pitch, byte flags) {
		super(bot);
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
		this.flags = flags;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public byte getFlags() {
		return flags;
	}

}
