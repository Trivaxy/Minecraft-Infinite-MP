package net.minecraft.game.world.generation.features.trees;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.building.LayerBlock;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.block.plants.Flower;
import net.minecraft.game.world.World;
import net.minecraft.game.world.chunk.BlockPosition;
import net.minecraft.game.world.generation.features.Feature;

public abstract class Tree extends Feature {
    protected static final int logMetaHorzX = 64;
    protected static final int logMetaHorzZ = 128;
    protected int logMeta;
    protected int leafMeta;

    public void setBlockIfEmpty(World world, int x, int y, int z, int blockID, int meta) {
        if (world.getBlock(x, y, z) == null
            || world.getBlockMaterial(x, y, z) == MaterialList.water
            || world.getBlock(x, y, z) != null && (world.getBlock(x, y, z) instanceof Flower || world.getBlock(x, y, z) instanceof LayerBlock)) {
            world.setBlockAndMetadata(x, y, z, blockID, meta);
        }
    }

    public void setBlock(World world, BlockPosition blockPos, int blockID, int meta) {
        this.setBlockIfEmpty(world, blockPos.x, blockPos.y, blockPos.z, blockID, meta);
    }

    public void bresenham(World world, BlockPosition src, BlockPosition dst, int blockID, int meta) {
        this.drawBresehnam(world, src.x, src.y, src.z, dst.x, dst.y, dst.z, blockID, meta, true);
    }

    public void bresenhamNoRotation(World world, BlockPosition src, BlockPosition dst, int blockID, int meta) {
        this.drawBresehnam(world, src.x, src.y, src.z, dst.x, dst.y, dst.z, blockID, meta, false);
    }

    public BlockPosition findEndPoint(BlockPosition origin, double distance, double angle, double tilt) {
        BlockPosition dest = new BlockPosition();
        dest.set(
            origin.x + (int)Math.round(Math.sin(angle) * Math.sin(tilt) * distance),
            origin.y + (int)Math.round(Math.cos(tilt) * distance),
            origin.z + (int)Math.round(Math.cos(angle) * Math.sin(tilt) * distance)
        );
        return dest;
    }

    public void circle(World world, BlockPosition src, int rad, int blockID, int meta) {
        this.circle(world, src.x, src.y, src.z, rad, blockID, meta);
    }

    protected void drawBresehnam(World world, int x1, int y1, int z1, int x2, int y2, int z2, int blockID, int meta, boolean rotation) {
        int dx = x2 - x1;
        int dxAbs = Math.abs(dx);
        int dy = y2 - y1;
        int dyAbs = Math.abs(dy);
        int dz = z2 - z1;
        int dzAbs = Math.abs(dz);
        int xIncr = dx < 0 ? -1 : 1;
        int yIncr = dy < 0 ? -1 : 1;
        int zIncr = dz < 0 ? -1 : 1;
        int dx2 = dxAbs << 1;
        int dy2 = dyAbs << 1;
        int dz2 = dzAbs << 1;
        if (dxAbs >= dyAbs && dxAbs >= dzAbs) {
            int errorY = dy2 - dxAbs;
            int errorZ = dz2 - dxAbs;

            for (int i = 0; i < dxAbs; i++) {
                this.setBlockIfEmpty(world, x1, y1, z1, blockID, rotation ? meta | 64 : meta);
                if (errorY > 0) {
                    y1 += yIncr;
                    errorY -= dx2;
                }

                if (errorZ > 0) {
                    z1 += zIncr;
                    errorZ -= dx2;
                }

                errorY += dy2;
                errorZ += dz2;
                x1 += xIncr;
            }
        } else if (dyAbs >= dxAbs && dyAbs >= dzAbs) {
            int errorX = dx2 - dyAbs;
            int errorZ = dz2 - dyAbs;

            for (int i = 0; i < dyAbs; i++) {
                this.setBlockIfEmpty(world, x1, y1, z1, blockID, meta);
                if (errorX > 0) {
                    x1 += xIncr;
                    errorX -= dy2;
                }

                if (errorZ > 0) {
                    z1 += zIncr;
                    errorZ -= dy2;
                }

                errorX += dx2;
                errorZ += dz2;
                y1 += yIncr;
            }
        } else {
            int errorX = dx2 - dzAbs;
            int errorY = dy2 - dzAbs;

            for (int i = 0; i < dzAbs; i++) {
                this.setBlockIfEmpty(world, x1, y1, z1, blockID, rotation ? meta | 128 : meta);
                if (errorX > 0) {
                    x1 += xIncr;
                    errorX -= dz2;
                }

                if (errorY > 0) {
                    y1 += yIncr;
                    errorY -= dz2;
                }

                errorX += dx2;
                errorY += dy2;
                z1 += zIncr;
            }
        }

        this.setBlockIfEmpty(world, x1, y1, z1, blockID, meta);
    }

