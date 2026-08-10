package net.minecraft.game.block.utility;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.block.tileentity.other.LecternEntity;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class Lectern extends ContainerBlock {
    private Icon top;

    public Lectern(int id) {
        super(id, MaterialList.wood);
        this.setBounds(0.0, 0.0, 0.0, 1.0, 0.75, 1.0);
        this.setLightOpacity(0);
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.LECTERN;
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public Icon getIcon(int side, int meta) {
        if (side == 0) {
            return BlockList.planks.getIcon(0, 0);
        } else {
            return side == 1 ? this.top : this.blockIcon;
        }
    }

    @Override
    public void registerIcons(IconRegister r) {
        this.blockIcon = r.registerIcon("workstations/enchanter");
        this.top = r.registerIcon("workstations/enchanter_top");
    }

    @Override
    public TileEntity getBlockEntity() {
        return new LecternEntity();
    }

    @Override
    public void onBlockRemoval(World world, int xCoord, int yCoord, int zCoord) {
        this.clearInventory(world, xCoord, yCoord, zCoord);
        super.onBlockRemoval(world, xCoord, yCoord, zCoord);
    }

    @Override
    public final boolean blockActivated(World world, int x, int y, int z, Player player) {
        if (player.inventory.getCurrentItem() == null
            || player.inventory.getCurrentItem().getItem() != ItemList.bookAndQuill && player.inventory.getCurrentItem().getItem() != ItemList.writtenBook) {
            LecternEntity lectern = (LecternEntity)world.getTileEntity(x, y, z);
            if (lectern != null && lectern.book != null) {
                if (player.inventory.getCurrentItem() == null) {
                    player.inventory.setSlotContents(player.inventory.currentSlot, lectern.book);
                    lectern.book = null;
                } else {
                    player.displayGUIBook(lectern.book);
                }

                return true;
            } else {
                return false;
            }
        } else {
            if (world.getTileEntity(x, y, z) != null && ((LecternEntity)world.getTileEntity(x, y, z)).book == null) {
                ((LecternEntity)world.getTileEntity(x, y, z)).book = player.inventory.getCurrentItem();
                player.inventory.setSlotContents(player.inventory.currentSlot, null);
            }

            return true;
        }
    }
}
