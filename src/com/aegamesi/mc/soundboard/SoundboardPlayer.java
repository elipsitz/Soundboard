package com.aegamesi.mc.soundboard;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;

public class SoundboardPlayer {
	public int bound = -1;
	public ArrayList<SoundboardEffect> effects = new ArrayList<SoundboardEffect>();
	public int target = TARGET_SELF_ONLY;
	public String targetPlayer = null;
	public Location targetLocation = null;
	public boolean doSelectLocation = false;
	public int task = -1;

	public String name;

	public static final int TARGET_SELF = 1;
	public static final int TARGET_SELF_ONLY = 2;
	public static final int TARGET_PLAYER = 3;
	public static final int TARGET_PLAYER_ONLY = 4;
	public static final int TARGET_LOCATION = 5;
	public static final int TARGET_ALL = 6;

	public SoundboardPlayer(String name) {
		this.name = name;
	}

	public void play() {
		Player p = Bukkit.getPlayerExact(name);
		Player t = targetPlayer == null ? null : Bukkit.getPlayerExact(targetPlayer);

		if (p == null || (t == null && targetPlayer != null && (target == TARGET_PLAYER || target == TARGET_PLAYER_ONLY)) || effects.size() == 0)
			return;

		Random r = new Random();
		SoundboardEffect eff = effects.get(r.nextInt(effects.size()));
		switch (eff.type) {
		case SOUND:
			Sound sound = eff.sound;
			switch (target) {
			case TARGET_SELF:
				p.getWorld().playSound(p.getLocation(), sound, 1.0f, 1.0f);
				break;
			case TARGET_SELF_ONLY:
				p.playSound(p.getLocation(), sound, 1.0f, 1.0f);
				break;
			case TARGET_PLAYER:
				t.getWorld().playSound(t.getLocation(), sound, 1.0f, 1.0f);
				break;
			case TARGET_PLAYER_ONLY:
				t.playSound(t.getLocation(), sound, 1.0f, 1.0f);
				break;
			case TARGET_LOCATION:
				p.getWorld().playSound(targetLocation, sound, 1.0f, 1.0f);
				break;
			case TARGET_ALL:
				Player[] players = Bukkit.getOnlinePlayers();
				for (Player player : players)
					player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
				break;
			}
			break;
		case FIREWORK:
			Firework firework;
			switch (target) {
			case TARGET_SELF:
			case TARGET_SELF_ONLY:
				firework = (Firework) p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
				firework.setFireworkMeta(eff.fireworkMeta);
				break;
			case TARGET_PLAYER:
			case TARGET_PLAYER_ONLY:
				firework = (Firework) p.getWorld().spawnEntity(t.getLocation(), EntityType.FIREWORK);
				firework.setFireworkMeta(eff.fireworkMeta);
				break;
			case TARGET_LOCATION:
				firework = (Firework) p.getWorld().spawnEntity(targetLocation, EntityType.FIREWORK);
				firework.setFireworkMeta(eff.fireworkMeta);
				break;
			case TARGET_ALL:
				Player[] players = Bukkit.getOnlinePlayers();
				for (Player player : players) {
					firework = (Firework) p.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
					firework.setFireworkMeta(eff.fireworkMeta);
				}
				break;
			}
			break;
		}
	}
}
