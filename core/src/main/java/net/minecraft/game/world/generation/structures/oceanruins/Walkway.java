package net.minecraft.game.world.generation.structures.oceanruins;

import net.minecraft.game.world.World;

public class Walkway extends OceanRuinsPiece {
    public Walkway(World world, boolean rotated) {
        super(world, rotated);
    }

    @Override
    protected int buildingWidth() {
        return 15;
    }

    @Override
    protected int buildingLength() {
        return 7;
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
        this.setBlockColumnRelative(0, 1, new int[]{290, 73});
        this.setBlockColumnRelative(0, 2, new int[]{290, 73});
        this.setBlockColumnRelative(0, 4, new int[]{290, -2, 73});
        this.setBlockColumnRelative(1, 1, new int[]{290});
        this.setBlockColumnRelative(1, 2, new int[]{290});
        this.setBlockColumnRelative(2, 0, new int[]{290, 73});
        this.setBlockColumnRelative(2, 1, new int[]{290});
        this.setBlockColumnRelative(2, 4, new int[]{290, -2, 73});
        this.setBlockColumnRelative(3, 0, new int[]{290});
        this.setBlockColumnRelative(3, 1, new int[]{290});
        this.setBlockColumnRelative(3, 3, new int[]{290});
        this.setBlockColumnRelative(3, 4, new int[]{290});
        this.setBlockColumnRelative(4, 0, new int[]{290, -3, 73});
        this.setBlockColumnRelative(4, 1, new int[]{290, -3, -1, 73});
        this.setBlockColumnRelative(4, 2, new int[]{290});
        this.setBlockColumnRelative(4, 3, new int[]{290, -3, -1, 73});
        this.setBlockColumnRelative(4, 4, new int[]{290, -3, 73});
        this.setBlockColumnRelative(5, 0, new int[]{290});
        this.setBlockColumnRelative(5, 1, new int[]{290});
        this.setBlockColumnRelative(5, 2, new int[]{290});
        this.setBlockColumnRelative(5, 3, new int[]{290});
        this.setBlockColumnRelative(5, 4, new int[]{290});
        this.setBlockColumnRelative(6, 0, new int[]{290, 4, 13});
        this.setBlockColumnRelative(6, 1, new int[]{290});
        this.setBlockColumnRelative(6, 2, new int[]{290});
        this.setBlockColumnRelative(6, 3, new int[]{290, -3, -1, 73});
        this.setBlockColumnRelative(6, 4, new int[]{290, -3, 73});
        this.setBlockColumnRelative(7, 0, new int[]{290});
        this.setBlockColumnRelative(7, 1, new int[]{290});
        this.setBlockColumnRelative(7, 2, new int[]{290});
        this.setBlockColumnRelative(7, 3, new int[]{290});
        this.setBlockColumnRelative(7, 4, new int[]{290});
        this.setBlockColumnRelative(8, 0, new int[]{290, -3, 73});
        this.setBlockColumnRelative(8, 1, new int[]{290, -3, -1, 73});
        this.setBlockColumnRelative(8, 2, new int[]{290});
        this.setBlockColumnRelative(8, 3, new int[]{290, 73});
        this.setBlockColumnRelative(8, 4, new int[]{290, 73, 4, 73});
        this.setBlockColumnRelative(9, 0, new int[]{290});
        this.setBlockColumnRelative(9, 1, new int[]{290});
        this.setBlockColumnRelative(9, 2, new int[]{290});
        this.setBlockColumnRelative(9, 3, new int[]{290});
        this.setBlockColumnRelative(9, 4, new int[]{290});
        this.setBlockColumnRelative(10, 0, new int[]{290, -3, 73});
        this.setBlockColumnRelative(10, 1, new int[]{290, -3, -1, 73});
        this.setBlockColumnRelative(10, 3, new int[]{290, -3, -1, 73});
        this.setBlockColumnRelative(10, 4, new int[]{290, -3, 73});
        this.setBlockColumnRelative(11, 0, new int[]{290});
        this.setBlockColumnRelative(11, 1, new int[]{290});
        this.setBlockColumnRelative(11, 2, new int[]{290});
        this.setBlockColumnRelative(11, 3, new int[]{290});
        this.setBlockColumnRelative(11, 4, new int[]{290});
        this.setBlockColumnRelative(12, 0, new int[]{290, -3, 73});
        this.setBlockColumnRelative(12, 1, new int[]{290, -3, -1, 73});
        this.setBlockColumnRelative(12, 3, new int[]{290});
        this.setBlockColumnRelative(12, 4, new int[]{290, -2, 73, 4});
        this.setBlockColumnRelative(13, 0, new int[]{290});
        this.setBlockColumnRelative(13, 1, new int[]{290});
        this.setBlockColumnRelative(13, 3, new int[]{290});
        this.setBlockColumnRelative(13, 4, new int[]{290});
        this.setBlockColumnRelative(14, 0, new int[]{290, -2, 73});
        this.setBlockColumnRelative(14, 3, new int[]{-4, -1, 73});
        this.setBlockColumnRelative(14, 4, new int[]{290, -3, 73});
    }
}
