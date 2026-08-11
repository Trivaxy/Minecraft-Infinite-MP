package net.minecraft.game.world.generation.features.structure.mineshaft.pieces;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.structure.LegacyStructureBoundingBox;
import net.minecraft.game.world.generation.features.structure.LegacyStructurePiece;
import net.minecraft.game.world.generation.features.structure.mineshaft.MineshaftPiece;

public class DirtRoom extends MineshaftPiece {
    private LinkedList<LegacyStructureBoundingBox> chidStructures = new LinkedList<>();

    public DirtRoom(int compType, Random random, int x0, int z0) {
        super(compType);
        this.boundingBox = new LegacyStructureBoundingBox(x0, 50, z0, x0 + 7 + random.nextInt(6), 54 + random.nextInt(6), z0 + 7 + random.nextInt(6));
    }

    @Override
    public void buildComponent(LegacyStructurePiece piece, List<LegacyStructurePiece> pcs, Random rand) {
        int type = this.getComponentType();
        int height = this.boundingBox.getYSize() - 3 - 1;
        if (height <= 0) {
            height = 1;
        }

        int i = 0;

        while (i < this.boundingBox.getXSize()) {
            i += rand.nextInt(this.boundingBox.getXSize());
            if (i + 3 > this.boundingBox.getXSize()) {
                break;
            }

            LegacyStructurePiece comp = getNextComponent(
                piece, pcs, rand, this.boundingBox.minX + i, this.boundingBox.minY + rand.nextInt(height) + 1, this.boundingBox.minZ - 1, 2, type
            );
            if (comp != null) {
                LegacyStructureBoundingBox bb = comp.getBoundingBox();
                this.chidStructures.add(new LegacyStructureBoundingBox(bb.minX, bb.minY, this.boundingBox.minZ, bb.maxX, bb.maxY, this.boundingBox.minZ + 1));
            }

            i += 4;
        }

        i = 0;

        while (i < this.boundingBox.getXSize()) {
            i += rand.nextInt(this.boundingBox.getXSize());
            if (i + 3 > this.boundingBox.getXSize()) {
                break;
            }

            LegacyStructurePiece comp = getNextComponent(
                piece, pcs, rand, this.boundingBox.minX + i, this.boundingBox.minY + rand.nextInt(height) + 1, this.boundingBox.maxZ + 1, 0, type
            );
            if (comp != null) {
                LegacyStructureBoundingBox bb = comp.getBoundingBox();
                this.chidStructures.add(new LegacyStructureBoundingBox(bb.minX, bb.minY, this.boundingBox.maxZ - 1, bb.maxX, bb.maxY, this.boundingBox.maxZ));
            }

            i += 4;
        }

        i = 0;

        while (i < this.boundingBox.getZSize()) {
            i += rand.nextInt(this.boundingBox.getZSize());
            if (i + 3 > this.boundingBox.getZSize()) {
                break;
            }

            LegacyStructurePiece comp = getNextComponent(
                piece, pcs, rand, this.boundingBox.minX - 1, this.boundingBox.minY + rand.nextInt(height) + 1, this.boundingBox.minZ + i, 1, type
            );
            if (comp != null) {
                LegacyStructureBoundingBox bb = comp.getBoundingBox();
                this.chidStructures.add(new LegacyStructureBoundingBox(this.boundingBox.minX, bb.minY, bb.minZ, this.boundingBox.minX + 1, bb.maxY, bb.maxZ));
            }

            i += 4;
        }

        i = 0;

        while (i < this.boundingBox.getZSize()) {
            i += rand.nextInt(this.boundingBox.getZSize());
            if (i + 3 > this.boundingBox.getZSize()) {
                break;
            }

            LegacyStructurePiece comp = getNextComponent(
                piece, pcs, rand, this.boundingBox.maxX + 1, this.boundingBox.minY + rand.nextInt(height) + 1, this.boundingBox.minZ + i, 3, type
            );
            if (comp != null) {
                LegacyStructureBoundingBox bb = comp.getBoundingBox();
                this.chidStructures.add(new LegacyStructureBoundingBox(this.boundingBox.maxX - 1, bb.minY, bb.minZ, this.boundingBox.maxX, bb.maxY, bb.maxZ));
            }

            i += 4;
        }
    }

    @Override
    public boolean addComponentParts(World world, Random rand, LegacyStructureBoundingBox bb) {
        if (this.isFlooded(world, bb)) {
            return false;
        }

        this.fill(
            world,
            bb,
            this.boundingBox.minX,
            this.boundingBox.minY,
            this.boundingBox.minZ,
            this.boundingBox.maxX,
            this.boundingBox.minY,
            this.boundingBox.maxZ,
            BlockList.susDirt.id,
            0,
            true
        );
        this.fill(
            world,
            bb,
            this.boundingBox.minX,
            this.boundingBox.minY + 1,
            this.boundingBox.minZ,
            this.boundingBox.maxX,
            Math.min(this.boundingBox.minY + 3, this.boundingBox.maxY),
            this.boundingBox.maxZ,
            0,
            0,
            false
        );

        for (LegacyStructureBoundingBox structureBoundingBox5 : this.chidStructures) {
            this.fill(
                world,
                bb,
                structureBoundingBox5.minX,
                structureBoundingBox5.maxY - 2,
                structureBoundingBox5.minZ,
                structureBoundingBox5.maxX,
                structureBoundingBox5.maxY,
                structureBoundingBox5.maxZ,
                0,
                0,
                false
            );
        }

        this.carveOut(
            world,
            bb,
            this.boundingBox.minX,
            this.boundingBox.minY + 4,
            this.boundingBox.minZ,
            this.boundingBox.maxX,
            this.boundingBox.maxY,
            this.boundingBox.maxZ,
            0
        );
        return true;
    }

    protected void carveOut(World world, LegacyStructureBoundingBox bb, int startX, int startY, int startZ, int endX, int endY, int endZ, int id) {
        float x0 = endX - startX + 1;
        float y0 = endY - startY + 1;
        float z0 = endZ - startZ + 1;
        float x1 = startX + x0 / 2.0F;
        float z1 = startZ + z0 / 2.0F;

        for (int y1 = startY; y1 <= endY; y1++) {
            float height = (y1 - startY) / y0;

            for (int realX = startX; realX <= endX; realX++) {
                float width = (realX - x1) / (x0 * 0.5F);

                for (int realZ = startZ; realZ <= endZ; realZ++) {
                    float length = (realZ - z1) / (z0 * 0.5F);
                    if (this.getBlock(world, realX, y1, realZ, bb) != 0) {
                        float size = width * width + height * height + length * length;
                        if (size <= 1.05F) {
                            this.placeBlock(world, id, 0, realX, y1, realZ, bb);
                        }
                    }
                }
            }
        }
    }
}
