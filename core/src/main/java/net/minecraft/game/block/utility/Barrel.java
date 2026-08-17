package net.minecraft.game.block.utility;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.block.tileentity.container.BarrelEntity;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public final class Barrel extends ContainerBlock {
    private Icon top;
    private Icon bottom;

    public Barrel(int blockID) {
        super(blockID, MaterialList.wood);
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.BARREL;
    }

    @Override
    public void onPlaced(World world, Player player, int x, int y, int z, int side) {
        super.onPlaced(world, player, x, y, z, side);
        ItemStack stack = player.inventory.getCurrentItem();
        if (stack == null) {
            stack = player.inventory.getOffHandItem();
        }

        int meta = stack.itemDmg & 3;
        switch (side) {
            case 0:
            case 1:
            default:
                break;
            case 2:
                meta += 6;
                break;
            case 3:
                meta += 8;
                break;
            case 4:
                meta += 2;
                break;
            case 5:
                meta += 4;
        }

        world.setBlockMetadataWithNotify(x, y, z, meta);
    }

    @Override
    public void registerIcons(IconRegister r) {
        this.top = r.registerIcon("workstations/barrelTop");
        this.blockIcon = r.registerIcon("workstations/barrelSide");
        this.bottom = r.registerIcon("workstations/barrelBottom");
    }

    @Override
    public Icon getIcon(int side, int meta) {
        if (meta == 0) {
            if (side == 1) {
                return this.top;
            }

            if (side == 0) {
                return this.bottom;
            }
        }

        if (meta == 2) {
            if (side == 4) {
                return this.top;
            }

            if (side == 5) {
                return this.bottom;
            }
        }

        if (meta == 6) {
            if (side == 2) {
                return this.top;
            }

            if (side == 3) {
                return this.bottom;
            }
        }

        if (meta == 4) {
            if (side == 5) {
                return this.top;
            }

            if (side == 4) {
                return this.bottom;
            }
        }

        if (meta == 8) {
            if (side == 3) {
                return this.top;
            }

            if (side == 2) {
                return this.bottom;
            }
        }

        return this.blockIcon;
    }

    @Override
    public void onBlockRemoval(World world, int xCoord, int yCoord, int zCoord) {
        this.clearInventory(world, xCoord, yCoord, zCoord);
        super.onBlockRemoval(world, xCoord, yCoord, zCoord);
    }

    @Override
    public final boolean blockActivated(World world, int x, int y, int z, Player player) {
        BarrelEntity barrel = (BarrelEntity)world.getTileEntity(x, y, z);
        world.playBlockSound(player, x, y, z, "random.barrelOpen", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
        if (!world.isOnline) {
            player.displayGUIBarrel(barrel);
        }

        return true;
    }

    @Override
    public final TileEntity getBlockEntity() {
        return new BarrelEntity();
    }
}
