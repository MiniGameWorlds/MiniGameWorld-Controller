package com.worldbiomusic.minigameworld.controller.managers;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.wbm.plugin.util.ItemStackTool;
import com.worldbiomusic.minigameworld.api.MiniGameAccessor;
import com.worldbiomusic.minigameworld.api.MiniGameWorld;
import com.worldbiomusic.minigameworld.api.MiniGameWorldUtils;
import com.worldbiomusic.minigameworld.controller.MiniGameWorldControllerMain;
import com.worldbiomusic.minigameworld.customevents.menu.MenuClickEvent;
import com.worldbiomusic.minigameworld.customevents.minigame.MiniGameServerExceptionEvent;
import com.worldbiomusic.minigameworld.managers.menu.MiniGameMenu;
import com.worldbiomusic.minigameworld.managers.menu.MiniGameMenu.BaseIcon;
import com.worldbiomusic.minigameworld.util.Setting;

public class MiniGameControllerMenuManager {
	private MiniGameControlManager controlManager;

	public enum OptionIcon {
		START_ICON(ItemStackTool.item(Material.ARROW, "Start"), 7),
		CONTROL_ON_ICON(ItemStackTool.item(Material.GREEN_CONCRETE, ChatColor.GREEN + "Control",
				ChatColor.WHITE + "State: Control"), 8),
		CONTROL_OFF_ICON(ItemStackTool.item(Material.RED_CONCRETE, ChatColor.RED + "Control",
				ChatColor.WHITE + "State: Default"), 8);

		private ItemStack item;
		private int slot;

		OptionIcon(ItemStack item, int slot) {
			this.item = item;
			this.slot = slot;
		}

		public ItemStack getItem() {
			return this.item;
		}

		public int getSlot() {
			return this.slot;
		}

		public static boolean checkSlot(int slot) {
			return !Arrays.asList(values()).stream().filter(icon -> icon.getSlot() == slot).toList().isEmpty();
		}

		public static boolean checkIcon(ItemStack targetIcon) {
			return !Arrays.asList(values()).stream().filter(icon -> icon.getItem().equals(targetIcon)).toList()
					.isEmpty();
		}
	}

	public MiniGameControllerMenuManager(MiniGameControlManager controlManager) {
		this.controlManager = controlManager;
	}

	private boolean isUnderControl(Inventory menu) {
		return menu.getItem(OptionIcon.CONTROL_ON_ICON.getSlot()).equals(OptionIcon.CONTROL_ON_ICON.getItem());
	}

	private void changeControlMode(Inventory menu) {
		ItemStack controlIcon = menu.getItem(OptionIcon.CONTROL_ON_ICON.getSlot());
		if (controlIcon.equals(OptionIcon.CONTROL_OFF_ICON.getItem())) {
			menu.setItem(OptionIcon.CONTROL_OFF_ICON.getSlot(), OptionIcon.CONTROL_ON_ICON.getItem());
		} else {
			menu.setItem(OptionIcon.CONTROL_ON_ICON.getSlot(), OptionIcon.CONTROL_OFF_ICON.getItem());
		}
	}

	private boolean isControlIcon(ItemStack icon) {
		return icon.equals(OptionIcon.CONTROL_ON_ICON.getItem()) || icon.equals(OptionIcon.CONTROL_OFF_ICON.getItem());
	}

	public void initMenu(Inventory inv) {
		inv.setItem(OptionIcon.START_ICON.getSlot(), OptionIcon.START_ICON.getItem());
		inv.setItem(OptionIcon.CONTROL_ON_ICON.getSlot(), OptionIcon.CONTROL_ON_ICON.getItem());
	}

	public void processClickEvent(MenuClickEvent e) {
		ItemStack icon = e.getIcon();
		if (icon == null) {
			return;
		}

		if (!isUnderControl(e.getMenu()) && !isControlIcon(icon)) {
			return;
		}

		String area = e.getArea();
		if (!area.startsWith(Setting.MENU_INV_TITLE + ":")) {
			return;
		}

		String secondArea = "";
		if (area.split(":").length >= 2) {
			secondArea = area.split(":")[1];
		}

		// cancel event
		e.setCancelled(true);

		if (isControlRelativeBaseOrOptionIcon(icon)) {
			processOptionIcon(e);
		} else if (secondArea.equalsIgnoreCase("MINIGAME")) {
			processGameIcon(e);
		} else {
			// cancel event cancellation
			e.setCancelled(false);
		}
	}

	private boolean isControlRelativeBaseOrOptionIcon(ItemStack icon) {
		return icon.equals(BaseIcon.LEAVE_GAME.getItem()) || OptionIcon.checkIcon(icon);
	}

	private void processOptionIcon(MenuClickEvent e) {
		ItemStack icon = e.getIcon();
		Player p = e.getPlayer();

		// START_ICON and LEAVE_GAME icon works different in player's position (out of
		// minigame or in minigame)
		if (MiniGameWorldUtils.checkPlayerIsInMiniGame(p)) {
			MiniGameAccessor minigame = MiniGameWorldUtils.getInMiniGame(p);
			String minigameTitle = minigame.getSettings().getTitle();
			if (icon.equals(OptionIcon.START_ICON.getItem())) {
				this.controlManager.startGame(minigameTitle);
			} else if (icon.equals(MiniGameMenu.BaseIcon.LEAVE_GAME.getItem())) {
				this.controlManager.finishGame(minigameTitle);
			}
		} else {

			// start all games
			if (icon.equals(OptionIcon.START_ICON.getItem())) {
				MiniGameWorld mw = MiniGameWorldControllerMain.getMiniGameWorld();
				mw.getMiniGameList().forEach(game -> controlManager.startGame(game.getSettings().getTitle()));
			}
			// finish all games
			else if (icon.equals(MiniGameMenu.BaseIcon.LEAVE_GAME.getItem())) {
				Bukkit.getPluginManager().callEvent(new MiniGameServerExceptionEvent("finished-by-controller"));
			}
		}

		// change control mode
		if (icon.equals(OptionIcon.CONTROL_ON_ICON.getItem()) || icon.equals(OptionIcon.CONTROL_OFF_ICON.getItem())) {
			changeControlMode(e.getMenu());
		}
	}

	private void processGameIcon(MenuClickEvent e) {
		ItemStack icon = e.getIcon();
		MiniGameWorld mw = MiniGameWorldControllerMain.getMiniGameWorld();
		String title = icon.getItemMeta().getDisplayName();

		boolean isShiftClick = e.getInventoryClickEvent().isShiftClick();

		ClickType click = e.getClick();
		if (click == ClickType.LEFT) {
			this.controlManager.joinGame(title);
		} else if (click == ClickType.RIGHT) {
			this.controlManager.viewGame(title);
		} else if (click == ClickType.DROP) {
			this.controlManager.finishGame(title);
		} else if (click == ClickType.DROP && isShiftClick) {
			MiniGameAccessor minigame = MiniGameWorldUtils.getMiniGameWithTitle(title);
			minigame.getViewers().forEach(mw::unviewGame);
		} else if (click == ClickType.SHIFT_LEFT) {
			this.controlManager.startGame(title);
		}
	}
}
