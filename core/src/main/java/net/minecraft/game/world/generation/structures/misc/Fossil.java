package net.minecraft.game.world.generation.structures.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.hot.Desert;
import net.minecraft.game.world.biomes.hot.Wasteland;
import net.minecraft.game.world.chunk.ChunkLoader;
import net.minecraft.game.world.generation.structures.SchematicStructure;
import net.minecraft.game.world.generation.structures.StructureBoundingBox;
import net.minecraft.game.world.generation.structures.StructureList;

public class Fossil extends SchematicStructure {
    int x0Abs;
    int z0Abs;
    private List<StructureBoundingBox> pieceAABBs = new ArrayList<>();
    public static final Fossil.FossilPiece fossilPieceSkull1 = new Fossil.FossilPiece(
        new int[][][]{
            {
                    {-1, -1, 524437, -1, -1},
                    {-1, 149, 149, 149, -1},
                    {-1, 524437, 524437, 524437, -1},
                    {-1, 524437, 524437, 524437, -1},
                    {-1, 524437, 524437, 524437, -1},
                    {-1, 149, 149, 149, -1},
                    {-1, -1, -1, -1, -1}
            },
            {
                    {-1, 149, 262293, 262293, -1},
                    {-1, -1, -1, -1, 262293},
                    {-1, -1, -1, -1, 262293},
                    {-1, -1, -1, -1, 262293},
                    {524437, -1, -1, -1, 262293},
                    {262293, -1, -1, -1, 262293},
                    {-1, 149, 149, -1, -1}
            },
            {
                    {-1, -1, 262293, 262293, -1},
                    {-1, -1, -1, -1, 262293},
                    {-1, -1, -1, -1, 262293},
                    {-1, -1, -1, -1, 262293},
                    {-1, -1, -1, -1, 262293},
                    {262293, -1, -1, -1, 262293},
                    {-1, -1, 149, 262293, -1}
            },
            {
                    {-1, -1, 262293, 262293, -1},
                    {-1, -1, -1, -1, 262293},
                    {-1, -1, -1, -1, 262293},
                    {-1, -1, -1, -1, 262293},
                    {-1, -1, -1, -1, 262293},
                    {262293, -1, -1, -1, 262293},
                    {-1, -1, 149, 262293, -1}
            },
            {
                    {-1, 149, 262293, 262293, -1},
                    {-1, -1, -1, -1, 262293},
                    {-1, -1, -1, -1, 262293},
                    {-1, -1, -1, -1, 262293},
                    {524437, -1, -1, -1, 262293},
                    {262293, -1, -1, -1, 262293},
                    {-1, 149, 149, -1, -1}
            },
            {
                    {-1, -1, -1, -1, -1},
                    {-1, 149, 149, 149, -1},
                    {-1, 524437, 524437, 524437, -1},
                    {-1, 524437, 524437, 524437, -1},
                    {-1, 524437, 524437, 524437, -1},
                    {-1, 149, 149, 149, -1},
                    {-1, -1, -1, -1, -1}
            }
        }
    );
    public static final Fossil.FossilPiece fossilPieceSkull2 = new Fossil.FossilPiece(
        new int[][][]{
            {{-1, -1, -1, -1, -1}, {-1, 149, 149, 149, -1}, {-1, 524437, 524437, 524437, -1}, {-1, 149, 149, 149, -1}, {-1, -1, -1, -1, -1}},
            {{-1, 149, 149, 149, -1}, {524437, -1, -1, -1, 262293}, {524437, -1, -1, -1, 262293}, {262293, -1, -1, -1, 262293}, {-1, 149, 149, 149, -1}},
            {{-1, -1, 149, 262293, -1}, {-1, -1, -1, -1, 262293}, {-1, -1, -1, -1, 262293}, {262293, -1, -1, -1, 262293}, {-1, -1, 262293, -1, 262293}},
            {{-1, -1, -1, 262293, -1}, {-1, -1, -1, -1, 262293}, {-1, -1, -1, -1, 262293}, {262293, -1, -1, -1, 262293}, {-1, -1, 262293, 149, -1}},
            {{-1, -1, 149, 262293, -1}, {-1, -1, -1, -1, 262293}, {-1, -1, -1, -1, 262293}, {262293, -1, -1, -1, 262293}, {-1, -1, 262293, -1, 262293}},
            {{-1, 149, 149, 149, -1}, {524437, -1, -1, -1, 262293}, {524437, -1, -1, -1, 262293}, {262293, -1, -1, -1, 262293}, {-1, 149, 149, 149, -1}},
            {{-1, -1, -1, -1, -1}, {-1, 149, 149, 149, -1}, {-1, 524437, 524437, 524437, -1}, {-1, 149, 149, 149, -1}, {-1, -1, -1, -1, -1}}
        }
    );
    public static final Fossil.FossilPiece fossilPieceSkull3 = new Fossil.FossilPiece(
        new int[][][]{
            {{-1, 149, 149, -1}, {-1, 524437, 524437, -1}, {-1, 524437, 524437, -1}, {-1, 524437, 524437, -1}, {149, 149, 149, 262293}},
            {{-1, -1, 262293, -1}, {-1, -1, -1, 262293}, {524437, -1, -1, 262293}, {524437, -1, -1, 262293}, {-1, 262293, -1, 262293}},
            {{-1, -1, 262293, -1}, {-1, -1, -1, 262293}, {-1, -1, -1, 262293}, {524437, -1, -1, 262293}, {-1, 262293, 149, 262293}},
            {{-1, -1, 262293, -1}, {-1, -1, -1, 262293}, {524437, -1, -1, 262293}, {524437, -1, -1, 262293}, {-1, 262293, -1, 262293}},
            {{-1, 149, 149, -1}, {-1, 524437, 524437, -1}, {-1, 524437, 524437, -1}, {-1, 524437, 524437, -1}, {149, 149, 149, 262293}}
        }
    );
    public static final Fossil.FossilPiece fossilPieceSkull4 = new Fossil.FossilPiece(
        new int[][][]{
            {{-1, -1, -1, -1}, {-1, 524437, 524437, -1}, {-1, 524437, 524437, -1}, {-1, 149, 149, -1}},
            {{-1, 149, 149, -1}, {524437, -1, -1, 262293}, {262293, -1, -1, 262293}, {524437, 262293, -1, 149}},
            {{-1, -1, 262293, -1}, {-1, -1, -1, 262293}, {262293, -1, -1, 262293}, {-1, 262293, 149, -1}},
            {{-1, 149, 149, -1}, {524437, -1, -1, 262293}, {262293, -1, -1, 262293}, {524437, 262293, -1, 149}},
            {{-1, -1, -1, -1}, {-1, 524437, 524437, -1}, {-1, 524437, 524437, -1}, {-1, 149, 149, -1}}
        }
    );
    public static final Fossil.FossilPiece fossilPieceBody1 = new Fossil.FossilPiece(
        new int[][][]{{{149, 149, -1}, {-1, -1, -1}}, {{-1, -1, 524437}, {-1, -1, 524437}}, {{149, 149, -1}, {-1, -1, -1}}}
    );
    public static final Fossil.FossilPiece fossilPieceBody2 = new Fossil.FossilPiece(
        new int[][][]{
            {{-1, 149, 149, -1}, {-1, -1, -1, -1}},
            {{262293, -1, -1, 262293}, {-1, -1, -1, -1}},
            {{-1, -1, -1, 524437}, {-1, -1, -1, 524437}},
            {{262293, -1, -1, 262293}, {-1, -1, -1, -1}},
            {{-1, 149, 149, -1}, {-1, -1, -1, -1}}
        }
    );
    public static final Fossil.FossilPiece fossilPieceBody3 = new Fossil.FossilPiece(
        new int[][][]{
            {{149, 149, 149, 262293}, {-1, -1, -1, -1}},
            {{262293, -1, -1, 262293}, {-1, -1, -1, -1}},
            {{-1, -1, -1, 262293}, {-1, -1, -1, -1}},
            {{-1, -1, -1, 524437}, {-1, -1, -1, 524437}},
            {{-1, -1, -1, 262293}, {-1, -1, -1, -1}},
            {{262293, -1, -1, 262293}, {-1, -1, -1, -1}},
            {{149, 149, 149, 262293}, {-1, -1, -1, -1}}
        }
    );
    public static final Fossil.FossilPiece fossilPieceBody4 = new Fossil.FossilPiece(
        new int[][][]{
            {{-1, 149, 149, 149, -1}, {-1, -1, -1, -1, -1}},
            {{262293, -1, -1, -1, 262293}, {-1, -1, -1, -1, -1}},
            {{262293, -1, -1, -1, 262293}, {-1, -1, -1, -1, -1}},
            {{-1, -1, -1, -1, 262293}, {-1, -1, -1, -1, -1}},
            {{-1, -1, -1, -1, 524437}, {-1, -1, -1, -1, 524437}},
            {{-1, -1, -1, -1, 262293}, {-1, -1, -1, -1, -1}},
            {{262293, -1, -1, -1, 262293}, {-1, -1, -1, -1, -1}},
            {{262293, -1, -1, -1, 262293}, {-1, -1, -1, -1, -1}},
            {{-1, 149, 149, 149, -1}, {-1, -1, -1, -1, -1}}
        }
    );
    public static final Fossil.FossilPiece[] fossilPieceSkulls = new Fossil.FossilPiece[]{
        fossilPieceSkull1, fossilPieceSkull2, fossilPieceSkull3, fossilPieceSkull4
    };
    public static final Fossil.FossilPiece[] fossilPieceBodys = new Fossil.FossilPiece[]{
        fossilPieceBody1, fossilPieceBody2, fossilPieceBody3, fossilPieceBody4
    };

