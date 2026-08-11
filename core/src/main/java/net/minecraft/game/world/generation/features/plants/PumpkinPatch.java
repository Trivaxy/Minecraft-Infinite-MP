package net.minecraft.game.world.generation.features.plants;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.Feature;

public class PumpkinPatch extends Feature {
    public static final PumpkinPatch pumpkinPatch = new PumpkinPatch(BlockList.pumpkin.id);
    public static final PumpkinPatch melonPatch = new PumpkinPatch(BlockList.melon.id);
    int blockid;

    public PumpkinPatch(int id) {
        this.blockid = id;
    }

    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        for (int attempts = 0; attempts < 64; attempts++) {
            int x0 = x + random.nextInt(8) - random.nextInt(8);
            int y0 = y + random.nextInt(4) - random.nextInt(4);
            int z0 = z + random.nextInt(8) - random.nextInt(8);
            int rotation = random.nextInt(3);
            if (world.isAir(x0, y0, z0) && world.getBlockId(x0, y0 - 1, z0) == BlockList.grass.id && BlockList.pumpkin.placeableAt(world, x0, y0, z0)) {
                if (random.nextInt(100) == 0 && this.blockid == BlockList.pumpkin.id) {
                    world.setBlockAndMetadata(x0, y0, z0, this.blockid, 1);
                }

                world.setBlockAndMetadata(x0, y0, z0, this.blockid, rotation == 0 ? 0 : (rotation == 1 ? 64 : 128));
            }
        }

        return true;
    }
}
