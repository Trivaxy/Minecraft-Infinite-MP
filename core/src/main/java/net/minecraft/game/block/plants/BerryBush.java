package net.minecraft.game.block.plants;

import java.util.List;
import java.util.Random;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;

public class BerryBush extends Flower {
    protected Icon altIcon;

    public BerryBush(int blockID) {
        super(blockID);
        this.setTickable(true);
        this.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        this.hasStates = true;
    }

    @Override
    public final void onTick(World world, int xCoord, int yCoord, int zCoord, Random random) {
        super.onTick(world, xCoord, yCoord, zCoord, random);
        if (world.getBlockBrightness(xCoord, yCoord + 1, zCoord) >= 9) {
            int meta = world.getBlockMetadata(xCoord, yCoord, zCoord);
            if (meta < 15) {
                world.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, meta + 1);
                return;
            }
        }
    }

    @Override
    public boolean blockActivated(World world, int x, int y, int z, Player player) {
        if (world.isOnline) {
            return true;
        }

        if (world.getBlockMetadata(x, y, z) != 15) {
            return false;
        }

        float offsetX = world.rand.nextFloat() * 0.7F + 0.15F;
        float offsetY = world.rand.nextFloat() * 0.7F + 0.15F;
        float offsetZ = world.rand.nextFloat() * 0.7F + 0.15F;
        DroppedItem drop = new DroppedItem(world, (double)x + offsetX, (double)y + offsetY, (double)z + offsetZ, new ItemStack(ItemList.berry));
        world.spawnEntity(drop);
        world.setBlockMetadataWithNotify(x, y, z, 0);
        player.addStat(StatList.berryGathered, 1);
        return true;
    }

    @Override
    public void dropBlockItem(World world, int x, int y, int z, int meta, float force, boolean fortune) {
        super.dropBlockItem(world, x, y, z, meta, force, fortune);
        if (!world.isOnline && meta == 15) {
            float offsetX = world.rand.nextFloat() * 0.7F + 0.15F;
            float offsetY = world.rand.nextFloat() * 0.7F + 0.15F;
            float offsetZ = world.rand.nextFloat() * 0.7F + 0.15F;
            DroppedItem drop = new DroppedItem(world, (double)x + offsetX, (double)y + offsetY, (double)z + offsetZ, new ItemStack(ItemList.berry));
            world.spawnEntity(drop);
        }
    }

    @Override
    public Icon getIcon(int side, int meta) {
        return meta == 15 ? this.altIcon : this.blockIcon;
    }

    @Override
    public int dropMeta(int meta) {
        return 0;
    }

    @Override
    public void getSubStacks(int id, CreativeTabs ct, List<ItemStack> ls) {
        ls.add(new ItemStack(id, 1, 15));
    }

    @Override
    public void registerIcons(IconRegister ir) {
        super.registerIcons(ir);
        this.altIcon = ir.registerIcon("plants/berryBush");
    }
}
