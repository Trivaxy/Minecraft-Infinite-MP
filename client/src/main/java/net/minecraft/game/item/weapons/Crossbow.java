package net.minecraft.game.item.weapons;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.game.container.player.PlayerContainer;
import net.minecraft.game.entity.arrow.Arrow;
import net.minecraft.game.entity.arrow.Bolt;
import net.minecraft.game.entity.arrow.BoneArrow;
import net.minecraft.game.entity.arrow.ExplosiveArrow;
import net.minecraft.game.entity.arrow.PiercingBolt;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.enchantment.EnchantmentHelper;
import net.minecraft.game.item.enchantment.EnchantmentList;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;

public class Crossbow extends Item {
    public static final String[] iconNames = new String[]{"crossbow", "crossbow_frame1", "crossbow_frame2", "crossbow_frame3"};
    private Icon[] icons;
    public static final Int2ObjectMap<Class<? extends Arrow>> arrowsMap = new Int2ObjectOpenHashMap<>();

    public Crossbow(int id) {
        super(id);
        this.maxStackSize = 1;
        this.maxDamage = 256;
        this.priorityItem = true;
        this.canSwing = false;
        this.info.add(this.maxDamage + " item.desc.durability");
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, Player player, int itemInUseCount) {
        boolean hasGoldenQuiver = player.inventory.accessorySlots[2] != null
            && player.inventory.accessorySlots[2].itemID == ItemList.quiverGold.id
            && player.inventory.accessorySlots[2].isNotBroken();
        float force = (this.getMaxUseDuration(stack) - itemInUseCount) / 20.0F;
        force = (force * force + force * 2.0F) / 3.0F;
        if (!(force < 0.1)) {
            if (force > 1.0F) {
                force = 1.0F;
            }

            if (this == ItemList.crossboner) {
                if (player.properties.gamemode == 1 || hasGoldenQuiver || player.inventory.consumeInventoryItem(ItemList.bone.id)) {
                    int arrowCount = 3;
                    float spread = 0.15F;
                    int center = arrowCount / 2;
                    int dmgMultiplier = EnchantmentHelper.getLevel(EnchantmentList.power.id, stack);
                    Vector look = player.getLookVector(1.0F);
                    Vector right = Vector.createVector(-look.zCoord, 0.0, look.xCoord).normalize();

                    for (int i = 0; i < arrowCount; i++) {
                        int offsetIndex = i - center;
                        Vector dir = look.addVector(right.xCoord * spread * offsetIndex, 0.0, right.zCoord * spread * offsetIndex);
                        Arrow arrow = new BoneArrow(world, player, force * 2.0F);
                        arrow.setHeading(dir.xCoord, dir.yCoord, dir.zCoord, force * 3.5F, 1.0F);
                        if (dmgMultiplier > 0) {
                            arrow.setDamage(arrow.getDamage() + dmgMultiplier * 2);
                            arrow.setKnockback(dmgMultiplier);
                        }

                        world.spawnEntity(arrow);
                    }

                    world.playEntitySound(player, "random.crossbow", 1.0F, 1.0F / (rand.nextFloat() * 0.4F + 1.2F) + force * 0.5F);
                    stack.damageItem(1, player);
                }
            } else if (player.properties.gamemode != 1 && !hasGoldenQuiver) {
                if ((
                        player.inventory.hasItem(ItemList.arrow.id)
                            || player.inventory.hasItem(ItemList.arrowPiercing.id)
                            || player.inventory.hasItem(ItemList.arrowExplosive.id)
                            || player.inventory.accessorySlots[2] != null
                                && (
                                    player.inventory.accessorySlots[2].itemID == ItemList.quiver.id
                                        || player.inventory.accessorySlots[2].itemID == ItemList.quiverGold.id
                                )
                    )
                    && stack.getMaxItemUseDuration() - player.itemAnimFrames >= 18) {
                    int arrowType = player.inventory.getArrow(true);
                    boolean hasQuiverEquipped = player.inventory.accessorySlots[2] != null && player.inventory.accessorySlots[2].itemID == ItemList.quiver.id;
                    hasGoldenQuiver = player.inventory.accessorySlots[2] != null && player.inventory.accessorySlots[2].itemID == ItemList.quiverGold.id;
                    boolean canShoot = player.inventory.consumeArrow(true);
                    PlayerContainer cocainer = (PlayerContainer)player.container;
                    int selectedSlot = -1;
                    Class<? extends Arrow> arrowClass = arrowsMap.get(arrowType);
                    if (hasQuiverEquipped || hasGoldenQuiver) {
                        for (int slot = 0; slot < 3; slot++) {
                            ItemStack quiverSlot = cocainer.quiverContents.getStackInSlot(slot);
                            if (quiverSlot != null) {
                                int id = cocainer.getQuiverSlot(slot);
                                Class<? extends Arrow> clazz = arrowsMap.get(id);
                                if (clazz != null) {
                                    selectedSlot = slot;
                                    arrowClass = clazz;
                                    canShoot = true;
                                    break;
                                }
                            }
                        }

                        if (!canShoot) {
                            canShoot = player.inventory.consumeArrow(true);
                        }
                    }

                    try {
                        Arrow arrow = arrowClass.getConstructor(World.class, Mob.class, float.class).newInstance(world, player, force * 2.0F);
                        int dmgMultiplier = EnchantmentHelper.getLevel(EnchantmentList.power.id, stack);
                        if (dmgMultiplier > 0) {
                            arrow.setDamage(arrow.getDamage() + dmgMultiplier * 2);
                            arrow.setKnockback(dmgMultiplier);
                        }

                        cocainer.consumeQuiverItem(selectedSlot);
                        world.playEntitySound(player, "random.crossbow", 1.0F, 1.0F / (rand.nextFloat() * 0.4F + 1.2F) + force * 0.5F);
                        if ((player.properties.gamemode == 1 || canShoot) && !world.isOnline) {
                            world.spawnEntity(arrow);
                            stack.damageItem(1, player);
                            player.addStat(StatList.useStats[this.id], 1);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                Arrow arrow = new Bolt(world, player, force * 2.0F);
                arrow.shotByPlayer = false;
                int dmgMultiplier = EnchantmentHelper.getLevel(EnchantmentList.power.id, stack);
                if (dmgMultiplier > 0) {
                    arrow.setDamage(arrow.getDamage() + dmgMultiplier * 2);
                    arrow.setKnockback(dmgMultiplier);
                }

                world.playEntitySound(player, "random.crossbow", 1.0F, 1.0F / (rand.nextFloat() * 0.4F + 1.2F) + force * 0.5F);
                world.spawnEntity(arrow);
                if (hasGoldenQuiver) {
                    player.inventory.accessorySlots[2].damageItem(1, player);
                    stack.damageItem(1, player);
                }
            }
        }
    }

    @Override
    public int getMaxUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public Item.Action getItemUseAction(ItemStack stack) {
        return Item.Action.bow;
    }

    @Override
    public ItemStack use(ItemStack stack, World world, Player player) {
        boolean hasGoldenQuiverEquipped = player.inventory.accessorySlots[2] != null
            && player.inventory.accessorySlots[2].itemID == ItemList.quiverGold.id
            && player.inventory.accessorySlots[2].isNotBroken();
        if (this == ItemList.crossboner) {
            if (player.inventory.hasItem(ItemList.bone.id) || player.properties.gamemode == 1 || hasGoldenQuiverEquipped) {
                player.setItemInUse(stack, this.getMaxUseDuration(stack));
            }

            return stack;
        } else {
            if (hasGoldenQuiverEquipped) {
                player.setItemInUse(stack, this.getMaxUseDuration(stack));
                return stack;
            }

            if ((
                    player.inventory.accessorySlots[2] == null
                        || (
                                player.inventory.accessorySlots[2].itemID != ItemList.quiverGold.id
                                        && player.inventory.accessorySlots[2].itemID != ItemList.quiver.id
                                    || ((PlayerContainer)player.container).hasQuiverItem(ItemList.arrow.id) < 0
                            )
                            && ((PlayerContainer)player.container).hasQuiverItem(ItemList.arrowPiercing.id) < 0
                )
                && ((PlayerContainer)player.container).hasQuiverItem(ItemList.arrowExplosive.id) < 0) {
                if (!player.inventory.hasItem(ItemList.arrow.id)
                    && !player.inventory.hasItem(ItemList.arrowPiercing.id)
                    && !player.inventory.hasItem(ItemList.arrowExplosive.id)
                    && player.properties.gamemode != 1) {
                    return stack;
                }

                player.setItemInUse(stack, this.getMaxUseDuration(stack));
                return stack;
            } else {
                player.setItemInUse(stack, this.getMaxUseDuration(stack));
                return stack;
            }
        }
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.itemIcon = ir.registerIcon("crossbow/" + this.internalName);
        this.icons = new Icon[iconNames.length];

        for (int icon = 0; icon < this.icons.length; icon++) {
            this.icons[icon] = ir.registerIcon("crossbow/" + iconNames[icon] + (this == ItemList.crossboner ? "_bone" : ""));
        }
    }

    @Override
    public Icon getIconFrame(int frame) {
        return this.icons[frame];
    }

    @Override
    public boolean isEnchantable(ItemStack stack1, ItemStack stack2) {
        return stack2 == null ? false : stack2.getItem() == ItemList.scroll && (stack2.getItemDamage() == 5 || stack2.getItemDamage() == 3);
    }

    @Override
    public boolean isRepairable(ItemStack stack1, ItemStack stack2) {
        int repairID = ItemList.ingotIron.id;
        if (this == ItemList.crossboner) {
            repairID = ItemList.bone.id;
        }

        return stack2 == null ? super.isRepairable(stack1, stack2) : stack2.itemID == repairID || stack2.itemID == this.id;
    }

    @Override
    public int repairCost() {
        return 500;
    }

    private static void addMapping(Class<? extends Arrow> entityclass, int id) {
        arrowsMap.put(id, entityclass);
    }

    static {
        addMapping(Bolt.class, 4102);
        addMapping(ExplosiveArrow.class, 4283);
        addMapping(PiercingBolt.class, 4417);
    }
}
