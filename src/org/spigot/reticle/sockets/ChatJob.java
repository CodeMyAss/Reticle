package org.spigot.reticle.sockets;

public class ChatJob {
	private final connector con;
	public final String message;
	public final long delay;

	protected ChatJob(connector c, String message, long delay2) {
		this.con = c;
		this.message = message;
		this.delay = delay2;
	}
	
	protected void Execute() {
		this.con.sendToServerNow(message);
	}
}
