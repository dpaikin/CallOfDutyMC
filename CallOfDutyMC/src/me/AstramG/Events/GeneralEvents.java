package me.AstramG.Events;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import me.AstramG.CallOfDutyMC.CallOfDuty;
import me.AstramG.CallOfDutyMC.LoadoutManager;
import me.AstramG.Util.LocationUtil;
import net.minecraft.server.v1_7_R3.EntityPlayer;
import net.minecraft.server.v1_7_R3.PacketPlayOutCloseWindow;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class GeneralEvents implements Listener {
	
	static CallOfDuty cod;
	
	public GeneralEvents(CallOfDuty cod) {
		GeneralEvents.cod = cod;
	}
	
	@EventHandler
	public void hunger(FoodLevelChangeEvent event) {
		event.setFoodLevel(20);
		event.setCancelled(true);
	}
	
	@EventHandler
	public void PlayerChat(AsyncPlayerChatEvent event) {
		final Player player = event.getPlayer();
		if (CallOfDuty.instance.changingName.contains(player.getName())) {
			if (event.getMessage().equalsIgnoreCase("cancel")) {
				CallOfDuty.instance.changingName.remove(player.getName());
				player.sendMessage(ChatColor.RED + "You've cancelled the name change!");
				event.setCancelled(true);
				return;
			}
			List<String> loadouts = LoadoutManager.getLoadouts(player);
			for (String loadout : loadouts) {
				String[] data = loadout.split(":");
				if (data[0].equalsIgnoreCase(event.getMessage())) {
					player.sendMessage(ChatColor.RED + "You already have a loadout named " + event.getMessage() + "!");
					player.sendMessage(ChatColor.RED + "Please input a unique loadout name or say 'cancel'!");
					event.setCancelled(true);
					return;
				}
			}
			if (event.getMessage().length() <= 10) {
				LoadoutManager.setLoadoutName(player, event.getMessage());
				CallOfDuty.instance.changingName.remove(player.getName());
				Bukkit.getScheduler().scheduleSyncDelayedTask(CallOfDuty.instance, new BukkitRunnable() {
					public void run() {
						LoadoutManager.createInventory(player);
					}
				}, 1L);
			} else {
				player.sendMessage(ChatColor.RED + "This name is too long! It must be < 10 characters!");
			}
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void completeTab(PlayerChatTabCompleteEvent event) {
		if (event.getChatMessage().length() >= 1) {
			EntityPlayer ep = ((CraftPlayer) event.getPlayer()).getHandle();
			PacketPlayOutCloseWindow packet = new PacketPlayOutCloseWindow();
			ep.playerConnection.sendPacket(packet);
			Player player = event.getPlayer();
			if (CallOfDuty.instance.began) {
				if (CallOfDuty.instance.redTeam.contains(player.getName())) {
					for (String stringer : CallOfDuty.instance.redTeam) {
						Player p = Bukkit.getPlayer(stringer);
						p.sendMessage(ChatColor.GRAY + "[" + ChatColor.RED + ChatColor.BOLD + "Team" + ChatColor.RESET + ChatColor.GRAY + "] " + player.getName() + "⇉ " + event.getChatMessage());
					}
				}
				if (CallOfDuty.instance.blueTeam.contains(player.getName())) {
					for (String stringer : CallOfDuty.instance.blueTeam) {
						Player p = Bukkit.getPlayer(stringer);
						p.sendMessage(ChatColor.GRAY + "[" + ChatColor.BLUE + ChatColor.BOLD + "Team" + ChatColor.RESET + ChatColor.GRAY + "] " + player.getName() + "⇉ " + event.getChatMessage());
					}
				}
			} else {
				player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You can't talk to your team right now!");
			}
		}
	}
	
	@EventHandler
	public void placeBlock(BlockPlaceEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.SURVIVAL) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		event.setLeaveMessage(null);
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (CallOfDuty.instance.began) {
			event.getPlayer().kickPlayer(ChatColor.RED + "The Game has already began!");
			event.setJoinMessage(null);
			return;
		}
		Player player = event.getPlayer();
		try {
			Statement statement = CallOfDuty.instance.c.createStatement();
			statement.executeUpdate("INSERT INTO `codmc_plugin_players` (Name, Loadouts, Unlockables, Loadout, Level) VALUES ('" + player.getName() + "', 'Default:Remington R5:Glock18:none;', 'Remington R5:Glock18;Lightweight:', 'Default', '1:5');");
		} catch (Exception e) {
			//Ignore
		}
		if (!(CallOfDuty.instance.tokens.containsKey(player.getName()))) {
			CallOfDuty.instance.tokens.put(player.getName(), CallOfDuty.instance.token.getTokens(player.getName()));
		}
		CallOfDuty.instance.registerLevels(player);
		CallOfDuty.instance.registerLoadout(player);
		CallOfDuty.instance.changescore(player);
		sendToHub(player);
		player.setHealth(20.0D);
		player.setMaxHealth(20.0D);
		player.setFoodLevel(20);
		player.setLevel(0);
		player.getInventory().clear();
		player.getInventory().setHelmet(new ItemStack(Material.AIR, 1));
		player.getInventory().setChestplate(new ItemStack(Material.AIR, 1));
		player.getInventory().setLeggings(new ItemStack(Material.AIR, 1));
		player.getInventory().setBoots(new ItemStack(Material.AIR, 1));
		ItemStack item = new ItemStack(Material.INK_SACK, 1, (byte) 10);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Select Loadout");
		item.setItemMeta(meta);
		player.getInventory().setItem(4, item);
		player.removePotionEffect(PotionEffectType.SLOW);
		event.setJoinMessage(ChatColor.YELLOW + "> " + ChatColor.DARK_GRAY + player.getName());
	}
	
	@EventHandler
	public void getHurt(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (CallOfDuty.instance.tokens.containsKey(player.getName())) {
			CallOfDuty.instance.token.setTokens(player.getName(), CallOfDuty.instance.tokens.get(player.getName()));
			CallOfDuty.instance.tokens.remove(player.getName());
			try {
				Statement statement = CallOfDuty.instance.c.createStatement();
				statement.executeUpdate("UPDATE CallOfDuty.instancemc_plugin_players SET Loadout='" + CallOfDuty.instance.loadouts.get(player.getName()) + "' WHERE Name='" + player.getName() + "'");
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				Statement statement = CallOfDuty.instance.c.createStatement();
				statement.executeUpdate("UPDATE CallOfDuty.instancemc_plugin_players SET Level='" + CallOfDuty.instance.levels.getFirstValue(player.getName()) + ":" + CallOfDuty.instance.levels.getSecondValue(player.getName()) + "' WHERE Name='" + player.getName() + "'");
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if (CallOfDuty.instance.redTeam.contains(player.getName())) {
				CallOfDuty.instance.redTeam.remove(player.getName());
			} else if (CallOfDuty.instance.blueTeam.contains(player.getName())) {
				CallOfDuty.instance.blueTeam.remove(player.getName());
			}
			CallOfDuty.instance.loadouts.remove(player.getName());
			CallOfDuty.instance.levels.remove(player.getName());
		}
		event.setQuitMessage(ChatColor.RED + "< " + ChatColor.DARK_GRAY + player.getName());
	}
	
	@EventHandler
	public void interact(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (CallOfDuty.instance.began) return;
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (event.getItem() != null) {
				if (event.getItem().getType() == Material.INK_SACK) {
					LoadoutManager.createInventory(player);
				}
			}
		}
	}
	
	@EventHandler
	public void inventoryMove(InventoryClickEvent event) {
		if (event.isCancelled() == false) event.setCancelled(true);
	}
	
	@EventHandler
	public void blockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.SURVIVAL) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void drop(PlayerDropItemEvent event) {
		if (CallOfDuty.instance.began == false) {
			event.setCancelled(true);
		}
	}
	
	public static void sendToHub(Player player) {
		player.teleport(LocationUtil.unserializeLocation(CallOfDuty.instance.getConfig().getString("Hub.Location")));
	}
	
}
