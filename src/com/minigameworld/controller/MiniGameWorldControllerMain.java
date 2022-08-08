package com.minigameworld.controller;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.wbm.plugin.util.Metrics;
import com.wbm.plugin.util.instance.TaskManager;
import com.minigameworld.api.MiniGameWorld;
import com.minigameworld.controller.cmds.ControlCommand;
import com.minigameworld.controller.listeners.MiniGameEventListener;
import com.minigameworld.controller.managers.MiniGameControlManager;
import com.minigameworld.controller.managers.MiniGameStartFlag;
import com.minigameworld.controller.utils.Utils;

public class MiniGameWorldControllerMain extends JavaPlugin {
	private MiniGameControlManager controlManager;
	private MiniGameStartFlag minigameStartFlag;
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

		this.minigameStartFlag = new MiniGameStartFlag();
		mw = MiniGameWorld.create(MiniGameWorld.API_VERSION);

		// managers
		this.controlManager = new MiniGameControlManager(mw, minigameStartFlag);
	}

	private void setCommands() {
		this.controlCommand = new ControlCommand(this, mw, controlManager);
		getCommand("mwcontrol").setExecutor(this.controlCommand);
	}

	private void registerListener() {
		this.minigameListener = new MiniGameEventListener(this.minigameStartFlag, controlManager);
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
