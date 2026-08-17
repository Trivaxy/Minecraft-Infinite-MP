package net.minecraft.game.block.machines;

import java.util.Random;
import net.minecraft.commands.Commands;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.block.tileentity.other.CommandBlockEntity;
import net.minecraft.game.block.utility.ContainerBlock;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class CommandBlock extends ContainerBlock {
    public CommandBlock(int id, Material mat) {
        super(id, mat);
    }

    @Override
    public void onTick(World world, int x, int y, int z, Random random) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof CommandBlockEntity) || !world.hasNeighborSignal(x, y, z)) {
            return;
        }

        CommandBlockEntity block = (CommandBlockEntity)tile;
        String command = block.command.startsWith("/") ? block.command.substring(1) : block.command;
        Commands commands = Commands.active();
        if (commands != null && !command.trim().isEmpty()) {
            commands.perform(command, block.createCommandSource());
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
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("machines/" + this.name);
    }
}
