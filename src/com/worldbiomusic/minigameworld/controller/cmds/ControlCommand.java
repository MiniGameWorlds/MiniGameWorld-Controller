package com.worldbiomusic.minigameworld.controller.cmds;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.worldbiomusic.minigameworld.api.MiniGameAccessor;
import com.worldbiomusic.minigameworld.api.MiniGameWorld;
import com.worldbiomusic.minigameworld.api.MiniGameWorldUtils;
import com.worldbiomusic.minigameworld.controller.managers.MiniGameControlManager;
import com.worldbiomusic.minigameworld.controller.managers.MiniGameStartManager;
import com.worldbiomusic.minigameworld.util.Setting;

/*
 * [Commands]
 * /mwc start [<minigame>]: start <minigame> minigame
 * /mwc finish [<minigame>]: finish <minigame> minigame
 * /mwc <join | leave | view | unview> <minigame> [<player> [<player> [<player> ...]]]: Control all(specific) players (not OP)  
 */
public class ControlCommand implements CommandExecutor {
	private JavaPlugin plugin;
	private MiniGameControlManager controlManager;
	private MiniGameStartManager minigameStartManager;
	private MiniGameWorld mw;
	private ControlCommandTabCompleter tabCompleter;

	public ControlCommand(JavaPlugin plugin, MiniGameStartManager minigameStartManager, MiniGameWorld mw,
			MiniGameControlManager controlManager) {
		this.plugin = plugin;
		this.minigameStartManager = minigameStartManager;
		this.mw = mw;
		this.controlManager = controlManager;

		this.tabCompleter = new ControlCommandTabCompleter(mw);
		this.plugin.getCommand("mwcontrol").setTabCompleter(this.tabCompleter);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		// check op
		if (!sender.isOp()) {
			sender.sendMessage("OP only");
			return true;
		}

		try {
			String menu = args[0];

			switch (menu) {
			case "start":
				start(sender, args);
				break;
			case "finish":
				finish(sender, args);
				break;
			case "join":
			case "leave":
			case "view":
			case "unview":
				play(sender, args);
				break;
			}

		} catch (Exception e) {
			if (Setting.DEBUG_MODE) {
				e.printStackTrace();
			}
		}

		return true;
	}

	private void start(CommandSender sender, String[] args) throws Exception {
		// /mwc start [<minigame>]: start <minigame> minigame

		String minigameTitle = "";
		if (args.length == 1) {
			// start minigame sender is already in
			if (!(sender instanceof Player)) {
				sender.sendMessage("Player Only");
				return;
			}

			Player p = (Player) sender;

			// check player is in any minigame to start
			if (!MiniGameWorldUtils.checkPlayerIsInMiniGame(p)) {
				p.sendMessage("You are NOT in any minigame to start");
				return;
			}

			MiniGameAccessor minigame = MiniGameWorldUtils.getInMiniGame(p);
			minigameTitle = minigame.getSettings().getTitle();
		} else if (args.length == 2) {
			// start <minigame> minigame
			minigameTitle = args[1];
		} else {
			return;
		}

		// start game
		boolean isStarted = this.controlManager.startGame(minigameTitle);
		if (!isStarted) {
			sender.sendMessage(minigameTitle + " is not exist");
			return;
		}
	}

	private void finish(CommandSender sender, String[] args) throws Exception {
		// /mwc finish [<minigame>]: finish <minigame> minigame

		String minigameTitle = "";
		if (args.length == 1) {
			// start minigame sender is already in
			if (!(sender instanceof Player)) {
				sender.sendMessage("Player Only");
				return;
			}

			Player p = (Player) sender;

			// check player is in any minigmae to start
			if (!MiniGameWorldUtils.checkPlayerIsInMiniGame(p)) {
				p.sendMessage("You are NOT in any minigame to finish");
				return;
			}

			MiniGameAccessor minigame = MiniGameWorldUtils.getInMiniGame(p);
			minigameTitle = minigame.getSettings().getTitle();
		} else if (args.length == 2) {
			// start <minigame> minigame
			minigameTitle = args[1];
		} else {
			return;
		}

		// finish game
		boolean isFinished = this.controlManager.finishGame(minigameTitle);
		if (!isFinished) {
			sender.sendMessage(minigameTitle + " is not exist");
			return;
		}
	}

	@SuppressWarnings("unchecked")
	private void play(CommandSender sender, String[] args) throws Exception {
		// /mwc <join | leave | view | unview> <minigame> [<player> [<player> [<player>
		// ...]]]:
		// Control all players except for OPs
		List<Player> players = new ArrayList<>();

		// check command has players arguments
		if (args.length < 3) {
			players = (List<Player>) Bukkit.getOnlinePlayers().stream().filter(p -> !p.isOp()).toList();
		} else {
			for (int i = 2; i < args.length; i++) {
				String playerName = args[i];
				Player player = Bukkit.getPlayer(playerName);

				// check player is exist
				if (player != null) {
					players.add(player);
				}
			}
		}

		String minigameTitle = args[1];
		String menu = args[0];

		players.forEach(p -> this.controlManager.option(menu, p, minigameTitle));
	}

}

//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
