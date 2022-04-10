package fr.aerwyn81.oreregen.commands.list;

import fr.aerwyn81.oreregen.OreRegen;
import fr.aerwyn81.oreregen.commands.Cmd;
import fr.aerwyn81.oreregen.commands.ORAnnotations;
import fr.aerwyn81.oreregen.data.RegenBlock;
import fr.aerwyn81.oreregen.handlers.ConfigHandler;
import fr.aerwyn81.oreregen.handlers.LanguageHandler;
import fr.aerwyn81.oreregen.runnables.OreRegenCheckTask;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

@ORAnnotations(command = "reload", permission = "headblocks.admin")
public class Reload implements Cmd {
    private final OreRegen main;
    private final ConfigHandler configHandler;
    private final LanguageHandler languageHandler;

    public Reload(OreRegen main) {
        this.main = main;
        this.configHandler = main.getConfigHandler();
        this.languageHandler = main.getLanguageHandler();
    }

    @Override
    public boolean perform(CommandSender sender, String[] args) {
        main.reloadConfig();
        main.getConfigHandler().loadConfiguration();

        main.getLanguageHandler().setLanguage(main.getConfigHandler().getLanguage());
        main.getLanguageHandler().pushMessages();

        main.getItemHandler().loadItem();

        main.getLocationHandler().getBlocks().forEach(RegenBlock::resetMinedBlock);
        main.getLocationHandler().loadLocations();

        Bukkit.getScheduler().cancelTasks(main);
        main.setOreRegenCheckTask(new OreRegenCheckTask(main));
        main.getOreRegenCheckTask().runTaskTimer(main, 0, configHandler.getTimerDelay());

        sender.sendMessage(languageHandler.getMessage("Messages.ReloadComplete"));
        return true;
    }

    @Override
    public ArrayList<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
