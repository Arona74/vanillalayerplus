package net.fellter.vanillalayerplus.util;

import net.minecraft.block.Block;
import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.TextureMap;
import net.minecraft.util.Identifier;

public class ModTextureMap {
	public static TextureMap custom(Block sideBlock, Block topBlock, Block bottomBlock, String sideSuffix, String topSuffix, String bottomSuffix) {
		return new TextureMap()
				.put(TextureKey.SIDE, TextureMap.getSubId(sideBlock, sideSuffix))
				.put(TextureKey.TOP, TextureMap.getSubId(topBlock, topSuffix))
				.put(TextureKey.BOTTOM, TextureMap.getSubId(bottomBlock, bottomSuffix));
	}

	public static TextureMap blockAndTopForEnds(Block block) {
		return new TextureMap().put(TextureKey.SIDE, TextureMap.getId(block))
				.put(TextureKey.TOP, TextureMap.getSubId(block, "_top"))
				.put(TextureKey.BOTTOM, TextureMap.getSubId(block, "_top"));
	}

	public static TextureMap sideAndTopForEnds(Block block) {
		return new TextureMap().put(TextureKey.SIDE, TextureMap.getSubId(block, "_side"))
				.put(TextureKey.TOP, TextureMap.getSubId(block, "_top"))
				.put(TextureKey.BOTTOM, TextureMap.getSubId(block, "_top"));
	}

	public static TextureMap blockTB(Block block) {
		return new TextureMap()
				.put(TextureKey.TOP, TextureMap.getSubId(block, "_top"))
				.put(TextureKey.SIDE, TextureMap.getId(block))
				.put(TextureKey.BOTTOM, TextureMap.getSubId(block, "_bottom"));
	}

	public static TextureMap allWithSuffix(Block block, String suffix) {
		return new TextureMap()
				.put(TextureKey.ALL, TextureMap.getSubId(block, suffix));
	}

	public static TextureMap blockSTB(Block block) {
		return new TextureMap()
				.put(TextureKey.TOP, TextureMap.getSubId(block, "_top"))
				.put(TextureKey.SIDE, TextureMap.getSubId(block, "_side"))
				.put(TextureKey.BOTTOM, TextureMap.getSubId(block, "_bottom"));
	}

	public static TextureMap blockSTB(Identifier block) {
		return new TextureMap()
				.put(TextureKey.TOP, block.withSuffixedPath("_top"))
				.put(TextureKey.SIDE, block.withSuffixedPath("_side"))
				.put(TextureKey.BOTTOM, block.withSuffixedPath("_bottom"));
	}
}
