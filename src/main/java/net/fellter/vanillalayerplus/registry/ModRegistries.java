package net.fellter.vanillalayerplus.registry;

import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.fellter.vanillalayerplus.VanillaLayerPlus;
import net.fellter.vanillalayerplus.block.ModBlocks;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.component.CookingFuel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.CopyPropertiesProvider;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProviders;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;
import net.fabricmc.fabric.api.item.v1.BlockTransformerHelper;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;

public class ModRegistries {
	// 26.3 replaced the AxeItem/HoeItem/ShovelItem maps with data-driven block
	// transformers. Fabric appends our entries to the vanilla axe, hoe and shovel
	// transformers when they load. CopyPropertiesProvider carries the layer count
	// (and waterlogging) over to the resulting block, as the old item mixins did.
	private static void register(Block input, Block stripped) {
		BlockTransformerHelper.registerStripping(input, stripped);
	}

	public static void registerStrippableBlocks() {
		BuiltInRegistries.BLOCK.forEach(block -> {
			if (ModRegistryArgs.REGISTRY_ARGS.containsKey(block)) {
				RegistryArgs registryArgs = ModRegistryArgs.REGISTRY_ARGS.get(block);

				if (BuiltInRegistries.BLOCK.getKey(block).getNamespace().equals(VanillaLayerPlus.MOD_ID) && registryArgs.stripped != null) {
					register(block, registryArgs.stripped);
				}
			}
		});
	}

	public static void registerTillableBlocks() {
		BuiltInRegistries.BLOCK.forEach(block -> {
			if (ModRegistryArgs.REGISTRY_ARGS.containsKey(block)) {
				Identifier identifier = BuiltInRegistries.BLOCK.getKey(block);
				RegistryArgs args = ModRegistryArgs.REGISTRY_ARGS.get(block);

				if (identifier.getNamespace().equals(VanillaLayerPlus.MOD_ID) && args.tilled != null) {
					BlockTransformerHelper.registerTilling(BlockPredicate.matchesBlocks(block), new CopyPropertiesProvider(args.tilled));
				}
			}
		});
	}

	public static final Map<Block, BlockState> FLATTENED_TO_BLOCK_MAP = Maps.newHashMap(new ImmutableMap.Builder<Block, BlockState>().build());

	public static void registerFlattenableBlocks() {
		flattenedToBlock(ModBlocks.DIRT_PATH_LAYER, ModBlocks.DIRT_LAYER);
		flattenedToBlock(ModBlocks.FARMLAND_LAYER, ModBlocks.DIRT_LAYER);

		BuiltInRegistries.BLOCK.forEach(block -> {
			if (ModRegistryArgs.REGISTRY_ARGS.containsKey(block)) {
				Identifier identifier = BuiltInRegistries.BLOCK.getKey(block);
				RegistryArgs args = ModRegistryArgs.REGISTRY_ARGS.get(block);

				if (identifier.getNamespace().equals(VanillaLayerPlus.MOD_ID) && args.flattened != null) {
					blockToFlattened(block, args.flattened);
				}
			}
		});
	}

	private static void blockToFlattened(Block input, Block flattened) {
		Objects.requireNonNull(input, "input block cannot be null");
		Objects.requireNonNull(flattened, "flattened block state cannot be null");
		BlockTransformerHelper.registerFlattening(BlockPredicate.matchesBlocks(input), new CopyPropertiesProvider(flattened));
	}

	private static void flattenedToBlock(Block flattened, Block output) {
		Objects.requireNonNull(flattened, "flattened block cannot be null");
		Objects.requireNonNull(output, "output block cannot be null");
		BlockState old = FLATTENED_TO_BLOCK_MAP.put(flattened, output.defaultBlockState());

		if (old != null) {
			VanillaLayerPlus.LOGGER.debug("Replaced old block mapping from {} to {} with {}", flattened, old, output);
		}
	}

	public static void registerOxidizableBlocks() {
		BuiltInRegistries.BLOCK.forEach(block -> {
			if (ModRegistryArgs.REGISTRY_ARGS.containsKey(block)) {
				RegistryArgs registryArgs = ModRegistryArgs.REGISTRY_ARGS.get(block);

				if (BuiltInRegistries.BLOCK.getKey(block).getNamespace().equals(VanillaLayerPlus.MOD_ID) && registryArgs.oxidizables != null) {
					OxidizableBlocksRegistry.registerNextStage(block, registryArgs.exposed);
					OxidizableBlocksRegistry.registerNextStage(registryArgs.exposed, registryArgs.weathered);
					OxidizableBlocksRegistry.registerNextStage(registryArgs.weathered, registryArgs.oxidized);

					OxidizableBlocksRegistry.registerWaxable(block, registryArgs.waxed);
					OxidizableBlocksRegistry.registerWaxable(registryArgs.exposed, registryArgs.exposedWaxed);
					OxidizableBlocksRegistry.registerWaxable(registryArgs.weathered, registryArgs.weatheredWaxed);
					OxidizableBlocksRegistry.registerWaxable(registryArgs.oxidized, registryArgs.oxidizedWaxed);
				}
			}
		});
	}

	// A layer burns 1/8 as long as its parent block. Fuel is the COOKING_FUEL item
	// component in 26.3, whose burn time points at a context_int_provider entry. Each
	// layer points at vanillalayerplus:cooking/layer/<parent entry>, generated by
	// ModFuelProvider as "parent entry / 8", so blast furnaces and smokers (which
	// halve the parent value) and data packs that retune vanilla fuel stay in ratio.
	public static ResourceKey<ContextIntProvider> layerBurnTime(ResourceKey<ContextIntProvider> parentBurnTime) {
		return ResourceKey.create(Registries.CONTEXT_INT_PROVIDER,
				Identifier.fromNamespaceAndPath(VanillaLayerPlus.MOD_ID, "cooking/layer/" + parentBurnTime.identifier().getPath().replace("cooking/", "")));
	}

	public static void registerFuel() {
		DefaultItemComponentEvents.MODIFY.register(context -> ModRegistryArgs.REGISTRY_ARGS.forEach((block, args) -> {
			if (args.fuel != null) {
				context.modify(block.asItem(), builder -> builder.set(DataComponents.COOKING_FUEL,
						new CookingFuel(layerBurnTime(args.fuel), ContextFloatProviders.COOKING_DEFAULT_SPEED_MULTIPLIER)));
			}
		}));
	}
}
