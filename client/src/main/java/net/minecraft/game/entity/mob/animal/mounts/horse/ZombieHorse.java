package net.minecraft.game.entity.mob.animal.mounts.horse;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;

public class ZombieHorse extends SkeletonHorse {
    public ZombieHorse(World world) {
        super(world);
        this.tamedItem = BlockList.mushroomCrimson.id;
    }

    @Override
    public void setType() {
        this.texture = "/mob/horse/horse_zombie.png";
    }

    @Override
    public int getSpawnID() {
        return 40;
    }

    @Override
    public int getDrop() {
        return ItemList.rottenFlesh.id;
    }
}
