package com.minigameworld.controller.managers;

import java.util.HashMap;
import java.util.Map;

import com.minigameworld.api.MiniGameAccessor;

public class MiniGameStartFlag {
	private Map<String, Boolean> flags;

	public MiniGameStartFlag() {
		this.flags = new HashMap<>();
	}

	public void setFlag(MiniGameAccessor minigame, boolean flag) {
		this.flags.put(getMinigameKey(minigame), flag);
	}

	public boolean getFlag(MiniGameAccessor minigame) {
		String key = getMinigameKey(minigame);
		if (!this.flags.containsKey(key)) {
			return false;
		}

		return this.flags.get(key);
	}

	private String getMinigameKey(MiniGameAccessor m) {
		return m.settings().getTitle() + "_" + m.settings().getId();
	}
}
