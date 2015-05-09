package me.AstramG.CallOfDutyMC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class ArenaManager {
	
	CallOfDuty cod;
	//142.4.219.45
	
	public List<Arena> arenas = new ArrayList<Arena>();
	private HashMap<String, Integer> spawnAttempts = new HashMap<String, Integer>();
	
	public Arena arena;
	
	public ArenaManager(CallOfDuty cod) {
		this.cod = cod;
	}
	
	public void addPlayerToArena(Player player, Arena arena, int type) {
		if (type == 1) {
			player.teleport(arena.redSpawn);
		} else if (type == 2) {
			player.teleport(arena.blueSpawn);
		} else if (type == 0) {
			tryForNewSpawnFFA(player, arena);
		}
	}
	
	public boolean isOnSameTeam(Player player, Player compare) {
		if (cod.redTeam.contains(player.getName()) && cod.redTeam.contains(compare.getName())) {
			return true;
		} else if (cod.blueTeam.contains(player.getName()) && cod.blueTeam.contains(compare.getName())) {
			return true;
		} else {
			return false;
		}
	}
	
	public void tryForNewSpawnTEAM(Player player, Arena arena) {
		if (!(spawnAttempts.containsKey(player.getName()))) {
			spawnAttempts.put(player.getName(), 1);
		} else {
			spawnAttempts.put(player.getName(), spawnAttempts.get(player.getName()) + 1);
		}
		Random rand = new Random();
		Location spawn = arena.spawns.get(rand.nextInt(arena.spawns.size()));
		if (spawnAttempts.get(player.getName()) != 5) {
			List<Entity> entities = getNearbyEntities(spawn, 10);
			for (Entity ne : entities) {
				if (ne instanceof Player) {
					if (isOnSameTeam(player, (Player) ne)) {//Safe Spawn
						player.teleport(spawn);
						if (spawnAttempts.containsKey(player.getName())) {
							spawnAttempts.remove(player.getName());
						}
					} else {
						tryForNewSpawnTEAM(player, arena);
					}
				}
			}
		} else {
			if (cod.redTeam.contains(player.getName()))
				player.teleport(arena.redSpawn);
			if (cod.blueTeam.contains(player.getName()))
				player.teleport(arena.blueSpawn);
			player.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "Couldn't find a safe spawn");
			spawnAttempts.remove(player.getName());
		}
	}
	
	public void tryForNewSpawnFFA(Player player, Arena arena) {
		if (!(spawnAttempts.containsKey(player.getName()))) {
			spawnAttempts.put(player.getName(), 1);
		} else {
			spawnAttempts.put(player.getName(), spawnAttempts.get(player.getName()) + 1);
		}
		Random rand = new Random();
		Location spawn = arena.spawns.get(rand.nextInt(arena.spawns.size()));
		if (spawnAttempts.get(player.getName()) != 5) {
			List<Entity> entities = getNearbyEntities(spawn, 15);
			for (Entity ne : entities) {
				if (!(ne instanceof Player)) {//Safe Spawn
					player.teleport(spawn);
					if (spawnAttempts.containsKey(player.getName())) {
						spawnAttempts.remove(player.getName());
					}
				} else {
					tryForNewSpawnFFA(player, arena);
				}
			}
		} else {
			player.teleport(spawn);
			player.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "Couldn't find a safe spawn");
			spawnAttempts.remove(player.getName());
		}
	}
	
	public Arena getArena(String arenaName) {
		for (Arena a : arenas) {
			if (a.name.equalsIgnoreCase(arenaName))
				return a;
		}
		return null;
	}
	
	public Arena getRandomArena() {
		Random rand = new Random();
		return arenas.get(rand.nextInt(arenas.size()));
	}
	
	public static List<Entity> getNearbyEntities(Location l, int radius) {
	    int chunkRadius = radius < 16 ? 1 : (radius - (radius % 16)) / 16;
	    HashSet <Entity> radiusEntities = new HashSet < Entity > ();
	 
	    for (int chX = 0 - chunkRadius; chX <= chunkRadius; chX++) {
	        for (int chZ = 0 - chunkRadius; chZ <= chunkRadius; chZ++) {
	            int x = (int) l.getX(), y = (int) l.getY(), z = (int) l.getZ();
	            for (Entity e: new Location(l.getWorld(), x + (chX * 16), y, z + (chZ * 16)).getChunk().getEntities()) {
	                if (e.getLocation().distance(l) <= radius && e.getLocation().getBlock() != l.getBlock())
	                    radiusEntities.add(e);
	            }
	        }
	    }
	 
	    return Arrays.asList(radiusEntities.toArray(new Entity[radiusEntities.size()]));
	}
	
}
