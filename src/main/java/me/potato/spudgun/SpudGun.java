package me.potato.spudgun;

import me.potato.spudgun.registry.ItemRegistry;
import net.fabricmc.api.ModInitializer;

public class SpudGun implements ModInitializer {
    @Override
    public void onInitialize() {
        ItemRegistry.init();
    }
}
