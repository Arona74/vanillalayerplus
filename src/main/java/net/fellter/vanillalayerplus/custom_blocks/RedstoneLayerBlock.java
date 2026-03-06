package net.fellter.vanillalayerplus.custom_blocks;

import net.fellter.vanillalayerplus.block.LayerBlock;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public class RedstoneLayerBlock extends LayerBlock {
	public RedstoneLayerBlock(Settings settings) {
		super(settings);
	}

	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return (state.get(LAYERS) * 2) - 1;
	}
}
