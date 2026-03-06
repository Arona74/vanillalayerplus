package net.fellter.vanillalayerplus.mixin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.fellter.vanillalayerplus.block.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TransparentBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.EmptyBlockView;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TransparentBlock.class)
public class MixinTransparentBlock extends Block {
	@Unique
	private static final Map<BlockState, VoxelShape> CACHED = new ConcurrentHashMap<>();
	public MixinTransparentBlock(Settings settings) {
		super(settings);
	}

	@Unique
	protected VoxelShape getCached(BlockState state) {
		return CACHED.computeIfAbsent(state, s -> s.getOutlineShape(EmptyBlockView.INSTANCE, BlockPos.ORIGIN));
	}

	@Inject(method = "isSideInvisible", at = @At("HEAD"), cancellable = true)
	public void isSideInvisible(BlockState state, BlockState stateFrom, Direction direction, CallbackInfoReturnable<Boolean> cir) {
		if (stateFrom.isOf(ModBlocks.GLASS_LAYER)) {
			VoxelShape stateCullingShape = getCached(state);
			VoxelShape stateFromCullingShape = getCached(stateFrom);
			cir.setReturnValue(VoxelShapes.isSideCovered(stateCullingShape, stateFromCullingShape, direction));
		}
	}
}
