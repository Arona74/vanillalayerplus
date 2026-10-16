package net.fellter.vanillalayerplus.datagen;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import net.fellter.vanillalayerplus.VanillaLayerPlus;
import net.fellter.vanillalayerplus.block.LayerBlock;
import net.fellter.vanillalayerplus.registry.Args;
import net.fellter.vanillalayerplus.registry.DatagenArgs;
import net.fellter.vanillalayerplus.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;

public class ModBlockTagProvider extends FabricTagsProvider.BlockTagsProvider {
	// Mining tier tags a layer inherits from its parent block, so e.g. an iron ore layer
	// only drops when mined with a stone pickaxe or better, like iron ore itself.
	private static final List<TagKey<Block>> TOOL_TIER_TAGS = List.of(BlockTags.NEEDS_STONE_TOOL, BlockTags.NEEDS_IRON_TOOL, BlockTags.NEEDS_DIAMOND_TOOL);

	public ModBlockTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider wrapperLookup) {
		List<Set<Identifier>> tierMembers = TOOL_TIER_TAGS.stream().map(tag -> readVanillaBlockTag(tag.location())).toList();

		BuiltInRegistries.BLOCK.stream().filter(VanillaLayerPlus::isNamespaced).forEach(block -> {
			Identifier identifier = BuiltInRegistries.BLOCK.getKey(block);
			if (block instanceof LayerBlock && Args.DATAGEN_ARGS.containsKey(block)) {
				getOrCreateRawBuilder(ModTags.LAYERS).addElement(identifier);
				DatagenArgs args = Args.DATAGEN_ARGS.get(block);

				for (TagKey<Block> blockTagKey : args.blockTags) {
					getOrCreateRawBuilder(blockTagKey).addElement(identifier);
				}

				if (args.parentBlock != null) {
					Identifier parent = BuiltInRegistries.BLOCK.getKey(args.parentBlock);

					for (int i = 0; i < TOOL_TIER_TAGS.size(); i++) {
						if (tierMembers.get(i).contains(parent)) {
							getOrCreateRawBuilder(TOOL_TIER_TAGS.get(i)).addElement(identifier);
						}
					}
				}
			}
		});
	}

	// Vanilla tags are not bound during datagen, so read the tag file shipped in the
	// Minecraft jar instead, following "#tag" references.
	private static Set<Identifier> readVanillaBlockTag(Identifier tag) {
		Set<Identifier> blocks = new HashSet<>();
		String path = "data/" + tag.getNamespace() + "/tags/block/" + tag.getPath() + ".json";

		try (InputStream stream = Block.class.getClassLoader().getResourceAsStream(path)) {
			if (stream == null) {
				throw new IllegalStateException("Vanilla block tag not found: " + path);
			}

			for (JsonElement value : JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject().getAsJsonArray("values")) {
				String entry = value.isJsonObject() ? value.getAsJsonObject().get("id").getAsString() : value.getAsString();

				if (entry.startsWith("#")) {
					blocks.addAll(readVanillaBlockTag(Identifier.parse(entry.substring(1))));
				} else {
					blocks.add(Identifier.parse(entry));
				}
			}
		} catch (IOException e) {
			throw new IllegalStateException("Could not read vanilla block tag " + path, e);
		}

		return blocks;
	}
}
