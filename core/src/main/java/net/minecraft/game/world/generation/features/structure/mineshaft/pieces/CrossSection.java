package net.minecraft.game.world.generation.features.structure.mineshaft.pieces;

import java.util.List;
import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.structure.LegacyStructureBoundingBox;
import net.minecraft.game.world.generation.features.structure.LegacyStructurePiece;
import net.minecraft.game.world.generation.features.structure.mineshaft.MineshaftPiece;

public class CrossSection extends MineshaftPiece {
    private final int corridorDirection;
    private final boolean isMultipleFloors;

    public CrossSection(int id, Random rand, LegacyStructureBoundingBox bb, int dir) {
        super(id);
        this.corridorDirection = dir;
        this.boundingBox = bb;
        this.isMultipleFloors = bb.getYSize() > 3;
    }

    public static LegacyStructureBoundingBox findValidPlacement(List<LegacyStructurePiece> pieces, Random random, int x0, int y0, int z0, int mode) {
        LegacyStructureBoundingBox bb = new LegacyStructureBoundingBox(x0, y0, z0, x0, y0 + 2, z0);
        if (random.nextInt(4) == 0) {
            bb.maxY += 4;
        }

        switch (mode) {
            case 0:
                bb.minX = x0 - 1;
                bb.maxX = x0 + 3;
                bb.maxZ = z0 + 4;
                break;
            case 1:
                bb.minX = x0 - 4;
                bb.minZ = z0 - 1;
                bb.maxZ = z0 + 3;
                break;
            case 2:
                bb.minX = x0 - 1;
                bb.maxX = x0 + 3;
                bb.minZ = z0 - 4;
                break;
            case 3:
                bb.maxX = x0 + 4;
                bb.minZ = z0 - 1;
                bb.maxZ = z0 + 3;
        }

        return LegacyStructurePiece.findIntersecting(pieces, bb) != null ? null : bb;
    }

    @Override
    public void buildComponent(LegacyStructurePiece comp, List<LegacyStructurePiece> pieces, Random random) {
        int type = this.getComponentType();
        switch (this.corridorDirection) {
            case 0:
                getNextComponent(comp, pieces, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, 0, type);
                getNextComponent(comp, pieces, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, 1, type);
                getNextComponent(comp, pieces, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, 3, type);
                break;
            case 1:
                getNextComponent(comp, pieces, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, 2, type);
                getNextComponent(comp, pieces, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, 0, type);
                getNextComponent(comp, pieces, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, 1, type);
                break;
            case 2:
                getNextComponent(comp, pieces, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, 2, type);
                getNextComponent(comp, pieces, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, 1, type);
                getNextComponent(comp, pieces, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, 3, type);
                break;
            case 3:
                getNextComponent(comp, pieces, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, 2, type);
                getNextComponent(comp, pieces, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, 0, type);
                getNextComponent(comp, pieces, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, 3, type);
        }

        if (this.isMultipleFloors) {
            if (random.nextBoolean()) {
                getNextComponent(comp, pieces, random, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ - 1, 2, type);
            }

            if (random.nextBoolean()) {
                getNextComponent(comp, pieces, random, this.boundingBox.minX - 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, 1, type);
            }

            if (random.nextBoolean()) {
                getNextComponent(comp, pieces, random, this.boundingBox.maxX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, 3, type);
            }

            if (random.nextBoolean()) {
                getNextComponent(comp, pieces, random, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.maxZ + 1, 0, type);
            }
        }
    }

