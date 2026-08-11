package net.minecraft.game.block.machines;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class SolarPanel extends Block {
    public Icon top;
    public Icon bottom;

    public SolarPanel(int blockID, Material material) {
        super(blockID, material);
        this.setBounds(0.0, 0.0, 0.0, 1.0, 0.375, 1.0);
        this.setTickable(true);
    }

    @Override
    public void onTick(World world, int xCoord, int yCoord, int zCoord, Random rand) {
        if ((world.skylightSubtracted < 4 || world.skylightSubtracted > 7) && world.isBlockExposed(xCoord, yCoord, zCoord)) {
            if (world.skylightSubtracted < 4 && world.skylightSubtracted >= 1) {
                world.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 1);
            }
        } else {
            world.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 0);
        }
    }

    @Override
    public void onNeighborChange(World world, int x, int y, int z, int id) {
        if (id != this.id) {
            if (!world.isSolid(x, y + 1, z) && world.isBlockExposed(x, y, z)) {
                world.setBlockMetadataWithNotify(x, y, z, 1);
            } else {
                world.setBlockMetadataWithNotify(x, y, z, 0);
            }
        }
    }

    @Override
    public void onBlockRemoval(World world, int x, int y, int z) {
        world.updateNeighbors(x, y, z, this.id);
        world.updateNeighbors(x, y, z - 1, this.id);
        world.updateNeighbors(x, y, z + 1, this.id);
        world.updateNeighbors(x + 1, y, z, this.id);
        world.updateNeighbors(x - 1, y, z, this.id);
        world.updateNeighbors(x, y + 1, z, this.id);
        world.updateNeighbors(x, y - 1, z, this.id);
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        if (world.isDaytime() && world.isBlockExposed(x, y, z)) {
            world.setBlockMetadataWithNotify(x, y, z, 1);
        }
    }

    @Override
    public boolean powersDirectly(World world, int x, int y, int z, int id) {
        return world.isDaytime() && world.isBlockExposed(x, y, z) && world.getBlockMetadata(x, y, z) == 1;
    }

    @Override
    public boolean powersIndirectly(World world, int x, int y, int z, int id) {
        return world.isDaytime() && world.isBlockExposed(x, y, z) && world.getBlockMetadata(x, y, z) == 1;
    }

    @Override
    public boolean providesPower() {
        return true;
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public boolean particles3D() {
        return false;
    }

    @Override
    public int tickRate() {
        return 5;
    }

    @Override
    public Icon getIcon(int side, int meta) {
        switch (side) {
            case 0:
                return this.bottom;
            case 1:
                return this.top;
            default:
                return this.blockIcon;
        }
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("machines/panelSide");
        this.top = ir.registerIcon("machines/daylightSensor");
        this.bottom = ir.registerIcon("machines/panelBottom");
    }
}
