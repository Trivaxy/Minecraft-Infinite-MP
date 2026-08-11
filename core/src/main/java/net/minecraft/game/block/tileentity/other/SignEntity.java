package net.minecraft.game.block.tileentity.other;

import com.mojang.nbt.tag.special.CompoundTag;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.container.SignPacket;

public class SignEntity extends TileEntity {
    public String[] signText = new String[]{"", "", "", ""};
    public int activeLine = -1;
    public int textColor = 0;
    public boolean glowing = false;
    private boolean editable = true;

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.setString("Text1", this.signText[0]);
        tag.setString("Text2", this.signText[1]);
        tag.setString("Text3", this.signText[2]);
        tag.setString("Text4", this.signText[3]);
        tag.setInteger("TextColor", this.textColor);
        tag.setBoolean("Glowing", this.glowing);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);

        for (int line = 0; line < 4; line++) {
            this.signText[line] = tag.getString("Text" + (line + 1));
            if (this.signText[line].length() > 15) {
                this.signText[line] = this.signText[line].substring(0, 15);
            }
        }

        this.textColor = tag.getInteger("TextColor");
        this.glowing = tag.getBoolean("Glowing");
    }

    public void setEditable(boolean e) {
        this.editable = e;
    }

    @Override
    public Packet getPacket() {
        String[] strings = new String[4];

        for (int i = 0; i < 4; i++) {
            strings[i] = this.signText[i];
        }

        return new SignPacket(this.x, this.y, this.z, strings);
    }

    public boolean isEditable() {
        return this.editable;
    }

    @Override
    public Block getBlockType() {
        return BlockList.blocks[this.world.getBlockId(this.x, this.y, this.z)];
    }
}
