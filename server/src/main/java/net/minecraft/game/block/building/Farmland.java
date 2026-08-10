package net.minecraft.game.block.building;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.block.plants.Crops;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;

public class Farmland extends Block {
    private Icon wet;
    private Icon dry;

    public Farmland(int blockid) {
        super(blockid, MaterialList.ground);
        this.setTickable(true);
        this.setBounds(0.0, 0.0, 0.0, 1.0, 0.9375, 1.0);
        this.setLightOpacity(255);
        this.silkTouch = false;
    }

    @Override
    public BoundingBox getCollisionBox(World world, int xCoord, int yCoord, int zCoord) {
        return BoundingBox.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.wet = ir.registerIcon("soils/farmland_wet");
        this.dry = ir.registerIcon("soils/farmland_dry");
    }

    @Override
    public Icon getIcon(int side, int meta) {
        return side == 1 ? (meta > 0 ? this.wet : this.dry) : BlockList.dirt.getSideIcon(side);
    }

    @Override
    public void onTick(World world, int xCoord, int yCoord, int zCoord, Random random) {
        if (random.nextInt(5) == 0) {
            if (!this.isWaterNearby(world, xCoord, yCoord, zCoord)
                && (
                    !world.isRaining()
                        || !world.isBlockExposed(xCoord, yCoord, zCoord)
                        || world.getBiomeAt(xCoord, zCoord).getTemperatureEnum() == Biome.Temperature.HOT
                )) {
                int meta = world.getBlockMetadata(xCoord, yCoord, zCoord);
                if (meta > 0) {
                    world.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, meta - 1);
                } else if (!(world.getBlock(xCoord, yCoord + 1, zCoord) instanceof Crops)) {
                    world.setBlockWithNotify(xCoord, yCoord, zCoord, BlockList.dirt.id);
                }
            } else {
                world.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 7);
            }
        }
    }

    @Override
    public void onEntityWalking(World world, int xCoord, int yCoord, int zCoord, Entity entity) {
        if (entity.jumping) {
            world.setBlockWithNotify(xCoord, yCoord, zCoord, BlockList.dirt.id);
        }
    }

    private boolean isWaterNearby(World world, int xCoord, int yCoord, int zCoord) {
        int range = 4;

        for (int x = xCoord - range; x <= xCoord + range; x++) {
            for (int z = zCoord - range; z <= zCoord + range; z++) {
                for (int y = yCoord; y <= yCoord + 1; y++) {
                    if (world.getBlockMaterial(x, y, z) == MaterialList.water) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void onNeighborChange(World world, int xCoord, int yCoord, int zCoord, int id) {
        Material material = world.getBlockMaterial(xCoord, yCoord + 1, zCoord);
        if (material.isSolid()) {
            world.setBlockWithNotify(xCoord, yCoord, zCoord, BlockList.dirt.id);
        }
    }

    @Override
    public int dropID(int blockid, Random random) {
        return BlockList.dirt.id;
    }

    @Override
    public int pickID(int blockid) {
        return BlockList.dirt.id;
    }

    @Override
    public int getRotation(int x, int y, int z, int side) {
        return side == 1 ? -1 : this.rotationRand(x, y, z, side);
    }
}
