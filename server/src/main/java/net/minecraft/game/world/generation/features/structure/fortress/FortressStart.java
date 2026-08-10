package net.minecraft.game.world.generation.features.structure.fortress;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.structure.LegacyStructurePiece;
import net.minecraft.game.world.generation.features.structure.LegacyStructureStart;
import net.minecraft.game.world.generation.features.structure.fortress.pieces.EntranceBridge;

class FortressStart extends LegacyStructureStart {
    public FortressStart(World world, Random rand, int x, int z) {
        EntranceBridge startPiece = new EntranceBridge(rand, (x << 4) + 2, (z << 4) + 2);
        this.pieces.add(startPiece);
        startPiece.buildComponent(startPiece, this.pieces, rand);
        ArrayList<?> pcs = startPiece.maxPieces;

        while (!pcs.isEmpty()) {
            int next = rand.nextInt(pcs.size());
            LegacyStructurePiece piss = (LegacyStructurePiece)pcs.remove(next);
            piss.buildComponent(startPiece, this.pieces, rand);
        }

        this.updateBoundingBox();
        this.setRandomHeight(world, rand, 48, 70);
    }
}
