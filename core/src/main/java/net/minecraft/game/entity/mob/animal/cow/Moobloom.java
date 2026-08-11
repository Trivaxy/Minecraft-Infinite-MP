package net.minecraft.game.entity.mob.animal.cow;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;

public class Moobloom extends Cow {
    public Moobloom(World world) {
        super(world);
        this.type = (byte)this.rand.nextInt(5);
        this.setType();
    }

    @Override
    public void setType() {
        switch (this.type) {
            case 1:
                this.texture = "/mob/cow/flowercow_blue.png";
                break;
            case 2:
                this.texture = "/mob/cow/redcow.png";
                break;
            case 3:
                this.texture = "/mob/cow/flowercow_purple.png";
                break;
            default:
                this.texture = "/mob/cow/flowercow.png";
        }
    }

    @Override
    public void setTypeOnSpawn() {
    }

    @Override
    public int getSpawnID() {
        return 36;
    }

    @Override
    public int getShearedDrop() {
        switch (this.type) {
            case 1:
                return BlockList.lilac.id;
            case 2:
                return BlockList.rose.id;
            case 3:
                return BlockList.petunia.id;
            default:
                return BlockList.dandelion.id;
        }
    }
}
