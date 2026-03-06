package net.fellter.vanillalayerplus.custom_blocks;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import net.fellter.vanillalayerplus.block.LayerBlock;
import net.fellter.vanillalayerplus.block.ModBlocks;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class SpongeLayerBlock extends LayerBlock {
	public SpongeLayerBlock(Settings settings) {
		super(settings);
	}

	protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		if (!oldState.isOf(state.getBlock())) {
			this.update(world, pos);
		}
	}

	protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
		this.update(world, pos);
		super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
	}

	protected void update(World world, BlockPos pos) {
		if (this.absorbWater(world, pos)) {
			world.setBlockState(pos, ModBlocks.WET_SPONGE_LAYER.getStateWithProperties(world.getBlockState(pos)).with(WATERLOGGED, false), 2);
			world.playSound(null, pos, SoundEvents.BLOCK_SPONGE_ABSORB, SoundCategory.BLOCKS, 1.0F, 1.0F);
		}
	}

	private boolean absorbWater(World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		int maxRange = (int) Math.ceil(state.get(LAYERS) * 0.75);

		Deque<BlockPos> posQueue = new ArrayDeque<>();
		Deque<Integer> depthQueue = new ArrayDeque<>();
		Set<Long> visited = new HashSet<>();

		posQueue.add(pos);
		depthQueue.add(0);
		visited.add(pos.asLong());

		int absorbed = 0;

		while (!posQueue.isEmpty()) {
			BlockPos currentPos = posQueue.poll();
			int depth = depthQueue.poll();

			for (Direction direction : DIRECTIONS) {
				BlockPos neighborPos = currentPos.offset(direction);
				if (!visited.add(neighborPos.asLong())) continue;

				BlockState blockState = world.getBlockState(neighborPos);
				FluidState fluidState = world.getFluidState(neighborPos);

				if (!fluidState.isIn(FluidTags.WATER)) continue;

				Block block = blockState.getBlock();
				boolean drained = false;

				if (block instanceof FluidDrainable fluidDrainable) {
					if (!fluidDrainable.tryDrainFluid(null, world, neighborPos, blockState).isEmpty()) {
						drained = true;
					}
				}

				if (!drained) {
					if (blockState.getBlock() instanceof FluidBlock) {
						world.setBlockState(neighborPos, Blocks.AIR.getDefaultState(), 3);
						drained = true;
					} else if (blockState.isOf(Blocks.KELP) || blockState.isOf(Blocks.KELP_PLANT)
							|| blockState.isOf(Blocks.SEAGRASS) || blockState.isOf(Blocks.TALL_SEAGRASS)) {
						BlockEntity blockEntity = blockState.hasBlockEntity() ? world.getBlockEntity(neighborPos) : null;
						dropStacks(blockState, world, neighborPos, blockEntity);
						world.setBlockState(neighborPos, Blocks.AIR.getDefaultState(), 3);
						drained = true;
					}
				}

				if (drained) {
					absorbed++;
					if (depth < maxRange) {
						posQueue.add(neighborPos);
						depthQueue.add(depth + 1);
					}
				}
			}
		}

		return absorbed > 0;
	}
}
