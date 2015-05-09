package me.AstramG.Game;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import me.AstramG.CallOfDutyMC.CallOfDuty;
import me.AstramG.CallOfDutyMC.Gun;
import me.AstramG.CallOfDutyMC.LoadoutManager;
import me.AstramG.CallOfDutyMC.CallOfDuty.GameState;
import me.AstramG.Util.SetArmour;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Score;

public class Grind extends GameMode {
	
	CallOfDuty cod;
	
	int redScore = 0;
	int blueScore = 0;
	
	public Grind(CallOfDuty cod) {
		super(cod, 60 * 2, GameType.GRIND);
		this.cod = cod;
	}
	
	public void start() {
		this.timeLeft = 60 * 2;
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (!(LoadoutManager.getLoadoutGun(player, cod.loadouts.get(player.getName())).equals("none"))) {
				Gun gun = cod.gm.getGun(LoadoutManager.getLoadoutGun(player, cod.loadouts.get(player.getName())));
				ItemStack primary = new ItemStack(gun.item, 1, (byte) gun.data);
				ItemMeta primaryMeta = primary.getItemMeta();
				primaryMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + gun.gunName + " <" + gun.clip + "> " + " <" + gun.maxAmmo + ">");
				primaryMeta.setLore(Arrays.asList(ChatColor.RED + gun.gunName));
				primary.setItemMeta(primaryMeta);
				player.getInventory().addItem(primary);
				primaryAmmo.put(player.getName(), gun.clip, gun.maxAmmo);
			}
			
