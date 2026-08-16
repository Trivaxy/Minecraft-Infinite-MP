package net.minecraft.game.block.utility;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.block.tileentity.container.ChestEntity;
import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSource;
import net.minecraft.util.math.MathHelper;

public class LockedChest extends ContainerBlock {
    protected Icon side;
    protected Icon top;
    protected Icon front;

    public LockedChest(int blockid) {
        super(blockid, MaterialList.rock);
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.CHEST;
    }

    @Override
    public boolean isRotatable(int id) {
        return id != 1;
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public float breakStrength(Player player, int metadata) {
        return metadata == 1 ? 0.0F : super.breakStrength(player, metadata);
    }

    @Override
    public void onPlaced(World world, Player player, int x, int y, int z, int side) {
        super.onPlaced(world, player, x, y, z, side);
        byte meta = 0;
        int facing = MathHelper.floor_double(player.yaw * 4.0F / 360.0F + 0.5) & 3;
        if (facing == 0) {
            meta = 2;
        }

        if (facing == 1) {
            meta = 5;
        }

        if (facing == 2) {
            meta = 3;
        }

        if (facing == 3) {
            meta = 4;
        }

        world.setBlockMetadataWithNotify(x, y, z, meta);
    }

    @Override
    public Icon getTexture(WorldSource world, int xCoord, int yCoord, int zCoord, int side) {
        if (side != 1 && side != 0) {
            return side == world.getBlockMetadata(xCoord, yCoord, zCoord) ? this.front : this.side;
        } else {
            return this.top;
        }
    }

    @Override
    public Icon getSideIcon(int side) {
        return side == 1 ? this.top : (side == 0 ? this.top : (side == 3 ? this.front : this.side));
    }

    @Override
    public Icon getIcon(int side, int meta) {
        return side == 1 ? this.top : (side == 0 ? this.top : (side == 3 ? this.front : this.side));
    }

    @Override
    public void onBlockRemoval(World world, int xCoord, int yCoord, int zCoord) {
        this.clearInventory(world, xCoord, yCoord, zCoord);
        super.onBlockRemoval(world, xCoord, yCoord, zCoord);
    }

    @Override
    public boolean blockActivated(World world, int xCoord, int yCoord, int zCoord, Player player) {
        Object tileentity = world.getTileEntity(xCoord, yCoord, zCoord);
        if (world.getBlockMetadata(xCoord, yCoord, zCoord) != 1 && !world.isBlockFull(xCoord, yCoord + 1, zCoord)) {
            if (!world.isOnline) {
                player.displayGUIChest((Inventory)tileentity, true);
                return true;
            } else {
                return true;
            }
        } else {
            if (player.getCurrentEquippedItem() == null || player.getCurrentEquippedItem().getItem() != ItemList.key) {
                world.playBlockSound(player, xCoord, yCoord, zCoord, "random.locked", 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
            }

            return false;
        }
    }

    @Override
    public void registerIcons(IconRegister r) {
        this.side = r.registerIcon("workstations/lockedChestSide");
        this.top = r.registerIcon("workstations/furnace_top");
        this.front = r.registerIcon("workstations/lockedChestFront");
        this.blockIcon = r.registerIcon("workstations/lockedChestSide");
    }

    @Override
    public TileEntity getBlockEntity() {
        return new ChestEntity();
    }
}
