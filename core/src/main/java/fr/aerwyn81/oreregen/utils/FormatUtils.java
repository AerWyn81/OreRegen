package fr.aerwyn81.oreregen.utils;

import net.md_5.bungee.api.ChatColor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FormatUtils {
	private static final Pattern hexPattern = Pattern.compile("\\{#[0-9a-fA-F]{6}}");

	/**
	 * Format a message with chat format and color (& or hexa)
	 * <p>
	 * Support MC Version 12.2 -> 1.16+
	 *
	 * @param message with {#RRGGBB}
	 * @return Formatted string to be displayed by SpigotAPI
	 */
	public static String translate(String message) {
		String replaced = message;
		Matcher m = hexPattern.matcher(replaced);
		while (m.find()) {
			String hexcode = m.group();
			String fixed = hexcode.substring(1, 8);

			try {
				Method ofMethod = ChatColor.class.getMethod("of", String.class);
				replaced = replaced.replace(hexcode, ofMethod.invoke(null, fixed).toString());
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignore) {
			}
		}
		return ChatColor.translateAlternateColorCodes('&', replaced);
	}

	public static ArrayList<String> translate(ArrayList<String> messages) {
		return messages.stream().map(FormatUtils::translate).collect(Collectors.toCollection(ArrayList::new));
	}
}
