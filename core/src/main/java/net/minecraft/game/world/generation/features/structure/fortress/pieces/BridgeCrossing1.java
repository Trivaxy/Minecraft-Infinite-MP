package net.minecraft.game.world.generation.features.structure.fortress.pieces;

import java.util.List;
import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.structure.LegacyStructureBoundingBox;
import net.minecraft.game.world.generation.features.structure.LegacyStructurePiece;
import net.minecraft.game.world.generation.features.structure.fortress.FortressPiece;

public class BridgeCrossing1 extends FortressPiece {
    public BridgeCrossing1(int id, Random rand, LegacyStructureBoundingBox bb, int rot) {
        super(id);
        this.rotation = rot;
        this.boundingBox = bb;
    }

    @Override
    public void buildComponent(LegacyStructurePiece piece, List<LegacyStructurePiece> pcs, Random rand) {
        this.getNextComponent((EntranceBridge)piece, pcs, rand, 2, 0, false);
        this.getNextComponentX((EntranceBridge)piece, pcs, rand, 0, 2, false);
        this.getNextComponentZ((EntranceBridge)piece, pcs, rand, 0, 2, false);
    }

    public static BridgeCrossing1 createValidComponent(List<LegacyStructurePiece> pcs, Random rand, int x, int y, int z, int rot, int id) {
        LegacyStructureBoundingBox bb = LegacyStructureBoundingBox.addToBB(x, y, z, -2, 0, 0, 7, 9, 7, rot);
        return isAboveGround(bb) && LegacyStructurePiece.findIntersecting(pcs, bb) == null ? new BridgeCrossing1(id, rand, bb, rot) : null;
    }

    @Override
    public boolean addComponentParts(World world, Random rand, LegacyStructureBoundingBox bb) {
        this.fill(world, bb, 0, 0, 0, 6, 1, 6, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 0, 2, 0, 6, 7, 6, 0, 0, false);
        this.fill(world, bb, 0, 2, 0, 1, 6, 0, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 0, 2, 6, 1, 6, 6, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 5, 2, 0, 6, 6, 0, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 5, 2, 6, 6, 6, 6, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 0, 2, 0, 0, 6, 1, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 0, 2, 5, 0, 6, 6, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 6, 2, 0, 6, 6, 1, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 6, 2, 5, 6, 6, 6, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 2, 6, 0, 4, 6, 0, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 2, 5, 0, 4, 5, 0, BlockList.ironBars.id, BlockList.ironBars.id, false);
        this.fill(world, bb, 2, 6, 6, 4, 6, 6, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 2, 5, 6, 4, 5, 6, BlockList.ironBars.id, BlockList.ironBars.id, false);
        this.fill(world, bb, 0, 6, 2, 0, 6, 4, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 0, 5, 2, 0, 5, 4, BlockList.ironBars.id, BlockList.ironBars.id, false);
        this.fill(world, bb, 6, 6, 2, 6, 6, 4, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 6, 5, 2, 6, 5, 4, BlockList.ironBars.id, BlockList.ironBars.id, false);

        for (int x = 0; x <= 6; x++) {
            for (int z = 0; z <= 6; z++) {
                this.fillDownwards(world, BlockList.obsidianBrick.id, 0, x, -1, z, bb);
            }
        }

        return true;
    }
}
