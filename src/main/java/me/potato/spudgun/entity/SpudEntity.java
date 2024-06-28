package me.potato.spudgun.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

import java.util.function.BiConsumer;

public class SpudEntity extends SnowballEntity {

    private final int damage;
    private final BiConsumer<SpudEntity, HitResult> onCollide;

    public SpudEntity(World world, LivingEntity owner, int damage, BiConsumer<SpudEntity, HitResult> onCollide) {
        super(world, owner);
        this.damage = damage;
        this.onCollide = onCollide;
    }

    @Override
    protected Item getDefaultItem() {
        return Items.SNOWBALL;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        entity.damage(entity.getWorld().getDamageSources().thrown(this, this.getOwner()), (float)damage * (float)Math.sqrt(getVelocity().length()));
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        onCollide.accept(this, hitResult);
        super.onCollision(hitResult);
    }
}