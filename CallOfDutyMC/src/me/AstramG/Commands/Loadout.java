package me.AstramG.Commands;

import me.AstramG.CallOfDutyMC.CallOfDuty;
import me.AstramG.CallOfDutyMC.LoadoutManager;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Loadout implements CommandExecutor {

	CallOfDuty cod;
	
	public Loadout(CallOfDuty cod) {
		this.cod = cod;
	}

	//Loadout Order= NAME:GUNW/ATTACHMENTS:SECONDARYW/ATTACHMENTS:PERKS
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player))
			return true;
		Player player = (Player) sender;
		if (cod.changingName.contains(player.getName())) {
			player.sendMessage(ChatColor.RED + "You can't do that while changing the loadout name!");
			return true;
		}
		if (label.equalsIgnoreCase("loadout")) {
			LoadoutManager.createInventory(player);
		}
		return true;
	}
	
}
