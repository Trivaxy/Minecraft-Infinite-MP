package net.minecraft.game.world.generation.structures.oceanruins;

import net.minecraft.game.world.World;

public class DerelictBuilding1 extends OceanRuinsPiece {
    public DerelictBuilding1(World world, boolean rotated) {
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
        return 7;
    }

    @Override
    public void generate() {
    }

    @Override
    public void build() {
        this.setBlockColumnRelative(1, 1, new int[]{73});
        this.setBlockColumnRelative(1, 5, new int[]{73});
        this.setBlockColumnRelative(1, 8, new int[]{73});
        this.setBlockColumnRelative(1, 12, new int[]{73});
        this.setBlockColumnRelative(2, 0, new int[]{73});
        this.setBlockColumnRelative(2, 1, new int[]{-3, 73});
        this.setBlockColumnRelative(2, 4, new int[]{290});
        this.setBlockColumnRelative(2, 5, new int[]{-3, 73});
        this.setBlockColumnRelative(2, 8, new int[]{-3, 73});
        this.setBlockColumnRelative(2, 9, new int[]{290});
        this.setBlockColumnRelative(2, 12, new int[]{-3, 73});
        this.setBlockColumnRelative(2, 13, new int[]{73});
        this.setBlockColumnRelative(3, 1, new int[]{290});
        this.setBlockColumnRelative(3, 4, new int[]{290});
        this.setBlockColumnRelative(3, 5, new int[]{290});
        this.setBlockColumnRelative(3, 8, new int[]{290});
        this.setBlockColumnRelative(4, 4, new int[]{290});
        this.setBlockColumnRelative(5, 8, new int[]{290});
        this.setBlockColumnRelative(5, 9, new int[]{290});
        this.setBlockColumnRelative(5, 12, new int[]{290});
        this.setBlockColumnRelative(6, 0, new int[]{73});
        this.setBlockColumnRelative(6, 1, new int[]{-3, 73});
        this.setBlockColumnRelative(6, 4, new int[]{290});
        this.setBlockColumnRelative(6, 5, new int[]{-4, 73, 4});
        this.setBlockColumnRelative(6, 6, new int[]{73, -3, -1, 4});
        this.setBlockColumnRelative(6, 7, new int[]{73, -3, -1, 4});
        this.setBlockColumnRelative(6, 8, new int[]{-4, 73, 41});
        this.setBlockColumnRelative(6, 9, new int[]{-2, 290});
        this.setBlockColumnRelative(6, 12, new int[]{-3, 73});
        this.setBlockColumnRelative(6, 13, new int[]{73});
        this.setBlockColumnRelative(7, 5, new int[]{73, -3, -1, 4});
        this.setBlockColumnRelative(7, 6, new int[]{290, -3, -1, 4});
        this.setBlockColumnRelative(7, 7, new int[]{290, -3, -1, 4});
        this.setBlockColumnRelative(7, 8, new int[]{73, -3, -1, 4});
        this.setBlockColumnRelative(8, 5, new int[]{73, -3, -1, 4});
        this.setBlockColumnRelative(8, 6, new int[]{290, 0, -2, -1, 4});
        this.setBlockColumnRelative(8, 7, new int[]{290, -3, -1, 4});
        this.setBlockColumnRelative(8, 8, new int[]{73, -3, -1, 4});
        this.setBlockColumnRelative(9, 0, new int[]{73});
        this.setBlockColumnRelative(9, 1, new int[]{-3, 73});
        this.setBlockColumnRelative(9, 2, new int[]{290});
        this.setBlockColumnRelative(9, 5, new int[]{-4, 73, 41});
        this.setBlockColumnRelative(9, 6, new int[]{73, -3, -1, 4});
        this.setBlockColumnRelative(9, 7, new int[]{73, -3, -1, 4});
        this.setBlockColumnRelative(9, 8, new int[]{-4, 73, 41});
        this.setBlockColumnRelative(9, 12, new int[]{-3, 73});
        this.setBlockColumnRelative(9, 13, new int[]{73});
        this.setBlockColumnRelative(10, 1, new int[]{290});
        this.setBlockColumnRelative(10, 4, new int[]{290});
        this.setBlockColumnRelative(10, 6, new int[]{290});
        this.setBlockColumnRelative(10, 7, new int[]{290});
        this.setBlockColumnRelative(11, 1, new int[]{290});
        this.setBlockColumnRelative(11, 6, new int[]{290});
        this.setBlockColumnRelative(13, 0, new int[]{73});
        this.setBlockColumnRelative(13, 1, new int[]{-3, 73});
        this.setBlockColumnRelative(13, 5, new int[]{-3, 73});
        this.setBlockColumnRelative(13, 6, new int[]{290});
        this.setBlockColumnRelative(13, 8, new int[]{-3, 73});
        this.setBlockColumnRelative(13, 12, new int[]{-3, 73});
        this.setBlockColumnRelative(13, 13, new int[]{73});
        this.setBlockColumnRelative(14, 1, new int[]{73});
        this.setBlockColumnRelative(14, 5, new int[]{73});
        this.setBlockColumnRelative(14, 8, new int[]{73});
        this.setBlockColumnRelative(14, 12, new int[]{73});
        this.addChestRelative(8, 1, 6);
    }
}
