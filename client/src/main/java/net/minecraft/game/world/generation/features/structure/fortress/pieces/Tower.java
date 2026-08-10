package net.minecraft.game.world.generation.features.structure.fortress.pieces;

import java.util.List;
import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.structure.LegacyStructureBoundingBox;
import net.minecraft.game.world.generation.features.structure.LegacyStructurePiece;
import net.minecraft.game.world.generation.features.structure.fortress.FortressPiece;

public class Tower extends FortressPiece {
    public Tower(int id, Random rand, LegacyStructureBoundingBox bb, int rot) {
        super(id);
        this.rotation = rot;
        this.boundingBox = bb;
    }

    @Override
    public void buildComponent(LegacyStructurePiece piece, List<LegacyStructurePiece> bb, Random rand) {
        this.getNextComponentZ((EntranceBridge)piece, bb, rand, 6, 2, false);
    }

    public static Tower createValidComponent(List<LegacyStructurePiece> pcs, Random rand, int x, int y, int z, int rot, int id) {
        LegacyStructureBoundingBox bb = LegacyStructureBoundingBox.addToBB(x, y, z, -2, 0, 0, 7, 11, 7, rot);
        return isAboveGround(bb) && LegacyStructurePiece.findIntersecting(pcs, bb) == null ? new Tower(id, rand, bb, rot) : null;
    }

    @Override
    public boolean addComponentParts(World world, Random rand, LegacyStructureBoundingBox bb) {
        this.fill(world, bb, 0, 0, 0, 6, 1, 6, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 0, 2, 0, 6, 10, 6, 0, 0, false);
        this.fill(world, bb, 0, 2, 0, 1, 8, 0, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 5, 2, 0, 6, 8, 0, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 0, 2, 1, 0, 8, 6, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 6, 2, 1, 6, 8, 6, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 1, 2, 6, 5, 8, 6, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 0, 3, 2, 0, 5, 4, BlockList.ironBars.id, BlockList.ironBars.id, false);
        this.fill(world, bb, 6, 3, 2, 6, 5, 2, BlockList.ironBars.id, BlockList.ironBars.id, false);
        this.fill(world, bb, 6, 3, 4, 6, 5, 4, BlockList.ironBars.id, BlockList.ironBars.id, false);
        this.placeBlock(world, BlockList.obsidianBrick.id, 0, 5, 2, 5, bb);
        this.fill(world, bb, 4, 2, 5, 4, 3, 5, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 3, 2, 5, 3, 4, 5, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 2, 2, 5, 2, 5, 5, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 1, 2, 5, 1, 6, 5, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 1, 7, 1, 5, 7, 4, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 6, 8, 2, 6, 8, 4, 0, 0, false);
        this.fill(world, bb, 2, 6, 0, 4, 8, 0, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 2, 5, 0, 4, 5, 0, BlockList.ironBars.id, BlockList.ironBars.id, false);

        for (int x = 0; x <= 6; x++) {
            for (int z = 0; z <= 6; z++) {
                this.fillDownwards(world, BlockList.obsidianBrick.id, 0, x, -1, z, bb);
            }
        }

        return true;
    }
}
