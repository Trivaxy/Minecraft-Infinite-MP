package net.minecraft.game.item;

import com.mojang.nbt.tag.special.CompoundTag;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import net.minecraft.game.block.Block;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;

public class Item {
    public static final Logger logger = Logger.getLogger("Minecraft");
    protected static final Random rand = new Random();
    public Rarity rarity = Rarity.COMMON;
    public Icon itemIcon;
    private CreativeTabs creativeTab;
    public String internalName;
    public String fullName;
    protected String description;
    protected ArrayList<String> info = new ArrayList<>();
    public int potionPrefix = -1;
    private String potionEffect = null;
    public final int id;
    public int maxDamage = 0;
    public int maxStackSize = 64;
    public int attackDelay = 5;
    public int outputId = 0;
    public int outputMeta = 0;
    public int outputCount = 0;
    protected boolean hasSubtypes = false;
    public boolean priorityItem = false;
    public boolean isWeapon = false;
    public boolean canSwing = true;
    private boolean canBurn = true;
    private boolean compostable = false;
    public boolean unbreakable = false;

    protected Item(int id) {
        this.id = 4096 + id;
        if (ItemList.items[4096 + id] != null) {
            logger.warning("Item already registered for id " + id + " when adding " + this);
        }

        ItemList.items[4096 + id] = this;
    }

    protected Item setPriorityItem() {
        this.priorityItem = true;
        return this;
    }

    public Item setBurnRate(boolean burn) {
        this.canBurn = burn;
        return this;
    }

    public Item setCompostable(boolean burn) {
        this.compostable = burn;
        return this;
    }

    public Item setAttackDelay(int atk) {
        this.attackDelay = atk;
        return this;
    }

    public Item setMaxStackSize(int i) {
        this.maxStackSize = i;
        return this;
    }

    public Item setRarity(Rarity i) {
        this.rarity = i;
        return this;
    }

    public Item setReduceAmount(int i, int i2) {
        this.outputId = 4449;
        this.outputMeta = i;
        this.outputCount = i2;
        return this;
    }

    public Item setInternalName(String name) {
        this.internalName = name;
        this.fullName = "item." + name;
        this.description = this.fullName + ".desc";
        return this;
    }

    protected Item setItemDescription(String s) {
        this.info.add(s);
        return this;
    }

    public Item setCreativeTab(CreativeTabs tab) {
        this.creativeTab = tab;
        return this;
    }

    public Item setHasSubtypes(boolean z) {
        this.hasSubtypes = z;
        return this;
    }

    protected Item setPotionPrefix(int prefixData, String prefix) {
        this.potionPrefix = prefixData;
        this.potionEffect = prefix;
        return this;
    }

    public boolean canBurn() {
        return this.canBurn;
    }

    public boolean canCompost() {
        return this.compostable;
    }

    public Rarity getRarity() {
        return this.rarity;
    }

    public boolean useOnBlock(ItemStack stack, Player player, World world, int xCoord, int yCoord, int zCoord, int meta) {
        player.addStat(StatList.useStats[this.id], 1);
        return false;
    }

    public ItemStack use(ItemStack stack, World world, Player player) {
        player.addStat(StatList.useStats[this.id], 1);
        return stack;
    }

    public void useOnEntity(ItemStack stack, Mob mob, Player player) {
    }

    public float getDigSpd(ItemStack stack, Block block) {
        return 1.0F;
    }

    public boolean isRepairable(ItemStack input, ItemStack output) {
        return false;
    }

    public int getStackLimit() {
        return this.maxStackSize;
    }

    public int getMaxDamage() {
        return this.maxDamage;
    }

    public void hitEntity(ItemStack stack, Mob mob, Player player, World world) {
    }

    public void onBlockDestroyed(ItemStack stack, int side, int x, int y, int z, Mob mob) {
    }

    public int getDamageVsEntity(Entity entity) {
        return 1;
    }

    public boolean canHarvestBlock(Block block) {
        return false;
    }

    public int getAtlas() {
        return 1;
    }

    public boolean isDamageable() {
        return this.maxDamage > 0 && !this.hasSubtypes;
    }

    public boolean hasSubtypes() {
        return this.hasSubtypes;
    }

    public void registerIcons(IconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon(this.internalName);
    }

    public Icon getIconFromMeta(int meta) {
        return this.itemIcon;
    }

    public final Icon getIconIndex(ItemStack stack) {
        return this.getIconFromMeta(stack.getItemDamage());
    }

    public Icon getIconFrame(int frame) {
        return this.itemIcon;
    }

    public CreativeTabs creativeCategory() {
        return this.creativeTab;
    }

    public int getPotionPrefix() {
        return this.potionPrefix;
    }

    public String getPotionEffect() {
        return this.potionEffect;
    }

    public void getSubItems(int id, CreativeTabs ct, List<ItemStack> ls) {
        ls.add(new ItemStack(id, 1, 0));
    }

    public String getNameFromMeta(int dmg) {
        return this.fullName;
    }

    public String getDescFromMeta(int dmg) {
        return this.description;
    }

    public String getInfoLine(ItemStack stack, int i) {
        return this.info.get(i);
    }

    public int getInfoLines(ItemStack stack) {
        return this.info.size();
    }

    public void addInformation(ItemStack stack) {
    }

    public void onCrafted(ItemStack stack, World world, Player player) {
    }

    public void onUpdate(ItemStack stack, World world, Entity entity, int meta, boolean update) {
    }

    public int getMaxUseDuration(ItemStack stack) {
        return 0;
    }

    public int repairCost() {
        return 0;
    }

    public Item.Action getItemUseAction(ItemStack itemstack) {
        return Item.Action.none;
    }

    public ItemStack onFinishedUsing(ItemStack itemstack, World world, Player entityplayer) {
        return itemstack;
    }

    public void onStoppedUsing(ItemStack stack, World world, Player player, int useCount) {
    }

    protected int defaultColor() {
        return 16777215;
    }

    public int getColor(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return this.defaultColor();
        }

        CompoundTag displayTag = tag.getCompoundTag("display");
        return displayTag == null ? this.defaultColor() : (displayTag.hasExtraValues("color", 3) ? displayTag.getInteger("color") : this.defaultColor());
    }

    public boolean hasColor(ItemStack stack) {
        return !stack.hasTag()
            ? false
            : (!stack.getTag().hasExtraValues("display", 10) ? false : stack.getTag().getCompoundTag("display").hasExtraValues("color", 3));
    }

    public void removeColor(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            CompoundTag displayTag = tag.getCompoundTag("display");
            if (displayTag.hasKey("color")) {
                displayTag.removeTag("color");
            }
        }
    }

    public void addColor(ItemStack stack, int color) {
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            tag = new CompoundTag();
            stack.setTag(tag);
        }

        CompoundTag displayTag = tag.getCompoundTag("display");
        if (!tag.hasExtraValues("display", 10)) {
            tag.setTag("display", displayTag);
        }

        displayTag.setInteger("color", color);
    }

    public int getColorTint(ItemStack stack) {
        int colVal = this.getColor(stack);
        if (colVal < 0) {
            colVal = 16777215;
        }

        return colVal;
    }

    public boolean isDyable() {
        return false;
    }

    public boolean hasEffect(ItemStack stack) {
        return stack.isItemEnchanted();
    }

    public boolean isEnchantable(ItemStack input, ItemStack output) {
        return false;
    }

    public void triggerAchievement(Player player) {
    }

    public enum Action {
        none,
        eat,
        drink,
        block,
        bow,
        dig,
        joust;
    }
}
