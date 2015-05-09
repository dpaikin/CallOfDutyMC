package me.AstramG.Commands;

import java.util.List;

import me.AstramG.CallOfDutyMC.CallOfDuty;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateArena implements CommandExecutor{
	
	String arenaData = "";
	CallOfDuty cod;
	
	public CreateArena(CallOfDuty cod) {
		this.cod = cod;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player))
			return true;
		if (!(sender.isOp()))
			return true;
		Player player = (Player) sender;
		if (label.equalsIgnoreCase("createArena")) {
			if (args.length == 0) {
				player.sendMessage(ChatColor.RED + "Use /createArena <name>");
				player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "WARNING: " + ChatColor.RED + "Don't proceed if you don't know what your doing. Please ask " + Bukkit.getPluginManager().getPlugin("CallOfDutyMC").getDescription().getAuthors().get(0) + " if you need any help!");
			}
			if (args.length == 1) {
				if (!(args[0].equalsIgnoreCase("finish") || args[0].equalsIgnoreCase("mark"))) {
					arenaData += args[0] + ";";
					player.sendMessage(ChatColor.GREEN + "Arena created named: " + args[0]);
					player.sendMessage(ChatColor.RED + "Say /createArena mark red");
				} else if (args[0].equalsIgnoreCase("finish")) {
					player.sendMessage(ChatColor.GREEN + "Arena Creation Complete!");
					List<String> arenaList = cod.getConfig().getStringList("Arenas");
					arenaList.add(arenaData);
					cod.getConfig().set("Arenas", arenaList);
					cod.saveConfig();
					arenaData = "";
				}
				
			}
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("mark")) {
					Location loc = player.getLocation();
					arenaData += "spawn:" + loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ() + ";";
				}
			}
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("mark")) {
					Location loc = player.getLocation();
					if (args[1].equalsIgnoreCase("red")) {
						arenaData += "red_main_spawn:" + loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ() + ";";
						player.sendMessage(ChatColor.RED + "Say /createArena mark blue");
					} else if (args[1].equalsIgnoreCase("blue")) {
						arenaData += "blue_main_spawn:" + loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ() + ";";
						player.sendMessage(ChatColor.RED + "Say /createArena mark as much as you like to mark spawn points");
					}
				}
			}
		}
		return true;
	}
	
	
}
