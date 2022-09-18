package fr.aerwyn81.v1_16_r3;

import fr.aerwyn81.interfaces.IBlockCompatibility;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.Directional;

public class V1_16_R3 implements IBlockCompatibility {

    @Override
    public BlockFace getBlockFace(Block block) {
        if(block.getBlockData() instanceof Directional) {
            return ((Directional) block.getBlockData()).getFacing();
        }

        return null;
    }

    @Override
    public void setBlockFace(Block block, BlockFace blockFace) {
        if(block.getBlockData() instanceof Directional) {
            BlockState blockState = block.getState();
            ((Directional) blockState).setFacingDirection(blockFace);
            blockState.update();
        }
    }
}
