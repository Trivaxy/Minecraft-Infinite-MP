package net.minecraft.game.world.generation.features.structure.mineshaft;

import java.util.List;
import java.util.Random;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.structure.LegacyStructureBoundingBox;
import net.minecraft.game.world.generation.features.structure.LegacyStructurePiece;
import net.minecraft.game.world.generation.features.structure.mineshaft.pieces.CrossSection;
import net.minecraft.game.world.generation.features.structure.mineshaft.pieces.Staircase;
import net.minecraft.game.world.generation.features.structure.mineshaft.pieces.Tunnel;

public class MineshaftPiece extends LegacyStructurePiece {
    protected MineshaftPiece(int compType) {
        super(compType);
    }

    protected static LegacyStructurePiece getRandomComponent(List<LegacyStructurePiece> pcs, Random rand, int x, int y, int z, int rot, int id) {
        int chance = rand.nextInt(100);
        if (chance >= 80) {
            LegacyStructureBoundingBox bb = CrossSection.findValidPlacement(pcs, rand, x, y, z, rot);
            if (bb != null) {
                return new CrossSection(id, rand, bb, rot);
            }
        } else if (chance >= 70) {
            LegacyStructureBoundingBox bb = Staircase.findValidPlacement(pcs, rand, x, y, z, rot);
            if (bb != null) {
                return new Staircase(id, rand, bb, rot);
            }
        } else {
            LegacyStructureBoundingBox bb = Tunnel.findValidPlacement(pcs, rand, x, y, z, rot);
            if (bb != null) {
                return new Tunnel(id, rand, bb, rot);
            }
        }

        return null;
    }

    protected static LegacyStructurePiece getNextComponent(
        LegacyStructurePiece piece, List<LegacyStructurePiece> pcs, Random rand, int x, int y, int z, int rot, int id
    ) {
        if (id > 8) {
            return null;
        }

        if (Math.abs(x - piece.getBoundingBox().minX) <= 80 && Math.abs(z - piece.getBoundingBox().minZ) <= 80) {
            LegacyStructurePiece piss = getRandomComponent(pcs, rand, x, y, z, rot, id + 1);
            if (piss != null) {
                pcs.add(piss);
                piss.buildComponent(piece, pcs, rand);
            }

            return piss;
        } else {
            return null;
        }
    }

    @Override
    public boolean addComponentParts(World world, Random rand, LegacyStructureBoundingBox bb) {
        return false;
    }
}
