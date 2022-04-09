package fr.aerwyn81.oreregen.commands.list;

import fr.aerwyn81.oreregen.OreRegen;
import fr.aerwyn81.oreregen.commands.Cmd;
import fr.aerwyn81.oreregen.commands.ORAnnotations;
import fr.aerwyn81.oreregen.handlers.LanguageHandler;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

@ORAnnotations(command = "version")
public class Version implements Cmd {
    private final OreRegen main;
    private final LanguageHandler languageHandler;

    public Version(OreRegen main) {
        this.main = main;
        this.languageHandler = main.getLanguageHandler();
    }

    @Override
    public boolean perform(CommandSender sender, String[] args) {
        sender.sendMessage(languageHandler.getMessage("Messages.Version")
                .replaceAll("%version%", main.getDescription().getVersion()));

        return true;
    }

    @Override
    public ArrayList<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
