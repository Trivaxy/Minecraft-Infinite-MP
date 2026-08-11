package net.minecraft.game.world.generation.features.trees.deciduous;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.util.math.MathHelper;

public class ProceduralTree extends Tree {
    protected static final byte[] axisConversionArray = new byte[]{2, 0, 0, 1, 2, 1};
    protected Random rnd = new Random();
    protected World world;
    protected int[] origin = new int[]{0, 0, 0};
    protected int height;
    protected int trunkHeight;
    protected double trunkHeightScale = 0.618;
    protected double branchDensity = 1.0;
    protected double branchSlope = 0.381;
    protected double widthScale = 1.0;
    protected double foliageDensity = 1.0;
    protected int trunkWidth = 1;
    protected int heightVariance = 12;
    protected int foliageHeight = 5;
    protected int[][] foliageCoords;
    protected boolean useRotation = true;
    protected int secondaryBlockToGrowOn = BlockList.grass.id;

    public ProceduralTree() {
        this(0, 0, 12);
    }

    public ProceduralTree(int meta) {
        this(meta, 0, 12);
    }

    public ProceduralTree(int meta, int heightVariance) {
        this(meta, 0, heightVariance);
    }

    public ProceduralTree(int meta, int leafmeta, int heightVariance) {
        this.logMeta = meta;
        this.heightVariance = heightVariance;
        this.leafMeta = leafmeta;
    }

    protected void prepare() {
        this.trunkHeight = (int)(this.height * this.trunkHeightScale);
        if (this.trunkHeight >= this.height) {
            this.trunkHeight = this.height - 1;
        }

        int clustersPerY = (int)(1.382 + Math.pow(this.foliageDensity * this.height / 13.0, 2.0));
        if (clustersPerY < 1) {
            clustersPerY = 1;
        }

        int[][] tempFoliageCoords = new int[clustersPerY * this.height][4];
        int y = this.origin[1] + this.height - this.foliageHeight;
        int clusterCount = 1;
        int trunkTop = this.origin[1] + this.trunkHeight;
        int relativeY = y - this.origin[1];
        tempFoliageCoords[0][0] = this.origin[0];
        tempFoliageCoords[0][1] = y;
        tempFoliageCoords[0][2] = this.origin[2];
        tempFoliageCoords[0][3] = trunkTop;
        y--;

        while (relativeY >= 0) {
            int num = 0;
            float shapefac = this.treeShape(relativeY);
            if (shapefac < 0.0F) {
                y--;
                relativeY--;
            } else {
                double originOffset = 0.5;

                while (num < clustersPerY) {
                    double radius = this.widthScale * (shapefac * (this.rnd.nextFloat() + 0.328));
                    radius = Math.min(radius, 7.5);
                    double angle = this.rnd.nextFloat() * 2.0 * 3.14159;
                    int x = MathHelper.floor(radius * Math.sin(angle) + this.origin[0] + originOffset);
                    int z = MathHelper.floor(radius * Math.cos(angle) + this.origin[2] + originOffset);
                    int[] checkStart = new int[]{x, y, z};
                    int[] checkEnd = new int[]{x, y + this.foliageHeight, z};
                    if (this.checkLine(checkStart, checkEnd) == -1) {
                        int[] checkBranchBase = new int[]{this.origin[0], this.origin[1], this.origin[2]};
                        double distance = Math.sqrt(
                            Math.pow(Math.abs(this.origin[0] - checkStart[0]), 2.0) + Math.pow(Math.abs(this.origin[2] - checkStart[2]), 2.0)
                        );
                        double branchHeight = distance * this.branchSlope;
                        if (checkStart[1] - branchHeight > trunkTop) {
                            checkBranchBase[1] = trunkTop;
                        } else {
                            checkBranchBase[1] = (int)(checkStart[1] - branchHeight);
                        }

                        if (this.checkLine(checkBranchBase, checkStart) == -1) {
                            tempFoliageCoords[clusterCount][0] = x;
                            tempFoliageCoords[clusterCount][1] = y;
                            tempFoliageCoords[clusterCount][2] = z;
                            tempFoliageCoords[clusterCount][3] = checkBranchBase[1];
                            clusterCount++;
                        }
                    }

                    num++;
                }

                y--;
                relativeY--;
            }
        }

        this.foliageCoords = new int[clusterCount][4];
        System.arraycopy(tempFoliageCoords, 0, this.foliageCoords, 0, clusterCount);
    }

