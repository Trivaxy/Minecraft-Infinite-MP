package net.minecraft.game.entity.mob.monster.creeper;

import net.minecraft.game.world.World;

public class CreeperFire extends Creeper {
    public CreeperFire(World world) {
        super(world);
        this.texture = "/mob/creeper/creeper_red.png";
    }

    @Override
    protected void explode() {
        this.world.createFireExplosion(this, this.posX, this.posY, this.posZ, 3.0F);
    }

    @Override
    public boolean canBurn() {
        return false;
    }
}
