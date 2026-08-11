package net.minecraft.game.world.generation.structures.misc;

import java.util.Arrays;
import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.tileentity.container.BookcaseEntity;
import net.minecraft.game.block.tileentity.container.ChestEntity;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.biomes.ocean.FloatingIsland;
import net.minecraft.game.world.chunk.ChunkLoader;
import net.minecraft.game.world.generation.structures.SchematicStructure;
import net.minecraft.game.world.generation.structures.StructureList;

public class Shipwreck extends SchematicStructure {
    private int y0;
    public static final int[][][] wreck1 = new int[][][]{
        {
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                {-1, -1, -1, -1, 5, 17, 17},
                {-1, -1, -1, 5, 5, 262161, 36},
                {-1, -1, -1, 5, 20552, 262161, 36},
                {-1, -1, -1, 5, 20552, 262161, 36},
                {-1, -1, -1, 5, 20552, 262161, 17},
                {-1, -1, -1, 5, 20552},
                {-1, -1, -1, 5, 5},
                {-1, -1, -1, 5}
        },
        {
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                {-1, -1, -1, -1, 5, 17},
                {-1, -1, -1, 5, 5, 17, 17},
                {-1, -1, -1, 5, -1, 5},
                {-1, -1, 5, 5, -1, 5},
                {-1, -1, 5, -1, -1, 5},
                {-1, -1, 5},
                {-1, -1, 5},
                {-1, -1, 5},
                {-1, -1, 5},
                {-1, -1, 5}
        },
        {
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                {-1, -1, -1, 5, 5, 262161, 17},
                {-1, -1, 5, 47, -1, 5},
                {-1, -1, 5, -1, -1, 5},
                {-1, 5, -1, -1, -1, 5},
                {-1, 5},
                {-1, 5},
                {-1, 5},
                {-1, 5},
                new int[0],
                new int[0]
        },
        {
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                {-1, -1, -1, 5, 5, 262161, 36},
                {-1, -1, 5, -1, -1, 5},
                {-1, 5, -1, -1, -1, 5},
                {-1, 5, -1, -1, -1, 5},
                {-1, 5, -1, -1, -1, 5},
                {-1, 5},
                {-1, 5},
                {-1, 5},
                {-1, 5},
                {-1, 5}
        },
        {
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                {-1, -1, -1, 17, 17, 17, 36},
                {-1, -1, 262161, 5, -1, 5},
                {-1, 262161, 12342, -1, -1, 5},
                {262161, 5, -1, -1, -1, 5},
                {262161, 5, -1, -1, -1, 5, 17, 17, 17},
                {262161, 5},
                {262161, 5},
                {262161, 5},
                {262161, 5},
                {17, 5}
        },
        {
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                {-1, -1, -1, 5, 5, 262161, 36},
                {-1, -1, 5, -1, -1, 5},
                {-1, 5, -1, -1, -1, 5},
                {-1, 5, -1, -1, -1, 5},
                {-1, 5, -1, -1, -1, 5},
                {-1, 5, -1, -1, -1, 5},
                {-1, 5, -1, -1, -1, 12360},
                {-1, 5},
                {-1, 5},
                {-1, 5}
        },
        {
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                {-1, -1, -1, 5, 5, 262161, 17},
                {-1, -1, 5, 47, -1, 5},
                {-1, -1, 5, -1, -1, 5},
                {-1, 5, -1, -1, -1, 5},
                {-1, 5, -1, -1, -1, 5},
                {-1, 5, -1, -1, -1, 5},
                {-1, 5, -1, -1, -1, 5},
                {-1, 5},
                {-1, 5},
                new int[0]
        },
        {
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                {-1, -1, -1, -1, 5, 17},
                {-1, -1, -1, 5, 5, 17, 17},
                {-1, -1, -1, 5, -1, 5},
                {-1, -1, 5, 5, -1, 5},
                {-1, -1, 5, -1, -1, 5},
                {-1, -1, 5, -1, -1, 5},
                {-1, -1, 5, -1, -1, 5},
                {-1, -1, 5, -1, -1, 5},
                {-1, -1, 5, -1, -1, 5},
                {-1, -1, 5}
        },
        {
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                {-1, -1, -1, -1, 5, 17, 17},
                {-1, -1, -1, 5, 5, 262161, 36},
                {-1, -1, -1, 5, 28744, 262161, 36},
                {-1, -1, -1, 5, 28744, 262161, 36},
                {-1, -1, -1, 5, 28744, 262161, 17},
                {-1, -1, -1, 5, 28744, 262161},
                {-1, -1, -1, 5, 5, 262161},
                {-1, -1, -1, -1, 5}
        }
    };
    public static final int[][][] wreck2 = new int[][][]{
        {
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                {-1, -1, -1, -1, -1, 262161, 36},
                {-1, -1, -1, -1, 5, 262161, 36},
                {-1, -1, -1, -1, 5, 262161, 36},
                {-1, -1, -1, 5, 262161, 36},
                {-1, -1, 5, 5, 262161, 36},
                {-1, -1, 5, 5, 262161, 36},
                {-1, -1, 5, 5, 262161, 36},
                {-1, -1, 5, 5, 262161, 36},
                {-1, -1, 17, 17, 17}
        },
        {
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                {-1, -1, -1, -1, 5, 262161, 36},
                {-1, -1, -1, -1, 5, 5},
                {-1, -1, -1, 5, -1, 5},
                {-1, -1, -1, 5, -1, 5},
                {-1, -1, 5, -1, 5},
                {-1, 5, -1, -1, 5},
                {-1, 5, -1, -1, 5},
                {-1, 5, -1, -1, 5},
                {-1, 5, -1, -1, 5},
                {-1, 262161, -1, -1, 5}
        },
        {
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                {-1, -1, -1, -1, 5, 262161, 36},
                {-1, -1, -1, 5, -1, 5},
                {-1, -1, -1, 5, -1, 5},
                {-1, -1, 5, -1, -1, 5},
                {-1, 5, 5, -1, -1, 5},
                {-1, 5, -1, -1, 5},
                {5, -1, -1, -1, 5},
                {5},
                {5},
                {5, -1, -1, -1, 5},
                {262161, -1, -1, -1, 5, -1, -1, -1, 262161}
        },
        {
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                {-1, -1, -1, 5, 5, 262161, 36},
                {-1, -1, 5, -1, -1, 5},
                {-1, -1, 5, -1, -1, 5},
                {-1, 5, -1, -1, -1, 5},
                {-1, 5, -1, -1, -1, 5},
                {-1, 5, -1, -1, 5},
                {5},
                {5},
                {5},
                {5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 5},
                {262161, -1, -1, -1, 5, -1, -1, -1, 262161, -1, -1, -1, -1, -1, 5}
        },
        {
                {-1, -1, -1, -1, -1, -1, 262161},
                {-1, -1, -1, -1, -1, -1, 262161},
                {-1, -1, -1, -1, -1, -1, 262161},
                {-1, -1, -1, -1, 36, 262161, 262161},
                {-1, -1, -1, 262161, 17, 17, 12341},
                {-1, -1, 262161, 12342, -1, 5},
                {-1, -1, 17, -1, -1, 8264},
                {-1, 262161, 5, -1, -1, 5},
                {-1, 262161, 17, -1, -1, 12341},
                {-1, 17, -1, -1, 5},
                {262161},
                {262161},
                {262161},
                {262161, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 5},
                {17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17}
        },
        {
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                {-1, -1, -1, 5, 5, 262161, 36},
                {-1, -1, 5, -1, -1, 5},
                {-1, -1, 5, -1, -1, 5},
                {-1, 5, -1, -1, -1, 5},
                {-1, 5, -1, -1, -1, 5},
                {-1, 5, -1, -1, 5},
                {5},
                {5},
                {5},
                {5},
                {-1, -1, -1, -1, -1, -1, -1, -1, 262161}
        },
        {
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                {-1, -1, -1, -1, 5, 262161, 36},
                {-1, -1, -1, 5, -1, 5},
                {-1, -1, -1, 5, -1, 5},
                {-1, -1, 5, -1, -1, 5},
                {-1, 5, 5, -1, -1, 5},
                {-1, 5, -1, -1, 5},
                {5, -1, -1, -1, 5},
                {5, -1, -1, -1, 5},
                {5},
                {5},
                {-1, -1, -1, -1, -1, -1, -1, -1, 262161}
        },
        {
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                {-1, -1, -1, -1, 5, 262161, 36},
                {-1, -1, -1, -1, 5},
                {-1, -1, -1, 5, -1, 5},
                {-1, -1, -1, 5, -1, 5},
                {-1, -1, 5, -1, 5},
                {-1, 5, -1, -1, 5},
                {-1, 5, -1, -1, 5},
                {-1, 5, -1, -1, 5},
                {-1, 5, -1, -1, 5},
                {-1, -1, -1, -1, -1, -1, -1, -1, 262161}
        },
        {
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                {-1, -1, -1, -1, -1, 262161, 36},
                {-1, -1, -1, -1, 5, 262161, 36},
                {-1, -1, -1, -1, 5, 262161, 36},
                {-1, -1, -1, 5, 262161, 36},
                {-1, -1, 5, 5, 262161, 36},
                {-1, -1, 5, 5, 262161, 36},
                {-1, -1, 5, 5, 262161, 36},
                {-1, -1, -1, 5, 262161},
                {-1, -1, -1, 17, 17, -1, -1, -1, 262161}
        }
    };
    public static final int[][][] wreck3 = new int[][][]{
        new int[0][],
        {
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                {-1, -1, -1, 5, 262161},
                {-1, -1, -1, 5, 262161},
                {-1, -1, -1, 5, 262161},
                {-1, -1, 5, 5, 262161},
                {-1, -1, 17, 17, 17, -1, -1, -1, -1, -1, -1, -1, -1, 131089},
                {-1, -1, 5, 5, 262161},
                {-1, -1, 5, 5, 262161, 17, 17, 17, 36},
                {-1, -1, 5, -1, 262161, 5, 5, 262161, 36},
                {-1, -1, -1, -1, 262161, 5, 20552, 262161, 36},
                {-1, -1, 5, -1, 262161, 5, 20552, 262161, 36},
                {-1, -1, 5, 5, 262161, 5, 5, 262161, 36},
                {-1, -1, 17, 17, 17, 17, 17, 17, 36}
        },
        {
                new int[0],
                new int[0],
                new int[0],
                {-1, -1, -1, 5, 262161},
                {-1, -1, -1, -1, 5},
                {-1, -1, -1, -1, 5},
                {-1, -1, -1, -1, 5},
                {-1, 5, -1, -1, 5},
                {-1, 17, -1, -1, 5, -1, -1, -1, -1, -1, -1, -1, -1, 131089},
                {-1, 5, -1, -1, 5},
                {-1, 5, -1, -1, 5, -1, -1, 131089},
                {-1, 5, -1, -1, -1, -1, -1, 5},
                {-1, 5, -1, -1, -1, -1, -1, 5},
                {-1, 5, -1, -1, -1, -1, -1, 5},
                {-1, 5, -1, -1, 5, -1, -1, 5},
                {-1, 131089, 5, 5, 131089, 5, 5, 131089, 36}
        },
        {
                new int[0],
                new int[0],
                {-1, -1, -1, 5, 131089},
                {-1, -1, 5, -1, 5},
                {-1, 5, -1, -1, 5},
                {-1, 5, -1, -1, 5},
                {5, -1, -1, -1, 5},
                {5, -1, -1, -1, 5},
                {131089, -1, -1, -1, 5, -1, -1, -1, -1, -1, -1, -1, -1, 131089},
                {5, -1, -1, -1, 5},
                {5, -1, -1, -1, 5, 5, 5, 131089},
                {5, -1, -1, -1, -1, -1, -1, 5},
                {5, -1, -1, -1, -1, -1, -1, 5},
                {5, -1, -1, -1, -1, -1, -1, 5},
                {5, -1, -1, -1, 5, -1, -1, 5},
                {-1, 131089, 5, 5, 131089, 5, 16456, 131089, 36}
        },
        {
                {-1, -1, -1, -1, 262161},
                {-1, -1, -1, -1, 262161},
                {-1, -1, -1, 17, 17},
                {-1, -1, 262161, -1, 5},
                {-1, 262161, -1, -1, 5},
                {-1, 262161},
                {262161},
                {262161},
                {262161, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 36},
                {262161, -1, -1, -1, 5},
                {262161, -1, -1, -1, 5, 5, 24648, 131089},
                {262161, -1, -1, -1, -1, -1, -1, 5},
                {262161, -1, -1, -1, -1, -1, -1, 5},
                {262161, -1, -1, -1, -1, -1, -1, 5, 17, 36},
                {262161, 8246, 5, -1, 5, -1, -1, 5},
                {-1, 17, 5, 5, 131089, 5, 16456, 131089, 36}
        },
        {
                new int[0],
                new int[0],
                {-1, -1, -1, 5, 131089},
                {-1, -1, 5, -1, 5},
                {-1, 5, -1, -1, 5},
                {-1, 5, -1, -1, 5},
                {5, -1, -1, -1, 5},
                {5, -1, -1, -1, 5},
                {131089, -1, -1, -1, 5, -1, -1, -1, 131089, -1, -1, -1, -1, 131089},
                {5, -1, -1, -1, 5},
                {5, -1, -1, -1, 5, 5, 24648, 131089},
                {5, -1, -1, -1, -1, -1, -1, 5},
                {5, -1, -1, -1, -1, -1, -1, 5},
                {5, -1, -1, -1, -1, -1, -1, 5},
                {5, -1, -1, -1, 5, -1, -1, 5},
                {-1, 131089, 5, 5, 131089, 5, 16456, 131089, 36}
        },
        {
                new int[0],
                new int[0],
                new int[0],
                {-1, -1, -1, 5, 262161},
                {-1, -1, 5, -1, 5},
                {-1, -1, 5, -1, 5},
                {-1, 5, -1, -1, 5},
                {-1, 5, -1, -1, 5},
                {-1, 17, -1, -1, 5, -1, -1, -1, 131089, -1, -1, -1, -1, 131089},
                {-1, 5, -1, -1, 5},
                {-1, 5, -1, -1, 5, 5, 5, 131089},
                {-1, 5, -1, -1, -1, -1, -1, 5},
                {-1, 5, -1, -1, -1, -1, -1, 5},
                {-1, 5, -1, -1, -1, -1, -1, 5},
                {-1, 5, -1, -1, 5, -1, -1, 5},
                {-1, 131089, 5, 5, 131089, 5, 5, 131089, 36}
        },
        {
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                {-1, -1, -1, 5, 262161},
                {-1, -1, -1, 5, 262161},
                {-1, -1, 5, 5, 262161},
                {-1, -1, 5, 5, 262161},
                {-1, -1, 17, 17, 17, -1, -1, -1, 131089, -1, -1, -1, -1, 131089},
                {-1, -1, 5, 5, 262161},
                {-1, -1, 5, 5, 262161, 17, 17, 17, 36},
                {-1, -1, 5, 5, 262161, 5, 5, 262161, 36},
                {-1, -1, 5, 5, 262161, 5, 28744, 262161, 36},
                {-1, -1, 5, 5, 262161, 5, 28744, 262161, 36},
                {-1, -1, 5, 5, 262161, 5, 5, 262161, 36},
                {-1, -1, 17, 17, 17, 17, 17, 17, 36}
        },
        {
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 131089},
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                new int[0]
        }
    };
    public static final int[][][] wreck4 = new int[][][]{
        new int[0][],
        new int[0][],
        {new int[0], new int[0], new int[0], new int[0], new int[0], {5}, new int[0], new int[0], {5}, {131089}, {5}, {5}, {5}, {5}, {131089}},
        {new int[0], new int[0], new int[0], new int[0], {5}, {5}, new int[0], new int[0], {5}, {131089}, {5}, {12360}, {12360}, {5}, {131089, 5}},
        {
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                {5},
                {5},
                {131089, -1, -1, -1, -1, -1, 131089},
                {5},
                {5},
                {5},
                {5},
                {131089, 5, 5}
        },
        {
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                {-1, -1, -1, 5, 17, 17},
                {-1, 5},
                {-1, 5, 5},
                {-1, 17, 17, -1, -1, -1, 131089, -1, -1, -1, -1, 131089},
                {-1, 5, 5},
                {-1, 5, 5},
                {-1, 5},
                {-1, 5},
                {17, 17, 17}
        },
        {
                new int[0],
                new int[0],
                new int[0],
                {-1, -1, -1, -1, 5, 17},
                {-1, 17, 17, 17, 17, 36},
                {-1, 5, 5, 5, 5, 36},
                {5, -1, -1, -1, 5},
                new int[0],
                new int[0],
                {-1, -1, -1, -1, -1, -1, 131089, -1, -1, -1, -1, 131089},
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                new int[0]
        },
        {
                new int[0],
                new int[0],
                {-1, 5, 5, 5, 5, 17},
                {-1, 5, 5, 5, 5},
                {131089, -1, -1, -1, 5},
                {5, -1, -1, -1, 5},
                {5},
                {5},
                new int[0],
                {-1, -1, -1, -1, -1, -1, 131089, -1, -1, -1, -1, 131089},
                new int[0],
                new int[0],
                {5},
                {5},
                {-1, -1, 5, 5, 36}
        },
        {
                {-1, -1, -1, -1, 262161, 36},
                {-1, -1, 17, 17, 17, 36},
                {-1, 17, 5, 12342, 5, 36},
                {17, 5, -1, -1, 5},
                {262161, 5, -1, -1, 5},
                {262161, -1, -1, -1, 5},
                {262161, -1, -1, 5},
                {262161},
                {262161},
                {17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17},
                {262161},
                {262161},
                {262161},
                {262161, -1, -1, 5},
                {17, 17, 17, 17, 17}
        },
        {
                new int[0],
                new int[0],
                {-1, 5, 5, 5, 5, 17},
                {-1, 5, 5, 5, 5},
                {131089, -1, -1, -1, 5},
                {5, -1, -1, -1, 5},
                {5, -1, -1, 5},
                {5, -1, -1, 5},
                {5, -1, -1, 5},
                {131089, -1, -1, 5, -1, -1, 131089},
                {5},
                {5, -1, -1, 5},
                {5, -1, -1, 5},
                {5, -1, -1, 5},
                {131089, 5, 5, 5, 36}
        },
        {
                new int[0],
                new int[0],
                new int[0],
                {-1, -1, -1, -1, 5, 17},
                {-1, 17, 17, 17, 17, 36},
                {-1, 5, 5, 5, 5, 36},
                {5, -1, -1, 5, 5, 36},
                {5, -1, -1, 5},
                {5, -1, -1, 5},
                {131089, -1, -1, 5, -1, -1, 131089},
                {5, -1, -1, 5},
                {5, -1, -1, 5},
                {5, -1, -1, 5},
                {5, -1, -1, 5},
                {131089, 5, 5, 5, 36}
        },
        {
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                {-1, 5, 5, 5, 17, 17},
                {-1, 5, 5, 5, 36},
                {-1, 5, 5, 5, 36},
                {-1, 17, 17, 17, 36, -1, 131089},
                {-1, 5, 5, 5, 36},
                {-1, 5, 28744, 5, 36},
                {-1, 5, 28744, 5, 36},
                {-1, 5, 5, 5, 36},
                {-1, 131089, 17, 17, 17}
        },
        {
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                {-1, -1, -1, -1, -1, -1, 131089},
                new int[0],
                new int[0],
                new int[0],
                new int[0],
                new int[0]
        },
        new int[0][],
        new int[0][]
    };
    public static final int[][][] wreck5 = new int[][][]{
        {
                new int[0],
                new int[0],
                {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 36, 131089},
                {-1, -1, -1, -1, -1, 262161},
                {-1, -1, -1, 5, -1, 262161},
                {-1, -1, 5, 5, 5, 262161, 36},
                {-1, -1, 17, 17, 17, 262161, 36},
                {-1, -1, 5, 5, 5, 262161},
                {-1, -1, 5, 5, 20552, 262161, 36},
                {-1, -1, 5, 5, 20552, 262161, 36},
                {-1, -1, 5, 5, 20552, 262161, 36},
                {-1, -1, 5, 5, 20552, 262161, 17},
                {-1, -1, -1, 5, 5, 262161, 5},
                new int[0],
                new int[0]
        },
        {
                new int[0],
                new int[0],
                {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 36, 131089},
                new int[0],
                new int[0],
                {-1, 5, -1, -1, -1, 5},
                {-1, 17, 36, 36, 36, 5},
                {-1, 5, -1, -1, -1, 5},
                {-1, 5, -1, -1, -1, 5},
                {-1, 5, -1, -1, -1, 5},
                {-1, 5, -1, -1, -1, 5},
                {-1, 5, -1, -1, -1, 5},
                {-1, -1, 5, -1, -1, 5},
                {-1, -1, -1, 5, 5, 131089, 5},
                new int[0]
        },
        {
                new int[0],
                new int[0],
                {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 131089},
                new int[0],
                {5},
                {5},
                {131089, -1, -1, -1, -1, 5},
                {5, -1, -1, -1, -1, 5},
                {5, -1, -1, -1, -1, 5},
                {5, -1, -1, -1, -1, 5},
                {5, -1, -1, -1, -1, 5},
                {5, -1, -1, -1, -1, 5},
                {-1, 5, -1, -1, -1, 5},
                {-1, -1, 5, 5, -1, 5},
                {-1, -1, -1, -1, 5, 131089, 5}
        },
        {
                new int[0],
                {-1, -1, -1, -1, -1, 5},
                {5, -1, -1, -1, -1, 5, -1, -1, -1, -1, -1, -1, 131089},
                {5},
                {5},
                {5},
                {131089},
                {5},
                {5, -1, -1, -1, -1, 5},
                {5, -1, -1, -1, -1, 5},
                {5, -1, -1, -1, -1, 5},
                {5, -1, -1, -1, -1, 5},
                {-1, 5, -1, -1, -1, 5},
                {-1, -1, 5, -1, -1, 5},
                {-1, -1, -1, 5, 16456, 131089, 36}
        },
        {
                {262161},
                {262161, -1, -1, -1, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36},
                {262161, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17},
                {262161, -1, -1, -1, -1, 5},
                {262161},
                {262161},
                {262161},
                {262161},
                {262161},
                {262161},
                {262161, 5, -1, -1, -1, 5},
                {262161, -1, -1, -1, -1, 4168},
                {-1, 17, 8246, -1, -1, 4168},
                {-1, -1, 17, -1, -1, 5},
                {5, 36, 36, 17, 17, 17, 36}
        },
        {
                new int[0],
                {5, -1, -1, -1, -1, 5},
                {5, -1, -1, -1, -1, 5, -1, -1, -1, -1, -1, -1, 131089},
                {5, -1, -1, -1, -1, 5},
                {5},
                {5},
                {131089},
                {5},
                {5},
                {5},
                {5, -1, -1, -1, -1, 5},
                {5, -1, -1, -1, -1, 12360},
                {-1, 5, -1, -1, -1, 12360},
                {-1, -1, 5, -1, -1, 5},
                {-1, -1, -1, 5, 16456, 131089, 36}
        },
        {
                new int[0],
                new int[0],
                {5, -1, -1, -1, -1, 5},
                {5},
                {5},
                {5},
                {131089},
                {5},
                {5},
                {5, -1, -1, -1, -1, 5},
                {5, -1, -1, -1, -1, 5},
                {5, -1, -1, -1, -1, 5},
                {-1, 5, -1, -1, -1, 5},
                {-1, -1, 5, 5, -1, 5},
                {-1, -1, -1, -1, 5, 131089, 5}
        },
        {
                new int[0],
                new int[0],
                {-1, -1, -1, -1, -1, 5},
                {-1, -1, -1, -1, -1, 5},
                {-1, 5, -1, -1, -1, 5},
                {-1, 5, -1, -1, -1, -1},
                {-1, 17, 36, 36, -1, -1},
                {-1, 5, -1, -1, -1, -1},
                {-1, 5, -1, -1, -1, 5},
                {-1, 5, -1, -1, -1, 5},
                {-1, 5, -1, -1, -1, 5},
                {-1, 5, -1, -1, -1, 5},
                {-1, -1, 5, -1, -1, 5},
                {-1, -1, -1, 5, 5, 131089, 5},
                new int[0]
        },
        {
                new int[0],
                {-1, -1, -1, -1, -1, 262161},
                {-1, -1, -1, 5, 28744, 262161, 36},
                {-1, -1, 5, 5, -1, 262161, 36},
                {-1, -1, 5, 5, 28744, 262161},
                {-1, -1, 5, 5, 5, 262161, 36},
                {-1, -1, 17, 17, 17, 262161, 36},
                {-1, -1, 5, 5, 5, 262161, 36},
                {-1, -1, 5, 5, 28744, 262161, 36},
                {-1, -1, 5, 5, 28744, 262161, 36},
                {-1, -1, 5, 5, 28744, 262161, 36},
                {-1, -1, 5, 5, 28744, 262161, 17},
                {-1, -1, -1, 5, 5, 262161, 5},
                new int[0],
                new int[0]
        }
    };
    public static final int[][][][] wrecks = new int[][][][]{wreck1, wreck2, wreck3, wreck4, wreck5};

    public Shipwreck(World world, int originChunkX, int originChunkZ, StructureList featureProvider) {
        super(world, originChunkX, originChunkZ, featureProvider);
        this.y0 = world.getSurfaceHeightmap((originChunkX << 4) + 8, (originChunkZ << 4) + 8);
    }

    @Override
    public int getHeight() {
        return 32;
    }

    protected int rotateX(int x, int z, boolean rotated) {
        return rotated ? z : x;
    }

    protected int rotateZ(int x, int z, boolean rotated) {
        return rotated ? x : z;
    }

    @Override
    public void generateSchematic(World world, Random rand, Biome biome, int chunkX, int chunkZ) {
        for (int[][] arr1 : this.schematic) {
            for (int[] arr2 : arr1) {
                Arrays.fill(arr2, -1);
            }
        }

        int[][][] piece = wrecks[rand.nextInt(wrecks.length)];
        boolean rotated = rand.nextBoolean();
        int pieceWidth = piece.length;
        int pieceLength = piece[2].length;
        int xO = rand.nextInt(17 - pieceWidth);
        int zO = rand.nextInt(17 - pieceLength);
        int x0 = this.rotateX(xO, zO, rotated);
        int z0 = this.rotateZ(xO, zO, rotated);

        for (int x = 0; x < piece.length; x++) {
            for (int z = 0; z < piece[x].length; z++) {
                for (int y = 0; y < piece[x][z].length; y++) {
                    int s = piece[x][z][y];
                    if (s >= 0) {
                        int blockID = s & 0xFF;
                        int meta = s >> 12;
                        if (rotated) {
                            meta = this.rotateMeta(blockID, meta);
                            s = (short)(blockID | meta << 12);
                        }

                        this.schematic[x0 + this.rotateX(x, z, rotated)][z0 + this.rotateZ(x, z, rotated)][y] = s;
                        if (blockID == 54 || blockID == 47) {
                            this.addSpecialBlock(x0 + this.rotateX(x, z, rotated), y, z0 + this.rotateZ(x, z, rotated), blockID, meta);
                        }
                    }
                }
            }
        }
    }

    protected int rotateMeta(int blockID, int meta) {
        switch (blockID) {
            case 17:
                if (meta == 16) {
                    meta = 32;
                } else if (meta == 32) {
                    meta = 16;
                }
                break;
            case 53:
            case 72:
                if (meta == 5) {
                    meta = 6;
                } else if (meta == 7) {
                    meta = 4;
                } else if (meta == 4) {
                    meta = 7;
                } else if (meta == 6) {
                    meta = 5;
                } else if (meta == 3) {
                    meta = 0;
                } else if (meta == 1) {
                    meta = 2;
                }
                break;
            case 54:
            case 65:
                meta ^= 6;
        }

        return meta;
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
        return 10;
    }

    @Override
    public boolean canSpawnHere(ChunkLoader chunkProvider, World world, Random rand, Biome biome, int chunkX, int chunkZ) {
        return biome == BiomeList.ocean || biome == BiomeList.frozenOcean || biome == BiomeList.warmOcean || biome instanceof FloatingIsland;
    }

    @Override
    public void onSpecialBlockSet(int x, int y, int z, int id, int meta) {
        switch (id) {
            case 47:
                BookcaseEntity chest1 = (BookcaseEntity)this.world.getTileEntity(x, y, z);
                if (chest1 != null && chest1.getSize() > 0) {
                    int ni = 4 + this.world.rand.nextInt(4);

                    for (int i = 0; i < ni; i++) {
                        ItemStack itemStack = new ItemStack(ItemList.book, this.world.rand.nextInt(7) + 1);
                        chest1.setSlotContents(this.world.rand.nextInt(chest1.getSize()), itemStack);
                    }
                }
                break;
            case 54:
                ChestEntity chest = (ChestEntity)this.world.getTileEntity(x, y, z);
                if (chest != null && chest.getSize() > 0) {
                    int ni = 4 + this.world.rand.nextInt(4);
                    int level = this.world.rand.nextInt(10);

                    for (int i = 0; i < ni; i++) {
                        ItemStack itemStack = this.getTreasure(level, this.world.rand);
                        chest.setSlotContents(this.world.rand.nextInt(chest.getSize()), itemStack);
                    }
                }
        }
    }

    protected ItemStack getTreasure(int level, Random rand) {
        if (level < 4) {
            switch (rand.nextInt(11)) {
                case 0:
                    return new ItemStack(ItemList.ingotIron, rand.nextInt(3) + 1);
                case 1:
                    return new ItemStack(ItemList.nameTag);
                case 2:
                    return new ItemStack(ItemList.bread);
                case 3:
                    return new ItemStack(ItemList.bowlSussy);
                case 4:
                    return new ItemStack(ItemList.wheat, rand.nextInt(3) + 1);
                case 5:
                    return new ItemStack(ItemList.coal, rand.nextInt(5) + 1);
                case 6:
                    return new ItemStack(ItemList.stick, rand.nextInt(16) + 1);
                case 7:
                    return new ItemStack(BlockList.wood, rand.nextInt(4) + 1);
                case 8:
                    return new ItemStack(BlockList.planks, rand.nextInt(8) + 1);
                case 9:
                    return new ItemStack(ItemList.bucketFish);
                default:
                    return new ItemStack(BlockList.torch, rand.nextInt(16) + 1);
            }
        } else if (level < 7) {
            switch (rand.nextInt(11)) {
                case 0:
                    return new ItemStack(ItemList.compass);
                case 1:
                    return new ItemStack(ItemList.mapEmpty);
                case 2:
                    return this.getTreasure(1, rand);
                case 3:
                default:
                    return new ItemStack(ItemList.bookAndQuill);
                case 4:
                    return new ItemStack(ItemList.helmetChain);
                case 5:
                    return new ItemStack(ItemList.plateChain);
                case 6:
                    return new ItemStack(ItemList.swordSteel);
                case 7:
                    return new ItemStack(ItemList.legsChain);
                case 8:
                    return new ItemStack(ItemList.bootsChain);
                case 9:
                    return new ItemStack(ItemList.spyGlass);
            }
        } else {
            switch (rand.nextInt(11)) {
                case 0:
                    return new ItemStack(ItemList.axeSteel);
                case 1:
                    return new ItemStack(ItemList.pickaxeSteel);
                case 2:
                    return new ItemStack(ItemList.ingotGold, rand.nextInt(3) + 1);
                case 3:
                    return new ItemStack(ItemList.shovelSteel);
                case 4:
                    if (rand.nextInt(10) == 0) {
                        return new ItemStack(ItemList.appleGold);
                    }
                case 5:
                    return new ItemStack(ItemList.sapphire, rand.nextInt(3) + 1);
                case 6:
                    return new ItemStack(ItemList.ruby, rand.nextInt(3) + 1);
                case 7:
                    return new ItemStack(ItemList.emerald, rand.nextInt(3) + 1);
                case 8:
                    return new ItemStack(ItemList.backpack);
                case 9:
                    return new ItemStack(ItemList.scroll, 1, rand.nextInt(6));
                default:
                    return new ItemStack(ItemList.diamond, rand.nextInt(2) + 1);
            }
        }
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
