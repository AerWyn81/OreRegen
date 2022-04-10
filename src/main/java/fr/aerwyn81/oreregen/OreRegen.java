package fr.aerwyn81.oreregen;

import fr.aerwyn81.oreregen.commands.ORCommandExecutor;
import fr.aerwyn81.oreregen.data.RegenBlock;
import fr.aerwyn81.oreregen.events.OnPlayerBreakBlockEvent;
import fr.aerwyn81.oreregen.events.OnPlayerInteractEvent;
import fr.aerwyn81.oreregen.handlers.ConfigHandler;
import fr.aerwyn81.oreregen.handlers.ItemHandler;
import fr.aerwyn81.oreregen.handlers.LanguageHandler;
import fr.aerwyn81.oreregen.handlers.LocationHandler;
import fr.aerwyn81.oreregen.runnables.OreRegenCheckTask;
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

    private OreRegenCheckTask oreRegenCheckTask;

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

        this.oreRegenCheckTask = new OreRegenCheckTask(this);
        oreRegenCheckTask.runTaskTimer(this, 0, (configHandler.getTimerDelay() == 0 ? 1 : configHandler.getTimerDelay()) * 20L);

        getCommand("oreregen").setExecutor(new ORCommandExecutor(this));

        Bukkit.getPluginManager().registerEvents(new OnPlayerInteractEvent(this), this);
        Bukkit.getPluginManager().registerEvents(new OnPlayerBreakBlockEvent(this), this);

        log.sendMessage(FormatUtils.translate("&6OreRegen &asuccessfully loaded!"));
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);

        getLocationHandler().getBlocks().forEach(RegenBlock::resetMinedBlock);

        log.sendMessage(FormatUtils.translate("&6OreRegen &cdisabled!"));
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

    public OreRegenCheckTask getOreRegenCheckTask() {
        return oreRegenCheckTask;
    }

    public void setOreRegenCheckTask(OreRegenCheckTask oreRegenCheckTask) {
        this.oreRegenCheckTask = oreRegenCheckTask;
    }
}