    public Fossil(World world, int originChunkX, int originChunkZ, StructureList featureProvider) {
        super(world, originChunkX, originChunkZ, featureProvider);
        this.x0Abs = this.originChunkX - this.getRadius() << 4;
        this.z0Abs = this.originChunkZ - this.getRadius() << 4;
    }

    @Override
    public int getRadius() {
        return 1;
    }

    @Override
    public int getSpawnChance() {
        return 5;
    }

    @Override
    public boolean canSpawnHere(ChunkLoader chunkProvider, World world, Random rand, Biome biome, int chunkX, int chunkZ) {
        return biome instanceof Desert || biome instanceof Wasteland;
    }

    @Override
    public void generate(int chunkX, int chunkZ, short[] blockArray, byte[] metadata, Biome[] biomesForGeneration) {
        this.drawPieceOnGeneration(chunkX, chunkZ, blockArray, metadata);
    }

    @Override
    public void populate(World world, Random rand, int chunkX, int chunkZ) {
    }

    @Override
    public int getHeight() {
        return this.world.getSeaLevel();
    }

    @Override
    public void generateSchematic(World world, Random rand, Biome biome, int chunkX, int chunkZ) {
        for (int[][] arr1 : this.schematic) {
            for (int[] arr2 : arr1) {
                Arrays.fill(arr2, -1);
            }
        }

        int maxSkeletons = 2 + rand.nextInt(4);
        int featureSizeBlocks = this.getRadius() * 2 + 1 << 4;

        for (int skeletons = 0; skeletons < maxSkeletons; skeletons++) {
            Fossil.FossilPiece skull = fossilPieceSkulls[rand.nextInt(fossilPieceSkulls.length)];
            Fossil.FossilPiece body = fossilPieceBodys[rand.nextInt(fossilPieceBodys.length)];
            int tailLength = 5 + rand.nextInt(15);
            boolean direction = rand.nextBoolean();
            StructureBoundingBox pieceAABB = null;
            int skullZ;
            int x1;
            int z1;
            int x2;
            int z2;
            int skullX;
            if (direction) {
                skullZ = 5 + rand.nextInt(featureSizeBlocks - 10);
                skullX = 3 + skull.length + 2 * tailLength + rand.nextInt(featureSizeBlocks - skull.length - 2 * tailLength);
                x1 = skullX - skull.length / 2 - 1 - 2 * tailLength;
                x2 = skullX + skull.length / 2;
                z1 = Math.min(skullZ - skull.width / 2, skullZ - body.width / 2);
                z2 = Math.max(skullZ + skull.width / 2, skullZ + body.width / 2);
            } else {
                skullX = 5 + rand.nextInt(featureSizeBlocks - 10);
                skullZ = 3 + skull.length + 2 * tailLength + rand.nextInt(featureSizeBlocks - skull.length - 2 * tailLength);
                z1 = skullZ - skull.width / 2 - 1 - 2 * tailLength;
                z2 = skullZ + skull.width / 2;
                x1 = Math.min(skullX - skull.width / 2, skullX - body.width / 2);
                x2 = Math.max(skullX + skull.width / 2, skullX + body.width / 2);
            }

            pieceAABB = new StructureBoundingBox(x1, 0, z1, x2, 7, z2);
            if (this.aabb.containsFully(pieceAABB) && !this.collideWithAny(pieceAABB)) {
                if (rand.nextBoolean()) {
                    skull.draw(this.schematic, skullX, 0, skullZ, direction);
                    if (direction) {
                        int x = skullX - skull.length / 2 - 1;

                        for (int i = 0; i < tailLength; i++) {
                            body.draw(this.schematic, x, 0, skullZ, direction);
                            x -= 2;
                        }
                    } else {
                        int z = skullZ - skull.length / 2 - 1;

                        for (int i = 0; i < tailLength; i++) {
                            body.draw(this.schematic, skullX, 0, z, direction);
                            z -= 2;
                        }
                    }
                } else {
                    skull.draw(this.schematic, skullX, this.surfaceHeight(skullX, skullZ), skullZ, direction);
                    if (direction) {
                        int x = skullX - skull.length / 2 - 1;

                        for (int i = 0; i < tailLength; i++) {
                            body.draw(this.schematic, x, this.surfaceHeight(x, skullZ), skullZ, direction);
                            x -= 2;
                        }
                    } else {
                        int z = skullZ - skull.length / 2 - 1;

                        for (int i = 0; i < tailLength; i++) {
                            body.draw(this.schematic, skullX, this.surfaceHeight(skullX, z), z, direction);
                            z -= 2;
                        }
                    }
                }

                this.pieceAABBs.add(pieceAABB);
            }
        }
    }

