package net.minecraft.game.block.utility;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.block.tileentity.other.BeaconEntity;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSource;

public class Beacon extends ContainerBlock {
    private Icon altIcon;

    public Beacon(int id, Material mat) {
        super(id, mat);
    }

    @Override
    public Icon getIcon(int side, int meta) {
        return side == 1 ? this.altIcon : this.blockIcon;
    }

    @Override
    public void onBlockRemoval(World world, int x, int y, int z) {
        BeaconEntity beacon = (BeaconEntity)world.getTileEntity(x, y, z);
        float offsetX = world.rand.nextFloat() * 0.8F + 0.1F;
        float offsetY = world.rand.nextFloat() * 0.8F + 0.1F;
        float offsetZ = world.rand.nextFloat() * 0.8F + 0.1F;
        if (beacon != null) {
            if (beacon.range > 0) {
                ItemStack newStack = new ItemStack(ItemList.pearl.id, beacon.range);
                DroppedItem drop;
                (drop = new DroppedItem(world, x + offsetX, y + offsetY, z + offsetZ, newStack)).motionX = (float)world.rand.nextGaussian() * 0.05F;
                drop.motionY = (float)world.rand.nextGaussian() * 0.05F + 0.2F;
                drop.motionZ = (float)world.rand.nextGaussian() * 0.05F;
                world.spawnEntity(drop);
            }

            if (beacon.effectStack != null) {
                DroppedItem drop;
                (drop = new DroppedItem(world, x + offsetX, y + offsetY, z + offsetZ, beacon.effectStack)).motionX = (float)world.rand.nextGaussian() * 0.05F;
                drop.motionY = (float)world.rand.nextGaussian() * 0.05F + 0.2F;
                drop.motionZ = (float)world.rand.nextGaussian() * 0.05F;
                world.spawnEntity(drop);
            }
        }

        super.onBlockRemoval(world, x, y, z);
    }

    @Override
    public void registerIcons(IconRegister r) {
        super.registerIcons(r);
        this.altIcon = r.registerIcon("beaconCage");
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
    public Block.RenderType getRenderType() {
        return Block.RenderType.BEACON;
    }

    @Override
    public final boolean blockActivated(World world, int x, int y, int z, Player player) {
        BeaconEntity beacon = (BeaconEntity)world.getTileEntity(x, y, z);
        if (!world.isOnline) {
            player.displayGUIBeacon(beacon);
        }

        return true;
    }

    @Override
    public TileEntity getBlockEntity() {
        return new BeaconEntity();
    }

    @Override
    public void spawnParticles(World world, int xCoord, int yCoord, int zCoord, Random rand) {
        BeaconEntity beacon = (BeaconEntity)world.getTileEntity(xCoord, yCoord, zCoord);
        if (rand.nextInt(24) == 0 && beacon != null && beacon.effectStack != null) {
            world.playBlockSound(xCoord + 0.5F, yCoord + 0.5F, zCoord + 0.5F, "random.beacon", 1.0F, rand.nextFloat() * 0.7F + 0.3F);
        }
    }
}
