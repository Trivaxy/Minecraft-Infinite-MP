package net.minecraft.game.world.generation.features.structure.mineshaft.pieces;

import java.util.List;
import java.util.Random;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.structure.LegacyStructureBoundingBox;
import net.minecraft.game.world.generation.features.structure.LegacyStructurePiece;
import net.minecraft.game.world.generation.features.structure.mineshaft.MineshaftPiece;

public class Staircase extends MineshaftPiece {
    public Staircase(int type, Random random, LegacyStructureBoundingBox bb, int dir) {
        super(type);
        this.rotation = dir;
        this.boundingBox = bb;
    }

    public static LegacyStructureBoundingBox findValidPlacement(List<LegacyStructurePiece> list0, Random random1, int i2, int i3, int i4, int i5) {
        LegacyStructureBoundingBox bb = new LegacyStructureBoundingBox(i2, i3 - 5, i4, i2, i3 + 2, i4);
        switch (i5) {
            case 0:
                bb.maxX = i2 + 2;
                bb.maxZ = i4 + 8;
                break;
            case 1:
                bb.minX = i2 - 8;
                bb.maxZ = i4 + 2;
                break;
            case 2:
                bb.maxX = i2 + 2;
                bb.minZ = i4 - 8;
                break;
            case 3:
                bb.maxX = i2 + 8;
                bb.maxZ = i4 + 2;
        }

        return LegacyStructurePiece.findIntersecting(list0, bb) != null ? null : bb;
    }

    @Override
    public void buildComponent(LegacyStructurePiece comp, List<LegacyStructurePiece> pieces, Random random) {
        int typ = this.getComponentType();
        switch (this.rotation) {
            case 0:
                getNextComponent(comp, pieces, random, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.maxZ + 1, 0, typ);
                break;
            case 1:
                getNextComponent(comp, pieces, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ, 1, typ);
                break;
            case 2:
                getNextComponent(comp, pieces, random, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ - 1, 2, typ);
                break;
            case 3:
                getNextComponent(comp, pieces, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ, 3, typ);
        }
    }

    @Override
    public boolean addComponentParts(World world, Random random, LegacyStructureBoundingBox bb) {
        if (this.isFlooded(world, bb)) {
            return false;
        }

        this.fill(world, bb, 0, 5, 0, 2, 7, 1, 0, 0, false);
        this.fill(world, bb, 0, 0, 7, 2, 2, 8, 0, 0, false);

        for (int i4 = 0; i4 < 5; i4++) {
            this.fill(world, bb, 0, 5 - i4 - (i4 < 4 ? 1 : 0), 2 + i4, 2, 7 - i4, 2 + i4, 0, 0, false);
        }

        return true;
    }
}
