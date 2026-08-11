package net.minecraft.game.world.generation.features.structure.fortress.pieces;

import java.util.List;
import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.structure.LegacyStructureBoundingBox;
import net.minecraft.game.world.generation.features.structure.LegacyStructurePiece;
import net.minecraft.game.world.generation.features.structure.fortress.FortressPiece;

public class BridgeCrossing2 extends FortressPiece {
    public BridgeCrossing2(int id, Random rand, LegacyStructureBoundingBox bb, int rot) {
        super(id);
        this.rotation = rot;
        this.boundingBox = bb;
    }

    @Override
    public void buildComponent(LegacyStructurePiece piece, List<LegacyStructurePiece> pcs, Random rand) {
        this.getNextComponent((EntranceBridge)piece, pcs, rand, 1, 0, true);
        this.getNextComponentX((EntranceBridge)piece, pcs, rand, 0, 1, true);
        this.getNextComponentZ((EntranceBridge)piece, pcs, rand, 0, 1, true);
    }

    public static BridgeCrossing2 createValidComponent(List<LegacyStructurePiece> pcs, Random rand, int x, int y, int z, int rot, int id) {
        LegacyStructureBoundingBox bb = LegacyStructureBoundingBox.addToBB(x, y, z, -1, 0, 0, 5, 7, 5, rot);
        return isAboveGround(bb) && LegacyStructurePiece.findIntersecting(pcs, bb) == null ? new BridgeCrossing2(id, rand, bb, rot) : null;
    }

    @Override
    public boolean addComponentParts(World world, Random rand, LegacyStructureBoundingBox bb) {
        this.fill(world, bb, 0, 0, 0, 4, 1, 4, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 0, 2, 0, 4, 5, 4, 0, 0, false);
        this.fill(world, bb, 0, 2, 0, 0, 5, 0, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 4, 2, 0, 4, 5, 0, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 0, 2, 4, 0, 5, 4, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 4, 2, 4, 4, 5, 4, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 0, 6, 0, 4, 6, 4, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);

        for (int x = 0; x <= 4; x++) {
            for (int z = 0; z <= 4; z++) {
                this.fillDownwards(world, BlockList.obsidianBrick.id, 0, x, -1, z, bb);
            }
        }

        return true;
    }
}
