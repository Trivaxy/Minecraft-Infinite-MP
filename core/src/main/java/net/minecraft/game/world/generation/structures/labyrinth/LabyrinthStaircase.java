package net.minecraft.game.world.generation.structures.labyrinth;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;

public class LabyrinthStaircase extends LabyrinthPiece {
    public LabyrinthStaircase(World world, boolean rotated) {
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
        return 15;
    }

    @Override
    public void generate() {
    }

    @Override
    public void build() {
        this.drawClippedBoxRelative(1, 1, 1, 7, 6, 7, 0, 0, true);
        this.drawClippedBoxRelative(1, 8, 1, 7, 12, 7, 0, 0, true);
        this.drawClippedBoxRelative(3, 7, 3, 5, 7, 5, 0, 0, true);
        this.drawClippedBoxRandomizer(0, 0, 0, 8, 0, 8, BlockList.cobblestoneMossy.id, BlockList.cobblestone.id, 0, 2, false);
        this.drawClippedBoxRandomizer(1, 7, 1, 7, 7, 7, BlockList.cobblestoneMossy.id, BlockList.cobblestone.id, 0, 2, false);
        this.drawClippedBoxRandomizer(0, 1, 0, 8, 13, 0, BlockList.cobblestoneMossy.id, BlockList.cobblestone.id, 0, 3, false);
        this.drawClippedBoxRandomizer(0, 1, 0, 0, 13, 8, BlockList.cobblestoneMossy.id, BlockList.cobblestone.id, 0, 3, false);
        this.drawClippedBoxRandomizer(8, 1, 0, 8, 13, 8, BlockList.cobblestoneMossy.id, BlockList.cobblestone.id, 0, 3, false);
        this.drawClippedBoxRandomizer(0, 1, 8, 8, 13, 8, BlockList.cobblestoneMossy.id, BlockList.cobblestone.id, 0, 3, false);
        this.drawClippedBoxRelative(0, 14, 0, 8, 14, 8, BlockList.planks.id, 0, false);
        this.drawClippedBoxRandomizer(1, 13, 1, 7, 13, 7, BlockList.cobweb.id, 0, 0, 10, false);
        this.drawClippedBoxRelative(4, 1, 4, 4, 7, 4, BlockList.stone.id, 0, true);
        this.drawClippedBoxRelative(4, 1, 5, 4, 1, 5, BlockList.slabStone.id, 0, true);
        this.drawClippedBoxRelative(5, 1, 5, 5, 1, 5, BlockList.slabStone.id, 128, true);
        this.drawClippedBoxRelative(5, 2, 4, 5, 2, 4, BlockList.slabStone.id, 0, true);
        this.drawClippedBoxRelative(5, 2, 3, 5, 2, 3, BlockList.slabStone.id, 128, true);
        this.drawClippedBoxRelative(4, 3, 3, 4, 3, 3, BlockList.slabStone.id, 0, true);
        this.drawClippedBoxRelative(3, 3, 3, 3, 3, 3, BlockList.slabStone.id, 128, true);
        this.drawClippedBoxRelative(3, 4, 4, 3, 4, 4, BlockList.slabStone.id, 0, true);
        this.drawClippedBoxRelative(3, 4, 5, 3, 4, 5, BlockList.slabStone.id, 128, true);
        this.drawClippedBoxRelative(4, 5, 5, 4, 5, 5, BlockList.slabStone.id, 0, true);
        this.drawClippedBoxRelative(5, 5, 5, 5, 5, 5, BlockList.slabStone.id, 128, true);
        this.drawClippedBoxRelative(5, 6, 4, 5, 6, 4, BlockList.slabStone.id, 0, true);
        this.drawClippedBoxRelative(5, 6, 3, 5, 6, 3, BlockList.slabStone.id, 128, true);
        this.drawClippedBoxRelative(4, 7, 3, 4, 7, 3, BlockList.slabStone.id, 0, true);
        this.drawClippedBoxRelative(3, 7, 3, 3, 7, 5, BlockList.slabStone.id, 128, true);
    }
}
