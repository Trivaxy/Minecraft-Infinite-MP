package net.minecraft.game.item.weapons;

import java.util.List;
import net.minecraft.game.ParticleType;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.monster.Monster;
import net.minecraft.game.entity.mob.monster.Pigman;
import net.minecraft.game.entity.mob.monster.zombie.PigmanZombie;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;
import net.minecraft.statistics.achievement.AchievementList;
import net.minecraft.util.math.MathHelper;

public class Whip extends Item {
    private Icon altIcon;

    public Whip(int itemid, int dmg) {
        super(itemid);
        this.maxStackSize = 1;
        this.maxDamage = dmg;
        this.priorityItem = true;
        this.isWeapon = true;
        this.canSwing = false;
        this.info.add(this.maxDamage + " item.desc.durability");
        this.info.add("7 item.desc.attack");
    }

    @Override
    public int getDamageVsEntity(Entity entity) {
        return 7;
    }

    @Override
    public void hitEntity(ItemStack stack, Mob target, Player player, World world) {
        stack.damageItem(1, player);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, Player player, int itemInUseCount) {
        if (stack.getMaxItemUseDuration() - player.itemAnimFrames >= 18) {
            float force = (this.getMaxUseDuration(stack) - itemInUseCount) / 20.0F;
            force = (force * force + force * 2.0F) / 3.0F;
            if (force < 0.1) {
                return;
            }

            if (force > 1.0F) {
                force = 1.0F;
            }

            if (player.inventory.getOffHandItem() != null && player.itemInUse == player.inventory.getOffHandItem()) {
                player.swingOffhandItem();
            } else {
                player.swingItem();
            }

            if (this.id != ItemList.harp.id) {
                double rotX = -MathHelper.sin(player.yaw * (float) (Math.PI / 180.0));
                double rotZ = MathHelper.cos(player.yaw * (float) (Math.PI / 180.0));
                world.spawnParticle(
                    ParticleType.SWEEP,
                    player.posX + rotX,
                    player.posY - 0.3,
                    player.posZ + rotZ,
                    world.rand.nextGaussian() * 0.02,
                    world.rand.nextGaussian() * 0.02,
                    world.rand.nextGaussian() * 0.02
                );
                world.playEntitySound(player, "random.whip", 1.0F, 1.0F / (rand.nextFloat() * 0.4F + 1.2F) + force * 0.5F);
            } else {
                world.playEntitySound(player, "random.harp", 1.0F, 1.0F / (rand.nextFloat() * 0.4F + 1.2F) + force * 0.5F);
            }

            double range = 4.0;
            if (this.id == ItemList.harp.id) {
                range = 6.0;
            }

            List<Entity> entities = world.getEntities(
                player,
                BoundingBox.getBoundingBox(
                    player.posX - range, player.posY - range, player.posZ - range, player.posX + range, player.posY + range, player.posZ + range
                )
            );

            for (int index = 0; index < entities.size(); index++) {
                Entity entity = entities.get(index);
                if (this.id != ItemList.harp.id) {
                    if (entity instanceof Mob && entity.visible) {
                        entity.damageEntity(DamageSource.mobDamage(player), 7);
                    }

                    if (entities.size() >= 5) {
                        player.triggerAchievement(AchievementList.whip);
                    }
                } else if (entity instanceof Monster && !entity.boss) {
                    ((Monster)entity).calmTime = 500;
                    player.triggerAchievement(AchievementList.charmMob);
                } else if (entity instanceof Pigman) {
                    ((Pigman)entity).calmTime = 500;
                    ((Pigman)entity).angerLevel = 0;
                    player.triggerAchievement(AchievementList.charmMob);
                } else if (entity instanceof PigmanZombie) {
                    ((PigmanZombie)entity).calmTime = 500;
                    ((PigmanZombie)entity).angryTime = 0;
                    player.triggerAchievement(AchievementList.charmMob);
                }
            }

            stack.damageItem(5, player);
            player.addStat(StatList.useStats[this.id], 1);
        }
    }

    @Override
    public Item.Action getItemUseAction(ItemStack stack) {
        return Item.Action.bow;
    }

    @Override
    public int getMaxUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public ItemStack use(ItemStack stack, World world, Player player) {
        player.setItemInUse(stack, this.getMaxUseDuration(stack));
        return stack;
    }

    @Override
    public boolean isRepairable(ItemStack stack1, ItemStack stack2) {
        if (stack2 == null) {
            return super.isRepairable(stack1, stack2);
        } else {
            return this.id == ItemList.whip.id
                ? stack2.itemID == ItemList.emerald.id || stack2.itemID == this.id
                : stack2.itemID == ItemList.ingotGold.id || stack2.itemID == this.id;
        }
    }

    @Override
    public boolean isEnchantable(ItemStack stack1, ItemStack stack2) {
        return stack2 == null ? false : stack2.getItem() == ItemList.scroll && stack2.getItemDamage() == 3;
    }

    @Override
    public int repairCost() {
        return 3000;
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.itemIcon = ir.registerIcon(this.internalName);
        this.altIcon = ir.registerIcon("whipUnleashed");
    }

    @Override
    public Icon getIconFromMeta(int par1) {
        return par1 == -1 && this.id == ItemList.whip.id ? this.altIcon : this.itemIcon;
    }
}
