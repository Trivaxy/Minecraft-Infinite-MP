package net.minecraft.game.item;

import com.mojang.nbt.tag.Tag;
import com.mojang.nbt.tag.special.CompoundTag;
import com.mojang.nbt.tag.special.ListTag;
import java.util.ArrayList;
import net.minecraft.game.block.Block;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.hanging.Frame;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.item.enchantment.Enchantment;
import net.minecraft.game.item.enchantment.EnchantmentHelper;
import net.minecraft.game.item.enchantment.EnchantmentList;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;
import net.minecraft.statistics.achievement.AchievementList;

public final class ItemStack {
    public CompoundTag customTag;
    private Frame itemFrame;
    public String customName = "";
    private ArrayList<String> descriptionLines = new ArrayList<>();
    public int stackSize;
    public int animationsToGo;
    public int itemID;
    public int itemDmg;
    public float textTime;
    public float popupTime;

    public ItemStack(Block block) {
        this(block, 1);
    }

    public ItemStack(Block block, int stackSize) {
        this(block.id, stackSize);
    }

    public ItemStack(Block block, int count, int damage) {
        this(block.id, count, damage);
    }

    public ItemStack(Item item) {
        this(item, 1);
    }

    public ItemStack(Item item, int stackSize) {
        this(item.id, stackSize);
    }

    public ItemStack(Item item, int stackSize, int damage) {
        this(item.id, stackSize, damage);
    }

    public ItemStack(int id) {
        this(id, 1);
    }

    public ItemStack(int id, int size) {
        this.itemID = id;
        this.stackSize = size;
    }

    public ItemStack(int id, int size, int dmg) {
        this.itemID = id;
        this.stackSize = size;
        this.itemDmg = dmg;
    }

    public ItemStack(int id, int size, int dmg, String name) {
        this.itemID = id;
        this.stackSize = size;
        this.itemDmg = dmg;
        this.customName = name;
    }

    public ItemStack(int id, int size, int dmg, String name, CompoundTag nbt) {
        this.itemID = id;
        this.stackSize = size;
        this.itemDmg = dmg;
        this.customName = name;
        this.customName = name;
        this.setTag(nbt);
    }

    public ItemStack(CompoundTag compound) {
        this.itemID = compound.getShort("id");
        this.stackSize = compound.getByte("Count");
        this.itemDmg = compound.getShort("Damage");
        this.customName = compound.getString("CustomName");
        if (compound.hasKey("tag")) {
            this.customTag = compound.getCompoundTag("tag");
        }
    }

    private ItemStack() {
        this.stackSize = 0;
        this.itemFrame = null;
    }

    public ItemStack splitStack(int size) {
        ItemStack stck = new ItemStack(this.itemID, size, this.itemDmg, this.customName);
        if (this.customTag != null) {
            stck.customTag = (CompoundTag)this.customTag.copy();
        }

        this.stackSize -= size;
        return stck;
    }

    public Item getItem() {
        return ItemList.items[this.itemID];
    }

    public Icon getIconIndex() {
        return this.getItem().getIconIndex(this);
    }

    public int getSpriteNumber() {
        return this.getItem().getAtlas();
    }

    public boolean useItem(Player player, World world, int x, int y, int z, int meta) {
        return player.getUsing() ? false : this.getItem().useOnBlock(this, player, world, x, y, z, meta);
    }

    public ItemStack useItemOnBlock(World world, Player player) {
        return this.getItem().use(this, world, player);
    }

    public float getDigSpeed(Block block) {
        return this.getItem().getDigSpd(this, block);
    }

    public ItemStack onFinishedUsing(World world, Player player) {
        return this.getItem().onFinishedUsing(this, world, player);
    }

    public CompoundTag writeToNBT(CompoundTag tag) {
        tag.setShort("id", (short)this.itemID);
        tag.setByte("Count", (byte)this.stackSize);
        tag.setShort("Damage", (short)this.itemDmg);
        tag.setString("CustomName", this.customName);
        if (this.customTag != null) {
            tag.setTag("tag", this.customTag);
        }

        return tag;
    }

    public void readFromNBT(CompoundTag tag) {
        this.itemID = tag.getShort("id");
        this.stackSize = tag.getByte("Count");
        this.itemDmg = tag.getShort("Damage");
        this.customName = tag.getString("CustomName");
        if (this.itemDmg < 0) {
            this.itemDmg = 0;
        }

        if (tag.hasKey("tag")) {
            this.customTag = tag.getCompoundTag("tag");
        }
    }

    public int getStackLimit() {
        return this.getItem().getStackLimit();
    }

    public ItemStack copy() {
        ItemStack stck = new ItemStack(this.itemID, this.stackSize, this.itemDmg, this.customName);
        if (this.customTag != null) {
            stck.customTag = (CompoundTag)this.customTag.copy();
        }

        return stck;
    }

    public boolean isStackDamageable() {
        return this.getItem().getMaxDamage() > 0 && !this.getItem().hasSubtypes() ? !this.hasTag() || !this.getTag().getBoolean("Unbreakable") : false;
    }

