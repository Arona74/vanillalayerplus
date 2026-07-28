package net.fellter.vanillalayerplus.custom_blocks;

import org.jetbrains.annotations.Nullable;

import net.fellter.vanillalayerplus.block.LayerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class CoralLayerBlock extends LayerBlock {
	private final Block dead;

	public CoralLayerBlock(Block dead, Properties settings) {
		super(settings);
		this.dead = dead;
	}

	protected void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		if (!this.isInWater(world, pos)) {
			world.setBlock(pos, this.dead.withPropertiesOf(state), 2);
		}
	}

	protected BlockState updateShape(BlockState state, LevelReader world, ScheduledTickAccess tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
		if (!this.isInWater(world, pos)) {
			tickView.scheduleTick(pos, this, 60 + random.nextInt(40));
		}

		return super.updateShape(state, world, tickView, pos, direction, neighborPos, neighborState, random);
	}

	protected boolean isInWater(BlockGetter world, BlockPos pos) {
		for (Direction direction : Direction.values()) {
			FluidState fluidState = world.getFluidState(pos.relative(direction));

			if (fluidState.is(FluidTags.WATER)) {
				return true;
			}
		}

		return false;
	}

	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		if (!this.isInWater(ctx.getLevel(), ctx.getClickedPos())) {
			ctx.getLevel().scheduleTick(ctx.getClickedPos(), this, 60 + ctx.getLevel().getRandom().nextInt(40));
		}

		return super.getStateForPlacement(ctx);
	}
}
