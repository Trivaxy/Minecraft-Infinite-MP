package net.minecraft.game.item.tools;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.building.Slab;
import net.minecraft.game.block.stepsound.StepSound;
import net.minecraft.game.block.utility.ContainerBlock;
import net.minecraft.game.entity.misc.FallingBlock;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.block.BlockItem;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;
import net.minecraft.statistics.achievement.AchievementList;

public class Pickaxe extends Tool {
    private int harvestLevel;

    public Pickaxe(int itemid, ToolMaterial toolTier) {
        super(itemid, 1, toolTier);
        this.harvestLevel = toolTier.getHarvestLevel();
        this.toolType = ToolType.PICKAXE;
        this.attackDelay = 10;
        this.outputCount = 3;
    }

    @Override
    public float getDigSpd(ItemStack stack, Block block) {
        return this.toolMaterial == ToolMaterial.DARKMATTER ? ToolMaterial.DARKMATTER.getEfficiency() : super.getDigSpd(stack, block);
    }

    @Override
    public boolean canHarvestBlock(Block block) {
        if (block == BlockList.obsidian
            || block == BlockList.blockDamascus
            || block == BlockList.oreDamascus
            || block == BlockList.oreAdamantine
            || block == BlockList.blockAdamantine) {
            return this.harvestLevel >= 4;
        } else if (block == BlockList.oreDiamond
            || block == BlockList.blockDiamond
            || block == BlockList.oreAmethyst
            || block == BlockList.blockAmethyst
            || block == BlockList.blockTopaz
            || block == BlockList.oreTopaz) {
            return this.harvestLevel >= 3;
        } else if (block == BlockList.oreGold
            || block == BlockList.blockGold
            || block == BlockList.oreEmerald
            || block == BlockList.blockEmerald
            || block == BlockList.oreRuby
            || block == BlockList.blockRuby
            || block == BlockList.oreSapphire
            || block == BlockList.blockSapphire) {
            return this.harvestLevel >= 2;
        } else {
            return block != BlockList.oreIron && block != BlockList.blockIron ? super.canHarvestBlock(block) : this.harvestLevel >= 1;
        }
    }

    @Override
    public final boolean useOnBlock(ItemStack stack, Player player, World world, int x, int y, int z, int side) {
        int id = world.getBlockId(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        Block block = BlockList.blocks[id];
        double offsetX = x + 0.5;
        double offsetY = y + 0.5;
        double offsetZ = z + 0.5;
        if (this.id == ItemList.gauntletUriel.id
            && BlockList.blocks[id] != null
            && BlockList.blocks[id].getHardness() > 0.0F
            && BlockList.blocks[id].fullBlock()
            && !(BlockList.blocks[id] instanceof ContainerBlock)) {
            world.spawnEntity(new FallingBlock(world, x + 0.5, y + 0.5, z + 0.5, id, meta, player));
            StepSound snd = block.getStepSound(meta);
            world.playSoundEffect(player, offsetX, offsetY, offsetZ, snd.getSound(), (snd.volume + 1.0F) / 2.0F, snd.pitch * 0.8F);
            world.setBlockWithNotify(x, y, z, 0);
            player.addStat(StatList.useStats[this.id], 1);
            return true;
        } else if (block instanceof Slab && ((Slab)block).doubleSlab) {
            StepSound snd = block.getStepSound(meta);
            world.playSoundEffect(player, offsetX, offsetY, offsetZ, snd.getSound(), (snd.volume + 1.0F) / 2.0F, snd.pitch * 0.8F);
            world.setBlockAndMetadataWithNotify(x, y, z, block.dropID(id, world.rand), meta);
            BlockItem.getSlab(id).dropBlock(world, x, y, z, meta);
            stack.damageItem(1, player);
            player.addStat(StatList.useStats[this.id], 1);
            return true;
        } else {
            return super.useOnBlock(stack, player, world, x, y, z, side);
        }
    }

    @Override
    public void triggerAchievement(Player player) {
        if (this == ItemList.pickaxeStone) {
            player.triggerAchievement(AchievementList.buildPickaxe);
        } else {
            player.triggerAchievement(AchievementList.buildBetterPickaxe);
        }
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.itemIcon = ir.registerIcon("pickaxes/" + this.internalName);
    }
}
