package net.fellter.vanillalayerplus.datagen;

import java.util.concurrent.CompletableFuture;

import net.fellter.vanillalayerplus.VanillaLayerPlus;
import net.fellter.vanillalayerplus.block.LayerBlock;
import net.fellter.vanillalayerplus.registry.Args;
import net.fellter.vanillalayerplus.registry.DatagenArgs;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;

public class ModLootTableProvider extends FabricBlockLootSubProvider {
	public ModLootTableProvider(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
		super(dataOutput, registryLookup);
	}

	@Override
	public void generate() {
		BuiltInRegistries.BLOCK.stream().filter(VanillaLayerPlus::isNamespaced).forEach(block -> {
			if (Args.DATAGEN_ARGS.containsKey(block)) {
				DatagenArgs datagenArgs = Args.DATAGEN_ARGS.get(block);

				if (block.getLootTable().isPresent()) {
					if (block instanceof LayerBlock && datagenArgs.needsSilkTouch) {
						add(block, silkTouchDrops(block));
					} else if (block instanceof LayerBlock) {
						add(block, layerDrops(block));
					}
				}
			}
		});
	}

	private LootTable.Builder silkTouchDrops(Block drop) {
		return LootTable.lootTable().withPool(LootPool.lootPool()
				.when(this.hasSilkTouch()).setRolls(ConstantValue.exactly(1.0F)).add(layerDropsEntry(drop)));
	}

	private LootPoolSingletonContainer.Builder<?> layerDropsEntry(Block drop) {
		return LootItem.lootTableItem(drop)
				.apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F))
						.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(drop)
								.setProperties(net.minecraft.advancements.criterion.StatePropertiesPredicate.Builder.properties().hasProperty(BlockStateProperties.LAYERS, 1))))
				.apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F))
						.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(drop)
								.setProperties(net.minecraft.advancements.criterion.StatePropertiesPredicate.Builder.properties().hasProperty(BlockStateProperties.LAYERS, 2))))
				.apply(SetItemCountFunction.setCount(ConstantValue.exactly(3.0F))
						.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(drop)
								.setProperties(net.minecraft.advancements.criterion.StatePropertiesPredicate.Builder.properties().hasProperty(BlockStateProperties.LAYERS, 3))))
				.apply(SetItemCountFunction.setCount(ConstantValue.exactly(4.0F))
						.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(drop)
								.setProperties(net.minecraft.advancements.criterion.StatePropertiesPredicate.Builder.properties().hasProperty(BlockStateProperties.LAYERS, 4))))
				.apply(SetItemCountFunction.setCount(ConstantValue.exactly(5.0F))
						.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(drop)
								.setProperties(net.minecraft.advancements.criterion.StatePropertiesPredicate.Builder.properties().hasProperty(BlockStateProperties.LAYERS, 5))))
				.apply(SetItemCountFunction.setCount(ConstantValue.exactly(6.0F))
						.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(drop)
								.setProperties(net.minecraft.advancements.criterion.StatePropertiesPredicate.Builder.properties().hasProperty(BlockStateProperties.LAYERS, 6))))
				.apply(SetItemCountFunction.setCount(ConstantValue.exactly(7.0F))
						.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(drop)
								.setProperties(net.minecraft.advancements.criterion.StatePropertiesPredicate.Builder.properties().hasProperty(BlockStateProperties.LAYERS, 7))))
				.apply(SetItemCountFunction.setCount(ConstantValue.exactly(8.0F))
						.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(drop)
								.setProperties(net.minecraft.advancements.criterion.StatePropertiesPredicate.Builder.properties().hasProperty(BlockStateProperties.LAYERS, 8))));
	}

	public LootTable.Builder layerDrops(Block drop) {
		return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
				.add(this.applyExplosionDecay(drop, layerDropsEntry(drop))));
	}
}
