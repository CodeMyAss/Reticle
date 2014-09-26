package org.spigot.reticle.events;

import org.spigot.reticle.botfactory.mcbot;

public class UpdateHealthEvent extends Event {
	private final float health;
	private final int food;
	private final float satur;
	
	
	public UpdateHealthEvent(mcbot bot,float health,int food, float satur) {
		super(bot);
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
