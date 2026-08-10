package net.minecraft.game.world.generation.features.structure.fortress.pieces;

import java.util.List;
import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.structure.LegacyStructureBoundingBox;
import net.minecraft.game.world.generation.features.structure.LegacyStructurePiece;
import net.minecraft.game.world.generation.features.structure.fortress.FortressPiece;

public class Staircase extends FortressPiece {
    public Staircase(int id, Random rand, LegacyStructureBoundingBox bb, int rot) {
        super(id);
        this.rotation = rot;
        this.boundingBox = bb;
    }

    @Override
    public void buildComponent(LegacyStructurePiece piece, List<LegacyStructurePiece> pcs, Random rand) {
        this.getNextComponent((EntranceBridge)piece, pcs, rand, 1, 0, true);
    }

    public static Staircase createValidComponent(List<LegacyStructurePiece> pcs, Random rand, int x, int y, int z, int rot, int nostalgia) {
        LegacyStructureBoundingBox bb = LegacyStructureBoundingBox.addToBB(x, y, z, -1, -7, 0, 5, 14, 10, rot);
        return isAboveGround(bb) && LegacyStructurePiece.findIntersecting(pcs, bb) == null ? new Staircase(nostalgia, rand, bb, rot) : null;
    }

    @Override
    public boolean addComponentParts(World world, Random rand, LegacyStructureBoundingBox bb) {
        int meta = this.getMetadataWithOffset(BlockList.stairObsidianBrick.id, 2);

        for (int x = 0; x <= 9; x++) {
            int start = Math.max(1, 7 - x);
            int end = Math.min(Math.max(start + 5, 14 - x), 13);
            this.fill(world, bb, 0, 0, x, 4, start, x, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
            this.fill(world, bb, 1, start + 1, x, 3, end - 1, x, 0, 0, false);
            if (x <= 6) {
                this.placeBlock(world, BlockList.stairObsidianBrick.id, meta, 1, start + 1, x, bb);
                this.placeBlock(world, BlockList.stairObsidianBrick.id, meta, 2, start + 1, x, bb);
                this.placeBlock(world, BlockList.stairObsidianBrick.id, meta, 3, start + 1, x, bb);
            }

            this.fill(world, bb, 0, end, x, 4, end, x, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
            this.fill(world, bb, 0, start + 1, x, 0, end - 1, x, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
            this.fill(world, bb, 4, start + 1, x, 4, end - 1, x, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
            if ((x & 1) == 0) {
                this.fill(world, bb, 0, start + 2, x, 0, start + 3, x, BlockList.ironBars.id, BlockList.ironBars.id, false);
                this.fill(world, bb, 4, start + 2, x, 4, start + 3, x, BlockList.ironBars.id, BlockList.ironBars.id, false);
            }

            for (int z = 0; z <= 4; z++) {
                this.fillDownwards(world, BlockList.obsidianBrick.id, 0, z, -1, x, bb);
            }
        }

        return true;
    }
}
