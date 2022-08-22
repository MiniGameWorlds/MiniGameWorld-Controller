package com.minigameworld.controller.utils;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.minigameworld.api.MiniGameAccessor;
import com.minigameworld.controller.MiniGameWorldControllerMain;
import com.wbm.plugin.util.PlayerTool;

public class Utils {
	public static void setAccessPermission(Player p, boolean access) {
		PlayerTool.setPermission(p, "minigameworld.access.*", access);
	}

	@SuppressWarnings("unchecked")
	public static List<Player> nonOps() {
		return (List<Player>) Bukkit.getOnlinePlayers().stream().filter(p -> !p.isOp()).toList();
	}

	@SuppressWarnings("unchecked")
	public static List<Player> ops() {
		return (List<Player>) Bukkit.getOnlinePlayers().stream().filter(p -> p.isOp()).toList();
	}

	/**
	 * Get instance game with title <br>
	 * In MiniGameWorld-Controller plugin, there is only 1 instance game by blocking the instance creation event
	 * 
	 * @param rawTitle Game title
	 * @return Null if game with the title doesn't exist
	 */
	public static MiniGameAccessor getGame(String rawTitle) {
		String title = ChatColor.stripColor(rawTitle);
		List<MiniGameAccessor> minigames = MiniGameWorldControllerMain.getMiniGameWorld().instanceGames().stream()
				.filter(g -> g.settings().getTitle().equals(title)).toList();

		return minigames.isEmpty() ? null : minigames.get(0);
	}
}
