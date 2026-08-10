package net.minecraft.game.entity.mob.animal.cow;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;

public class Mooshroom extends Cow {
    public Mooshroom(World world) {
        super(world);
        this.type = (byte)this.rand.nextInt(5);
        this.setType();
    }

    @Override
    public void setType() {
        if (this.type == 0) {
            this.texture = "/mob/cow/browncow.png";
        } else {
            this.texture = "/mob/cow/redcow.png";
        }
    }

    @Override
    public void setTypeOnSpawn() {
    }

    @Override
    public int getSpawnID() {
        return 35;
    }

    @Override
    public int getShearedDrop() {
        return this.type == 0 ? BlockList.mushroomBrown.id : BlockList.mushroomRed.id;
    }
}
