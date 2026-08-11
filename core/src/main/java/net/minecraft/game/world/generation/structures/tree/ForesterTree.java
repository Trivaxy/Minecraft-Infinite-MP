package net.minecraft.game.world.generation.structures.tree;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.trees.Tree;

public abstract class ForesterTree extends Tree {
    protected double trunkHeight;
    protected double[] foliageShape;
    private ArrayList<int[]> foliageCoords;
    protected double trunkRadius;
    protected double branchDensity;
    protected double branchSlope;
    public final Random random = new Random();
    protected World world;
    public int[] pos;
    public int height;
    public int treeEdgeHeight = 25;
    public boolean treeWood = true;
    public double treeTrunkThickness = 1.0;
    public double treeTrunkHeight = 0.7;
    public boolean treeBrokenTrunk = false;
    public boolean treeHollowTrunk = false;
    public double treeBranchDensity = 1.0;
    public TreeRoots treeRoots = TreeRoots.YES;
    public boolean treeRootButtresses = false;
    public boolean treeFoliage = true;
    public double treeFoliageDensity = 1.0;
    public int treeLights = 0;
    public int treeWoodBlock = 17;
    public int treeWoodMetadata = 0;
    public int treeLeafBlock = 18;
    public int treeLeafMetadata = 0;
    public int treeLightingBlock = 50;
    public int treeLightingMetadata = 0;
    public int treeTrunkFillerBlock = 0;
    public int treeTrunkFillerMetadata = 0;
    public int[] treeRootStoppingBlocks = new int[]{1, 7};
    public int[] treeBranchStoppingBlocks = new int[]{1, 4, 7, 20};
    public boolean causeBlockUpdates = false;

    public ForesterTree() {
        this.pos = new int[]{0, 0, 0};
        this.height = 1;
    }

    public void checkParameters() {
        if (this.treeEdgeHeight < 1) {
            this.treeEdgeHeight = 1;
        }

        if (this.treeTrunkThickness < 0.0) {
            this.treeTrunkThickness = 0.0;
        }

        if (this.treeTrunkHeight < 0.0) {
            this.treeTrunkHeight = 0.0;
        }

        if (this.treeRoots == null) {
            this.treeRoots = TreeRoots.NO;
        }

        if (this.treeFoliageDensity < 0.0) {
            this.treeFoliageDensity = 0.0;
        }

        if (this.treeBranchDensity < 0.0) {
            this.treeBranchDensity = 0.0;
        }

        if (this.treeLights < 0 || this.treeLights > 4) {
            this.treeLights = 0;
        }
    }

    @Override
    public String toString() {
        return String.format("Tree{pos=(%d,%d,%d),height=%d,type=%s}", this.pos[0], this.pos[1], this.pos[2], this.height, this.getClass().getSimpleName());
    }

    protected final void assignValue(int x, int y, int z, int id, int metadata) {
        if (!this.world.isSolid(x, y, z) || this.treeHollowTrunk) {
            if (this.causeBlockUpdates) {
                this.world.setBlockAndMetadataWithNotify(x, y, z, id, metadata);
            } else {
                this.world.setBlockAndMetadata(x, y, z, id, metadata);
            }
        }
    }

    protected boolean validTreeLocation() {
        int[] start = new int[]{this.pos[0], this.pos[1], this.pos[2]};
        int[] end = new int[]{this.pos[0], this.pos[1] + this.height - 1, this.pos[2]};
        int mat = this.world.getBlockId(this.pos[0], this.pos[1] - 1, this.pos[2]);
        if (mat != 2 && mat != 3) {
            return false;
        }

        int allowedHeight = this.checkBlockLine(start, end);
        return allowedHeight == -1 ? true : allowedHeight >= 6;
    }

