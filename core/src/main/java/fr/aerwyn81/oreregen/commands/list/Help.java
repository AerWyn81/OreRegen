package fr.aerwyn81.oreregen.commands.list;

import fr.aerwyn81.oreregen.commands.Cmd;
import fr.aerwyn81.oreregen.commands.ORAnnotations;
import fr.aerwyn81.oreregen.commands.ORCommand;
import fr.aerwyn81.oreregen.handlers.LanguageService;
import fr.aerwyn81.oreregen.utils.ChatPageUtils;
import fr.aerwyn81.oreregen.utils.FormatUtils;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

@ORAnnotations(command = "help")
public class Help implements Cmd {
    private final ArrayList<ORCommand> registeredCommands;

    public Help() {
        this.registeredCommands = new ArrayList<>();
    }

    @Override
    public boolean perform(CommandSender sender, String[] args) {
        ChatPageUtils cpu = new ChatPageUtils(sender)
                .entriesCount(registeredCommands.size())
                .currentPage(args);

        String message = LanguageService.getMessage("Chat.LineTitle");
        if (sender instanceof Player) {
            TextComponent titleComponent = new TextComponent(message);
            cpu.addTitleLine(titleComponent);
        } else {
            sender.sendMessage(message);
        }

        for (int i = cpu.getFirstPos(); i < cpu.getFirstPos() + cpu.getPageHeight() && i < cpu.getSize() ; i++) {
            String command = StringUtils.capitalize(registeredCommands.get(i).getCommand());

            if (!LanguageService.hasMessage("Help." + command)) {
                sender.sendMessage(FormatUtils.translate("&6/or " + registeredCommands.get(i).getCommand() + " &8: &c&oNo help message found. Please report to developer!"));
            } else {
                message = LanguageService.getMessage("Help." + command);
                if (sender instanceof Player) {
                    cpu.addLine(new TextComponent(message));
                } else {
                    sender.sendMessage(message);
                }
            }
        }

        cpu.addPageLine("help");
        cpu.build();
        return true;
    }

    @Override
    public ArrayList<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    public void addCommand(ORCommand command) {
        registeredCommands.add(command);
    }
}
