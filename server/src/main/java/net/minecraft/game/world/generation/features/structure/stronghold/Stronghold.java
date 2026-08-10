package net.minecraft.game.world.generation.features.structure.stronghold;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.temperate.CherryGrove;
import net.minecraft.game.world.biomes.temperate.Plains;
import net.minecraft.game.world.generation.features.structure.LegacyStructure;
import net.minecraft.game.world.generation.features.structure.LegacyStructureStart;

public class Stronghold extends LegacyStructure {
    public static final int chance = 1024;
    public static final int minSeparation = 64;
    public static int bricksId;
    public static int bricksMeta;
    public static int bricksAlt1Id;
    public static int bricksAlt1Meta;
    public static int bricksAlt2Id;
    public static int bricksAlt2Meta;
    public static int ironFenceId;
    public static int ironFenceMeta;

    public Stronghold(World world) {
        this.world = world;
        bricksId = BlockList.stoneBrick.id;
        bricksMeta = 0;
        bricksAlt1Id = BlockList.stoneBrick.id;
        bricksAlt1Meta = 1;
        bricksAlt2Id = BlockList.stoneBrick.id;
        bricksAlt2Meta = 2;
        ironFenceId = BlockList.ironBars.id;
        ironFenceMeta = 0;
    }

    @Override
    protected boolean canSpawnStructureAtCoords(int cX, int cZ) {
        long seed = this.world.getSeed() + cX * 25117L + cZ * 151121L;
        Random rand = new Random(seed);
        Biome biome = this.world.getBiomeAt((cX << 4) + 8, (cZ << 4) + 8);
        return rand.nextInt(1024) == 0 && (biome instanceof Plains || biome instanceof CherryGrove);
    }

    @Override
    protected LegacyStructureStart getStructureStart(int x, int z) {
        StrongholdStart start = new StrongholdStart(this.world, rand, x, z);

        while (start.getPieces().isEmpty()) {
            start = new StrongholdStart(this.world, rand, x, z);
        }

        return start;
    }
}
