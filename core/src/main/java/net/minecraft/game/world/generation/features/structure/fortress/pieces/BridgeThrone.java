package net.minecraft.game.world.generation.features.structure.fortress.pieces;

import java.util.List;
import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.tileentity.other.SpawnerEntity;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.structure.LegacyStructureBoundingBox;
import net.minecraft.game.world.generation.features.structure.LegacyStructurePiece;
import net.minecraft.game.world.generation.features.structure.fortress.FortressPiece;

public class BridgeThrone extends FortressPiece {
    private boolean hasSpawner;

    public BridgeThrone(int id, Random rand, LegacyStructureBoundingBox bb, int rot) {
        super(id);
        this.rotation = rot;
        this.boundingBox = bb;
    }

    @Override
    public void buildComponent(LegacyStructurePiece piss, List<LegacyStructurePiece> pcs, Random rand) {
    }

    public static BridgeThrone createValidComponent(List<LegacyStructurePiece> pcs, Random rand, int x, int y, int z, int rot, int id) {
        LegacyStructureBoundingBox bb = LegacyStructureBoundingBox.addToBB(x, y, z, -2, 0, 0, 7, 8, 9, rot);
        return isAboveGround(bb) && LegacyStructurePiece.findIntersecting(pcs, bb) == null ? new BridgeThrone(id, rand, bb, rot) : null;
    }

    @Override
    public boolean addComponentParts(World world, Random rand, LegacyStructureBoundingBox bb) {
        this.fill(world, bb, 0, 2, 0, 6, 7, 7, 0, 0, false);
        this.fill(world, bb, 1, 0, 0, 5, 1, 7, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 1, 2, 1, 5, 2, 7, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 1, 3, 2, 5, 3, 7, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 1, 4, 3, 5, 4, 7, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 1, 2, 0, 1, 4, 2, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 5, 2, 0, 5, 4, 2, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 1, 5, 2, 1, 5, 3, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 5, 5, 2, 5, 5, 3, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 0, 5, 3, 0, 5, 8, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 6, 5, 3, 6, 5, 8, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 1, 5, 8, 5, 5, 8, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.placeBlock(world, BlockList.ironBars.id, 0, 1, 6, 3, bb);
        this.placeBlock(world, BlockList.ironBars.id, 0, 5, 6, 3, bb);
        this.fill(world, bb, 0, 6, 3, 0, 6, 8, BlockList.ironBars.id, BlockList.ironBars.id, false);
        this.fill(world, bb, 6, 6, 3, 6, 6, 8, BlockList.ironBars.id, BlockList.ironBars.id, false);
        this.fill(world, bb, 1, 6, 8, 5, 7, 8, BlockList.ironBars.id, BlockList.ironBars.id, false);
        this.fill(world, bb, 2, 8, 8, 4, 8, 8, BlockList.ironBars.id, BlockList.ironBars.id, false);
        if (!this.hasSpawner) {
            int y = this.getYWithOffset(5);
            int x = this.getXWithOffset(3, 5);
            int z = this.getZWithOffset(3, 5);
            if (bb.isVecInside(x, y, z)) {
                this.hasSpawner = true;
                world.setBlockWithNotify(x, y, z, BlockList.mobSpawner.id);
                SpawnerEntity tileEntityMobSpawner7 = (SpawnerEntity)world.getTileEntity(x, y, z);
                if (tileEntityMobSpawner7 != null) {
                    tileEntityMobSpawner7.setMob("Blaze");
                }
            }
        }

        for (int x = 0; x <= 6; x++) {
            for (int z = 0; z <= 6; z++) {
                this.fillDownwards(world, BlockList.obsidianBrick.id, 0, x, -1, z, bb);
            }
        }

        return true;
    }
}
