package org.spigot.reticle.sockets;

public class ChatJob {
	private final connector con;
	public final String message;
	public final long delay;

	public ChatJob(connector c, String message, long delay2) {
		this.con = c;
		this.message = message;
		this.delay = delay2;
	}
	
	public void Execute() {
		this.con.sendToServerNow(message);
	}
}
