package net.minecraft.game.block.plants;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class GlowLychen extends Flower {
    public Icon variant1;
    public Icon variant2;

    public GlowLychen(int id) {
        super(id);
        this.setBounds(0.0, 0.3F, 0.0, 1.0, 1.0, 1.0);
    }

    @Override
    public boolean placeableAt(World world, int x, int y, int z) {
        return this.canStay(world, x, y, z);
    }

    @Override
    public boolean canGrowHere(int blockid) {
        return Block.isOpaque.get(blockid);
    }

    @Override
    public boolean canStay(World world, int x, int y, int z) {
        return world.getBlockId(x, y + 1, z) == BlockList.dirt.id || world.getBlockId(x, y + 1, z) == BlockList.moss.id;
    }

    @Override
    public void registerIcons(IconRegister r) {
        super.registerIcons(r);
        this.variant1 = r.registerIcon("plants/glowLychenA");
        this.variant2 = r.registerIcon("plants/glowLychenB");
    }

    @Override
    public int dropID(int meta, Random rand) {
        return 0;
    }
}
