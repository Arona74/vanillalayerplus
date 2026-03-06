package net.fellter.vanillalayerplus.custom_blocks;

import org.jetbrains.annotations.Nullable;

import net.fellter.vanillalayerplus.block.LayerBlock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class CoralLayerBlock extends LayerBlock {
	private final Block dead;

	public CoralLayerBlock(Block dead, Settings settings) {
		super(settings);
		this.dead = dead;
	}

	protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (!this.isInWater(world, pos)) {
			world.setBlockState(pos, this.dead.getStateWithProperties(state), 2);
		}
	}

	protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		if (!this.isInWater(world, pos)) {
			world.scheduleBlockTick(pos, this, 60 + world.getRandom().nextInt(40));
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	protected boolean isInWater(BlockView world, BlockPos pos) {
		for (Direction direction : Direction.values()) {
			FluidState fluidState = world.getFluidState(pos.offset(direction));

			if (fluidState.isIn(FluidTags.WATER)) {
				return true;
			}
		}

		return false;
	}

	@Nullable
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		if (!this.isInWater(ctx.getWorld(), ctx.getBlockPos())) {
			ctx.getWorld().scheduleBlockTick(ctx.getBlockPos(), this, 60 + ctx.getWorld().getRandom().nextInt(40));
		}

		return super.getPlacementState(ctx);
	}
}
