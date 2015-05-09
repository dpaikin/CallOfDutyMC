package me.AstramG.Util;


import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class SetArmour {
	public static void SetBlueArmour(Player player){
		ItemStack helm = new ItemStack(Material.LEATHER_HELMET);
		LeatherArmorMeta meta = (LeatherArmorMeta) helm.getItemMeta();
		meta.setColor(Color.BLUE);
		helm.setItemMeta(meta);
		player.getInventory().setHelmet(helm);
		
		ItemStack body = new ItemStack(Material.LEATHER_CHESTPLATE);
		LeatherArmorMeta meta1 = (LeatherArmorMeta) body.getItemMeta();
		meta1.setColor(Color.BLUE);
		body.setItemMeta(meta1);
		player.getInventory().setChestplate(body);
		
		ItemStack legs = new ItemStack(Material.LEATHER_LEGGINGS);
		LeatherArmorMeta meta2 = (LeatherArmorMeta) legs.getItemMeta();
		meta2.setColor(Color.BLUE);
		legs.setItemMeta(meta2);
		player.getInventory().setLeggings(legs);
		
		ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
		LeatherArmorMeta meta3 = (LeatherArmorMeta) boots.getItemMeta();
		meta3.setColor(Color.BLUE);
		boots.setItemMeta(meta3);
		player.getInventory().setBoots(boots);
	}
	
	public static void SetRedArmour(Player player){
		ItemStack helm = new ItemStack(Material.LEATHER_HELMET);
		LeatherArmorMeta meta = (LeatherArmorMeta) helm.getItemMeta();
		meta.setColor(Color.RED);
		helm.setItemMeta(meta);
		player.getInventory().setHelmet(helm);
		
		ItemStack body = new ItemStack(Material.LEATHER_CHESTPLATE);
		LeatherArmorMeta meta1 = (LeatherArmorMeta) body.getItemMeta();
		meta1.setColor(Color.RED);
		body.setItemMeta(meta1);
		player.getInventory().setChestplate(body);
		
		ItemStack legs = new ItemStack(Material.LEATHER_LEGGINGS);
		LeatherArmorMeta meta2 = (LeatherArmorMeta) legs.getItemMeta();
		meta2.setColor(Color.RED);
		legs.setItemMeta(meta2);
		player.getInventory().setLeggings(legs);
		
		ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
		LeatherArmorMeta meta3 = (LeatherArmorMeta) boots.getItemMeta();
		meta3.setColor(Color.RED);
		boots.setItemMeta(meta3);
		player.getInventory().setBoots(boots);
	}
}
