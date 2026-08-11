package net.minecraft.game.entity.mob.animal;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.CreatureType;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public abstract class Animal extends Mob {
    public Animal(World world) {
        super(world);
        this.setHealth(10);
        this.scoreValue = 10;
    }

    @Override
    public CreatureType getCreatureType() {
        return CreatureType.ANIMAL;
    }

    @Override
    public float getPathWeight(int xCoord, int yCoord, int zCoord) {
        return this.world.getBlockId(xCoord, yCoord - 1, zCoord) == BlockList.grass.id ? 10.0F : this.world.getBrightness(xCoord, yCoord, zCoord) - 0.5F;
    }

    @Override
    public boolean canSpawnHere() {
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.boundingBox.minY);
        int z = MathHelper.floor_double(this.posZ);
        return this.world.getBlockId(x, y - 1, z) == BlockList.grass.id && this.world.getBlockBrightness(x, y, z) > 8 && super.canSpawnHere();
    }

    @Override
    public int getSoundInterval() {
        return 120;
    }
}
