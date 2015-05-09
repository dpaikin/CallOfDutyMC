package me.AstramG.CallOfDutyMC;

import java.util.List;

import org.bukkit.Location;

public class Arena {
	
	public String name = "";
	public Location redSpawn;
	public Location blueSpawn;
	public List<Location> spawns;
	
	public Arena (String name, Location rs, Location bs, List<Location> spawns) {
		this.name = name;
		redSpawn = rs;
		blueSpawn = bs;
		this.spawns = spawns;
	}
	
}
