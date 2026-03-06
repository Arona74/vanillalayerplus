package net.fellter.vanillalayerplus.custom_blocks;

import net.fellter.vanillalayerplus.block.LayerBlock;

import net.minecraft.block.BlockState;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;

import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class FallingLayerBlock extends LayerBlock {
	public FallingLayerBlock(Settings settings) {
		super(settings);
	}

	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		world.scheduleBlockTick(pos, this, this.getFallDelay());
	}

	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		world.scheduleBlockTick(pos, this, this.getFallDelay());
		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (canFallThrough(world.getBlockState(pos.down())) && pos.getY() >= world.getBottomY()) {
			FallingBlockEntity fallingBlockEntity = FallingBlockEntity.spawnFromBlock(world, pos, state);
			this.configureFallingBlockEntity(fallingBlockEntity);
		}
	}

	protected void configureFallingBlockEntity(FallingBlockEntity entity) {
	}

	protected int getFallDelay() {
		return 2;
	}

	public static boolean canFallThrough(BlockState state) {
		return state.isAir() || state.isIn(BlockTags.FIRE) || state.isLiquid() || state.isReplaceable();
	}

	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (random.nextInt(16) == 0) {
			BlockPos blockPos = pos.down();

			if (canFallThrough(world.getBlockState(blockPos))) {
				world.addParticle(new BlockStateParticleEffect(ParticleTypes.FALLING_DUST, state),
					pos.getX() + random.nextFloat(),
					pos.getY() + random.nextFloat(),
					pos.getZ() + random.nextFloat(),
					0.0, 0.0, 0.0);
			}
		}
	}
}
