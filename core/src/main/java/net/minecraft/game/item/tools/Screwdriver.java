package net.minecraft.game.item.tools;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.building.Panel;
import net.minecraft.game.block.building.Slab;
import net.minecraft.game.block.building.Stairs;
import net.minecraft.game.block.misc.MinecartTrack;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;

public class Screwdriver extends Item {
    public Screwdriver(int itemid) {
        super(itemid);
        this.maxDamage = 128;
        this.info.add(this.maxDamage + " item.desc.durability");
        this.outputId = 4363;
        this.outputCount = 1;
        this.priorityItem = true;
    }

    @Override
    public final boolean useOnBlock(ItemStack itemStack, Player player, World world, int x, int y, int z, int side) {
        if (y > 0 && y < world.getWorldHeight()) {
            int id = world.getBlockId(x, y, z);
            int metadata = world.getBlockMetadata(x, y, z);
            Block block = BlockList.blocks[id];
            if (block == null) {
                return false;
            }

            if (world.getBlock(x, y, z) instanceof Stairs) {
                metadata++;

                for (int i = 0; i < 256; i += 8) {
                    if (world.getBlockMetadata(x, y, z) < i && metadata >= i) {
                        metadata -= 8;
                        break;
                    }
                }

                this.rotateBlock(x, y, z, 0, metadata, itemStack, world, player);
                return true;
            } else if (world.getBlockId(x, y, z) == BlockList.chair.id || world.getBlock(x, y, z) instanceof Panel) {
                metadata++;

                for (int i = 0; i < 256; i += 4) {
                    if (world.getBlockMetadata(x, y, z) < i && metadata >= i) {
                        metadata -= 4;
                        break;
                    }
                }

                this.rotateBlock(x, y, z, 0, metadata, itemStack, world, player);
                return true;
            } else if (world.getBlock(x, y, z) instanceof Slab && !((Slab)world.getBlock(x, y, z)).doubleSlab) {
                if (metadata >= 128) {
                    metadata -= 128;
                } else {
                    metadata += 128;
                }

                this.rotateBlock(x, y, z, 0, metadata, itemStack, world, player);
                return true;
            } else {
                if (world.getBlock(x, y, z).getRenderType() == Block.RenderType.LOG && !world.getBlock(x, y, z).isRotatable(metadata)) {
                    metadata += 64;
                    this.rotateBlock(x, y, z, 0, metadata, itemStack, world, player);
                    return true;
                }

                if (world.getBlock(x, y, z) instanceof MinecartTrack) {
                    metadata++;
                    if (metadata > 1 && metadata < 6) {
                        metadata = 6;
                    }

                    if (metadata > 9) {
                        metadata = 0;
                    }

                    this.rotateBlock(x, y, z, 0, metadata, itemStack, world, player);
                    return true;
                } else if (world.getBlock(x, y, z).isRotatable(metadata)) {
                    if (++metadata > 5) {
                        metadata = 2;
                    }

                    this.rotateBlock(x, y, z, 0, metadata, itemStack, world, player);
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    private void rotateBlock(int x, int y, int z, int id, int metadata, ItemStack itemStack, World world, Player player) {
        if (id <= 0) {
            world.setBlockMetadataWithNotify(x, y, z, metadata);
        } else {
            world.setBlockAndMetadataWithNotify(x, y, z, id, metadata);
        }

        itemStack.damageItem(1, player);
        player.addStat(StatList.useStats[this.id], 1);
        world.playBlockSound(player, x, y, z, "random.screwdriver", 0.5F, 1.0F / (rand.nextFloat() * 0.4F + 1.2F));
    }

    @Override
    public boolean isRepairable(ItemStack stack1, ItemStack stack2) {
        return stack2 != null && (stack2.itemID == ItemList.ingotCopper.id || stack2.itemID == ItemList.ingotIron.id || stack2.itemID == this.id);
    }

    @Override
    public boolean isEnchantable(ItemStack stack1, ItemStack stack2) {
        return stack2 != null && stack2.getItem() == ItemList.scroll && stack2.itemDmg == 3;
    }

    @Override
    public int repairCost() {
        return 100;
    }
}
