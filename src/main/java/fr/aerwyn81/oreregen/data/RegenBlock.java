package fr.aerwyn81.oreregen.data;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.UUID;

public class RegenBlock {
    private final UUID identifier;
    private final Vector vector;
    private final String worldName;
    private final Material material;

    private Location location;
    private boolean mined;
    private long nextResetTime;

    public RegenBlock(UUID identifier, Vector vector, String worldName, Material material) {
        this.identifier = identifier;
        this.vector = vector;
        this.worldName = worldName;
        this.material = material;
    }

    public UUID getIdentifier() {
        return identifier;
    }

    public Material getMaterial() {
        return material;
    }

    public Vector getVector() {
        return vector;
    }

    public String getWorldName() {
        return worldName;
    }

    public boolean isMined() {
        return mined;
    }

    public void setMined(long time) {
        this.mined = true;
        this.nextResetTime = System.currentTimeMillis() + time;
    }

    public void resetMinedBlock() {
        mined = false;
        location.getBlock().setType(material);
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(World world) {
        location = new Location(world, vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }

    public boolean canBeReset() {
        return System.currentTimeMillis() >= nextResetTime;
    }

    public long getNextResetTime() {
        return nextResetTime;
    }
}
