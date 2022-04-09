package fr.aerwyn81.oreregen.handlers;

import fr.aerwyn81.oreregen.OreRegen;
import fr.aerwyn81.oreregen.data.RegenBlock;
import fr.aerwyn81.oreregen.utils.FormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class LocationHandler {
    private final File configFile;
    private FileConfiguration config;

    private ArrayList<RegenBlock> blocks;

    public LocationHandler(OreRegen main) {
        this.blocks = new ArrayList<>();

        configFile = new File(main.getDataFolder(), "locations.yml");
    }

    public static boolean areEquals(Location loc1, Location loc2) {
        return loc1 != null && loc2 != null && loc1.getBlockX() == loc2.getBlockX()
                && loc1.getBlockY() == loc2.getBlockY()
                && loc1.getBlockZ() == loc2.getBlockZ()
                && loc1.getWorld() != null && loc2.getWorld() != null &&
                loc1.getWorld().getName().equals(loc2.getWorld().getName());
    }

    public void loadLocations() {
        config = YamlConfiguration.loadConfiguration(configFile);

        ConfigurationSection locs = config.getConfigurationSection("blocks");

        if (locs == null) {
            blocks = new ArrayList<>();
            return;
        }

        locs.getKeys(false).forEach(uuid -> {
            ConfigurationSection locationSection = config.getConfigurationSection("blocks." + uuid + ".location");
            String blockType = config.getString("blocks." + uuid + ".blockType", "");

            if (locationSection != null && !blockType.isEmpty()) {
                Map<String, Object> serializedLoc = locationSection.getValues(false);

                try {
                    blocks.add(new RegenBlock(UUID.fromString(uuid), Location.deserialize(serializedLoc), Material.valueOf(blockType)));
                } catch (Exception e) {
                    OreRegen.log.sendMessage(FormatUtils.translate("&cCannot deserialize configuration of block " + uuid + ": " + e.getMessage()));
                }
            }
        });
    }

    public void addBlock(Block block) {
        UUID uniqueUuid = UUID.randomUUID();
        while (getBlockByUUID(uniqueUuid) != null) {
            uniqueUuid = UUID.randomUUID();
        }

        blocks.add(new RegenBlock(uniqueUuid, block.getLocation(), block.getType()));
        saveLocations();
    }

    public void removeBlock(RegenBlock block) {
        block.getLocation().getBlock().setType(Material.AIR);

        blocks.remove(block);
        saveLocations();
        saveConfig();
    }

    public void saveLocations() {
        config.set("blocks", new ArrayList<>());

        blocks.forEach(key -> {
            String item = "blocks." + key.getIdentifier().toString();
            config.set(item + ".blockType", key.getMaterial().name());
            config.set(item + ".location", key.getLocation().serialize());
        });
        saveConfig();
    }

    private void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot save to {0}", configFile.getName());
        }
    }

    public RegenBlock getBlockByUUID(UUID blockUuid) {
        return blocks.stream().filter(rB -> rB.getIdentifier().equals(blockUuid)).findFirst().orElse(null);
    }

    public RegenBlock getBlockByLocation(Location loc) {
        return blocks.stream().filter(rB -> areEquals(rB.getLocation(), loc)).findFirst().orElse(null);
    }

    public ArrayList<RegenBlock> getBlocks() {
        return blocks;
    }
}
