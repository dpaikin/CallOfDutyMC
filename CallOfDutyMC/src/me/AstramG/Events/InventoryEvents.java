package me.AstramG.Events;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import me.AstramG.CallOfDutyMC.CallOfDuty;
import me.AstramG.CallOfDutyMC.LoadoutManager;
import me.AstramG.Util.UnlockUtil;
import me.AstramG.CallOfDutyMC.Gun;

public class InventoryEvents implements Listener{
	
	CallOfDuty cod;
	
	public InventoryEvents(CallOfDuty cod) {
		this.cod = cod;
	}
	
	//private List<String> invNames = Arrays.asList("Primary Guns");
	
	List<String> selectionInventories = Arrays.asList("Primary Guns", "Secondary Guns");
	
	@EventHandler
	public void chooseButton(final InventoryClickEvent event) {
		if (event.getCurrentItem().getType() != Material.AIR && event.getCurrentItem().getItemMeta().getDisplayName() != null && event.getInventory().getType() != InventoryType.CREATIVE) {
			final Player player = (Player) event.getWhoClicked();
			if (event.getInventory().getName().equalsIgnoreCase(ChatColor.GREEN + "" + ChatColor.BOLD + "Loadouts")) {
				final String name = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
				if (LoadoutManager.hasLoadout((Player) event.getViewers().get(0), name)) {
					cod.loadouts.put(player.getName(), name);
					Bukkit.getScheduler().scheduleSyncDelayedTask(cod, new Runnable() {
						public void run() {
							LoadoutManager.createLoadoutInventory(player, name);
						}
					}, 1L);
				}
				event.setCancelled(true);
			}
			if (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).startsWith("Buy Loadout")) {
				int id = Integer.parseInt(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName().split(" ")[2].replace("!", "")));
				LoadoutManager.addNewLoadout(player, id);
				event.setCancelled(true);
			}
			if (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Go back!")) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(cod, new Runnable() {
					public void run() {
						LoadoutManager.createInventory(player);
					}
				}, 1L);
				event.setCancelled(true);
				return;
			}
			if (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Add a Primary!")) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(cod, new Runnable() {
					public void run() {
						LoadoutManager.createPrimaryInventory(player, ChatColor.stripColor(event.getInventory().getName()));
					}
				}, 1L);
				event.setCancelled(true);
			}
			if (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Add a Secondary!")) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(cod, new Runnable() {
					public void run() {
						LoadoutManager.createSecondaryInventory(player, ChatColor.stripColor(event.getInventory().getName()));
					}
				}, 1L);
				event.setCancelled(true);
			}
			if (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Remove a Primary!")) {
				LoadoutManager.removeGunFromLoadout(player, ChatColor.stripColor(event.getInventory().getName()), 0);
				Bukkit.getScheduler().scheduleSyncDelayedTask(cod, new Runnable() {
					public void run() {
						LoadoutManager.createLoadoutInventory(player, ChatColor.stripColor(event.getInventory().getName()));
					}
				}, 1L);
				event.setCancelled(true);
			}
			if (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Remove a Secondary!")) {
				LoadoutManager.removeGunFromLoadout(player, ChatColor.stripColor(event.getInventory().getName()), 1);
				Bukkit.getScheduler().scheduleSyncDelayedTask(cod, new Runnable() {
					public void run() {
						LoadoutManager.createLoadoutInventory(player, ChatColor.stripColor(event.getInventory().getName()));
					}
				}, 1L);
				event.setCancelled(true);
			}
			if (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Change Name!")) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(cod, new Runnable() {
					public void run() {
						LoadoutManager.changeName(player, cod.loadouts.get(player.getName()));
					}
				}, 1L);
				event.setCancelled(true);
			}
			if (cod.gm.isGun(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()))) {
				if (!(UnlockUtil.hasGun(player, ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName())))) {
					Gun gun = cod.gm.getGun(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()));
					if (cod.tokens.get(player.getName()) >= gun.price) {
						UnlockUtil.unlock(player, ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()), 0);
						cod.tokens.put(player.getName(), cod.tokens.get(player.getName()) - gun.price);
						Bukkit.getScheduler().scheduleSyncDelayedTask(cod, new Runnable() {
							public void run() {
								LoadoutManager.createPrimaryInventory(player, ChatColor.stripColor(event.getInventory().getName()));
							}
						}, 1L);
						player.sendMessage(ChatColor.GREEN + "You've just bought the: " + ChatColor.YELLOW + gun.gunName + ChatColor.GREEN + "!");
					} else {
						player.sendMessage(ChatColor.RED + "You can't afford that!");
					}
					event.setCancelled(true);
				} else {
					if (selectionInventories.contains(ChatColor.stripColor(event.getInventory().getName()))) {
						Gun gun = cod.gm.getGun(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()));
						int type = 0;
						if (ChatColor.stripColor(event.getInventory().getName()).equalsIgnoreCase(selectionInventories.get(0))) {
							type = 0;
						} else if (ChatColor.stripColor(event.getInventory().getName()).equalsIgnoreCase(selectionInventories.get(1))) {
							type = 1;
						}
						LoadoutManager.setLoadoutGun(player, cod.loadouts.get(player.getName()), gun.gunName, type);
					}
				}
				Bukkit.getScheduler().scheduleSyncDelayedTask(cod, new Runnable() {
					public void run() {
						LoadoutManager.createLoadoutInventory(player, cod.loadouts.get(player.getName()));
					}
				}, 1L);
				event.setCancelled(true);
			}
			if (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase(LoadoutManager.getLoadoutGun(player, ChatColor.stripColor(event.getInventory().getName())))) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(cod, new Runnable() {
					public void run() {
						LoadoutManager.createPrimaryInventory(player, ChatColor.stripColor(event.getInventory().getName()));
					}
				}, 1L);
				event.setCancelled(true);
			}
			if (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase(LoadoutManager.getLoadoutSecondaryGun(player, ChatColor.stripColor(event.getInventory().getName())))) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(cod, new Runnable() {
					public void run() {
						LoadoutManager.createSecondaryInventory(player, ChatColor.stripColor(event.getInventory().getName()));
					}
				}, 1L);
				event.setCancelled(true);
			}
		}
	}
	
}
