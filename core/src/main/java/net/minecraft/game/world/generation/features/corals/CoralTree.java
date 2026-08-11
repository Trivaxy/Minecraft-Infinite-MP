package net.minecraft.game.world.generation.features.corals;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;

public class CoralTree extends CoralFeature {
    public static CoralTree INSTANCE = new CoralTree();

    @Override
    protected boolean generateCoral(World world, Random random, int x, int y, int z, int coralId, int coralMeta) {
        int cx = x;
        int cy = y;
        int cz = z;
        int trunkHeight = random.nextInt(3) + 1;

        for (int i = 0; i < trunkHeight; i++) {
            if (world.getBlock(cx, cy - 1, cz) != BlockList.sand) {
                return true;
            }
        }

        int topX = cx;
        int topY = cy;
        int topZ = cz;
        int branchCount = random.nextInt(3) + 2;
        int[] directions = new int[]{0, 1, 2, 3};
        this.shuffleArray(directions, random);

        for (int b = 0; b < branchCount; b++) {
            int dir = directions[b];
            cx = topX;
            cy = topY;
            cz = topZ;
            cx += this.dx(dir);
            cz += this.dz(dir);
            int branchLength = random.nextInt(5) + 2;
            int verticalRun = 0;

            for (int i = 0; i < branchLength && this.placeCoralBlock(world, random, cx, cy, cz, coralId, coralMeta); i++) {
                verticalRun++;
                cy++;
                if (i == 0 || verticalRun >= 2 && random.nextFloat() < 0.25F) {
                    cx += this.dx(dir);
                    cz += this.dz(dir);
                    verticalRun = 0;
                }
            }
        }

        return true;
    }
}
