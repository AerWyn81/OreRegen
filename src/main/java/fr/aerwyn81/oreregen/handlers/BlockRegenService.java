package fr.aerwyn81.oreregen.handlers;

import fr.aerwyn81.oreregen.OreRegen;
import fr.aerwyn81.oreregen.data.RegenBlock;
import fr.aerwyn81.oreregen.utils.FormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;
import xyz.xenondevs.particle.task.TaskManager;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class BlockRegenService {
    private static File configFile;
    private static FileConfiguration config;

    private static ArrayList<RegenBlock> blocks;
    private static HashMap<UUID, ArrayList<Integer>> particlesCache;

    public static void initialize() {
        configFile = new File(OreRegen.getInstance().getDataFolder(), "locations.yml");
    }

    public static void loadBlocks() {
        if (blocks != null) {
            blocks.clear();
        } else {
            blocks = new ArrayList<>();
        }

        if (particlesCache != null) {
            particlesCache.clear();
        } else {
            particlesCache = new HashMap<>();
        }

        config = YamlConfiguration.loadConfiguration(configFile);

        ConfigurationSection locs = config.getConfigurationSection("blocks");

        if (locs == null) {
            blocks = new ArrayList<>();
            return;
        }

        locs.getKeys(false).forEach(uuid -> {
            ConfigurationSection locationSection = config.getConfigurationSection("blocks." + uuid + ".location");
            String worldName = config.getString("blocks." + uuid + ".world", "");
            String blockType = config.getString("blocks." + uuid + ".blockType", "");

            if (locationSection != null && !blockType.isEmpty()) {
                Map<String, Object> serializedLoc = locationSection.getValues(false);

                try {
                    RegenBlock newRegenBlock = new RegenBlock(UUID.fromString(uuid), Vector.deserialize(serializedLoc), worldName, Material.valueOf(blockType));
                    World world = Bukkit.getWorld(worldName);
                    if (world != null) {
                        newRegenBlock.setLocation(world);
                    }

                    blocks.add(newRegenBlock);
                } catch (Exception e) {
                    OreRegen.log.sendMessage(FormatUtils.translate("&cCannot deserialize configuration of block " + uuid + ": " + e.getMessage()));
                }
            }
        });
    }

    public static void addBlock(Block block) {
        UUID uniqueUuid = UUID.randomUUID();
        while (getBlockByUUID(uniqueUuid) != null) {
            uniqueUuid = UUID.randomUUID();
        }

        RegenBlock regenBlock = new RegenBlock(uniqueUuid, block.getLocation().toVector(), block.getWorld().getName(), block.getType());
        regenBlock.setLocation(block.getWorld());
        blocks.add(regenBlock);

        saveLocations();
    }

    public static void removeBlock(RegenBlock block) {
        blocks.remove(block);
        saveLocations();
        saveConfig();
    }

    public static void saveLocations() {
        config.set("blocks", new ArrayList<>());

        blocks.forEach(key -> {
            String item = "blocks." + key.getIdentifier().toString();
            config.set(item + ".blockType", key.getMaterial().name());
            config.set(item + ".world", key.getWorldName());
            config.set(item + ".location", key.getVector().serialize());
        });
        saveConfig();
    }

    private static void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot save to {0}", configFile.getName());
        }
    }

    public static RegenBlock getBlockByUUID(UUID blockUuid) {
        return blocks.stream().filter(rB -> rB.getIdentifier().equals(blockUuid)).findFirst().orElse(null);
    }

    public static RegenBlock getBlockByLocation(Location loc) {
        return blocks.stream().filter(rB -> areEquals(loc, rB.getLocation())).findFirst().orElse(null);
    }

    public static ArrayList<RegenBlock> getBlocks() {
        return blocks;
    }

    private static boolean areEquals(Location loc1, Location loc2) {
        return loc1 != null && loc2 != null && loc1.getBlockX() == loc2.getBlockX()
                && loc1.getBlockY() == loc2.getBlockY()
                && loc1.getBlockZ() == loc2.getBlockZ()
                && loc1.getWorld() != null && loc2.getWorld() != null &&
                loc1.getWorld().getName().equals(loc2.getWorld().getName());
    }

    public static void showParticlesOnBlocks(Player player) {
        if (blocks.size() != 0) {
            if (!particlesCache.containsKey(player.getUniqueId())) {
                particlesCache.put(player.getUniqueId(), new ArrayList<>());
            }

            for (RegenBlock block : blocks) {
                Location locMax = block.getLocation().clone().add(1, 1, 1);

                Integer pTask = startCubeTask(block.getLocation().getWorld(), block.getLocation().toVector(), locMax.toVector(), player);
                particlesCache.get(player.getUniqueId()).add(pTask);
            }
        }
    }


    private static Integer startCubeTask(World world, Vector start, Vector end, Player player) {
        List<Object> packets = new ArrayList<>();
        ParticleBuilder particle = new ParticleBuilder(ParticleEffect.FLAME);

        for (int x = start.getBlockX(); x <= end.getBlockX(); ++x) {
            for (int y = start.getBlockY(); y <= end.getBlockY(); ++y) {
                for (int z = start.getBlockZ(); z <= end.getBlockZ(); ++z) {
                    packets.add(particle.setLocation(new Location(world, x, y, z)).toPacket());
                }
            }
        }

        return TaskManager.startSingularTask(packets, 10, player);
    }

    public static void removeBlockParticlesCache(Player player) {
        if (particlesCache.containsKey(player.getUniqueId())) {
            particlesCache.get(player.getUniqueId()).forEach(tId -> TaskManager.getTaskManager().stopTask(tId));
            particlesCache.remove(player.getUniqueId());
        }
    }
}
