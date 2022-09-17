package fr.aerwyn81.oreregen.events;

import fr.aerwyn81.oreregen.OreRegen;
import fr.aerwyn81.oreregen.data.RegenBlock;
import fr.aerwyn81.oreregen.handlers.LanguageHandler;
import fr.aerwyn81.oreregen.handlers.LocationHandler;
import fr.aerwyn81.oreregen.utils.FormatUtils;
import fr.aerwyn81.oreregen.utils.MillisecondConverter;
import fr.aerwyn81.oreregen.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.concurrent.ThreadLocalRandom;

public class OnPlayerBreakBlockEvent implements Listener {
    private final OreRegen main;
    private final LanguageHandler languageHandler;
    private final LocationHandler locationHandler;

    public OnPlayerBreakBlockEvent(OreRegen main) {
        this.main = main;
        this.languageHandler = main.getLanguageHandler();
        this.locationHandler = main.getLocationHandler();
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent e) {
        Player player = e.getPlayer();

        Block block = e.getBlock();
        Location blockLocation = block.getLocation();

        RegenBlock regenBlock = locationHandler.getBlockByLocation(blockLocation);
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

        if (player.getGameMode() == GameMode.CREATIVE && player.getInventory().getItemInMainHand().getType() == Material.AIR) {
            if (!player.isSneaking()) {
                e.setCancelled(true);
                player.sendMessage(languageHandler.getMessage("Messages.CreativeSneakRemoveBlock"));
                return;
            }

            locationHandler.removeBlock(regenBlock);
            player.sendMessage(languageHandler.getMessage("Messages.BlockDeleted")
                    .replaceAll("%x%", String.valueOf(blockLocation.getBlockX()))
                    .replaceAll("%y%", String.valueOf(blockLocation.getBlockY()))
                    .replaceAll("%z%", String.valueOf(blockLocation.getBlockZ()))
                    .replaceAll("%world%", blockLocation.getWorld().getName()));

            return;
        }

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

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().toString().toLowerCase().contains("pickaxe") && player.getGameMode() == GameMode.SURVIVAL) {
            Damageable itemDurability = (Damageable) item.getItemMeta();
            if (itemDurability != null) {
                if (item.containsEnchantment(Enchantment.DURABILITY)) {
                    int duraLevel = item.getEnchantmentLevel(Enchantment.DURABILITY);
                    boolean shouldDamage = ((Math.random()) < (1.0 / (duraLevel + 1)));

                    if (shouldDamage) {
                        itemDurability.setDamage(itemDurability.getDamage() + 1);
                        item.setItemMeta((ItemMeta) itemDurability);
                    }
                } else {
                    itemDurability.setDamage(itemDurability.getDamage() + 1);
                    item.setItemMeta((ItemMeta) itemDurability);
                }
            }

            player.updateInventory();
        }

        String message = languageHandler.getMessage("Messages.BlockBreaked");
        if (!message.trim().isEmpty()) {
            player.sendMessage(message);
        }

        Bukkit.getScheduler().runTaskLater(main, () -> {
            main.getConfigHandler().getRewardCommands().forEach(command ->
                    main.getServer().dispatchCommand(main.getServer().getConsoleSender(), command
                            .replace("%player%", player.getName())));
        }, 1L);
    }

    private void internalMinedBlock(RegenBlock regenBlock, Block block) {
        regenBlock.setMined(true);

        int time = ThreadLocalRandom.current().nextInt(main.getConfigHandler().getTimerRangeMin(), main.getConfigHandler().getTimerRangeMax() + 1);
        regenBlock.setNextResetTime(time * 1000L);

        try {
            block.setType(Material.valueOf(main.getConfigHandler().getReplacingBlock()));
        } catch (Exception ex) {
            block.setType(Material.BEDROCK);
            OreRegen.log.sendMessage(FormatUtils.translate("&cError while replacing block: " + ex.getMessage()));
        }
    }
}
