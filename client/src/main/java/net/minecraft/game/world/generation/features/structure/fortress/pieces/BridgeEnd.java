package net.minecraft.game.world.generation.features.structure.fortress.pieces;

import java.util.List;
import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.structure.LegacyStructureBoundingBox;
import net.minecraft.game.world.generation.features.structure.LegacyStructurePiece;
import net.minecraft.game.world.generation.features.structure.fortress.FortressPiece;

public class BridgeEnd extends FortressPiece {
    Random uniqueRNG;
    private int fillSeed;

    public BridgeEnd(int id, Random rand, LegacyStructureBoundingBox bb, int rot) {
        super(id);
        this.rotation = rot;
        this.boundingBox = bb;
        this.fillSeed = rand.nextInt();
        this.uniqueRNG = new Random(this.fillSeed);
    }

    @Override
    public void buildComponent(LegacyStructurePiece piece, List<LegacyStructurePiece> pcs, Random rand) {
    }

    public static BridgeEnd generateTunnelEnd(List<LegacyStructurePiece> pcs, Random rand, int x, int y, int z, int rot, int id) {
        LegacyStructureBoundingBox bb = LegacyStructureBoundingBox.addToBB(x, y, z, -1, -3, 0, 5, 10, 8, rot);
        return isAboveGround(bb) && LegacyStructurePiece.findIntersecting(pcs, bb) == null ? new BridgeEnd(id, rand, bb, rot) : null;
    }

    @Override
    public boolean addComponentParts(World world, Random rand, LegacyStructureBoundingBox bb) {
        for (int x = 0; x <= 4; x++) {
            for (int z = 3; z <= 4; z++) {
                int amount = this.uniqueRNG.nextInt(8);
                this.fill(world, bb, x, z, 0, x, z, amount, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
            }
        }

        int var7 = this.uniqueRNG.nextInt(8);
        this.fill(world, bb, 0, 5, 0, 0, 5, var7, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        var7 = this.uniqueRNG.nextInt(8);
        this.fill(world, bb, 4, 5, 0, 4, 5, var7, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);

        for (int var9 = 0; var9 <= 4; var9++) {
            int z = this.uniqueRNG.nextInt(5);
            this.fill(world, bb, var9, 2, 0, var9, 2, z, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        }

        for (int var10 = 0; var10 <= 4; var10++) {
            for (int z = 0; z <= 1; z++) {
                int amount = this.uniqueRNG.nextInt(3);
                this.fill(world, bb, var10, z, 0, var10, z, amount, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
            }
        }

        return true;
    }
}
