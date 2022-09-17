package fr.aerwyn81.oreregen.handlers;

import fr.aerwyn81.oreregen.OreRegen;
import fr.aerwyn81.oreregen.utils.ConfigUpdater;
import fr.aerwyn81.oreregen.utils.FormatUtils;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"unchecked", "ResultOfMethodCallIgnored", "ConstantConditions"})
public class LanguageService {
	private static String language;
	private static HashMap<String, Object> messages;

	public static void initialize(String lang) {
		new File(OreRegen.getInstance() + "/language").mkdirs();

		loadLanguage("fr");
		language = checkLanguage(lang);
		messages = new HashMap<>();
	}

	public static void setLanguage(String lang) {
		language = lang;
	}

	public static String getPrefix() {
		return FormatUtils.translate(messages.get("Prefix").toString());
	}

	public static boolean hasMessage(String message) {
		return messages.containsKey(message);
	}

	public static String getMessage(String message) {
		return FormatUtils.translate(messages.get(message).toString().replaceAll("%prefix%", getPrefix()));
	}

	public static List<String> getMessages(String message) {
		return ((List<String>) messages.get(message)).stream().map(FormatUtils::translate).collect(Collectors.toList());
	}

	public static String checkLanguage(String lang) {
		File f = new File(OreRegen.getInstance().getDataFolder() + "/language/messages_" + lang + ".yml");
		if (f.exists())
			return lang;
		return "fr";
	}

	public static void pushMessages() {
		File f = new File(OreRegen.getInstance().getDataFolder() + "/language/messages_" + language + ".yml");
		YamlConfiguration c = YamlConfiguration.loadConfiguration(f);

		c.getKeys(true).stream().filter(key -> !(c.get(key) instanceof MemorySection)).forEach(key -> {
			if (c.get(key) instanceof List) {
				messages.put(key, c.getStringList(key));
			} else {
				messages.put(key, c.getString(key));
			}
		});
	}

	public static void loadLanguage(String lang) {
		File file = new File(OreRegen.getInstance().getDataFolder() + "/language/messages_" + lang + ".yml");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

		cfg.options().header("\nThis is the messsages file.\nYou can change any messages that are in this file\n\nIf you want to reset a message back to the default,\ndelete the entire line the message is on and restart the server.\n\t");

		Map<String, Object> msgDefaults = new LinkedHashMap<>();

		InputStreamReader input = new InputStreamReader(OreRegen.getInstance().getResource("language/messages_" + lang + ".yml"), StandardCharsets.UTF_8);
		FileConfiguration data = YamlConfiguration.loadConfiguration(input);

		for(String key : data.getKeys(true)) {
			if(!(data.get(key) instanceof MemorySection)) {
				if (data.get(key) instanceof List) {
					msgDefaults.put(key, data.getStringList(key));
				} else {
					msgDefaults.put(key, data.getString(key));
				}
			}
		}

		for (String key : msgDefaults.keySet()) {
			if (!cfg.isSet(key)) {
				cfg.set(key, msgDefaults.get(key));
			}
		}

		for(String key : cfg.getKeys(true)) {
			if(!(cfg.get(key) instanceof MemorySection)) {
				if (!data.isSet(key)) {
					cfg.set(key, null);
				}
			}
		}

		try {
			cfg.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			ConfigUpdater.update(OreRegen.getInstance(), "language/messages_" + lang + ".yml", new File(OreRegen.getInstance().getDataFolder() + "/language/messages_" + lang + ".yml"), Collections.emptyList());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}