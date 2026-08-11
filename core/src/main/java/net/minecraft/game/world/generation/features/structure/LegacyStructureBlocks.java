package net.minecraft.game.world.generation.features.structure;

import java.util.Random;

public abstract class LegacyStructureBlocks {
    protected int id;
    protected int meta;

    public abstract void selectBlocks(Random var1, int var2, int var3, int var4, boolean var5);

    public int getBlockId() {
        return this.id;
    }

    public int getBlockMetadata() {
        return this.meta;
    }
}
