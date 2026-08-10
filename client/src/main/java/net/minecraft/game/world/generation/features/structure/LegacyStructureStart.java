package net.minecraft.game.world.generation.features.structure;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import net.minecraft.game.world.World;

public abstract class LegacyStructureStart {
    protected LinkedList<LegacyStructurePiece> pieces = new LinkedList<>();
    protected LegacyStructureBoundingBox boundingBox;
    private int chunkX;
    private int chunkZ;

    public LegacyStructureStart() {
    }

    public LegacyStructureStart(int chunkX, int chunkZ) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public LegacyStructureBoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    public LinkedList<LegacyStructurePiece> getPieces() {
        return this.pieces;
    }

    public void generateStructure(World world, Random rand, LegacyStructureBoundingBox bb) {
        Iterator<LegacyStructurePiece> iterator = this.pieces.iterator();

        try {
            while (iterator.hasNext()) {
                LegacyStructurePiece piece = iterator.next();
                if (piece.getBoundingBox().intersectsWith(bb) && !piece.addComponentParts(world, rand, bb)) {
                    iterator.remove();
                }
            }
        } catch (Exception var6) {
        }
    }

    protected void updateBoundingBox() {
        this.boundingBox = LegacyStructureBoundingBox.getNewBB();

        for (LegacyStructurePiece piece : this.pieces) {
            this.boundingBox.expand(piece.getBoundingBox());
        }
    }

    protected void markAvailableHeight(World world, Random random, int relYoffset) {
        int min = world.getSeaLevel() - 1 - relYoffset;
        int ySize1 = this.boundingBox.getYSize() + 1;
        if (ySize1 < min) {
            ySize1 += random.nextInt(min - ySize1);
        }

        int yoffset = ySize1 - this.boundingBox.maxY;
        this.boundingBox.offset(0, yoffset, 0);

        for (LegacyStructurePiece comp : this.pieces) {
            comp.getBoundingBox().offset(0, yoffset, 0);
        }
    }

    protected void setRandomHeight(World world, Random random, int min, int max) {
        int offset = max - min + 1 - this.boundingBox.getYSize();
        int value;
        if (offset > 1) {
            value = min + random.nextInt(offset);
        } else {
            value = min;
        }

        int yoffset = value - this.boundingBox.minY;
        this.boundingBox.offset(0, yoffset, 0);

        for (LegacyStructurePiece comp : this.pieces) {
            comp.getBoundingBox().offset(0, yoffset, 0);
        }
    }

    public int getChunkX() {
        return this.chunkX;
    }

    public void setChunkX(int chunkPosX) {
        this.chunkX = chunkPosX;
    }

    public int getChunkZ() {
        return this.chunkZ;
    }

    public void setChunkZ(int chunkPosZ) {
        this.chunkZ = chunkPosZ;
    }
}
