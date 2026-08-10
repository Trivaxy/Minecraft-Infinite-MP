package net.minecraft.game.block.plants;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.fungal.BigMushroom;
import net.minecraft.statistics.StatList;

public class Mushroom extends Flower {
    public Mushroom(int id) {
        super(id);
        float offset = 0.2F;
        this.setBounds(0.5F - offset, 0.0, 0.5F - offset, 0.5F + offset, offset * 2.0F, 0.5F + offset);
    }

    @Override
    public boolean canGrowHere(int blockid) {
        return Block.isOpaque.get(blockid);
    }

    @Override
    public boolean canStay(World world, int xCoord, int yCoord, int zCoord) {
        return world.getBlockId(xCoord, yCoord - 1, zCoord) == BlockList.grass.id && world.getBlockMetadata(xCoord, yCoord - 1, zCoord) == 4
            || world.getBlockBrightness(xCoord, yCoord, zCoord) <= 12 && this.canGrowHere(world.getBlockId(xCoord, yCoord - 1, zCoord));
    }

    @Override
    public void growTree(World w, Player player, int x, int y, int z, Random r) {
        Tree grower = BigMushroom.MUSHROOMS.get(w.getBlockId(x, y, z));
        if (grower != null && grower.place(w, r, x, y, z)) {
            player.addStat(StatList.mushroomGrowth, 1);
        }
    }
}
