package org.spigot.reticle.botfactory;

import java.util.ArrayList;
import java.util.List;

public class UpListKeeper {
	private List<String> messages=new ArrayList<String>();
	private int index;
	
	protected UpListKeeper() {
		
	}
	
	public void addMessage(String message) {
		if(messages.contains(message)) {
			messages.remove(message);
		}
		messages.add(message);
		index=messages.size();
	}
	
	public String getNext() {
		if(index<messages.size()) {
			index++;
		}
		if(messages.size()>index) {
			return messages.get(index);
		} else {
			return "";
		}
	}
	
	public String getPrevious() {
		if(index>0) {
			index--;
		}
		if(messages.size()>index) {
			return messages.get(index);
		} else {
			return "";
		}
	}

}
