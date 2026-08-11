package net.minecraft.game.item.weapons;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.game.container.player.PlayerContainer;
import net.minecraft.game.entity.arrow.Arrow;
import net.minecraft.game.entity.arrow.CalmingArrow;
import net.minecraft.game.entity.arrow.FlameArrow;
import net.minecraft.game.entity.arrow.FrostArrow;
import net.minecraft.game.entity.arrow.GlowArrow;
import net.minecraft.game.entity.arrow.LightningArrow;
import net.minecraft.game.entity.arrow.MagicArrow;
import net.minecraft.game.entity.arrow.PoisonArrow;
import net.minecraft.game.entity.arrow.RopeArrow;
import net.minecraft.game.entity.arrow.SpawnArrow;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.enchantment.EnchantmentHelper;
import net.minecraft.game.item.enchantment.EnchantmentList;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;

public class Bow extends Item {
    public static final Int2ObjectMap<Class<? extends Arrow>> arrowsMap = new Int2ObjectOpenHashMap<>();

    public Bow(int id) {
        super(id);
        this.maxStackSize = 1;
        this.maxDamage = 256;
        this.priorityItem = true;
        this.canSwing = false;
        this.info.add(this.maxDamage + " item.desc.durability");
    }

    @Override
    public ItemStack use(ItemStack stack, World world, Player player) {
        boolean isQuiverEquipped = player.inventory.accessorySlots[2] != null
            && player.inventory.accessorySlots[2].itemID == ItemList.quiverGold.id
            && player.inventory.accessorySlots[2].isNotBroken();
        if (player.properties.gamemode != 1 && !isQuiverEquipped) {
            if (player.inventory.accessorySlots[2] != null
                && (player.inventory.accessorySlots[2].itemID == ItemList.quiver.id || player.inventory.accessorySlots[2].itemID == ItemList.quiverGold.id)) {
                boolean canFire = false;
                PlayerContainer cocainer = (PlayerContainer)player.container;
                int selectedSlot = -1;
                Class<? extends Arrow> arrowClass = null;

                for (int slot = 0; slot < 3; slot++) {
                    ItemStack slotStack = cocainer.quiverContents.getStackInSlot(slot);
                    if (slotStack != null) {
                        int id = cocainer.getQuiverSlot(slot);
                        Class<? extends Arrow> clazz = arrowsMap.get(id);
                        if (clazz != null) {
                            selectedSlot = slot;
                            arrowClass = clazz;
                            canFire = true;
                            break;
                        }
                    }
                }

                try {
                    Arrow arrow = arrowClass.getConstructor(World.class, Mob.class, float.class).newInstance(world, player, 1.0F);
                    int dmgAmp = EnchantmentHelper.getLevel(EnchantmentList.power.id, stack);
                    if (dmgAmp > 0) {
                        arrow.setDamage(arrow.getDamage() + dmgAmp * 3);
                        arrow.setKnockback(dmgAmp);
                    }

                    if (!world.isOnline && canFire) {
                        cocainer.consumeQuiverItem(selectedSlot);
                        world.playEntitySound(player, "random.bow", 1.0F, 1.0F / (rand.nextFloat() * 0.4F + 0.8F));
                        world.spawnEntity(arrow);
                        stack.damageItem(1, player);
                        player.addStat(StatList.useStats[this.id], 1);
                    }
                } catch (Exception ex) {
                    this.shootArrow(player, stack, world);
                }
            } else {
                this.shootArrow(player, stack, world);
            }
        } else {
            Arrow arrow = new Arrow(world, player, 1.0F);
            arrow.shotByPlayer = false;
            int dmgAmp = EnchantmentHelper.getLevel(EnchantmentList.power.id, stack);
            if (dmgAmp > 0) {
                arrow.setDamage(arrow.getDamage() + dmgAmp * 3);
                arrow.setKnockback(dmgAmp);
            }

            world.playEntitySound(player, "random.bow", 1.0F, 1.0F / (rand.nextFloat() * 0.4F + 0.8F));
            world.spawnEntity(arrow);
            if (isQuiverEquipped) {
                player.inventory.accessorySlots[2].damageItem(1, player);
                stack.damageItem(1, player);
            }
        }

        return stack;
    }

    private void shootArrow(Player player, ItemStack stack, World world) {
        try {
            Arrow arrow = arrowsMap.get(player.inventory.getArrow(false)).getConstructor(World.class, Mob.class, float.class).newInstance(world, player, 1.0F);
            int dmgAmp = EnchantmentHelper.getLevel(EnchantmentList.power.id, stack);
            if (dmgAmp > 0) {
                arrow.setDamage(arrow.getDamage() + dmgAmp * 3);
                arrow.setKnockback(dmgAmp);
            }

            if (!world.isOnline && player.inventory.consumeArrow(false)) {
                world.playEntitySound(player, "random.bow", 1.0F, 1.0F / (rand.nextFloat() * 0.4F + 0.8F));
                world.spawnEntity(arrow);
                stack.damageItem(1, player);
                player.addStat(StatList.useStats[this.id], 1);
            }
        } catch (Exception var6) {
        }
    }

    @Override
    public boolean isEnchantable(ItemStack stack1, ItemStack stack2) {
        return stack2 == null ? false : stack2.getItem() == ItemList.scroll && (stack2.getItemDamage() == 5 || stack2.getItemDamage() == 3);
    }

    @Override
    public boolean isRepairable(ItemStack stack1, ItemStack stack2) {
        return stack2 == null ? super.isRepairable(stack1, stack2) : stack2.itemID == ItemList.silk.id || stack2.itemID == this.id;
    }

    @Override
    public int repairCost() {
        return 100;
    }

    private static void addMapping(Class<? extends Arrow> entityclass, int id) {
        arrowsMap.put(id, entityclass);
    }

    static {
        addMapping(Arrow.class, 4218);
        addMapping(FlameArrow.class, 4280);
        addMapping(FrostArrow.class, 4281);
        addMapping(PoisonArrow.class, 4282);
        addMapping(MagicArrow.class, 4332);
        addMapping(LightningArrow.class, 4241);
        addMapping(CalmingArrow.class, 4415);
        addMapping(GlowArrow.class, 4466);
        addMapping(RopeArrow.class, 4269);
        addMapping(SpawnArrow.class, 4284);
    }
}
