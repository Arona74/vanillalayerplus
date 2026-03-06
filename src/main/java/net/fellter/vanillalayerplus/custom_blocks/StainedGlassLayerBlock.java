package net.fellter.vanillalayerplus.custom_blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.Stainable;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Direction;

public class StainedGlassLayerBlock extends GlassLayerBlock implements Stainable {
	private final DyeColor color;

	public StainedGlassLayerBlock(DyeColor color, Settings settings) {
		super(settings);
		this.color = color;
	}

	@Override
	public DyeColor getColor() {
		return color;
	}

	public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
		if (stateFrom.getBlock() instanceof Stainable stainable) {
			return stainable.getColor() == color && super.isSideInvisible(state, stateFrom, direction);
		}

		return false;
	}
}
