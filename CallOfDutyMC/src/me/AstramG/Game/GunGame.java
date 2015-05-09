package me.AstramG.Game;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import me.AstramG.CallOfDutyMC.CallOfDuty;
import me.AstramG.CallOfDutyMC.Gun;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Score;

public class GunGame extends GameMode {
	
	CallOfDuty cod;
	
	int redScore = 0;
	int blueScore = 0;
	
	public Gun[] GUN_LIST;
	
	private HashMap<String, Integer> progression = new HashMap<String, Integer>();
	
	public GunGame(CallOfDuty cod, int timeLeftInSeconds) {
		super(cod, timeLeftInSeconds, GameType.GUN_GAME);
		this.cod = cod;
		super.timeLeft = timeLeftInSeconds;
		GUN_LIST = new Gun[]{
				cod.gm.getGun("Glock18"),
				cod.gm.getGun("M9"),
				cod.gm.getGun("SPAS12"),
				cod.gm.getGun("Tac-45"),
				cod.gm.getGun("KAP-40"),
				cod.gm.getGun("B23R"), 
				cod.gm.getGun("CBJ-MS"), 
				cod.gm.getGun("VECTOR CRB"),
				cod.gm.getGun("AN94"),
				cod.gm.getGun("M4 Carbine"),
				cod.gm.getGun("Remington R5"),
				cod.gm.getGun("MTAR-X"),
				cod.gm.getGun("LSAT"), 
				cod.gm.getGun("QBB-LSW"),
				cod.gm.getGun("HAMR"),
				cod.gm.getGun("AK47"),
				cod.gm.getGun("L96A1"),
				cod.gm.getGun("L115"),
				cod.gm.getGun("MP-7"),
				cod.gm.getGun("FAL")
			};
	}
	
	public void start() {
		this.timeLeft = 60*10;
		for (Player player : Bukkit.getOnlinePlayers()) {
			Gun gun = GUN_LIST[0];
			ItemStack primary = new ItemStack(gun.item, 1, (byte) gun.data);
			ItemMeta primaryMeta = primary.getItemMeta();
			primaryMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + gun.gunName + " <" + gun.clip + "> " + " <" + gun.maxAmmo + ">");
			primaryMeta.setLore(Arrays.asList(ChatColor.RED + gun.gunName));
			primary.setItemMeta(primaryMeta);
			player.getInventory().addItem(primary);
			primaryAmmo.put(player.getName(), gun.clip, gun.maxAmmo);
			
			progression.put(player.getName(), 1);
		}
		Bukkit.getScheduler().scheduleSyncRepeatingTask(cod, new GameRunnable(), 20L, 20L);
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
		
		Gun gun = GUN_LIST[progression.get(player.getName()) - 1];
		ItemStack primary = new ItemStack(gun.item, 1, (byte) gun.data);
		ItemMeta primaryMeta = primary.getItemMeta();
		primaryMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + gun.gunName + " <" + gun.clip + "> " + " <" + gun.maxAmmo + ">");
		primaryMeta.setLore(Arrays.asList(ChatColor.RED + gun.gunName));
		primary.setItemMeta(primaryMeta);
		player.getInventory().addItem(primary);
		primaryAmmo.put(player.getName(), gun.clip, gun.maxAmmo);
		
		player.removePotionEffect(PotionEffectType.SPEED);
		if (cod.began == false)
			return;
		cod.am.tryForNewSpawnFFA(player, cod.am.arena);
		player.setHealth(player.getHealth());
		primaryAmmo.setFirstValue(player.getName(), gun.clip);
		primaryAmmo.setSecondValue(player.getName(), gun.maxAmmo);
		Bukkit.getScheduler().scheduleSyncDelayedTask(cod, new BukkitRunnable() {
			public void run() {
				invulnerable.remove(player.getName());
			}
		}, 40L);
	}
	
	public void endGame(boolean timeRanOut) {
		cod.began = false;
		redScore = 0;
		blueScore = 0;
		primaryAmmo.clear();
		secondaryAmmo.clear();
		kdr.clear();
		if (timeRanOut) {
			String player = (String) progression.keySet().toArray()[0];
			for (String playerName : progression.keySet()) {
				if (progression.get(playerName) > progression.get(player)) {
					player = playerName;
				}
			}
			progression.clear();
			cod.tokens.put(player, cod.tokens.get(player) + 30);
			Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + player + " has won the game!");
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
			Score score = cod.objective.getScore(Bukkit.getOfflinePlayer(ChatColor.BOLD + "Score: " + progression.get(player.getName())));
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
			score.setScore(13);
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
		progression.put(killer.getName(), progression.get(killer.getName()) + 1);
		killer.getInventory().clear();
		
		cod.awardExp(killer, 5);
		
		Gun gun = GUN_LIST[progression.get(killer.getName()) - 1];
		ItemStack primary = new ItemStack(gun.item, 1, (byte) gun.data);
		ItemMeta primaryMeta = primary.getItemMeta();
		primaryMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + gun.gunName + " <" + gun.clip + "> " + " <" + gun.maxAmmo + ">");
		primaryMeta.setLore(Arrays.asList(ChatColor.RED + gun.gunName));
		primary.setItemMeta(primaryMeta);
		killer.getInventory().addItem(primary);
		primaryAmmo.put(killer.getName(), gun.clip, gun.maxAmmo);
		
		//SCORE CALCULATIONS
		if (progression.get(killer) == 21) {
			endGame(false);
			cod.tokens.put(killer.getName(), cod.tokens.get(killer.getName()) + 30);
		}
	}
	
}