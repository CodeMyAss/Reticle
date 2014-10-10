package org.spigot.reticle.events;

import org.spigot.reticle.botfactory.mcbot;

/**
 * Fired when Login Success is received
 * @author Encorn
 *
 */
public class LoginSuccessEvent extends Event {

	private final String user;
	private final String uuid;

	public LoginSuccessEvent(mcbot bot, String username, String uuid) {
		super(bot);
		this.user=username;
		this.uuid=uuid;
	}

	public String getUsername() {
		return user;
	}
	
	public String getUUID() {
		return uuid;
	}
	
}
