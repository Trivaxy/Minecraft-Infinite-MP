package net.minecraft.game.item.tools;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.decoration.Chair;
import net.minecraft.game.block.stepsound.StepSound;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;
import net.minecraft.util.math.MathHelper;

public class Axe extends Tool {
    public Axe(int itemid, ToolMaterial toolTier) {
        super(itemid, 2, toolTier);
        this.toolType = ToolType.AXE;
        this.attackDelay = 15;
        this.outputCount = 3;
    }

    @Override
    public boolean useOnBlock(ItemStack itemStack, Player player, World world, int x, int y, int z, int id) {
        int blockID = world.getBlockId(x, y, z);
        Block cutBlock = BlockList.blocks[blockID];
        int meta = world.getBlockMetadata(x, y, z);
        Block block = BlockList.log;
        StepSound snd = block.getStepSound(meta);
        if (cutBlock == BlockList.wood) {
            world.playBlockSound(player, x + 0.5F, y + 0.5F, z + 0.5F, snd.getSound(), snd.volume, snd.pitch * 0.8F);
            world.setBlockAndMetadataWithNotify(x, y, z, block.id, meta);
            if (meta == 16) {
                float offset = 0.7F;
                double offsetX = world.rand.nextFloat() * offset + (1.0F - offset) * 0.5;
                double offsetY = world.rand.nextFloat() * offset + (1.0F - offset) * 0.5;
                double offsetZ = world.rand.nextFloat() * offset + (1.0F - offset) * 0.5;
                DroppedItem item = new DroppedItem(world, x + offsetX, y + offsetY, z + offsetZ, new ItemStack(ItemList.bark.id, 1, 0));
                item.pickupDelay = 10;
                world.spawnEntity(item);
            }

            itemStack.damageItem(1, player);
            player.addStat(StatList.useStats[this.id], 1);
            return true;
        } else {
            if (cutBlock instanceof Chair) {
                world.playBlockSound(player, x + 0.5F, y + 0.5F, z + 0.5F, snd.getSound(), snd.volume, snd.pitch * 0.8F);
                world.setBlockMetadataWithNotify(x, y, z, meta & 248 | 4);
                itemStack.damageItem(1, player);
                player.addStat(StatList.useStats[this.id], 1);
                return true;
            }

            if (cutBlock == BlockList.fence && meta < 128) {
                world.playBlockSound(player, x + 0.5F, y + 0.5F, z + 0.5F, snd.getSound(), snd.volume, snd.pitch * 0.8F);
                meta += 128;
                world.setBlockAndMetadataWithNotify(x, y, z, BlockList.fence.id, meta);
                itemStack.damageItem(1, player);
                player.addStat(StatList.useStats[this.id], 1);
                return true;
            }

            if (cutBlock == BlockList.pumpkin && meta == 0) {
                world.playBlockSound(player, x + 0.5F, y + 0.5F, z + 0.5F, snd.getSound(), snd.volume, snd.pitch * 0.8F);
                int side = MathHelper.floor_double(player.yaw * 4.0F / 360.0F + 0.5) & 3;
                if (side == 0) {
                    world.setBlockMetadataWithNotify(x, y, z, 2);
                }

                if (side == 1) {
                    world.setBlockMetadataWithNotify(x, y, z, 5);
                }

                if (side == 2) {
                    world.setBlockMetadataWithNotify(x, y, z, 3);
                }

                if (side == 3) {
                    world.setBlockMetadataWithNotify(x, y, z, 4);
                }

                float offset = 0.7F;
                double offsetX = world.rand.nextFloat() * offset + (1.0F - offset) * 0.5;
                double offsetY = world.rand.nextFloat() * offset + (1.0F - offset) * 0.5;
                double offsetZ = world.rand.nextFloat() * offset + (1.0F - offset) * 0.5;
                DroppedItem item = new DroppedItem(world, x + offsetX, y + offsetY, z + offsetZ, new ItemStack(ItemList.seedsPumpkin.id, 2, 0));
                item.pickupDelay = 10;
                world.spawnEntity(item);
                player.addStat(StatList.useStats[this.id], 1);
                itemStack.damageItem(1, player);
                return true;
            } else if (cutBlock == BlockList.doubleSlabWood) {
                world.playBlockSound(player, x + 0.5F, y + 0.5F, z + 0.5F, snd.getSound(), snd.volume, snd.pitch * 0.8F);
                world.setBlockAndMetadataWithNotify(x, y, z, BlockList.slabWood.id, meta);
                BlockList.slabWood.dropBlock(world, x, y, z, meta);
                itemStack.damageItem(1, player);
                player.addStat(StatList.useStats[this.id], 1);
                return true;
            } else {
                return super.useOnBlock(itemStack, player, world, x, y, z, id);
            }
        }
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.itemIcon = ir.registerIcon("axes/" + this.internalName);
    }

    @Override
    public int getDamageVsEntity(Entity entity) {
        return this == ItemList.axeSpecial ? 999999999 : this.atkDmg;
    }
}
