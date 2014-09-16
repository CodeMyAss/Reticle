package org.spigot.reticle.events;

public class PlayerPositionAndLookEvent extends event {
	private double x;
	private double y;
	private double z;
	private float yaw;
	private float pitch;
	private byte flags;

	public PlayerPositionAndLookEvent(double x, double y, double z, float yaw, float pitch, byte flags) {
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
