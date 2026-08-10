package net.minecraft.game.entity.arrow;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.world.World;

public class RopeArrow extends Arrow {
    public RopeArrow(World world) {
        super(world);
    }

    public RopeArrow(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public RopeArrow(World world, Mob mob, float tickDelta) {
        super(world, mob, tickDelta);
    }

    @Override
    protected void onBlockHit() {
        for (int i = 1; i <= 8 && this.world.getBlockId(this.xTile, this.yTile - i, this.zTile) == 0; i++) {
            this.world.setBlockWithNotify(this.xTile, this.yTile - i, this.zTile, BlockList.rope.id);
        }
    }
}
