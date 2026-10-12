package net.fellter.vanillalayerplus;

import net.fellter.vanillalayerplus.registry.ModClientRegistries;

import net.fabricmc.api.ClientModInitializer;

public class VanillaLayerPlusClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModClientRegistries.registerFoliage();
		ModClientRegistries.registerTranslucentBlocks();
		ModClientRegistries.registerTransparentBlocks();
	}
}
