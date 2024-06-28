package me.potato.spudgun.Item;

import me.potato.spudgun.entity.SpudEntity;
import me.potato.spudgun.registry.ItemRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.minecraft.world.explosion.ExplosionBehavior;

import java.util.function.BiConsumer;

public class SpudGunItem extends Item {

    private final int cooldown;
    private final float speedMultiplier;

    public SpudGunItem(Settings settings, int cooldown, float speedMultiplier) {
        super(settings);
        this.cooldown = cooldown;
        this.speedMultiplier = speedMultiplier;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = getSpuds(user);
        if (itemStack == null && !user.getAbilities().creativeMode) {
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
            return TypedActionResult.pass(user.getStackInHand(hand));
        }
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.BLOCK_DISPENSER_LAUNCH, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
        user.getItemCooldownManager().set(this, cooldown);
        if (!world.isClient) {
            ItemStack spudDisplay;
            BiConsumer<SpudEntity, HitResult> onCollide = (e, h) -> {};
            float speed = 4;
            if(itemStack == null && user.getAbilities().creativeMode) {
                spudDisplay = new ItemStack(Items.POTATO);
            } else {
                spudDisplay = itemStack;
                if (itemStack.getItem().equals(Items.POISONOUS_POTATO)) {
                    speed = 0.2F;
                } else if (itemStack.getItem().equals(Items.BAKED_POTATO)) {
                    onCollide = (e, hitResult) -> {
                        if(hitResult.getType() == HitResult.Type.ENTITY) {
                            ((EntityHitResult)hitResult).getEntity().setOnFireFor(2);
                        }
                    };
                } else if (itemStack.getItem().equals(ItemRegistry.LOADED_POTATO_ITEM)) {
                    onCollide = (e, hitResult) -> e.getWorld().createExplosion(e, e.getDamageSources().explosion(e, e.getOwner()), new ExplosionBehavior(), e.getPos(), 6, false, World.ExplosionSourceType.MOB);
                }
            }
            SpudEntity spud = new SpudEntity(world, user, 12, onCollide);
            spud.setItem(spudDisplay);
            spud.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, speed * speedMultiplier, 0.15F);
            world.spawnEntity(spud);
        }

        user.incrementStat(Stats.USED.getOrCreateStat(this));
        if (!user.getAbilities().creativeMode) {
            itemStack.decrement(1);
        }

        return TypedActionResult.success(user.getStackInHand(hand), world.isClient());
    }

    private ItemStack getSpuds(PlayerEntity user) {
        ItemStack spuds = null;
        for (int i = 0; i < user.getInventory().size(); i++) {
            if (user.getInventory().getStack(i).getItem() == Items.POTATO
                    || user.getInventory().getStack(i).getItem() == Items.POISONOUS_POTATO
                    || user.getInventory().getStack(i).getItem() == Items.BAKED_POTATO
                    || user.getInventory().getStack(i).getItem() == ItemRegistry.LOADED_POTATO_ITEM
            ) {
                spuds = user.getInventory().getStack(i);
                break;
            }
        }
        return spuds;
    }
}
