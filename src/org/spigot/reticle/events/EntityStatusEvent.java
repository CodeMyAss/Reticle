package org.spigot.reticle.events;

import org.spigot.reticle.botfactory.mcbot;

public class EntityStatusEvent extends Event {
	private final STATUS status;
	private final int id;

	public EntityStatusEvent(mcbot bot,byte status, int id) {
		super(bot);
		this.status=STATUS.values()[status];
		this.id=id;
	}
	
	public int getEntityId() {
		return id;
	}
	
	public STATUS getStatus() {
		return status;
	}
	
	
	public enum STATUS {
		RELATED_TO_LIVING_ENTITIES(0),
		RELATED_TO_PLAYER_ENTITY(1),
		LIVING_ENTITY_HURT(3),
		LIVING_ENTITY_DEAD(4),
		GOLEM_THROWING_ARMS(5),
		TAMED_HEART_PARTICLES(6),
		TAMED_SMOKE_PARTICLES(7),
		WOLF_SHAKING_WATER(8),
		SELF_EATING_ACCEPTED(9),
		SHEEP_EATING_GRASS(10),
		GOLEM_HANDING_ROSE(11),
		VILLAGER_HEART_PARTICLES(12),
		VILLAGER_ANGRY_PARTICLES(13),
		VILLAGER_HAPPY_PARTICLES(14),
		WITCH_MAGIC_PARTICLES(15),
		ZOMBIE_CONVERTING(16),
		FIREWORK_EXPLODING(17),
		ANIMAL_IN_LOVE(18);
		public final int id;
		private STATUS(int it) {
			id=it;
		}
	}
}
