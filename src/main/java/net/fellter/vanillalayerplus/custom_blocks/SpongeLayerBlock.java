package net.fellter.vanillalayerplus.custom_blocks;

import org.jetbrains.annotations.Nullable;

import net.fellter.vanillalayerplus.block.LayerBlock;
import net.fellter.vanillalayerplus.block.ModBlocks;

import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.redstone.Orientation;

public class SpongeLayerBlock extends LayerBlock {
	public SpongeLayerBlock(Properties settings) {
		super(settings);
	}

	protected void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
		if (!oldState.is(state.getBlock())) {
			this.update(world, pos);
		}
	}

	protected void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, @Nullable Orientation wireOrientation, boolean notify) {
		this.update(world, pos);
		super.neighborChanged(state, world, pos, sourceBlock, wireOrientation, notify);
	}

	protected void update(Level world, BlockPos pos) {
		if (this.absorbWater(world, pos)) {
			world.setBlock(pos, ModBlocks.WET_SPONGE_LAYER.withPropertiesOf(world.getBlockState(pos)).setValue(WATERLOGGED, false), 2);
			world.playSound(null, pos, SoundEvents.SPONGE_ABSORB, SoundSource.BLOCKS, 1.0F, 1.0F);
		}
	}

	private boolean absorbWater(Level world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);

		return BlockPos.breadthFirstTraversal(pos, (int) Math.ceil(state.getValue(LAYERS) * 0.75), 65, (currentPos, queuer) -> {
			for (Direction direction : UPDATE_SHAPE_ORDER) {
				queuer.accept(currentPos.relative(direction));
			}
		}, (currentPos) -> {
			if (currentPos.equals(pos)) {
				return BlockPos.TraversalNodeStatus.ACCEPT;
			} else {
				BlockState blockState = world.getBlockState(currentPos);
				FluidState fluidState = world.getFluidState(currentPos);

				if (!fluidState.is(FluidTags.WATER)) {
					return BlockPos.TraversalNodeStatus.SKIP;
				} else {
					Block block = blockState.getBlock();

					if (block instanceof BucketPickup fluidDrainable) {
						if (!fluidDrainable.pickupBlock(null, world, currentPos, blockState).isEmpty()) {
							return BlockPos.TraversalNodeStatus.ACCEPT;
						}
					}

					if (blockState.getBlock() instanceof LiquidBlock) {
						world.setBlock(currentPos, Blocks.AIR.defaultBlockState(), 3);
					} else {
						if (!blockState.is(Blocks.KELP) && !blockState.is(Blocks.KELP_PLANT) && !blockState.is(Blocks.SEAGRASS) && !blockState.is(Blocks.TALL_SEAGRASS)) {
							return BlockPos.TraversalNodeStatus.SKIP;
						}

						BlockEntity blockEntity = blockState.hasBlockEntity() ? world.getBlockEntity(currentPos) : null;
						dropResources(blockState, world, currentPos, blockEntity);
						world.setBlock(currentPos, Blocks.AIR.defaultBlockState(), 3);
					}

					return BlockPos.TraversalNodeStatus.ACCEPT;
				}
			}
		}) > 1;
	}
}
