package net.fellter.vanillalayerplus.util;

import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class ModBlockStateModelGenerator {
	private static BlockStateVariant v(Identifier id) {
		return BlockStateVariant.create().put(VariantSettings.MODEL, id);
	}

	private static BlockStateVariant v(Identifier id, VariantSettings.Rotation x, VariantSettings.Rotation y) {
		BlockStateVariant variant = BlockStateVariant.create().put(VariantSettings.MODEL, id);
		if (x != null) variant = variant.put(VariantSettings.X, x);
		if (y != null) variant = variant.put(VariantSettings.Y, y);
		return variant;
	}

	public static void registerLayerBlock(Block layerBlock, Block fullBlock, BlockStateModelGenerator bsmg, TextureMap textureMap) {
		Identifier id = ModModels.LAYER_2.upload(layerBlock, textureMap, bsmg.modelCollector);
		Identifier id2 = ModModels.LAYER_4.upload(layerBlock, textureMap, bsmg.modelCollector);
		Identifier id3 = ModModels.LAYER_6.upload(layerBlock, textureMap, bsmg.modelCollector);
		Identifier id4 = ModModels.LAYER_8.upload(layerBlock, textureMap, bsmg.modelCollector);
		Identifier id5 = ModModels.LAYER_10.upload(layerBlock, textureMap, bsmg.modelCollector);
		Identifier id6 = ModModels.LAYER_12.upload(layerBlock, textureMap, bsmg.modelCollector);
		Identifier id7 = ModModels.LAYER_14.upload(layerBlock, textureMap, bsmg.modelCollector);
		bsmg.blockStateCollector.accept(createLayerBlockState(layerBlock, id, id2, id3, id4, id5, id6, id7, ModelIds.getBlockModelId(fullBlock)));
		bsmg.registerParentedItemModel(layerBlock, id);
	}

	public static void registerLayerBlock(Block layerBlock, Identifier fullBlock, BlockStateModelGenerator bsmg, TextureMap textureMap) {
		Identifier id = ModModels.LAYER_2.upload(layerBlock, textureMap, bsmg.modelCollector);
		Identifier id2 = ModModels.LAYER_4.upload(layerBlock, textureMap, bsmg.modelCollector);
		Identifier id3 = ModModels.LAYER_6.upload(layerBlock, textureMap, bsmg.modelCollector);
		Identifier id4 = ModModels.LAYER_8.upload(layerBlock, textureMap, bsmg.modelCollector);
		Identifier id5 = ModModels.LAYER_10.upload(layerBlock, textureMap, bsmg.modelCollector);
		Identifier id6 = ModModels.LAYER_12.upload(layerBlock, textureMap, bsmg.modelCollector);
		Identifier id7 = ModModels.LAYER_14.upload(layerBlock, textureMap, bsmg.modelCollector);
		bsmg.blockStateCollector.accept(createLayerBlockState(layerBlock, id, id2, id3, id4, id5, id6, id7, fullBlock));
		bsmg.registerParentedItemModel(layerBlock, id);
	}

	public static void registerLayerBlockY15(Block layerBlock, Block fullBlock, BlockStateModelGenerator bsmg, TextureMap textureMap) {
		Identifier id = ModModels.LAYER_1.upload(layerBlock, textureMap, bsmg.modelCollector);
		Identifier id2 = ModModels.LAYER_3.upload(layerBlock, textureMap, bsmg.modelCollector);
		Identifier id3 = ModModels.LAYER_5.upload(layerBlock, textureMap, bsmg.modelCollector);
		Identifier id4 = ModModels.LAYER_7.upload(layerBlock, textureMap, bsmg.modelCollector);
		Identifier id5 = ModModels.LAYER_9.upload(layerBlock, textureMap, bsmg.modelCollector);
		Identifier id6 = ModModels.LAYER_11.upload(layerBlock, textureMap, bsmg.modelCollector);
		Identifier id7 = ModModels.LAYER_13.upload(layerBlock, textureMap, bsmg.modelCollector);
		bsmg.blockStateCollector.accept(createLayerBlockState(layerBlock, id, id2, id3, id4, id5, id6, id7, ModelIds.getBlockModelId(fullBlock)));
		bsmg.registerParentedItemModel(layerBlock, id);
	}

	public static BlockStateSupplier createLayerBlockState(Block layerBlock,
			Identifier h2, Identifier h4, Identifier h6, Identifier h8,
			Identifier h10, Identifier h12, Identifier h14, Identifier fullBlock) {
		return VariantsBlockStateSupplier.create(layerBlock)
				.coordinate(BlockStateVariantMap.create(Properties.LAYERS, Properties.FACING)
						// down
						.register(1, Direction.DOWN, v(h2))
						.register(2, Direction.DOWN, v(h4))
						.register(3, Direction.DOWN, v(h6))
						.register(4, Direction.DOWN, v(h8))
						.register(5, Direction.DOWN, v(h10))
						.register(6, Direction.DOWN, v(h12))
						.register(7, Direction.DOWN, v(h14))
						.register(8, Direction.DOWN, v(fullBlock))
						// up (x180)
						.register(1, Direction.UP, v(h2, VariantSettings.Rotation.R180, null))
						.register(2, Direction.UP, v(h4, VariantSettings.Rotation.R180, null))
						.register(3, Direction.UP, v(h6, VariantSettings.Rotation.R180, null))
						.register(4, Direction.UP, v(h8, VariantSettings.Rotation.R180, null))
						.register(5, Direction.UP, v(h10, VariantSettings.Rotation.R180, null))
						.register(6, Direction.UP, v(h12, VariantSettings.Rotation.R180, null))
						.register(7, Direction.UP, v(h14, VariantSettings.Rotation.R180, null))
						.register(8, Direction.UP, v(fullBlock, VariantSettings.Rotation.R180, null))
						// north (x270)
						.register(1, Direction.NORTH, v(h2, VariantSettings.Rotation.R270, null))
						.register(2, Direction.NORTH, v(h4, VariantSettings.Rotation.R270, null))
						.register(3, Direction.NORTH, v(h6, VariantSettings.Rotation.R270, null))
						.register(4, Direction.NORTH, v(h8, VariantSettings.Rotation.R270, null))
						.register(5, Direction.NORTH, v(h10, VariantSettings.Rotation.R270, null))
						.register(6, Direction.NORTH, v(h12, VariantSettings.Rotation.R270, null))
						.register(7, Direction.NORTH, v(h14, VariantSettings.Rotation.R270, null))
						.register(8, Direction.NORTH, v(fullBlock, VariantSettings.Rotation.R270, null))
						// west (x270, y270)
						.register(1, Direction.WEST, v(h2, VariantSettings.Rotation.R270, VariantSettings.Rotation.R270))
						.register(2, Direction.WEST, v(h4, VariantSettings.Rotation.R270, VariantSettings.Rotation.R270))
						.register(3, Direction.WEST, v(h6, VariantSettings.Rotation.R270, VariantSettings.Rotation.R270))
						.register(4, Direction.WEST, v(h8, VariantSettings.Rotation.R270, VariantSettings.Rotation.R270))
						.register(5, Direction.WEST, v(h10, VariantSettings.Rotation.R270, VariantSettings.Rotation.R270))
						.register(6, Direction.WEST, v(h12, VariantSettings.Rotation.R270, VariantSettings.Rotation.R270))
						.register(7, Direction.WEST, v(h14, VariantSettings.Rotation.R270, VariantSettings.Rotation.R270))
						.register(8, Direction.WEST, v(fullBlock, VariantSettings.Rotation.R270, VariantSettings.Rotation.R270))
						// south (x90)
						.register(1, Direction.SOUTH, v(h2, VariantSettings.Rotation.R90, null))
						.register(2, Direction.SOUTH, v(h4, VariantSettings.Rotation.R90, null))
						.register(3, Direction.SOUTH, v(h6, VariantSettings.Rotation.R90, null))
						.register(4, Direction.SOUTH, v(h8, VariantSettings.Rotation.R90, null))
						.register(5, Direction.SOUTH, v(h10, VariantSettings.Rotation.R90, null))
						.register(6, Direction.SOUTH, v(h12, VariantSettings.Rotation.R90, null))
						.register(7, Direction.SOUTH, v(h14, VariantSettings.Rotation.R90, null))
						.register(8, Direction.SOUTH, v(fullBlock, VariantSettings.Rotation.R90, null))
						// east (x270, y90)
						.register(1, Direction.EAST, v(h2, VariantSettings.Rotation.R270, VariantSettings.Rotation.R90))
						.register(2, Direction.EAST, v(h4, VariantSettings.Rotation.R270, VariantSettings.Rotation.R90))
						.register(3, Direction.EAST, v(h6, VariantSettings.Rotation.R270, VariantSettings.Rotation.R90))
						.register(4, Direction.EAST, v(h8, VariantSettings.Rotation.R270, VariantSettings.Rotation.R90))
						.register(5, Direction.EAST, v(h10, VariantSettings.Rotation.R270, VariantSettings.Rotation.R90))
						.register(6, Direction.EAST, v(h12, VariantSettings.Rotation.R270, VariantSettings.Rotation.R90))
						.register(7, Direction.EAST, v(h14, VariantSettings.Rotation.R270, VariantSettings.Rotation.R90))
						.register(8, Direction.EAST, v(fullBlock, VariantSettings.Rotation.R270, VariantSettings.Rotation.R90))
		);
	}
}
