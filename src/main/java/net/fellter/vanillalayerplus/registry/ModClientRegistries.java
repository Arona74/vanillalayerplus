package net.fellter.vanillalayerplus.registry;

import net.fellter.vanillalayerplus.VanillaLayerPlus;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.FoliageColors;
import net.minecraft.world.biome.GrassColors;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;

/**
 * Client-only registrations. Kept out of {@link ModRegistries} so that class can run
 * from the common initializer on a dedicated server, where client classes are absent.
 */
public class ModClientRegistries {
	public static void registerTransparentBlocks() {
		Registries.BLOCK.forEach(block -> {
			if (ModRegistryArgs.REGISTRY_ARGS.containsKey(block)) {
				RegistryArgs registryArgs = ModRegistryArgs.REGISTRY_ARGS.get(block);

				if (Registries.BLOCK.getId(block).getNamespace().equals(VanillaLayerPlus.MOD_ID) && registryArgs.transparent) {
					BlockRenderLayerMap.putBlock(block, BlockRenderLayer.CUTOUT);
				}
			}
		});
	}

	public static void registerTranslucentBlocks() {
		Registries.BLOCK.forEach(block -> {
			if (ModRegistryArgs.REGISTRY_ARGS.containsKey(block)) {
				RegistryArgs registryArgs = ModRegistryArgs.REGISTRY_ARGS.get(block);

				if (Registries.BLOCK.getId(block).getNamespace().equals(VanillaLayerPlus.MOD_ID) && registryArgs.translucent) {
					BlockRenderLayerMap.putBlock(block, BlockRenderLayer.TRANSLUCENT);
				}
			}
		});
	}

	public static void registerFoliage() {
		Registries.BLOCK.forEach(block -> {
			if (ModRegistryArgs.REGISTRY_ARGS.containsKey(block)) {
				Identifier identifier = Registries.BLOCK.getId(block);
				RegistryArgs args = ModRegistryArgs.REGISTRY_ARGS.get(block);

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
								return FoliageColors.DEFAULT;
							}

							return BiomeColors.getFoliageColor(world, pos);
						}), block);
					}
				}
			}
		});
	}
}
