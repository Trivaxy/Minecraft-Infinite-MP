package net.minecraft.game.block.machines;

import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.block.tileentity.container.VacuumEntity;
import net.minecraft.game.block.utility.ContainerBlock;
import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class Vacuum extends ContainerBlock {
    private Icon top;
    private Icon bottom;

    public Vacuum(int blockID) {
        super(blockID, MaterialList.vacuum);
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("machines/chute");
        this.top = ir.registerIcon("machines/chuteTop");
        this.bottom = ir.registerIcon("machines/blockGold_bottom");
    }

    @Override
    public Icon getIcon(int side, int meta) {
        if (side == 0) {
            return this.bottom;
        } else {
            return side == 1 ? this.top : this.blockIcon;
        }
    }

    @Override
    public final TileEntity getBlockEntity() {
        return new VacuumEntity();
    }

    @Override
    public final boolean blockActivated(World world, int x, int y, int z, Player player) {
        if (!world.isOnline) {
            Inventory barrel = (VacuumEntity)world.getTileEntity(x, y, z);
            player.displayGUIVacuum(barrel);
        }

        return true;
    }

    @Override
    public final void onBlockRemoval(World world, int xCoord, int yCoord, int zCoord) {
        this.clearInventory(world, xCoord, yCoord, zCoord);
        super.onBlockRemoval(world, xCoord, yCoord, zCoord);
    }
}
