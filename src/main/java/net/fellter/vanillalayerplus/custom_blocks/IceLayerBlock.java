package net.fellter.vanillalayerplus.custom_blocks;

import org.jetbrains.annotations.Nullable;

import net.fellter.vanillalayerplus.block.LayerBlock;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class IceLayerBlock extends LayerBlock {
	public IceLayerBlock(Settings settings) {
		super(settings);
	}

	public static BlockState getMeltedState() {
		return Blocks.WATER.getDefaultState();
	}

	public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
		if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, tool) == 0) {
			if (world.getDimension().ultrawarm()) {
				world.removeBlock(pos, false);
				return;
			}

			BlockState blockState = world.getBlockState(pos.down());

			if ((blockState.blocksMovement() && state.get(LAYERS) == 8) || blockState.isLiquid()) {
				world.setBlockState(pos, getMeltedState());
			}
		} else {
			super.afterBreak(world, player, pos, state, blockEntity, tool);
		}
	}

	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (world.getLightLevel(LightType.BLOCK, pos) > 11 - state.getOpacity(world, pos)) {
			this.melt(world, pos);
		}
	}

	protected void melt(World world, BlockPos pos) {
		if (world.getDimension().ultrawarm() || world.getBlockState(pos).get(LAYERS) < 8) {
			world.removeBlock(pos, false);
		} else {
			world.setBlockState(pos, getMeltedState());
			world.updateNeighbor(pos, getMeltedState().getBlock(), pos);
		}
	}
}
