package org.spigot.reticle.API;

import java.awt.Component;

import javax.swing.JOptionPane;

import org.spigot.reticle.storage;

public class InputDialog {

	
	private String Title;
	private String Message;

	public InputDialog(String Title,String text) {
		this.Title=Title;
		this.Message=text;
	}
	
	public String getValue() {
		Component comp = storage.gettabbedpane();
		return  JOptionPane.showInputDialog(comp, Message, Title, JOptionPane.ERROR_MESSAGE);
	}
	
}
