package net.minecraft.game.block.tileentity.other;

import com.mojang.nbt.tag.special.CompoundTag;
import net.minecraft.commands.CommandOutput;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.physics.Vector;

public class CommandBlockEntity extends TileEntity {
    public String command = "";
    public int lineBeingEdited = -1;
    private boolean editable = true;
    public boolean suppressOutput = false;

    @Override
    public void writeToNBT(CompoundTag c) {
        super.writeToNBT(c);
        c.setString("Command", this.command);
        c.setBoolean("SuppressOutput", this.suppressOutput);
    }

    @Override
    public void readFromNBT(CompoundTag c) {
        super.readFromNBT(c);
        this.command = c.getString("Command");
        this.suppressOutput = c.getBoolean("SuppressOutput");
    }

    public void setEditable(boolean e) {
        this.editable = e;
    }

    public boolean isEditable() {
        return this.editable;
    }

    @Override
    public Block getBlockType() {
        return BlockList.blocks[this.world.getBlockId(this.x, this.y, this.z)];
    }

    public CommandSourceStack createCommandSource() {
        final Player nearest = this.world.getClosestPlayer(this.x, this.y, this.z, 16.0);
        CommandOutput output = CommandOutput.NONE;
        if (!this.suppressOutput && nearest != null) {
            output = new CommandOutput() {
                @Override
                public void sendCommandMessage(String message) {
                    nearest.addChatMessage(message);
                }
            };
        }

        return new CommandSourceStack(
            output,
            "CommandBlock",
            CommandSourceStack.LEVEL_OWNER,
            nearest,
            this.world,
            Vector.createVector(this.x + 0.5, this.y + 0.5, this.z + 0.5),
            0.0F,
            0.0F,
            null,
            !this.world.isOnline
        );
    }
}
