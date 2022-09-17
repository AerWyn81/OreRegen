package fr.aerwyn81.oreregen.events;

import fr.aerwyn81.oreregen.data.RegenBlock;
import fr.aerwyn81.oreregen.handlers.ItemService;
import fr.aerwyn81.oreregen.handlers.LanguageService;
import fr.aerwyn81.oreregen.handlers.LocationService;
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

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();

        if (block == null || !isMainHand(e)) {
            return;
        }

        Player player = e.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE && e.getAction() == Action.LEFT_CLICK_BLOCK) {
            return;
        }

        if (!PlayerUtils.hasPermission(player, "oreregen.admin")) {
            return;
        }

        Location clickedLocation = block.getLocation();
        RegenBlock regenBlock = LocationService.getBlockByLocation(clickedLocation);

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (!hasPluginItemInHand(player)) {
                return;
            }

            if (regenBlock != null) {
                player.sendMessage(LanguageService.getMessage("Messages.BlockAlreadyRegistered"));
                return;
            }

            LocationService.addBlock(clickedLocation.getBlock());
            player.sendMessage(LanguageService.getMessage("Messages.BlockRegistered")
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
        return player.getInventory().getItemInMainHand().isSimilar(ItemService.getItem());
    }
}
