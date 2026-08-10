package net.minecraft.game.block.machines;

import java.util.Random;
import net.minecraft.commands.Command;
import net.minecraft.commands.CommandHolder;
import net.minecraft.commands.CommandSource;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.block.tileentity.other.CommandBlockEntity;
import net.minecraft.game.block.utility.ContainerBlock;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class CommandBlock extends ContainerBlock implements CommandSource {
    public CommandBlock(int id, Material mat) {
        super(id, mat);
    }

    @Override
    public void onTick(World world, int x, int y, int z, Random random) {
        try {
            String command = ((CommandBlockEntity)world.getTileEntity(x, y, z)).command.substring(1);
            if (world.getTileEntity(x, y, z) != null && world.hasNeighborSignal(x, y, z) && !command.isEmpty()) {
                CommandBlockEntity.CommandsManagerCore.instance
                    .runCommand(new CommandHolder(command.trim(), this), (CommandBlockEntity)world.getTileEntity(x, y, z));
            }
        } catch (Exception var7) {
        }
    }

    @Override
    public void onNeighborChange(World world, int x, int y, int z, int id) {
        if (!world.isOnline) {
            world.forceTick(x, y, z, this.id);
        }
    }

    @Override
    public int tickRate() {
        return 1;
    }

    @Override
    public final boolean blockActivated(World world, int x, int y, int z, Player player) {
        player.displayGUIEditCommand((CommandBlockEntity)world.getTileEntity(x, y, z));
        return true;
    }

    @Override
    public TileEntity getBlockEntity() {
        return new CommandBlockEntity();
    }

    @Override
    public String getUsername() {
        return "CommandBlock";
    }

    @Override
    public void print(String text) {
    }

    @Override
    public Command.UserType getType() {
        return Command.UserType.OPERATOR;
    }

    @Override
    public boolean canRunCommand(Command c) {
        return true;
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("machines/" + this.name);
    }
}
