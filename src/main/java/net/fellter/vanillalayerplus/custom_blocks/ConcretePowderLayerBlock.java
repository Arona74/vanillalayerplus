package net.fellter.vanillalayerplus.custom_blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class ConcretePowderLayerBlock extends FallingLayerBlock {
	private final BlockState hardened;

	public ConcretePowderLayerBlock(Block hardened, Settings settings) {
		super(settings);
		this.hardened = hardened.getDefaultState();
	}

	public void onLanding(World world, BlockPos pos, BlockState fallingBlockState, BlockState currentStateInPos, FallingBlockEntity fallingBlockEntity) {
		if (shouldHarden(world, pos, currentStateInPos) && pos != null) {
			world.setBlockState(pos, this.hardened
					.with(WATERLOGGED, world.getBlockState(pos).get(WATERLOGGED))
					.with(FACING, world.getBlockState(pos).get(FACING))
					.with(LAYERS, world.getBlockState(pos).get(LAYERS)), Block.NOTIFY_ALL);
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockPos blockPos = ctx.getBlockPos();
		World blockView = ctx.getWorld();
		BlockState placementState = super.getPlacementState(ctx);

		if (shouldHarden(blockView, blockPos, blockView.getBlockState(blockPos))) {
			if (placementState != null) {
				return this.hardened
						.with(WATERLOGGED, placementState.get(WATERLOGGED))
						.with(FACING, placementState.get(FACING))
						.with(LAYERS, placementState.get(LAYERS));
			}
		}

		return super.getPlacementState(ctx);
	}

	private static boolean shouldHarden(BlockView world, BlockPos pos, BlockState state) {
		return hardensIn(state) || hardensOnAnySide(world, pos);
	}

	private static boolean hardensOnAnySide(BlockView world, BlockPos pos) {
		boolean bl = false;
		BlockPos.Mutable mutable = pos.mutableCopy();

		for (Direction direction : Direction.values()) {
			BlockState blockState = world.getBlockState(mutable);
			if (direction == Direction.DOWN && !hardensIn(blockState)) continue;
			mutable.set(pos, direction);
			blockState = world.getBlockState(mutable);
			if (!hardensIn(blockState) || blockState.isSideSolidFullSquare(world, pos, direction.getOpposite())) continue;
			bl = true;
			break;
		}

		return bl;
	}

	private static boolean hardensIn(BlockState state) {
		return state.getFluidState().isIn(FluidTags.WATER);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		if (hardensOnAnySide(world, pos)) {
			return this.hardened
					.with(WATERLOGGED, world.getBlockState(pos).get(WATERLOGGED))
					.with(FACING, world.getBlockState(pos).get(FACING))
					.with(LAYERS, world.getBlockState(pos).get(LAYERS));
		}

		world.scheduleBlockTick(pos, this, this.getFallDelay());
		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}
}
