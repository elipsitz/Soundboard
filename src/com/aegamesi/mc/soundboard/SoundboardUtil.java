package com.aegamesi.mc.soundboard;

import org.bukkit.command.CommandSender;

public class SoundboardUtil {
	public static String processColours(String str) {
		return str.replaceAll("(&([a-f0-9klmnor]))", "\u00A7$2");
	}

	public static String stripColours(String str) {
		return str.replaceAll("(&([a-f0-9klmnor]))", "");
	}

	public static void message(CommandSender sender, String message) {
		sender.sendMessage(processColours(message));
	}
}
