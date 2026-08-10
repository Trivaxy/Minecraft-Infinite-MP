package net.minecraft.game.world.generation.features.structure.stronghold.pieces;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.structure.LegacyStructureBoundingBox;
import net.minecraft.game.world.generation.features.structure.LegacyStructurePiece;
import net.minecraft.game.world.generation.features.structure.stronghold.PieceWeight;

public class EntryStairs extends Stairs {
    public ArrayList<LegacyStructurePiece> structureList = new ArrayList<>();
    public PieceWeight weight;
    public TreasureRoom treasureRoom;
    public BattleRoom bossRoom;
    public World world;

    public EntryStairs(World world, int id, Random rand, int x, int z) {
        super(world, 0, rand, x, z);
        this.world = world;
    }

    @Override
    public boolean addComponentParts(World world, Random rand, LegacyStructureBoundingBox bb) {
        return false;
    }
}
