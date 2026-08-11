package net.minecraft.game.world;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.tileentity.TileEntity;

public interface WorldSource {
    int getBlockId(int var1, int var2, int var3);

    TileEntity getTileEntity(int var1, int var2, int var3);

    float getBrightness(int var1, int var2, int var3);

    int getBlockMetadata(int var1, int var2, int var3);

    Material getBlockMaterial(int var1, int var2, int var3);

    boolean isBlockFull(int var1, int var2, int var3);

    boolean isSolid(int var1, int var2, int var3);

    int getBrightness(int var1, int var2, int var3, int var4);

    boolean areChunksEmpty();

    Block getBlock(int var1, int var2, int var3);
}
