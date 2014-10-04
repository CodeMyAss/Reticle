package org.spigot.reticle.events;

import org.spigot.reticle.API.Position;
import org.spigot.reticle.botfactory.mcbot;
import org.spigot.reticle.sockets.connector;

/**
 * Fired when sign is updated
 * @author Encorn
 *
 */
public class SignUpdateEvent extends Event {
	private final String[] lines;
	private final String[] formlines;
	private final Position pos;
	
	/**
	 * @return Returns sign position
	 */
	public Position getPosition() {
		return pos;
	}
	
	/**
	 * @return Returns formated lines of sign
	 */
	public String[] getFormatedLines() {
		return formlines;
	}
	
	/**
	 * @return Returns lines of sign
	 */
	public String[] getLines() {
		return lines;
	}
	
	public SignUpdateEvent(mcbot bot, int x, int y, int z, String[] lines) {
		super(bot);
		this.lines=lines;
		this.pos=new Position(x,y,z);
		formlines=new String[]{connector.parsechat(lines[0]),connector.parsechat(lines[1]),connector.parsechat(lines[2]),connector.parsechat(lines[3])};
	}
}
