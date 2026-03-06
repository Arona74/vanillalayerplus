package net.fellter.vanillalayerplus.custom_blocks;

import net.fellter.vanillalayerplus.block.LayerBlock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class RedstoneOreLayerBlock extends LayerBlock {
	public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

	public RedstoneOreLayerBlock(Settings settings) {
		super(settings);
	}

	public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
		light(state, world, pos);
		super.onBlockBreakStart(state, world, pos, player);
	}

	public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
		if (!entity.bypassesSteppingEffects()) {
			light(state, world, pos);
		}

		super.onSteppedOn(world, pos, state, entity);
	}

	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if (world.isClient) {
			spawnParticles(world, pos);
		} else {
			light(state, world, pos);
		}

		return ActionResult.PASS;
	}

	private static void light(BlockState state, World world, BlockPos pos) {
		spawnParticles(world, pos);

		if (!(Boolean) state.get(LIT)) {
			world.setBlockState(pos, state.with(LIT, true), 3);
		}
	}

	public boolean hasRandomTicks(BlockState state) {
		return state.get(LIT);
	}

	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (state.get(LIT)) {
			world.setBlockState(pos, state.with(LIT, false), 3);
		}
	}

	public void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack tool, boolean dropExperience) {
		super.onStacksDropped(state, world, pos, tool, dropExperience);

		if (dropExperience) {
			this.dropExperienceWhenMined(world, pos, tool, UniformIntProvider.create(1, 5));
		}
	}

	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (state.get(LIT)) {
			spawnParticles(world, pos);
		}
	}

	private static void spawnParticles(World world, BlockPos pos) {
		Random random = world.random;

		for (Direction direction : Direction.values()) {
			BlockPos blockPos = pos.offset(direction);

			if (!world.getBlockState(blockPos).isOpaqueFullCube(world, blockPos)) {
				Direction.Axis axis = direction.getAxis();
				double e = axis == Direction.Axis.X ? (double) 0.5F + (double) 0.5625F * (double) direction.getOffsetX() : (double) random.nextFloat();
				double f = axis == Direction.Axis.Y ? (double) 0.5F + (double) 0.5625F * (double) direction.getOffsetY() : (double) random.nextFloat();
				double g = axis == Direction.Axis.Z ? (double) 0.5F + (double) 0.5625F * (double) direction.getOffsetZ() : (double) random.nextFloat();
				world.addParticle(DustParticleEffect.DEFAULT, (double) pos.getX() + e, (double) pos.getY() + f, (double) pos.getZ() + g, 0.0F, 0.0F, 0.0F);
			}
		}
	}

	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(LIT);
	}
}
