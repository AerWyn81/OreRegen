package fr.aerwyn81.oreregen.commands.list;

import fr.aerwyn81.oreregen.OreRegen;
import fr.aerwyn81.oreregen.commands.Cmd;
import fr.aerwyn81.oreregen.commands.ORAnnotations;
import fr.aerwyn81.oreregen.handlers.ItemHandler;
import fr.aerwyn81.oreregen.handlers.LanguageHandler;
import fr.aerwyn81.oreregen.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.stream.Collectors;

@ORAnnotations(command = "give", permission = "oreregen.admin", isPlayerCommand = true)
public class Give implements Cmd {
    private final LanguageHandler languageHandler;
    private final ItemHandler itemHandler;

    public Give(OreRegen main) {
        this.languageHandler = main.getLanguageHandler();
        this.itemHandler = main.getItemHandler();
    }

    @Override
    public boolean perform(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (PlayerUtils.getEmptySlots(player) < 1) {
            player.sendMessage(languageHandler.getMessage("Messages.InventoryFull"));
            return true;
        }

        player.getInventory().addItem(itemHandler.getItem());
        player.sendMessage(languageHandler.getMessage("Messages.ItemGived"));

        return true;
    }

    @Override
    public ArrayList<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        return new ArrayList<>();
    }
}
