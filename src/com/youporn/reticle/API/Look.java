package org.spigot.reticle.API;

public class Look {
	private float pitch;
	private float yaw;

	public Look(float Pitch, float Yaw) {
		this.pitch=Pitch;
		this.yaw=Yaw;
	}
	
	public float getPitch() {
		return pitch;
	}
	
	public float getYaw() {
		return yaw;
	}
}
