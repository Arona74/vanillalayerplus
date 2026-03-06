package net.fellter.vanillalayerplus.mixin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.block.*;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.EmptyBlockView;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(StainedGlassBlock.class)
public class MixinStainedGlassBlock extends Block {
	@Unique
	private static final Map<BlockState, VoxelShape> CACHED = new ConcurrentHashMap<>();
	@Shadow
	@Final
	private DyeColor color;

	public MixinStainedGlassBlock(Settings settings) {
		super(settings);
	}

	@Unique
	protected VoxelShape getCached(BlockState state) {
		return CACHED.computeIfAbsent(state, s -> s.getOutlineShape(EmptyBlockView.INSTANCE, BlockPos.ORIGIN));
	}

	@Override
	public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
		if (stateFrom.getBlock() instanceof Stainable stainable && !stateFrom.getBlock().equals(Blocks.BEACON)) {
			VoxelShape stateCullingShape = getCached(state);
			VoxelShape stateFromCullingShape = getCached(stateFrom);

			return VoxelShapes.isSideCovered(stateCullingShape, stateFromCullingShape, direction) && stainable.getColor() == color;
		}

		return false;
	}
}
