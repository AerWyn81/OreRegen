package fr.aerwyn81.oreregen;

import fr.aerwyn81.oreregen.commands.ORCommandExecutor;
import fr.aerwyn81.oreregen.data.RegenBlock;
import fr.aerwyn81.oreregen.events.OnPlayerBreakBlockEvent;
import fr.aerwyn81.oreregen.events.OnPlayerHelEvent;
import fr.aerwyn81.oreregen.events.OnPlayerInteractEvent;
import fr.aerwyn81.oreregen.handlers.BlockRegenService;
import fr.aerwyn81.oreregen.handlers.ConfigService;
import fr.aerwyn81.oreregen.handlers.ItemService;
import fr.aerwyn81.oreregen.handlers.LanguageService;
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

    private OreRegenCheckTask oreRegenCheckTask;

    public static OreRegen getInstance() {
        return instance;
    }

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

        ConfigService.initialize(configFile);

        LanguageService.initialize(ConfigService.getLanguage());
        LanguageService.pushMessages();

        ItemService.loadItem();

        BlockRegenService.initialize();
        BlockRegenService.loadBlocks();

        this.oreRegenCheckTask = new OreRegenCheckTask();
        oreRegenCheckTask.runTaskTimer(this, 0, (ConfigService.getTimerDelay() == 0 ? 1 : ConfigService.getTimerDelay()) * 20L);

        getCommand("oreregen").setExecutor(new ORCommandExecutor(this));

        Bukkit.getPluginManager().registerEvents(new OnPlayerInteractEvent(), this);
        Bukkit.getPluginManager().registerEvents(new OnPlayerBreakBlockEvent(), this);
        Bukkit.getPluginManager().registerEvents(new OnPlayerHelEvent(), this);

        log.sendMessage(FormatUtils.translate("&6OreRegen &asuccessfully loaded!"));
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);

        BlockRegenService.getBlocks().forEach(RegenBlock::resetMinedBlock);

        log.sendMessage(FormatUtils.translate("&6OreRegen &cdisabled!"));
    }

    public OreRegenCheckTask getOreRegenCheckTask() {
        return oreRegenCheckTask;
    }

    public void setOreRegenCheckTask(OreRegenCheckTask oreRegenCheckTask) {
        this.oreRegenCheckTask = oreRegenCheckTask;
    }
}
