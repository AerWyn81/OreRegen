package fr.aerwyn81.oreregen.handlers;

import fr.aerwyn81.oreregen.OreRegen;
import fr.aerwyn81.oreregen.utils.FormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.javatuples.Pair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class LocationHandler {
    private final File configFile;
    private FileConfiguration config;

    private ArrayList<Pair<UUID, Location>> locations;

    public LocationHandler(OreRegen main) {
        this.locations = new ArrayList<>();

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

        ConfigurationSection locs = config.getConfigurationSection("locations");

        if (locs == null) {
            locations = new ArrayList<>();
            return;
        }

        locs.getKeys(false).forEach(uuid -> {
            ConfigurationSection configSection = config.getConfigurationSection("locations." + uuid);

            if (configSection != null) {
                Map<String, Object> serializedLoc = configSection.getValues(false);

                try {
                    locations.add(new Pair<>(UUID.fromString(uuid), Location.deserialize(serializedLoc)));
                } catch (Exception e) {
                    OreRegen.log.sendMessage(FormatUtils.translate("&cCannot deserialize location of block " + uuid));
                }
            }
        });
    }

    public UUID addLocation(Location loc) {
        UUID uniqueUuid = UUID.randomUUID();
        while (getBlockByUUID(uniqueUuid) != null) {
            uniqueUuid = UUID.randomUUID();
        }

        locations.add(new Pair<>(uniqueUuid, loc));
        saveLocations();

        return uniqueUuid;
    }

    public void removeLocation(UUID blockUuid) {
        Pair<UUID, Location> block = getBlockByUUID(blockUuid);

        if (block != null) {
            block.getValue1().getBlock().setType(Material.AIR);

            locations.remove(block);
            saveLocations();
            saveConfig();
        }
    }

    public void saveLocations() {
        config.set("locations", new ArrayList<>());

        locations.forEach(key -> config.set("locations." + key.getValue0().toString(), key.getValue1().serialize()));
        saveConfig();
    }

    private void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot save to {0}", configFile.getName());
        }
    }

    public Pair<UUID, Location> getBlockByUUID(UUID blockUuid) {
        return locations.stream().filter(p -> p.getValue0().equals(blockUuid)).findFirst().orElse(null);
    }

    public boolean isExist(Location loc) {
        return locations.stream().anyMatch(l -> areEquals(l.getValue1(), loc));
    }

    public ArrayList<Pair<UUID, Location>> getLocations() {
        return locations;
    }
}
