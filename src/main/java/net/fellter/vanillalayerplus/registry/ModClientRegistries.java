package net.fellter.vanillalayerplus.registry;

import net.fellter.vanillalayerplus.VanillaLayerPlus;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.FoliageColors;
import net.minecraft.world.biome.GrassColors;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
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
					BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getCutout());
				}
			}
		});
	}

	public static void registerTranslucentBlocks() {
		Registries.BLOCK.forEach(block -> {
			if (ModRegistryArgs.REGISTRY_ARGS.containsKey(block)) {
				RegistryArgs registryArgs = ModRegistryArgs.REGISTRY_ARGS.get(block);

				if (Registries.BLOCK.getId(block).getNamespace().equals(VanillaLayerPlus.MOD_ID) && registryArgs.translucent) {
					BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getTranslucent());
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
								return FoliageColors.getDefaultColor();
							}

							return BiomeColors.getFoliageColor(world, pos);
						}), block);
					}
				}
			}
		});
	}
}
