package net.minecraft.game.block.machines;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.block.tileentity.smelters.GeneratorEntity;
import net.minecraft.game.block.utility.Furnace;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.statistics.achievement.AchievementList;
import net.minecraft.util.math.MathHelper;

public final class GeneratorBlock extends Furnace {
    public GeneratorBlock(int blockID, boolean status) {
        super(blockID, status);
        this.material = MaterialList.metal;
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("machines/generatorSide");
        this.front = ir.registerIcon(this.active ? "machines/generatorLit" : "machines/generator");
        this.top = ir.registerIcon("machines/blockIron_top");
        this.bottom = ir.registerIcon("machines/blockIron_bottom");
    }

    @Override
    public final boolean blockActivated(World world, int xCoord, int yCoord, int zCoord, Player player) {
        if (!world.isOnline) {
            GeneratorEntity tef = (GeneratorEntity)world.getTileEntity(xCoord, yCoord, zCoord);
            player.displayGUIGenerator(tef);
        }

        return true;
    }

    @Override
    public final TileEntity getBlockEntity() {
        return new GeneratorEntity();
    }

    @Override
    public int dropID(int id, Random random) {
        return BlockList.generator.id;
    }

    @Override
    public int pickID(int id) {
        return BlockList.generator.id;
    }

    @Override
    public boolean powersDirectly(World world, int x, int y, int z, int id) {
        return this.active;
    }

    @Override
    public boolean powersIndirectly(World world, int x, int y, int z, int id) {
        return this.active;
    }

    @Override
    public boolean providesPower() {
        return true;
    }

    public static void updateState(boolean active, World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        keepInventory = true;
        if (active) {
            world.setBlockWithNotify(x, y, z, BlockList.generatorActive.id);
            Player player = world.getClosestPlayer(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z), 8.0);
            if (player != null) {
                player.triggerAchievement(AchievementList.generator);
            }
        } else {
            world.setBlockWithNotify(x, y, z, BlockList.generator.id);
        }

        world.updateNeighbors(x, y, z - 1, BlockList.generatorActive.id);
        world.updateNeighbors(x, y, z + 1, BlockList.generatorActive.id);
        world.updateNeighbors(x + 1, y, z, BlockList.generatorActive.id);
        world.updateNeighbors(x - 1, y, z, BlockList.generatorActive.id);
        world.updateNeighbors(x, y + 1, z, BlockList.generatorActive.id);
        world.updateNeighbors(x, y - 1, z, BlockList.generatorActive.id);
        keepInventory = false;
        world.setBlockMetadataWithNotify(x, y, z, meta);
        tileEntity.validate();
        world.setBlockTileEntity(x, y, z, tileEntity);
    }

    @Override
    public void onBlockRemoval(World world, int x, int y, int z) {
        world.updateNeighbors(x, y, z, this.id);
        if (!keepInventory) {
            this.clearInventory(world, x, y, z);
        }

        world.updateNeighbors(x, y, z - 1, BlockList.generatorActive.id);
        world.updateNeighbors(x, y, z + 1, BlockList.generatorActive.id);
        world.updateNeighbors(x + 1, y, z, BlockList.generatorActive.id);
        world.updateNeighbors(x - 1, y, z, BlockList.generatorActive.id);
        world.updateNeighbors(x, y + 1, z, BlockList.generatorActive.id);
        world.updateNeighbors(x, y - 1, z, BlockList.generatorActive.id);
        world.removeBlockTileEntity(x, y, z);
    }
}
