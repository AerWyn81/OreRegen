package fr.aerwyn81.interfaces;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public interface IBlockCompatibility {
    BlockFace getBlockFace(Block block);
    void setBlockFace(Block block, BlockFace blockFace);
}