    public void circle(World world, int x0, int y0, int z0, int rad, int blockID, int meta) {
        for (byte dx = 0; dx <= rad; dx++) {
            for (byte dz = 0; dz <= rad; dz++) {
                int dist = (int)(Math.max(dx, dz) + Math.min(dx, dz) * 0.5);
                if (dx == 3 && dz == 3) {
                    dist = 6;
                }

                if (dist <= rad) {
                    this.setBlockIfEmpty(world, x0 + dx, y0, z0 + dz, blockID, meta);
                    this.setBlockIfEmpty(world, x0 + dx, y0, z0 - dz, blockID, meta);
                    this.setBlockIfEmpty(world, x0 - dx, y0, z0 + dz, blockID, meta);
                    this.setBlockIfEmpty(world, x0 - dx, y0, z0 - dz, blockID, meta);
                }
            }
        }
    }

    public boolean validGround(World world, int x, int y, int z) {
        Block b = BlockList.blocks[world.getBlockId(x, y, z)];
        return b != null && (b == BlockList.grass || b == BlockList.dirt || b == BlockList.snowLayer);
    }

    public void crossTreeLayer(World world, Random rand, int x0, int y0, int z0, int w, int leavesID, int leavesMeta) {
        int offset = w >> 1;
        if ((w & 1) == 0) {
            offset--;
        }

        for (int i = 0; i < w; i++) {
            this.setBlockIfEmpty(world, x0 + i - offset, y0, z0, leavesID, leavesMeta);
            this.setBlockIfEmpty(world, x0, y0, z0 + i - offset, leavesID, leavesMeta);
        }
    }

    public void squareTreeLayer(World world, Random rand, int x0, int y0, int z0, int w, int leavesID, int leavesMeta) {
        int offset = w >> 1;
        if ((w & 1) == 0) {
            offset--;
        }

        for (int x = 0; x < w; x++) {
            for (int z = 0; z < w; z++) {
                this.setBlockIfEmpty(world, x0 + x - offset, y0, z0 + z - offset, leavesID, leavesMeta);
            }
        }
    }

    public void diamondTreeLayer(World world, Random rand, int x0, int y0, int z0, int w, int leavesID, int leavesMeta) {
        int radius = w >> 1;

        for (int i = 0; i < radius; i++) {
            for (int j = 0; j <= i; j++) {
                this.setBlockIfEmpty(world, x0 - radius + 1 + i, y0, z0 - j, leavesID, leavesMeta);
                this.setBlockIfEmpty(world, x0 - radius + 1 + i, y0, z0 + 1 + j, leavesID, leavesMeta);
                this.setBlockIfEmpty(world, x0 + radius - i, y0, z0 - j, leavesID, leavesMeta);
                this.setBlockIfEmpty(world, x0 + radius - i, y0, z0 + 1 + j, leavesID, leavesMeta);
            }
        }
    }

    public boolean isFree(int mat) {
        return mat == 0 || mat == BlockList.leaves.id || mat == BlockList.snowLayer.id || mat == BlockList.mushroomCrimson.id;
    }

    public void placeBlock(World thisWorld, int x, int y, int z, int material, int dmg) {
        thisWorld.setBlockAndMetadata(x, y, z, material, dmg);
    }

    @Override
    public abstract boolean place(World var1, Random var2, int var3, int var4, int var5);

    protected static int logRotation(int[] coord, int[] start, int dir) {
        int xdiff = Math.abs(coord[0] - start[0]);
        int zdiff = Math.abs(coord[2] - start[2]);
        int maxdiff = Math.max(xdiff, zdiff);
        if (maxdiff > 0) {
            if (xdiff == maxdiff) {
                if (dir < 64) {
                    dir += 64;
                }
            } else if (dir < 64) {
                dir += 128;
            }
        }

        return dir;
    }
}
