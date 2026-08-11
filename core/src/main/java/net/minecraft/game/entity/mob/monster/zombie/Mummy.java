package net.minecraft.game.entity.mob.monster.zombie;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.ai.hostile.Attack;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class Mummy extends Zombie {
    public Mummy(World world) {
        super(world);
        this.texture = "/mob/zombie/mummy.png";
        this.vampiric = false;
        this.clearAttributes();
        this.behaviors.addTask(4, new Attack(this, this.moveSpeed, false, EffectList.wither.id, 100, 0, true));
    }

    @Override
    protected void equipRandomArmor(World world) {
    }

    @Override
    public int getSpawnID() {
        return 45;
    }

    @Override
    public float getPathWeight(int xCoord, int yCoord, int zCoord) {
        return this.world.getBlockId(xCoord, yCoord - 1, zCoord) != BlockList.sand.id
                && this.world.getBlockId(xCoord, yCoord - 1, zCoord) != BlockList.sandStone.id
            ? 0.0F
            : 10.0F;
    }

    @Override
    public boolean canSpawnHere() {
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.boundingBox.minY);
        int z = MathHelper.floor_double(this.posZ);
        return this.world.isClear(this.boundingBox)
            && this.world.getBoundingBoxes(this, this.boundingBox).size() == 0
            && !this.world.containsLiquid(this.boundingBox)
            && this.getPathWeight(x, y, z) >= 0.0F;
    }
}
