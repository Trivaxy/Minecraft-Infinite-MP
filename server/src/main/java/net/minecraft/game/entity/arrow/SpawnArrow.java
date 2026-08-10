package net.minecraft.game.entity.arrow;

import net.minecraft.game.entity.EntityManager;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.world.World;

public class SpawnArrow extends Arrow {
    public SpawnArrow(World world) {
        super(world);
    }

    public SpawnArrow(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public SpawnArrow(World world, Mob mob, float tickDelta) {
        super(world, mob, tickDelta);
    }

    @Override
    protected void onBlockHit() {
        try {
            Mob entity = (Mob)EntityManager.createEntityFromId(this.rand.nextInt(100) + 100, this.world);
            entity.setPosAndRot(this.xTile, this.yTile + 1, this.zTile, this.rand.nextFloat() * 360.0F, 0.0F);
            this.world.spawnEntity(entity);
            this.setDead();
        } catch (Exception ex) {
            this.onBlockHit();
        }
    }
}
