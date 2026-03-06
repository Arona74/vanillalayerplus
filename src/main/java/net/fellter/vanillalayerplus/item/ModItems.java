package net.fellter.vanillalayerplus.item;

import java.util.function.Function;

import net.fellter.vanillalayerplus.VanillaLayerPlus;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
	public static final Item VLP_TITLE = registerItem("vlp_title", Item::new);

	private static Item registerItem(String name, Function<Item.Settings, Item> function) {
		return Registry.register(Registries.ITEM, Identifier.of(VanillaLayerPlus.MOD_ID, name), function.apply(new Item.Settings()));
	}


	public static void registerModItems() {
	}
}
