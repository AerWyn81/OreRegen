package fr.aerwyn81.oreregen.handlers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;

public class ConfigService {
    private static File configFile;
    private static FileConfiguration config;

    public static void initialize(File file) {
        configFile = file;
        load();
    }

    public static void load() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public static String getLanguage() {
        return config.getString("language", "en").toLowerCase();
    }

    public static long getTimerDelay() {
        return config.getLong("timer.delay", 1);
    }

    public static boolean getTimerRespawn() {
        return config.getBoolean("timer.respawn.enabled", true);
    }

    public static int getTimerRangeMin() {
        return config.getInt("timer.respawn.range.min", 1);
    }

    public static int getTimerRangeMax() {
        return config.getInt("timer.respawn.range.max", 10) + 1;
    }

    public static String getReplacingBlock() {
        return config.getString("replacingBlock.type", "BEDROCK");
    }

    public static ArrayList<String> getRewardCommands() {
        return new ArrayList<>(config.getStringList("rewardCommands"));
    }

    public static boolean isBreakChanceEnable() {
        return config.getBoolean("breakChance.enable", false);
    }

    public static int getBreakChance() {
        return config.getInt("breakChance.chance", 50);
    }
}
