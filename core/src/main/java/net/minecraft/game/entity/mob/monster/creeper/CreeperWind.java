package net.minecraft.game.entity.mob.monster.creeper;

import net.minecraft.game.world.World;

public class CreeperWind extends Creeper {
    public CreeperWind(World world) {
        super(world);
        this.texture = "/mob/creeper/creeper_yellow.png";
    }

    @Override
    protected void explode() {
        this.world.createWindExplosion(this, this.posX, this.posY, this.posZ, 100.0F);
    }

    @Override
    public void fall(float dist) {
    }
}
