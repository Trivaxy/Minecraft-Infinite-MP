package net.minecraft.game.world.generation.structures.village;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;

public class SmallHouse extends House {
    public SmallHouse(World world, boolean rotated) {
        super(world, rotated);
    }

    @Override
    protected int buildingWidth() {
        return 6;
    }

    @Override
    protected int buildingLength() {
        return 11;
    }

    @Override
    protected int buildingHeight() {
        return 7;
    }

    @Override
    public void generate() {
    }

    @Override
    public void build() {
        this.drawClippedBoxRelative(0, 0, 1, 5, 0, 9, BlockList.planks.id, 0, true);
        this.generatePillars(0, 1);
        this.generatePillars(0, 9);
        this.generatePillars(5, 1);
        this.generatePillars(5, 9);
        this.drawClippedBoxRelative(1, 1, 2, 4, 5, 8, 0, 0, true);
        this.drawClippedBoxRandomizer(0, 1, 1, 5, 3, 1, BlockList.cobblestoneMossy.id, BlockList.cobblestone.id, 0, 4, true);
        this.drawClippedBoxRandomizer(0, 1, 9, 5, 3, 9, BlockList.cobblestoneMossy.id, BlockList.cobblestone.id, 0, 4, true);
        this.drawClippedBoxRandomizer(0, 1, 2, 0, 3, 8, BlockList.cobblestoneMossy.id, BlockList.cobblestone.id, 0, 4, true);
        this.drawClippedBoxRandomizer(5, 1, 2, 5, 3, 8, BlockList.cobblestoneMossy.id, BlockList.cobblestone.id, 0, 4, true);
        this.setBlockRelative(0, 2, 2, BlockList.glass.id, 0);
        this.setBlockRelative(0, 2, 6, BlockList.glass.id, 0);
        this.setBlockRelative(0, 2, 8, BlockList.glass.id, 0);
        this.setBlockRelative(5, 2, 3, BlockList.glass.id, 0);
        this.setBlockRelative(5, 2, 5, BlockList.glass.id, 0);
        this.setBlockRelative(5, 2, 7, BlockList.glass.id, 0);
        this.setBlockRelative(2, 2, 1, BlockList.glass.id, 0);
        this.setBlockRelative(3, 2, 1, BlockList.glass.id, 0);
        this.setBlockRelative(2, 2, 9, BlockList.glass.id, 0);
        this.setBlockRelative(3, 2, 9, BlockList.glass.id, 0);
        this.setBlockRelative(0, 1, 4, BlockList.doorWood.id, 0);
        this.setBlockRelative(0, 2, 4, BlockList.doorWood.id, 8);

        for (int i = 0; i < 2; i++) {
            this.drawClippedBoxRelative(i, 4 + i, 0, i, 4 + i, 10, BlockList.planks.id, 0, true);
            this.drawClippedBoxRelative(5 - i, 4 + i, 0, 5 - i, 4 + i, 10, BlockList.planks.id, 0, true);
            this.drawClippedBoxRandomizer(i + 1, 4 + i, 2, i + 1, 4 + i, 8, BlockList.cobweb.id, 0, 0, 10, true);
            this.drawClippedBoxRandomizer(4 - i, 4 + i, 2, 4 - i, 4 + i, 8, BlockList.cobweb.id, 0, 0, 10, true);
            this.drawClippedBoxRandomizer(1 + i, 4 + i, 1, 4 - i, 4 + i, 1, BlockList.cobblestoneMossy.id, BlockList.cobblestone.id, 0, 4, true);
            this.drawClippedBoxRandomizer(1 + i, 4 + i, 9, 4 - i, 4 + i, 9, BlockList.cobblestoneMossy.id, BlockList.cobblestone.id, 0, 4, true);
        }

        this.drawClippedBoxRelative(2, 6, 0, 3, 6, 10, BlockList.planks.id, 0, true);
        Random random = this.buildingRng;
        if (random.nextBoolean()) {
            this.addChestRelative(1, 1, 8);
        }

        if (random.nextBoolean()) {
            this.setBlockRelative(3, 1, 8, BlockList.workbench.id, 0);
        }

        if (random.nextBoolean()) {
            this.setBlockRelative(2, 1, 8, BlockList.stoneOvenIdle.id, 2);
        }

        if (random.nextBoolean()) {
            this.setBlockRelative(4, 1, 2, BlockList.bed.id, 10);
            this.setBlockRelative(4, 1, 3, BlockList.bed.id, 2);
        }

        if (random.nextInt(1) == 0) {
            this.setBlockRelative(1, 1, 2, BlockList.table.id, 0);
            if (random.nextBoolean()) {
                this.setBlockRelative(2, 1, 2, BlockList.chair.id, this.rotated ? 0 : 3);
            }
        }

        if (random.nextBoolean()) {
            this.addBookshelfRelative(4, 1, 5);
        }

        if (random.nextBoolean()) {
            this.addBookshelfRelative(4, 1, 6);
        }
    }
}
