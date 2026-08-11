package net.minecraft.game.item.tools;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.stepsound.StepSound;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;

public class Shovel extends Tool {
    public Shovel(int itemid, ToolMaterial toolTier) {
        super(itemid, 1, toolTier);
        this.toolType = ToolType.SHOVEL;
        this.attackDelay = 10;
        this.outputCount = 1;
    }

    @Override
    public boolean useOnBlock(ItemStack stack, Player player, World world, int xCoord, int yCoord, int zCoord, int side) {
        int id = world.getBlockId(xCoord, yCoord, zCoord);
        int meta = world.getBlockMetadata(xCoord, yCoord, zCoord);
        Material material = world.getBlockMaterial(xCoord, yCoord + 1, zCoord);
        if (id == BlockList.grass.id && meta == 0 && !material.isSolid()) {
            Block block = BlockList.pathBlock;
            StepSound snd = block.getStepSound(0);
            world.playBlockSound(xCoord + 0.5F, yCoord + 0.5F, zCoord + 0.5F, snd.getSound(), (snd.getVolume() + 1.0F) / 2.0F, snd.getPitch() * 0.8F);
            if (world.isOnline) {
                return true;
            }

            world.setBlockWithNotify(xCoord, yCoord, zCoord, block.id);
            stack.damageItem(1, player);
            player.addStat(StatList.useStats[this.id], 1);
            return true;
        } else {
            return super.useOnBlock(stack, player, world, xCoord, yCoord, zCoord, side);
        }
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.itemIcon = ir.registerIcon("shovels/" + this.internalName);
    }
}
