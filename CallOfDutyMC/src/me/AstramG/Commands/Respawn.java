package me.AstramG.Commands;

import me.AstramG.CallOfDutyMC.CallOfDuty;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Respawn implements CommandExecutor {
	
	CallOfDuty cod;
	
	public Respawn(CallOfDuty cod) {
		this.cod = cod;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (cod.began) {
				String mode = cod.getConfig().getString("Gamemode");
				if (mode.equalsIgnoreCase("Grind")) {
					cod.grind.respawn(player);
				} else if (mode.equalsIgnoreCase("Cranked")) {
					cod.cranked.respawn(player);
				}
			} else {
				player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "The game hasn't began yet, how could you respawn!");
			}
		}
		return true;
	}
	
}
