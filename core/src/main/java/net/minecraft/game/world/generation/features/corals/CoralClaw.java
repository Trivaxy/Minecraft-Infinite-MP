package net.minecraft.game.world.generation.features.corals;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;

public class CoralClaw extends CoralFeature {
    public static CoralClaw INSTANCE = new CoralClaw();

    @Override
    protected boolean generateCoral(World world, Random random, int x, int y, int z, int coralId, int coralMeta) {
        if (world.getBlock(x, y - 1, z) != BlockList.sand) {
            return false;
        }

        int mainDirection = random.nextInt(4);
        int branchCount = random.nextInt(2) + 2;
        int[] directions = new int[]{mainDirection, this.rotateLeft(mainDirection), this.rotateRight(mainDirection)};
        this.shuffleArray(directions, random);

        for (int d = 0; d < branchCount; d++) {
            int dir = directions[d];
            int cx = x;
            int cy = y;
            int cz = z;
            int initialLength = random.nextInt(2) + 1;
            cx += this.dx(dir);
            cz += this.dz(dir);
            int forwardLength;
            int moveDir;
            if (dir == mainDirection) {
                moveDir = dir;
                forwardLength = random.nextInt(3) + 2;
            } else {
                cy++;
                if (random.nextBoolean()) {
                    moveDir = dir;
                } else {
                    moveDir = 4;
                }

                forwardLength = random.nextInt(3) + 3;
            }

            for (int i = 0; i < initialLength && this.placeCoralBlock(world, random, cx, cy, cz, coralId, coralMeta); i++) {
                if (moveDir == 4) {
                    cy++;
                } else {
                    cx += this.dx(moveDir);
                    cz += this.dz(moveDir);
                }
            }

            if (moveDir == 4) {
                cy--;
            } else {
                cx -= this.dx(moveDir);
                cz -= this.dz(moveDir);
            }

            cy++;

            for (int i = 0; i < forwardLength; i++) {
                cx += this.dx(mainDirection);
                cz += this.dz(mainDirection);
                if (!this.placeCoralBlock(world, random, cx, cy, cz, coralId, coralMeta)) {
                    break;
                }

                if (random.nextFloat() < 0.25F) {
                    cy++;
                }
            }
        }

        return true;
    }
}
