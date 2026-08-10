package net.minecraft.game.block.machines;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class Battery extends Block {
    public Icon top;
    public Icon[] side = new Icon[13];
    public Icon btm;

    public Battery(int blockID, Material material) {
        super(blockID, material);
        this.setTickable(true);
        this.hasStates = true;
    }

    @Override
    public Icon getIcon(int side, int mdata) {
        if (side == 0) {
            return this.btm;
        }

        if (side == 1) {
            return this.top;
        }

        int newMeta = mdata / 16;
        if (newMeta > 12) {
            newMeta = 12;
        }

        return this.side[newMeta];
    }

    @Override
    public void registerIcons(IconRegister r) {
        this.top = r.registerIcon("machines/" + this.name + "_top");
        this.btm = r.registerIcon("machines/" + this.name + "_bottom");

        for (int i = 0; i < 13; i++) {
            this.side[i] = r.registerIcon("machines/" + this.name + "_" + i);
        }
    }

    @Override
    public void onTick(World world, int xCoord, int yCoord, int zCoord, Random random5) {
        boolean providingPower = false;

        label53:
        for (int i = -2; i < 3; i++) {
            for (int j = -1; j < 2; j++) {
                for (int l = -2; l < 3; l++) {
                    if ((
                            world.isPowering(xCoord + i, yCoord + j, zCoord + l, BlockList.fan.id)
                                || world.isPowering(xCoord + i, yCoord + j, zCoord + l, BlockList.cog.id)
                                || world.isPowering(xCoord + i, yCoord + j, zCoord + l, BlockList.lamp.id)
                        )
                        && world.getBlockMetadata(xCoord, yCoord, zCoord) > 0) {
                        providingPower = true;
                        break label53;
                    }
                }
            }
        }

        if (providingPower) {
            world.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, world.getBlockMetadata(xCoord, yCoord, zCoord) - 1);
            this.notifyNeighbors(world, xCoord, yCoord, zCoord);
        } else if ((
                world.isPoweredBy(xCoord, yCoord, zCoord, BlockList.generatorActive.id)
                    || world.isPoweredBy(xCoord, yCoord, zCoord, BlockList.daylightSensor.id)
            )
            && world.getBlockMetadata(xCoord, yCoord, zCoord) < 255) {
            world.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, world.getBlockMetadata(xCoord, yCoord, zCoord) + 1);
        }
    }

    @Override
    public void onBlockRemoval(World world, int x, int y, int z) {
        world.updateNeighbors(x, y, z, this.id);
        this.notifyNeighbors(world, x, y, z);
    }

    private void notifyNeighbors(World world, int x, int y, int z) {
        world.updateNeighbors(x, y, z - 1, this.id);
        world.updateNeighbors(x, y, z + 1, this.id);
        world.updateNeighbors(x + 1, y, z, this.id);
        world.updateNeighbors(x - 1, y, z, this.id);
        world.updateNeighbors(x, y + 1, z, this.id);
        world.updateNeighbors(x, y - 1, z, this.id);
    }

    @Override
    public boolean powersDirectly(World world, int x, int y, int z, int id) {
        return world.getBlockMetadata(x, y, z) > 0;
    }

    @Override
    public boolean powersIndirectly(World world, int x, int y, int z, int id) {
        return world.getBlockMetadata(x, y, z) > 0;
    }

    @Override
    public int tickRate() {
        return 5;
    }

    @Override
    public boolean providesPower() {
        return true;
    }

    @Override
    public int pickMeta(int id) {
        return 0;
    }

    @Override
    public int dropMeta(int id) {
        return id;
    }
}
