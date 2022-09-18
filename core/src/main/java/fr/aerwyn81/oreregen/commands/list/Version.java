package fr.aerwyn81.oreregen.commands.list;

import fr.aerwyn81.oreregen.OreRegen;
import fr.aerwyn81.oreregen.commands.Cmd;
import fr.aerwyn81.oreregen.commands.ORAnnotations;
import fr.aerwyn81.oreregen.handlers.LanguageService;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

@ORAnnotations(command = "version")
public class Version implements Cmd {

    @Override
    public boolean perform(CommandSender sender, String[] args) {
        sender.sendMessage(LanguageService.getMessage("Messages.Version")
                .replaceAll("%version%", OreRegen.getInstance().getDescription().getVersion()));

        return true;
    }

    @Override
    public ArrayList<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