			if (!(LoadoutManager.getLoadoutSecondaryGun(player, cod.loadouts.get(player.getName())).equals("none"))) {
				Gun secondaryg = cod.gm.getGun(LoadoutManager.getLoadoutSecondaryGun(player, cod.loadouts.get(player.getName())));
				ItemStack secondary = new ItemStack(secondaryg.item, 1, (byte) secondaryg.data);
				ItemMeta secondaryMeta = secondary.getItemMeta();
				secondaryMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + secondaryg.gunName + " <" + secondaryg.clip + "> " + " <" + secondaryg.maxAmmo + ">");
				secondaryMeta.setLore(Arrays.asList(ChatColor.RED + secondaryg.gunName));
				secondary.setItemMeta(secondaryMeta);
				player.getInventory().addItem(secondary);
				secondaryAmmo.put(player.getName(), secondaryg.clip, secondaryg.maxAmmo);
			}
			
			if (cod.redTeam.contains(player.getName())) {
				SetArmour.SetRedArmour(player);
			} else {
				SetArmour.SetBlueArmour(player);
			}
			
		}
		gr = new GameRunnable();
		gr.runTaskTimer(cod, 20l, 20l);
		
	}
	
	/*
	@SuppressWarnings("deprecation")
	@EventHandler
	public void deposit(final PlayerInteractEvent event) {
		if (cod.began == false)
			return;
		if (event.getItem() == null)
			return;
		final Player player = event.getPlayer();
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (event.getClickedBlock().getType() == Material.HUGE_MUSHROOM_1) {
				if (player.getInventory().contains(Material.SKULL_ITEM)) {
					for (int i = 0; i < 5; i ++)
						player.getWorld().playEffect(event.getClickedBlock().getLocation(), Effect.SMOKE, 1);
					if (cod.blueTeam.contains(player.getName())) {
						blueScore++;
					} else {
						redScore++;
					}
					player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "You've deposited a head into the bank!");
					if (redScore == 100) {
						endGame(false);
					}
					if (blueScore == 100) {
						endGame(false);
					}
					player.getInventory().remove(Material.SKULL_ITEM);
					player.updateInventory();
				}
				event.setCancelled(true);
				return;
			}
		}
	}*/
	
	public void onKill(Player killer, Player killed) {
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
		SkullMeta sm = (SkullMeta) head.getItemMeta();
		sm.setOwner(killed.getName());
		if (cod.redTeam.contains(killed.getName()))
			sm.setDisplayName(ChatColor.RED + killed.getName());
		else
			sm.setDisplayName(ChatColor.BLUE + killed.getName());
		sm.setLore(Arrays.asList(ChatColor.RESET + "" + ChatColor.LIGHT_PURPLE + "Bring this head to the nearest bank to earn your team a point!"));
		head.setItemMeta(sm);
		killed.getWorld().dropItem(killed.getLocation(), head);
	}
	
	public void respawn(final Player player) {
		if (cod.began == false)
			return;
		if (!(kdr.containsKey(player.getName()))) {
			kdr.put(player.getName(), 0, 1);
		} else {
			kdr.put(player.getName(), kdr.getFirstValue(player.getName()), kdr.getSecondValue(player.getName()) + 1);
		}
		player.getInventory().remove(Material.SKULL_ITEM);
		player.getInventory().clear();
		
		if (!(LoadoutManager.getLoadoutGun(player, cod.loadouts.get(player.getName())).equals("none"))) {
			Gun gun = cod.gm.getGun(LoadoutManager.getLoadoutGun(player, cod.loadouts.get(player.getName())));
			ItemStack primary = new ItemStack(gun.item, 1, (byte) gun.data);
			ItemMeta primaryMeta = primary.getItemMeta();
			primaryMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + gun.gunName + " <" + gun.clip + "> " + " <" + gun.maxAmmo + ">");
			primaryMeta.setLore(Arrays.asList(ChatColor.RED + gun.gunName));
			primary.setItemMeta(primaryMeta);
			player.getInventory().addItem(primary);
			primaryAmmo.put(player.getName(), gun.clip, gun.maxAmmo);
		}
		
		if (!(LoadoutManager.getLoadoutSecondaryGun(player, cod.loadouts.get(player.getName())).equals("none"))) {
			Gun secondaryg = cod.gm.getGun(LoadoutManager.getLoadoutSecondaryGun(player, cod.loadouts.get(player.getName())));
			ItemStack secondary = new ItemStack(secondaryg.item, 1, (byte) secondaryg.data);
			ItemMeta secondaryMeta = secondary.getItemMeta();
			secondaryMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + secondaryg.gunName + " <" + secondaryg.clip + "> " + " <" + secondaryg.maxAmmo + ">");
			secondaryMeta.setLore(Arrays.asList(ChatColor.RED + secondaryg.gunName));
			secondary.setItemMeta(secondaryMeta);
			player.getInventory().addItem(secondary);
			secondaryAmmo.put(player.getName(), secondaryg.clip, secondaryg.maxAmmo);
		}
		
		player.setMaxHealth(20.0D);
		player.setHealth(20.0D);
		invulnerable.add(player.getName());
		if (cod.began == false)
			return;
		if (cod.blueTeam.contains(player.getName())) {
			player.teleport(cod.am.arena.blueSpawn);
			SetArmour.SetBlueArmour(player);
		}
		if (cod.redTeam.contains(player.getName())) {
			player.teleport(cod.am.arena.redSpawn);
			SetArmour.SetRedArmour(player);
		}
		player.setHealth(player.getHealth());
		Gun gun = cod.gm.getGun(LoadoutManager.getLoadoutGun(player, cod.loadouts.get(player.getName())));
		primaryAmmo.setFirstValue(player.getName(), gun.clip);
		primaryAmmo.setSecondValue(player.getName(), gun.maxAmmo);
		Gun sgun = cod.gm.getGun(LoadoutManager.getLoadoutSecondaryGun(player, cod.loadouts.get(player.getName())));
		secondaryAmmo.setFirstValue(player.getName(), sgun.clip);
		secondaryAmmo.setSecondValue(player.getName(), sgun.maxAmmo);
		Bukkit.getScheduler().scheduleSyncDelayedTask(cod, new BukkitRunnable() {
			public void run() {
				invulnerable.remove(player.getName());
			}
		}, 40L);
	}
	
	@EventHandler
	public void pickupHeadEvent(PlayerPickupItemEvent event) {
		if (event.getItem() != null) {
			if (event.getItem().getItemStack().hasItemMeta()) {
				if (event.getItem().getItemStack().getItemMeta().getDisplayName().startsWith(ChatColor.BLUE + "")) {
					if (cod.blueTeam.contains(event.getPlayer().getName())) {
						event.getItem().remove();
						event.setCancelled(true);
					}
				} else {
					if (cod.redTeam.contains(event.getPlayer().getName())) {
						event.getItem().remove();
						event.setCancelled(true);
					}
				}
			}
		}
	}
	
	public void endGame(boolean timeRanOut) {
		cod.began = false;
		gr.cancel();
		CallOfDuty.instance.state = GameState.RESTARTING;
		redScore = 0;
		blueScore = 0;
		primaryAmmo.clear();
		secondaryAmmo.clear();
		kdr.clear();
		if (timeRanOut) {
			if (redScore > blueScore) {
				Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "The " + ChatColor.RED + ChatColor.BOLD + "RED" +  ChatColor.GREEN + ChatColor.BOLD + " team has won the game!");
				if (!(cod.redTeam.isEmpty())) {
					for (String s : cod.redTeam){
						cod.tokens.put(s, cod.tokens.get(s) + 20);
					}
				}
				if (!(cod.blueTeam.isEmpty())) {
					for (String s : cod.blueTeam){
						cod.tokens.put(s, cod.tokens.get(s) + 5);
					}
				}
			} else if (blueScore > redScore) {
				Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "The " + ChatColor.BLUE + ChatColor.BOLD + "BLUE" +  ChatColor.GREEN + ChatColor.BOLD + " team has won the game!");
				if (!(cod.redTeam.isEmpty())) {
					for (String s : cod.redTeam){
						cod.tokens.put(s, cod.tokens.get(s) + 5);
					}
				}
				if (!(cod.blueTeam.isEmpty())) {
					for (String s : cod.blueTeam){
						cod.tokens.put(s, cod.tokens.get(s) + 20);
					}
				}
			} else {
				Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "The Game has ended in a tie!");
				if (!(cod.redTeam.isEmpty())) {
					for (String s : cod.redTeam){
						cod.tokens.put(s, cod.tokens.get(s) + 10);
					}
				}
				if (!(cod.blueTeam.isEmpty())) {
					for (String s : cod.blueTeam){
						cod.tokens.put(s, cod.tokens.get(s) + 10);
					}
				}
			}
		}
		cod.reset();
	}
	
	public void updateScoreboard(Player player) {
		if (cod.began) {
			cod.board = cod.manager.getNewScoreboard();//SOOOO INTENSIVE!
			cod.objective = cod.board.registerNewObjective("CoDMC", "dummy");
			cod.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
			List<ChatColor> colors = Arrays.asList(ChatColor.GREEN, ChatColor.RED, ChatColor.YELLOW, ChatColor.BLUE, ChatColor.LIGHT_PURPLE);
			ChatColor color;
			Random random = new Random();
			int colorid = random.nextInt(colors.size());
			color = colors.get(colorid);
			cod.objective.setDisplayName(color + "" + ChatColor.BOLD + "CoDMC Game");
			Score space1 = cod.objective.getScore(Bukkit.getOfflinePlayer(ChatColor.RED + " "));
			Score space2 = cod.objective.getScore(Bukkit.getOfflinePlayer(ChatColor.RED + "  "));
			Score space3 = cod.objective.getScore(Bukkit.getOfflinePlayer(ChatColor.RED + "   "));
			Score space4 = cod.objective.getScore(Bukkit.getOfflinePlayer(ChatColor.RED + "    "));
			Score space5 = cod.objective.getScore(Bukkit.getOfflinePlayer(ChatColor.RED + "     "));
			Score space6 = cod.objective.getScore(Bukkit.getOfflinePlayer(ChatColor.RED + "      "));
			Score scoreRed = cod.objective.getScore(Bukkit.getOfflinePlayer(ChatColor.RED + "" + ChatColor.BOLD + "Red: " + redScore));
			Score scoreBlue = cod.objective.getScore(Bukkit.getOfflinePlayer(ChatColor.BLUE + "" + ChatColor.BOLD + "Blue: " + blueScore));
			Score score3 = cod.objective.getScore(Bukkit.getOfflinePlayer(ChatColor.YELLOW + "" + ChatColor.BOLD + "Level: " + cod.levels.getFirstValue(player.getName())));
			Score score2 = cod.objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GOLD + "" + ChatColor.BOLD + "Loadout:"));
			Score scoreLoadout = cod.objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GOLD + "" + ChatColor.BOLD + cod.loadouts.get(player.getName())));
			Score scoreTime = cod.objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GRAY + "" + ChatColor.BOLD + "Time: " + timeLeft));
			Score scoreTokens = cod.objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + "" + ChatColor.BOLD + "Tokens: "));
			Score scoreActualTokens = cod.objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + "" + ChatColor.BOLD + cod.tokens.get(player.getName())));
			Score scoreKdr = null;
			if (kdr.containsKey(player.getName())) {
				double playerkdr = 0.0D;
				try {
					playerkdr = kdr.getFirstValue(player.getName()) / kdr.getSecondValue(player.getName());
					scoreKdr = cod.objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GRAY + "" + ChatColor.BOLD + "KDR: " + playerkdr));
				} catch (ArithmeticException e) {
					scoreKdr = cod.objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GRAY + "" + ChatColor.BOLD + "KDR: " + kdr.getFirstValue(player.getName()) + ".0"));
				}
			} else {
				scoreKdr = cod.objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GRAY + "" + ChatColor.BOLD + "KDR: N/A"));
			}
			space5.setScore(15);
			scoreRed.setScore(14);
			scoreBlue.setScore(13);
			space4.setScore(12);
			score3.setScore(11);
			space3.setScore(3);
			space2.setScore(10);
			scoreKdr.setScore(9);
			space6.setScore(8);
			scoreLoadout.setScore(6);
			space1.setScore(5);
			scoreTime.setScore(4);
			scoreTokens.setScore(2);
			scoreActualTokens.setScore(1);
			score2.setScore(7);
			player.setScoreboard(cod.board);
		}
	}
	
}