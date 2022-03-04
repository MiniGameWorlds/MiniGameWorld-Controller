package com.worldbiomusic.minigameworld.controller.utils;

import org.bukkit.entity.Player;

import com.wbm.plugin.util.PlayerTool;

public class Utils {
	public static void setBasicPermissionsToFalse(Player p) {
		PlayerTool.setPermission(p, "minigameworld.signblock", false);
		PlayerTool.setPermission(p, "minigameworld.allcommands", false);
	}
}
