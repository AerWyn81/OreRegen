package fr.aerwyn81.oreregen.data;

import fr.aerwyn81.oreregen.OreRegen;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import java.util.UUID;

public class RegenBlock {
    private final UUID identifier;
    private final Vector vector;
    private final String worldName;
    private final Material material;

    private Block block;
    private BlockFace blockFacing;
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

        block.setType(material);
        OreRegen.getInstance().getBlockCompatibility().setBlockFace(block, blockFacing);
    }

    public Block getBlock() {
        return block;
    }

    public void registerBlock(World world) {
        block = new Location(world, vector.getBlockX(), vector.getBlockY(), vector.getBlockZ()).getBlock();
        blockFacing = OreRegen.getInstance().getBlockCompatibility().getBlockFace(block);
    }

    public boolean canBeReset() {
        return System.currentTimeMillis() >= nextResetTime;
    }

    public long getNextResetTime() {
        return nextResetTime;
    }
}
