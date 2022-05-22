package com.worldbiomusic.minigameworld.controller;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.wbm.plugin.util.Metrics;
import com.wbm.plugin.util.instance.TaskManager;
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

	private TaskManager taskManager;

	public static MiniGameWorld getMiniGameWorld() {
		return mw;
	}

	@Override
	public void onEnable() {
		super.onEnable();

		// setup
		setup();

		// register command
		setCommands();

		// listeners
		registerListener();

		// permission update task
		runPermissionUpdateTask();
	}

	private void setup() {
		// bstats
		new Metrics(this, 14517);

		this.minigameStartManager = new MiniGameStartManager();
		mw = MiniGameWorld.create(MiniGameWorld.API_VERSION);

		// managers
		this.controlManager = new MiniGameControlManager(mw, minigameStartManager);
	}

	private void setCommands() {
		this.controlCommand = new ControlCommand(this, mw, controlManager);
		getCommand("mwcontrol").setExecutor(this.controlCommand);
	}

	private void registerListener() {
		this.minigameListener = new MiniGameEventListener(this.minigameStartManager, controlManager);
		getServer().getPluginManager().registerEvents(this.minigameListener, this);

	}

	private void runPermissionUpdateTask() {
		this.taskManager = new TaskManager();
		this.taskManager.registerTask("update-permission", () -> {
			Bukkit.getOnlinePlayers().forEach(p -> Utils.setAccessPermission(p, p.isOp()));
		});
		this.taskManager.runTaskTimer("update-permission", 0, 20);
	}

	@Override
	public void onDisable() {
		super.onDisable();

		// stop all tasks
		this.taskManager.cancelAllTasks();
	}
}
