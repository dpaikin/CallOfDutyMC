package me.AstramG.Util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import me.AstramG.CallOfDutyMC.CallOfDuty;

public class HubWarpUtil {
	
	CallOfDuty cod;
	
	public HubWarpUtil(CallOfDuty cod) {
		this.cod = cod;
	}
	
	public void setCanJoin(boolean canJoin) {
		String data = "";
		try {
			Statement statement = cod.c.createStatement();
			ResultSet res = statement.executeQuery("SELECT Data FROM aliacraft_hub WHERE Tag='" + cod.getConfig().getString("Tag") + "'");
			res.next();
			data = res.getString("Data");
		} catch (Exception e) {
			e.printStackTrace();
		}
		String[] data2 = data.split(";");
		if (canJoin) {
			data2[data2.length - 1] = "true";
		} else {
			data2[data2.length - 1] = "false";
		}
		String newData = "";
		for (String singdat : data2) {
			newData += singdat + ";";
		}
		try {
			Statement statement = cod.c.createStatement();
			statement.executeUpdate("UPDATE aliacraft_hub SET Data='" + newData + "' WHERE Tag='" + cod.getConfig().getString("Tag") + "'");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}