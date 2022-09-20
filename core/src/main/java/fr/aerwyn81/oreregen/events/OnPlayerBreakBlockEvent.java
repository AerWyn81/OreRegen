package fr.aerwyn81.oreregen.events;

import fr.aerwyn81.oreregen.OreRegen;
import fr.aerwyn81.oreregen.data.RegenBlock;
import fr.aerwyn81.oreregen.handlers.BlockRegenService;
import fr.aerwyn81.oreregen.handlers.ConfigService;
import fr.aerwyn81.oreregen.handlers.LanguageService;
import fr.aerwyn81.oreregen.utils.FormatUtils;
import fr.aerwyn81.oreregen.utils.MillisecondConverter;
import fr.aerwyn81.oreregen.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class OnPlayerBreakBlockEvent implements Listener {

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent e) {
        Player player = e.getPlayer();

        Block block = e.getBlock();
        Location blockLocation = block.getLocation();

        RegenBlock regenBlock = BlockRegenService.getBlockByLocation(blockLocation);
        if (regenBlock == null)
            return;

        if (!PlayerUtils.hasPermission(player, "oreregen.use")) {
            String message = LanguageService.getMessage("Messages.NoPermission");

            if (!message.trim().isEmpty()) {
                player.sendMessage(message);
            }
            return;
        }

        if (player.getGameMode() == GameMode.CREATIVE) {
            e.setCancelled(true);

            if (!player.isSneaking()) {
                player.sendMessage(LanguageService.getMessage("Messages.CreativeSneakRemoveBlock"));
                return;
            }

            BlockRegenService.removeBlock(regenBlock);
            player.sendMessage(LanguageService.getMessage("Messages.BlockDeleted")
                    .replaceAll("%x%", String.valueOf(blockLocation.getBlockX()))
                    .replaceAll("%y%", String.valueOf(blockLocation.getBlockY()))
                    .replaceAll("%z%", String.valueOf(blockLocation.getBlockZ()))
                    .replaceAll("%world%", blockLocation.getWorld().getName()));

            return;
        }

        if (regenBlock.isMined()) {
            e.setCancelled(true);
            String message = LanguageService.getMessage("Messages.BlockAlreadyBreaked");
            if (!message.trim().isEmpty()) {
                MillisecondConverter converter = new MillisecondConverter(regenBlock.getNextResetTime() - System.currentTimeMillis());
                player.sendMessage(message.replaceAll("%h%", String.valueOf(converter.getHours()))
                        .replaceAll("%m%", String.valueOf(converter.getMinutes()))
                        .replaceAll("%s%", String.valueOf(converter.getSeconds()))
                        .replaceAll("%ms%", String.format("%03d", converter.getMilliseconds())));
            }

            return;
        }

        e.setDropItems(false);

        OreRegen plugin = OreRegen.getInstance();

        Bukkit.getScheduler().runTaskLater(plugin, () -> internalMinedBlock(regenBlock, block), 1L);

        //ItemStack item = player.getInventory().getItemInMainHand();
        //if (item.getType().toString().toLowerCase().contains("pickaxe") && player.getGameMode() == GameMode.SURVIVAL) {
        //    Damageable itemDurability = (Damageable) item.getItemMeta();
        //    if (itemDurability != null) {
        //        if (item.containsEnchantment(Enchantment.DURABILITY)) {
        //            int duraLevel = item.getEnchantmentLevel(Enchantment.DURABILITY);
        //            boolean shouldDamage = ((Math.random()) < (1.0 / (duraLevel + 1)));

        //            if (shouldDamage) {
        //                itemDurability.setDamage(itemDurability.getDamage() + 1);
        //                item.setItemMeta((ItemMeta) itemDurability);
        //            }
        //        } else {
        //            itemDurability.setDamage(itemDurability.getDamage() + 1);
        //            item.setItemMeta((ItemMeta) itemDurability);
        //        }
        //    }

        //    player.updateInventory();
        //}

        if (ConfigService.isBreakChanceEnable() && isUnlucky()) {
            String message = LanguageService.getMessage("Messages.RewardUnlucky");

            if (message.length() > 0) {
                player.sendMessage(message);
            }

            return;
        }

        String message = LanguageService.getMessage("Messages.BlockMined");
        if (message.length() > 0) {
            player.sendMessage(message);
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> ConfigService.getRewardCommands().forEach(command ->
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command
                        .replace("%player%", player.getName()))), 1L);
    }

    private boolean isUnlucky() {
        int chance = ConfigService.getBreakChance();

        if (chance <= 0) {
            return true;
        }

        if (chance >= 100) {
            return false;
        }

        int rChance = new Random().nextInt(100);
        return rChance <= chance;
    }

    private void internalMinedBlock(RegenBlock regenBlock, Block block) {
        long time = getRandomTimeInRange(ConfigService.getTimerRangeMin(), ConfigService.getTimerRangeMax());
        regenBlock.setMined(time);

        try {
            block.setType(Material.valueOf(ConfigService.getReplacingBlock()));
        } catch (Exception ex) {
            block.setType(Material.BEDROCK);
            OreRegen.log.sendMessage(FormatUtils.translate("&cError while replacing block: " + ex.getMessage()));
        }
    }

    private long getRandomTimeInRange(int rMin, int rMax) {
        return ThreadLocalRandom.current().nextInt(rMin, rMax) * 1000L;
    }
}
