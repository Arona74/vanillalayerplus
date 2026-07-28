package net.fellter.vanillalayerplus.util;

import net.fellter.vanillalayerplus.VanillaLayerPlus;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModTags {
	public static final TagKey<Block> LAYERS = createTag("layers");

	private static TagKey<Block> createTag(String name) {
		return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(VanillaLayerPlus.MOD_ID, name));
	}
}
