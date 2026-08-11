package net.minecraft.game.block.machines;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSource;

public class Observer extends Block {
    private Icon top;
    private Icon onIcon;

    public Observer(int blockID, Material material) {
        super(blockID, material);
        this.setTickable(true);
    }

    @Override
    public int tickRate() {
        return 3;
    }

    @Override
    public void onTick(World world, int x, int y, int z, Random rand) {
        world.setBlockMetadataWithNotify(x, y, z, 0);
    }

    @Override
    public void onNeighborChange(World world, int x, int y, int z, int id) {
        if (id != BlockList.generatorActive.id) {
            if (world.getBlockMetadata(x, y, z) == 4) {
                world.setBlockMetadata(x, y, z, 0);
            } else {
                world.setBlockMetadata(x, y, z, 4);
            }
        }
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("machines/observer");
        this.onIcon = ir.registerIcon("machines/observerOn");
        this.top = ir.registerIcon("machines/observerTop");
    }

    @Override
    public Icon getIcon(int side, int meta) {
        Icon returnIcon = this.blockIcon;
        if (meta == 4) {
            returnIcon = this.onIcon;
        }

        return side != 1 && side != 0 ? returnIcon : this.top;
    }

    @Override
    public Icon getTexture(WorldSource world, int xCoord, int yCoord, int zCoord, int side) {
        if (side != 1 && side != 0) {
            return world.getBlockMetadata(xCoord, yCoord, zCoord) == 0 ? this.blockIcon : this.onIcon;
        } else {
            return this.top;
        }
    }
}
