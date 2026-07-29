package net.fellter.vanillalayerplus.registry;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.fellter.vanillalayerplus.VanillaLayerPlus;
import net.fellter.vanillalayerplus.block.ModBlocks;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.fabricmc.fabric.api.registry.FuelValueEvents;
import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.fabricmc.fabric.api.registry.TillableBlockRegistry;
import net.fabricmc.fabric.impl.content.registry.util.ImmutableCollectionUtils;
import net.fabricmc.fabric.mixin.content.registry.AxeItemAccessor;
import net.fabricmc.fabric.mixin.content.registry.ShovelItemAccessor;

public class ModRegistries {
	public static void register(Block input, Block stripped) {
		Block old = getRegistry().put(input, stripped);

		if (old != null) {
			VanillaLayerPlus.LOGGER.debug("Replaced old stripping mapping from {} to {} with {}", input, old, stripped);
		}
	}

	@SuppressWarnings("unstable")
	private static Map<Block, Block> getRegistry() {
		return ImmutableCollectionUtils.getAsMutableMap(AxeItemAccessor::getStrippables, AxeItemAccessor::setStrippables);
	}

	public static void registerStrippableBlocks() {
		BuiltInRegistries.BLOCK.forEach(block -> {
			if (Args.REGISTRY_ARGS.containsKey(block)) {
				RegistryArgs registryArgs = Args.REGISTRY_ARGS.get(block);

				if (BuiltInRegistries.BLOCK.getKey(block).getNamespace().equals(VanillaLayerPlus.MOD_ID) && registryArgs.stripped != null) {
					register(block, registryArgs.stripped);
				}
			}
		});
	}

	public static void registerTillableBlocks() {
		BuiltInRegistries.BLOCK.forEach(block -> {
			if (Args.REGISTRY_ARGS.containsKey(block)) {
				Identifier identifier = BuiltInRegistries.BLOCK.getKey(block);
				RegistryArgs args = Args.REGISTRY_ARGS.get(block);

				if (identifier.getNamespace().equals(VanillaLayerPlus.MOD_ID) && args.tilled != null) {
					TillableBlockRegistry.register(block, HoeItem::onlyIfAirAbove, HoeItem.changeIntoState(args.tilled.defaultBlockState()));
				}
			}
		});
	}

	public static final Map<Block, BlockState> FLATTENED_TO_BLOCK_MAP = Maps.newHashMap(new ImmutableMap.Builder<Block, BlockState>().build());

	public static void registerFlattenableBlocks() {
		flattenedToBlock(ModBlocks.DIRT_PATH_LAYER, ModBlocks.DIRT_LAYER);
		flattenedToBlock(ModBlocks.FARMLAND_LAYER, ModBlocks.DIRT_LAYER);

		BuiltInRegistries.BLOCK.forEach(block -> {
			if (Args.REGISTRY_ARGS.containsKey(block)) {
				Identifier identifier = BuiltInRegistries.BLOCK.getKey(block);
				RegistryArgs args = Args.REGISTRY_ARGS.get(block);

				if (identifier.getNamespace().equals(VanillaLayerPlus.MOD_ID) && args.flattened != null) {
					blockToFlattened(block, args.flattened);
				}
			}
		});
	}

	private static void blockToFlattened(Block input, Block flattened) {
		Objects.requireNonNull(input, "input block cannot be null");
		Objects.requireNonNull(flattened, "flattened block state cannot be null");
		BlockState old = ShovelItemAccessor.getFlattenables().put(input, flattened.defaultBlockState());

		if (old != null) {
			VanillaLayerPlus.LOGGER.debug("Replaced old flattening mapping from {} to {} with {}", input, old, flattened);
		}
	}

	private static void flattenedToBlock(Block flattened, Block output) {
		Objects.requireNonNull(flattened, "flattened block cannot be null");
		Objects.requireNonNull(output, "output block cannot be null");
		BlockState old = FLATTENED_TO_BLOCK_MAP.put(flattened, output.defaultBlockState());

		if (old != null) {
			VanillaLayerPlus.LOGGER.debug("Replaced old block mapping from {} to {} with {}", flattened, old, output);
		}
	}

	// TODO(26.2): render layers are no longer registered at runtime. Fabric's
	// BlockRenderLayerMap and vanilla's ItemBlockRenderTypes are both gone, and the
	// layer now comes from "render_type" in the block model json. Until the model
	// generator emits that for blocks flagged transparent/translucent in
	// RegistryArgs, cutout and translucent layer blocks will render as solid.
	public static void registerTransparentBlocks() {
	}

	public static void registerTranslucentBlocks() {
	}

	public static void registerFoliage() {
		BuiltInRegistries.BLOCK.forEach(block -> {
			if (Args.REGISTRY_ARGS.containsKey(block)) {
				Identifier identifier = BuiltInRegistries.BLOCK.getKey(block);
				RegistryArgs args = Args.REGISTRY_ARGS.get(block);

				if (identifier.getNamespace().equals(VanillaLayerPlus.MOD_ID)) {
					if (args.grassTinted) {
						BlockColorRegistry.register(List.of(BlockTintSources.grass()), block);
					}

					if (args.foliageTinted) {
						BlockColorRegistry.register(List.of(BlockTintSources.foliage()), block);
					}
				}
			}
		});
	}

	public static void registerOxidizableBlocks() {
		BuiltInRegistries.BLOCK.forEach(block -> {
			if (Args.REGISTRY_ARGS.containsKey(block)) {
				RegistryArgs registryArgs = Args.REGISTRY_ARGS.get(block);

				if (BuiltInRegistries.BLOCK.getKey(block).getNamespace().equals(VanillaLayerPlus.MOD_ID) && registryArgs.oxidizables != null) {
					OxidizableBlocksRegistry.registerNextStage(block, registryArgs.exposed);
					OxidizableBlocksRegistry.registerNextStage(registryArgs.exposed, registryArgs.weathered);
					OxidizableBlocksRegistry.registerNextStage(registryArgs.weathered, registryArgs.oxidized);

					OxidizableBlocksRegistry.registerWaxable(block, registryArgs.waxed);
					OxidizableBlocksRegistry.registerWaxable(registryArgs.exposed, registryArgs.exposedWaxed);
					OxidizableBlocksRegistry.registerWaxable(registryArgs.weathered, registryArgs.weatheredWaxed);
					OxidizableBlocksRegistry.registerWaxable(registryArgs.oxidized, registryArgs.oxidizedWaxed);
				}
			}
		});
	}

	public static void registerFuel() {
		BuiltInRegistries.BLOCK.forEach(block -> {
			if (Args.REGISTRY_ARGS.containsKey(block)) {
				RegistryArgs registryArgs = Args.REGISTRY_ARGS.get(block);

				if (BuiltInRegistries.BLOCK.getKey(block).getNamespace().equals(VanillaLayerPlus.MOD_ID) && registryArgs.fuel != null) {
					FuelValueEvents.BUILD.register((builder, context) -> builder.add(block, context.baseSmeltTime()));
				}
			}
		});
	}
}
