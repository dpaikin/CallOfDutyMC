package me.AstramG.Game;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import me.AstramG.CallOfDutyMC.CallOfDuty;
import me.AstramG.CallOfDutyMC.Gun;
import me.AstramG.CallOfDutyMC.LoadoutManager;
import me.AstramG.Util.SetArmour;
import net.minecraft.server.v1_7_R3.EntityPlayer;
import net.minecraft.server.v1_7_R3.PacketPlayOutWorldParticles;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Score;

public class Cranked extends GameMode {
	
	CallOfDuty cod;
	
	private final HashMap<String, Integer> crankedTime = new HashMap<String, Integer>();
	private final HashMap<String, BukkitRunnable> crankedTasks = new HashMap<String, BukkitRunnable>();
	
	int redScore = 0;
	int blueScore = 0;
	
	public Cranked(CallOfDuty cod, int timeLeftInSeconds) {
		super(cod, timeLeftInSeconds, GameType.CRANKED);
		this.cod = cod;
		super.timeLeft = timeLeftInSeconds;
	}
	
	public void start() {
		this.timeLeft = 60*10;
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
		gr.runTaskTimer(cod, 20L, 20L);
	}
	
	public void respawn(final Player player) {
		if (cod.began == false)
			return;
		player.setMaxHealth(20.0D);
		player.setHealth(20.0D);
		if (!(kdr.containsKey(player.getName()))) {
			kdr.put(player.getName(), 0, 1);
		} else {
			kdr.put(player.getName(), kdr.getFirstValue(player.getName()), kdr.getSecondValue(player.getName()) + 1);
		}
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
		
		player.removePotionEffect(PotionEffectType.SPEED);
		invulnerable.add(player.getName());
		if (crankedTime.containsKey(player.getName()) || crankedTasks.containsKey(player.getName())) {
			crankedTasks.get(player.getName()).cancel();
			crankedTime.remove(player.getName());
			crankedTasks.remove(player.getName());
			player.setLevel(0);
			player.setExp(0);
		}
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
	
	public void crank(final Player player) {
		if (cod.began == false)
			return;
		try {
			crankedTasks.get(player.getName()).cancel();
			crankedTasks.remove(player.getName());
		} catch (Exception e) {
			//DO NOTHING :D
		}
		player.setHealth(20.0D);
		PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles("heart", (float) player.getLocation().getX(), (float) player.getLocation().getY(), (float) player.getLocation().getZ(), 0, (float) 1.5, 0, 1, 1);
		for (Player p : Bukkit.getOnlinePlayers()) {
			EntityPlayer ep = ((CraftPlayer) p).getHandle();
			ep.playerConnection.sendPacket(packet);
		}
		player.removePotionEffect(PotionEffectType.SPEED);
		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*30, 2, true));
		final Random random = new Random();
		crankedTime.put(player.getName(), 30);
		crankedTasks.put(player.getName(),  new BukkitRunnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				int rand = random.nextInt(10);
				if (crankedTime.get(player.getName()) != 1) {
					player.setLevel(crankedTime.get(player.getName()));
					player.setExp(Math.round((crankedTime.get(player.getName()) / 30) * 100) / 100); 
					crankedTime.put(player.getName(), crankedTime.get(player.getName()) - 1);
					PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles("reddust", (float) player.getLocation().getX(), (float) player.getLocation().getY(), (float) player.getLocation().getZ(), 0, 1, 0, rand, 25);
					for (Player p : Bukkit.getOnlinePlayers()) {
						EntityPlayer ep = ((CraftPlayer) p).getHandle();
						ep.playerConnection.sendPacket(packet);
					}
				} else {
					crankedTime.put(player.getName(), -1);
					PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles("lava", (float) player.getLocation().getX(), (float) player.getLocation().getY(), (float) player.getLocation().getZ(), 0, 1, 0, rand, 25);
					for (Player p : Bukkit.getOnlinePlayers()) {
						EntityPlayer ep = ((CraftPlayer) p).getHandle();
						ep.playerConnection.sendPacket(packet);
					}
					player.playEffect(player.getLocation(), Effect.STEP_SOUND, 55);
					for (int i = 0; i < 5; i ++) {
						Random random = new Random();
						int dX = random.nextInt(3) - 1;
						int dZ = random.nextInt(3) - 1;
						final Item item = player.getWorld().dropItem(player.getLocation().add(dX, .75, dZ), new ItemStack(Material.INK_SACK, 1, (byte) 1));
						item.setVelocity(player.getLocation().toVector().add(item.getLocation().toVector()).normalize().multiply(.15));
						item.setPickupDelay(1000);
						Bukkit.getScheduler().scheduleSyncDelayedTask(cod, new BukkitRunnable() {
							public void run() {
								item.remove();
							}
						}, 15L);
						player.getWorld().createExplosion(player.getLocation(), 3.0F);
					}
					respawn(player);
					crankedTime.remove(player.getName());
					crankedTasks.remove(player.getName());
					cancel();
				}
			}
		});
	}
	
	public void endGame(boolean timeRanOut) {
		cod.began = false;
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
						cod.tokens.put(s, cod.tokens.get(s) + 15);
					}
				}
				if (!(cod.blueTeam.isEmpty())) {
					for (String s : cod.blueTeam){
						cod.tokens.put(s, cod.tokens.get(s) + 2);
					}
				}
			} else if (blueScore > redScore) {
				Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "The " + ChatColor.BLUE + ChatColor.BOLD + "BLUE" +  ChatColor.GREEN + ChatColor.BOLD + " team has won the game!");
				if (!(cod.redTeam.isEmpty())) {
					for (String s : cod.redTeam){
						cod.tokens.put(s, cod.tokens.get(s) + 2);
					}
				}
				if (!(cod.blueTeam.isEmpty())) {
					for (String s : cod.blueTeam){
						cod.tokens.put(s, cod.tokens.get(s) + 15);
					}
				}
			} else {
				Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "The Game has ended in a tie!");
				if (!(cod.redTeam.isEmpty())) {
					for (String s : cod.redTeam){
						cod.tokens.put(s, cod.tokens.get(s) + 5);
					}
				}
				if (!(cod.blueTeam.isEmpty())) {
					for (String s : cod.blueTeam){
						cod.tokens.put(s, cod.tokens.get(s) + 5);
					}
				}
			}
		}
		cod.reset();
	}
	
	public void updateScoreboard(Player player) {
		if (cod.began) {
			cod.board = cod.manager.getNewScoreboard();
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
			Score scoreTime = cod.objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GRAY + "" + ChatColor.BOLD + "Time: " + super.timeLeft));
			Score scoreTokens = cod.objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + "" + ChatColor.BOLD + "Tokens: "));
			Score scoreActualTokens = cod.objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + "" + ChatColor.BOLD + cod.tokens.get(player.getName())));
			Score scoreKdr;
			if (kdr.containsKey(player.getName())) {
				double playerkdr = 0.0D;
				try {
					playerkdr = kdr.getFirstValue(player.getName()) / kdr.getSecondValue(player.getName());
					scoreKdr = cod.objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GRAY + "" + ChatColor.BOLD + "KDR: " + playerkdr));
				} catch (ArithmeticException e) {
					int kills = kdr.getFirstValue(player.getName());
					scoreKdr = cod.objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GRAY + "" + ChatColor.BOLD + "KDR: " + kills + ".0"));
				}
			} else {
				scoreKdr = cod.objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GRAY + "" + ChatColor.BOLD + "KDR: N/A"));
			}
			space5.setScore(3);
			scoreRed.setScore(14);
			scoreBlue.setScore(13);
			space4.setScore(12);
			score3.setScore(11);
			space3.setScore(3);
			space2.setScore(10);
			space6.setScore(8);
			scoreKdr.setScore(9);
			scoreLoadout.setScore(6);
			space1.setScore(5);
			scoreTime.setScore(4);
			scoreTokens.setScore(2);
			scoreActualTokens.setScore(1);
			score2.setScore(7);
			player.setScoreboard(cod.board);
		}
	}
	
	public void onKill(Player killer, Player killed) {
		crank(killer);
		cod.awardExp(killer, 5);
		if (cod.redTeam.contains(killer.getName())) {
			redScore++;
		} else if (cod.blueTeam.contains(killer.getName())) {
			blueScore++;
		}
		if (redScore == 100) {
			Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "The " + ChatColor.RED + ChatColor.BOLD + "RED" +  ChatColor.GREEN + ChatColor.BOLD + " team has won the game!");
			for (String name : cod.redTeam)
				cod.tokens.put(name, cod.tokens.get(name) + 20);
			for (String name : cod.blueTeam)
				cod.tokens.put(name, cod.tokens.get(name) + 5);
			endGame(false);
		}
		if (blueScore == 100) {
			Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "The " + ChatColor.BLUE + ChatColor.BOLD + "BLUE" +  ChatColor.GREEN + ChatColor.BOLD + " team has won the game!");
			for (String name : cod.redTeam)
				cod.tokens.put(name, cod.tokens.get(name) + 5);
			for (String name : cod.blueTeam)
				cod.tokens.put(name, cod.tokens.get(name) + 20);
			endGame(false);
		}
	}
	
}