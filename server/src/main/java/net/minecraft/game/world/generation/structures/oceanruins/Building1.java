package net.minecraft.game.world.generation.structures.oceanruins;

import net.minecraft.game.world.World;

public class Building1 extends OceanRuinsPiece {
    public Building1(World world, boolean rotated) {
        super(world, rotated);
    }

    @Override
    protected int buildingWidth() {
        return 15;
    }

    @Override
    protected int buildingLength() {
        return 15;
    }

    @Override
    protected int buildingHeight() {
        return 12;
    }

    @Override
    public void generate() {
    }

    @Override
    public void build() {
        this.setBlockColumnRelative(0, 1, new int[]{290});
        this.setBlockColumnRelative(0, 5, new int[]{290});
        this.setBlockColumnRelative(0, 6, new int[]{290});
        this.setBlockColumnRelative(0, 7, new int[]{290});
        this.setBlockColumnRelative(0, 8, new int[]{290});
        this.setBlockColumnRelative(0, 9, new int[]{290});
        this.setBlockColumnRelative(0, 11, new int[]{290});
        this.setBlockColumnRelative(0, 12, new int[]{290});
        this.setBlockColumnRelative(1, 1, new int[]{290});
        this.setBlockColumnRelative(1, 2, new int[]{290});
        this.setBlockColumnRelative(1, 5, new int[]{290});
        this.setBlockColumnRelative(1, 6, new int[]{290, 48, 4, -2, 73});
        this.setBlockColumnRelative(1, 7, new int[]{290, -2, 48, 4, 73});
        this.setBlockColumnRelative(1, 8, new int[]{290, 48, 4, -2, 73});
        this.setBlockColumnRelative(1, 9, new int[]{-2, 290});
        this.setBlockColumnRelative(1, 10, new int[]{-2, 290});
        this.setBlockColumnRelative(1, 11, new int[]{290});
        this.setBlockColumnRelative(1, 12, new int[]{-2, 290});
        this.setBlockColumnRelative(1, 13, new int[]{290});
        this.setBlockColumnRelative(2, 0, new int[]{290});
        this.setBlockColumnRelative(2, 2, new int[]{290});
        this.setBlockColumnRelative(2, 3, new int[]{290});
        this.setBlockColumnRelative(2, 4, new int[]{290});
        this.setBlockColumnRelative(2, 5, new int[]{290, -2, 48, 4, 73});
        this.setBlockColumnRelative(2, 6, new int[]{290, -4, -1, 73});
        this.setBlockColumnRelative(2, 7, new int[]{290, -4, -1, 73});
        this.setBlockColumnRelative(2, 8, new int[]{290, -4, -1, 73});
        this.setBlockColumnRelative(2, 9, new int[]{290, -2, 48, 4, 73});
        this.setBlockColumnRelative(2, 10, new int[]{-2, 290});
        this.setBlockColumnRelative(2, 11, new int[]{-2, 290});
        this.setBlockColumnRelative(2, 12, new int[]{290, -2, 73, 8338});
        this.setBlockColumnRelative(2, 13, new int[]{290});
        this.setBlockColumnRelative(3, 2, new int[]{290});
        this.setBlockColumnRelative(3, 3, new int[]{290});
        this.setBlockColumnRelative(3, 4, new int[]{290, 4, -3, 73});
        this.setBlockColumnRelative(3, 5, new int[]{5, -4, -1, 73});
        this.setBlockColumnRelative(3, 6, new int[]{5, -5, -1, 43});
        this.setBlockColumnRelative(3, 7, new int[]{290, -5, -1, 43});
        this.setBlockColumnRelative(3, 8, new int[]{290, -5, -1, 43});
        this.setBlockColumnRelative(3, 9, new int[]{290, -4, -1, 73});
        this.setBlockColumnRelative(3, 10, new int[]{290, 48, 4, -2, 73});
        this.setBlockColumnRelative(3, 11, new int[]{290});
        this.setBlockColumnRelative(3, 12, new int[]{290});
        this.setBlockColumnRelative(4, 0, new int[]{290});
        this.setBlockColumnRelative(4, 1, new int[]{290});
        this.setBlockColumnRelative(4, 2, new int[]{-2, 290});
        this.setBlockColumnRelative(4, 3, new int[]{-2, 290});
        this.setBlockColumnRelative(4, 4, new int[]{290, 48, 4, -2, 73});
        this.setBlockColumnRelative(4, 5, new int[]{5, -4, -1, 73});
        this.setBlockColumnRelative(4, 6, new int[]{290, -5, -1, 43});
        this.setBlockColumnRelative(4, 7, new int[]{290, -5, -1, 41});
        this.setBlockColumnRelative(4, 8, new int[]{5, -5, -1, 43});
        this.setBlockColumnRelative(4, 9, new int[]{290, -4, -1, 73});
        this.setBlockColumnRelative(4, 10, new int[]{290, -2, -1, 48, 4});
        this.setBlockColumnRelative(4, 11, new int[]{290});
        this.setBlockColumnRelative(5, 0, new int[]{290});
        this.setBlockColumnRelative(5, 1, new int[]{-2, 290});
        this.setBlockColumnRelative(5, 2, new int[]{-2, 290});
        this.setBlockColumnRelative(5, 3, new int[]{-3, 290});
        this.setBlockColumnRelative(5, 4, new int[]{290, -2, 48, 4, 73});
        this.setBlockColumnRelative(5, 5, new int[]{-2, 13, -3, -1, 73});
        this.setBlockColumnRelative(5, 6, new int[]{290, -5, -1, 43});
        this.setBlockColumnRelative(5, 7, new int[]{5, -5, -1, 43});
        this.setBlockColumnRelative(5, 8, new int[]{5, -5, -1, 43});
        this.setBlockColumnRelative(5, 9, new int[]{290, -4, -1, 4});
        this.setBlockColumnRelative(5, 10, new int[]{290, 48, -2, 4, 73});
        this.setBlockColumnRelative(5, 11, new int[]{290});
        this.setBlockColumnRelative(5, 12, new int[]{290});
        this.setBlockColumnRelative(6, 1, new int[]{290});
        this.setBlockColumnRelative(6, 2, new int[]{290});
        this.setBlockColumnRelative(6, 3, new int[]{-2, 290});
        this.setBlockColumnRelative(6, 4, new int[]{-2, 290});
        this.setBlockColumnRelative(6, 5, new int[]{290, 48, 4, -2, 73});
        this.setBlockColumnRelative(6, 6, new int[]{-2, 290, -3, -1, 48});
        this.setBlockColumnRelative(6, 7, new int[]{290, -4, -1, 73});
        this.setBlockColumnRelative(6, 8, new int[]{290, -4, -1, 73});
        this.setBlockColumnRelative(6, 9, new int[]{290, -2, 48, 4, 73});
        this.setBlockColumnRelative(6, 10, new int[]{290});
        this.setBlockColumnRelative(6, 11, new int[]{290});
        this.setBlockColumnRelative(6, 12, new int[]{290, 73, 8338});
        this.setBlockColumnRelative(6, 13, new int[]{290});
        this.setBlockColumnRelative(7, 3, new int[]{290});
        this.setBlockColumnRelative(7, 4, new int[]{290});
        this.setBlockColumnRelative(7, 5, new int[]{290, 48, -2, 4});
        this.setBlockColumnRelative(7, 6, new int[]{-3, 290, -2, 73});
        this.setBlockColumnRelative(7, 7, new int[]{290, -2, 4, -2, 73});
        this.setBlockColumnRelative(7, 8, new int[]{290, 4, -3, 73});
        this.setBlockColumnRelative(7, 9, new int[]{-2, 290});
        this.setBlockColumnRelative(7, 10, new int[]{290});
        this.setBlockColumnRelative(7, 11, new int[]{290});
        this.setBlockColumnRelative(7, 12, new int[]{290});
        this.setBlockColumnRelative(7, 13, new int[]{290});
        this.setBlockColumnRelative(8, 2, new int[]{290});
        this.setBlockColumnRelative(8, 3, new int[]{-2, 290});
        this.setBlockColumnRelative(8, 4, new int[]{290, 48, 4, -3, 73, -3, -1, 48});
        this.setBlockColumnRelative(8, 5, new int[]{290, -2, -1, -2, 73, 4, 73});
        this.setBlockColumnRelative(8, 6, new int[]{290, -2, -1, -3, 73, -3, -1, 48});
        this.setBlockColumnRelative(8, 7, new int[]{290, -2, 4, 73, 13});
        this.setBlockColumnRelative(8, 8, new int[]{-4, 290});
        this.setBlockColumnRelative(8, 9, new int[]{-2, 290});
        this.setBlockColumnRelative(8, 10, new int[]{-2, 290});
        this.setBlockColumnRelative(8, 11, new int[]{290});
        this.setBlockColumnRelative(8, 12, new int[]{290});
        this.setBlockColumnRelative(8, 13, new int[]{290});
        this.setBlockColumnRelative(8, 14, new int[]{290});
        this.setBlockColumnRelative(9, 0, new int[]{290});
        this.setBlockColumnRelative(9, 1, new int[]{290});
        this.setBlockColumnRelative(9, 2, new int[]{-2, 290});
        this.setBlockColumnRelative(9, 3, new int[]{290, 48, 4, -2, 73, -4, 43, 48});
        this.setBlockColumnRelative(9, 4, new int[]{-2, 13, -3, -1, 5, -4, -1, 43, -1});
        this.setBlockColumnRelative(9, 5, new int[]{290, -4, -1, 5, -4, -1, 43, -1});
        this.setBlockColumnRelative(9, 6, new int[]{290, -4, -1, 5, -4, -1, 43, -1});
        this.setBlockColumnRelative(9, 7, new int[]{290, 4, -3, 73, -4, 43, 48});
        this.setBlockColumnRelative(9, 8, new int[]{-4, 290});
        this.setBlockColumnRelative(9, 9, new int[]{-3, 290});
        this.setBlockColumnRelative(9, 10, new int[]{-3, 290});
        this.setBlockColumnRelative(9, 11, new int[]{-2, 290});
        this.setBlockColumnRelative(9, 12, new int[]{290});
        this.setBlockColumnRelative(9, 13, new int[]{290});
        this.setBlockColumnRelative(9, 14, new int[]{290});
        this.setBlockColumnRelative(10, 1, new int[]{290});
        this.setBlockColumnRelative(10, 2, new int[]{290, 48, 4, -3, 73, -3, -1, 48});
        this.setBlockColumnRelative(10, 3, new int[]{0, -2, 290, -2, -1, 5, -4, -1, 43, -1});
        this.setBlockColumnRelative(10, 4, new int[]{-2, 290, -3, -1, 5, -4, -1, 43, 73});
        this.setBlockColumnRelative(10, 5, new int[]{290, -4, -1, 5, -5, -1, 73});
        this.setBlockColumnRelative(10, 6, new int[]{290, -4, -1, 5, -4, -1, 43, 73});
        this.setBlockColumnRelative(10, 7, new int[]{290, -4, -1, 5, -4, -1, 43, -1});
        this.setBlockColumnRelative(10, 8, new int[]{290, 48, 4, -3, 73, -3, -1, 48});
        this.setBlockColumnRelative(10, 9, new int[]{-4, 290});
        this.setBlockColumnRelative(10, 10, new int[]{-3, 290});
        this.setBlockColumnRelative(10, 11, new int[]{-2, 290});
        this.setBlockColumnRelative(10, 12, new int[]{290});
        this.setBlockColumnRelative(10, 13, new int[]{290});
        this.setBlockColumnRelative(11, 0, new int[]{290});
        this.setBlockColumnRelative(11, 1, new int[]{-3, 290});
        this.setBlockColumnRelative(11, 2, new int[]{-3, 4, -4, 73});
        this.setBlockColumnRelative(11, 3, new int[]{-2, 290, -3, -1, 5, -4, -1, 43, -1});
        this.setBlockColumnRelative(11, 4, new int[]{5, -4, -1, 5, -5, -1, 73});
        this.setBlockColumnRelative(11, 5, new int[]{5, -4, -1, 5, -5, -1, 41});
        this.setBlockColumnRelative(11, 6, new int[]{290, -4, -1, 5, -5, -1, 73});
        this.setBlockColumnRelative(11, 7, new int[]{5, 13, -3, -1, 5, -4, -1, 43, -1});
        this.setBlockColumnRelative(11, 8, new int[]{290, 4, -4, 73, 48});
        this.setBlockColumnRelative(11, 9, new int[]{-3, 290});
        this.setBlockColumnRelative(11, 10, new int[]{-2, 290});
        this.setBlockColumnRelative(11, 11, new int[]{290});
        this.setBlockColumnRelative(11, 12, new int[]{290});
        this.setBlockColumnRelative(11, 13, new int[]{290});
        this.setBlockColumnRelative(11, 14, new int[]{290});
        this.setBlockColumnRelative(12, 0, new int[]{290});
        this.setBlockColumnRelative(12, 1, new int[]{-2, 290});
        this.setBlockColumnRelative(12, 2, new int[]{290, 48, -2, 73, 4, 73, -3, -1, 48});
        this.setBlockColumnRelative(12, 3, new int[]{5, -4, -1, 5, -4, -1, 43, -1});
        this.setBlockColumnRelative(12, 4, new int[]{5, -4, -1, 5, -4, -1, 43, 73});
        this.setBlockColumnRelative(12, 5, new int[]{290, -4, -1, 5, -5, -1, 73});
        this.setBlockColumnRelative(12, 6, new int[]{-2, 290, -3, -1, 5, -4, -1, 43, 73});
        this.setBlockColumnRelative(12, 7, new int[]{-2, 290, -3, -1, 5, -4, -1, 43, -1});
        this.setBlockColumnRelative(12, 8, new int[]{290, 48, 4, -2, 73, 4, -3, -1, 48});
        this.setBlockColumnRelative(12, 9, new int[]{-2, 13});
        this.setBlockColumnRelative(12, 10, new int[]{290});
        this.setBlockColumnRelative(12, 11, new int[]{290});
        this.setBlockColumnRelative(12, 13, new int[]{290});
        this.setBlockColumnRelative(12, 14, new int[]{290});
        this.setBlockColumnRelative(13, 1, new int[]{290});
        this.setBlockColumnRelative(13, 2, new int[]{290});
        this.setBlockColumnRelative(13, 3, new int[]{290, -2, 4, -2, 73, -4, 43, 48});
        this.setBlockColumnRelative(13, 4, new int[]{290, -4, -1, 5, -4, -1, 43, -1});
        this.setBlockColumnRelative(13, 5, new int[]{-2, 290, -3, -1, 5, -4, -1, 43, -1});
        this.setBlockColumnRelative(13, 6, new int[]{290, 0, 13, -2, -1, 5, -4, -1, 43, -1});
        this.setBlockColumnRelative(13, 7, new int[]{290, 48, 4, -2, 73, -4, 43, 48});
        this.setBlockColumnRelative(13, 8, new int[]{290});
        this.setBlockColumnRelative(13, 9, new int[]{290});
        this.setBlockColumnRelative(13, 10, new int[]{290});
        this.setBlockColumnRelative(13, 11, new int[]{290});
        this.setBlockColumnRelative(13, 12, new int[]{290});
        this.setBlockColumnRelative(13, 13, new int[]{290});
        this.setBlockColumnRelative(14, 2, new int[]{290});
        this.setBlockColumnRelative(14, 3, new int[]{290});
        this.setBlockColumnRelative(14, 4, new int[]{290, 48, -2, 73, 4, 73, -3, -1, 48});
        this.setBlockColumnRelative(14, 5, new int[]{290, -2, 4, -2, 73, 4, 73});
        this.setBlockColumnRelative(14, 6, new int[]{290, 48, 4, -3, 73, -3, -1, 48});
        this.setBlockColumnRelative(14, 7, new int[]{290});
        this.setBlockColumnRelative(14, 9, new int[]{290});
        this.setBlockColumnRelative(14, 10, new int[]{290});
        this.setBlockColumnRelative(14, 11, new int[]{290});
        this.setBlockColumnRelative(14, 12, new int[]{290});
        this.addPot(10, 0, 3);
        this.addChestRelative(13, 1, 6);
    }
}
