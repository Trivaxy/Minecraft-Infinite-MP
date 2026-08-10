package net.minecraft.game.world.generation.structures.labyrinth;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;

public class LabyrinthCorridorNS extends LabyrinthPiece {
    public LabyrinthCorridorNS(World world, boolean rotated) {
        super(world, rotated);
    }

    @Override
    protected int buildingWidth() {
        return 5;
    }

    @Override
    protected int buildingLength() {
        return 3;
    }

    @Override
    protected int buildingHeight() {
        return 5;
    }

    @Override
    public void generate() {
    }

    @Override
    public void build() {
        this.drawClippedBoxRelative(1, 1, 0, 3, 2, 2, 0, 0, true);
        this.drawClippedBoxRelative(2, 3, 0, 2, 3, 2, 0, 0, true);
        this.drawClippedBoxRelative(1, 3, 0, 1, 3, 2, 0, 0, true);
        this.drawClippedBoxRelative(3, 3, 0, 3, 3, 2, 0, 0, true);
        this.drawClippedBoxRandomizer(0, 0, 0, 4, 0, 2, BlockList.cobblestoneMossy.id, BlockList.cobblestone.id, 0, 2, false);
        this.drawClippedBoxRandomizer(0, 1, 0, 0, 3, 2, BlockList.cobblestoneMossy.id, BlockList.cobblestone.id, 0, 3, false);
        this.drawClippedBoxRandomizer(4, 1, 0, 4, 3, 2, BlockList.cobblestoneMossy.id, BlockList.cobblestone.id, 0, 3, false);
        this.drawClippedBoxRandomizer(1, 4, 0, 1, 4, 2, BlockList.cobblestoneMossy.id, BlockList.cobblestone.id, 0, 3, false);
        this.drawClippedBoxRandomizer(2, 4, 0, 2, 4, 2, BlockList.cobblestoneMossy.id, BlockList.cobblestone.id, 0, 3, false);
        this.drawClippedBoxRandomizer(3, 4, 0, 3, 4, 2, BlockList.cobblestoneMossy.id, BlockList.cobblestone.id, 0, 3, false);
    }
}