    public boolean isStackable() {
        return this.getStackLimit() > 1 && (!this.isStackDamageable() || !this.isItemDamaged() || this.hasSubtypes());
    }

    public boolean isItemDamaged() {
        return this.isStackDamageable() && this.itemDmg > 0;
    }

    public boolean isNotBroken() {
        return this.itemDmg < this.getMaxDamage();
    }

    public int getMaxDamage() {
        return this.getItem().getMaxDamage();
    }

    public void damageItem(int amount, Mob entity) {
        if (!(entity instanceof Player) || ((Player)entity).properties.getDurability() && ((Player)entity).properties.gamemode != 1) {
            if (this.isStackDamageable()) {
                if (amount > 0 && entity instanceof Player) {
                    int chance = EnchantmentHelper.getUnbreakingModifier(((Player)entity).inventory);
                    if (chance > 0 && entity.world.rand.nextInt(chance + 1) > 0) {
                        return;
                    }
                }

                boolean hasBracelet = entity instanceof Player
                    && ((Player)entity).inventory.accessorySlots[0] != null
                    && ((Player)entity).inventory.accessorySlots[0].getItem() == ItemList.bracelet
                    && ((Player)entity).inventory.accessorySlots[0].isNotBroken();
                this.itemDmg += amount;
                ((Player)entity).addStat(StatList.useStats[this.itemID], 1);
                if (this.itemDmg > this.getMaxDamage()) {
                    if (!this.getItem().unbreakable && !hasBracelet) {
                        entity.breakItem(this);
                        if (entity instanceof Player) {
                            Player player = (Player)entity;
                            player.addStat(StatList.breakStats[this.itemID], 1);
                            player.addStat(StatList.itemsBrokenStat, 1);
                            if (this.itemID == ItemList.hoeDamascus.id) {
                                ((Player)entity).triggerAchievement(AchievementList.troll);
                            }

                            if (this.itemID == ItemList.grapplingHook.id && ((Player)entity).hook != null) {
                                player.hook.setDead();
                                player.hook = null;
                            }
                        }

                        this.stackSize--;
                        if (this.stackSize < 0) {
                            this.stackSize = 0;
                        }

                        this.itemDmg = 0;
                        if (entity instanceof Player) {
                            Player player = (Player)entity;
                            if (player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().stackSize <= 0) {
                                player.inventory.mainInventory[player.inventory.currentSlot] = null;
                            } else if (player.inventory.getOffHandItem() != null && player.inventory.getOffHandItem().stackSize <= 0) {
                                player.inventory.offhandSlot[0] = null;
                            }
                        }
                    } else {
                        this.itemDmg = this.getMaxDamage();
                        if (hasBracelet) {
                            ((Player)entity).inventory.accessorySlots[0].damageItem(amount, entity);
                        }
                    }
                }
            }
        }
    }

    public void updateItem(World world, Entity entity, int slot, boolean check) {
        this.getItem().onUpdate(this, world, entity, slot, check);
    }

    public void hitEntity(Mob target, Player player, World world) {
        if (this.itemID == ItemList.swordVampire.id && target.health <= 0) {
            player.heal(2);
        }

        if (this.itemID == ItemList.mace.id) {
            player.fallDistance = 0.0F;
        }

        this.getItem().hitEntity(this, target, player, world);
    }

    public void onDestroyBlock(int x, int y, int z, int meta, Mob mob) {
        this.getItem().onBlockDestroyed(this, x, y, z, meta, mob);
    }

    public int getDamageVsEntity(Entity entity) {
        return this.getItem().getDamageVsEntity(entity);
    }

    public boolean canHarvestBlock(Block block) {
        return this.getItem().canHarvestBlock(block);
    }

    public void useItemOnEntity(Mob mob, Player playa) {
        this.getItem().useOnEntity(this, mob, playa);
    }

    public int getItemDamage() {
        return this.itemDmg;
    }

    public void setItemDamage(int dmg) {
        this.itemDmg = dmg;
    }

    public void onCrafting(World world, Player player, int amount) {
        player.addStat(StatList.craftStats[this.itemID], amount);
        this.getItem().onCrafted(this, world, player);
    }

    public boolean hasSubtypes() {
        return this.getItem().hasSubtypes();
    }

    public static boolean areItemStackTagsEqual(ItemStack stack1, ItemStack stack2) {
        return stack1 == null && stack2 == null
            ? true
            : (
                stack1 == null || stack2 == null
                    ? false
                    : (stack1.customTag == null && stack2.customTag != null ? false : stack1.customTag == null || stack1.customTag.equals(stack2.customTag))
            );
    }

    public static boolean areItemStacksEqual(ItemStack stack1, ItemStack stack2) {
        return stack1 == null && stack2 == null ? true : (stack1 != null && stack2 != null ? stack1.isItemStackEqual(stack2) : false);
    }

    private boolean isItemStackEqual(ItemStack stack) {
        return this.stackSize != stack.stackSize ? false : (this.itemID != stack.itemID ? false : this.itemDmg == stack.itemDmg);
    }

    public boolean isItemEqual(ItemStack stack) {
        return this.itemID == stack.itemID && this.itemDmg == stack.itemDmg;
    }

