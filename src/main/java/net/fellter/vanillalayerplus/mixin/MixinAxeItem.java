package net.fellter.vanillalayerplus.mixin;

import java.util.Map;
import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AxeItem.class)
public class MixinAxeItem {
	@Shadow
	@Final
	protected static Map<Block, Block> STRIPPED_BLOCKS;

	@Inject(method = "useOnBlock", at = @At(value = "HEAD"), cancellable = true)
	private void fellter$useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
		World world = context.getWorld();
		BlockPos pos = context.getBlockPos();
		PlayerEntity player = context.getPlayer();
		BlockState state = world.getBlockState(pos);

		Optional<BlockState> optional = this.fellter$getStrippedState(state);

		if (optional.isPresent()) {
			world.playSound(player, pos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0f, 1.0f);
			if (!world.isClient()) {
				world.setBlockState(pos, optional.get(), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
				if (player != null) {
					context.getStack().damage(1, player, p -> p.sendToolBreakStatus(context.getHand()));
				}
			}
			cir.setReturnValue(ActionResult.success(world.isClient()));
		}
	}

	@Unique
	private Optional<BlockState> fellter$getStrippedState(BlockState blockState) {
		return Optional.ofNullable(STRIPPED_BLOCKS.get(blockState.getBlock())).map(block ->
				block.getStateWithProperties(blockState));
	}
}
