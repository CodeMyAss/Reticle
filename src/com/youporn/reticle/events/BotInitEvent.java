package org.spigot.reticle.events;

import org.spigot.reticle.botfactory.mcbot;

/**
 * Invoked when connector is reloaded
 * All cache reset events should happen here
 * @author Encorn
 *
 */
public class BotInitEvent extends Event {

	public BotInitEvent(mcbot bot) {
		super(bot);
	}

}
