package me.AstramG.Commands;

import me.AstramG.CallOfDutyMC.CallOfDuty;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResourcePack implements CommandExecutor {
	
	CallOfDuty cod;
	
	public ResourcePack(CallOfDuty cod) {
		this.cod = cod;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("pack")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				player.setResourcePack("http://aliacraft.net/Ali-Pack%20V1.0.zip");
				player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Sent ResourcePack Request!");
			}
		}
		return true;
	}
	
}
