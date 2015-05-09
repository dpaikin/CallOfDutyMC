package me.AstramG.CallOfDutyMC;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

public class GunManager {
	
	List<Gun> guns = new ArrayList<Gun>();
	
	CallOfDuty cod;
	
	public GunManager(CallOfDuty cod) {
		this.cod = cod;
	}

	public void addGun(String gunName, String gunData) {
		String[] parsedData = gunData.split(":");
		Gun gun = new Gun();
		gun.gunName = gunName;
		gun.item = Material.valueOf(parsedData[0]);
		gun.data = Integer.valueOf(parsedData[1]);
		gun.gunType = parsedData[2];
		gun.damage = Double.parseDouble(parsedData[3]);
		gun.attachmentId = Integer.parseInt(parsedData[4]);
		gun.fireRate = Double.parseDouble(parsedData[5]);
		gun.price = Integer.parseInt(parsedData[6]);
		gun.bullets = Integer.parseInt(parsedData[7]);
		gun.clip = Integer.parseInt(parsedData[8]);
		gun.maxAmmo = Integer.parseInt(parsedData[9]);
		gun.range = Double.parseDouble(parsedData[10]);
		gun.reload = Long.parseLong(parsedData[11]);
		gun.zoom = Integer.parseInt(parsedData[12]);
		guns.add(gun);
	}
	
	public Gun getGun(String name) {
		for (Gun gun : guns) {
			if (gun.gunName.equalsIgnoreCase(name)) {
				return gun;
			}
		}
		return null;
	}

	public boolean isGun(String gunName) {
		for (Gun gun : guns) {
			if (gunName.equalsIgnoreCase(gun.gunName))
				return true;
		}
		return false;
	}
	
}
