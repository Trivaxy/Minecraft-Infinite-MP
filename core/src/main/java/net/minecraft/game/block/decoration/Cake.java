package net.minecraft.game.block.decoration;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSource;
import net.minecraft.statistics.StatList;
import net.minecraft.statistics.achievement.AchievementList;

public class Cake extends Block {
    private Icon top;
    private Icon bottom;
    private Icon side;

    public Cake(int id) {
        super(id, MaterialList.cake);
        this.enableStats = false;
    }

    @Override
    public void modifyOutline(WorldSource world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        double offsetZ = 0.0625;
        double offsetX = (1 + meta * 2) / 16.0;
        this.setBounds(offsetX, 0.0, offsetZ, 1.0 - offsetZ, 0.5, 1.0 - offsetZ);
    }

    @Override
    public void forceBounds(int id) {
        this.setBounds(0.0625, 0.0, 0.0625, 0.9375, 0.5, 0.9375);
    }

    @Override
    public BoundingBox getCollisionBox(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        double offset = 0.0625;
        double length = (1 + meta * 2) / 16.0;
        return new BoundingBox(x + length, y, z + offset, x + 1 - offset, y + 0.5 - offset, z + 1 - offset);
    }

    @Override
    public BoundingBox getOutline(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        double offset = 0.0625;
        double length = (1 + meta * 2) / 16.0;
        return new BoundingBox(x + length, y, z + offset, x + 1 - offset, y + 0.5, z + 1 - offset);
    }

    @Override
    public Icon getIcon(int side, int meta) {
        return side == 1 ? this.top : (side == 0 ? this.bottom : (meta > 0 && side == 4 ? this.side : this.blockIcon));
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("food/pieSide");
        this.side = ir.registerIcon("food/pieSideEaten");
        this.top = ir.registerIcon("food/pieTop");
        this.bottom = ir.registerIcon("food/pieBottom");
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public boolean blockActivated(World world, int x, int y, int z, Player player) {
        this.eatCakeSlice(world, x, y, z, player);
        player.addStat(StatList.pieUse, 1);
        return true;
    }

    private void eatCakeSlice(World world, int x, int y, int z, Player player) {
        if (player.canEat(false)) {
            if (player.properties.getHunger()) {
                player.getFoodStats().addStats(2, 0.1F);
            } else {
                player.heal(3);
            }

            int meta = world.getBlockMetadata(x, y, z) + 1;
            if (meta >= 6) {
                world.setBlockWithNotify(x, y, z, 0);
            } else {
                world.setBlockMetadataWithNotify(x, y, z, meta);
                world.markForUpdate(x, y, z);
            }
        }

        player.triggerAchievement(AchievementList.bakeCake);
    }

    @Override
    public boolean placeableAt(World world, int x, int y, int z) {
        return world.getBlockId(x, y, z) == 0 && (world.isSolid(x, y - 1, z) || world.getBlockId(x, y - 1, z) == BlockList.table.id);
    }

    @Override
    public void onNeighborChange(World world, int x, int y, int z, int id) {
        if (!this.canStay(world, x, y, z)) {
            this.dropBlock(world, x, y, z, world.getBlockMetadata(x, y, z));
            world.setBlockWithNotify(x, y, z, 0);
        }
    }

    @Override
    public boolean canStay(World world, int x, int y, int z) {
        return world.getBlockMaterial(x, y - 1, z).isSolid();
    }

    @Override
    public int quantityDropped(Random rand) {
        return 0;
    }

    @Override
    public int dropID(int meta, Random rand) {
        return 0;
    }

    @Override
    public int pickID(int meta) {
        return ItemList.cake.id;
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.CAKE;
    }

    @Override
    public boolean particles3D() {
        return false;
    }
}
