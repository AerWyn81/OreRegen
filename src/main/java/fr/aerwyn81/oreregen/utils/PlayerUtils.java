package fr.aerwyn81.oreregen.utils;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerUtils {

    public static boolean hasPermission(CommandSender sender, String permission) {
        if (!(sender instanceof Player)) {
            return true;
        }

        return sender.hasPermission(permission) || sender.isOp();
    }

    public static int getEmptySlots(Player player) {
        int i = 0;

        for (ItemStack is : player.getInventory().getStorageContents()) {
            if (is != null && is.getType() != Material.AIR)
                continue;
            i++;
        }
        return i;
    }
}
