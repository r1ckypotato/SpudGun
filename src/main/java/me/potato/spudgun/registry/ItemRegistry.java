package me.potato.spudgun.registry;

import me.potato.spudgun.Item.LoadedPotatoItem;
import me.potato.spudgun.Item.SpudGunItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ItemRegistry {

    public static final SpudGunItem SPUD_GUN_ITEM = new SpudGunItem(new Item.Settings().maxCount(1).rarity(Rarity.RARE), 25, 1);
    public static final SpudGunItem SPUD_SNIPER_ITEM = new SpudGunItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC), 50, 1.75F);
    public static final LoadedPotatoItem LOADED_POTATO_ITEM = new LoadedPotatoItem();

    public static void init() {
        register("spud_gun", SPUD_GUN_ITEM, new ItemGroupData(ItemGroups.TOOLS), new ItemGroupData(ItemGroups.COMBAT));
        register("spud_sniper", SPUD_SNIPER_ITEM, new ItemGroupData(ItemGroups.TOOLS), new ItemGroupData(ItemGroups.COMBAT));
        register("loaded_potato", LOADED_POTATO_ITEM, new ItemGroupData(ItemGroups.COMBAT), new ItemGroupData(ItemGroups.FOOD_AND_DRINK));
    }

    private static void register(String id, Item item, ItemGroupData... groups) {
        Registry.register(Registries.ITEM, Identifier.of("spud_gun", id), item);
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