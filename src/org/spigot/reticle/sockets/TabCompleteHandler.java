package org.spigot.reticle.sockets;

import javax.swing.JTextField;

public class TabCompleteHandler {
	private String[] names;
	private int current;
	private String currentcomplete;
	private JTextField area;
	private boolean isLocked = true;
	private String origmsg;
	private String suffixmsg;

	public void setOriginal() {
		isLocked = true;
		origmsg = null;
	}

	protected TabCompleteHandler() {

	}

	public void setNames(String[] in) {
		this.names = in;
	}

	public void setComponent(JTextField field) {
		area = field;
		if (isLocked || currentcomplete == null) {
			int maxlen=field.getCaretPosition();
			current = 0;
			currentcomplete = field.getText();
			suffixmsg=currentcomplete.substring(maxlen);
			currentcomplete=currentcomplete.substring(0,maxlen);
			String[] text = currentcomplete.split(" ");
			if (text.length == 1) {
				origmsg = currentcomplete;
				currentcomplete = "";
			} else if (text.length > 1) {
				origmsg = text[text.length - 1];
				currentcomplete = currentcomplete.substring(0, currentcomplete.length() - origmsg.length());
			}
		}
	}

	private void setNext() {
		if (current < names.length) {
			String toset=names[current];
			int len=toset.length()+currentcomplete.length();
			area.setText(currentcomplete + toset+suffixmsg);
			area.setCaretPosition(len);
		}
	}

	public String getOriginalMessage() {
		return origmsg;
	}

	public void getNext() {
		isLocked = false;
		if (names.length > 0) {
			current = (current + 1) % names.length;
			setNext();
		}
	}
}
