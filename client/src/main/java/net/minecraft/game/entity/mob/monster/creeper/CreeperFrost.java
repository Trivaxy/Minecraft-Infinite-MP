package net.minecraft.game.entity.mob.monster.creeper;

import net.minecraft.game.world.World;

public class CreeperFrost extends Creeper {
    public CreeperFrost(World world) {
        super(world);
        this.texture = "/mob/creeper/creeper_blue.png";
    }

    @Override
    protected void explode() {
        this.world.createFrostExplosion(this, this.posX, this.posY, this.posZ, 3.0F);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
}
