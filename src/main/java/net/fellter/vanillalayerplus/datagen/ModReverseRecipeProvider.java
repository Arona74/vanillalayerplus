package net.fellter.vanillalayerplus.datagen;

import java.util.concurrent.CompletableFuture;

import net.fellter.vanillalayerplus.VanillaLayerPlus;
import net.fellter.vanillalayerplus.block.LayerBlock;
import net.fellter.vanillalayerplus.registry.Args;
import net.fellter.vanillalayerplus.registry.DatagenArgs;

import net.minecraft.block.Block;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;

/**
 * Generates the "8 layers -> 1 full block" recipes into the built-in
 * {@code reverse_recipes} datapack, so players can toggle them per world.
 */
public class ModReverseRecipeProvider extends FabricRecipeProvider {
	public ModReverseRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter) {
		return new RecipeGenerator(registryLookup, exporter) {
			public void offerReverseRecipe(Block layer, Block fullBlock) {
				String layerName = Registries.BLOCK.getId(layer).getPath();
				String fullBlockName = Registries.BLOCK.getId(fullBlock).getPath();

				ShapedRecipeJsonBuilder.create(registries.getOrThrow(RegistryKeys.ITEM), RecipeCategory.BUILDING_BLOCKS, fullBlock, 1)
						.input('L', layer)
						.pattern("LLL")
						.pattern("L L")
						.pattern("LLL")
						.criterion(hasItem(layer), conditionsFromItem(layer))
						.offerTo(exporter, RegistryKey.of(RegistryKeys.RECIPE, Identifier.of(VanillaLayerPlus.MOD_ID, fullBlockName + "_from_" + layerName)));
			}

			@Override
			public void generate() {
				Registries.BLOCK.stream().filter(VanillaLayerPlus::isNamespaced).forEach(block -> {
					DatagenArgs args = Args.DATAGEN_ARGS.get(block);

					if (block instanceof LayerBlock && args != null && args.parentBlock != null) {
						offerReverseRecipe(block, args.parentBlock);
					}
				});
			}
		};
	}

	@Override
	public String getName() {
		return "Reverse Layer Recipes";
	}
}
