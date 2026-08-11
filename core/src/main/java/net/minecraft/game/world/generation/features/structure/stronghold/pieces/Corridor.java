package net.minecraft.game.world.generation.features.structure.stronghold.pieces;

import java.util.List;
import java.util.Random;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.structure.LegacyStructureBoundingBox;
import net.minecraft.game.world.generation.features.structure.LegacyStructurePiece;
import net.minecraft.game.world.generation.features.structure.stronghold.Stronghold;
import net.minecraft.game.world.generation.features.structure.stronghold.StrongholdPiece;

public class Corridor extends StrongholdPiece {
    private final int corridorLength;

    public Corridor(int id, Random random2, LegacyStructureBoundingBox bb, int rot) {
        super(id);
        this.rotation = rot;
        this.boundingBox = bb;
        this.corridorLength = rot != 2 && rot != 0 ? bb.getXSize() : bb.getZSize();
    }

    @Override
    public void buildComponent(LegacyStructurePiece componente, List<LegacyStructurePiece> pcs, Random rand) {
    }

    public static LegacyStructureBoundingBox getBounds(List<LegacyStructurePiece> pcs, Random rand, int x, int y, int z, int id) {
        LegacyStructureBoundingBox bb = LegacyStructureBoundingBox.addToBB(x, y, z, -1, -1, 0, 5, 5, 4, id);
        LegacyStructurePiece piece = LegacyStructurePiece.findIntersecting(pcs, bb);
        if (piece == null) {
            return null;
        }

        if (piece.getBoundingBox().minY == bb.minY) {
            for (int i = 3; i >= 1; i--) {
                bb = LegacyStructureBoundingBox.addToBB(x, y, z, -1, -1, 0, 5, 5, i - 1, id);
                if (!piece.getBoundingBox().intersectsWith(bb)) {
                    return LegacyStructureBoundingBox.addToBB(x, y, z, -1, -1, 0, 5, 5, i, id);
                }
            }
        }

        return null;
    }

    @Override
    public boolean addComponentParts(World world, Random rand, LegacyStructureBoundingBox bb) {
        if (this.isFlooded(world, bb)) {
            return false;
        }

        if (this.getAirRatio(world, bb) > 0.2F) {
            return false;
        }

        for (int length = 0; length < this.corridorLength; length++) {
            this.placeBlock(world, Stronghold.bricksId, rand.nextInt(3), 0, 0, length, bb);
            this.placeBlock(world, Stronghold.bricksId, rand.nextInt(3), 1, 0, length, bb);
            this.placeBlock(world, Stronghold.bricksId, rand.nextInt(3), 2, 0, length, bb);
            this.placeBlock(world, Stronghold.bricksId, rand.nextInt(3), 3, 0, length, bb);
            this.placeBlock(world, Stronghold.bricksId, rand.nextInt(3), 4, 0, length, bb);

            for (int z = 1; z <= 3; z++) {
                this.placeBlock(world, Stronghold.bricksId, rand.nextInt(3), 0, z, length, bb);
                this.placeBlock(world, 0, 0, 1, z, length, bb);
                this.placeBlock(world, 0, 0, 2, z, length, bb);
                this.placeBlock(world, 0, 0, 3, z, length, bb);
                this.placeBlock(world, Stronghold.bricksId, rand.nextInt(3), 4, z, length, bb);
            }

            this.placeBlock(world, Stronghold.bricksId, rand.nextInt(3), 0, 4, length, bb);
            this.placeBlock(world, Stronghold.bricksId, rand.nextInt(3), 1, 4, length, bb);
            this.placeBlock(world, Stronghold.bricksId, rand.nextInt(3), 2, 4, length, bb);
            this.placeBlock(world, Stronghold.bricksId, rand.nextInt(3), 3, 4, length, bb);
            this.placeBlock(world, Stronghold.bricksId, rand.nextInt(3), 4, 4, length, bb);
        }

        return true;
    }
}
