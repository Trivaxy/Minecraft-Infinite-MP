package net.minecraft.game.world.generation.features.structure.fortress.pieces;

import java.util.List;
import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.structure.LegacyStructureBoundingBox;
import net.minecraft.game.world.generation.features.structure.LegacyStructurePiece;
import net.minecraft.game.world.generation.features.structure.fortress.FortressPiece;

public class MushroomStaircase extends FortressPiece {
    public MushroomStaircase(int id, Random rand, LegacyStructureBoundingBox bb, int rot) {
        super(id);
        this.rotation = rot;
        this.boundingBox = bb;
    }

    @Override
    public void buildComponent(LegacyStructurePiece piece, List<LegacyStructurePiece> pcs, Random rand) {
        this.getNextComponent((EntranceBridge)piece, pcs, rand, 5, 3, true);
        this.getNextComponent((EntranceBridge)piece, pcs, rand, 5, 11, true);
    }

    public static MushroomStaircase createValidComponent(List<LegacyStructurePiece> pcs, Random rand, int x, int y, int z, int rot, int id) {
        LegacyStructureBoundingBox bb = LegacyStructureBoundingBox.addToBB(x, y, z, -5, -3, 0, 13, 14, 13, rot);
        return isAboveGround(bb) && LegacyStructurePiece.findIntersecting(pcs, bb) == null ? new MushroomStaircase(id, rand, bb, rot) : null;
    }

