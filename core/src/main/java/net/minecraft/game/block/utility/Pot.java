package net.minecraft.game.block.utility;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.block.tileentity.container.PotEntity;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSource;

public class Pot extends ContainerBlock {
    private Icon bottom;
    public static Icon top;

    public Pot(int blockid, Material material) {
        super(blockid, material);
        this.setBounds(0.0625, 0.0, 0.0625, 0.9375, 1.0, 0.9375);
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.POT;
    }

    @Override
    public void registerIcons(IconRegister r) {
        super.registerIcons(r);
        this.bottom = r.registerIcon("bigPot_bottom");
        top = r.registerIcon("bigPot_entry");
    }

    @Override
    public Icon getIcon(int side, int meta) {
        return side != 0 && side != 1 ? this.blockIcon : this.bottom;
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public boolean renderFace(WorldSource world, int x, int y, int z, int side) {
        return true;
    }

    @Override
    public final TileEntity getBlockEntity() {
        return new PotEntity();
    }

    @Override
    public void onBlockRemoval(World world, int xCoord, int yCoord, int zCoord) {
        this.clearInventory(world, xCoord, yCoord, zCoord);
        super.onBlockRemoval(world, xCoord, yCoord, zCoord);
    }

    @Override
    public final boolean blockActivated(World world, int x, int y, int z, Player player) {
        if (player.inventory.getCurrentItem() != null) {
            if (world.getTileEntity(x, y, z) != null && ((PotEntity)world.getTileEntity(x, y, z)).storeStack(player.inventory.getCurrentItem())) {
                player.inventory.setSlotContents(player.inventory.currentSlot, null);
            }

            return true;
        } else {
            if (world.getTileEntity(x, y, z) != null) {
                PotEntity barrel = (PotEntity)world.getTileEntity(x, y, z);

                for (int slot = 0; slot < barrel.getSize(); slot++) {
                    if (barrel.getStackInSlot(slot) != null) {
                        player.inventory.setSlotContents(player.inventory.currentSlot, barrel.getStackInSlot(slot));
                        barrel.setSlotContents(slot, null);
                        return true;
                    }
                }
            }

            return false;
        }
    }
}
