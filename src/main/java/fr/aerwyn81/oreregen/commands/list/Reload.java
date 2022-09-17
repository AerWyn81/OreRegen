package fr.aerwyn81.oreregen.commands.list;

import fr.aerwyn81.oreregen.OreRegen;
import fr.aerwyn81.oreregen.commands.Cmd;
import fr.aerwyn81.oreregen.commands.ORAnnotations;
import fr.aerwyn81.oreregen.data.RegenBlock;
import fr.aerwyn81.oreregen.handlers.ConfigService;
import fr.aerwyn81.oreregen.handlers.ItemService;
import fr.aerwyn81.oreregen.handlers.LanguageService;
import fr.aerwyn81.oreregen.handlers.LocationService;
import fr.aerwyn81.oreregen.runnables.OreRegenCheckTask;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

@ORAnnotations(command = "reload", permission = "headblocks.admin")
public class Reload implements Cmd {

    @Override
    public boolean perform(CommandSender sender, String[] args) {
        OreRegen plugin = OreRegen.getInstance();

        plugin.reloadConfig();
        ConfigService.load();

        LanguageService.setLanguage(ConfigService.getLanguage());
        LanguageService.pushMessages();

        ItemService.loadItem();

        LocationService.getBlocks().forEach(RegenBlock::resetMinedBlock);
        LocationService.loadLocations();

        Bukkit.getScheduler().cancelTasks(plugin);
        plugin.setOreRegenCheckTask(new OreRegenCheckTask());
        plugin.getOreRegenCheckTask().runTaskTimer(plugin, 0, ConfigService.getTimerDelay());

        sender.sendMessage(LanguageService.getMessage("Messages.ReloadComplete"));
        return true;
    }

    @Override
    public ArrayList<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
