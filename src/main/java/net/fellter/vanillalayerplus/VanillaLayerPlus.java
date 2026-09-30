package net.fellter.vanillalayerplus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fellter.vanillalayerplus.block.ModBlocks;
import net.fellter.vanillalayerplus.item.ModItemGroups;
import net.fellter.vanillalayerplus.item.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;

public class VanillaLayerPlus implements ModInitializer {
	public static final String MOD_ID = "vanillalayerplus";
	public static final Logger LOGGER = LoggerFactory.getLogger("Vanilla+ Layers");

	public static final Identifier REVERSE_RECIPES_PACK = Identifier.fromNamespaceAndPath(MOD_ID, "reverse_recipes");

	@Override
	public void onInitialize() {
		ModBlocks.registerModBlocks();
		ModItemGroups.registerItemGroups();
		ModItems.registerModItems();

		FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(container ->
				ResourceLoader.registerBuiltinPack(REVERSE_RECIPES_PACK, container,
						Component.literal("Vanilla+ Layers: Reverse Recipes"), PackActivationType.DEFAULT_ENABLED));
	}

	public static boolean isNamespaced(Block block) {
		return BuiltInRegistries.BLOCK.getKey(block).getNamespace().equals(MOD_ID);
	}
}
