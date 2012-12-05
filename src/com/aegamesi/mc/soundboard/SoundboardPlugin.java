package com.aegamesi.mc.soundboard;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class SoundboardPlugin extends JavaPlugin implements Listener {
	public static HashMap<String, SoundboardPlayer> playerMap;

	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		getCommand("sb").setExecutor(new SoundboardCommandExecutor(this));
		// saveDefaultConfig();

		playerMap = new HashMap<String, SoundboardPlayer>();
	}

	public void onDisable() {
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent evt) {
		if (!playerMap.containsKey(evt.getPlayer().getName()))
			playerMap.put(evt.getPlayer().getName(), new SoundboardPlayer(evt.getPlayer().getName()));
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent evt) {
		if (playerMap.containsKey(evt.getPlayer().getName())) {
			if(playerMap.get(evt.getPlayer().getName()).task != -1) {
				Bukkit.getServer().getScheduler().cancelTask(playerMap.get(evt.getPlayer().getName()).task);
			}
			playerMap.remove(evt.getPlayer().getName());
		}
	}

	@EventHandler
	public void onPlayerInteractBlock(PlayerInteractEvent evt) {
		if(playerMap.get(evt.getPlayer().getName()).doSelectLocation) {
			playerMap.get(evt.getPlayer().getName()).doSelectLocation = false;
			playerMap.get(evt.getPlayer().getName()).targetLocation = evt.getPlayer().getTargetBlock(null, 200).getLocation();
			playerMap.get(evt.getPlayer().getName()).target = SoundboardPlayer.TARGET_LOCATION;
			SoundboardUtil.message(evt.getPlayer(), "Target set to location.");
		}
		
		int itemid = evt.getPlayer().getItemInHand() == null ? 0 : evt.getPlayer().getItemInHand().getTypeId();
		if (itemid == playerMap.get(evt.getPlayer().getName()).bound) {
			playerMap.get(evt.getPlayer().getName()).play();
		}
	}
}