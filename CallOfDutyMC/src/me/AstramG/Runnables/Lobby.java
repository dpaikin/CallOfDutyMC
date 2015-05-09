package me.AstramG.Runnables;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import me.AstramG.CallOfDutyMC.CallOfDuty;
import me.AstramG.CallOfDutyMC.CallOfDuty.GameState;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Lobby extends BukkitRunnable {

	CallOfDuty cod;

	public Lobby(CallOfDuty cod) {
		this.cod = cod;
	}

	public void run() {
		if (CallOfDuty.instance.state == GameState.LOBBY) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				cod.changescore(p);
			}
			if (cod.timeInSeconds >= 5 && cod.timeInSeconds < 0) {
				for (Player p : Bukkit.getOnlinePlayers()) {
					p.playSound(p.getLocation(), Sound.NOTE_PIANO, 1, 2);
				}
			}
			if (cod.timeInSeconds % 15 == 0 && cod.timeInSeconds != 0) {
				Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Say /loadout to customize your loadout!");
			}
			if (cod.timeInSeconds <= 0) {
				Player[] players = Bukkit.getOnlinePlayers();
				List<Player> safeList = new CopyOnWriteArrayList<Player>();
				for (Player player : players) {
					safeList.add(player);
					if (player.getGameMode() != GameMode.SURVIVAL) {
						safeList.remove(player);
					}
				}
				if (safeList.size() >= 2) { // Game Starts /*CHANGE TO 4
											// PLAYERS*/
					for (Player p : players) {
						p.getInventory().clear();
					}
					cod.game.begin();
					cod.timeInSeconds = -1;
					CallOfDuty.instance.state = GameState.SESSION;
					// cod.cancelTask();
				} else {
					Bukkit.broadcastMessage(ChatColor.BOLD + "" + ChatColor.RED + "[CoDMC]" + ChatColor.RESET + ChatColor.RED + " Not enough players to begin.");
					cod.timeInSeconds = 60;
				}
			} else {
				cod.timeInSeconds--;
			}
		} else {
			cancel();
		}
	}

}
