package net.minecraft.game.block.misc;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.mob.monster.arachnid.Silverfish;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.world.World;

public class InfestedStone extends Block {
    public InfestedStone(int id) {
        super(id, MaterialList.rock);
        this.silkTouch = false;
    }

    @Override
    public Icon getIcon(int side, int mdata) {
        return BlockList.stone.getIcon(side, mdata);
    }

    @Override
    public int dropMeta(int id) {
        return id;
    }

    @Override
    public int dropID(int blockid, Random random) {
        return BlockList.cobblestone.id;
    }

    @Override
    public void onBroken(World world, int x, int y, int z, int side) {
        if (!world.isOnline) {
            Silverfish shit = new Silverfish(world);
            shit.setPosAndRot(x + 0.5, y, z + 0.5, 0.0F, 0.0F);
            world.spawnEntity(shit);
            shit.spawnPuffParticles();
        }

        super.onBroken(world, x, y, z, side);
    }

    @Override
    public int quantityDropped(Random rand) {
        return 0;
    }

    public static boolean getPosingIdByMetadata(int id) {
        return id == BlockList.stone.id;
    }
}
