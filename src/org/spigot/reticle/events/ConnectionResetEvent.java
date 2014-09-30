package org.spigot.reticle.events;

import org.spigot.reticle.botfactory.mcbot;
import org.spigot.reticle.sockets.connector;

public class ConnectionResetEvent extends Event {

	private final String reason;
	
	public ConnectionResetEvent(mcbot bot,String reason) {
		super(bot);
		this.reason=connector.parsechat(reason);
	}
	
	public String getReason() {
		return reason;
	}

}
