package fr.aerwyn81.oreregen.events;

import fr.aerwyn81.oreregen.OreRegen;
import fr.aerwyn81.oreregen.data.RegenBlock;
import fr.aerwyn81.oreregen.handlers.ConfigHandler;
import fr.aerwyn81.oreregen.handlers.LanguageHandler;
import fr.aerwyn81.oreregen.handlers.LocationHandler;
import fr.aerwyn81.oreregen.utils.FormatUtils;
import fr.aerwyn81.oreregen.utils.MillisecondConverter;
import fr.aerwyn81.oreregen.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.concurrent.ThreadLocalRandom;

public class OnPlayerBreakBlockEvent implements Listener {
    private final OreRegen main;
    private final ConfigHandler configHandler;
    private final LanguageHandler languageHandler;
    private final LocationHandler locationHandler;

    public OnPlayerBreakBlockEvent(OreRegen main) {
        this.main = main;
        this.configHandler = main.getConfigHandler();
        this.languageHandler = main.getLanguageHandler();
        this.locationHandler = main.getLocationHandler();
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent e) {
        Player player = e.getPlayer();

        Block block = e.getBlock();
        RegenBlock regenBlock = locationHandler.getBlockByLocation(block.getLocation());
        if (regenBlock == null)
            return;

        if (!PlayerUtils.hasPermission(player, "oreregen.use")) {
            String message = languageHandler.getMessage("Messages.NoPermission");

            if (!message.trim().isEmpty()) {
                player.sendMessage(message);
            }
            return;
        }

        e.setCancelled(true);

        if (regenBlock.isMined()) {
            String message = languageHandler.getMessage("Messages.BlockAlreadyBreaked");
            if (!message.trim().isEmpty()) {
                MillisecondConverter converter = regenBlock.getTimeLeft();
                player.sendMessage(message.replaceAll("%h%", String.valueOf(converter.getHours()))
                        .replaceAll("%m%", String.valueOf(converter.getMinutes()))
                        .replaceAll("%s%", String.valueOf(converter.getSeconds()))
                        .replaceAll("%ms%", String.format("%03d", converter.getMilliseconds())));
            }

            return;
        }

        internalMinedBlock(regenBlock, block);

        String message = languageHandler.getMessage("Messages.BlockBreaked");
        if (!message.trim().isEmpty()) {
            player.sendMessage(message);
        }

        Bukkit.getScheduler().runTaskLater(main, () -> {
            configHandler.getRewardCommands().forEach(command ->
                    main.getServer().dispatchCommand(main.getServer().getConsoleSender(), command
                            .replace("%player%", player.getName())));
        }, 1L);
    }

    private void internalMinedBlock(RegenBlock regenBlock, Block block) {
        regenBlock.setMined(true);

        int time = ThreadLocalRandom.current().nextInt(configHandler.getTimerRangeMin(), configHandler.getTimerRangeMax() + 1);
        regenBlock.setNextResetTime(time * 1000L);

        try {
            block.setType(Material.valueOf(configHandler.getReplacingBlock()));
        } catch (Exception ex) {
            block.setType(Material.BEDROCK);
            OreRegen.log.sendMessage(FormatUtils.translate("&cError while replacing block: " + ex.getMessage()));
        }
    }
}
