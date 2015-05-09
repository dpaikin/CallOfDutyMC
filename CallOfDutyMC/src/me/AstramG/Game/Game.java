package me.AstramG.Game;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.AstramG.CallOfDutyMC.Arena;
import me.AstramG.CallOfDutyMC.CallOfDuty;

public class Game {
	
//	CallOfDuty CallOfDuty.instance;
	
	public Game(CallOfDuty cod) {
	//	this.CallOfDuty.instance = CallOfDuty.instance;
	}
	
	public void begin() {
		CallOfDuty.instance.hw.setCanJoin(false);
		String type = CallOfDuty.instance.getConfig().getString("Gamemode");
		CallOfDuty.instance.began = true;
		if (type.equalsIgnoreCase("Cranked")) {
			Bukkit.getPluginManager().registerEvents(CallOfDuty.instance.cranked, CallOfDuty.instance);
			Arena arena = CallOfDuty.instance.am.getRandomArena();
			CallOfDuty.instance.am.arena = arena;
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (CallOfDuty.instance.redTeam.size() <= CallOfDuty.instance.blueTeam.size()) {
					CallOfDuty.instance.redTeam.add(p.getName());
				} else {
					CallOfDuty.instance.blueTeam.add(p.getName());
				}
			}
			for (String playerName : CallOfDuty.instance.redTeam) {
				CallOfDuty.instance.am.addPlayerToArena(Bukkit.getPlayer(playerName), arena, 1);
			}
			for (String playerName : CallOfDuty.instance.blueTeam) {
				CallOfDuty.instance.am.addPlayerToArena(Bukkit.getPlayer(playerName), arena, 2);
			}
			CallOfDuty.instance.cranked.start();
			CallOfDuty.instance.lb.cancel();
		} else if (type.equalsIgnoreCase("Grind")) {
			Bukkit.getPluginManager().registerEvents(CallOfDuty.instance.grind, CallOfDuty.instance);
			Arena arena = CallOfDuty.instance.am.getRandomArena();
			CallOfDuty.instance.am.arena = arena;
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (CallOfDuty.instance.redTeam.size() <= CallOfDuty.instance.blueTeam.size()) {
					CallOfDuty.instance.redTeam.add(p.getName());
				} else {
					CallOfDuty.instance.blueTeam.add(p.getName());
				}
			}
			for (String playerName : CallOfDuty.instance.redTeam) {
				CallOfDuty.instance.am.addPlayerToArena(Bukkit.getPlayer(playerName), arena, 1);
			}
			for (String playerName : CallOfDuty.instance.blueTeam) {
				CallOfDuty.instance.am.addPlayerToArena(Bukkit.getPlayer(playerName), arena, 2);
			}
			CallOfDuty.instance.grind.start();
			CallOfDuty.instance.lb.cancel();
		} else if (type.equalsIgnoreCase("GunGame")) {
			Bukkit.getPluginManager().registerEvents(CallOfDuty.instance.gunGame, CallOfDuty.instance);
			Arena arena = CallOfDuty.instance.am.getRandomArena();
			CallOfDuty.instance.am.arena = arena;
			for (Player player : Bukkit.getOnlinePlayers()) {
				CallOfDuty.instance.am.addPlayerToArena(player, arena, 0);
			}
			CallOfDuty.instance.grind.start();
			CallOfDuty.instance.lb.cancel();
		} else {
			System.out.println("Something's wrong here... This Gamemode doesn't exist. This server will now sink like the Titanic.");
			CallOfDuty.instance.getServer().shutdown();
		}
	}
	
}