    protected void crossection(int x, int y, int z, float radius, byte direction, int material, int metadata) {
        int rad = (int)(radius + 0.618);
        byte secidx1 = axisConversionArray[direction];
        byte secidx2 = axisConversionArray[direction + 3];
        int[] center = new int[]{x, y, z};
        int[] position = new int[]{0, 0, 0};
        int offset1 = -rad;
        int offset2 = -rad;

        for (position[direction] = center[direction]; offset1 <= rad; offset1++) {
            position[secidx1] = center[secidx1] + offset1;
            offset2 = -rad;

            while (offset2 <= rad) {
                double thisdistance = Math.pow(Math.abs(offset1) + 0.5, 2.0) + Math.pow(Math.abs(offset2) + 0.5, 2.0);
                if (thisdistance > radius * radius) {
                    offset2++;
                } else {
                    position[secidx2] = center[secidx2] + offset2;
                    int thisMat = this.world.getBlockId(position[0], position[1], position[2]);
                    if (thisMat != 0 && thisMat != BlockList.leaves.id) {
                        offset2++;
                    } else {
                        this.place(this.world, position[0], position[1], position[2], material, metadata);
                        offset2++;
                    }
                }
            }
        }
    }

    public void place(World thisWorld, int x, int y, int z, int material, int dmg) {
        this.placeBlock(this.world, x, y, z, material, dmg);
    }

    protected float treeShape(int y) {
        if (y < this.height * 0.3) {
            return -1.618F;
        }

        float radius = this.height / 2.0F;
        float adjacent = this.height / 2.0F - y;
        float distance;
        if (adjacent == 0.0F) {
            distance = radius;
        } else if (Math.abs(adjacent) >= radius) {
            distance = 0.0F;
        } else {
            distance = (float)Math.sqrt(Math.pow(Math.abs(radius), 2.0) - Math.pow(Math.abs(adjacent), 2.0));
        }

        return distance * 0.5F;
    }

    protected float foliageShape(int y) {
        if (y < 0 || y >= this.foliageHeight) {
            return -1.0F;
        } else {
            return y != 0 && y != this.foliageHeight - 1 ? 3.0F : 2.0F;
        }
    }

    protected void foliageCluster(int x, int y, int z) {
        int cury = y;

        for (int topy = y + this.foliageHeight; cury < topy; cury++) {
            float radius = this.foliageShape(cury - y);
            this.crossection(x, cury, z, radius, (byte)1, BlockList.leaves.id, this.leafMeta);
        }
    }

    protected void limb(int[] start, int[] end, int material, int metadata) {
        int[] delta = new int[]{0, 0, 0};
        byte idx = 0;
        byte primidx = 0;

        while (idx < 3) {
            delta[idx] = end[idx] - start[idx];
            if (Math.abs(delta[idx]) > Math.abs(delta[primidx])) {
                primidx = idx;
            }

            idx++;
        }

        if (delta[primidx] != 0) {
            byte secidx1 = axisConversionArray[primidx];
            byte secidx2 = axisConversionArray[primidx + 3];
            byte primsign;
            if (delta[primidx] > 0) {
                primsign = 1;
            } else {
                primsign = -1;
            }

            double secfac1 = (double)delta[secidx1] / delta[primidx];
            double secfac2 = (double)delta[secidx2] / delta[primidx];
            int[] coordinate = new int[]{0, 0, 0};
            int primoffset = 0;

            for (int endoffset = delta[primidx] + primsign; primoffset != endoffset; primoffset += primsign) {
                coordinate[primidx] = MathHelper.floor(start[primidx] + primoffset + 0.5);
                coordinate[secidx1] = MathHelper.floor(start[secidx1] + primoffset * secfac1 + 0.5);
                coordinate[secidx2] = MathHelper.floor(start[secidx2] + primoffset * secfac2 + 0.5);
                if (this.useRotation) {
                    metadata = logRotation(coordinate, start, metadata);
                }

                this.placeBlock(this.world, coordinate[0], coordinate[1], coordinate[2], material, metadata);
            }
        }
    }

    protected void makeFoliage() {
        int idx = 0;

        for (int finish = this.foliageCoords.length; idx < finish; idx++) {
            int x = this.foliageCoords[idx][0];
            int y = this.foliageCoords[idx][1];
            int z = this.foliageCoords[idx][2];
            this.foliageCluster(x, y, z);
        }
    }

    protected boolean trimBranches(int localY) {
        return !(localY < this.height * 0.2);
    }

