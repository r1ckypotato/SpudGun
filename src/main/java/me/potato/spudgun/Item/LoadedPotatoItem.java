package me.potato.spudgun.Item;

import net.minecraft.block.BlockState;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

public class LoadedPotatoItem extends Item {
    public LoadedPotatoItem() {
        super(new Item.Settings().maxCount(64).rarity(Rarity.UNCOMMON).food(new FoodComponent.Builder().alwaysEdible().nutrition(6).saturationModifier(0.6F).build()));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        world.createExplosion(user, world.getDamageSources().explosion(user, user), new ExplosionBehavior() {
            @Override
            public boolean canDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power) {
                return false;
            }
        }, user.getPos(), 4, false, World.ExplosionSourceType.MOB);
        return super.finishUsing(stack, world, user);
    }
}