    private int checkBlockLine(int[] start, int[] end) {
        int[] delta = new int[]{0, 0, 0};

        for (int idx = 0; idx < 3; idx++) {
            delta[idx] = end[idx] - start[idx];
        }

        int maxdist = TreeHelper.maxKeyAbs(delta);
        int primidx = TreeHelper.getArrayIndex(delta, maxdist);
        if (delta[primidx] == 0) {
            return -1;
        }

        int secidx1 = TreeHelper.getOtherIndexes(delta, primidx)[0];
        int secidx2 = TreeHelper.getOtherIndexes(delta, primidx)[1];
        int primsign;
        if (delta[primidx] > 0) {
            primsign = 1;
        } else {
            primsign = -1;
        }

        double secfac1 = (double)delta[secidx1] / delta[primidx];
        double secfac2 = (double)delta[secidx2] / delta[primidx];
        int[] coord = new int[]{0, 0, 0};
        int primoffset = 0;

        int endoffset;
        for (endoffset = delta[primidx] + primsign; primoffset != endoffset; primoffset += primsign) {
            coord[primidx] = start[primidx] + primoffset;
            coord[secidx1] = (int)Math.floor(start[secidx1] + primoffset * secfac1);
            coord[secidx2] = (int)Math.floor(start[secidx2] + primoffset * secfac2);
            int mat = this.world.getBlockId(coord[0], coord[1], coord[2]);
            if (mat != 0 && mat != 18) {
                break;
            }
        }

        return primoffset == endoffset ? -1 : Math.abs(primoffset);
    }

    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        this.random.setSeed(random.nextLong());
        if (this.height == 0) {
            this.height = 25 + this.random.nextInt(10);
        }

        this.world = world;
        this.pos[0] = x;
        this.pos[1] = y;
        this.pos[2] = z;
        this.checkParameters();
        if (!this.validTreeLocation()) {
            return false;
        }

        this.prepare();
        if (this.treeFoliage) {
            this.makeFoliage();
        }

        if (this.treeWood) {
            this.makeTrunk();
        }

