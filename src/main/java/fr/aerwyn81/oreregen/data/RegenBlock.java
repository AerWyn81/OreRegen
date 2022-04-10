package fr.aerwyn81.oreregen.data;

import fr.aerwyn81.oreregen.utils.MillisecondConverter;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.UUID;

public class RegenBlock {
    private final UUID identifier;
    private final Location location;
    private final Material material;

    private boolean mined;
    private long lastMinedTime;
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

    public void setMined(boolean mined) {
        this.mined = mined;
        lastMinedTime = System.currentTimeMillis();
    }

    public void resetMinedBlock() {
        mined = false;
        location.getBlock().setType(material);
    }

    public boolean canBeReset() {
        return System.currentTimeMillis() >= nextResetTime;
    }

    public void setNextResetTime(long nextResetTime) {
        this.nextResetTime = System.currentTimeMillis() + nextResetTime;
    }

    public MillisecondConverter getTimeLeft() {
        return new MillisecondConverter(nextResetTime - System.currentTimeMillis());
    }
}
