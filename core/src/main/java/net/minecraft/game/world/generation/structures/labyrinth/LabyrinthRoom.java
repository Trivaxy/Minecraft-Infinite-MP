package net.minecraft.game.world.generation.structures.labyrinth;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;

public class LabyrinthRoom extends LabyrinthPiece {
    public LabyrinthRoom(World world, boolean rotated) {
        super(world, rotated);
    }

    @Override
    protected int buildingWidth() {
        return 9;
    }

    @Override
    protected int buildingLength() {
        return 9;
    }

    @Override
    protected int buildingHeight() {
        return 6;
    }

    @Override
    public void generate() {
    }

    @Override
    public void build() {
        this.drawClippedBoxRelative(1, 1, 1, 7, 3, 7, 0, 0, true);
        this.drawClippedBoxRandomizer(0, 0, 0, 8, 0, 8, BlockList.cobblestoneMossy.id, BlockList.cobblestone.id, 0, 2, false);
        this.drawClippedBoxRandomizer(0, 1, 0, 8, 4, 0, BlockList.cobblestoneMossy.id, BlockList.cobblestone.id, 0, 3, false);
        this.drawClippedBoxRandomizer(0, 1, 0, 0, 4, 8, BlockList.cobblestoneMossy.id, BlockList.cobblestone.id, 0, 3, false);
        this.drawClippedBoxRandomizer(8, 1, 0, 8, 4, 8, BlockList.cobblestoneMossy.id, BlockList.cobblestone.id, 0, 3, false);
        this.drawClippedBoxRandomizer(0, 1, 8, 8, 4, 8, BlockList.cobblestoneMossy.id, BlockList.cobblestone.id, 0, 3, false);
        this.drawClippedBoxRelative(0, 5, 0, 8, 5, 8, BlockList.planks.id, 0, false);
        this.drawClippedBoxRandomizer(1, 4, 1, 7, 4, 7, BlockList.cobweb.id, 0, 0, 10, false);
        int blockID;
        if ((blockID = this.getBlockRelative(4, 0, 4)) > 0) {
            Block block = BlockList.blocks[blockID];
            if (block != null && block.fullBlock()) {
                if (this.world.rand.nextBoolean()) {
                    this.addChestRelative(4, 1, 4);
                } else {
                    this.addSpawnerRelative(4, 1, 4, this.getRandomMob());
                }
            }
        }
    }
}