    protected void makeTrunk() {
        int x = this.origin[0];
        int startY = this.origin[1];
        int topY = this.origin[1] + this.trunkHeight;
        int z = this.origin[2];
        int[] startCoord = new int[]{x, startY, z};
        int[] endCoord = new int[]{x, topY, z};
        this.limb(startCoord, endCoord, BlockList.wood.id, this.logMeta);
        if (this.trunkWidth == 2) {
            startCoord[0]++;
            endCoord[0]++;
            this.limb(startCoord, endCoord, BlockList.wood.id, this.logMeta);
            startCoord[2]++;
            endCoord[2]++;
            this.limb(startCoord, endCoord, BlockList.wood.id, this.logMeta);
            startCoord[0] += -1;
            endCoord[0] += -1;
            this.limb(startCoord, endCoord, BlockList.wood.id, this.logMeta);
        }
    }

    protected void placeBranches(int localY, int finish, int[] baseCoord, int[] endCoord) {
        if (this.trimBranches(localY)) {
            this.limb(baseCoord, endCoord, BlockList.wood.id, this.logMeta);
        }
    }

    protected void makeBranches() {
        int idx = 0;
        int finish = this.foliageCoords.length;
        int[] baseCoord = new int[]{this.origin[0], this.origin[1], this.origin[2]};

        while (idx < finish) {
            int[] coordValues = this.foliageCoords[idx];
            int[] endCoord = new int[]{coordValues[0], coordValues[1], coordValues[2]};
            baseCoord[1] = coordValues[3];
            int localY = baseCoord[1] - this.origin[1];
            this.placeBranches(localY, finish, baseCoord, endCoord);
            idx++;
        }
    }

    protected int checkLine(int[] start, int[] end) {
        int[] delta = new int[]{0, 0, 0};
        byte idx = 0;
        byte primidx = 0;

        while (idx < 3) {
            delta[idx] = end[idx] - start[idx];
            if (Math.abs(delta[idx]) > Math.abs(delta[primidx])) {
                primidx = idx;
            }

            idx++;
        }

        if (delta[primidx] == 0) {
            return -1;
        }

        byte secidx1 = axisConversionArray[primidx];
        byte secidx2 = axisConversionArray[primidx + 3];
        byte primsign;
        if (delta[primidx] > 0) {
            primsign = 1;
        } else {
            primsign = -1;
        }

        double secfac1 = (double)delta[secidx1] / delta[primidx];
        double secfac2 = (double)delta[secidx2] / delta[primidx];
        int[] coordinate = new int[]{0, 0, 0};
        int primoffset = 0;

        int endoffset;
        for (endoffset = delta[primidx] + primsign; primoffset != endoffset; primoffset += primsign) {
            coordinate[primidx] = start[primidx] + primoffset;
            coordinate[secidx1] = MathHelper.floor(start[secidx1] + primoffset * secfac1);
            coordinate[secidx2] = MathHelper.floor(start[secidx2] + primoffset * secfac2);
            int thisMat = this.world.getBlockId(coordinate[0], coordinate[1], coordinate[2]);
            if (!this.isFree(thisMat)) {
                break;
            }
        }

        return primoffset == endoffset ? -1 : Math.abs(primoffset);
    }

    protected boolean checkLocation() {
        int[] startPosition = new int[]{this.origin[0], this.origin[1], this.origin[2]};
        int[] endPosition = new int[]{this.origin[0], this.origin[1] + this.height - 1, this.origin[2]};
        int tile = this.world.getBlockId(this.origin[0], this.origin[1] - 1, this.origin[2]);
        if (tile != BlockList.dirt.id && tile != this.secondaryBlockToGrowOn) {
            return false;
        }

        int allowedHeight = this.checkLine(startPosition, endPosition);
        if (allowedHeight == -1) {
            return true;
        }

        if (allowedHeight < 6) {
            return false;
        }

        this.height = allowedHeight;
        return true;
    }

    @Override
    public boolean place(World level, Random random, int x, int y, int z) {
        this.world = level;
        long seed = random.nextLong();
        this.rnd.setSeed(seed);
        this.origin[0] = x;
        this.origin[1] = y;
        this.origin[2] = z;
        if (this.height == 0) {
            this.height = 5 + this.rnd.nextInt(this.heightVariance);
        }

        if (!this.checkLocation()) {
            return false;
        }

        this.prepare();
        this.makeFoliage();
        this.makeTrunk();
        this.makeBranches();
        return true;
    }
}
