package net.fellter.vanillalayerplus.item;

import java.util.function.Function;

import net.fellter.vanillalayerplus.VanillaLayerPlus;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class ModItems {
	public static final Item VLP_TITLE = registerItem("vlp_title", Item::new);

	private static Item registerItem(String name, Function<Item.Properties, Item> function) {
		return Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(VanillaLayerPlus.MOD_ID, name), function.apply(new Item.Properties().setId(keyOfItem(name))));
	}

	private static ResourceKey<Item> keyOfItem(String name) {
		return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(VanillaLayerPlus.MOD_ID, name));
	}

	public static void registerModItems() {
	}
}
