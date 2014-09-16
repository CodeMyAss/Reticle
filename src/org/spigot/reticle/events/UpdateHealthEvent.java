package org.spigot.reticle.events;

public class UpdateHealthEvent extends event {
	private final float health;
	private final int food;
	private final float satur;
	
	
	public UpdateHealthEvent(float health,int food, float satur) {
		this.satur=satur;
		this.food=food;
		this.health=health;
	}
	
	public float getHealth() {
		return health;
	}
	
	public int getFood() {
		return food;
	}
	
	public float getSaturation() {
		return satur;
	}
	
	
}
