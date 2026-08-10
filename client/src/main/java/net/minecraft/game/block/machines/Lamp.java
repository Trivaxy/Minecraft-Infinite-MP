package net.minecraft.game.block.machines;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class Lamp extends Block {
    public Lamp(int blockID, Material material) {
        super(blockID, material);
    }

    @Override
    public void onPlaced(World world, Player player, int x, int y, int z, int side) {
        super.onPlaced(world, player, x, y, z, side);

        for (int i = -1; i < 2; i++) {
            Block block = BlockList.blocks[world.getBlockId(x + i, y, z)];
            if (block != null && block instanceof MovingPiston) {
                return;
            }

            block = BlockList.blocks[world.getBlockId(x, y + i, z)];
            if (block != null && block instanceof MovingPiston) {
                return;
            }

            block = BlockList.blocks[world.getBlockId(x, y, z + i)];
            if (block != null && block instanceof MovingPiston) {
                return;
            }
        }

        if (world.hasNeighborSignal(x, y, z)) {
            world.setBlock(x, y, z, BlockList.lampLit.id);
        } else {
            world.setBlock(x, y, z, BlockList.lamp.id);
        }
    }

    @Override
    public void onNeighborChange(World world, int x, int y, int z, int id) {
        if (world.hasNeighborSignal(x, y, z)) {
            world.setBlock(x, y, z, BlockList.lampLit.id);
        } else {
            world.setBlock(x, y, z, BlockList.lamp.id);
        }
    }

    @Override
    public int dropID(int id, Random random) {
        return BlockList.lamp.id;
    }

    @Override
    public int pickID(int id) {
        return BlockList.lamp.id;
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("machines/" + this.name);
    }
}
