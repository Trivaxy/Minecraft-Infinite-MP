package net.minecraft.game.item.tools;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;

public abstract class Tool extends Item {
    public ToolMaterial toolMaterial;
    public ToolType toolType = ToolType.NONE;
    protected float efficiency = 4.0F;
    protected int atkDmg;

    public Tool(int itemid, int dmg, ToolMaterial material) {
        super(itemid);
        this.toolMaterial = material;
        this.maxStackSize = 1;
        this.maxDamage = material.getDurability();
        this.efficiency = material.getEfficiency();
        this.atkDmg = dmg + material.getEntityDamage();
        this.outputId = material.getRepairID();
        this.isWeapon = true;
        this.info.add(this.maxDamage + " item.desc.durability");
        this.info.add((int)this.efficiency + " item.desc.efficiency");
        this.info.add(this.atkDmg + " item.desc.attack");
    }

    @Override
    public float getDigSpd(ItemStack stack, Block block) {
        if (this == ItemList.paxel) {
            return this.efficiency;
        } else if (block.getTool() == this.toolType) {
            return this.toolMaterial == ToolMaterial.DAMASCUS ? this.efficiency / 2.0F + stack.getItemDamage() / 128 : this.efficiency;
        } else {
            return 1.0F;
        }
    }

    @Override
    public boolean useOnBlock(ItemStack stack, Player player, World world, int xCoord, int yCoord, int zCoord, int side) {
        return this.toolMaterial == ToolMaterial.RUBY ? ItemList.striker.useOnBlock(stack, player, world, xCoord, yCoord, zCoord, side) : false;
    }

    @Override
    public boolean canHarvestBlock(Block block) {
        return block.getTool() != ToolType.NONE && block.getTool() == this.toolType || this == ItemList.paxel;
    }

    @Override
    public void hitEntity(ItemStack stack, Mob target, Player player, World world) {
        stack.damageItem(2, player);
    }

    @Override
    public void onBlockDestroyed(ItemStack stack, int side, int x, int y, int z, Mob world) {
        stack.damageItem(1, world);
        Block block = world.world.getBlock(x, y, z);
        if (this != ItemList.paxel && this != ItemList.gauntletUriel && block.getTool() != this.toolType) {
            stack.damageItem(1, world);
        }

        if (this.id == ItemList.gauntletUriel.id && stack.isNotBroken()) {
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    if (side == 0 || side == 1) {
                        this.breakSurroundingBlocks(world.world, (Player)world, x + i, y, z + j);
                    } else if (side != 3 && side != 2) {
                        this.breakSurroundingBlocks(world.world, (Player)world, x, y + j, z + i);
                    } else {
                        this.breakSurroundingBlocks(world.world, (Player)world, x + i, y + j, z);
                    }
                }
            }
        }
    }

    private void breakSurroundingBlocks(World world, Player player, int x, int y, int z) {
        if (world.getBlock(x, y, z) != null && world.getBlock(x, y, z).getHardness() >= 0.0F && world.getBlock(x, y, z) != BlockList.lockedChest) {
            BlockList.blocks[world.getBlockId(x, y, z)].dropItem(world, player, x, y, z, world.getBlockMetadata(x, y, z));
            BlockList.blocks[world.getBlockId(x, y, z)].onBroken(world, x, y, z, world.getBlockMetadata(x, y, z));
            world.playEffect(2003, x, y, z, world.getBlockMetadata(x, y, z) << 12 | world.getBlockId(x, y, z));
            world.setBlockWithNotify(x, y, z, 0);
        }
    }

    @Override
    public int getDamageVsEntity(Entity entity) {
        return this.atkDmg;
    }

    @Override
    public boolean isEnchantable(ItemStack input, ItemStack slot2) {
        return slot2 == null ? false : slot2.getItem() == ItemList.scroll && (slot2.getItemDamage() == 2 || slot2.getItemDamage() == 3);
    }

    @Override
    public boolean isRepairable(ItemStack input, ItemStack slot2) {
        return slot2 == null ? false : slot2.itemID == this.toolMaterial.getRepairID() || slot2.itemID == this.id;
    }

    @Override
    public int repairCost() {
        return 500 * this.toolMaterial.getHarvestLevel();
    }
}
