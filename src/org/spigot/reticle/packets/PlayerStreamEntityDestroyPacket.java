package org.spigot.reticle.packets;

import java.io.IOException;
import java.nio.ByteBuffer;

public class PlayerStreamEntityDestroyPacket extends AbstractPacket {
	private packet reader;

	public PlayerStreamEntityDestroyPacket(ByteBuffer buf, packet reader) {
		this.reader = reader;
		this.reader.input = buf;
	}

	public void Write(int[] entityid) throws IOException {
		if (entityid.length > 0) {
			int batch = 100;
			int done=0;
			int batches = (int) Math.ceil(entityid.length / batch);
			for (int i = 0; i < batches; i++) {
				int batchlen;
				if(done+batch>entityid.length) {
					batchlen=entityid.length-(done+batch);
				} else {
					batchlen=batch;
				}
				byte bytelen = (byte) (batchlen & 0xff);
				reader.setOutputStream(4 * batchlen + 2);
				reader.writeVarInt(0x13);
				reader.writeByte(bytelen);
				for(int o=done;o<done+batchlen;o++) {
					int ent=entityid[o];
					reader.writeInt(ent);
				}
				reader.Send();
				done+=batchlen;
			}
		}
	}

}
