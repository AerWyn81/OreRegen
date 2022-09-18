package fr.aerwyn81.oreregen.events;

import fr.aerwyn81.oreregen.handlers.BlockRegenService;
import fr.aerwyn81.oreregen.handlers.ItemService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

public class OnPlayerHelEvent implements Listener {

    @EventHandler
    public void onPlayerHelEvent(PlayerItemHeldEvent e) {
        Player player = e.getPlayer();

        ItemStack itemSelected = player.getInventory().getContents()[e.getNewSlot()];

        if (itemSelected == null || !itemSelected.isSimilar(ItemService.getItem())) {
            BlockRegenService.removeBlockParticlesCache(player);
            return;
        }

        BlockRegenService.showParticlesOnBlocks(player);
    }
}
