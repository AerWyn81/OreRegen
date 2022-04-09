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

    public String getItemType() { return config.getString("item.type", "stick"); }

    public String getItemName() { return config.getString("item.name", "{#eac086}&lO{#ffe39f}&lre{#eac086}&lR{#ffe39f}&legen"); }

    public ArrayList<String> getItemLore() { return new ArrayList<>(config.getStringList("item.lore")); }
}