package net.minecraft.game.world.generation.structures.labyrinth;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;

public class LabyrinthCorridorWE extends LabyrinthPiece {
    public LabyrinthCorridorWE(World world, boolean rotated) {
        super(world, rotated);
    }

    @Override
    protected int buildingWidth() {
        return 3;
    }

    @Override
    protected int buildingLength() {
        return 5;
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
        this.drawClippedBoxRelative(0, 1, 1, 2, 2, 3, 0, 0, true);
        this.drawClippedBoxRelative(0, 3, 2, 2, 3, 2, 0, 0, true);
        this.drawClippedBoxRelative(0, 3, 1, 2, 3, 1, 0, 0, true);
        this.drawClippedBoxRelative(0, 3, 3, 2, 3, 3, 0, 0, true);
        this.drawClippedBoxRandomizer(0, 0, 0, 2, 0, 4, BlockList.cobblestoneMossy.id, BlockList.cobblestone.id, 0, 2, false);
        this.drawClippedBoxRandomizer(0, 1, 0, 2, 3, 0, BlockList.cobblestoneMossy.id, BlockList.cobblestone.id, 0, 3, false);
        this.drawClippedBoxRandomizer(0, 1, 4, 2, 3, 4, BlockList.cobblestoneMossy.id, BlockList.cobblestone.id, 0, 3, false);
        this.drawClippedBoxRandomizer(0, 4, 1, 2, 4, 1, BlockList.cobblestoneMossy.id, BlockList.cobblestone.id, 0, 3, false);
        this.drawClippedBoxRandomizer(0, 4, 2, 2, 4, 2, BlockList.cobblestoneMossy.id, BlockList.cobblestone.id, 0, 3, false);
        this.drawClippedBoxRandomizer(0, 4, 3, 2, 4, 3, BlockList.cobblestoneMossy.id, BlockList.cobblestone.id, 0, 3, false);
    }
}
