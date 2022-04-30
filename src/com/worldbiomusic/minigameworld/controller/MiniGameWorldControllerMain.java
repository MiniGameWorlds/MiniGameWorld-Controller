package com.worldbiomusic.minigameworld.controller;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.wbm.plugin.util.Metrics;
import com.worldbiomusic.minigameworld.api.MiniGameWorld;
import com.worldbiomusic.minigameworld.controller.cmds.ControlCommand;
import com.worldbiomusic.minigameworld.controller.listeners.MiniGameEventListener;
import com.worldbiomusic.minigameworld.controller.managers.MiniGameControlManager;
import com.worldbiomusic.minigameworld.controller.managers.MiniGameStartManager;
import com.worldbiomusic.minigameworld.controller.utils.Utils;

public class MiniGameWorldControllerMain extends JavaPlugin {
	private MiniGameControlManager controlManager;
	private MiniGameStartManager minigameStartManager;
	private ControlCommand controlCommand;
	private MiniGameEventListener minigameListener;
	static private MiniGameWorld mw;

	public static MiniGameWorld getMiniGameWorld() {
		return mw;
	}

	@Override
	public void onEnable() {
		super.onEnable();

		// bstats
		new Metrics(this, 14517);

		this.minigameStartManager = new MiniGameStartManager();
		mw = MiniGameWorld.create(MiniGameWorld.API_VERSION);

		// managers
		this.controlManager = new MiniGameControlManager(mw, minigameStartManager);

		// register command
		this.controlCommand = new ControlCommand(this, mw, controlManager);
		getCommand("mwcontrol").setExecutor(this.controlCommand);

		// listeners
		this.minigameListener = new MiniGameEventListener(this.minigameStartManager, controlManager);
		getServer().getPluginManager().registerEvents(this.minigameListener, this);

		// set basic permissions to false
		Bukkit.getOnlinePlayers().stream().filter(p -> !p.isOp()).forEach(Utils::setBasicPermissionsToFalse);
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}
}
