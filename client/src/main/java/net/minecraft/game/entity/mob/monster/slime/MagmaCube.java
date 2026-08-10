package net.minecraft.game.entity.mob.monster.slime;

import net.minecraft.game.ParticleType;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.CreatureType;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.World;

public class MagmaCube extends Slime {
    public MagmaCube(World world) {
        super(world);
        this.particle = ParticleType.FLAME;
        this.canGlow = true;
    }

    @Override
    public void setType() {
        this.texture = "/mob/slime/slime_magma.png";
    }

    @Override
    public CreatureType getCreatureType() {
        return CreatureType.FLAME;
    }

    @Override
    public int getSpawnID() {
        return 57;
    }

    @Override
    public boolean canBurn() {
        return false;
    }

    @Override
    public boolean canSpawnHere() {
        return this.world.settings.getDifficulty() > 0
            && this.world.isClear(this.boundingBox)
            && this.world.getBoundingBoxes(this, this.boundingBox).size() == 0
            && !this.world.containsLiquid(this.boundingBox);
    }

    @Override
    public void setDead() {
        int size = this.getSlimeSize();
        if (!this.world.isOnline && size > 1 && this.health <= 0) {
            for (int count = 0; count < 4; count++) {
                float offsetX = (count % 2 - 0.5F) * size / 4.0F;
                float offsetZ = (count / 2 - 0.5F) * size / 4.0F;
                MagmaCube magmaCube = new MagmaCube(this.world);
                magmaCube.setSlimeSize(size / 2);
                magmaCube.teleportTo(this.posX + offsetX, this.posY + 0.5 + this.getEyeHeight(), this.posZ + offsetZ, this.rand.nextFloat() * 360.0F, 0.0F);
                this.world.spawnEntity(magmaCube);
            }
        }

        this.isDead = true;
    }

    @Override
    public void onCollideWith(Entity target) {
        if (target instanceof Player) {
            int scale = this.getSlimeSize();
            if (scale > 1 && this.getDistanceSqTo(target) < 1.0 * scale && target.damageEntity(DamageSource.mobDamage(this), scale)) {
                this.world.playEntitySound(this, "mob.slimeattack", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                target.fire = 100;
            }
        }
    }

    @Override
    public int getDrop() {
        return this.getSlimeSize() == 1 ? BlockList.magma.id : 0;
    }

    @Override
    public void jump() {
        this.motionY = 0.42F + this.getSlimeSize() * 0.1F;
    }

    @Override
    public final boolean isInLava() {
        return this.getSlimeSize() == 1 ? this.world.handleWaterMovement(this.boundingBox, MaterialList.water, this) : super.isInLava();
    }
}
