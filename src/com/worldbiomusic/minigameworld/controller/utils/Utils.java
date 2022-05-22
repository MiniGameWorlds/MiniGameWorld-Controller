package com.worldbiomusic.minigameworld.controller.utils;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.wbm.plugin.util.PlayerTool;

public class Utils {
	public static void setAccessPermission(Player p, boolean access) {
		PlayerTool.setPermission(p, "minigameworld.access.*", access);
	}

	@SuppressWarnings("unchecked")
	public static List<Player> getNonOps() {
		return (List<Player>) Bukkit.getOnlinePlayers().stream().filter(p -> !p.isOp()).toList();
	}

	@SuppressWarnings("unchecked")
	public static List<Player> getOps() {
		return (List<Player>) Bukkit.getOnlinePlayers().stream().filter(p -> p.isOp()).toList();
	}
}
