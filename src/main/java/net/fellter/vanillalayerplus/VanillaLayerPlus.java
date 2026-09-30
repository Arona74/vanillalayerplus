package net.fellter.vanillalayerplus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fellter.vanillalayerplus.block.ModBlocks;
import net.fellter.vanillalayerplus.item.ModItemGroups;
import net.fellter.vanillalayerplus.item.ModItems;

import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;

public class VanillaLayerPlus implements ModInitializer {
	public static final String MOD_ID = "vanillalayerplus";
	public static final Logger LOGGER = LoggerFactory.getLogger("Vanilla+ Layers");

	public static final Identifier REVERSE_RECIPES_PACK = new Identifier(MOD_ID, "reverse_recipes");

	@Override
	public void onInitialize() {
		ModBlocks.registerModBlocks();
		ModItemGroups.registerItemGroups();
		ModItems.registerModItems();

		FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(container ->
				ResourceManagerHelper.registerBuiltinResourcePack(REVERSE_RECIPES_PACK, container,
						Text.literal("Vanilla+ Layers: Reverse Recipes"), ResourcePackActivationType.DEFAULT_ENABLED));
	}

	public static boolean isNamespaced(Block block) {
		return Registries.BLOCK.getId(block).getNamespace().equals(MOD_ID);
	}
}
