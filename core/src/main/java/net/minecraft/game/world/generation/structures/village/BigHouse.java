package net.minecraft.game.world.generation.structures.village;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;

public class BigHouse extends House {
    public BigHouse(World world, boolean rotated) {
        super(world, rotated);
    }

    @Override
    protected int buildingWidth() {
        return 12;
    }

    @Override
    protected int buildingLength() {
        return 12;
    }

    @Override
    protected int buildingHeight() {
        return 10;
    }

    @Override
    public void generate() {
    }

    @Override
    public void build() {
        this.drawClippedBoxRelative(0, 0, 1, 9, 0, 10, BlockList.planks.id, 0, true);
        this.generatePillars(0, 10);
        this.generatePillars(9, 10);
        this.generatePillars(0, 1);
        this.generatePillars(9, 1);
        this.drawClippedBoxRelative(1, 1, 2, 8, 5, 9, 0, 0, true);
        this.drawClippedBoxRandomizer(0, 1, 1, 9, 4, 1, BlockList.cobblestoneMossy.id, BlockList.cobblestone.id, 0, 4, true);
        this.drawClippedBoxRandomizer(0, 1, 10, 9, 4, 10, BlockList.cobblestoneMossy.id, BlockList.cobblestone.id, 0, 4, true);
        this.drawClippedBoxRandomizer(0, 1, 2, 0, 4, 9, BlockList.cobblestoneMossy.id, BlockList.cobblestone.id, 0, 4, true);
        this.drawClippedBoxRandomizer(9, 1, 2, 9, 4, 9, BlockList.cobblestoneMossy.id, BlockList.cobblestone.id, 0, 4, true);
        this.setBlockRelative(0, 2, 6, BlockList.glass.id, 0);
        this.setBlockRelative(0, 2, 8, BlockList.glass.id, 0);
        this.setBlockRelative(9, 2, 4, BlockList.glass.id, 0);
        this.setBlockRelative(9, 2, 7, BlockList.glass.id, 0);
        this.setBlockRelative(2, 2, 10, BlockList.glass.id, 0);
        this.setBlockRelative(4, 2, 10, BlockList.glass.id, 0);
        this.setBlockRelative(5, 2, 10, BlockList.glass.id, 0);
        this.setBlockRelative(7, 2, 10, BlockList.glass.id, 0);
        this.setBlockRelative(2, 2, 1, BlockList.glass.id, 0);
        this.setBlockRelative(4, 2, 1, BlockList.glass.id, 0);
        this.setBlockRelative(5, 2, 1, BlockList.glass.id, 0);
        this.setBlockRelative(7, 2, 1, BlockList.glass.id, 0);
        this.setBlockRelative(0, 1, 4, BlockList.doorWood.id, 0);
        this.setBlockRelative(0, 2, 4, BlockList.doorWood.id, 8);

        for (int i = -1; i < 4; i++) {
            this.drawClippedBoxRelative(i, 5 + i, 0, i, 5 + i, 11, BlockList.planks.id, 0, true);
            this.drawClippedBoxRelative(9 - i, 5 + i, 0, 9 - i, 5 + i, 11, BlockList.planks.id, 0, true);
            this.drawClippedBoxRandomizer(i + 2, 6 + i, 2, i + 2, 6 + i, 9, BlockList.cobweb.id, 0, 0, 10, true);
            this.drawClippedBoxRandomizer(7 - i, 6 + i, 2, 7 - i, 6 + i, 9, BlockList.cobweb.id, 0, 0, 10, true);
            this.drawClippedBoxRandomizer(1 + i, 5 + i, 1, 8 - i, 5 + i, 1, BlockList.cobblestoneMossy.id, BlockList.cobblestone.id, 0, 4, true);
            this.drawClippedBoxRandomizer(1 + i, 5 + i, 10, 8 - i, 5 + i, 10, BlockList.cobblestoneMossy.id, BlockList.cobblestone.id, 0, 4, true);
        }

        this.drawClippedBoxRelative(4, 9, 0, 5, 9, 11, BlockList.planks.id, 0, true);
        Random random = this.buildingRng;
        if (random.nextBoolean()) {
            this.addChestRelative(1, 1, 9);
        }

        if (random.nextBoolean()) {
            this.setBlockRelative(3, 1, 9, BlockList.workbench.id, 0);
        }

        if (random.nextBoolean()) {
            this.setBlockRelative(2, 1, 9, BlockList.stoneOvenIdle.id, 4);
        }

        if (random.nextBoolean()) {
            this.setBlockRelative(7, 1, 9, BlockList.bed.id, 3);
            this.setBlockRelative(8, 1, 9, BlockList.bed.id, 11);
        }

        if (random.nextInt(1) == 0) {
            this.drawClippedBoxRelative(4, 1, 5, 5, 1, 6, BlockList.table.id, 0, true);
            if (random.nextBoolean()) {
                int rotation = random.nextInt(2);
                this.setBlockRelative(3, 1, 5 + rotation, BlockList.chair.id, this.rotated ? 2 : 1);
            }

            if (random.nextBoolean()) {
                int rotation = random.nextInt(2);
                this.setBlockRelative(5 - rotation, 1, 7, BlockList.chair.id, this.rotated ? 3 : 0);
            }

            if (random.nextBoolean()) {
                int rotation = random.nextInt(2);
                this.setBlockRelative(4 + rotation, 1, 4, BlockList.chair.id, this.rotated ? 1 : 2);
            }

            if (random.nextBoolean()) {
                int rotation = random.nextInt(2);
                this.setBlockRelative(6, 1, 5 + rotation, BlockList.chair.id, this.rotated ? 0 : 3);
            }
        }

        for (int i = 2; i < 7; i++) {
            if (this.world.rand.nextInt(4) == 0) {
                this.addBookshelfRelative(i, 1, 2);
            }
        }
    }
}