    @Override
    public boolean addComponentParts(World world, Random rand, LegacyStructureBoundingBox bb) {
        this.fill(world, bb, 0, 3, 0, 12, 4, 12, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 0, 5, 0, 12, 13, 12, 0, 0, false);
        this.fill(world, bb, 0, 5, 0, 1, 12, 12, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 11, 5, 0, 12, 12, 12, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 2, 5, 11, 4, 12, 12, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 8, 5, 11, 10, 12, 12, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 5, 9, 11, 7, 12, 12, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 2, 5, 0, 4, 12, 1, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 8, 5, 0, 10, 12, 1, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 5, 9, 0, 7, 12, 1, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 2, 11, 2, 10, 12, 10, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);

        for (int i = 1; i <= 11; i += 2) {
            this.fill(world, bb, i, 10, 0, i, 11, 0, BlockList.ironBars.id, BlockList.ironBars.id, false);
            this.fill(world, bb, i, 10, 12, i, 11, 12, BlockList.ironBars.id, BlockList.ironBars.id, false);
            this.fill(world, bb, 0, 10, i, 0, 11, i, BlockList.ironBars.id, BlockList.ironBars.id, false);
            this.fill(world, bb, 12, 10, i, 12, 11, i, BlockList.ironBars.id, BlockList.ironBars.id, false);
            this.placeBlock(world, BlockList.obsidianBrick.id, 0, i, 13, 0, bb);
            this.placeBlock(world, BlockList.obsidianBrick.id, 0, i, 13, 12, bb);
            this.placeBlock(world, BlockList.obsidianBrick.id, 0, 0, 13, i, bb);
            this.placeBlock(world, BlockList.obsidianBrick.id, 0, 12, 13, i, bb);
            this.placeBlock(world, BlockList.ironBars.id, 0, i + 1, 13, 0, bb);
            this.placeBlock(world, BlockList.ironBars.id, 0, i + 1, 13, 12, bb);
            this.placeBlock(world, BlockList.ironBars.id, 0, 0, 13, i + 1, bb);
            this.placeBlock(world, BlockList.ironBars.id, 0, 12, 13, i + 1, bb);
        }

        this.placeBlock(world, BlockList.ironBars.id, 0, 0, 13, 0, bb);
        this.placeBlock(world, BlockList.ironBars.id, 0, 0, 13, 12, bb);
        this.placeBlock(world, BlockList.ironBars.id, 0, 0, 13, 0, bb);
        this.placeBlock(world, BlockList.ironBars.id, 0, 12, 13, 0, bb);

        for (int var8 = 3; var8 <= 9; var8 += 2) {
            this.fill(world, bb, 1, 7, var8, 1, 8, var8, BlockList.ironBars.id, BlockList.ironBars.id, false);
            this.fill(world, bb, 11, 7, var8, 11, 8, var8, BlockList.ironBars.id, BlockList.ironBars.id, false);
        }

        int var9 = this.getMetadataWithOffset(BlockList.stairObsidianBrick.id, 3);

        for (int y = 0; y <= 6; y++) {
            int z = y + 4;

            for (int x = 5; x <= 7; x++) {
                this.placeBlock(world, BlockList.stairObsidianBrick.id, var9, x, 5 + y, z, bb);
            }

            if (z >= 5 && z <= 8) {
                this.fill(world, bb, 5, 5, z, 7, y + 4, z, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
            } else if (z >= 9 && z <= 10) {
                this.fill(world, bb, 5, 8, z, 7, y + 4, z, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
            }

            if (y >= 1) {
                this.fill(world, bb, 5, 6 + y, z, 7, 9 + y, z, 0, 0, false);
            }
        }

        for (int var12 = 5; var12 <= 7; var12++) {
            this.placeBlock(world, BlockList.stairObsidianBrick.id, var9, var12, 12, 11, bb);
        }

        this.fill(world, bb, 5, 6, 7, 5, 7, 7, BlockList.ironBars.id, BlockList.ironBars.id, false);
        this.fill(world, bb, 7, 6, 7, 7, 7, 7, BlockList.ironBars.id, BlockList.ironBars.id, false);
        this.fill(world, bb, 5, 13, 12, 7, 13, 12, 0, 0, false);
        this.fill(world, bb, 2, 5, 2, 3, 5, 3, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 2, 5, 9, 3, 5, 10, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 2, 5, 4, 2, 5, 8, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 9, 5, 2, 10, 5, 3, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 9, 5, 9, 10, 5, 10, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 10, 5, 4, 10, 5, 8, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        int var13 = this.getMetadataWithOffset(BlockList.stairObsidianBrick.id, 0);
        int z = this.getMetadataWithOffset(BlockList.stairObsidianBrick.id, 1);
        this.placeBlock(world, BlockList.stairObsidianBrick.id, z, 4, 5, 2, bb);
        this.placeBlock(world, BlockList.stairObsidianBrick.id, z, 4, 5, 3, bb);
        this.placeBlock(world, BlockList.stairObsidianBrick.id, z, 4, 5, 9, bb);
        this.placeBlock(world, BlockList.stairObsidianBrick.id, z, 4, 5, 10, bb);
        this.placeBlock(world, BlockList.stairObsidianBrick.id, var13, 8, 5, 2, bb);
        this.placeBlock(world, BlockList.stairObsidianBrick.id, var13, 8, 5, 3, bb);
        this.placeBlock(world, BlockList.stairObsidianBrick.id, var13, 8, 5, 9, bb);
        this.placeBlock(world, BlockList.stairObsidianBrick.id, var13, 8, 5, 10, bb);
        this.fill(world, bb, 3, 4, 4, 4, 4, 8, BlockList.slowSand.id, BlockList.slowSand.id, false);
        this.fill(world, bb, 8, 4, 4, 9, 4, 8, BlockList.slowSand.id, BlockList.slowSand.id, false);
        this.fill(world, bb, 3, 5, 4, 4, 5, 8, BlockList.mushroomCrimson.id, BlockList.mushroomCrimson.id, false);
        this.fill(world, bb, 8, 5, 4, 9, 5, 8, BlockList.mushroomNylium.id, BlockList.mushroomNylium.id, false);
        this.fill(world, bb, 4, 2, 0, 8, 2, 12, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 0, 2, 4, 12, 2, 8, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 4, 0, 0, 8, 1, 3, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 4, 0, 9, 8, 1, 12, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 0, 0, 4, 3, 1, 8, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 9, 0, 4, 12, 1, 8, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);

        for (int x = 4; x <= 8; x++) {
            for (int var15 = 0; var15 <= 2; var15++) {
                this.fillDownwards(world, BlockList.obsidianBrick.id, 0, x, -1, var15, bb);
                this.fillDownwards(world, BlockList.obsidianBrick.id, 0, x, -1, 12 - var15, bb);
            }
        }

        for (int var11 = 0; var11 <= 2; var11++) {
            for (int var16 = 4; var16 <= 8; var16++) {
                this.fillDownwards(world, BlockList.obsidianBrick.id, 0, var11, -1, var16, bb);
                this.fillDownwards(world, BlockList.obsidianBrick.id, 0, 12 - var11, -1, var16, bb);
            }
        }

        return true;
    }
}
