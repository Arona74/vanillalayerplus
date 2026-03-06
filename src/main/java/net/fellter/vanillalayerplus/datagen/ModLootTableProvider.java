package net.fellter.vanillalayerplus.datagen;

import net.fellter.vanillalayerplus.VanillaLayerPlus;
import net.fellter.vanillalayerplus.block.LayerBlock;
import net.fellter.vanillalayerplus.registry.Args;
import net.fellter.vanillalayerplus.registry.DatagenArgs;

import net.minecraft.block.Block;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Properties;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;

public class ModLootTableProvider extends FabricBlockLootTableProvider {
	public ModLootTableProvider(FabricDataOutput dataOutput) {
		super(dataOutput);
	}

	@Override
	public void generate() {
		Registries.BLOCK.stream().filter(VanillaLayerPlus::isNamespaced).forEach(block -> {
			if (Args.DATAGEN_ARGS.containsKey(block)) {
				DatagenArgs datagenArgs = Args.DATAGEN_ARGS.get(block);

				if (block instanceof LayerBlock && datagenArgs.needsSilkTouch) {
					addDrop(block, silkTouchDrops(block));
				} else if (block instanceof LayerBlock) {
					addDrop(block, layerDrops(block));
				}
			}
		});
	}

	private LootTable.Builder silkTouchDrops(Block drop) {
		return LootTable.builder().pool(LootPool.builder()
				.conditionally(WITH_SILK_TOUCH).rolls(ConstantLootNumberProvider.create(1.0F)).with(layerDropsEntry(drop)));
	}

	private LeafEntry.Builder<?> layerDropsEntry(Block drop) {
		return ItemEntry.builder(drop)
				.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))
						.conditionally(BlockStatePropertyLootCondition.builder(drop)
								.properties(net.minecraft.predicate.StatePredicate.Builder.create().exactMatch(Properties.LAYERS, 1))))
				.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0F))
						.conditionally(BlockStatePropertyLootCondition.builder(drop)
								.properties(net.minecraft.predicate.StatePredicate.Builder.create().exactMatch(Properties.LAYERS, 2))))
				.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(3.0F))
						.conditionally(BlockStatePropertyLootCondition.builder(drop)
								.properties(net.minecraft.predicate.StatePredicate.Builder.create().exactMatch(Properties.LAYERS, 3))))
				.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(4.0F))
						.conditionally(BlockStatePropertyLootCondition.builder(drop)
								.properties(net.minecraft.predicate.StatePredicate.Builder.create().exactMatch(Properties.LAYERS, 4))))
				.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(5.0F))
						.conditionally(BlockStatePropertyLootCondition.builder(drop)
								.properties(net.minecraft.predicate.StatePredicate.Builder.create().exactMatch(Properties.LAYERS, 5))))
				.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(6.0F))
						.conditionally(BlockStatePropertyLootCondition.builder(drop)
								.properties(net.minecraft.predicate.StatePredicate.Builder.create().exactMatch(Properties.LAYERS, 6))))
				.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(7.0F))
						.conditionally(BlockStatePropertyLootCondition.builder(drop)
								.properties(net.minecraft.predicate.StatePredicate.Builder.create().exactMatch(Properties.LAYERS, 7))))
				.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(8.0F))
						.conditionally(BlockStatePropertyLootCondition.builder(drop)
								.properties(net.minecraft.predicate.StatePredicate.Builder.create().exactMatch(Properties.LAYERS, 8))));
	}

	public LootTable.Builder layerDrops(Block drop) {
		return LootTable.builder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F))
				.with(this.applyExplosionDecay(drop, layerDropsEntry(drop))));
	}
}
