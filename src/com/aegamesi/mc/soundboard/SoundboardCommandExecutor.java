package com.aegamesi.mc.soundboard;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SoundboardCommandExecutor implements CommandExecutor {
	public SoundboardPlugin plugin;

	public SoundboardCommandExecutor(SoundboardPlugin plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// setup
		boolean isPlayer = sender instanceof Player;
		Player p = null;
		if (isPlayer)
			p = (Player) sender;

		// pre-check
		if (!cmd.getName().equalsIgnoreCase("sb"))
			return false;
		if (!isPlayer) {
			SoundboardUtil.message(sender, "&cYou can't use Soundboard from the console!");
			return true;
		}
		if (!sender.hasPermission("soundboard.*")) {
			SoundboardUtil.message(sender, "&cYou don't have permission to do that!");
			return true;
		}
		if (args.length == 0) {
			sendHelp(sender, 1);
			return true;
		}

		if (args[0].equalsIgnoreCase("bind")) {
			ItemStack stack = p.getInventory().getItemInHand();
			if (stack == null) {
				SoundboardUtil.message(sender, "Error: No bound item");
				return true;
			}
			SoundboardPlugin.playerMap.get(p.getName()).bound = stack.getTypeId();
			SoundboardUtil.message(sender, "Bound item " + stack.getTypeId());
			return true;
		}
		if (args[0].equalsIgnoreCase("unbind")) {
			SoundboardPlugin.playerMap.get(p.getName()).bound = -1;
			SoundboardUtil.message(sender, "Unbound");
			return true;
		}
		if (args[0].equalsIgnoreCase("set")) {
			if (args.length < 2) {
				SoundboardUtil.message(sender, "Error: Invalid arguments for /sb set");
				return true;
			}
			Sound sound = null;
			try {
				sound = Sound.valueOf(args[1].toUpperCase());
			} catch (IllegalArgumentException e) {
				SoundboardUtil.message(sender, "Error: Sound not found: " + args[1].toUpperCase());
				return true;
			}
			SoundboardUtil.message(sender, "Sound set to " + args[1].toUpperCase());
			SoundboardPlugin.playerMap.get(p.getName()).sound = sound;
			return true;
		}
		if (args[0].equalsIgnoreCase("cancel")) {
			if (SoundboardPlugin.playerMap.get(p.getName()).task == -1) {
				SoundboardUtil.message(sender, "Error: No repeat to cancel");
				return true;
			}
			Bukkit.getServer().getScheduler().cancelTask(SoundboardPlugin.playerMap.get(p.getName()).task);
			SoundboardPlugin.playerMap.get(p.getName()).task = -1;
		}
		if (args[0].equalsIgnoreCase("repeat")) {
			if (args.length < 2) {
				SoundboardUtil.message(sender, "Error: Invalid arguments for /sb repeat");
				return true;
			}
			int ticks = 20;
			try {
				ticks = Integer.parseInt(args[1]);
			} catch (Exception e) {
				SoundboardUtil.message(sender, "Error: Invalid arguments for /sb repeat");
				return true;
			}
			if (SoundboardPlugin.playerMap.get(p.getName()).task != -1)
				Bukkit.getServer().getScheduler().cancelTask(SoundboardPlugin.playerMap.get(p.getName()).task);
			final SoundboardPlayer sbPlayer = SoundboardPlugin.playerMap.get(p.getName());
			int taskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
				@Override
				public void run() {
					sbPlayer.play();
				}
			}, 0L, (long) ticks);
			SoundboardPlugin.playerMap.get(p.getName()).task = taskID;
			SoundboardUtil.message(sender, "Repeating sound every x ticks: " + ticks);
			return true;
		}
		if (args[0].equalsIgnoreCase("target")) {
			if (args.length < 2) {
				SoundboardUtil.message(sender, "Error: Invalid arguments for /sb target");
				return true;
			}
			if (args[1].equalsIgnoreCase("all")) {
				SoundboardPlugin.playerMap.get(p.getName()).target = SoundboardPlayer.TARGET_ALL;
				SoundboardUtil.message(sender, "Target set to all.");
				return true;
			}
			if (args[1].equalsIgnoreCase("location")) {
				SoundboardPlugin.playerMap.get(p.getName()).doSelectLocation = true;
				SoundboardUtil.message(sender, "Click a location to set target.");
				return true;
			}
			if (args[1].equalsIgnoreCase("self")) {
				SoundboardPlugin.playerMap.get(p.getName()).target = SoundboardPlayer.TARGET_SELF;
				SoundboardUtil.message(sender, "Target set to self.");
				return true;
			}
			if (args[1].equalsIgnoreCase("self_only")) {
				SoundboardPlugin.playerMap.get(p.getName()).target = SoundboardPlayer.TARGET_SELF_ONLY;
				SoundboardUtil.message(sender, "Target set to self only.");
				return true;
			}
			if (args[1].equalsIgnoreCase("player")) {
				if (args.length < 3) {
					SoundboardUtil.message(sender, "Error: Invalid arguments for /sb target player");
					return true;
				}
				Player player = Bukkit.getPlayer(args[2]);
				if (player == null) {
					SoundboardUtil.message(sender, "Player not found");
					return true;
				}
				SoundboardPlugin.playerMap.get(p.getName()).target = SoundboardPlayer.TARGET_PLAYER;
				SoundboardPlugin.playerMap.get(p.getName()).targetPlayer = player.getName();
				SoundboardUtil.message(sender, "Target set to player '" + player.getName() + "'.");
				return true;
			}
			if (args[1].equalsIgnoreCase("player_only")) {
				if (args.length < 3) {
					SoundboardUtil.message(sender, "Error: Invalid arguments for /sb target player_only");
					return true;
				}
				Player player = Bukkit.getPlayer(args[2]);
				if (player == null) {
					SoundboardUtil.message(sender, "Player not found");
					return true;
				}
				SoundboardPlugin.playerMap.get(p.getName()).target = SoundboardPlayer.TARGET_PLAYER_ONLY;
				SoundboardPlugin.playerMap.get(p.getName()).targetPlayer = player.getName();
				SoundboardUtil.message(sender, "Target set to player '" + player.getName() + "' only.");
				return true;
			}
		}
		sendHelp(sender, 1);
		return true;
	}

	public void sendHelp(CommandSender to, int page) {
		SoundboardUtil.message(to, " &e/sb bind - &7Binds a specific item to soundboard");
		SoundboardUtil.message(to, " &e/sb unbind - &7Unbinds your item");
		SoundboardUtil.message(to, " &e/sb set <sound> - &7Sets the desired sound");
		SoundboardUtil.message(to, " &e/sb repeat <period in ticks> - &7Repeats the current sound + target every x ticks");
		SoundboardUtil.message(to, " &e/sb cancel - &7Cancels the current repeat loop");
		SoundboardUtil.message(to, " &e/sb target - &7Sets the target for sounds");
		SoundboardUtil.message(to, " &d (self, self_only, player, player_only, location, all)");
	}
}
