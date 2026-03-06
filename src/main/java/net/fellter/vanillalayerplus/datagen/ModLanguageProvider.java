package net.fellter.vanillalayerplus.datagen;

import net.fellter.vanillalayerplus.VanillaLayerPlus;
import net.fellter.vanillalayerplus.registry.Args;

import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.loader.impl.util.StringUtil;

public class ModLanguageProvider extends FabricLanguageProvider {
	public ModLanguageProvider(FabricDataOutput dataOutput) {
		super(dataOutput, "en_us");
	}

	@Override
	public void generateTranslations(TranslationBuilder translationBuilder) {
		Registries.BLOCK.stream().filter(VanillaLayerPlus::isNamespaced).forEach(block -> {
			if (Args.DATAGEN_ARGS.containsKey(block)) {
				Identifier identifier = Registries.BLOCK.getId(block);
				String[] var10000 = identifier.getPath().split("_");
				StringBuilder stringBuilder = new StringBuilder();
				String var10001;

				for (String string : var10000) {
					stringBuilder.append(StringUtil.capitalize(string)).append(" ");
				}

				var10001 = stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length()).toString();
				translationBuilder.add(block, var10001);
			}
		});

		translationBuilder.add(RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier(VanillaLayerPlus.MOD_ID, "vlp")), "Vanilla+ Layers");
	}
}