    public String getItemName(int damage) {
        return this.getItem().getNameFromMeta(damage);
    }

    public int getItemInfoLength() {
        int i = this.getItem().getInfoLines(this);
        if (this.hasTag()) {
            ListTag<CompoundTag> tag = this.getEnchants();
            if (tag != null) {
                for (int id = 0; id < tag.size(); id++) {
                    if (EnchantmentList.enchantments[tag.get(id).getShort("id")] != null) {
                        i++;
                    }
                }
            }
        }

        return i;
    }

    public String getItemDescription(int i) {
        return this.getItem().getDescFromMeta(i);
    }

    public String getItemInformation(int i) {
        this.getItem().addInformation(this);
        if (i < this.getItem().getInfoLines(this)) {
            this.descriptionLines.add(this.getItem().getInfoLine(this, i));
        } else if (i >= this.getItem().getInfoLines(this) && this.hasTag()) {
            ListTag<CompoundTag> tag = this.getEnchants();
            if (tag != null) {
                for (int index = 0; index < tag.size(); index++) {
                    short id = tag.get(index).getShort("id");
                    short lvl = tag.get(index).getShort("lvl");
                    if (EnchantmentList.enchantments[id] != null) {
                        this.descriptionLines.add(EnchantmentList.enchantments[id].getTranslatedName(lvl));
                    }
                }
            }
        }

        return this.descriptionLines.get(i);
    }

    public static ItemStack copyStack(ItemStack stack) {
        return stack == null ? null : stack.copy();
    }

    public CreativeTabs getCreativeTab() {
        return this.getItem().creativeCategory();
    }

    public static ItemStack loadStackFromNBT(CompoundTag tag) {
        ItemStack stack = new ItemStack();
        stack.readFromNBT(tag);
        return stack.getItem() != null ? stack : null;
    }

    public boolean hasTag() {
        return this.customTag != null;
    }

    public CompoundTag getTag() {
        return this.customTag;
    }

    public void setTag(CompoundTag tag) {
        this.customTag = tag;
    }

    public void setTagInfo(String info, Tag tag) {
        if (this.customTag == null) {
            this.setTag(new CompoundTag());
        }

        this.customTag.setTag(info, tag);
    }

    public CompoundTag getSubCompound(String key) {
        return this.customTag != null && this.customTag.hasKey(key) ? this.customTag.getCompoundTag(key) : null;
    }

    public boolean isInFrame() {
        return this.itemFrame != null;
    }

    public void setFrame(Frame frame) {
        this.itemFrame = frame;
    }

    public Frame getFrame() {
        return this.itemFrame;
    }

    public Item.Action getItemUseAction() {
        return this.getItem().getItemUseAction(this);
    }

    public int getMaxItemUseDuration() {
        return this.getItem().getMaxUseDuration(this);
    }

    public void onPlayerStoppedUsing(World world, Player player, int itemInUseCount) {
        this.getItem().onStoppedUsing(this, world, player, itemInUseCount);
    }

    public String getDisplayName() {
        if (this.getItem().rarity != Rarity.COMMON) {
            String rarityColor = "";
            if (this.getItem().rarity == Rarity.RARE) {
                rarityColor = "§b";
            } else if (this.getItem().rarity == Rarity.EPIC) {
                rarityColor = "§d";
            } else if (this.getItem().rarity == Rarity.LEGENDARY) {
                rarityColor = "§e";
            }

            return rarityColor + this.customName;
        } else {
            return "§b" + this.customName;
        }
    }

    public boolean hasDisplayName() {
        return !this.customName.isEmpty();
    }

    public void setItemName(String newName) {
        this.customName = newName;
    }

    public int getRepairCost() {
        return this.getItem().repairCost();
    }

    public ListTag<CompoundTag> getEnchants() {
        return this.customTag == null ? null : (ListTag)this.customTag.getNBTTag("ench");
    }

    public void addEnchantment(Enchantment enchant, int level) {
        if (this.customTag == null) {
            this.customTag = new CompoundTag();
        }

        if (!this.customTag.hasKey("ench")) {
            ListTag<CompoundTag> list = new ListTag<>();
            list.setKey("ench");
            this.customTag.setTag("ench", list);
        }

        ListTag<CompoundTag> enchants = this.getEnchants();
        if (enchants != null) {
            for (int i = 0; i < enchants.size(); i++) {
                if (enchants.get(i).getShort("id") == enchant.id) {
                    enchants.get(i).removeTag("id");
                }
            }
        }

        ListTag<CompoundTag> tags = (ListTag<CompoundTag>)this.customTag.getTag("ench");
        CompoundTag enchantTag = new CompoundTag();
        enchantTag.setShort("id", (short)enchant.id);
        enchantTag.setShort("lvl", (byte)level);
        tags.add(enchantTag);
    }

    public void removeEnchant(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.hasKey("ench")) {
            tag.removeTag("ench");
        }
    }

    public boolean hasEffect() {
        return this.getItem().hasEffect(this);
    }

    public boolean isItemEnchanted() {
        return this.customTag != null && this.customTag.hasKey("ench");
    }
}
