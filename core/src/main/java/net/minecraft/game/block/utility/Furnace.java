package net.minecraft.game.block.utility;

import java.util.Random;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.block.tileentity.smelters.FurnaceEntity;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSource;
import net.minecraft.util.math.MathHelper;

public class Furnace extends ContainerBlock {
    protected final boolean active;
    protected static boolean keepInventory = false;
    protected Icon top;
    protected Icon front;
    protected Icon bottom;

    public Furnace(int blockid, boolean isActive) {
        super(blockid, MaterialList.rock);
        this.active = isActive;
        this.silkTouch = false;
    }

    @Override
    public boolean isRotatable(int id) {
        return true;
    }

    @Override
    public void onPlaced(World world, Player player, int x, int y, int z, int side) {
        super.onPlaced(world, player, x, y, z, side);
        int facingDir = MathHelper.floor_double(player.yaw * 4.0F / 360.0F + 0.5) & 3;
        if (facingDir == 0) {
            world.setBlockMetadataWithNotify(x, y, z, 2);
        }

        if (facingDir == 1) {
            world.setBlockMetadataWithNotify(x, y, z, 5);
        }

        if (facingDir == 2) {
            world.setBlockMetadataWithNotify(x, y, z, 3);
        }

        if (facingDir == 3) {
            world.setBlockMetadataWithNotify(x, y, z, 4);
        }
    }

    @Override
    public Icon getTexture(WorldSource world, int xCoord, int yCoord, int zCoord, int side) {
        if (side == 1) {
            return this.top;
        }

        if (side == 0) {
            return this.bottom;
        }

        int meta = world.getBlockMetadata(xCoord, yCoord, zCoord);
        return side != meta ? this.blockIcon : this.front;
    }

    @Override
    public void spawnParticles(World world, int xCoord, int yCoord, int zCoord, Random rand) {
        if (this.active) {
            if (rand.nextInt(24) == 0) {
                world.playBlockSound(xCoord + 0.5F, yCoord + 0.5F, zCoord + 0.5F, "random.furnace", 0.3F, rand.nextFloat() * 0.7F + 0.3F);
            }

            int meta = world.getBlockMetadata(xCoord, yCoord, zCoord);
            float xOffset = xCoord + 0.5F;
            float yOffset = yCoord + 0.0F + rand.nextFloat() * 6.0F / 16.0F;
            float zOffset = zCoord + 0.5F;
            float horizontalOffset = 0.52F;
            float verticalOffset = rand.nextFloat() * 0.6F - 0.3F;
            if (meta == 4) {
                world.spawnParticle(ParticleType.SMOKE, xOffset - horizontalOffset, yOffset, zOffset + verticalOffset, 0.0, 0.0, 0.0);
                world.spawnParticle(ParticleType.FLAME, xOffset - horizontalOffset, yOffset, zOffset + verticalOffset, 0.0, 0.0, 0.0);
            } else if (meta == 5) {
                world.spawnParticle(ParticleType.SMOKE, xOffset + horizontalOffset, yOffset, zOffset + verticalOffset, 0.0, 0.0, 0.0);
                world.spawnParticle(ParticleType.FLAME, xOffset + horizontalOffset, yOffset, zOffset + verticalOffset, 0.0, 0.0, 0.0);
            } else if (meta == 2) {
                world.spawnParticle(ParticleType.SMOKE, xOffset + verticalOffset, yOffset, zOffset - horizontalOffset, 0.0, 0.0, 0.0);
                world.spawnParticle(ParticleType.FLAME, xOffset + verticalOffset, yOffset, zOffset - horizontalOffset, 0.0, 0.0, 0.0);
            } else if (meta == 3) {
                world.spawnParticle(ParticleType.SMOKE, xOffset + verticalOffset, yOffset, zOffset + horizontalOffset, 0.0, 0.0, 0.0);
                world.spawnParticle(ParticleType.FLAME, xOffset + verticalOffset, yOffset, zOffset + horizontalOffset, 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("workstations/furnace_side");
        this.front = ir.registerIcon(this.active ? "workstations/furnace_front_lit" : "workstations/furnace_front");
        this.top = ir.registerIcon("workstations/furnace_top");
        this.bottom = ir.registerIcon("workstations/furnace_bottom");
    }

    @Override
    public Icon getIcon(int side, int meta) {
        return side != 1 && side != 0 ? (side == 3 ? this.front : this.blockIcon) : this.top;
    }

    @Override
    public boolean blockActivated(World world, int xCoord, int yCoord, int zCoord, Player player) {
        if (!world.isOnline) {
            FurnaceEntity furnace = (FurnaceEntity)world.getTileEntity(xCoord, yCoord, zCoord);
            player.displayGUIFurnace(furnace);
        }

        return true;
    }

    public static void updateState(boolean isActive, World world, int xCoord, int yCoord, int zCoord) {
        int meta = world.getBlockMetadata(xCoord, yCoord, zCoord);
        TileEntity tileEntity = world.getTileEntity(xCoord, yCoord, zCoord);
        keepInventory = true;
        if (isActive) {
            world.setBlockWithNotify(xCoord, yCoord, zCoord, BlockList.stoneOvenActive.id);
        } else {
            world.setBlockWithNotify(xCoord, yCoord, zCoord, BlockList.stoneOvenIdle.id);
        }

        keepInventory = false;
        world.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, meta);
        tileEntity.validate();
        world.setBlockTileEntity(xCoord, yCoord, zCoord, tileEntity);
    }

    @Override
    public TileEntity getBlockEntity() {
        return new FurnaceEntity();
    }

    @Override
    public int dropID(int blockid, Random random) {
        return BlockList.stoneOvenIdle.id;
    }

    @Override
    public int pickID(int blockid) {
        return BlockList.stoneOvenIdle.id;
    }

    @Override
    public void onBlockRemoval(World world, int x, int y, int z) {
        if (!keepInventory) {
            this.clearInventory(world, x, y, z);
        }

        super.onBlockRemoval(world, x, y, z);
    }
}
