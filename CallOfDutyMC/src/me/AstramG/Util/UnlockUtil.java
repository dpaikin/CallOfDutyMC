package me.AstramG.Util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.AstramG.CallOfDutyMC.CallOfDuty;

import org.bukkit.entity.Player;

public class UnlockUtil {
	
	static CallOfDuty cod;
	
	public UnlockUtil(CallOfDuty cod) {
		UnlockUtil.cod = cod;
	}

	//GUNS(:);PERKS;
	
	public static List<String> getUnlocks(Player player) {
		List<String> unlockables = new ArrayList<String>();
		String unlockablesData = "";
		try {
			Statement statement = cod.c.createStatement();
			ResultSet res = statement.executeQuery("SELECT Unlockables FROM codmc_plugin_players WHERE Name = '" + player.getName() + "';");
			res.next();
			if(res.getString("Unlockables").equalsIgnoreCase("none")) {
				return null;
			} else {
				unlockablesData = res.getString("Unlockables");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String[] unlocksParsed = unlockablesData.split(";");
		for (String unlock : unlocksParsed) {
			unlockables.add(unlock);
		}
		return unlockables;
	}
	
	public static boolean hasGun(Player player, String gunName) {
		String unlocks = getUnlocks(player).get(0);
		List<String> unlocked = Arrays.asList(unlocks.split(":"));
		if (unlocked.contains(gunName)) {
			return true;
		}
		return false;
	}

	public static void unlock(Player player, String unlock, int type) {
		List<String> unlocks = getUnlocks(player);
		List<String> guns = new ArrayList<String>();
		List<String> perks = new ArrayList<String>();
		for (String gunNames : Arrays.asList(unlocks.get(0).split(":"))) {
			guns.add(gunNames);
		}
		for (String perkNames : Arrays.asList(unlocks.get(1).split(":"))) {
			perks.add(perkNames);
		}
		if (type == 1) {
			perks.add(unlock);
		} else {
			guns.add(unlock);
		}
		String newUnlocks = "";
		for (String gunName: guns) {
			newUnlocks += gunName + ":";
			if (guns.indexOf(gunName) == guns.size() - 1) {
				newUnlocks += ";";
			}
		}
		for (String perk: perks) {
			newUnlocks += perk + ":";
			if (perks.indexOf(perk) == perks.size() - 1) {
				newUnlocks += ";";
			}
		}
		try {
			Statement statement = cod.c.createStatement();
			statement.executeUpdate("UPDATE codmc_plugin_players SET Unlockables='" + newUnlocks + "' WHERE Name='" + player.getName() + "'");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
