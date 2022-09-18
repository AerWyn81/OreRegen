package fr.aerwyn81.v1_17_r1;

import fr.aerwyn81.interfaces.IBlockCompatibility;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.AmethystCluster;

public class V1_17_R1 implements IBlockCompatibility {

    @Override
    public BlockFace getBlockFace(Block block) {
        if (block.getBlockData() instanceof AmethystCluster) {
            AmethystCluster amethyst = (AmethystCluster) block.getBlockData();
            return amethyst.getFacing();
        }

        return null;
    }

    @Override
    public void setBlockFace(Block block, BlockFace blockFace) {
        BlockState blockState = block.getState();

        if (block.getBlockData() instanceof AmethystCluster) {
            AmethystCluster amethyst = (AmethystCluster) blockState.getBlockData();
            amethyst.setFacing(blockFace);
            blockState.setBlockData(amethyst);
        }

        blockState.update();
    }
}
