package com.worldbiomusic.minigameworld.controller.managers;

import java.util.HashMap;
import java.util.Map;

import com.worldbiomusic.minigameworld.api.MiniGameAccessor;

public class MiniGameStartManager {
	private Map<String, Boolean> flags;

	public MiniGameStartManager() {
		this.flags = new HashMap<>();
	}

	public void setFlag(MiniGameAccessor minigame, boolean flag) {
		String minigameTitle = (String) minigame.getSettings().get("title");
		this.flags.put(minigameTitle, flag);
	}

	public boolean getFlag(MiniGameAccessor minigame) {
		String minigameTitle = (String) minigame.getSettings().get("title");
		if (!this.flags.containsKey(minigameTitle)) {
			return false;
		}

		return this.flags.get(minigameTitle);
	}
}
