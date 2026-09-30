package net.fellter.vanillalayerplus.datagen;

import net.fellter.vanillalayerplus.VanillaLayerPlus;
import net.fellter.vanillalayerplus.block.LayerBlock;
import net.fellter.vanillalayerplus.registry.Args;
import net.fellter.vanillalayerplus.registry.DatagenArgs;

import net.minecraft.block.Block;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;
import java.util.function.Consumer;

import net.minecraft.registry.Registries;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;

/**
 * Generates the "8 layers -> 1 full block" recipes into the built-in
 * {@code reverse_recipes} datapack, so players can toggle them per world.
 */
public class ModReverseRecipeProvider extends FabricRecipeProvider {
	public ModReverseRecipeProvider(FabricDataOutput output) {
		super(output);
	}

	private void offerReverseRecipe(Consumer<RecipeJsonProvider> exporter, Block layer, Block fullBlock) {
		String layerName = Registries.BLOCK.getId(layer).getPath();
		String fullBlockName = Registries.BLOCK.getId(fullBlock).getPath();

		ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, fullBlock, 1)
				.input('L', layer)
				.pattern("LLL")
				.pattern("L L")
				.pattern("LLL")
				.criterion(hasItem(layer), conditionsFromItem(layer))
				.offerTo(exporter, new Identifier(VanillaLayerPlus.MOD_ID, fullBlockName + "_from_" + layerName));
	}

	@Override
	public void generate(Consumer<RecipeJsonProvider> exporter) {
		Registries.BLOCK.stream().filter(VanillaLayerPlus::isNamespaced).forEach(block -> {
			DatagenArgs args = Args.DATAGEN_ARGS.get(block);

			if (block instanceof LayerBlock && args != null && args.parentBlock != null) {
				offerReverseRecipe(exporter, block, args.parentBlock);
			}
		});
	}

	@Override
	public String getName() {
		return "Reverse Layer Recipes";
	}
}
