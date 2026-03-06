package net.fellter.vanillalayerplus.custom_blocks;

import org.jetbrains.annotations.Nullable;

import net.fellter.vanillalayerplus.block.LayerBlock;
import net.fellter.vanillalayerplus.registry.ModRegistries;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;

public class FlattenableLayerBlock extends LayerBlock {
	protected static VoxelShape[] FLOOR_LAYERS_TO_SHAPE_15;
	protected static VoxelShape[] NORTH_LAYERS_TO_SHAPE_15;
	protected static VoxelShape[] SOUTH_LAYERS_TO_SHAPE_15;
	protected static VoxelShape[] EAST_LAYERS_TO_SHAPE_15;
	protected static VoxelShape[] WEST_LAYERS_TO_SHAPE_15;
	protected static VoxelShape[] CEILING_LAYERS_TO_SHAPE_15;

	public FlattenableLayerBlock(Settings settings) {
		super(settings);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		if (direction == Direction.UP) {
			world.scheduleBlockTick(pos, this, 1);
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		setToDirt(null, state, world, pos);
	}

	public void setToDirt(@Nullable Entity entity, BlockState from, World world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		BlockState state = ModRegistries.FLATTENED_TO_BLOCK_MAP.get(blockState.getBlock()).getBlock().getStateWithProperties(blockState);
		world.setBlockState(pos, state);
		world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(entity, state));
	}

	@Override
	public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
		return super.getPlacementState(ctx);
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		if (world.getBlockState(pos.up()).isSolidBlock(world, pos.up()) && state.get(LAYERS) == 8) {
			world.scheduleBlockTick(pos, this, 1);
		}
	}

	@Override
	public VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
		Direction direction = state.get(FACING);
		VoxelShape voxel;
		switch (direction) {
			case WEST -> voxel = WEST_LAYERS_TO_SHAPE_15[state.get(LAYERS)];
			case EAST -> voxel = EAST_LAYERS_TO_SHAPE_15[state.get(LAYERS)];
			case SOUTH -> voxel = SOUTH_LAYERS_TO_SHAPE_15[state.get(LAYERS)];
			case NORTH -> voxel = NORTH_LAYERS_TO_SHAPE_15[state.get(LAYERS)];
			case UP -> voxel = CEILING_LAYERS_TO_SHAPE_15[state.get(LAYERS)];
			case DOWN -> voxel = FLOOR_LAYERS_TO_SHAPE_15[state.get(LAYERS)];
			default -> throw new MatchException(null, null);
		}

		return voxel;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return getSidesShape(state, world, pos);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return getSidesShape(state, world, pos);
	}

	static {
		FLOOR_LAYERS_TO_SHAPE_15 = new VoxelShape[]{VoxelShapes.empty(),
				Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 3.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 5.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 7.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 9.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 11.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 13.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 15.0, 16.0)};
		NORTH_LAYERS_TO_SHAPE_15 = new VoxelShape[]{VoxelShapes.empty(),
				Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 1.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 3.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 5.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 7.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 9.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 11.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 13.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 15.0)};
		SOUTH_LAYERS_TO_SHAPE_15 = new VoxelShape[]{VoxelShapes.empty(),
				Block.createCuboidShape(0.0, 0.0, 15.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 13.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 11.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 9.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 7.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 5.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 3.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 1.0, 16.0, 16.0, 16.0)};
		WEST_LAYERS_TO_SHAPE_15 = new VoxelShape[]{VoxelShapes.empty(),
				Block.createCuboidShape(0.0, 0.0, 0.0, 1.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 3.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 5.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 7.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 9.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 11.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 13.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 15.0, 16.0, 16.0)};
		EAST_LAYERS_TO_SHAPE_15 = new VoxelShape[]{VoxelShapes.empty(),
				Block.createCuboidShape(15.0, 0.0, 0.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(13.0, 0.0, 0.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(11.0, 0.0, 0.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(9.0, 0.0, 0.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(7.0, 0.0, 0.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(5.0, 0.0, 0.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(3.0, 0.0, 0.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(1.0, 0.0, 0.0, 16.0, 16.0, 16.0)};
		CEILING_LAYERS_TO_SHAPE_15 = new VoxelShape[]{VoxelShapes.empty(),
				Block.createCuboidShape(0.0, 15.0, 0.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 13.0, 0.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 11.0, 0.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 9.0, 0.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 7.0, 0.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 5.0, 0.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 3.0, 0.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 1.0, 0.0, 16.0, 16.0, 16.0)};
	}
}
