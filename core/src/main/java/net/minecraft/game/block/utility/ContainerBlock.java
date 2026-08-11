package net.minecraft.game.block.utility;

import com.mojang.nbt.tag.special.CompoundTag;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;

public abstract class ContainerBlock extends Block {
    protected ContainerBlock(int id, Material mat) {
        super(id, mat);
        isContainer.set(id);
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        world.setBlockTileEntity(x, y, z, this.getBlockEntity());
    }

    @Override
    public void onBlockRemoval(World world, int x, int y, int z) {
        world.removeBlockTileEntity(x, y, z);
    }

    public abstract TileEntity getBlockEntity();

    protected void clearInventory(World world, int xCoord, int yCoord, int zCoord) {
        Inventory inv = (Inventory)world.getTileEntity(xCoord, yCoord, zCoord);
        if (inv != null) {
            for (int slot = 0; slot < inv.getSize(); slot++) {
                ItemStack stack = inv.getStackInSlot(slot);
                if (stack != null) {
                    float offsetX = world.rand.nextFloat() * 0.8F + 0.1F;
                    float offsetY = world.rand.nextFloat() * 0.8F + 0.1F;
                    float offsetZ = world.rand.nextFloat() * 0.8F + 0.1F;
                    CompoundTag nbt = null;
                    if (stack.hasTag()) {
                        nbt = stack.getTag();
                    }

                    ItemStack theItem = new ItemStack(stack.itemID, stack.stackSize, stack.itemDmg, stack.customName);
                    if (nbt != null) {
                        theItem = new ItemStack(stack.itemID, stack.stackSize, stack.itemDmg, stack.customName, nbt);
                    }

                    DroppedItem drop = new DroppedItem(world, (double)xCoord + offsetX, (double)yCoord + offsetY, (double)zCoord + offsetZ, theItem);
                    drop.motionX = (float)world.rand.nextGaussian() * 0.05F;
                    drop.motionY = (float)world.rand.nextGaussian() * 0.05F + 0.2F;
                    drop.motionZ = (float)world.rand.nextGaussian() * 0.05F;
                    world.spawnEntity(drop);
                }
            }
        }
    }
}
