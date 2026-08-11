package net.minecraft.game.world.generation.features;

import java.util.Random;
import net.minecraft.game.world.World;

public abstract class Feature {
    protected int topBlockMetadata;
    protected int fillerBlockMetadata;

    public abstract boolean place(World var1, Random var2, int var3, int var4, int var5);
}
