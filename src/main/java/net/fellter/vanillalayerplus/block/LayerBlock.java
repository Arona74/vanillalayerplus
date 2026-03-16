package net.fellter.vanillalayerplus.block;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class LayerBlock extends Block implements Waterloggable {
	public static final EnumProperty<Direction> FACING = Properties.FACING;
	public static final IntProperty LAYERS = Properties.LAYERS;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	protected static VoxelShape[] FLOOR_LAYERS_TO_SHAPE;
	protected static VoxelShape[] NORTH_LAYERS_TO_SHAPE;
	protected static VoxelShape[] SOUTH_LAYERS_TO_SHAPE;
	protected static VoxelShape[] EAST_LAYERS_TO_SHAPE;
	protected static VoxelShape[] WEST_LAYERS_TO_SHAPE;
	protected static VoxelShape[] CEILING_LAYERS_TO_SHAPE;

	public LayerBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.getStateManager().getDefaultState().with(LAYERS, 1).with(WATERLOGGED, false).with(FACING, Direction.DOWN));
	}

	protected boolean canPathfindThrough(BlockState state, BlockView world, NavigationType type) {
		if (Objects.requireNonNull(type) == NavigationType.LAND && state.get(FACING) == Direction.DOWN) {
			return state.get(LAYERS) < 5;
		}

		return false;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return getSidesShape(state, world, pos);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return getSidesShape(state, world, pos);
	}

	@Override
	public VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
		Direction direction = state.get(FACING);
		VoxelShape voxel;
		switch (direction) {
			case WEST -> voxel = WEST_LAYERS_TO_SHAPE[state.get(LAYERS)];
			case EAST -> voxel = EAST_LAYERS_TO_SHAPE[state.get(LAYERS)];
			case SOUTH -> voxel = SOUTH_LAYERS_TO_SHAPE[state.get(LAYERS)];
			case NORTH -> voxel = NORTH_LAYERS_TO_SHAPE[state.get(LAYERS)];
			case UP -> voxel = CEILING_LAYERS_TO_SHAPE[state.get(LAYERS)];
			case DOWN -> voxel = FLOOR_LAYERS_TO_SHAPE[state.get(LAYERS)];
			default -> throw new IllegalStateException("unreachable");
		}

		return voxel;
	}

	@Override
	public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return getOutlineShape(state, world, pos, context);
	}

	@Override
	public boolean hasSidedTransparency(BlockState state) {
		return true;
	}

	@Override
	public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
		return state.get(LAYERS) == 8 ? 0.35f : 1.0f;
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		for (Direction direction : DIRECTIONS) {
			boolean canPlace = world.getBlockState(pos.offset(direction)).isSideSolidFullSquare(world, pos, direction);
			if (canPlace) return true;
		}

		return false;
	}

	@Override
	public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
		if (player.isSneaking() && state.get(LAYERS) != 1) {
			world.setBlockState(pos, state.with(LAYERS, state.get(LAYERS) - 1));
			player.incrementStat(Stats.MINED.getOrCreateStat(this));
			player.addExhaustion(0.005F);
			dropStack(world, pos, new ItemStack(this));
		} else {
			super.afterBreak(world, player, pos, state, blockEntity, tool);
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		if (!state.canPlaceAt(world, pos)) {
			// Schedule a tick instead of immediately breaking, so the block can fall as an entity
			world.scheduleBlockTick(pos, this, 2);
		}

		if (state.get(WATERLOGGED)) {
			world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (!state.canPlaceAt(world, pos)) {
			if (FallingBlock.canFallThrough(world.getBlockState(pos.down()))) {
				FallingBlockEntity.spawnFromBlock(world, pos, state);
			} else {
				world.breakBlock(pos, true);
			}
		}
	}

	@Override
	public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
		return state.get(Properties.LAYERS) < 8;
	}

	@Override
	public ItemStack tryDrainFluid(WorldAccess world, BlockPos pos, BlockState state) {
		if (state.get(WATERLOGGED)) {
			world.setBlockState(pos, state.with(WATERLOGGED, false), 3);

			if (!state.canPlaceAt(world, pos)) {
				world.breakBlock(pos, true);
			}

			return new ItemStack(Items.WATER_BUCKET);
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext context) {
		int i = state.get(LAYERS);

		if (context.getStack().isOf(this.asItem()) && i < 8) {
			if (context.canReplaceExisting()) {
				return context.getSide().getOpposite() == state.get(FACING);
			}

			return context.getSide().getOpposite() == state.get(FACING);
		}

		return false;
	}

	@Override
	@Nullable
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		Direction direction = ctx.getSide().getOpposite();
		BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos());
		FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());

		if (blockState.isOf(this)) {
			int i = blockState.get(LAYERS);
			return this.getDefaultState().with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER).with(LAYERS, Math.min(8, i + 1)).with(FACING, direction);
		}

		return Objects.requireNonNull(super.getPlacementState(ctx)).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER).with(FACING, direction);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(LAYERS, WATERLOGGED, FACING);
	}

	public FluidState getFluidState(BlockState state) {
		if (state.get(Properties.LAYERS) >= 8) {
			return Fluids.EMPTY.getDefaultState();
		} else {
			return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
		}
	}

	static {
		FLOOR_LAYERS_TO_SHAPE = new VoxelShape[]{VoxelShapes.empty(),
				Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 10.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 14.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)};
		NORTH_LAYERS_TO_SHAPE = new VoxelShape[]{VoxelShapes.empty(),
				Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 2.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 4.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 6.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 8.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 10.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 12.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 14.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)};
		SOUTH_LAYERS_TO_SHAPE = new VoxelShape[]{VoxelShapes.empty(),
				Block.createCuboidShape(0.0, 0.0, 14.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 12.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 10.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 8.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 4.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 2.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)};
		WEST_LAYERS_TO_SHAPE = new VoxelShape[]{VoxelShapes.empty(),
				Block.createCuboidShape(0.0, 0.0, 0.0, 2.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 4.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 6.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 8.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 10.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 12.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 14.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)};
		EAST_LAYERS_TO_SHAPE = new VoxelShape[]{VoxelShapes.empty(),
				Block.createCuboidShape(14.0, 0.0, 0.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(12.0, 0.0, 0.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(10.0, 0.0, 0.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(8.0, 0.0, 0.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(6.0, 0.0, 0.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(4.0, 0.0, 0.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(2.0, 0.0, 0.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)};
		CEILING_LAYERS_TO_SHAPE = new VoxelShape[]{VoxelShapes.empty(),
				Block.createCuboidShape(0.0, 14.0, 0.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 12.0, 0.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 10.0, 0.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 8.0, 0.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 6.0, 0.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 4.0, 0.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 2.0, 0.0, 16.0, 16.0, 16.0),
				Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)};
	}
}
