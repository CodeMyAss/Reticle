package org.spigot.mcbot.sockets;

import java.util.Collection;

public class chatparse {
	public Collection<chatclass> extra;
	public String text="";
	
	
	public class chatclass {
	public Collection<chatclass> extra=null;	
	public boolean bold=false;
	public boolean italic=false;
	public String color="none";
	public boolean underlined=false;
	public boolean strikethrough=false;
	public boolean reset=false;
	public String text="";
	}
	
}
