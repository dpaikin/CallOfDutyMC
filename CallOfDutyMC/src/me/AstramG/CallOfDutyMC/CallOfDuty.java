package me.AstramG.CallOfDutyMC;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import me.AstramG.Commands.CreateArena;
import me.AstramG.Commands.CreateHub;
import me.AstramG.Commands.Loadout;
import me.AstramG.Commands.ResourcePack;
import me.AstramG.Commands.Respawn;
import me.AstramG.Events.GeneralEvents;
import me.AstramG.Events.InventoryEvents;
import me.AstramG.Game.Cranked;
import me.AstramG.Game.Game;
import me.AstramG.Game.GameMode;
import me.AstramG.Game.Grind;
import me.AstramG.Game.GunGame;
import me.AstramG.MultiMap.MultiMap;
import me.AstramG.Runnables.Lobby;
import me.AstramG.TokenAPI.Token;
import me.AstramG.Util.AutoRespawn;
import me.AstramG.Util.HubWarpUtil;
import me.AstramG.Util.UnlockUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class CallOfDuty extends JavaPlugin {
	
	public ScoreboardManager manager;
	public Scoreboard board;
	public Objective objective;
	public static CallOfDuty instance;
	
	LoadoutManager loadout = new LoadoutManager(this);
	
	//TokenAPI
	public Token token;
	public GunManager gm;
	public UnlockUtil uu;
	public Game game;
	public ArenaManager am;
	public Lobby lb;
	public HubWarpUtil hw;
	
	public MySQL MySQL = new MySQL(this, "ip", "port", "db", "user", "pass");
	public  Connection c = null;

	public int timeInSeconds;
	public int taskId;
	
	public HashMap<String, Integer> tokens = new HashMap<String, Integer>();
	public HashMap<String, String> loadouts = new HashMap<String, String>();
	public HashMap<String, Integer> loadoutNums = new HashMap<String, Integer>();
	public MultiMap<String, Integer, Integer> levels = new MultiMap<String, Integer, Integer>();
	public HashMap<String, Integer> kills = new HashMap<String, Integer>();
	
	public List<String> changingName = new ArrayList<String>();
	
	public List<String> redTeam = new ArrayList<String>();
	public List<String> blueTeam = new ArrayList<String>();
	
	//Game Classes are below!
	public Cranked cranked;
	public Grind grind;
	public GunGame gunGame;
	public GameState state = GameState.LOBBY;
	
	public boolean began = false;
	
	//FireRate is RPM -> /60 -> /65
	//Gun Data: ITEMIDMATERIAL:DATA:TYPE:DMG:PERMATACHMENT:FIRERATE:PRICE:BULLETSTHATGETFIRED:CLIP:MAXAMMO:RANGE:RELOAD(TICKS):ZOOM
	
	@Override
	public void onEnable() {
		instance = this;
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		new AutoRespawn(this);
		init();
	}
	
	public void init(){
		cranked = new Cranked(this, 60 * 10);
		grind = new Grind(this);
		hw = new HubWarpUtil(this);
		began = false;
		token = (Token) Bukkit.getPluginManager().getPlugin("TokenAPI");
		manager = Bukkit.getScoreboardManager();
		board = manager.getNewScoreboard();
		gm = new GunManager(this);
		uu = new UnlockUtil(this);
		am = new ArenaManager(this);
		game = new Game(this);
		lb = new Lobby(this);
		c = MySQL.openConnection();
		//taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, lb, 20L, 20L);
		lb.runTaskTimer(this, 20L, 20L);
		registerCommands();
		registerEvents();
		timeInSeconds = 60;
		registerUnlockables();
		loadArenas();
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.kickPlayer(ChatColor.YELLOW + "" + ChatColor.BOLD + "This server is restarting! Join again right away!");
		}
		hw.setCanJoin(true);
		new BukkitRunnable(){
			public void run(){
				if (state == GameState.RESTARTING){
					state = GameState.LOBBY;
					cranked = new Cranked(instance, 60 * 10);
					grind = new Grind(instance);
					game = new Game(instance);
					lb = new Lobby(instance);
					lb.runTaskTimer(instance, 20L, 20L);
					timeInSeconds = 60;
				}
			}
		}.runTaskTimer(this, 0L, 25L);
	}
	
	@Override
	public void onDisable() {
		this.saveConfig();
		hw.setCanJoin(false);
	}
	
	private void registerUnlockables() {
		try {
			Statement statement = c.createStatement();
			ResultSet res = statement.executeQuery("SELECT * FROM codmc_plugin_unlocks_guns");
			while (res.next()) {
				gm.addGun(res.getString("Name"), res.getString("Data"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		gunGame = new GunGame(this, 60 * 10);
	}
	
	public void reset() {
		began = false;
		blueTeam.clear();
		redTeam.clear();
		timeInSeconds = 60;
		for (Player p : Bukkit.getOnlinePlayers()) {
			GeneralEvents.sendToHub(p);
			changescore(p);
			p.getInventory().clear();
			p.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Top Kills: " + ChatColor.ITALIC + getPlayerWithMostKills());
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(b);
			try {
				out.writeUTF("Connect");
				out.writeUTF("arcade");
				p.sendPluginMessage(this, "BungeeCord", b.toByteArray());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		kills.clear();
		//taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, lb, 20L, 20L);
		hw.setCanJoin(true);
	}
	
	public void registerLevels(Player player) {
		try {
			Statement statement = c.createStatement();
			ResultSet res = statement.executeQuery("SELECT Level FROM `codmc_plugin_players` WHERE Name='" + player.getName() + "';");
			res.next();
			String level = res.getString("Level");
			String[] dLevel = level.split(":");
			levels.put(player.getName(), Integer.parseInt(dLevel[0]), Integer.parseInt(dLevel[1]));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void registerEvents() {
		Bukkit.getPluginManager().registerEvents(new GeneralEvents(this), this);
		Bukkit.getPluginManager().registerEvents(new InventoryEvents(this), this);
		Bukkit.getPluginManager().registerEvents(GameMode.getGameMode(), this);
	}
	
	private void registerCommands() {
		getCommand("loadout").setExecutor(new Loadout(this));
		getCommand("createHub").setExecutor(new CreateHub(this));
		getCommand("createArena").setExecutor(new CreateArena(this));
		getCommand("pack").setExecutor(new ResourcePack(this));
		getCommand("respawn").setExecutor(new Respawn(this));
	}
	
	public String getPlayerWithMostKills() {
		if (kills.size() == 0) return "N/A";
		String topPlayer = "";
		for (String player : kills.keySet()) {
			if (topPlayer.equals("")) {
				topPlayer = player;
			}
			if (kills.get(player) > kills.get(topPlayer)) {
				topPlayer = player;
			}
		}
		return topPlayer;
	}
	
	public void changescore(Player player) {
		if (this.began == false) {
			board = manager.getNewScoreboard();
			objective = board.registerNewObjective("CoDMC", "dummy");
			objective.setDisplaySlot(DisplaySlot.SIDEBAR);
			List<ChatColor> colors = Arrays.asList(ChatColor.GREEN, ChatColor.RED, ChatColor.YELLOW, ChatColor.BLUE, ChatColor.LIGHT_PURPLE);
			ChatColor color;
			Random random = new Random();
			int colorid = random.nextInt(colors.size());
			color = colors.get(colorid);
			objective.setDisplayName(color + "" + ChatColor.BOLD + "Welcome to CoDMC");
			Score space1 = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.RED + " "));
			Score space2 = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.RED + "  "));
			Score space3 = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.RED + "   "));
			Score space4 = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.RED + "    "));
			Score score3;
			if (levels.containsKey(player.getName())) {
				score3 = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.YELLOW + "" + ChatColor.BOLD + "Level: " + levels.getFirstValue(player.getName())));
			} else {
				score3 = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.YELLOW + "" + ChatColor.BOLD + "Level: 0"));
			}
			Score score2 = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GOLD + "" + ChatColor.BOLD + "Loadout:"));
			Score scoreLoadout = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GOLD + "" + ChatColor.BOLD + loadouts.get(player.getName())));
			Score scoreTime = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GRAY + "" + ChatColor.BOLD + "Time: " + timeInSeconds));
			Score scoreTokens = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + "" + ChatColor.BOLD + "Tokens: "));
			Score scoreActualTokens = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + "" + ChatColor.BOLD + tokens.get(player.getName())));
			space4.setScore(10);
			score3.setScore(9);
			space3.setScore(3);
			space2.setScore(8);
			scoreLoadout.setScore(6);
			space1.setScore(5);
			scoreTime.setScore(4);
			scoreTokens.setScore(2);
			scoreActualTokens.setScore(1);
			score2.setScore(7);
			player.setScoreboard(board);
		}
	}

	public void registerLoadout(Player player) {
		try {
			Statement statement = c.createStatement();
			ResultSet res = statement.executeQuery("SELECT Loadout FROM codmc_plugin_players WHERE Name='" + player.getName() + "';");
			res.next();
			loadouts.put(player.getName(), res.getString("Loadout"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadArenas() {
		List<String> arenas = this.getConfig().getStringList("Arenas");
		for (String arenaData : arenas) {
			String[] parsed = arenaData.split(";");
			List<Location> spawns = new ArrayList<Location>();
			for (int i = 2; i < parsed.length; i ++) {
				String[] parseds = parsed[i].split(":");
				spawns.add(new Location(Bukkit.getWorld(parseds[1]), Double.parseDouble(parseds[2]), Double.parseDouble(parseds[3]), Double.parseDouble(parseds[4])));
			}
			String[] parsedrs = parsed[1].split(":");
			String[] parsedbs = parsed[2].split(":");
			WorldCreator wc = new WorldCreator(parsedrs[1]);
			Bukkit.createWorld(wc);
			Arena arena = new Arena(parsed[0],
					new Location(Bukkit.getWorld(parsedrs[1]), Double.parseDouble(parsedrs[2]), Double.parseDouble(parsedrs[3]), Double.parseDouble(parsedrs[4])),
					new Location(Bukkit.getWorld(parsedbs[1]), Double.parseDouble(parsedbs[2]), Double.parseDouble(parsedbs[3]), Double.parseDouble(parsedbs[4])),
					spawns
			);
			am.arenas.add(arena);
			System.out.println("Arena " + parsed[0] + " has been loaded!");
		}
	}
	
	public void cancelTask() {
		Bukkit.getScheduler().cancelTask(taskId);
	}
	
	public void awardExp(Player player, int exp) {
		if (levels.getSecondValue(player.getName()) - exp <= 0) {
			int currentLevel = levels.getFirstValue(player.getName());
			levels.setFirstValue(player.getName(), currentLevel + 1);
			levels.setSecondValue(player.getName(), (currentLevel + 1) * 6 );
			player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "You've leveled up!");
		} else {
			levels.setSecondValue(player.getName(), levels.getSecondValue(player.getName()) - exp);
		}
	}
	
	public enum GameState {
		LOBBY, SESSION, RESTARTING;
	}
}
