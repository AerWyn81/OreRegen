package fr.aerwyn81.oreregen.data;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.UUID;

public class RegenBlock {
    private final UUID identifier;
    private final Location location;
    private final Material material;

    private boolean mined;
    private long nextResetTime;

    public RegenBlock(UUID identifier, Location location, Material material) {
        this.identifier = identifier;
        this.location = location;
        this.material = material;
    }

    public UUID getIdentifier() {
        return identifier;
    }

    public Location getLocation() {
        return location;
    }

    public Material getMaterial() {
        return material;
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

    public boolean canBeReset() {
        return System.currentTimeMillis() >= nextResetTime;
    }

    public long getNextResetTime() {
        return nextResetTime;
    }
}
