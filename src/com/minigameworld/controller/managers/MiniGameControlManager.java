package com.minigameworld.controller.managers;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import com.minigameworld.MiniGameWorldMain;
import com.minigameworld.api.MiniGameAccessor;
import com.minigameworld.api.MiniGameWorld;
import com.minigameworld.api.MwUtil;
import com.minigameworld.controller.utils.Settings;
import com.minigameworld.controller.utils.Utils;
import com.minigameworld.events.minigame.MiniGameExceptionEvent;
import com.wbm.plugin.util.SoundTool;
import com.wbm.plugin.util.instance.Counter;

public class MiniGameControlManager {
	private MiniGameWorld mw;
	private MiniGameStartFlag minigameStartFlag;

	public MiniGameControlManager(MiniGameWorld mw, MiniGameStartFlag minigameStartFlag) {
		this.mw = mw;
		this.minigameStartFlag = minigameStartFlag;
	}

	public boolean startGame(String title) {
		return startGame(title, Settings.START_DELAY);
	}

	public boolean startGame(String title, int delay) {
		MiniGameAccessor minigame = Utils.getGame(title);
		if (minigame == null) {
			sendMsgToOps(title + " has no instance to be started");
			return false;
		}

		Counter counter = new Counter(delay);

		new BukkitRunnable() {
			@Override
			public void run() {
				if (counter.getCount() > 0) {
					minigame.players().forEach(p -> {
						p.sendMessage(ChatColor.AQUA + "Starts in ... " + ChatColor.RED + ChatColor.BOLD
								+ counter.getCount() + ChatColor.RESET + " seconds");
						SoundTool.play(p, Sound.BLOCK_NOTE_BLOCK_BIT);
					});
					counter.removeCount(1);
				} else {
					// set flag to true
					minigameStartFlag.setFlag(minigame, true);

					// start game
					String id = minigame.settings().getId();
					mw.startGame(title, id);

					// set flag to false
					minigameStartFlag.setFlag(minigame, false);

					// cancel task
					cancel();
				}
			}
		}.runTaskTimer(MiniGameWorldMain.getInstance(), 0, 20);

		// msg to OPs
		sendMsgToOps(title + ChatColor.AQUA + " starts in ... " + ChatColor.RED + ChatColor.BOLD + Settings.START_DELAY
				+ ChatColor.RESET + " seconds");
		return true;
	}

	public boolean finishGame(String title) {
		MiniGameAccessor minigame = Utils.getGame(title);
		if (minigame == null) {
			sendMsgToOps(title + " has no instance to be finished");
			return false;
		}

		int count = minigame.players().size();

		// call exception event to finish the game
		MiniGameExceptionEvent exception = new MiniGameExceptionEvent(minigame, "finished-by-controller");
		MiniGameWorldMain.getInstance().getServer().getPluginManager().callEvent(exception);

		// msg to OPs
		sendMsgToOps(title + " has finished (" + count + " players quit)");
		return true;
	}

	public boolean joinGame(String title) {
		MiniGameAccessor minigame = Utils.getGame(title);
		if (minigame != null && minigame.isStarted()) {
			sendMsgToOps(title + " game has already started");
			return false;
		}

		int joinedCount = MwUtil.getPlayingGamePlayers(Utils.nonOps(), true).size();
		
		Utils.nonOps().forEach(p -> this.mw.joinGame(p, title));
		joinedCount -= MwUtil.getPlayingGamePlayers(Utils.nonOps(), true).size();

		// msg to OPs
		sendMsgToOps(joinedCount + " players joined " + title);
		return true;
	}

	public boolean leaveGame() {
		Utils.nonOps().forEach(mw::leaveGame);

		// msg to OPs
		sendMsgToOps("Players left games");
		return true;
	}

	public boolean viewGame(String title) {
		MiniGameAccessor minigame = Utils.getGame(title);
		if (minigame == null) {
			sendMsgToOps(title + " has no instance to view");
			return false;
		}

		int joinedCount = minigame.viewers().size();
		Utils.nonOps().forEach(p -> this.mw.viewGame(p, title));
		joinedCount = minigame.viewers().size() - joinedCount;

		// msg to OPs
		sendMsgToOps(joinedCount + " players started viewing " + title);

		return true;
	}

	public boolean unviewGame() {
		Utils.nonOps().forEach(mw::unviewGame);

		// msg to OPs
		sendMsgToOps("Players left game views");
		return true;
	}

	private void sendMsgToOps(String msg) {
		Utils.ops().forEach(p -> p.sendMessage(msg));
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
			return leaveGame();
		case "unview":
			return unviewGame();
		}
		return false;
	}

}
