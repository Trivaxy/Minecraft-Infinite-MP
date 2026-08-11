package net.minecraft.game.block.misc;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class StoneSpike extends Block {
    private Icon top;
    private Icon bottom;
    private Icon topAlt;
    private Icon bottomAlt;

    public StoneSpike(int blockID, Material material) {
        super(blockID, material);
    }

    @Override
    public boolean placeableAt(World world, int x, int y, int z) {
        return this.canStay(world, x, y, z);
    }

    @Override
    public boolean canStay(World world, int x, int y, int z) {
        if (world.isSolid(x, y - 1, z) || world.isSolid(x, y + 1, z)) {
            return true;
        } else {
            return world.getBlockId(x, y - 1, z) == this.id && world.getBlockMetadata(x, y, z) < 3
                ? true
                : world.getBlockId(x, y + 1, z) == this.id && world.getBlockMetadata(x, y + 1, z) >= 3;
        }
    }

    @Override
    public void onNeighborChange(World world, int x, int y, int z, int id) {
        if (!this.canStay(world, x, y, z)) {
            this.dropBlock(world, x, y, z, world.getBlockMetadata(x, y, z));
            world.playEffect(2001, x, y, z, world.getBlockMetadata(x, y, z) << 12 | this.id);
            world.setBlockWithNotify(x, y, z, 0);
        } else if (world.getBlockId(x, y - 1, z) == this.id && world.getBlockMetadata(x, y + 1, z) == 2) {
            world.setBlockMetadata(x, y, z, 1);
        } else if (world.getBlockId(x, y + 1, z) == this.id && world.getBlockMetadata(x, y - 1, z) == 4) {
            world.setBlockMetadata(x, y, z, 5);
        } else if ((world.isSolid(x, y + 1, z) || world.getBlockId(x, y + 1, z) == this.id || world.getBlockId(x, y - 1, z) != this.id)
            && (!world.isSolid(x, y - 1, z) || world.getBlockId(x, y + 1, z) == this.id)) {
            if ((world.isSolid(x, y - 1, z) || world.getBlockId(x, y - 1, z) == this.id || world.getBlockId(x, y + 1, z) != this.id)
                && (!world.isSolid(x, y + 1, z) || world.getBlockId(x, y - 1, z) == this.id)) {
                if (world.isSolid(x, y + 1, z) && world.getBlockId(x, y - 1, z) == this.id) {
                    world.setBlockMetadata(x, y, z, 3);
                } else if (world.isSolid(x, y - 1, z) && world.getBlockId(x, y + 1, z) == this.id) {
                    world.setBlockMetadata(x, y, z, 0);
                }
            } else {
                world.setBlockMetadata(x, y, z, 4);
            }
        } else {
            world.setBlockMetadata(x, y, z, 2);
        }
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        if (world.getBlockId(x, y + 1, z) == this.id || world.getBlockId(x, y - 1, z) != this.id && !world.isSolid(x, y - 1, z)) {
            if (world.getBlockId(x, y - 1, z) == this.id || world.getBlockId(x, y + 1, z) != this.id && !world.isSolid(x, y + 1, z)) {
                if (world.getBlockId(x, y + 1, z) == this.id && world.getBlockId(x, y - 1, z) == this.id) {
                    world.setBlockMetadata(x, y, z, 1);
                    if (world.getBlockMetadata(x, y - 1, z) != 1 && !world.isSolid(x, y - 2, z)) {
                        world.setBlockMetadata(x, y - 1, z, 1);
                    }

                    if (world.getBlockMetadata(x, y + 1, z) != 3 && !world.isSolid(x, y + 2, z)) {
                        world.setBlockMetadata(x, y + 1, z, 1);
                    }
                }
            } else {
                world.setBlockMetadata(x, y, z, 4);
            }
        } else {
            world.setBlockMetadata(x, y, z, 2);
        }
    }

    @Override
    public Icon getIcon(int side, int meta) {
        if (meta == 1 || meta == 5) {
            return this.blockIcon;
        } else if (meta != 2 && meta != 4) {
            return this.id == BlockList.icicle.id ? this.bottomAlt : this.bottom;
        } else {
            return this.id == BlockList.icicle.id ? this.topAlt : this.top;
        }
    }

    @Override
    public Icon getSideIcon(int blockSide) {
        return this.top;
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("spikes/" + this.name);
        this.bottom = ir.registerIcon("spikes/spikeBase");
        this.top = ir.registerIcon("spikes/spikeTop");
        this.bottomAlt = ir.registerIcon("spikes/spikeBaseIce");
        this.topAlt = ir.registerIcon("spikes/spikeTopIce");
    }

    @Override
    public BoundingBox getOutline(World world, int x, int y, int z) {
        this.setStateBounds(world, x, y, z);
        return super.getOutline(world, x, y, z);
    }

    @Override
    public BoundingBox getCollisionBox(World world, int x, int y, int z) {
        this.setStateBounds(world, x, y, z);
        return super.getCollisionBox(world, x, y, z);
    }

    @Override
    public void setStateBounds(World world, int x, int y, int z) {
        this.setSpikeSize(world.getBlockMetadata(x, y, z));
    }

    public void setSpikeSize(int meta) {
        this.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        if (meta == 1 || meta == 5) {
            this.setBounds(0.25, 0.0, 0.25, 0.75, 1.0, 0.75);
        }

        if (meta == 2) {
            this.setBounds(0.25, 0.0, 0.25, 0.75, 0.75, 0.75);
        }

        if (meta == 4) {
            this.setBounds(0.25, 0.25, 0.25, 0.75, 1.0, 0.75);
        }
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.CROSS;
    }

    @Override
    public boolean particles3D() {
        return false;
    }
}
