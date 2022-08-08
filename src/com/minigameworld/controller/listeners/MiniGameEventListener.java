package com.minigameworld.controller.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.minigameworld.api.MiniGameAccessor;
import com.minigameworld.controller.managers.MiniGameControlManager;
import com.minigameworld.controller.managers.MiniGameControllerMenuManager;
import com.minigameworld.controller.managers.MiniGameStartFlag;
import com.minigameworld.controller.utils.Utils;
import com.minigameworld.events.menu.MenuClickEvent;
import com.minigameworld.events.menu.MenuOpenEvent;
import com.minigameworld.events.minigame.MiniGameStartEvent;
import com.minigameworld.events.minigame.instance.MiniGameInstanceCreateEvent;

public class MiniGameEventListener implements Listener {
	private MiniGameStartFlag minigameStartFlag;
	private MiniGameControllerMenuManager controllerMenuManager;

	public MiniGameEventListener(MiniGameStartFlag minigameStartFlag, MiniGameControlManager controlManager) {
		this.minigameStartFlag = minigameStartFlag;
		this.controllerMenuManager = new MiniGameControllerMenuManager(controlManager);
	}

	@EventHandler
	public void onMiniGameStart(MiniGameStartEvent e) {
		MiniGameAccessor minigame = e.getMiniGame();

		// cancel or not with start flag
		boolean canStart = this.minigameStartFlag.getFlag(minigame);
		System.out.println(minigame.getSettings().getTitle() + ", canStart: " + canStart + ", id: "
				+ minigame.getSettings().getId());
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

	@EventHandler
	public void onGameInstanceCreate(MiniGameInstanceCreateEvent e) {
		int instanceCount = e.instances().size();

		// force game has only 1 instance to control games
		if (instanceCount >= 1) {
			e.setCancelled(true);

			// msg
			Utils.ops().forEach(op -> op.sendMessage("Game cab have only 1 instance if you're using MiniGameWorld-Controller plugin"));
		}
	}

}
