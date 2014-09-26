package org.spigot.reticle;

import org.spigot.reticle.API.POST;
import org.spigot.reticle.API.POST.POSTMETHOD;

public class News extends Thread {
	
	protected News() {
		
	}
	
	@Override
	public void run() {
		try {
		POST form = new POST(storage.news,true);
		form.addField("version", storage.version, true);
		form.setMethod(POSTMETHOD.GET);
		if (form.Execute()) {
			String news=form.getResponse();
			storage.conlog("NEWS: \n=============================\n"+news+"\n=============================\n");
		}
		} catch (Exception e) {
			storage.conlog("News service not available");
		}
	}
}
