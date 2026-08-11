package net.minecraft.game.entity.projectile;

import net.minecraft.game.ParticleType;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.physics.TargetingSystem;
import net.minecraft.game.world.World;

public class Rock extends Projectile {
    public Rock(World world) {
        super(world);
        this.itemAssigned = ItemList.rock;
    }

    public Rock(World world, Mob thrower, int dmg) {
        super(world, thrower, dmg);
        this.itemAssigned = ItemList.rock;
    }

    public Rock(World world, double x, double y, double z) {
        super(world, x, y, z);
        this.itemAssigned = ItemList.rock;
    }

    @Override
    protected void onImpact(TargetingSystem mop) {
        if (mop.entityHit != null) {
            mop.entityHit.damageEntity(DamageSource.projectileDamage(this, this.thrower), this.damage);
        } else if (this.thrower instanceof Player) {
            float offsetX = this.world.rand.nextFloat() * 0.7F + 0.15F;
            float offsetY = this.world.rand.nextFloat() * 0.7F + 0.15F;
            float offsetZ = this.world.rand.nextFloat() * 0.7F + 0.15F;
            DroppedItem drop = new DroppedItem(
                this.world, (float)this.posX + offsetX, (float)this.posY + offsetY, (float)this.posZ + offsetZ, new ItemStack(ItemList.rock.id, 1)
            );
            this.world.spawnEntity(drop);
        }

        for (int i = 0; i < 8; i++) {
            this.world.spawnParticle(ParticleType.PEBBLEPOOF, this.posX, this.posY, this.posZ, 0.0, 0.0, 0.0);
        }

        this.setDead();
    }
}
