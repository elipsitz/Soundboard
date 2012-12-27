package com.aegamesi.mc.soundboard;

import org.bukkit.Sound;
import org.bukkit.inventory.meta.FireworkMeta;

public class SoundboardEffect {
	public Type type;
	public Sound sound;
	public FireworkMeta fireworkMeta;
	
	public SoundboardEffect(Type type) {
		this.type = type;
	}
	
	public enum Type {
		SOUND, FIREWORK;
	}
}
