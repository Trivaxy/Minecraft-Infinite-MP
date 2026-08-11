package net.minecraft.game.world.generation.features.general;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.Feature;
import net.minecraft.util.math.MathHelper;

public class ClayLump extends Feature {
    public static final ClayLump clay = new ClayLump(32, BlockList.clay.id, BlockList.sand.id);
    public static final ClayLump gravel = new ClayLump(48, BlockList.gravel.id, BlockList.dirt.id);
    public static final ClayLump sand = new ClayLump(48, BlockList.sand.id, BlockList.gravel.id);
    private int blockToSpawnIn;
    private int size;
    private int clayBlockId;

    public ClayLump(int num, int blockid, int blockToSpawn) {
        this.size = num;
        this.clayBlockId = blockid;
        this.blockToSpawnIn = blockToSpawn;
    }

    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        if (world.getBlockMaterial(x, y, z) != MaterialList.water) {
            return false;
        }

        float randomizer = random.nextFloat() * (float) Math.PI;
        float xOffPos = x + 8 + MathHelper.sin(randomizer) * this.size / 8.0F;
        float xOffNeg = x + 8 - MathHelper.sin(randomizer) * this.size / 8.0F;
        float zOffPos = z + 8 + MathHelper.cos(randomizer) * this.size / 8.0F;
        float zOffNeg = z + 8 - MathHelper.cos(randomizer) * this.size / 8.0F;
        float yOffPos = y + random.nextInt(3) - 2;
        float yOffNeg = y + random.nextInt(3) - 2;

        for (int attempts = 0; attempts <= this.size; attempts++) {
            double x1 = xOffPos + (double)(xOffNeg - xOffPos) * attempts / this.size;
            double y1 = yOffPos + (double)(yOffNeg - yOffPos) * attempts / this.size;
            double z1 = zOffPos + (double)(zOffNeg - zOffPos) * attempts / this.size;
            double scale = random.nextDouble() * this.size / 16.0;
            double ass = (MathHelper.sin(attempts * (float) Math.PI / this.size) + 1.0F) * scale + 1.0;
            double cunt = (MathHelper.sin(attempts * (float) Math.PI / this.size) + 1.0F) * scale + 1.0;
            int left = MathHelper.floor_double(x1 - ass / 2.0);
            int right = MathHelper.floor_double(x1 + ass / 2.0);
            int down = MathHelper.floor_double(y1 - cunt / 2.0);
            int up = MathHelper.floor_double(y1 + cunt / 2.0);
            int back = MathHelper.floor_double(z1 - ass / 2.0);
            int front = MathHelper.floor_double(z1 + ass / 2.0);

            for (int finalX = left; finalX <= right; finalX++) {
                for (int finalY = down; finalY <= up; finalY++) {
                    for (int finalZ = back; finalZ <= front; finalZ++) {
                        double distX = (finalX + 0.5 - x1) / (ass / 2.0);
                        double distY = (finalY + 0.5 - y1) / (cunt / 2.0);
                        double distZ = (finalZ + 0.5 - z1) / (ass / 2.0);
                        if (distX * distX + distY * distY + distZ * distZ < 1.0) {
                            int id = world.getBlockId(finalX, finalY, finalZ);
                            if (id == this.blockToSpawnIn) {
                                world.setBlock(finalX, finalY, finalZ, this.clayBlockId);
                            }
                        }
                    }
                }
            }
        }

        return true;
    }
}
