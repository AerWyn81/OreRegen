package fr.aerwyn81.oreregen.events;

import fr.aerwyn81.oreregen.OreRegen;
import fr.aerwyn81.oreregen.data.RegenBlock;
import fr.aerwyn81.oreregen.handlers.ItemHandler;
import fr.aerwyn81.oreregen.handlers.LanguageHandler;
import fr.aerwyn81.oreregen.handlers.LocationHandler;
import fr.aerwyn81.oreregen.utils.PlayerUtils;
import fr.aerwyn81.oreregen.utils.Version;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class OnPlayerInteractEvent implements Listener {
    private final LanguageHandler languageHandler;
    private final ItemHandler itemHandler;
    private final LocationHandler locationHandler;

    public OnPlayerInteractEvent(OreRegen main) {
        this.languageHandler = main.getLanguageHandler();
        this.itemHandler = main.getItemHandler();
        this.locationHandler = main.getLocationHandler();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();

        if (block == null || !isMainHand(e)) {
            return;
        }

        Player player = e.getPlayer();
        Location clickedLocation = block.getLocation();

        if (player.getGameMode() != GameMode.CREATIVE || !PlayerUtils.hasPermission(player, "oreregen.admin")) {
            return;
        }

        RegenBlock regenBlock = locationHandler.getBlockByLocation(clickedLocation);
        if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (regenBlock == null) {
                return;
            }

            if (!player.isSneaking()) {
                e.setCancelled(true);
                player.sendMessage(languageHandler.getMessage("Messages.CreativeSneakRemoveBlock"));
                return;
            }

            locationHandler.removeBlock(regenBlock);
            player.sendMessage(languageHandler.getMessage("Messages.BlockDeleted")
                    .replaceAll("%x%", String.valueOf(clickedLocation.getBlockX()))
                    .replaceAll("%y%", String.valueOf(clickedLocation.getBlockY()))
                    .replaceAll("%z%", String.valueOf(clickedLocation.getBlockZ()))
                    .replaceAll("%world%", clickedLocation.getWorld().getName()));

            return;
        }

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (!hasPluginItemInHand(player)) {
                return;
            }

            if (regenBlock != null) {
                player.sendMessage(languageHandler.getMessage("Messages.BlockAlreadyRegistered"));
                return;
            }

            locationHandler.addBlock(clickedLocation.getBlock());
            player.sendMessage(languageHandler.getMessage("Messages.BlockRegistered")
                    .replaceAll("%x%", String.valueOf(clickedLocation.getBlockX()))
                    .replaceAll("%y%", String.valueOf(clickedLocation.getBlockY()))
                    .replaceAll("%z%", String.valueOf(clickedLocation.getBlockZ()))
                    .replaceAll("%world%", clickedLocation.getWorld().getName()));
        }
    }

    private boolean isMainHand(PlayerInteractEvent e) {
        if (Version.getCurrent() == Version.v1_8) {
            return true;
        }

        return e.getHand() == EquipmentSlot.HAND;
    }

    private boolean hasPluginItemInHand(Player player) {
        return player.getInventory().getItemInMainHand().isSimilar(itemHandler.getItem());
    }
}
