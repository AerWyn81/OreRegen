package fr.aerwyn81.oreregen;

import fr.aerwyn81.oreregen.commands.ORCommandExecutor;
import fr.aerwyn81.oreregen.events.OnPlayerInteractEvent;
import fr.aerwyn81.oreregen.handlers.ConfigHandler;
import fr.aerwyn81.oreregen.handlers.ItemHandler;
import fr.aerwyn81.oreregen.handlers.LanguageHandler;
import fr.aerwyn81.oreregen.handlers.LocationHandler;
import fr.aerwyn81.oreregen.utils.ConfigUpdater;
import fr.aerwyn81.oreregen.utils.FormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

public final class OreRegen extends JavaPlugin {
    public static ConsoleCommandSender log;
    private static OreRegen instance;

    private ConfigHandler configHandler;
    private LanguageHandler languageHandler;
    private ItemHandler itemHandler;
    private LocationHandler locationHandler;

    @Override
    public void onEnable() {
        instance = this;
        log = Bukkit.getConsoleSender();

        log.sendMessage(FormatUtils.translate("&6OreRegen &einitializing..."));

        File configFile = new File(getDataFolder(), "config.yml");

        saveDefaultConfig();
        try {
            ConfigUpdater.update(this, "config.yml", configFile, Collections.emptyList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        reloadConfig();

        this.configHandler = new ConfigHandler(configFile);
        this.configHandler.loadConfiguration();

        this.languageHandler = new LanguageHandler(this, configHandler.getLanguage());
        this.languageHandler.pushMessages();

        this.itemHandler = new ItemHandler(this);
        this.itemHandler.loadItem();

        this.locationHandler = new LocationHandler(this);
        this.locationHandler.loadLocations();

        getCommand("oreregen").setExecutor(new ORCommandExecutor(this));

        Bukkit.getPluginManager().registerEvents(new OnPlayerInteractEvent(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public LanguageHandler getLanguageHandler() {
        return languageHandler;
    }

    public ItemHandler getItemHandler() {
        return itemHandler;
    }

    public LocationHandler getLocationHandler() {
        return locationHandler;
    }
}
