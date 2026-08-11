package net.minecraft.game.entity.projectile;

import net.minecraft.game.ParticleType;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.item.staff.GabrielStaff;
import net.minecraft.game.physics.TargetingSystem;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class Orb extends Projectile {
    public Orb(World world) {
        super(world);
    }

    public Orb(World world, Mob thrower, int dmg) {
        super(world, thrower, dmg);
    }

    @Override
    protected void onImpact(TargetingSystem mop) {
        if (this.thrower != null && !this.thrower.sitting && !this.thrower.sleeping) {
            this.thrower
                .setPosition(MathHelper.floor_double(this.posX) + 0.5F, MathHelper.floor_double(this.posY) + 2.0F, MathHelper.floor_double(this.posZ) + 0.5F);
        }

        GabrielStaff.canUseStaff = true;

        for (int i = 0; i < 8; i++) {
            this.world.spawnParticle(ParticleType.EXPLODE, this.posX, this.posY, this.posZ, 0.0, 0.0, 0.0);
        }

        this.setDead();
    }
}
