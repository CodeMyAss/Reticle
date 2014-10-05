package org.spigot.reticle.coresp;

public class ChunkDataInfo {
	public final int x;
	public final int z;
	public final boolean update;

	public ChunkDataInfo(int x, int z, boolean update) {
		this.x = x;
		this.z = z;
		this.update = update;
	}
}
