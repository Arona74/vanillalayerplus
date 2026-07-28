package net.fellter.vanillalayerplus.datagen;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethodStage;

import net.fellter.vanillalayerplus.VanillaLayerPlus;
import net.fellter.vanillalayerplus.block.LayerBlock;
import net.fellter.vanillalayerplus.block.ModBlocks;
import net.fellter.vanillalayerplus.item.ModItems;
import net.fellter.vanillalayerplus.registry.Args;
import net.fellter.vanillalayerplus.registry.DatagenArgs;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
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
					if (block instanceof LayerBlock && args.y15 && args.tintSource != null && args.fullTextureBlock != null) {
						registerLayerBlockY15(block, args.fullTextureBlock, bsmg, args.textureMap, args.tintSource);
					} else if (block instanceof LayerBlock && args.y15 && args.tintSource != null && args.parentBlock != null) {
						registerLayerBlockY15(block, args.parentBlock, bsmg, args.textureMap, args.tintSource);
					} else if (block instanceof LayerBlock && args.y15 && args.fullTextureBlock != null) {
						registerLayerBlockY15(block, args.fullTextureBlock, bsmg, args.textureMap);
					} else if (block instanceof LayerBlock && args.y15 && args.parentBlock != null) {
						registerLayerBlockY15(block, args.parentBlock, bsmg, args.textureMap);
					} else if (block instanceof LayerBlock && args.tintSource != null && args.fullTextureBlock != null) {
						registerLayerBlock(block, args.fullTextureBlock, bsmg, args.textureMap, args.tintSource);
					} else if (block instanceof LayerBlock && args.tintSource != null && args.parentBlock != null) {
						registerLayerBlock(block, args.parentBlock, bsmg, args.textureMap, args.tintSource);
					} else if (block instanceof LayerBlock && args.fullTextureBlock != null) {
						registerLayerBlock(block, args.fullTextureBlock, bsmg, args.textureMap);
					} else if (block instanceof LayerBlock && args.parentBlock != null) {
						registerLayerBlock(block, args.parentBlock, bsmg, args.textureMap);
					}
				}
			}
		});
	}

	@Override
	public void generateItemModels(ItemModelGenerators img) {
		img.generateFlatItem(ModItems.VLP_TITLE, ModelTemplates.FLAT_ITEM);
	}
}
