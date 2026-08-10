package net.minecraft.game.block.tileentity.other;

import com.mojang.nbt.tag.special.CompoundTag;
import net.minecraft.game.block.tileentity.TileEntity;

public class JukeboxEntity extends TileEntity {
    public int record;

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        this.record = tag.getInteger("Record");
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.setString("id", "Jukebox");
        if (this.record > 0) {
            tag.setInteger("Record", this.record);
        }
    }
}
