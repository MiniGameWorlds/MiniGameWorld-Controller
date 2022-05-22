package com.worldbiomusic.minigameworld.controller.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.worldbiomusic.minigameworld.api.MiniGameAccessor;
import com.worldbiomusic.minigameworld.controller.managers.MiniGameControlManager;
import com.worldbiomusic.minigameworld.controller.managers.MiniGameControllerMenuManager;
import com.worldbiomusic.minigameworld.controller.managers.MiniGameStartManager;
import com.worldbiomusic.minigameworld.controller.utils.Utils;
import com.worldbiomusic.minigameworld.customevents.menu.MenuClickEvent;
import com.worldbiomusic.minigameworld.customevents.menu.MenuOpenEvent;
import com.worldbiomusic.minigameworld.customevents.minigame.MiniGameStartEvent;

public class MiniGameEventListener implements Listener {
	private MiniGameStartManager minigameStartManager;
	private MiniGameControllerMenuManager controllerMenuManager;

	public MiniGameEventListener(MiniGameStartManager minigameStartManager, MiniGameControlManager controlManager) {
		this.minigameStartManager = minigameStartManager;
		this.controllerMenuManager = new MiniGameControllerMenuManager(controlManager);
	}

	@EventHandler
	public void onMiniGameStart(MiniGameStartEvent e) {
		MiniGameAccessor minigame = e.getMiniGame();

		// cancel or not with start flag
		boolean canStart = this.minigameStartManager.getFlag(minigame);
		e.setCancelled(!canStart);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		Utils.setAccessPermission(p, p.isOp());
	}

	@EventHandler
	public void onMenuOpenEvent(MenuOpenEvent e) {
		this.controllerMenuManager.initMenu(e.getMenu());
	}

	@EventHandler
	public void onMenuClickEvent(MenuClickEvent e) {
		this.controllerMenuManager.processClickEvent(e);
	}

}
