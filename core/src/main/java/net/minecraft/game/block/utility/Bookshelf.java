package net.minecraft.game.block.utility;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.block.tileentity.container.BookcaseEntity;
import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class Bookshelf extends ContainerBlock {
    public static String[] TEXTURES = new String[]{
        "bookshelf_0", "bookshelf_1", "bookshelf_2", "bookshelf_3", "bookshelf_4", "bookshelf_5", "bookshelf_6", "bookshelf_7", "bookshelf_8", "bookshelf_9"
    };
    private Icon[] icons;

    public Bookshelf(int blockid) {
        super(blockid, MaterialList.wood);
    }

    @Override
    public Icon getIcon(int side, int meta) {
        return side != 1 && side != 0 ? this.icons[meta] : BlockList.planks.getSideIcon(side);
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.icons = new Icon[TEXTURES.length * 2];

        for (int index = 0; index < TEXTURES.length; index++) {
            this.icons[index] = ir.registerIcon("bookshelf/" + TEXTURES[index]);
        }
    }

    @Override
    public final void onBlockRemoval(World world, int xCoord, int yCoord, int zCoord) {
        this.clearInventory(world, xCoord, yCoord, zCoord);
        super.onBlockRemoval(world, xCoord, yCoord, zCoord);
    }

    @Override
    public final boolean blockActivated(World world, int x, int y, int z, Player player) {
        Object bookshelf = (BookcaseEntity)world.getTileEntity(x, y, z);
        if (!world.isOnline) {
            player.displayGUIBookshelf((Inventory)bookshelf);
        }

        return true;
    }

    @Override
    public final TileEntity getBlockEntity() {
        return new BookcaseEntity();
    }
}
