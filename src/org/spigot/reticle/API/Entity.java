package org.spigot.reticle.API;

public class Entity {
	private int id;
	private Position pos;
	private Look look;
	private final boolean isMe;

	public Entity(int id, boolean isMe) {
		this.id = id;
		this.isMe = isMe;
	}

	/**
	 * @return True if this entity is current bot
	 */
	public boolean isMe() {
		return isMe;
	}
	
	/**
	 * @return Last known Look of entity
	 */
	public Look getEntityLook() {
		return look;
	}

	/**
	 * @return Last known entity position
	 */
	public Position getPosition() {
		return pos;
	}

	/**
	 * @return Last known entity ID
	 */
	public int getEntityID() {
		return id;
	}

	public Entity(int id, Position Position, boolean isMe) {
		this.id = id;
		this.pos = Position;
		this.isMe = isMe;
	}

	public Entity(int id, Position Position, Look Look, boolean isMe) {
		this.id = id;
		this.pos = Position;
		this.look = Look;
		this.isMe = isMe;
	}

	/**
	 * Invoked when entity has moved
	 * 
	 * @param newPosition
	 */
	public void EntityMoved(Position newPosition) {
		this.pos = newPosition;
	}
}
