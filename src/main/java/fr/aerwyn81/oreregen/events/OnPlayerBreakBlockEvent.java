package fr.aerwyn81.oreregen.events;

import fr.aerwyn81.oreregen.OreRegen;
import fr.aerwyn81.oreregen.data.RegenBlock;
import fr.aerwyn81.oreregen.handlers.LanguageHandler;
import fr.aerwyn81.oreregen.handlers.LocationHandler;
import fr.aerwyn81.oreregen.utils.PlayerUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class OnPlayerBreakBlockEvent implements Listener {
    private final LanguageHandler languageHandler;
    private final LocationHandler locationHandler;

    public OnPlayerBreakBlockEvent(OreRegen main) {
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


    }
}
