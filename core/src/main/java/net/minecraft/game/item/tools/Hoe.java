package net.minecraft.game.item.tools;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.stepsound.StepSound;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;
import net.minecraft.statistics.achievement.AchievementList;

public class Hoe extends Tool {
    public Hoe(int itemid, ToolMaterial toolTier) {
        super(itemid, 1, toolTier);
        this.toolType = ToolType.HOE;
        this.outputCount = 2;
        this.priorityItem = true;
    }

    @Override
    public boolean useOnBlock(ItemStack stack, Player player, World world, int xCoord, int yCoord, int zCoord, int side) {
        int id = world.getBlockId(xCoord, yCoord, zCoord);
        int meta = world.getBlockMetadata(xCoord, yCoord, zCoord);
        Material material = world.getBlockMaterial(xCoord, yCoord + 1, zCoord);
        if (!material.isSolid() && (id == BlockList.grass.id || id == BlockList.dirt.id) && meta == 0) {
            Block block = BlockList.tilledField;
            StepSound snd = block.getStepSound(0);
            world.playSoundEffect(player, xCoord + 0.5F, yCoord + 0.5F, zCoord + 0.5F, snd.getSound(), (snd.getVolume() + 1.0F) / 2.0F, snd.getPitch() * 0.8F);
            if (world.isOnline) {
                return true;
            }

            world.setBlockWithNotify(xCoord, yCoord, zCoord, block.id);
            stack.damageItem(1, player);
            boolean hasCoinEquipped = player.getLuckModifier();
            if ((world.rand.nextInt(8) == 0 || hasCoinEquipped) && id == BlockList.grass.id) {
                byte count = 1;

                for (int i = 0; i < count; i++) {
                    float offset = 0.7F;
                    float offsetX = world.rand.nextFloat() * offset + (1.0F - offset) * 0.5F;
                    float offsetZ = world.rand.nextFloat() * offset + (1.0F - offset) * 0.5F;
                    DroppedItem item = new DroppedItem(world, xCoord + offsetX, yCoord + 1.2F, zCoord + offsetZ, new ItemStack(ItemList.seeds));
                    world.spawnEntity(item);
                }
            }

            player.addStat(StatList.useStats[this.id], 1);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void triggerAchievement(Player player) {
        player.triggerAchievement(AchievementList.buildHoe);
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.itemIcon = ir.registerIcon("hoes/" + this.internalName);
    }
}
