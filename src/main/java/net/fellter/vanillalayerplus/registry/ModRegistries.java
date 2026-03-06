package net.fellter.vanillalayerplus.registry;

import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.fellter.vanillalayerplus.VanillaLayerPlus;
import net.fellter.vanillalayerplus.block.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.HoeItem;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.FoliageColors;
import net.minecraft.world.biome.GrassColors;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
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
		return ImmutableCollectionUtils.getAsMutableMap(AxeItemAccessor::getStrippedBlocks, AxeItemAccessor::setStrippedBlocks);
	}

	public static void registerStrippableBlocks() {
		Registries.BLOCK.forEach(block -> {
			if (Args.REGISTRY_ARGS.containsKey(block)) {
				RegistryArgs registryArgs = Args.REGISTRY_ARGS.get(block);

				if (Registries.BLOCK.getId(block).getNamespace().equals(VanillaLayerPlus.MOD_ID) && registryArgs.stripped != null) {
					register(block, registryArgs.stripped);
				}
			}
		});
	}

	public static void registerTillableBlocks() {
		Registries.BLOCK.forEach(block -> {
			if (Args.REGISTRY_ARGS.containsKey(block)) {
				Identifier identifier = Registries.BLOCK.getId(block);
				RegistryArgs args = Args.REGISTRY_ARGS.get(block);

				if (identifier.getNamespace().equals(VanillaLayerPlus.MOD_ID) && args.tilled != null) {
					TillableBlockRegistry.register(block, HoeItem::canTillFarmland, HoeItem.createTillAction(args.tilled.getDefaultState()));
				}
			}
		});
	}

	public static final Map<Block, BlockState> FLATTENED_TO_BLOCK_MAP = Maps.newHashMap(new ImmutableMap.Builder<Block, BlockState>().build());

	public static void registerFlattenableBlocks() {
		flattenedToBlock(ModBlocks.DIRT_PATH_LAYER, ModBlocks.DIRT_LAYER);
		flattenedToBlock(ModBlocks.FARMLAND_LAYER, ModBlocks.DIRT_LAYER);

		Registries.BLOCK.forEach(block -> {
			if (Args.REGISTRY_ARGS.containsKey(block)) {
				Identifier identifier = Registries.BLOCK.getId(block);
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
		BlockState old = ShovelItemAccessor.getPathStates().put(input, flattened.getDefaultState());

		if (old != null) {
			VanillaLayerPlus.LOGGER.debug("Replaced old flattening mapping from {} to {} with {}", input, old, flattened);
		}
	}

	private static void flattenedToBlock(Block flattened, Block output) {
		Objects.requireNonNull(flattened, "flattened block cannot be null");
		Objects.requireNonNull(output, "output block cannot be null");
		BlockState old = FLATTENED_TO_BLOCK_MAP.put(flattened, output.getDefaultState());

		if (old != null) {
			VanillaLayerPlus.LOGGER.debug("Replaced old block mapping from {} to {} with {}", flattened, old, output);
		}
	}

	public static void registerTransparentBlocks() {
		Registries.BLOCK.forEach(block -> {
			if (Args.REGISTRY_ARGS.containsKey(block)) {
				RegistryArgs registryArgs = Args.REGISTRY_ARGS.get(block);

				if (Registries.BLOCK.getId(block).getNamespace().equals(VanillaLayerPlus.MOD_ID) && registryArgs.transparent) {
					BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getCutout());
				}
			}
		});
	}

	public static void registerTranslucentBlocks() {
		Registries.BLOCK.forEach(block -> {
			if (Args.REGISTRY_ARGS.containsKey(block)) {
				RegistryArgs registryArgs = Args.REGISTRY_ARGS.get(block);

				if (Registries.BLOCK.getId(block).getNamespace().equals(VanillaLayerPlus.MOD_ID) && registryArgs.translucent) {
					BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getTranslucent());
				}
			}
		});
	}

	public static void registerFoliage() {
		Registries.BLOCK.forEach(block -> {
			if (Args.REGISTRY_ARGS.containsKey(block)) {
				Identifier identifier = Registries.BLOCK.getId(block);
				RegistryArgs args = Args.REGISTRY_ARGS.get(block);

				if (identifier.getNamespace().equals(VanillaLayerPlus.MOD_ID)) {
					if (args.grassTinted) {
						ColorProviderRegistry.BLOCK.register(((state, world, pos, tintIndex) -> {
							if (world == null || pos == null) {
								return GrassColors.getDefaultColor();
							}

							return BiomeColors.getGrassColor(world, pos);
						}), block);
					}

					if (args.foliageTinted) {
						ColorProviderRegistry.BLOCK.register(((state, world, pos, tintIndex) -> {
							if (world == null || pos == null) {
								return FoliageColors.getDefaultColor();
							}

							return BiomeColors.getFoliageColor(world, pos);
						}), block);
					}
				}
			}
		});
	}

	public static void registerOxidizableBlocks() {
		Registries.BLOCK.forEach(block -> {
			if (Args.REGISTRY_ARGS.containsKey(block)) {
				RegistryArgs registryArgs = Args.REGISTRY_ARGS.get(block);

				if (Registries.BLOCK.getId(block).getNamespace().equals(VanillaLayerPlus.MOD_ID) && registryArgs.oxidizables != null) {
					OxidizableBlocksRegistry.registerOxidizableBlockPair(block, registryArgs.exposed);
					OxidizableBlocksRegistry.registerOxidizableBlockPair(registryArgs.exposed, registryArgs.weathered);
					OxidizableBlocksRegistry.registerOxidizableBlockPair(registryArgs.weathered, registryArgs.oxidized);

					OxidizableBlocksRegistry.registerWaxableBlockPair(block, registryArgs.waxed);
					OxidizableBlocksRegistry.registerWaxableBlockPair(registryArgs.exposed, registryArgs.exposedWaxed);
					OxidizableBlocksRegistry.registerWaxableBlockPair(registryArgs.weathered, registryArgs.weatheredWaxed);
					OxidizableBlocksRegistry.registerWaxableBlockPair(registryArgs.oxidized, registryArgs.oxidizedWaxed);
				}
			}
		});
	}

	public static void registerFuel() {
	}
}
