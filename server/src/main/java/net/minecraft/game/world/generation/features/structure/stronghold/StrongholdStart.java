package net.minecraft.game.world.generation.features.structure.stronghold;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.structure.LegacyStructurePiece;
import net.minecraft.game.world.generation.features.structure.LegacyStructureStart;
import net.minecraft.game.world.generation.features.structure.stronghold.pieces.EntryStairs;

public class StrongholdStart extends LegacyStructureStart {
    public StrongholdStart(World world, Random rand, int cX, int cZ) {
        StrongholdPieces.prepareStructurePieces();
        EntryStairs start = new EntryStairs(world, 0, rand, (cX << 4) + 2, (cZ << 4) + 2);
        this.pieces.add(start);
        start.buildComponent(start, this.pieces, rand);
        ArrayList<LegacyStructurePiece> pcs = start.structureList;

        while (!pcs.isEmpty()) {
            int next = rand.nextInt(pcs.size());
            LegacyStructurePiece piece = pcs.remove(next);
            piece.buildComponent(start, this.pieces, rand);
        }

        this.updateBoundingBox();
    }
}