    @Override
    public boolean addComponentParts(World world, Random random, LegacyStructureBoundingBox bb) {
        if (this.isFlooded(world, bb)) {
            return false;
        }

        if (!(this.getFractionSolid(world, bb) < 0.4F) && !(this.getFractionExposed(world, bb) > 0.5F)) {
            if (this.isMultipleFloors) {
                this.fill(
                    world,
                    bb,
                    this.boundingBox.minX + 1,
                    this.boundingBox.minY,
                    this.boundingBox.minZ,
                    this.boundingBox.maxX - 1,
                    this.boundingBox.minY + 3 - 1,
                    this.boundingBox.maxZ,
                    0,
                    0,
                    false
                );
                this.fill(
                    world,
                    bb,
                    this.boundingBox.minX,
                    this.boundingBox.minY,
                    this.boundingBox.minZ + 1,
                    this.boundingBox.maxX,
                    this.boundingBox.minY + 3 - 1,
                    this.boundingBox.maxZ - 1,
                    0,
                    0,
                    false
                );
                this.fill(
                    world,
                    bb,
                    this.boundingBox.minX + 1,
                    this.boundingBox.maxY - 2,
                    this.boundingBox.minZ,
                    this.boundingBox.maxX - 1,
                    this.boundingBox.maxY,
                    this.boundingBox.maxZ,
                    0,
                    0,
                    false
                );
                this.fill(
                    world,
                    bb,
                    this.boundingBox.minX,
                    this.boundingBox.maxY - 2,
                    this.boundingBox.minZ + 1,
                    this.boundingBox.maxX,
                    this.boundingBox.maxY,
                    this.boundingBox.maxZ - 1,
                    0,
                    0,
                    false
                );
                this.fill(
                    world,
                    bb,
                    this.boundingBox.minX + 1,
                    this.boundingBox.minY + 3,
                    this.boundingBox.minZ + 1,
                    this.boundingBox.maxX - 1,
                    this.boundingBox.minY + 3,
                    this.boundingBox.maxZ - 1,
                    0,
                    0,
                    false
                );
            } else {
                this.fill(
                    world,
                    bb,
                    this.boundingBox.minX + 1,
                    this.boundingBox.minY,
                    this.boundingBox.minZ,
                    this.boundingBox.maxX - 1,
                    this.boundingBox.maxY,
                    this.boundingBox.maxZ,
                    0,
                    0,
                    false
                );
                this.fill(
                    world,
                    bb,
                    this.boundingBox.minX,
                    this.boundingBox.minY,
                    this.boundingBox.minZ + 1,
                    this.boundingBox.maxX,
                    this.boundingBox.maxY,
                    this.boundingBox.maxZ - 1,
                    0,
                    0,
                    false
                );
            }

            this.fill(
                world,
                bb,
                this.boundingBox.minX + 1,
                this.boundingBox.minY,
                this.boundingBox.minZ + 1,
                this.boundingBox.minX + 1,
                this.boundingBox.maxY,
                this.boundingBox.minZ + 1,
                BlockList.wood.id,
                0,
                false
            );
            this.fill(
                world,
                bb,
                this.boundingBox.minX + 1,
                this.boundingBox.minY,
                this.boundingBox.maxZ - 1,
                this.boundingBox.minX + 1,
                this.boundingBox.maxY,
                this.boundingBox.maxZ - 1,
                BlockList.wood.id,
                0,
                false
            );
            this.fill(
                world,
                bb,
                this.boundingBox.maxX - 1,
                this.boundingBox.minY,
                this.boundingBox.minZ + 1,
                this.boundingBox.maxX - 1,
                this.boundingBox.maxY,
                this.boundingBox.minZ + 1,
                BlockList.wood.id,
                0,
                false
            );
            this.fill(
                world,
                bb,
                this.boundingBox.maxX - 1,
                this.boundingBox.minY,
                this.boundingBox.maxZ - 1,
                this.boundingBox.maxX - 1,
                this.boundingBox.maxY,
                this.boundingBox.maxZ - 1,
                BlockList.wood.id,
                0,
                false
            );

            for (int x = this.boundingBox.minX; x <= this.boundingBox.maxX; x++) {
                for (int z = this.boundingBox.minZ; z <= this.boundingBox.maxZ; z++) {
                    int id = this.getBlock(world, x, this.boundingBox.minY - 1, z, bb);
                    if (id == 0) {
                        this.placeBlock(world, BlockList.planks.id, 0, x, this.boundingBox.minY - 1, z, bb);
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }
}
