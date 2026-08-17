package net.minecraft.game.item.weapons;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.stepsound.StepSound;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.CreatureType;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.entity.projectile.Fireball;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.potion.EffectData;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.item.tools.Tool;
import net.minecraft.game.item.tools.ToolMaterial;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;
import net.minecraft.statistics.achievement.AchievementList;

public class Sword extends Tool {
    private int weaponDamage;
    private CreatureType effectiveAgainst;

    public Sword(int itemid, ToolMaterial toolTier) {
        super(itemid, itemid, toolTier);
        this.maxStackSize = 1;
        this.maxDamage = toolTier.getDurability();
        this.weaponDamage = 4 + toolTier.getEntityDamage();
        this.attackDelay = 20;
        this.outputCount = 2;
        this.isWeapon = true;
        this.info.clear();
        this.info.add(this.maxDamage + " item.desc.durability");
        this.info.add(this.weaponDamage + " item.desc.attack");
    }

    public Sword(int itemid, ToolMaterial toolTier, CreatureType type) {
        this(itemid, toolTier);
        this.effectiveAgainst = type;
    }

    public Sword(int itemid, ToolMaterial toolTier, boolean unbreakable) {
        this(itemid, toolTier);
        this.unbreakable = unbreakable;
    }

    @Override
    public float getDigSpd(ItemStack stack, Block block) {
        return 1.5F;
    }

    @Override
    public void hitEntity(ItemStack stack, Mob mob, Player player, World world) {
        if (stack.itemID == ItemList.swordMichael.id && stack.getItemDamage() > 0) {
            stack.itemDmg--;
        } else {
            stack.damageItem(1, player);
        }

        if (this.id == ItemList.swordFrost.id && !mob.boss) {
            mob.freeze = 100;
        }

        if (this.id == ItemList.swordPoison.id) {
            mob.addEffect(new EffectData(EffectList.poison.id, 300, 3));
        }

        if (this.id == ItemList.battleAxe.id) {
            mob.addEffect(new EffectData(EffectList.slowness.id, 150, 3));
            mob.addEffect(new EffectData(EffectList.weakness.id, 150, 3));
            mob.addEffect(new EffectData(EffectList.fragility.id, 150, 3));
        }

        if (this.id == ItemList.swordPheonix.id) {
            mob.fire = 100;
        }
    }

    @Override
    public void onBlockDestroyed(ItemStack stack, int x, int y, int z, int side, Mob mob) {
        stack.damageItem(2, mob);
    }

    @Override
    public int getDamageVsEntity(Entity entity) {
        return this.effectiveAgainst != null && entity.getCreatureType() == this.effectiveAgainst ? this.weaponDamage * 4 : this.weaponDamage;
    }

    @Override
    public Item.Action getItemUseAction(ItemStack stack) {
        return Item.Action.block;
    }

    @Override
    public int getMaxUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public boolean useOnBlock(ItemStack stack, Player player, World world, int xCoord, int yCoord, int zCoord, int side) {
        if (this.id == ItemList.swordSpider.id) {
            if (side == 0) {
                yCoord--;
            }

            if (side == 1) {
                yCoord++;
            }

            if (side == 2) {
                zCoord--;
            }

            if (side == 3) {
                zCoord++;
            }

            if (side == 4) {
                xCoord--;
            }

            if (side == 5) {
                xCoord++;
            }

            if (world.getBlockId(xCoord, yCoord, zCoord) == 0) {
                Block cock = BlockList.cloth;
                StepSound snd = cock.getStepSound(0);
                world.playSoundEffect(player, xCoord + 0.5F, yCoord + 0.5F, zCoord + 0.5F, snd.getSound(), (snd.getVolume() + 1.0F) / 2.0F, snd.getPitch() * 0.8F);
                world.setBlockWithNotify(xCoord, yCoord, zCoord, BlockList.cobweb.id);
                player.addStat(StatList.useStats[this.id], 1);
                stack.damageItem(1, player);
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public ItemStack use(ItemStack stack, World world, Player player) {
        if (this.id == ItemList.swordPheonix.id) {
            if (player.inventory.canUseItem() && player.inventory.getOffHandItem().getItem() == stack.getItem()) {
                player.swingOffhandItem();
            } else {
                player.swingItem();
            }

            stack.damageItem(5, player);
            world.playEntitySound(player, "mob.ghast.shoot", 0.5F, 1.0F / (rand.nextFloat() * 0.4F + 0.8F));
            Entity throwable = new Fireball(world, player, 5);
            if (!world.isOnline) {
                world.spawnEntity(throwable);
            }
        } else if (this.id == ItemList.swordMichael.id
            && player.itemUseCooldown == 0
            && player.onGround
            && (player.motionX >= 0.05 || player.motionZ >= 0.05 || player.motionX <= -0.05 || player.motionZ <= -0.05)
            && stack.getItemDamage() < stack.getMaxDamage() - 5) {
            player.motionX *= 20.0;
            player.motionZ *= 20.0;
            if (player.inventory.canUseItem() && player.inventory.getOffHandItem().getItem() == stack.getItem()) {
                player.swingOffhandItem();
            } else {
                player.swingItem();
            }

            player.itemUseCooldown = player.maxDelay = 20;
            world.playSoundEffect(player, player.posX, player.posY, player.posZ, "mob.ghast.shoot", 1.0F, 1.0F / (world.rand.nextFloat() * 0.4F + 0.8F));
            stack.damageItem(5, player);
        } else if (player.inventory.getOffHandItem() == null) {
            player.setItemInUse(stack, this.getMaxUseDuration(stack));
        }

        player.addStat(StatList.useStats[this.id], 1);
        return stack;
    }

    @Override
    public boolean isRepairable(ItemStack stack1, ItemStack stack2) {
        return stack2 == null ? super.isRepairable(stack1, stack2) : stack2.itemID == this.toolMaterial.getRepairID() || stack2.itemID == this.id;
    }

    @Override
    public boolean isEnchantable(ItemStack stack1, ItemStack stack2) {
        return stack2 == null ? false : stack2.getItem() == ItemList.scroll && (stack2.getItemDamage() == 1 || stack2.getItemDamage() == 3);
    }

    @Override
    public int repairCost() {
        return 500 * this.toolMaterial.getHarvestLevel();
    }

    @Override
    public void triggerAchievement(Player player) {
        player.triggerAchievement(AchievementList.buildSword);
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.itemIcon = ir.registerIcon("swords/" + this.internalName);
    }
}