    private int surfaceHeight(int x, int z) {
        int h = this.world.getSurfaceHeightmap(x + this.x0Abs, z + this.z0Abs) - this.world.getSeaLevel();
        if (h < 0) {
            h = 0;
        }

        return h;
    }

    private boolean collideWithAny(StructureBoundingBox aabb) {
        for (StructureBoundingBox pieceAABB : this.pieceAABBs) {
            if (pieceAABB.collidesWith(aabb)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int getY0() {
        return this.world.getSeaLevel();
    }

    public static class FossilPiece {
        private final int[][][] schematic;
        int width;
        int length;
        int height;

        public FossilPiece(int[][][] schematic) {
            this.schematic = schematic;
            this.width = this.schematic.length;
            this.length = this.schematic[0].length;
            this.height = this.schematic[0][0].length;
        }

        private int rotateX(int x, int z, boolean rotated) {
            return rotated ? z : x;
        }

        private int rotateZ(int x, int z, boolean rotated) {
            return rotated ? x : z;
        }

        public void draw(int[][][] schematic2, int x0, int y0, int z0, boolean rotated) {
            int cx0 = this.width >> 1;
            int cz0 = this.length >> 1;
            int cx = this.rotateX(cx0, cz0, rotated);
            int cz = this.rotateZ(cx0, cz0, rotated);

            for (int x = 0; x < this.width; x++) {
                for (int z = 0; z < this.length; z++) {
                    for (int y = 0; y < this.height; y++) {
                        int xx = this.rotateX(x, z, rotated);
                        int zz = this.rotateZ(x, z, rotated);
                        int s = this.schematic[x][z][y];
                        if (s >= 0) {
                            int blockID = s & 4095;
                            if (rotated && blockID == 149) {
                                int meta = s >> 12;
                                if (meta != 0) {
                                    meta = meta == 64 ? 128 : 64;
                                    s = (short)(blockID | meta << 12);
                                }
                            }

                            schematic2[x0 + xx - cx][z0 + zz - cz][y0 + y] = s;
                        }
                    }
                }
            }
        }
    }
}
