package net.minecraft.game.world.generation.features.structure.fortress.pieces;

import java.util.List;
import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.structure.LegacyStructureBoundingBox;
import net.minecraft.game.world.generation.features.structure.LegacyStructurePiece;
import net.minecraft.game.world.generation.features.structure.fortress.FortressPiece;

public class BridgeType1 extends FortressPiece {
    public BridgeType1(int id, Random rand, LegacyStructureBoundingBox bb, int rot) {
        super(id);
        this.rotation = rot;
        this.boundingBox = bb;
    }

    @Override
    public void buildComponent(LegacyStructurePiece piss, List<LegacyStructurePiece> pcs, Random rand) {
        byte facing = 1;
        if (this.rotation == 1 || this.rotation == 2) {
            facing = 5;
        }

        this.getNextComponentX((EntranceBridge)piss, pcs, rand, 0, facing, rand.nextInt(8) > 0);
        this.getNextComponentZ((EntranceBridge)piss, pcs, rand, 0, facing, rand.nextInt(8) > 0);
    }

    public static BridgeType1 createValidComponent(List<LegacyStructurePiece> pcs, Random rand, int x, int y, int z, int rot, int id) {
        LegacyStructureBoundingBox bb = LegacyStructureBoundingBox.addToBB(x, y, z, -3, 0, 0, 9, 7, 9, rot);
        return isAboveGround(bb) && LegacyStructurePiece.findIntersecting(pcs, bb) == null ? new BridgeType1(id, rand, bb, rot) : null;
    }

    @Override
    public boolean addComponentParts(World world, Random rand, LegacyStructureBoundingBox bb) {
        this.fill(world, bb, 0, 0, 0, 8, 1, 8, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 0, 2, 0, 8, 5, 8, 0, 0, false);
        this.fill(world, bb, 0, 6, 0, 8, 6, 5, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 0, 2, 0, 2, 5, 0, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 6, 2, 0, 8, 5, 0, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 1, 3, 0, 1, 4, 0, BlockList.ironBars.id, BlockList.ironBars.id, false);
        this.fill(world, bb, 7, 3, 0, 7, 4, 0, BlockList.ironBars.id, BlockList.ironBars.id, false);
        this.fill(world, bb, 0, 2, 4, 8, 2, 8, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 1, 1, 4, 2, 2, 4, 0, 0, false);
        this.fill(world, bb, 6, 1, 4, 7, 2, 4, 0, 0, false);
        this.fill(world, bb, 0, 3, 8, 8, 3, 8, BlockList.ironBars.id, BlockList.ironBars.id, false);
        this.fill(world, bb, 0, 3, 6, 0, 3, 7, BlockList.ironBars.id, BlockList.ironBars.id, false);
        this.fill(world, bb, 8, 3, 6, 8, 3, 7, BlockList.ironBars.id, BlockList.ironBars.id, false);
        this.fill(world, bb, 0, 3, 4, 0, 5, 5, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 8, 3, 4, 8, 5, 5, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 1, 3, 5, 2, 5, 5, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 6, 3, 5, 7, 5, 5, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 1, 4, 5, 1, 5, 5, BlockList.ironBars.id, BlockList.ironBars.id, false);
        this.fill(world, bb, 7, 4, 5, 7, 5, 5, BlockList.ironBars.id, BlockList.ironBars.id, false);

        for (int z = 0; z <= 5; z++) {
            for (int x = 0; x <= 8; x++) {
                this.fillDownwards(world, BlockList.obsidianBrick.id, 0, x, -1, z, bb);
            }
        }

        return true;
    }
}
