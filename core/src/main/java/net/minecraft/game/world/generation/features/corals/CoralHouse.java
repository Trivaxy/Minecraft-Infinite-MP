package net.minecraft.game.world.generation.features.corals;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;

public class CoralHouse extends CoralFeature {
    public static CoralHouse INSTANCE = new CoralHouse();

    @Override
    protected boolean generateCoral(World world, Random random, int x, int y, int z, int coralId, int coralMeta) {
        if (world.getBlock(x, y - 1, z) != BlockList.sand) {
            return false;
        }

        int sizeX = random.nextInt(3) + 3;
        int sizeY = random.nextInt(3) + 3;
        int sizeZ = random.nextInt(3) + 3;
        int downOffset = random.nextInt(3) + 1;

        for (int dx = 0; dx <= sizeX; dx++) {
            for (int dy = 0; dy <= sizeY; dy++) {
                for (int dz = 0; dz <= sizeZ; dz++) {
                    int px = x + dx;
                    int py = y + dy - downOffset;
                    int pz = z + dz;
                    boolean isEdgeX = dx == 0 || dx == sizeX;
                    boolean isEdgeY = dy == 0 || dy == sizeY;
                    boolean isEdgeZ = dz == 0 || dz == sizeZ;
                    boolean skip = (dx != 0 && dx != sizeX || dy != 0 && dy != sizeY)
                        && (dz != 0 && dz != sizeZ || dy != 0 && dy != sizeY)
                        && (dx != 0 && dx != sizeX || dz != 0 && dz != sizeZ);
                    boolean isSurface = isEdgeX || isEdgeY || isEdgeZ;
                    if (skip && isSurface && !(random.nextFloat() < 0.1F)) {
                        this.placeCoralBlock(world, random, px, py, pz, coralId, coralMeta);
                    }
                }
            }
        }

        return true;
    }
}
