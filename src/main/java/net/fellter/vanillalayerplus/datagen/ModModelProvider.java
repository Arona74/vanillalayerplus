package net.fellter.vanillalayerplus.datagen;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethodStage;

import net.fellter.vanillalayerplus.VanillaLayerPlus;
import net.fellter.vanillalayerplus.block.LayerBlock;
import net.fellter.vanillalayerplus.block.ModBlocks;
import net.fellter.vanillalayerplus.item.ModItems;
import net.fellter.vanillalayerplus.registry.Args;
import net.fellter.vanillalayerplus.registry.DatagenArgs;
import net.fellter.vanillalayerplus.registry.RegistryArgs;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;

import static net.fellter.vanillalayerplus.util.ModBlockStateModelGenerator.registerLayerBlock;
import static net.fellter.vanillalayerplus.util.ModBlockStateModelGenerator.registerLayerBlockY15;

public class ModModelProvider extends FabricModelProvider {
	public ModModelProvider(FabricPackOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(BlockModelGenerators bsmg) {
		registerLayerBlock(ModBlocks.BROWN_MUSHROOM_LAYER, Identifier.fromNamespaceAndPath(VanillaLayerPlus.MOD_ID, "block/brown_mushroom_block"), bsmg, TextureMapping.cube(Blocks.BROWN_MUSHROOM_BLOCK));
		registerLayerBlock(ModBlocks.RED_MUSHROOM_LAYER, Identifier.fromNamespaceAndPath(VanillaLayerPlus.MOD_ID, "block/red_mushroom_block"), bsmg, TextureMapping.cube(Blocks.RED_MUSHROOM_BLOCK));

		BuiltInRegistries.BLOCK.stream().filter(VanillaLayerPlus::isNamespaced).forEach(block -> {
			if (Args.DATAGEN_ARGS.containsKey(block)) {
				DatagenArgs args = Args.DATAGEN_ARGS.get(block);

				if (args.textureMap != null) {
					TextureMapping textureMap = forceTranslucentIfNeeded(block, args.textureMap);

					if (block instanceof LayerBlock && args.y15 && args.tintSource != null && args.fullTextureBlock != null) {
						registerLayerBlockY15(block, args.fullTextureBlock, bsmg, textureMap, args.tintSource);
					} else if (block instanceof LayerBlock && args.y15 && args.tintSource != null && args.parentBlock != null) {
						registerLayerBlockY15(block, args.parentBlock, bsmg, textureMap, args.tintSource);
					} else if (block instanceof LayerBlock && args.y15 && args.fullTextureBlock != null) {
						registerLayerBlockY15(block, args.fullTextureBlock, bsmg, textureMap);
					} else if (block instanceof LayerBlock && args.y15 && args.parentBlock != null) {
						registerLayerBlockY15(block, args.parentBlock, bsmg, textureMap);
					} else if (block instanceof LayerBlock && args.tintSource != null && args.fullTextureBlock != null) {
						registerLayerBlock(block, args.fullTextureBlock, bsmg, textureMap, args.tintSource);
					} else if (block instanceof LayerBlock && args.tintSource != null && args.parentBlock != null) {
						registerLayerBlock(block, args.parentBlock, bsmg, textureMap, args.tintSource);
					} else if (block instanceof LayerBlock && args.fullTextureBlock != null) {
						registerLayerBlock(block, args.fullTextureBlock, bsmg, textureMap);
					} else if (block instanceof LayerBlock && args.parentBlock != null) {
						registerLayerBlock(block, args.parentBlock, bsmg, textureMap);
					}
				}
			}
		});
	}

	// 26.1 dropped runtime render layer registration. The layer is now derived from
	// the texture: alpha content is detected automatically, which covers our cutout
	// blocks, and "force_translucent" on the texture selects the blended path for
	// blocks whose sprite would otherwise be detected as cutout. Vanilla marks glass
	// and stained glass exactly this way.
	private static TextureMapping forceTranslucentIfNeeded(Block block, TextureMapping textureMap) {
		RegistryArgs registryArgs = Args.REGISTRY_ARGS.get(block);

		if (registryArgs != null && registryArgs.translucent) {
			return textureMap.updateSlots((slot, material) -> material.withForceTranslucent(true));
		}

		return textureMap;
	}

	@Override
	public void generateItemModels(ItemModelGenerators img) {
		img.generateFlatItem(ModItems.VLP_TITLE, ModelTemplates.FLAT_ITEM);
	}
}
