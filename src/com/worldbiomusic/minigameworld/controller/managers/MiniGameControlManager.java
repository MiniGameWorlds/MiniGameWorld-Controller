package com.worldbiomusic.minigameworld.controller.managers;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import com.wbm.plugin.util.SoundTool;
import com.wbm.plugin.util.instance.Counter;
import com.worldbiomusic.minigameworld.MiniGameWorldMain;
import com.worldbiomusic.minigameworld.api.MiniGameAccessor;
import com.worldbiomusic.minigameworld.api.MiniGameWorld;
import com.worldbiomusic.minigameworld.api.MiniGameWorldUtils;
import com.worldbiomusic.minigameworld.controller.utils.Settings;
import com.worldbiomusic.minigameworld.controller.utils.Utils;
import com.worldbiomusic.minigameworld.customevents.minigame.MiniGameExceptionEvent;

public class MiniGameControlManager {
	private MiniGameWorld mw;
	private MiniGameStartManager minigameStartManager;

	public MiniGameControlManager(MiniGameWorld mw, MiniGameStartManager minigameStartManager) {
		this.mw = mw;
		this.minigameStartManager = minigameStartManager;
	}

	public boolean startGame(String title) {
		return startGame(title, Settings.START_DELAY);
	}

	public boolean startGame(String title, int delay) {
		MiniGameAccessor minigame = MiniGameWorldUtils.getMiniGameWithTitle(title);
		if (minigame == null) {
			return false;
		}

		Counter counter = new Counter(delay);

		new BukkitRunnable() {
			@Override
			public void run() {
				if (counter.getCount() > 0) {
					minigame.getPlayers().forEach(p -> {
						p.sendMessage(ChatColor.AQUA + "Starts in ... " + ChatColor.RED + ChatColor.BOLD
								+ counter.getCount() + ChatColor.RESET + " seconds");
						SoundTool.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT);
					});
					counter.removeCount(1);
				} else {
					// set flag to true
					minigameStartManager.setFlag(minigame, true);

					// start game
					mw.startGame(title);

					// set flag to false
					minigameStartManager.setFlag(minigame, false);

					// cancel task
					cancel();
				}
			}
		}.runTaskTimer(MiniGameWorldMain.getInstance(), 0, 20);

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

	public boolean joinGame(String title) {
		// check title minigame is exist
		if (title == null) {
			return false;
		}

		Utils.getNonOps().forEach(p -> this.mw.joinGame(p, title));
		return true;
	}

	public void leaveGame() {
		Utils.getNonOps().forEach(p -> this.mw.leaveGame(p));
	}

	public boolean viewGame(String title) {
		// check title minigame is exist
		if (title == null) {
			return false;
		}

		Utils.getNonOps().forEach(p -> this.mw.viewGame(p, title));
		return true;
	}

	public void unviewGame() {
		Utils.getNonOps().forEach(p -> this.mw.unviewGame(p));
	}

	public boolean option(String option, String title) {
		switch (option) {
		case "start":
			return startGame(title);
		case "finish":
			return finishGame(title);
		case "join":
			return joinGame(title);
		case "view":
			return viewGame(title);
		case "leave":
			leaveGame();
			return true;
		case "unview":
			unviewGame();
			return true;
		}

		return false;
	}
}
