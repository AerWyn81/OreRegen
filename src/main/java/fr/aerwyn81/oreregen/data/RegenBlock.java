package fr.aerwyn81.oreregen.data;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.UUID;

public class RegenBlock {
    private final UUID identifier;
    private final Location location;
    private final Material material;

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
}
