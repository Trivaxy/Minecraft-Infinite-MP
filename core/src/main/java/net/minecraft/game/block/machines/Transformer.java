package net.minecraft.game.block.machines;

import net.minecraft.game.ParticleType;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class Transformer extends Block {
    private Icon top;
    private Icon bottom;
    private Icon[] icons = new Icon[4];

    public Transformer(int blockID, Material material) {
        super(blockID, material);
    }

    @Override
    public boolean blockActivated(World world, int x, int y, int z, Player player) {
        if (world.hasNeighborSignal(x, y, z)) {
            return false;
        }

        world.setBlockMetadataWithNotify(x, y, z, world.getBlockMetadata(x, y, z) + 1);
        if (world.getBlockMetadata(x, y, z) > 3) {
            world.setBlockMetadataWithNotify(x, y, z, 0);
        }

        return true;
    }

    @Override
    public void onPlaced(World world, Player player, int x, int y, int z, int side) {
        super.onPlaced(world, player, x, y, z, side);
        if (!world.isOnline) {
            this.updateState(world, x, y, z);
        }
    }

    @Override
    public void onNeighborChange(World world, int x, int y, int z, int id) {
        if (!world.isOnline) {
            this.updateState(world, x, y, z);
        }
    }

    private void updateState(World world, int x, int y, int z) {
        int metadata = world.getBlockMetadata(x, y, z);
        if (world.hasNeighborSignal(x, y, z) && metadata < 4) {
            world.setBlockMetadata(x, y, z, metadata + 4);
        } else if (!world.hasNeighborSignal(x, y, z) && metadata >= 4) {
            world.setBlockMetadata(x, y, z, metadata - 4);

            for (int i = 0; i < 20; i++) {
                double xOffset = x + world.rand.nextDouble();
                double yOffset = y + this.maxY;
                double zOffset = z + world.rand.nextDouble();
                world.spawnParticle(ParticleType.EXPLODE, xOffset, yOffset, zOffset, 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.top = ir.registerIcon("machines/" + this.name + "Top");
        this.bottom = ir.registerIcon("machines/" + this.name + "Bottom");

        for (int i = 1; i < 5; i++) {
            this.icons[i - 1] = ir.registerIcon("machines/" + this.name + "Phase" + i);
        }
    }

    @Override
    public Icon getIcon(int side, int meta) {
        if (side == 1) {
            return this.top;
        } else if (side == 0) {
            return this.bottom;
        } else {
            return meta < 4 ? this.icons[meta] : this.icons[meta - 4];
        }
    }
}
