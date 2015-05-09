package me.AstramG.CallOfDutyMC;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.AstramG.Util.UnlockUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class LoadoutManager {
	
	static CallOfDuty cod;
	
	public LoadoutManager(CallOfDuty cod) {
		LoadoutManager.cod = cod;
	}
	
	public String getChosenLoadout(Player player) {
		return "";
	}
	
	public static boolean hasLoadout(Player player, String loadoutName) {
		List<String> loadouts = getLoadouts(player);
		for (String loadout : loadouts) {
			String[] parsed = loadout.split(":");
			if (parsed[0].equalsIgnoreCase(loadoutName))
				return true;
		}
		return false;
	}
	
	public static List<String> getLoadouts(Player player) {
		List<String> loadouts = new ArrayList<String>();
		String loadoutsData = "";
		try {
			Statement statement = cod.c.createStatement();
			ResultSet res = statement.executeQuery("SELECT Loadouts FROM codmc_plugin_players WHERE Name = '" + player.getName() + "';");
			res.next();
			if(res.getString("Loadouts").equalsIgnoreCase("none")) {
				return null;
			} else {
				loadoutsData = res.getString("Loadouts");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String[] loadoutsParsed = loadoutsData.split(";");
		for (String loadout : loadoutsParsed) {
			loadouts.add(loadout);
		}
		return loadouts;
	}
	
	public static void createInventory(Player player) {
		Inventory inv = Bukkit.createInventory(null, 27, ChatColor.GREEN + "" + ChatColor.BOLD + "Loadouts");
		List<String> loadouts = getLoadouts(player);
		int i = 0;
		if (loadouts != null) {
			for (String loadout : loadouts) {
				String[] loadoutData = loadout.split(":");
				ItemStack item = new ItemStack(Material.INK_SACK, 1, (byte) 10);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + loadoutData[0]);
				meta.setLore(Arrays.asList(ChatColor.YELLOW + "" + ChatColor.BOLD + "Gun: " + ChatColor.RESET + ChatColor.RED + loadoutData[1], ChatColor.YELLOW + "" + ChatColor.BOLD + "Secondary: " + ChatColor.RESET + ChatColor.RED + loadoutData[2]));
				item.setItemMeta(meta);
				inv.setItem((i * 2) + 10, item);
				i ++;
			}
		}
		for (int z = loadouts.size(); z <= 3; z ++) {
			ItemStack item = new ItemStack(Material.INK_SACK, 1, (byte) 8);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "Buy Loadout " + z + "!");
			meta.setLore(Arrays.asList(ChatColor.RESET + "" + ChatColor.YELLOW + ChatColor.BOLD + "Price: " + ChatColor.RESET + ChatColor.GREEN + 500 * z));
			item.setItemMeta(meta);
			inv.setItem(10 + (z * 2), item);
		}
		player.openInventory(inv);
	}

	public static String getLoadoutGun(Player player, String loadoutName) {
		List<String> loadouts = getLoadouts(player);
		for (String s : loadouts) {
			String[] loadoutsData = s.split(":");
			if (loadoutsData[0].equalsIgnoreCase(loadoutName)) {
				return loadoutsData[1];
			}
		}
		return "N/A";
	}
	
	public static String getLoadoutSecondaryGun(Player player, String loadoutName) {
		List<String> loadouts = getLoadouts(player);
		for (String s : loadouts) {
			String[] loadoutsData = s.split(":");
			if (loadoutsData[0].equalsIgnoreCase(loadoutName)) {
				return loadoutsData[2];
			}
		}
		return "N/A";
	}
	
	public static void createLoadoutInventory(Player player, String name) {
		Inventory invl = Bukkit.createInventory(null, 54, ChatColor.RED + "" + ChatColor.BOLD + name);
		String gunString = getLoadoutGun(player, name);
		String[] splitGun = gunString.split(",");
		String gunName = splitGun[0];
		if (cod.gm.guns.contains(cod.gm.getGun(gunName))) {
			Gun gun = cod.gm.getGun(gunName);
			ItemStack item = new ItemStack(gun.item, 1, (byte) gun.data);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + gun.gunName);
			meta.setLore(Arrays.asList(ChatColor.YELLOW + "" + ChatColor.ITALIC + "Currenlty Equipped Primary", ChatColor.RESET + "" + ChatColor.RED + ChatColor.BOLD + "Damage: " + gun.damage, ChatColor.RESET + "" + ChatColor.RED + ChatColor.BOLD + "Fire Rate: " + gun.fireRate));
			item.setItemMeta(meta);
			invl.setItem(10, item);
			ItemStack itemRemove = new ItemStack(Material.LAVA, 1);
			ItemMeta itemRemoveMeta = itemRemove.getItemMeta();
			itemRemoveMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Remove a Primary!");
			itemRemove.setItemMeta(itemRemoveMeta);
			invl.setItem(1, itemRemove);
			
		} else {
			ItemStack itemAdd = new ItemStack(Material.WATER, 1);
			ItemMeta itemAddMeta = itemAdd.getItemMeta();
			itemAddMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Add a Primary!");
			itemAdd.setItemMeta(itemAddMeta);
			invl.setItem(1, itemAdd);
		}
		
		String secondaryString = getLoadoutSecondaryGun(player, name);
		String[] secondarySplit = secondaryString.split(",");
		String secondaryName = secondarySplit[0];
		if (cod.gm.guns.contains(cod.gm.getGun(secondaryName))) {
			Gun secondary = cod.gm.getGun(secondaryName);
			ItemStack item2 = new ItemStack(secondary.item, 1, (byte) secondary.data);
			ItemMeta meta2 = item2.getItemMeta();
			meta2.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + secondary.gunName);
			meta2.setLore(Arrays.asList(ChatColor.YELLOW + "" + ChatColor.ITALIC + "Currenlty Equipped Secondary", ChatColor.RESET + "" + ChatColor.RED + ChatColor.BOLD + "Damage: " + secondary.damage, ChatColor.RESET + "" + ChatColor.RED + ChatColor.BOLD + "Fire Rate: " + secondary.fireRate));
			item2.setItemMeta(meta2);
			invl.setItem(12, item2);
			ItemStack itemRemove = new ItemStack(Material.LAVA, 1);
			ItemMeta itemRemoveMeta = itemRemove.getItemMeta();
			itemRemoveMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Remove a Secondary!");
			itemRemove.setItemMeta(itemRemoveMeta);
			invl.setItem(3, itemRemove);
		} else {
			ItemStack itemAdd = new ItemStack(Material.WATER, 1);
			ItemMeta itemAddMeta = itemAdd.getItemMeta();
			itemAddMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Add a Secondary!");
			itemAdd.setItemMeta(itemAddMeta);
			invl.setItem(3, itemAdd);
		}
		
		ItemStack changeName = new ItemStack(Material.INK_SACK, 1, (byte) 1);
		ItemMeta cmeta = changeName.getItemMeta();
		cmeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Change Name!");
		changeName.setItemMeta(cmeta);
		invl.setItem(52, changeName);
		
		ItemStack item = new ItemStack(Material.FIRE, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Go back!");
		item.setItemMeta(meta);
		invl.setItem(53, item);
		
		player.openInventory(invl);
		try {
			Statement statement = cod.c.createStatement();
			statement.executeUpdate("UPDATE codmc_plugin_players SET Loadout='" + cod.loadouts.get(player.getName()) + "' WHERE Name='" + player.getName() + "'");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void createPrimaryInventory(Player player, String name) {
		Inventory inventory = Bukkit.createInventory(null, 9 * 3, ChatColor.RED + "Primary Guns");
		for (Gun g : cod.gm.guns) {
			if (!(g.gunType.equalsIgnoreCase("secondary"))) {
				ItemStack item = new ItemStack(g.item, 1, (byte) g.data);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + g.gunName);
				String unlockS = UnlockUtil.getUnlocks(player).get(0);
				List<String> unlocks = Arrays.asList(unlockS.split(":"));
				boolean equipped = cod.gm.getGun(LoadoutManager.getLoadoutGun(player, name)) == g;
				if (equipped) {
					meta.setLore(Arrays.asList(ChatColor.YELLOW + "" + ChatColor.ITALIC + "Damage: " + g.damage, ChatColor.YELLOW + "" + ChatColor.ITALIC + "Fire Rate: " + g.fireRate, ChatColor.GREEN + "" + ChatColor.ITALIC + "Currently Equipped"));
				} else {
					if (!(unlocks.contains(g.gunName))) {
						item.setType(Material.WEB);
						meta.setLore(Arrays.asList(ChatColor.YELLOW + "" + ChatColor.ITALIC + "Damage: " + g.damage, ChatColor.YELLOW + "" + ChatColor.ITALIC + "Fire Rate: " + g.fireRate, ChatColor.GRAY + "Locked", ChatColor.GRAY + "Price: " + ChatColor.YELLOW + g.price));
					} else {
						meta.setLore(Arrays.asList(ChatColor.YELLOW + "" + ChatColor.ITALIC + "Damage: " + g.damage, ChatColor.YELLOW + "" + ChatColor.ITALIC + "Fire Rate: " + g.fireRate));
					}
				}
				item.setItemMeta(meta);
				inventory.addItem(item);
				
			}
		}
		ItemStack back = new ItemStack(Material.FIRE, 1);
		ItemMeta backMeta = back.getItemMeta();
		backMeta.setDisplayName(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Go back!");
		back.setItemMeta(backMeta);
		inventory.setItem(26, back);
		
		player.openInventory(inventory);
	}
	
	public static void createSecondaryInventory(Player player, String name) {
		Inventory inventory = Bukkit.createInventory(null, 9 * 3, ChatColor.RED + "Secondary Guns");
		for (Gun g : cod.gm.guns) {
			if (g.gunType.equalsIgnoreCase("secondary")) {
				ItemStack item = new ItemStack(g.item, 1, (byte) g.data);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + g.gunName);
				String unlockS = UnlockUtil.getUnlocks(player).get(0);
				List<String> unlocks = Arrays.asList(unlockS.split(":"));
				boolean equipped = cod.gm.getGun(LoadoutManager.getLoadoutGun(player, name)) == g;
				if (equipped) {
					meta.setLore(Arrays.asList(ChatColor.YELLOW + "" + ChatColor.ITALIC + "Damage: " + g.damage, ChatColor.YELLOW + "" + ChatColor.ITALIC + "Fire Rate: " + g.fireRate, ChatColor.GREEN + "" + ChatColor.ITALIC + "Currently Equipped"));
				} else {
					if (!(unlocks.contains(g.gunName))) {
						item.setType(Material.WEB);
						meta.setLore(Arrays.asList(ChatColor.YELLOW + "" + ChatColor.ITALIC + "Damage: " + g.damage, ChatColor.YELLOW + "" + ChatColor.ITALIC + "Fire Rate: " + g.fireRate, ChatColor.GRAY + "Locked", ChatColor.GRAY + "Price: " + ChatColor.YELLOW + g.price));
					} else {
						meta.setLore(Arrays.asList(ChatColor.YELLOW + "" + ChatColor.ITALIC + "Damage: " + g.damage, ChatColor.YELLOW + "" + ChatColor.ITALIC + "Fire Rate: " + g.fireRate));
					}
				}
				item.setItemMeta(meta);
				inventory.addItem(item);
				
			}
		}
		ItemStack back = new ItemStack(Material.FIRE, 1);
		ItemMeta backMeta = back.getItemMeta();
		backMeta.setDisplayName(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Go back!");
		back.setItemMeta(backMeta);
		inventory.setItem(26, back);
		
		player.openInventory(inventory);
	}
	
	public static void setLoadoutName(Player player, String loadoutName) {
		List<String> loadouts = getLoadouts(player);
		String loadoutData = "";
		String currentLoadoutName = cod.loadouts.get(player.getName());
		for (String l : loadouts) {
			String[] parsedData = l.split(":");
			if (currentLoadoutName.equalsIgnoreCase(parsedData[0])) {
				loadoutData += loadoutName + ":" + parsedData[1] + ":" + parsedData[2] + ":" + parsedData[3] + ";";
			} else {
				loadoutData += parsedData[0] + ":" + parsedData[1] + ":" + parsedData[2] + ":" + parsedData[3] + ";";
			}
		}
		cod.loadouts.put(player.getName(), loadoutName);
		try {
			Statement statement = cod.c.createStatement();
			statement.executeUpdate("UPDATE codmc_plugin_players SET Loadouts='" + loadoutData + "' WHERE Name='" + player.getName() + "'");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			Statement statement = cod.c.createStatement();
			statement.executeUpdate("UPDATE codmc_plugin_players SET Loadout='" + cod.loadouts.get(player.getName()) + "' WHERE Name='" + player.getName() + "'");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void removeGunFromLoadout(Player player, String loadoutName, int type) {
		List<String> loadouts = getLoadouts(player);
		String loadoutStuff = "";
		for (String loadout : loadouts) {
			String[] loadoutData = loadout.split(":");
			if (!(loadoutData[0].equalsIgnoreCase(loadoutName))) {
				loadoutStuff += loadout + ";";
			} else {
				List<String> data = new ArrayList<String>();
				for (String d : loadoutData) {
					data.add(d);
				}
				if (type == 0) { //Primary
					data.set(1, "none");
				} else if (type == 1) { //Secondary
					data.set(2, "none");
				}
				loadoutStuff += data.get(0) + ":" + data.get(1) + ":" + data.get(2) + ":" + data.get(3) + ";";
			}
			try {
				Statement statement = cod.c.createStatement();
				statement.executeUpdate("UPDATE codmc_plugin_players SET Loadouts='" + loadoutStuff + "' WHERE Name='" + player.getName() + "'");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void setLoadoutGun(Player player, String loadoutName, String gunName, int type) {
		List<String> loadouts = getLoadouts(player);
		String loadoutStuff = "";
		for (String loadout : loadouts) {
			String[] loadoutData = loadout.split(":");
			if (!(loadoutData[0].equalsIgnoreCase(loadoutName))) {
				loadoutStuff += loadout + ";";
			} else {
				List<String> data = new ArrayList<String>();
				for (String d : loadoutData) {
					data.add(d);
				}
				if (type == 0) { //Primary
					data.set(1, gunName);
				} else if (type == 1) { //Secondary
					data.set(2, gunName);
				}
				loadoutStuff += data.get(0) + ":" + data.get(1) + ":" + data.get(2) + ":" + data.get(3) + ";";
			}
			try {
				Statement statement = cod.c.createStatement();
				statement.executeUpdate("UPDATE codmc_plugin_players SET Loadouts='" + loadoutStuff + "' WHERE Name='" + player.getName() + "'");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void changeName(Player player, String string) {
		player.closeInventory();
		player.sendMessage(ChatColor.GREEN + "===============[" + ChatColor.YELLOW + ChatColor.BOLD + "Loadout" + ChatColor.RESET + ChatColor.GREEN + "]===============");
		player.sendMessage(ChatColor.YELLOW + "Please enter the new name of the loadout");
		player.sendMessage(ChatColor.RED + "Type 'cancel' to cancel changing the name of the loadout");
		player.sendMessage(ChatColor.GREEN + "===============[" + ChatColor.YELLOW + ChatColor.BOLD + "Loadout" + ChatColor.RESET + ChatColor.GREEN + "]===============");
		cod.changingName.add(player.getName());
	}

	public static void addNewLoadout(final Player player, int id) {
		if (cod.tokens.get(player.getName()) - (id * 500) >= 0) {
			List<String> loadouts = getLoadouts(player);
			loadouts.add("Loadout " + (id + 1) + ":Remington R5:Glock18:none;");
			String loadoutString = "";
			for (String loadout : loadouts) {
				loadoutString += loadout + ";";
			}
			try {
				Statement statement = cod.c.createStatement();
				statement.executeUpdate("UPDATE codmc_plugin_players SET Loadouts='" + loadoutString + "' WHERE Name='" + player.getName() + "'");
			} catch (SQLException e) {
				e.printStackTrace();
			}
			cod.tokens.put(player.getName(), cod.tokens.get(player.getName()) - (id * 500));
			player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "You've successfully purhcased a new loadout!");
			Bukkit.getScheduler().scheduleSyncDelayedTask(cod, new BukkitRunnable() {
				public void run() {
					createInventory(player);
				}
			}, 1L);
		} else {
			player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You can not afford this!");
		}
	}
	
}
