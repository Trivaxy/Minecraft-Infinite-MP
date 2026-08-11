package net.minecraft.game.world.generation.structures.misc;

import java.util.Arrays;
import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.tileentity.container.ChestEntity;
import net.minecraft.game.entity.mob.monster.boss.HarpyQueen;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.sky.SkyBiome;
import net.minecraft.game.world.chunk.ChunkLoader;
import net.minecraft.game.world.generation.features.sky.Cloud;
import net.minecraft.game.world.generation.structures.SchematicStructure;
import net.minecraft.game.world.generation.structures.StructureList;

public class Tower extends SchematicStructure {
    private Random rand = new Random();
    private int y0;
    private boolean spawnedBoss = false;
    public static final int[][][] array = new int[][][]{
        {
                {32772, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {32772, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {32772, 0, 0, 0, 0, 204867, 0, 0, 0, 221251, 204867, 0, 0, 0, 0, 0, 0},
                {32772, 24608, 24608, 24608, 24608, 196675, 0, 0, 0, 213059, 196675, 0, 0, 0, 0, 0, 0},
                {32772, 0, 0, 0, 24608, 196675, 0, 0, 0, 213059, 196675, 0, 0, 0, 0, 0, 0},
                {32772, 0, 0, 0, 24608, 196675, 0, 0, 0, 213059, 196675, 0, 0, 0, 0, 0, 0},
                {32772, 0, 0, 0, 24608, 196675, 0, 0, 0, 213059, 196675, 0, 0, 0, 0, 0, 0},
                {32772, 24608, 24608, 24608, 24608, 196675, 0, 0, 0, 213059, 196675, 0, 0, 0, 0, 0, 0},
                {32772, 0, 0, 0, 0, 208963, 0, 0, 0, 213059, 196675, 0, 0, 0, 0, 0, 0},
                {32772, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {32772, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        },
        {
                {32772, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {32772, 0, 0, 0, 213059, 204867, 0, 0, 0, 221251, 204867, 0, 0, 24600, 204867, 0, 0},
                {32772, 24599, 0, 0, 213059, 196675, 0, 0, 0, 213059, 196675, 0, 0, 548887, 196675, 0, 0},
                {32772, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 32800, 0, 548887, 196675, 0, 0},
                {32772, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 32800, 0, 548887, 196675, 0, 0},
                {32772, 41033, 34, 34, 41033, 41033, 41033, 34, 34, 41033, 41033, 32800, 0, 548887, 196675, 0, 0},
                {32772, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 32800, 0, 548887, 196675, 0, 0},
                {32772, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 32800, 0, 548887, 196675, 0, 0},
                {32772, 24599, 0, 0, 213059, 196675, 0, 0, 0, 225347, 208963, 0, 0, 548887, 196675, 0, 0},
                {32772, 0, 0, 0, 213059, 208963, 0, 0, 0, 213059, 196675, 0, 0, 24600, 208963, 0, 0},
                {32772, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        },
        {
                {32772, 0, 0, 0, 0, 196675, 0, 0, 0, 221251, 204867, 0, 0, 0, 0, 0, 0},
                {32772, 24599, 0, 0, 221251, 196675, 0, 0, 0, 213059, 196675, 0, 0, 548887, 204867, 0, 0},
                {32772, 286, 286, 286, 286, 286, 286, 286, 286, 286, 286, 286, 286, 286, 286, 196675, 0},
                {32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 32772, 32800, 0, 0, 217155, 196675, 0},
                {32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 32772, 0, 0, 0, 217155, 196675, 0},
                {32772, 20534, 0, 0, 0, 32772, 0, 0, 0, 0, 32772, 0, 0, 0, 217155, 196675, 0},
                {32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 32772, 0, 0, 0, 217155, 196675, 0},
                {32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 32772, 32800, 0, 0, 217155, 196675, 0},
                {32772, 286, 286, 286, 286, 286, 286, 286, 286, 286, 286, 286, 286, 286, 286, 196675, 0},
                {32772, 24599, 0, 0, 225347, 196675, 0, 0, 0, 225347, 208963, 0, 0, 548887, 208963, 0, 0},
                {32772, 0, 0, 0, 0, 196675, 0, 0, 0, 213059, 196675, 0, 0, 0, 0, 0, 0}
        },
        {
                {32772, 24608, 24608, 24608, 24608, 204867, 0, 0, 0, 221251, 204867, 0, 0, 0, 0, 0, 0},
                {32772, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 32800, 0, 548887, 204867, 0, 0},
                {32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 32772, 32800, 0, 0, 225347, 204867, 0},
                {32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 225347, 24599},
                {32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 217155, 24599},
                {32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 217155, 24599},
                {32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 217155, 24599},
                {32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 221251, 24599},
                {32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 32772, 32800, 0, 0, 221251, 208963, 0},
                {32772, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 32800, 0, 548887, 208963, 0, 0},
                {32772, 24608, 24608, 24608, 24608, 208963, 0, 0, 0, 225347, 208963, 0, 0, 0, 0, 0, 0}
        },
        {
                {32772, 0, 0, 0, 24608, 204867, 0, 0, 0, 221251, 204867, 0, 0, 0, 0, 0, 0},
                {32772, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 32800, 0, 548887, 204867, 0, 0},
                {32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 32772, 0, 0, 0, 225347, 204867, 0},
                {32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 225347, 24599},
                {32772, 0, 557079, 0, 0, 0, 0, 557079, 0, 0, 0, 0, 0, 0, 0, 548887, 24599},
                {32772, 0, 274499, 0, 0, 32772, 0, 274499, 0, 0, 32772, 0, 0, 0, 0, 548887, 24599},
                {32772, 274499, 0, 0, 0, 32772, 274499, 0, 0, 0, 32772, 0, 0, 0, 0, 548887, 24599},
                {32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 221251, 24599},
                {32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 32772, 0, 0, 0, 221251, 208963, 0},
                {32772, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 32800, 0, 548887, 208963, 0, 0},
                {32772, 0, 0, 0, 24608, 208963, 0, 0, 0, 225347, 208963, 0, 0, 0, 0, 0, 0}
        },
        {
                {32772, 0, 0, 0, 24608, 204867, 0, 0, 0, 221251, 204867, 0, 0, 0, 0, 0, 0},
                {32772, 41033, 34, 34, 41033, 41033, 41033, 34, 34, 41033, 41033, 32800, 0, 548887, 204867, 0, 0},
                {32772, 0, 0, 0, 0, 32772, 12342, 0, 0, 0, 32772, 0, 0, 0, 225347, 204867, 0},
                {32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 225347, 24599},
                {32772, 0, 0, 262211, 0, 0, 0, 0, 262211, 0, 0, 0, 0, 0, 0, 548887, 24599},
                {32772, 286, 286, 286, 286, 286, 286, 286, 286, 286, 32772, 12467, 0, 0, 0, 24600, 24599},
                {32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 548887, 24599},
                {32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 221251, 24599},
                {32772, 0, 0, 0, 0, 32772, 12342, 0, 0, 0, 32772, 0, 0, 0, 221251, 208963, 0},
                {32772, 0, 0, 41033, 41033, 41033, 41033, 34, 34, 41033, 41033, 32800, 0, 548887, 208963, 0, 0},
                {32772, 0, 0, 0, 24608, 208963, 0, 0, 0, 225347, 208963, 0, 0, 0, 0, 0, 0}
        },
        {
                {32772, 0, 0, 0, 24608, 204867, 0, 0, 0, 221251, 204867, 0, 0, 0, 0, 0, 0},
                {32772, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 32800, 0, 548887, 204867, 0, 0},
                {32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 32772, 0, 0, 0, 225347, 204867, 0},
                {32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 225347, 24599},
                {32772, 0, 0, 557079, 0, 0, 0, 0, 557079, 0, 0, 0, 0, 0, 0, 548887, 24599},
                {32772, 0, 0, 0, 270403, 0, 0, 0, 0, 270403, 0, 0, 0, 0, 0, 548887, 24599},
                {32772, 0, 0, 0, 0, 270403, 0, 0, 0, 0, 270403, 0, 0, 0, 0, 548887, 24599},
                {32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 221251, 24599},
                {32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 32772, 0, 0, 0, 221251, 208963, 0},
                {32772, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 32800, 0, 548887, 208963, 0, 0},
                {32772, 0, 0, 0, 24608, 208963, 0, 0, 0, 225347, 208963, 0, 0, 0, 0, 0, 0}
        },
        {
                {32772, 24608, 24608, 24608, 24608, 204867, 0, 0, 0, 221251, 204867, 0, 0, 0, 0, 0, 0},
                {32772, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 32800, 0, 548887, 204867, 0, 0},
                {32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 32772, 32800, 0, 0, 225347, 204867, 0},
                {32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 213059, 24599},
                {32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 213059, 24599},
                {32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 213059, 24599},
                {32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 213059, 24599},
                {32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 221251, 24599},
                {32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 32772, 32800, 0, 0, 221251, 208963, 0},
                {32772, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 32800, 0, 548887, 208963, 0, 0},
                {32772, 24608, 24608, 24608, 24608, 208963, 0, 0, 0, 225347, 208963, 0, 0, 0, 0, 0, 0}
        },
        {
                {32772, 0, 0, 0, 0, 200771, 0, 0, 0, 217155, 200771, 0, 0, 0, 0, 0, 0},
                {32772, 24599, 0, 0, 221251, 200771, 0, 0, 0, 221251, 204867, 0, 0, 548887, 204867, 0, 0},
                {32772, 286, 286, 286, 286, 286, 286, 286, 286, 286, 286, 286, 286, 286, 286, 200771, 0},
                {32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 32772, 32800, 0, 0, 213059, 200771, 0},
                {32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 32772, 0, 0, 0, 213059, 200771, 0},
                {32772, 12342, 0, 0, 0, 32772, 0, 0, 0, 0, 32772, 0, 0, 0, 213059, 200771, 0},
                {32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 32772, 0, 0, 0, 213059, 200771, 0},
                {32772, 0, 0, 0, 0, 32772, 0, 0, 0, 0, 32772, 32800, 0, 0, 213059, 200771, 0},
                {32772, 286, 286, 286, 286, 286, 286, 286, 286, 286, 286, 286, 286, 286, 286, 208963, 0},
                {32772, 24599, 0, 0, 225347, 208963, 0, 0, 0, 217155, 200771, 0, 0, 548887, 208963, 0, 0},
                {32772, 0, 0, 0, 0, 200771, 0, 0, 0, 225347, 208963, 0, 0, 0, 0, 0, 0}
        },
        {
                {32772, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {32772, 0, 0, 0, 221251, 204867, 0, 0, 0, 217155, 200771, 0, 0, 24600, 200771, 0, 0},
                {32772, 24599, 0, 0, 217155, 200771, 0, 0, 0, 221251, 204867, 0, 0, 548887, 200771, 0, 0},
                {32772, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 32800, 0, 548887, 200771, 0, 0},
                {32772, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 32800, 0, 548887, 200771, 0, 0},
                {32772, 41033, 34, 34, 41033, 41033, 41033, 34, 34, 41033, 41033, 32800, 0, 548887, 200771, 0, 0},
                {32772, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 32800, 0, 548887, 200771, 0, 0},
                {32772, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 41033, 32800, 0, 548887, 200771, 0, 0},
                {32772, 24599, 0, 0, 217155, 208963, 0, 0, 0, 217155, 200771, 0, 0, 548887, 200771, 0, 0},
                {32772, 0, 0, 0, 217155, 200771, 0, 0, 0, 225347, 208963, 0, 0, 24600, 208963, 0, 0},
                {32772, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        },
        {
                {32772, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {32772, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {32772, 0, 0, 0, 0, 204867, 0, 0, 0, 217155, 200771, 0, 0, 0, 0, 0, 0},
                {32772, 24608, 24608, 24608, 24608, 200771, 0, 0, 0, 217155, 200771, 0, 0, 0, 0, 0, 0},
                {32772, 0, 0, 0, 24608, 200771, 0, 0, 0, 217155, 200771, 0, 0, 0, 0, 0, 0},
                {32772, 0, 0, 0, 24608, 200771, 0, 0, 0, 217155, 200771, 0, 0, 0, 0, 0, 0},
                {32772, 0, 0, 0, 24608, 200771, 0, 0, 0, 217155, 200771, 0, 0, 0, 0, 0, 0},
                {32772, 24608, 24608, 24608, 24608, 200771, 0, 0, 0, 217155, 200771, 0, 0, 0, 0, 0, 0},
                {32772, 0, 0, 0, 0, 200771, 0, 0, 0, 225347, 208963, 0, 0, 0, 0, 0, 0},
                {32772, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {32772, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        }
    };

    public Tower(World world, int originChunkX, int originChunkZ, StructureList featureProvider) {
        super(world, originChunkX, originChunkZ, featureProvider);
        this.y0 = this.world.getSurfaceHeightmap((originChunkX << 4) + 8, (originChunkZ << 4) + 8) + 16;
    }

    @Override
    public int getHeight() {
        return 22;
    }

    @Override
    public void generateSchematic(World world, Random rand, Biome biome, int chunkX, int chunkZ) {
        for (int[][] arr1 : this.schematic) {
            for (int[] arr2 : arr1) {
                Arrays.fill(arr2, -1);
            }
        }

        int[][][] piece = array;
        int pieceWidth = piece.length;
        int pieceLength = piece[2].length;
        int xO = rand.nextInt(pieceWidth);
        int zO = rand.nextInt(pieceLength);

        for (int x = 0; x < piece.length; x++) {
            for (int z = 0; z < piece[x].length; z++) {
                for (int y = 0; y < piece[x][z].length; y++) {
                    int s = piece[x][z][y];
                    if (s >= 0) {
                        int blockID = s & 0xFF;
                        int meta = s >> 12;
                        this.schematic[xO + x][zO + z][y] = s;
                        if (blockID == 54 || blockID == 179 || blockID == 4) {
                            this.addSpecialBlock(xO + x, y, zO + z, blockID, meta);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onSpecialBlockSet(int x, int y, int z, int id, int meta) {
        switch (id) {
            case 54: {
                ChestEntity chest = (ChestEntity)this.world.getTileEntity(x, y, z);
                if (chest != null && chest.getSize() > 0) {
                    int ni = 4 + this.rand.nextInt(4);
                    int level = this.rand.nextInt(10);

                    for (int i = 0; i < ni; i++) {
                        ItemStack itemStack = this.getTreasure(level, this.rand);
                        chest.setSlotContents(this.rand.nextInt(chest.getSize()), itemStack);
                    }
                }
                break;
            }
            case 179: {
                ChestEntity chest = (ChestEntity)this.world.getTileEntity(x, y, z);
                if (chest != null && chest.getSize() > 0) {
                    int ni = 4 + this.rand.nextInt(4);

                    for (int i = 0; i < ni; i++) {
                        ItemStack itemStack = this.getSpecialTreasure(this.rand);
                        chest.setSlotContents(this.rand.nextInt(chest.getSize()), itemStack);
                    }
                }

                this.world.setBlockMetadata(x, y, z, 1);
                if (!this.spawnedBoss) {
                    HarpyQueen bitch = new HarpyQueen(this.world);
                    bitch.setPosition(x, y + 8, z);
                    this.world.spawnEntity(bitch);
                    this.spawnedBoss = true;
                }
            }
        }
    }

    protected ItemStack getTreasure(int level, Random rand) {
        if (level < 4) {
            switch (rand.nextInt(10)) {
                case 0:
                    return new ItemStack(ItemList.pouch);
                case 1:
                    return new ItemStack(ItemList.crossbow);
                case 2:
                    if (rand.nextInt(5) == 0) {
                        return new ItemStack(ItemList.pearl);
                    }
                case 3:
                    return new ItemStack(ItemList.helmetSteel);
                case 4:
                    return new ItemStack(ItemList.plateSteel);
                case 5:
                    return new ItemStack(ItemList.saddle);
                case 6:
                    return new ItemStack(ItemList.legsSteel);
                case 7:
                    return new ItemStack(ItemList.bootsSteel);
                case 8:
                    return new ItemStack(ItemList.splashPotion, 1, rand.nextInt(19));
                default:
                    return new ItemStack(ItemList.arrowPiercing, 1 + rand.nextInt(4));
            }
        } else {
            switch (rand.nextInt(9)) {
                case 0:
                    return new ItemStack(ItemList.recordSky);
                case 1:
                    return new ItemStack(ItemList.arrowCalming, rand.nextInt(4) + 1);
                case 2:
                    return new ItemStack(BlockList.blockAdamantine, rand.nextInt(3) + 1);
                case 3:
                    if (rand.nextInt(10) == 0) {
                        return new ItemStack(ItemList.appleGold);
                    }
                case 4:
                    return new ItemStack(ItemList.topaz, rand.nextInt(3) + 1);
                case 5:
                    return new ItemStack(ItemList.spyGlass);
                case 6:
                    return new ItemStack(ItemList.pouch, rand.nextInt(3) + 1);
                case 7:
                    return new ItemStack(ItemList.scroll, 1, rand.nextInt(6));
                default:
                    return new ItemStack(ItemList.diamond, rand.nextInt(2) + 1);
            }
        }
    }

    protected ItemStack getSpecialTreasure(Random rand) {
        switch (rand.nextInt(18)) {
            case 0:
                return new ItemStack(ItemList.pickaxeValkyrie);
            case 1:
                return new ItemStack(ItemList.axeValkyrie);
            case 2:
                return new ItemStack(ItemList.shovelValkyrie);
            case 3:
            default:
                return new ItemStack(ItemList.lifeShard);
            case 4:
                return new ItemStack(ItemList.swordValkyrie);
            case 5:
                return new ItemStack(ItemList.helmetValkyrie);
            case 6:
                return new ItemStack(ItemList.plateValkyrie);
            case 7:
                return new ItemStack(ItemList.legsValkyrie);
            case 8:
                return new ItemStack(ItemList.bootsValkyrie);
            case 9:
                return new ItemStack(ItemList.rosary);
            case 10:
                return new ItemStack(ItemList.elytra);
            case 11:
                return new ItemStack(ItemList.cloudStaff);
            case 12:
                return new ItemStack(ItemList.gabrielStaff);
            case 13:
                return new ItemStack(ItemList.gauntletUriel);
            case 14:
                return new ItemStack(ItemList.harp);
            case 15:
                return new ItemStack(ItemList.mace);
            case 16:
                return new ItemStack(ItemList.windAmulet);
        }
    }

    @Override
    public void drawSpecialBlocksForChunk(World world, Random rand, int chunkX, int chunkZ) {
        int pieceXoffs = this.getPieceX(chunkX) << 4;
        int pieceZoffs = this.getPieceZ(chunkZ) << 4;
        int y0 = this.y0;

        for (SchematicStructure.StructureBlockData s : this.specialBlocks) {
            if (s.x >= pieceXoffs && s.x < pieceXoffs + 16 && s.z >= pieceZoffs && s.z < pieceZoffs + 16) {
                int xAbs = this.featureAABB.x1 + s.x;
                int zAbs = this.featureAABB.z1 + s.z;
                int yAbs = y0 + s.y;
                world.setBlockAndMetadata(xAbs, yAbs, zAbs, s.blockID, s.blockMetadata);
                this.onSpecialBlockSet(xAbs, yAbs, zAbs, s.blockID, s.blockMetadata);
                if (s.blockID == 4 && s.y == 0 && world.getBlock(xAbs, this.y0 - 3, zAbs) == null) {
                    Cloud.SMALL_CLOUD.place(world, rand, xAbs, this.y0 - 3, zAbs);
                }
            }
        }
    }

    @Override
    public int getY0() {
        return this.y0;
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
        return biome instanceof SkyBiome
            && this.y0 > world.getSeaLevel() + world.getSeaLevel() / 4
            && this.y0 < world.getWorldHeight() - (this.getHeight() + 16);
    }

    @Override
    public void generate(int chunkX, int chunkZ, short[] blockArray, byte[] metadata, Biome[] biomesForGeneration) {
        this.drawPieceOnGeneration(chunkX, chunkZ, blockArray, metadata);
    }

    @Override
    public void populate(World world, Random rand, int chunkX, int chunkZ) {
        this.drawSpecialBlocksForChunk(world, rand, chunkX, chunkZ);
    }
}
