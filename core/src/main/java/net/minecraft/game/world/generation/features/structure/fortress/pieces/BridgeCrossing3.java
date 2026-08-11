package net.minecraft.game.world.generation.features.structure.fortress.pieces;

import java.util.List;
import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.structure.LegacyStructureBoundingBox;
import net.minecraft.game.world.generation.features.structure.LegacyStructurePiece;
import net.minecraft.game.world.generation.features.structure.fortress.FortressPiece;

public class BridgeCrossing3 extends FortressPiece {
    public BridgeCrossing3(int id, Random rand, LegacyStructureBoundingBox bb, int rot) {
        super(id);
        this.rotation = rot;
        this.boundingBox = bb;
    }

    protected BridgeCrossing3(Random rand, int x, int z) {
        super(0);
        this.rotation = rand.nextInt(4);
        switch (this.rotation) {
            case 0:
            case 2:
                this.boundingBox = new LegacyStructureBoundingBox(x, 64, z, x + 19 - 1, 73, z + 19 - 1);
                break;
            default:
                this.boundingBox = new LegacyStructureBoundingBox(x, 64, z, x + 19 - 1, 73, z + 19 - 1);
        }
    }

    @Override
    public void buildComponent(LegacyStructurePiece piece, List<LegacyStructurePiece> pcs, Random rand) {
        this.getNextComponent((EntranceBridge)piece, pcs, rand, 8, 3, false);
        this.getNextComponentX((EntranceBridge)piece, pcs, rand, 3, 8, false);
        this.getNextComponentZ((EntranceBridge)piece, pcs, rand, 3, 8, false);
    }

    public static BridgeCrossing3 createValidComponent(List<LegacyStructurePiece> pcs, Random rand, int x, int y, int z, int rot, int id) {
        LegacyStructureBoundingBox bb = LegacyStructureBoundingBox.addToBB(x, y, z, -8, -3, 0, 19, 10, 19, rot);
        return isAboveGround(bb) && LegacyStructurePiece.findIntersecting(pcs, bb) == null ? new BridgeCrossing3(id, rand, bb, rot) : null;
    }

    @Override
    public boolean addComponentParts(World world, Random rand, LegacyStructureBoundingBox bb) {
        this.fill(world, bb, 7, 3, 0, 11, 4, 18, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 0, 3, 7, 18, 4, 11, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 8, 5, 0, 10, 7, 18, 0, 0, false);
        this.fill(world, bb, 0, 5, 8, 18, 7, 10, 0, 0, false);
        this.fill(world, bb, 7, 5, 0, 7, 5, 7, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 7, 5, 11, 7, 5, 18, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 11, 5, 0, 11, 5, 7, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 11, 5, 11, 11, 5, 18, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 0, 5, 7, 7, 5, 7, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 11, 5, 7, 18, 5, 7, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 0, 5, 11, 7, 5, 11, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 11, 5, 11, 18, 5, 11, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 7, 2, 0, 11, 2, 5, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 7, 2, 13, 11, 2, 18, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 7, 0, 0, 11, 1, 3, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 7, 0, 15, 11, 1, 18, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);

        for (int x = 7; x <= 11; x++) {
            for (int z = 0; z <= 2; z++) {
                this.fillDownwards(world, BlockList.obsidianBrick.id, 0, x, -1, z, bb);
                this.fillDownwards(world, BlockList.obsidianBrick.id, 0, x, -1, 18 - z, bb);
            }
        }

        this.fill(world, bb, 0, 2, 7, 5, 2, 11, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 13, 2, 7, 18, 2, 11, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 0, 0, 7, 3, 1, 11, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 15, 0, 7, 18, 1, 11, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);

        for (int var6 = 0; var6 <= 2; var6++) {
            for (int z = 7; z <= 11; z++) {
                this.fillDownwards(world, BlockList.obsidianBrick.id, 0, var6, -1, z, bb);
                this.fillDownwards(world, BlockList.obsidianBrick.id, 0, 18 - var6, -1, z, bb);
            }
        }

        return true;
    }
}
