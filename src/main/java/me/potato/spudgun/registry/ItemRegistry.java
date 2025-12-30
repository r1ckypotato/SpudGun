package me.potato.spudgun.registry;

import me.potato.spudgun.Item.LoadedPotatoItem;
import me.potato.spudgun.Item.SpudGunItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.function.Function;

public class ItemRegistry {

    public static final SpudGunItem SPUD_GUN_ITEM = register("spud_gun", new SpudGunItem.Builder()
            .cooldown(25)
            .speed(1)
            .build(),
            new Item.Settings().maxCount(1).rarity(Rarity.RARE)
    );
    public static final SpudGunItem SPUD_SNIPER_ITEM = register("spud_sniper", new SpudGunItem.Builder()
            .cooldown(50)
            .speed(1.75F)
            .build(),
            new Item.Settings().maxCount(1).rarity(Rarity.RARE)
    );
    public static final LoadedPotatoItem LOADED_POTATO_ITEM = register("loaded_potato", LoadedPotatoItem::new, new Item.Settings().maxCount(64).rarity(Rarity.UNCOMMON).food(new FoodComponent.Builder().alwaysEdible().nutrition(6).saturationModifier(0.6F).build()));
    public static final SpudGunItem SPUD_MACHINE_GUN_ITEM = register("spud_machine_gun", new SpudGunItem.Builder()
            .cooldown(4)
            .speed(1)
            .build(),
            new Item.Settings().maxCount(1).rarity(Rarity.EPIC)
    );

    public static void init() {}

    @Environment(EnvType.CLIENT)
    public static void initClient() {
        registerClient(SPUD_GUN_ITEM, new ItemGroupData(ItemGroups.TOOLS), new ItemGroupData(ItemGroups.COMBAT));
        registerClient(SPUD_SNIPER_ITEM, new ItemGroupData(ItemGroups.TOOLS), new ItemGroupData(ItemGroups.COMBAT));
        registerClient(LOADED_POTATO_ITEM, new ItemGroupData(ItemGroups.COMBAT), new ItemGroupData(ItemGroups.FOOD_AND_DRINK));
        registerClient(SPUD_MACHINE_GUN_ITEM, new ItemGroupData(ItemGroups.TOOLS), new ItemGroupData(ItemGroups.COMBAT));    }

    private static <T extends Item> T register(String id, Function<Item.Settings, T> factory, Item.Settings settings) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of("spud_gun", id));
        return Registry.register(Registries.ITEM, key, factory.apply(settings.registryKey(key)));
    }

    @Environment(EnvType.CLIENT)
    private static void registerClient(Item item, ItemGroupData... groups) {
        for(ItemGroupData group : groups) {
            if(group.after() != null) {
                ItemGroupEvents.modifyEntriesEvent(group.group()).register(
                        group.isOperatorOnly() ?
                                entries -> { if(MinecraftClient.getInstance().options.getOperatorItemsTab().getValue()) entries.addAfter(group.after(), item); } :
                                entries -> entries.addAfter(group.after(), item)
                );
            } else {
                ItemGroupEvents.modifyEntriesEvent(group.group()).register(
                        group.isOperatorOnly() ?
                                entries -> { if(MinecraftClient.getInstance().options.getOperatorItemsTab().getValue()) entries.add(item); } :
                                entries -> entries.add(item)
                );
            }
        }
    }

    private static class ItemGroupData {

        private ItemConvertible after = null;
        private final RegistryKey<ItemGroup> group;
        private boolean operatorOnly = false;

        public ItemGroupData(RegistryKey<ItemGroup> group) {
            this.group = group;
        }

        public ItemGroupData after(ItemConvertible after) {
            this.after = after;
            return this;
        }

        public ItemGroupData operatorOnly() {
            this.operatorOnly = true;
            return this;
        }

        public ItemConvertible after() {
            return after;
        }

        public RegistryKey<ItemGroup> group() {
            return group;
        }

        public boolean isOperatorOnly() {
            return operatorOnly;
        }
    }
}