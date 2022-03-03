package com.worldbiomusic.minigameworld.controller.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.PluginDescriptionFile;

import com.wbm.plugin.util.Utils;
import com.worldbiomusic.minigameworld.MiniGameWorldMain;
import com.worldbiomusic.minigameworld.api.MiniGameAccessor;
import com.worldbiomusic.minigameworld.controller.managers.MiniGameStartManager;
import com.worldbiomusic.minigameworld.customevents.minigame.MiniGameStartEvent;

public class MiniGameEventListener implements Listener {
	private MiniGameStartManager minigameStartManager;

	public MiniGameEventListener(MiniGameStartManager minigameStartManager) {
		this.minigameStartManager = minigameStartManager;
	}

	@EventHandler
	public void onMiniGameStart(MiniGameStartEvent e) {
		MiniGameAccessor minigame = e.getMiniGame();

		// cancel or not with start flag
		boolean canStart = this.minigameStartManager.getFlag(minigame);
		Utils.debug("cancelled: " + canStart);
		e.setCancelled(!canStart);
	}

	@EventHandler
	public void onPlayerTryToUseMiniGameWorldCmd(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		if (p.isOp()) {
			return;
		}

		String cmd = e.getMessage().substring(1).split(" ")[0];

		List<String> blockedCmds = new ArrayList<>();
		PluginDescriptionFile pluginYml = MiniGameWorldMain.getInstance().getDescription();
		Map<String, Map<String, Object>> cmdInfo = pluginYml.getCommands();

		// prevent all commands and aliases of MiniGameWorld plugin
		cmdInfo.forEach((k, v) -> {
			blockedCmds.add(k);
			@SuppressWarnings("unchecked")
			List<String> aliases = (List<String>) v.get("aliases");
			aliases.forEach(blockedCmds::add);
		});

		// check cmd is one of blockedCmds
		if (blockedCmds.contains(cmd)) {
			p.sendMessage(ChatColor.RED + "MiniGameWorld plugin is under control by the OPs");
			e.setCancelled(true);
		}

	}
}
