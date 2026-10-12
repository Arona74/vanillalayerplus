package net.fellter.vanillalayerplus.datagen;

import java.util.concurrent.CompletableFuture;

import net.fellter.vanillalayerplus.VanillaLayerPlus;
import net.fellter.vanillalayerplus.block.LayerBlock;
import net.fellter.vanillalayerplus.registry.Args;
import net.fellter.vanillalayerplus.registry.DatagenArgs;
import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.UniformContainerBase;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.MatchBlock;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;
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
				.when(this.hasSilkTouch()).setRolls(ContextIntProviders.exactly(1)).add(layerDropsEntry(drop)));
	}

	private UniformContainerBase.Builder<?> layerDropsEntry(Block drop) {
		return LootItem.lootTableItem(drop)
				.apply(LayerBlock.LAYERS.getPossibleValues(), layers -> SetItemCountFunction.setCount(ContextIntProviders.exactly(layers))
						.when(MatchBlock.blockMatches(this.blocks, drop, StatePropertiesPredicate.Builder.properties().hasProperty(LayerBlock.LAYERS, layers.intValue()))));
	}

	public LootTable.Builder layerDrops(Block drop) {
		return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ContextIntProviders.exactly(1))
				.add((LootPoolEntryContainer.Builder<?>) this.applyExplosionDecay(drop, layerDropsEntry(drop))));
	}
}