        return true;
    }

    protected void crossSection(int x, int y, int z, double radius, int dirAxis, int blockID, int metadata) {
        int rad = (int)(radius + 0.618);
        if (rad > 0) {
            int[] coord = new int[]{0, 0, 0};
            int[] center = new int[]{x, y, z};
            int secidx1 = TreeHelper.getOtherIndexes(center, dirAxis)[0];
            int secidx2 = TreeHelper.getOtherIndexes(center, dirAxis)[1];

            for (int off1 = -rad; off1 < rad + 1; off1++) {
                for (int off2 = -rad; off2 < rad + 1; off2++) {
                    double thisdist = Math.sqrt(Math.pow(Math.abs(off1) + 0.5, 2.0) + Math.pow(Math.abs(off2) + 0.5, 2.0));
                    if (!(thisdist > radius)) {
                        int pri = center[dirAxis];
                        int sec1 = center[secidx1] + off1;
                        int sec2 = center[secidx2] + off2;
                        coord[dirAxis] = pri;
                        coord[secidx1] = sec1;
                        coord[secidx2] = sec2;
                        this.assignValue(coord[0], coord[1], coord[2], blockID, metadata);
                    }
                }
            }
        }
    }

    protected double shape(int y) {
        return this.random.nextDouble() < 100.0 / Math.pow(this.height, 2.0) && y < this.trunkHeight ? this.height * 0.12 : Double.NaN;
    }

    protected void foliageCluster(int x, int y, int z) {
        double[] level_radius = this.foliageShape;

        for (double i : level_radius) {
            this.crossSection(x, y, z, i, 1, this.treeLeafBlock, this.treeLeafMetadata);
            y++;
        }
    }

    protected void taperedCylinder(int[] start, int[] end, double startSize, double endSize, int blockID, int metadata) {
        int[] delta = new int[]{0, 0, 0};

        for (int i = 0; i < 3; i++) {
            delta[i] = end[i] - start[i];
        }

        int maxdist = TreeHelper.maxKeyAbs(delta);
        if (maxdist != 0) {
            int primidx = TreeHelper.getArrayIndex(delta, maxdist);
            int secidx1 = TreeHelper.getOtherIndexes(delta, primidx)[0];
            int secidx2 = TreeHelper.getOtherIndexes(delta, primidx)[1];
            int primsign = delta[primidx] / Math.abs(delta[primidx]);
            int secdelta1 = delta[secidx1];
            double secfac1 = (double)secdelta1 / delta[primidx];
            int secdelta2 = delta[secidx2];
            double secfac2 = (double)secdelta2 / delta[primidx];
            int[] coord = new int[]{0, 0, 0};
            int endoffset = delta[primidx] + primsign;

            for (int primoffset = 0; primoffset != endoffset; primoffset += primsign) {
                int primloc = start[primidx] + primoffset;
                int secloc1 = (int)(start[secidx1] + primoffset * secfac1);
                int secloc2 = (int)(start[secidx2] + primoffset * secfac2);
                coord[primidx] = primloc;
                coord[secidx1] = secloc1;
                coord[secidx2] = secloc2;
                if (this.treeWoodBlock == 17) {
                    metadata = logRotation(coord, start, metadata);
                }

                int primdist = Math.abs(delta[primidx]);
                double radius = endSize + (startSize - endSize) * Math.abs(delta[primidx] - primoffset) / primdist;
                this.crossSection(coord[0], coord[1], coord[2], radius, primidx, blockID, metadata);
            }
        }
    }

    protected void makeRoots(ArrayList<double[]> rootBases) {
        int[] treeposition = this.pos;
        int height = this.height;

        for (int[] coord : this.foliageCoords) {
            double dist = Math.sqrt(Math.pow(coord[0] - treeposition[0], 2.0) + Math.pow(coord[2] - treeposition[2], 2.0));
            int ydist = coord[1] - treeposition[1];
            double value = this.branchDensity * 220.0 * height / Math.pow(ydist + dist, 3.0);
            if (!(value < this.random.nextDouble())) {
                double[] rootbase = rootBases.get(this.random.nextInt(rootBases.size()));
                int rootx = (int)rootbase[0];
                int rootz = (int)rootbase[1];
                double rootbaseradius = rootbase[2];
                double rndr = Math.sqrt(this.random.nextDouble()) * rootbaseradius * 0.618;
                double rndang = this.random.nextDouble() * 2.0 * Math.PI;
                int rndx = (int)(rndr * Math.sin(rndang) + 0.5);
                int rndz = (int)(rndr * Math.cos(rndang) + 0.5);
                int rndy = (int)(this.random.nextDouble() * rootbaseradius * 0.5);
                int[] startcoord = new int[]{rootx + rndx, treeposition[1] + rndy, rootz + rndz};
                int[] offset = new int[]{0, 0, 0};

                for (int i = 0; i < 3; i++) {
                    offset[i] = startcoord[i] - coord[i];
                }

                int[] endcoord = new int[]{0, 0, 0};

                for (int var42 = 0; var42 < 3; var42++) {
                    endcoord[var42] = startcoord[var42] + offset[var42];
                }

                double rootstartsize = rootbaseradius * 0.618 * Math.abs(offset[1]) / height * 0.618;
                if (rootstartsize < 1.0) {
                    rootstartsize = 1.0;
                }

                double endsize = 1.0;
                if (this.treeRoots != TreeRoots.TO_STONE && this.treeRoots != TreeRoots.HANGING) {
                    this.taperedCylinder(startcoord, endcoord, rootstartsize, endsize, this.treeWoodBlock, this.treeWoodMetadata);
                } else {
                    int offlength = (int)Math.sqrt(Math.pow(offset[0], 2.0) + Math.pow(offset[1], 2.0) + Math.pow(offset[2], 2.0));
                    if (offlength >= 1) {
                        double rootmid = endsize;
                        int[] vec = new int[]{0, 0, 0};

                        for (int var43 = 0; var43 < 3; var43++) {
                            vec[var43] = offset[var43] / offlength;
                        }

                        int[] searchindex = new int[]{0};
                        if (this.treeRoots == TreeRoots.TO_STONE) {
                            searchindex = this.treeRootStoppingBlocks;
                        } else if (this.treeRoots == TreeRoots.HANGING) {
                            searchindex = new int[]{0};
                        }

                        int startdist = (int)(this.random.nextDouble() * 6.0 * Math.sqrt(rootstartsize) + 2.8);
                        int[] searchstart = new int[]{0, 0, 0};

                        for (int var44 = 0; var44 < 3; var44++) {
                            searchstart[var44] = startcoord[var44] + startdist * vec[var44];
                        }

                        dist = startdist + TreeHelper.distToMat(searchstart, vec, searchindex, this.world, false, offlength);
                        if (dist < offlength) {
                            rootmid += (rootstartsize - endsize) * (1.0 - dist / offlength);

                            for (int var45 = 0; var45 < 3; var45++) {
                                endcoord[var45] = startcoord[var45] + (int)(vec[var45] * dist);
                            }

                            if (this.treeRoots == TreeRoots.HANGING) {
                                double remaining_dist = offlength - dist;
                                int[] bottomcord = new int[endcoord.length];
                                System.arraycopy(endcoord, 0, bottomcord, 0, endcoord.length);
                                bottomcord[1] -= (int)remaining_dist;
                                this.taperedCylinder(endcoord, bottomcord, rootmid, endsize, this.treeWoodBlock, this.treeWoodMetadata);
                            }
                        }

                        this.taperedCylinder(startcoord, endcoord, rootstartsize, rootmid, this.treeWoodBlock, this.treeWoodMetadata);
                    }
                }
            }
        }
    }

    protected boolean trimBranches(int y) {
        return true;
    }

    protected void makeBranches() {
        int[] treeposition = this.pos;
        int height = this.height;
        int topy = treeposition[1] + (int)(this.trunkHeight + 0.5);
        double endrad = this.trunkRadius * (1.0 - this.trunkHeight / height);
        if (endrad < 1.0) {
            endrad = 1.0;
        }

        for (int[] coord : this.foliageCoords) {
            double dist = Math.sqrt(Math.pow(coord[0] - treeposition[0], 2.0) + Math.pow(coord[2] - treeposition[2], 2.0));
            int ydist = coord[1] - treeposition[1];
            double value = this.branchDensity * 220.0 * height / Math.pow(ydist + dist, 3.0);
            if (!(value < this.random.nextDouble())) {
                int posy = coord[1];
                double slope = this.branchSlope + (0.5 - this.random.nextDouble()) * 0.16;
                double branchy;
                double basesize;
                if (coord[1] - dist * slope > topy) {
                    double threshhold = 1.0 / height;
                    if (this.random.nextDouble() < threshhold) {
                        continue;
                    }

                    branchy = topy;
                    basesize = endrad;
                } else {
                    branchy = posy - dist * slope;
                    basesize = endrad + (this.trunkRadius - endrad) * (topy - branchy) / this.trunkHeight;
                }

                double startsize = basesize * (1.0 + this.random.nextDouble()) * 0.618 * Math.pow(dist / height, 0.618);
                double rndr = Math.sqrt(this.random.nextDouble()) * basesize * 0.618;
                double rndang = this.random.nextDouble() * 2.0 * Math.PI;
                int rndx = (int)(rndr * Math.sin(rndang) + 0.5);
                int rndz = (int)(rndr * Math.cos(rndang) + 0.5);
                int[] startcoord = new int[]{treeposition[0] + rndx, (int)branchy, treeposition[2] + rndz};
                if (startsize < 1.0) {
                    startsize = 1.0;
                }

                double endsize = 1.0;
                int y = startcoord[1] - treeposition[1];
                if (this.trimBranches(y)) {
                    this.taperedCylinder(startcoord, coord, startsize, endsize, this.treeWoodBlock, this.treeWoodMetadata);
                }
            }
        }
    }

    public void prepare() {
        int[] treeposition = this.pos;
        this.trunkRadius = 0.618 * Math.sqrt(this.height * this.treeTrunkThickness);
        if (this.trunkRadius < 1.0) {
            this.trunkRadius = 1.0;
        }

        int yend;
        if (this.treeBrokenTrunk) {
            this.trunkHeight = this.height * (0.3 + this.random.nextDouble() * 0.4);
            yend = (int)(treeposition[1] + this.trunkHeight + 0.5);
        } else {
            this.trunkHeight = this.height;
            yend = treeposition[1] + this.height;
        }

        this.branchDensity = this.treeBranchDensity / this.treeFoliageDensity;
        int topy = treeposition[1] + (int)(this.trunkHeight + 0.5);
        int ystart = treeposition[1];
        int num_of_clusters_per_y = (int)(1.5 + Math.pow(this.treeFoliageDensity * this.height / 19.0, 2.0));
        ArrayList<int[]> foliage_coords = new ArrayList<>();
        if (num_of_clusters_per_y < 1) {
            num_of_clusters_per_y = 1;
        }

        if (yend > this.world.getWorldHeight()) {
            yend = this.world.getWorldHeight();
        }

        if (ystart > this.world.getWorldHeight()) {
            ystart = this.world.getWorldHeight();
        }

        for (int y = yend; y > ystart; y--) {
            for (int i = 0; i < num_of_clusters_per_y; i++) {
                double shapefac = this.shape(y - ystart);
                if (!Double.isNaN(shapefac)) {
                    double r = shapefac * (Math.sqrt(this.random.nextDouble()) + 0.328);
                    double theta = this.random.nextDouble() * 2.0 * Math.PI;
                    int x = (int)(r * Math.sin(theta) + treeposition[0]);
                    int z = (int)(r * Math.cos(theta) + treeposition[2]);
                    if (this.treeBranchStoppingBlocks.length != 0) {
                        double dist = Math.sqrt(Math.pow(x - treeposition[0], 2.0) + Math.pow(z - treeposition[2], 2.0));
                        double slope = this.branchSlope;
                        int starty;
                        if (y - dist * slope > topy) {
                            starty = topy;
                        } else {
                            starty = (int)(y - dist * slope);
                        }

                        int[] start = new int[]{treeposition[0], starty, treeposition[2]};
                        int[] offset = new int[]{x - treeposition[0], y - starty, z - treeposition[2]};
                        double offlength = Math.sqrt(Math.pow(offset[0], 2.0) + Math.pow(offset[1], 2.0) + Math.pow(offset[2], 2.0));
                        if (offlength < 1.0) {
                            continue;
                        }

                        int[] vec = new int[]{0, 0, 0};

                        for (int k = 0; k < 3; k++) {
                            vec[k] = (int)(offset[k] / offlength);
                        }

                        int mat_dist = TreeHelper.distToMat(start, vec, this.treeBranchStoppingBlocks, this.world, false, (int)offlength + 3);
                        if (mat_dist < (int)offlength + 2) {
                            continue;
                        }
                    }

                    foliage_coords.add(new int[]{x, y, z});
                }
            }
        }

        this.foliageCoords = foliage_coords;
    }

    public void makeFoliage() {
        ArrayList<int[]> foliage_coords = this.foliageCoords;

        for (int[] coord : foliage_coords) {
            this.foliageCluster(coord[0], coord[1], coord[2]);
        }

        for (int[] cord : foliage_coords) {
            this.assignValue(cord[0], cord[1], cord[2], this.treeWoodBlock, this.treeWoodMetadata);
            if (this.treeLights == 1) {
                this.assignValue(cord[0], cord[1] + 1, cord[2], this.treeLightingBlock, this.treeLightingMetadata);
            } else if (this.treeLights == 2 || this.treeLights == 3 || this.treeLights == 4) {
                this.assignValue(cord[0] + 1, cord[1], cord[2], this.treeLightingBlock, this.treeLightingMetadata);
                this.assignValue(cord[0] - 1, cord[1], cord[2], this.treeLightingBlock, this.treeLightingMetadata);
                if (this.treeLights == 4) {
                    this.assignValue(cord[0], cord[1], cord[2] + 1, this.treeLightingBlock, this.treeLightingMetadata);
                    this.assignValue(cord[0], cord[1], cord[2] - 1, this.treeLightingBlock, this.treeLightingMetadata);
                }
            }
        }
    }

    public void makeTrunk() {
        int height = this.height;
        double trunkheight = this.trunkHeight;
        double trunkradius = this.trunkRadius;
        int[] treeposition = this.pos;
        int starty = treeposition[1];
        int midy = treeposition[1] + (int)(trunkheight * 0.382);
        int topy = treeposition[1] + (int)(trunkheight + 0.5);
        int x = treeposition[0];
        int z = treeposition[2];
        double end_size_factor = trunkheight / height;
        double midrad = trunkradius * (1.0 - end_size_factor * 0.5);
        double endrad = trunkradius * (1.0 - end_size_factor);
        if (endrad < 1.0) {
            endrad = 1.0;
        }

        if (midrad < endrad) {
            midrad = endrad;
        }

        ArrayList<double[]> rootbases = new ArrayList<>();
        double startrad;
        if (this.treeRootButtresses) {
            startrad = trunkradius * 0.8;
            rootbases.add(new double[]{x, z, startrad});
            double buttress_radius = trunkradius * 0.382;
            double posradius = trunkradius;
            int num_of_buttresses = (int)(Math.sqrt(trunkradius) + 3.5);

            for (int i = 0; i < num_of_buttresses; i++) {
                double rnang = this.random.nextDouble() * 2.0 * Math.PI;
                double thisposradius = posradius * (0.9 + this.random.nextDouble() * 0.2);
                int thisx = x + (int)(thisposradius * Math.sin(rnang));
                int thisz = z + (int)(thisposradius * Math.cos(rnang));
                double thisbuttressradius = buttress_radius * (0.618 + this.random.nextDouble());
                if (thisbuttressradius < 1.0) {
                    thisbuttressradius = 1.0;
                }

                this.taperedCylinder(
                    new int[]{thisx, starty, thisz}, new int[]{x, midy, z}, thisbuttressradius, thisbuttressradius, this.treeWoodBlock, this.treeWoodMetadata
                );
                rootbases.add(new double[]{thisx, thisz, thisbuttressradius});
            }
        } else {
            startrad = trunkradius;
            rootbases.add(new double[]{x, z, startrad});
        }

        this.taperedCylinder(new int[]{x, starty, z}, new int[]{x, midy, z}, startrad, midrad, this.treeWoodBlock, this.treeWoodMetadata);
        this.taperedCylinder(new int[]{x, midy, z}, new int[]{x, topy, z}, midrad, endrad, this.treeWoodBlock, this.treeWoodMetadata);
        this.makeBranches();
        if (this.treeRoots == TreeRoots.YES || this.treeRoots == TreeRoots.TO_STONE || this.treeRoots == TreeRoots.HANGING) {
            this.makeRoots(rootbases);
        }

        if (trunkradius > 2.0 && this.treeHollowTrunk) {
            double wall_thickness = 1.0 + trunkradius * 0.1 * this.random.nextDouble();
            if (wall_thickness < 1.3) {
                wall_thickness = 1.3;
            }

            double base_radius = trunkradius - wall_thickness;
            if (base_radius < 1.0) {
                base_radius = 1.0;
            }

            double mid_radius = midrad - wall_thickness;
            double top_radius = endrad - wall_thickness;
            int base_offset = (int)wall_thickness;
            int[] x_choices = TreeHelper.range(x - base_offset, x + base_offset, 1);
            int start_x = TreeHelper.choice(this.random, x_choices);
            int[] z_choices = TreeHelper.range(z - base_offset, z + base_offset, 1);
            int start_z = TreeHelper.choice(this.random, z_choices);
            this.taperedCylinder(
                new int[]{start_x, starty, start_z}, new int[]{x, midy, z}, base_radius, mid_radius, this.treeTrunkFillerBlock, this.treeTrunkFillerMetadata
            );
            int hollow_top_y = (int)(topy + trunkradius + 1.5);
            this.taperedCylinder(
                new int[]{x, midy, z}, new int[]{x, hollow_top_y, z}, mid_radius, top_radius, this.treeTrunkFillerBlock, this.treeTrunkFillerMetadata
            );
        }
    }
}
