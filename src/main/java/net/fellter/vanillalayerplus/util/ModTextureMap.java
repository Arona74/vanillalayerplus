package net.fellter.vanillalayerplus.util;

import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

public class ModTextureMap {
	public static TextureMapping custom(Block sideBlock, Block topBlock, Block bottomBlock, String sideSuffix, String topSuffix, String bottomSuffix) {
		return new TextureMapping()
				.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(sideBlock, sideSuffix))
				.put(TextureSlot.TOP, TextureMapping.getBlockTexture(topBlock, topSuffix))
				.put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(bottomBlock, bottomSuffix));
	}

	public static TextureMapping blockAndTopForEnds(Block block) {
		return new TextureMapping().put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block))
				.put(TextureSlot.TOP, TextureMapping.getBlockTexture(block, "_top"))
				.put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(block, "_top"));
	}

	public static TextureMapping sideAndTopForEnds(Block block) {
		return new TextureMapping().put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "_side"))
				.put(TextureSlot.TOP, TextureMapping.getBlockTexture(block, "_top"))
				.put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(block, "_top"));
	}

	public static TextureMapping blockTB(Block block) {
		return new TextureMapping()
				.put(TextureSlot.TOP, TextureMapping.getBlockTexture(block, "_top"))
				.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block))
				.put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(block, "_bottom"));
	}

	public static TextureMapping allWithSuffix(Block block, String suffix) {
		return new TextureMapping()
				.put(TextureSlot.ALL, TextureMapping.getBlockTexture(block, suffix));
	}

	public static TextureMapping blockSTB(Block block) {
		return new TextureMapping()
				.put(TextureSlot.TOP, TextureMapping.getBlockTexture(block, "_top"))
				.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "_side"))
				.put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(block, "_bottom"));
	}

	public static TextureMapping blockSTB(Identifier block) {
		return new TextureMapping()
				.put(TextureSlot.TOP, new Material(block.withSuffix("_top")))
				.put(TextureSlot.SIDE, new Material(block.withSuffix("_side")))
				.put(TextureSlot.BOTTOM, new Material(block.withSuffix("_bottom")));
	}
}
