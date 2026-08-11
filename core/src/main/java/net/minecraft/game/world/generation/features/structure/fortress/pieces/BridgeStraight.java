package net.minecraft.game.world.generation.features.structure.fortress.pieces;

import java.util.List;
import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.structure.LegacyStructureBoundingBox;
import net.minecraft.game.world.generation.features.structure.LegacyStructurePiece;
import net.minecraft.game.world.generation.features.structure.fortress.FortressPiece;

public class BridgeStraight extends FortressPiece {
    public BridgeStraight(int id, Random rand, LegacyStructureBoundingBox bb, int rot) {
        super(id);
        this.rotation = rot;
        this.boundingBox = bb;
    }

    @Override
    public void buildComponent(LegacyStructurePiece piece, List<LegacyStructurePiece> pcs, Random rand) {
        this.getNextComponent((EntranceBridge)piece, pcs, rand, 1, 3, false);
    }

    public static BridgeStraight createValidComponent(List<LegacyStructurePiece> pcs, Random rand, int x, int y, int z, int rot, int id) {
        LegacyStructureBoundingBox bb = LegacyStructureBoundingBox.addToBB(x, y, z, -1, -3, 0, 5, 10, 19, rot);
        return isAboveGround(bb) && LegacyStructurePiece.findIntersecting(pcs, bb) == null ? new BridgeStraight(id, rand, bb, rot) : null;
    }

    @Override
    public boolean addComponentParts(World world, Random rand, LegacyStructureBoundingBox bb) {
        this.fill(world, bb, 0, 3, 0, 4, 4, 18, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 1, 5, 0, 3, 7, 18, 0, 0, false);
        this.fill(world, bb, 0, 5, 0, 0, 5, 18, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 4, 5, 0, 4, 5, 18, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 0, 2, 0, 4, 2, 5, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 0, 2, 13, 4, 2, 18, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 0, 0, 0, 4, 1, 3, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 0, 0, 15, 4, 1, 18, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);

        for (int x = 0; x <= 4; x++) {
            for (int z = 0; z <= 2; z++) {
                this.fillDownwards(world, BlockList.obsidianBrick.id, 0, x, -1, z, bb);
                this.fillDownwards(world, BlockList.obsidianBrick.id, 0, x, -1, 18 - z, bb);
            }
        }

        this.fill(world, bb, 0, 1, 1, 0, 4, 1, BlockList.ironBars.id, BlockList.ironBars.id, false);
        this.fill(world, bb, 0, 3, 4, 0, 4, 4, BlockList.ironBars.id, BlockList.ironBars.id, false);
        this.fill(world, bb, 0, 3, 14, 0, 4, 14, BlockList.ironBars.id, BlockList.ironBars.id, false);
        this.fill(world, bb, 0, 1, 17, 0, 4, 17, BlockList.ironBars.id, BlockList.ironBars.id, false);
        this.fill(world, bb, 4, 1, 1, 4, 4, 1, BlockList.ironBars.id, BlockList.ironBars.id, false);
        this.fill(world, bb, 4, 3, 4, 4, 4, 4, BlockList.ironBars.id, BlockList.ironBars.id, false);
        this.fill(world, bb, 4, 3, 14, 4, 4, 14, BlockList.ironBars.id, BlockList.ironBars.id, false);
        this.fill(world, bb, 4, 1, 17, 4, 4, 17, BlockList.ironBars.id, BlockList.ironBars.id, false);
        return true;
    }
}
