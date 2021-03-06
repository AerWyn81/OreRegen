package fr.aerwyn81.oreregen.commands;

import fr.aerwyn81.oreregen.OreRegen;
import fr.aerwyn81.oreregen.commands.list.Give;
import fr.aerwyn81.oreregen.commands.list.Help;
import fr.aerwyn81.oreregen.commands.list.Reload;
import fr.aerwyn81.oreregen.commands.list.Version;
import fr.aerwyn81.oreregen.handlers.LanguageHandler;
import fr.aerwyn81.oreregen.utils.PlayerUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class ORCommandExecutor implements CommandExecutor, TabCompleter {
    private final HashMap<String, ORCommand> registeredCommands;
    private final LanguageHandler languageHandler;
    private final Help helpCommand;

    public ORCommandExecutor(OreRegen main) {
        this.languageHandler = main.getLanguageHandler();
        this.registeredCommands = new HashMap<>();

        this.helpCommand = new Help(main);

        this.register(helpCommand);
        this.register(new Give(main));
        this.register(new Version(main));
        this.register(new Reload(main));
    }

    private void register(Object c) {
        ORCommand command = new ORCommand(c);

        registeredCommands.put(command.getCommand(), command);
        helpCommand.addCommand(command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command c, String s, String[] args) {
        if (args.length <= 0) {
            sender.sendMessage(languageHandler.getMessage("Messages.ErrorCommand"));
            return false;
        }

        ORCommand command = registeredCommands.get(args[0].toLowerCase());

        if (command == null) {
            sender.sendMessage(languageHandler.getMessage("Messages.ErrorCommand"));
            return false;
        }

        if (!PlayerUtils.hasPermission(sender, command.getPermission())) {
            sender.sendMessage(languageHandler.getMessage("Messages.NoPermission"));
            return false;
        }

        String[] argsWithoutCmd = Arrays.copyOfRange(args, 1, args.length);

        if (argsWithoutCmd.length < command.getArgs().length) {
            sender.sendMessage(languageHandler.getMessage("Messages.ErrorCommand"));
            return false;
        }

        return registeredCommands.get(args[0].toLowerCase()).getCmdClass().perform(sender, args);
    }

    @Override
    public ArrayList<String> onTabComplete(CommandSender sender, Command c, String s, String[] args) {
        if(args.length == 1) {
            return registeredCommands.keySet().stream()
                    .filter(arg -> arg.startsWith(args[0].toLowerCase()))
                    .filter(arg -> PlayerUtils.hasPermission(sender, registeredCommands.get(arg).getPermission())).distinct()
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        if (!registeredCommands.containsKey(args[0].toLowerCase())) {
            return new ArrayList<>();
        }

        ORCommand command = registeredCommands.get(args[0].toLowerCase());

        if (!PlayerUtils.hasPermission(sender, command.getPermission())) {
            return new ArrayList<>();
        }

        return command.getCmdClass().tabComplete(sender, args);
    }
}