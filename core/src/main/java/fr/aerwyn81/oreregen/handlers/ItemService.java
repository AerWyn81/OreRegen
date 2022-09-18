package fr.aerwyn81.oreregen.handlers;

import fr.aerwyn81.oreregen.OreRegen;
import fr.aerwyn81.oreregen.utils.FormatUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class ItemService {
    private static ItemStack item;

    public static void loadItem() {
        try {
            item = setItem(Material.valueOf(ConfigService.getItemType()), FormatUtils.translate(ConfigService.getItemName()), FormatUtils.translate(ConfigService.getItemLore()));
        } catch (Exception ex) {
            item = setItem(Material.STICK, FormatUtils.translate("{#eac086}&lO{#ffe39f}re{#eac086}&lR{#ffe39f}egen"), new ArrayList<>());
            OreRegen.log.sendMessage(FormatUtils.translate("&cError while configuring item: " + ex.getMessage()));
        }
    }

    private static ItemStack setItem(Material material, String name, ArrayList<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();

        if (itemMeta == null) {
            OreRegen.log.sendMessage(FormatUtils.translate("&cError while retrieving itemMeta of the plugin item. Is your server version compatible?"));
            return item;
        }

        itemMeta.setDisplayName(name);
        itemMeta.setLore(lore);
        itemMeta.addEnchant(Enchantment.KNOCKBACK, 1, false);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack getItem() {
        return item;
    }
}
