package me.potato.spudgun.client;

import me.potato.spudgun.registry.ItemRegistry;
import net.fabricmc.api.ClientModInitializer;

public class SpudGunClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ItemRegistry.initClient();
    }
}
