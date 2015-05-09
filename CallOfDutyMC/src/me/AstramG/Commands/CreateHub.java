package me.AstramG.Commands;

import me.AstramG.CallOfDutyMC.CallOfDuty;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateHub implements CommandExecutor {
	
	CallOfDuty cod;
	
	public CreateHub(CallOfDuty cod) {
		this.cod = cod;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player || sender.isOp()))
			return true;
		Player player = (Player) sender;
		if (label.equalsIgnoreCase("createHub")) {
			Location hubLoc = player.getLocation();
			String hubString = hubLoc.getWorld().getName() + ":" + hubLoc.getX() + ":" + hubLoc.getY() + ":" + hubLoc.getZ();
			cod.getConfig().set("Hub.Location", hubString);
			cod.saveConfig();
			player.sendMessage(ChatColor.GREEN + "Hub location created!");
		}
		return true;
	}

}
