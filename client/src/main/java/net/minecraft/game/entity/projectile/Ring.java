package net.minecraft.game.entity.projectile;

import java.util.List;
import net.minecraft.game.ParticleType;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.physics.TargetingSystem;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class Ring extends Projectile {
    protected Entity attackTarget;
    private double waypointX;
    private double waypointY;
    private double waypointZ;

    public Ring(World world) {
        super(world);
        this.itemAssigned = ItemList.raphaelRing;
        this.gravityForce = 0.0;
    }

    public Ring(World world, Mob thrower) {
        super(world, thrower, 8);
        this.itemAssigned = ItemList.raphaelRing;
        this.gravityForce = 0.0;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.attackTarget == null) {
            this.attackTarget = this.findNearestMob();
        } else if (!this.attackTarget.isAlive()) {
            this.attackTarget = null;
        }

        double targetX = this.waypointX - this.posX;
        double targetY = this.waypointY - this.posY;
        double targetZ = this.waypointZ - this.posZ;
        float dist = MathHelper.sqrt_double(targetX * targetX + targetY * targetY + targetZ * targetZ);
        float motion = 0.99F;
        if (this.attackTarget != null) {
            this.waypointX = this.attackTarget.posX;
            this.waypointY = this.attackTarget.posY;
            this.waypointZ = this.attackTarget.posZ;
            motion = 0.75F;
        }

        if (this.isCourseTraversable(dist)) {
            this.motionX += targetX / dist * 0.2;
            this.motionY += targetY / dist * 0.2;
            this.motionZ += targetZ / dist * 0.2;
        }

        this.motionX *= motion;
        this.motionY *= motion;
        this.motionZ *= motion;
        this.setPosition(this.posX, this.posY, this.posZ);
    }

    private boolean isCourseTraversable(double dist) {
        double x = (this.waypointX - this.posX) / dist;
        double y = (this.waypointY - this.posY) / dist;
        double z = (this.waypointZ - this.posZ) / dist;
        BoundingBox boundingBox = this.boundingBox.copy();
        if (dist > 256.0) {
            dist = 256.0;
        }

        for (int i = 1; i < dist; i++) {
            boundingBox.offset(x, y, z);
            if (this.world.getBoundingBoxes(this, boundingBox).size() > 0) {
                return false;
            }
        }

        return true;
    }

    protected Entity findNearestMob() {
        List<Entity> entities = this.world.getEntities(this, this.boundingBox.expand(16.0, 8.0, 16.0));
        if (!entities.isEmpty()) {
            Entity target = entities.get(this.world.rand.nextInt(entities.size()));
            if (!(target instanceof Player) && target instanceof Mob && this.canReachTarget(target)) {
                return target;
            }
        }

        return null;
    }

    private boolean canReachTarget(Entity entity) {
        return this.world
                .rayTraceBlocks(
                    Vector.createVector(this.posX, this.posY + this.getEyeHeight(), this.posZ),
                    Vector.createVector(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ)
                )
            == null;
    }

    @Override
    protected void onImpact(TargetingSystem mop) {
        if (mop.entityHit != null) {
            mop.entityHit.damageEntity(DamageSource.projectileDamage(this, this.thrower), this.damage);
        }

        for (int i = 0; i < 8; i++) {
            this.world.spawnParticle(ParticleType.EXPLODE, this.posX, this.posY, this.posZ, 0.0, 0.0, 0.0);
        }

        this.setDead();
    }
}
