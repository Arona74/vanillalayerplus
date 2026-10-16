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

import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
	// Mining tier tags a layer inherits from its parent block, so e.g. an iron ore layer
	// only drops when mined with a stone pickaxe or better, like iron ore itself.
	private static final List<TagKey<Block>> TOOL_TIER_TAGS = List.of(BlockTags.NEEDS_STONE_TOOL, BlockTags.NEEDS_IRON_TOOL, BlockTags.NEEDS_DIAMOND_TOOL);

	public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
		List<Set<Identifier>> tierMembers = TOOL_TIER_TAGS.stream().map(tag -> readVanillaBlockTag(tag.id())).toList();

		Registries.BLOCK.stream().filter(VanillaLayerPlus::isNamespaced).forEach(block -> {
			Identifier identifier = Registries.BLOCK.getId(block);
			if (block instanceof LayerBlock && Args.DATAGEN_ARGS.containsKey(block)) {
				getTagBuilder(ModTags.LAYERS).add(identifier);
				DatagenArgs args = Args.DATAGEN_ARGS.get(block);

				for (TagKey<Block> blockTagKey : args.blockTags) {
					getTagBuilder(blockTagKey).add(identifier);
				}

				if (args.parentBlock != null) {
					Identifier parent = Registries.BLOCK.getId(args.parentBlock);

					for (int i = 0; i < TOOL_TIER_TAGS.size(); i++) {
						if (tierMembers.get(i).contains(parent)) {
							getTagBuilder(TOOL_TIER_TAGS.get(i)).add(identifier);
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
		String path = "data/" + tag.getNamespace() + "/tags/blocks/" + tag.getPath() + ".json";

		try (InputStream stream = Block.class.getClassLoader().getResourceAsStream(path)) {
			if (stream == null) {
				throw new IllegalStateException("Vanilla block tag not found: " + path);
			}

			for (JsonElement value : JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject().getAsJsonArray("values")) {
				String entry = value.isJsonObject() ? value.getAsJsonObject().get("id").getAsString() : value.getAsString();

				if (entry.startsWith("#")) {
					blocks.addAll(readVanillaBlockTag(new Identifier(entry.substring(1))));
				} else {
					blocks.add(new Identifier(entry));
				}
			}
		} catch (IOException e) {
			throw new IllegalStateException("Could not read vanilla block tag " + path, e);
		}

		return blocks;
	}
}
