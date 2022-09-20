package fr.aerwyn81.oreregen;

import fr.aerwyn81.interfaces.IBlockCompatibility;
import fr.aerwyn81.oreregen.commands.ORCommandExecutor;
import fr.aerwyn81.oreregen.data.RegenBlock;
import fr.aerwyn81.oreregen.events.OnPlayerBreakBlockEvent;
import fr.aerwyn81.oreregen.events.OnPlayerHelEvent;
import fr.aerwyn81.oreregen.events.OnPlayerInteractEvent;
import fr.aerwyn81.oreregen.events.OnWorldLoadEvent;
import fr.aerwyn81.oreregen.handlers.BlockRegenService;
import fr.aerwyn81.oreregen.handlers.ConfigService;
import fr.aerwyn81.oreregen.handlers.LanguageService;
import fr.aerwyn81.oreregen.runnables.OreRegenCheckTask;
import fr.aerwyn81.oreregen.utils.ConfigUpdater;
import fr.aerwyn81.oreregen.utils.FormatUtils;
import fr.aerwyn81.oreregen.utils.ItemBuilder;
import fr.aerwyn81.oreregen.utils.Version;
import fr.aerwyn81.v1_16_r3.V1_16_R3;
import fr.aerwyn81.v1_17_r1.V1_17_R1;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

public final class OreRegen extends JavaPlugin {
    public static ConsoleCommandSender log;
    private static OreRegen instance;

    private OreRegenCheckTask oreRegenCheckTask;

    private IBlockCompatibility blockCompatibility;

    private static ItemBuilder item;

    public static OreRegen getInstance() {
        return instance;
    }

    public static ItemStack getPluginItem() {
        return item.getItemStack();
    }

    @Override
    public void onEnable() {
        instance = this;
        log = Bukkit.getConsoleSender();

        log.sendMessage(FormatUtils.translate("&6OreRegen &einitializing..."));

        setupVersionCompatibility();

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

        BlockRegenService.initialize();
        BlockRegenService.loadBlocks();

        createItem();

        startGlobalTask();

        getCommand("oreregen").setExecutor(new ORCommandExecutor(this));

        Bukkit.getPluginManager().registerEvents(new OnPlayerInteractEvent(), this);
        Bukkit.getPluginManager().registerEvents(new OnPlayerBreakBlockEvent(), this);
        Bukkit.getPluginManager().registerEvents(new OnPlayerHelEvent(), this);
        Bukkit.getPluginManager().registerEvents(new OnWorldLoadEvent(), this);

        log.sendMessage(FormatUtils.translate("&6OreRegen &asuccessfully loaded!"));
    }

    private void setupVersionCompatibility() {
        switch (Version.getCurrent()) {
            case v1_17:
            case v1_18:
            case v1_19:
                blockCompatibility = new V1_17_R1();
                log.sendMessage(FormatUtils.translate("Detected version " + Version.getCurrentFormatted() + "! Setting up block compatibility V1_17_R1 and above"));
                break;
            default:
                blockCompatibility = new V1_16_R3();
                log.sendMessage(FormatUtils.translate("Detected version " + Version.getCurrentFormatted() + "! Setting up block compatibility V1_16_R3 and beyond"));
                break;
        }
    }

    private void createItem() {
        item = new ItemBuilder(Material.STICK)
                .name(FormatUtils.translate("{#eac086}&lO{#ffe39f}&lre{#eac086}&lR{#ffe39f}&legen"))
                .lore("")
                .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
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

    public IBlockCompatibility getBlockCompatibility() {
        return blockCompatibility;
    }

    private void startGlobalTask() {
        this.oreRegenCheckTask = new OreRegenCheckTask();
        oreRegenCheckTask.runTaskTimer(this, 0, (ConfigService.getTimerDelay() == 0 ? 1 : ConfigService.getTimerDelay()) * 20L);
    }
}
