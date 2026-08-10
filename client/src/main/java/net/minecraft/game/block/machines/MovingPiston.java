package net.minecraft.game.block.machines;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.block.tileentity.other.PistonEntity;
import net.minecraft.game.block.utility.ContainerBlock;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.util.math.Direction;

public class MovingPiston extends ContainerBlock {
    public MovingPiston(int id) {
        super(id, MaterialList.pulley);
        this.setHardness(-1.0F);
    }

    @Override
    public TileEntity getBlockEntity() {
        return null;
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
    }

    @Override
    public void onBlockRemoval(World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity != null && tileEntity instanceof PistonEntity) {
            ((PistonEntity)tileEntity).remove();
        } else {
            super.onBlockRemoval(world, x, y, z);
        }
    }

    @Override
    public boolean placeableAt(World world, int x, int y, int z) {
        return false;
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.NONE;
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public boolean blockActivated(World world, int x, int y, int z, Player player) {
        if (!world.isOnline && world.getTileEntity(x, y, z) == null) {
            world.setBlockWithNotify(x, y, z, 0);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int dropID(int id, Random rand) {
        return 0;
    }

    @Override
    public void dropBlockItem(World world, int x, int y, int z, int meta, float force, boolean fortune) {
        if (!world.isOnline) {
            PistonEntity piston = this.getTileEntity(world, x, y, z);
            if (piston != null) {
                BlockList.blocks[piston.getStoredBlock()].dropBlock(world, x, y, z, piston.getBlockMetadata());
            }
        }
    }

    @Override
    public void onNeighborChange(World world, int x, int y, int z, int id) {
        if (!world.isOnline && world.getTileEntity(x, y, z) == null) {
            world.setBlockWithNotify(x, y, z, 0);
        }
    }

    public static TileEntity getTileEntity(int x, int y, int z, boolean extend, boolean finish) {
        return new PistonEntity(x, y, z, extend, finish);
    }

    @Override
    public BoundingBox getCollisionBox(World world, int x, int y, int z) {
        PistonEntity piston = this.getTileEntity(world, x, y, z);
        if (piston == null) {
            return null;
        }

        float progress = piston.getProgress(0.0F);
        if (piston.isExtending()) {
            progress = 1.0F - progress;
        }

        return this.getBoundingBox(world, x, y, z, piston.getStoredBlock(), progress, piston.getPistonOrientation());
    }

    @Override
    public void setStateBounds(World world, int x, int y, int z) {
        PistonEntity piston = this.getTileEntity(world, x, y, z);
        if (piston != null) {
            Block pushingBlock = BlockList.blocks[piston.getStoredBlock()];
            if (pushingBlock == null || pushingBlock == this) {
                return;
            }

            pushingBlock.setStateBounds(world, x, y, z);
            float prog = piston.getProgress(0.0F);
            if (piston.isExtending()) {
                prog = 1.0F - prog;
            }

            int facing = piston.getPistonOrientation();
            this.minX = pushingBlock.minX - Direction.offsetsXForSide[facing] * prog;
            this.minY = pushingBlock.minY - Direction.offsetsYForSide[facing] * prog;
            this.minZ = pushingBlock.minZ - Direction.offsetsZForSide[facing] * prog;
            this.maxX = pushingBlock.maxX - Direction.offsetsXForSide[facing] * prog;
            this.maxY = pushingBlock.maxY - Direction.offsetsYForSide[facing] * prog;
            this.maxZ = pushingBlock.maxZ - Direction.offsetsZForSide[facing] * prog;
        }
    }

    public BoundingBox getBoundingBox(World world, int x, int y, int z, int id, float offset, int side) {
        if (id != 0 && id != this.id) {
            BoundingBox box = BlockList.blocks[id].getCollisionBox(world, x, y, z);
            if (box == null) {
                return null;
            }

            if (Direction.offsetsXForSide[side] < 0) {
                box.minX = box.minX - Direction.offsetsXForSide[side] * offset;
            } else {
                box.maxX = box.maxX - Direction.offsetsXForSide[side] * offset;
            }

            if (Direction.offsetsYForSide[side] < 0) {
                box.minY = box.minY - Direction.offsetsYForSide[side] * offset;
            } else {
                box.maxY = box.maxY - Direction.offsetsYForSide[side] * offset;
            }

            if (Direction.offsetsZForSide[side] < 0) {
                box.minZ = box.minZ - Direction.offsetsZForSide[side] * offset;
            } else {
                box.maxZ = box.maxZ - Direction.offsetsZForSide[side] * offset;
            }

            return box;
        } else {
            return null;
        }
    }

    private PistonEntity getTileEntity(World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        return tileEntity != null && tileEntity instanceof PistonEntity ? (PistonEntity)tileEntity : null;
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("machines/pistonSide");
    }
}
