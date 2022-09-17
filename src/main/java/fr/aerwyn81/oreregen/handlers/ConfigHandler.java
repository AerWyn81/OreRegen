package fr.aerwyn81.oreregen.handlers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;

public class ConfigHandler {

    private final File configFile;
    private FileConfiguration config;

    public ConfigHandler(File configFile) {
        this.configFile = configFile;
    }

    public void loadConfiguration() {
        this.config = YamlConfiguration.loadConfiguration(configFile);
    }

    public String getLanguage() {
        return config.getString("language", "en").toLowerCase();
    }

    public String getItemType() {
        return config.getString("item.type", "stick");
    }

    public String getItemName() {
        return config.getString("item.name", "{#eac086}&lO{#ffe39f}&lre{#eac086}&lR{#ffe39f}&legen");
    }

    public ArrayList<String> getItemLore() {
        return new ArrayList<>(config.getStringList("item.lore"));
    }

    public long getTimerDelay() {
        return config.getLong("timer.delay", 1);
    }

    public boolean getTimerRespawn() {
        return config.getBoolean("timer.respawn.enabled", true);
    }

    public int getTimerRangeMin() {
        return config.getInt("timer.respawn.range.min", 1);
    }

    public int getTimerRangeMax() {
        return config.getInt("timer.respawn.range.max", 10) + 1;
    }

    public String getReplacingBlock() {
        return config.getString("replacingBlock.type", "BEDROCK");
    }

    public ArrayList<String> getRewardCommands() {
        return new ArrayList<>(config.getStringList("rewardCommands"));
    }
}
