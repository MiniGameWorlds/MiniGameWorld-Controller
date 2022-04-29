package com.worldbiomusic.minigameworld.controller.managers;

import org.bukkit.entity.Player;

import com.worldbiomusic.minigameworld.MiniGameWorldMain;
import com.worldbiomusic.minigameworld.api.MiniGameAccessor;
import com.worldbiomusic.minigameworld.api.MiniGameWorld;
import com.worldbiomusic.minigameworld.api.MiniGameWorldUtils;
import com.worldbiomusic.minigameworld.customevents.minigame.MiniGameExceptionEvent;

public class MiniGameControlManager {
	private MiniGameWorld mw;
	private MiniGameStartManager minigameStartManager;

	public MiniGameControlManager(MiniGameWorld mw, MiniGameStartManager minigameStartManager) {
		this.mw = mw;
		this.minigameStartManager = minigameStartManager;
	}

	public boolean startGame(String title) {
		MiniGameAccessor minigame = MiniGameWorldUtils.getMiniGameWithTitle(title);
		if (minigame == null) {
			return false;
		}

		// set flag to true
		this.minigameStartManager.setFlag(minigame, true);

		// start game
		this.mw.startGame(title);

		// set flag to false
		this.minigameStartManager.setFlag(minigame, false);

		return true;
	}

	public boolean finishGame(String title) {
		MiniGameAccessor minigame = MiniGameWorldUtils.getMiniGameWithTitle(title);
		if (minigame == null) {
			return false;
		}

		// call exception event to finish the minigame
		MiniGameExceptionEvent exception = new MiniGameExceptionEvent(minigame, "finished-by-controller");
		MiniGameWorldMain.getInstance().getServer().getPluginManager().callEvent(exception);

		return true;
	}

	public boolean joinGame(Player p, String title) {
		// check title minigame is exist
		if (title == null) {
			p.sendMessage(title + " is not exist");
			return false;
		}

		this.mw.joinGame(p, title);
		return true;
	}

	public void leaveGame(Player p) {
		this.mw.leaveGame(p);
	}

	public boolean viewGame(Player p, String title) {
		// check title minigame is exist
		if (title == null) {
			p.sendMessage(title + " is not exist");
			return false;
		}

		this.mw.viewGame(p, title);
		return true;
	}

	public void unviewGame(Player p) {
		this.mw.unviewGame(p);
	}

	public boolean option(String option, Player p, String title) {
		switch (option) {
		case "start":
			return startGame(title);
		case "finish":
			return finishGame(title);
		case "join":
			return joinGame(p, title);
		case "view":
			return viewGame(p, title);
		case "leave":
			leaveGame(p);
			return true;
		case "unview":
			unviewGame(p);
			return true;
		}
		
		return false;
	}
}
