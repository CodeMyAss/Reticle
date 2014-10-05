package org.spigot.reticle.coresp;


public class PlayerStreamHandshakeResponse {
	public final int Version,nextState;
	
	public PlayerStreamHandshakeResponse(int version, int nextstate) {
		this.Version=version;
		this.nextState=nextstate;
	}
}

