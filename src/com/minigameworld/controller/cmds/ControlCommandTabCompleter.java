package com.minigameworld.controller.cmds;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.minigameworld.api.MiniGameWorld;

/*
 * [Commands]
 * /mwc start [<minigame>]: start <minigame> minigame
 * /mwc finish [<minigame>]: finish <minigame> minigame
 * /mwc <join | leave | view | unview> <minigame> [<player> [<player> [<player> ...]]]: Control all(specific) players (not OP)  
 */
public class ControlCommandTabCompleter implements TabCompleter {
	private MiniGameWorld mw;
	private List<String> candidates;

	public ControlCommandTabCompleter(MiniGameWorld mw) {
		this.mw = mw;
		this.candidates = new ArrayList<>();
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		this.candidates.clear();
		int length = args.length;

		if (length == 2) {
			addMiniGameTitles();
		} else if (length == 1) {
			addLength1Candidates();
		}

		// add player names
		if (this.candidates.isEmpty()) {
			Bukkit.getOnlinePlayers().forEach(p -> candidates.add(p.getName()));
		}

		return this.candidates;
	}

	private void addLength1Candidates() {
		this.candidates.add("start");
		this.candidates.add("finish");
		this.candidates.add("join");
		this.candidates.add("leave");
		this.candidates.add("view");
		this.candidates.add("unview");
	}

	private void addMiniGameTitles() {
		this.mw.getTemplateGames().forEach(m -> {
			String minigameTitle = m.getSettings().getTitle();
			this.candidates.add(minigameTitle);
		});
	}
}
