package org.spigot.reticle.API;

import java.util.List;

public class StatusResponse {
	public String description;
	public players players;
	
 public class players {
	 public int max;
	 public int online;
	 public List<playeritem> sample;
 }
 
 public class playeritem {
	 public String id;
	 public String name;
 }
}
