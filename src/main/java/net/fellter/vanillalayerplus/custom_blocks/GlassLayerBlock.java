package net.fellter.vanillalayerplus.custom_blocks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.fellter.vanillalayerplus.block.LayerBlock;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.EmptyBlockView;

public class GlassLayerBlock extends LayerBlock {
	protected static final Map<BlockState, VoxelShape> CACHED = new ConcurrentHashMap<>();
	public GlassLayerBlock(Settings settings) {
		super(settings);
	}

	protected VoxelShape getCached(BlockState state) {
		return CACHED.computeIfAbsent(state, s -> s.getOutlineShape(EmptyBlockView.INSTANCE, BlockPos.ORIGIN));
	}

	public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.empty();
	}

	public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
		return 1.0F;
	}

	protected boolean isTransparent(BlockState state) {
		return true;
	}

	public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
		VoxelShape stateCullingShape = getCached(state);
		VoxelShape stateFromCullingShape = getCached(stateFrom);
		return VoxelShapes.isSideCovered(stateCullingShape, stateFromCullingShape, direction);
	}
}
