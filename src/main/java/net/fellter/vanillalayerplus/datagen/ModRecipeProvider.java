package net.fellter.vanillalayerplus.datagen;

import net.fellter.vanillalayerplus.VanillaLayerPlus;
import net.fellter.vanillalayerplus.block.LayerBlock;
import net.fellter.vanillalayerplus.registry.Args;
import net.fellter.vanillalayerplus.registry.DatagenArgs;

import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.book.RecipeCategory;
import java.util.function.Consumer;

import net.minecraft.registry.Registries;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;

public class ModRecipeProvider extends FabricRecipeProvider {
	public ModRecipeProvider(FabricDataOutput output) {
		super(output);
	}

	private CraftingRecipeJsonBuilder layerBlockRecipe(ItemConvertible output, ItemConvertible input) {
		return ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, output, 16)
				.input('W', input)
				.pattern(" W")
				.pattern("W ")
				.criterion(hasItem(input), conditionsFromItem(input))
				.showNotification(true);
	}

	@Override
	public void generate(Consumer<RecipeJsonProvider> exporter) {
		Registries.BLOCK.stream().filter(VanillaLayerPlus::isNamespaced).forEach(block -> {
			if (Args.DATAGEN_ARGS.containsKey(block)) {
				DatagenArgs args = Args.DATAGEN_ARGS.get(block);

				if (args.parentBlock != null) {
					if (block instanceof LayerBlock) {
						layerBlockRecipe(block, args.parentBlock).offerTo(exporter);
					}

					if (block instanceof LayerBlock && args.stonecuttingInput != null) {
						for (ItemConvertible itemConvertible : args.stonecuttingInput) {
							offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, block, itemConvertible, 8);
						}
					}
				}
			}
		});
	}

	@Override
	public String getName() {
		return "Recipes";
	}
}
