package me.AstramG.Util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationUtil {
	
	public static Location unserializeLocation(String slocation) {
		String[] parsed = slocation.split(":");
		Location loc = new Location(Bukkit.getWorld(parsed[0]), Double.parseDouble(parsed[1]), Double.parseDouble(parsed[2]), Double.parseDouble(parsed[3]));
		return loc;
	}
	
}