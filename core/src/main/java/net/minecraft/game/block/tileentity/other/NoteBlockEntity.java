package net.minecraft.game.block.tileentity.other;

import com.mojang.nbt.tag.special.CompoundTag;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.world.World;

public class NoteBlockEntity extends TileEntity {
    public byte note = 0;
    public boolean playedSound = false;

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.setByte("note", this.note);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        this.note = tag.getByte("note");
        if (this.note < 0) {
            this.note = 0;
        }

        if (this.note > 24) {
            this.note = 24;
        }
    }

    public void changePitch() {
        this.note = (byte)((this.note + 1) % 25);
        this.onChanged();
    }

    public void triggerNote(World world, int x, int y, int z) {
        if (world.getBlockMaterial(x, y + 1, z) == MaterialList.air) {
            Material mat = world.getBlockMaterial(x, y - 1, z);
            byte snd = 0;
            if (mat == MaterialList.rock) {
                snd = 1;
            }

            if (mat == MaterialList.sand) {
                snd = 2;
            }

            if (mat == MaterialList.glass) {
                snd = 3;
            }

            if (mat == MaterialList.wood) {
                snd = 4;
            }

            if (mat == MaterialList.metal) {
                snd = 5;
            }

            if (mat == MaterialList.cloth) {
                snd = 6;
            }

            if (mat == MaterialList.ice) {
                snd = 7;
            }

            if (mat == MaterialList.pumpkin) {
                snd = 8;
            }

            if (mat == MaterialList.bone) {
                snd = 9;
            }

            world.blockEvent(x, y, z, snd, this.note);
        }
    }
}
