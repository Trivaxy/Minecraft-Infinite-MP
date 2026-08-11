package net.minecraft.game.world.generation.features.underground;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.tileentity.container.PotEntity;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.Feature;

public class Ruins extends Feature {
    public static final Ruins INSTANCE = new Ruins();
    private int x0;
    private int y0;
    private int z0;
    private Random rand;
    private World world;
    private int rotation = 0;

    @Override
    public boolean place(World world, Random random, int x0, int y0, int z0) {
        this.x0 = x0;
        this.y0 = y0;
        this.z0 = z0;
        this.rand = random;
        this.world = world;
        this.rotation = this.rand.nextInt(4);

        for (int x = x0; x < x0 + 16; x++) {
            for (int z = z0; z < z0 + 16; z++) {
                if (world.getBlock(x, y0, z) == null) {
                    return false;
                }
            }
        }

        this.setBlockRotated(0, 0, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(0, 1, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(0, 2, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(0, 3, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(0, 4, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(0, 5, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(0, 6, new int[]{BlockList.susDirt.id, BlockList.stoneBrick.id, BlockList.ironBars.id, 0, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(0, 7, new int[]{BlockList.susDirt.id, BlockList.stoneBrick.id, BlockList.ironBars.id, 0, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(0, 8, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(0, 9, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(0, 10, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(0, 11, new int[]{BlockList.susDirt.id, BlockList.stoneBrick.id, BlockList.ironBars.id, 0, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(0, 12, new int[]{BlockList.susDirt.id, BlockList.stoneBrick.id, BlockList.ironBars.id, 0, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(0, 13, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(0, 14, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(0, 15, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(1, 0, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(1, 1, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(1, 2, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(1, 3, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(1, 4, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(1, 5, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(1, 6, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(1, 7, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(1, 8, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(1, 9, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(1, 10, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(1, 11, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(1, 12, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(1, 13, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(1, 14, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(1, 15, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(2, 0, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(2, 1, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(2, 2, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(2, 3, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(2, 4, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(2, 5, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(2, 6, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(2, 7, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(2, 8, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(2, 9, new int[]{BlockList.susDirt.id, -2, 0, BlockList.ironBars.id | 2048, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(2, 10, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(2, 11, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(2, 12, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(2, 13, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(2, 14, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(2, 15, new int[]{BlockList.susDirt.id, -2, 0, -2, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(3, 0, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(3, 1, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(3, 2, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(3, 3, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(3, 4, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(3, 5, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(3, 6, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(3, 7, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(3, 8, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(3, 9, new int[]{BlockList.susDirt.id, -3, 0, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(3, 10, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(3, 11, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(3, 12, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(3, 13, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(3, 14, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(3, 15, new int[]{BlockList.susDirt.id, -2, 0, BlockList.ironBars.id | 2048, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(4, 0, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(4, 1, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(4, 2, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(4, 3, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(4, 4, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(4, 5, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(4, 6, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(4, 7, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(4, 8, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(4, 9, new int[]{BlockList.susDirt.id, -2, 0, BlockList.ironBars.id | 2048, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(4, 10, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(4, 11, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(4, 12, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(4, 13, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(4, 14, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(4, 15, new int[]{BlockList.susDirt.id, -2, 0, -2, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(5, 0, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(5, 1, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(5, 2, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(5, 3, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(5, 4, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(5, 5, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(5, 6, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(5, 7, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(5, 8, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(5, 9, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(5, 10, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(5, 11, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(5, 12, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(5, 13, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(5, 14, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(5, 15, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(6, 0, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(6, 1, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(6, 2, new int[]{BlockList.susDirt.id, -2, 0, -2, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(6, 3, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(6, 4, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(6, 5, new int[]{BlockList.susDirt.id, -2, 0, -2, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(6, 6, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(6, 7, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(6, 8, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(6, 9, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(6, 10, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(6, 11, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(6, 12, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(6, 13, new int[]{BlockList.susDirt.id, -2, 0, -2, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(6, 14, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(6, 15, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(7, 0, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(7, 1, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(7, 2, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(7, 3, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(7, 4, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(7, 5, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(7, 6, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(7, 7, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(7, 8, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(7, 9, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(7, 10, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(7, 11, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(7, 12, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(7, 13, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(7, 14, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(7, 15, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(8, 0, new int[]{BlockList.susDirt.id, BlockList.stoneBrick.id, BlockList.ironBars.id, 0, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(8, 1, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(8, 2, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(8, 3, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(8, 4, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(8, 5, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(8, 6, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(8, 7, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(8, 8, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(8, 9, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(8, 10, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(8, 11, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(8, 12, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(8, 13, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(8, 14, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(8, 15, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(9, 0, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(9, 1, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(9, 2, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(9, 3, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(9, 4, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(9, 5, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(9, 6, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(9, 7, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(9, 8, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(9, 9, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(9, 10, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(9, 11, new int[]{BlockList.susDirt.id, BlockList.stoneBrick.id, BlockList.ironBars.id, 0, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(9, 12, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(9, 13, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(9, 14, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(9, 15, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(10, 0, new int[]{BlockList.susDirt.id, BlockList.stoneBrick.id, BlockList.ironBars.id, 0, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(10, 1, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(10, 2, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(10, 3, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(10, 4, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(10, 5, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(10, 6, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(10, 7, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(10, 8, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(10, 9, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(10, 10, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(10, 11, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(10, 12, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(10, 13, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(10, 14, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(10, 15, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(11, 0, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(11, 1, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(11, 2, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(11, 3, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(11, 4, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(11, 5, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(11, 6, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(11, 7, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(11, 8, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(11, 9, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(11, 10, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(11, 11, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(11, 12, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(11, 13, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(11, 14, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(11, 15, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(12, 0, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(12, 1, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(12, 2, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(12, 3, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(12, 4, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(12, 5, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(12, 6, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(12, 7, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(12, 8, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(12, 9, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(12, 10, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(12, 11, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(12, 12, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(12, 13, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(12, 14, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(12, 15, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(13, 0, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(13, 1, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(13, 2, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(13, 3, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(13, 4, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(13, 5, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(13, 6, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(13, 7, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(13, 8, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(13, 9, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(13, 10, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(13, 11, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(13, 12, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(13, 13, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(13, 14, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(13, 15, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(14, 0, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(14, 1, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(14, 2, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(14, 3, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(14, 4, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(14, 5, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(14, 6, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(14, 7, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(14, 8, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(14, 9, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(14, 10, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(14, 11, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(14, 12, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(14, 13, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(14, 14, new int[]{BlockList.susDirt.id, -6, 0});
        this.setBlockRotated(14, 15, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(15, 0, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(15, 1, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(15, 2, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(15, 3, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(15, 4, new int[]{BlockList.susDirt.id, BlockList.stoneBrick.id, BlockList.ironBars.id, 0, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(15, 5, new int[]{BlockList.susDirt.id, BlockList.stoneBrick.id, BlockList.ironBars.id, 0, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(15, 6, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(15, 7, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(15, 8, new int[]{BlockList.susDirt.id, BlockList.stoneBrick.id, BlockList.ironBars.id, 0, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(15, 9, new int[]{BlockList.susDirt.id, BlockList.stoneBrick.id, BlockList.ironBars.id, 0, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(15, 10, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(15, 11, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(15, 12, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(15, 13, new int[]{BlockList.susDirt.id, BlockList.stoneBrick.id, -2, 0, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(15, 14, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});
        this.setBlockRotated(15, 15, new int[]{BlockList.susDirt.id, -4, BlockList.stoneBrick.id, -2, 0});

        for (int i = 0; i < this.rand.nextInt(6) + 3; i++) {
            int x = this.rand.nextInt(14) + 1;
            int z = this.rand.nextInt(14) + 1;
            if (world.getBlockId(x0 + x, y0 + 1, z0 + z) == 0) {
                world.setBlockWithNotify(x0 + x, y0 + 1, z0 + z, BlockList.pot.id);
                PotEntity chest = (PotEntity)world.getTileEntity(x0 + x, y0 + 1, z0 + z);
                if (chest != null) {
                    int q = this.rand.nextInt(4) + 1;

                    for (int i1 = 0; i1 < q; i1++) {
                        chest.setSlotContents(this.rand.nextInt(chest.getSize()), this.getPotLoot(this.rand));
                    }
                }
            }
        }

        return true;
    }

    private void setBlockRotated(int x, int z, int[] blocks) {
        switch (this.rotation) {
            case 0:
                this.setBlockRelative(x, z, blocks);
                break;
            case 1:
                this.setBlockRelative(z, x, blocks);
                break;
            case 2:
                this.setBlockRelative(15 - x, 15 - z, blocks);
                break;
            case 3:
                this.setBlockRelative(15 - z, 15 - x, blocks);
        }
    }

    private void setBlockRelative(int x, int z, int[] blocks) {
        int maxHeight = 3 + this.rand.nextInt(2);
        x += this.x0;
        z += this.z0;
        int count = 0;
        int idx = 0;
        int blockID = 0;
        int metadata = 0;

        for (int y = 0; y < 7; y++) {
            if (count == 0) {
                int item = blocks[idx++];
                if (item == -1) {
                    continue;
                }

                if (item < -1) {
                    count = -item - 1;
                    item = blocks[idx++];
                }

                blockID = item & 4095;
                metadata = item >> 12 & 0xFF;
            } else {
                count--;
            }

            if (blockID > 290) {
                blockID = 0;
            }

            if (blockID == BlockList.stoneBrick.id) {
                metadata = this.rand.nextInt(3);
            }

            if (blockID == BlockList.susDirt.id && this.rand.nextInt(3) == 0) {
                blockID = BlockList.dirt.id;
            }

            if (y < maxHeight) {
                this.world.setBlockAndMetadataWithNotify(x, this.y0 + y, z, blockID, metadata);
            } else if (y == maxHeight && blockID != 0 && this.rand.nextBoolean() && this.world.getBlockId(x, this.y0 + y - 1, z) != 0) {
                this.world.setBlockAndMetadataWithNotify(x, this.y0 + y, z, BlockList.slabStoneBrick.id, this.rand.nextInt(3));
            }
        }

        int existingBlockID = this.world.getBlockId(x, this.y0 + 5, z);
        Block block = BlockList.blocks[existingBlockID];
        if (block != null && block.fullBlock()) {
            this.world.setBlock(x, this.y0 + 5, z, BlockList.stoneBrick.id);
        }
    }

    private ItemStack getPotLoot(Random random) {
        switch (random.nextInt(12)) {
            case 0:
                return new ItemStack(ItemList.scroll);
            case 1:
                return new ItemStack(ItemList.nuggetIron, random.nextInt(8) + 1);
            case 2:
                return new ItemStack(ItemList.rottenFlesh, random.nextInt(8) + 1);
            case 3:
                return new ItemStack(ItemList.nuggetGold, random.nextInt(4) + 1);
            case 4:
                return new ItemStack(ItemList.gunpowder, random.nextInt(4) + 1);
            case 5:
                return new ItemStack(ItemList.silk, random.nextInt(4) + 1);
            case 6:
                return new ItemStack(ItemList.bone, random.nextInt(6) + 1);
            case 7:
                return new ItemStack(BlockList.cobweb, random.nextInt(6) + 1);
            case 8:
                return new ItemStack(ItemList.diamond, random.nextInt(2) + 1);
            case 9:
                return new ItemStack(ItemList.ruby, random.nextInt(3) + 1);
            case 10:
                return new ItemStack(ItemList.sapphire, random.nextInt(3) + 1);
            case 11:
                return new ItemStack(ItemList.emerald, random.nextInt(3) + 1);
            default:
                return new ItemStack(ItemList.dyePowder, 1, 3);
        }
    }
}
