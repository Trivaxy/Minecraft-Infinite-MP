package net.minecraft.game.item.tools;

import net.minecraft.game.container.player.PlayerContainer;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.statistics.achievement.AchievementList;

public class Charm extends Item {
    public static final String[] slots = new String[]{"slot_empty_amulet", "slot_empty_quiver", "slot_empty_shield", "slot_empty_ring"};
    public static Icon[] charmSlot = new Icon[]{null, null, null, null};
    private int tier;
    public int type;

    public Charm(int itemid, int dim, int type, int dmg) {
        super(itemid);
        this.tier = dim;
        this.type = type;
        this.maxDamage = dmg;
        this.maxStackSize = 1;
        this.unbreakable = true;
        this.info.add(this.maxDamage + " item.desc.durability");
        if (this.id == 4397) {
            this.outputId = 4326;
            this.outputCount = 7;
        }
    }

    public Charm(int itemid, int dim, int type) {
        super(itemid);
        this.tier = dim;
        this.type = type;
        this.maxStackSize = 1;
    }

    @Override
    public final ItemStack use(ItemStack itemStack, World world, Player entityPlayer) {
        ItemStack charm = itemStack.copy();
        if (entityPlayer.inventory.canUseItem()) {
            entityPlayer.inventory.offhandSlot[0] = entityPlayer.inventory.accessorySlots[3 - this.type];
        } else {
            entityPlayer.inventory.mainInventory[entityPlayer.inventory.currentSlot] = entityPlayer.inventory.accessorySlots[3 - this.type];
        }

        entityPlayer.inventory.accessorySlots[3 - this.type] = charm;
        if (this.type == 1) {
            for (int i = 0; i < ((PlayerContainer)entityPlayer.openContainer).backpackContents.getSize(); i++) {
                ((PlayerContainer)entityPlayer.openContainer).backpackContents.setSlotContents(i, null);
            }

            for (int i = 0; i < ((PlayerContainer)entityPlayer.openContainer).quiverContents.getSize(); i++) {
                ((PlayerContainer)entityPlayer.openContainer).quiverContents.setSlotContents(i, null);
            }
        }

        if (itemStack.hasTag()) {
            ((PlayerContainer)entityPlayer.container).loadContents(itemStack);
        }

        world.playEntitySound(entityPlayer, "random.bundle", 1.0F, rand.nextFloat() * 0.4F + 0.8F);
        entityPlayer.triggerAchievement(AchievementList.charm);
        return itemStack;
    }

    @Override
    public boolean isRepairable(ItemStack stack1, ItemStack stack2) {
        if (stack2 != null) {
            switch (this.tier) {
                case 0:
                    return stack2.itemID == ItemList.emerald.id || stack2.itemID == this.id;
                case 1:
                    return stack2.itemID == ItemList.ruby.id || stack2.itemID == this.id;
                case 2:
                    return stack2.itemID == ItemList.sapphire.id || stack2.itemID == this.id;
                case 3:
                    return stack2.itemID == ItemList.ingotGold.id || stack2.itemID == this.id;
                case 4:
                    return stack2.itemID == ItemList.ingotCopper.id || stack2.itemID == this.id;
                case 5:
                    return stack2.itemID == ItemList.amethyst.id || stack2.itemID == this.id;
                case 6:
                    return stack2.itemID == ItemList.topaz.id || stack2.itemID == this.id;
            }
        }

        return super.isRepairable(stack1, stack2);
    }

    @Override
    public boolean isEnchantable(ItemStack stack1, ItemStack stack2) {
        return stack2 != null && stack2.getItem() == ItemList.scroll && stack2.itemDmg == 3;
    }

    @Override
    public int repairCost() {
        return this != ItemList.backpack && this != ItemList.quiver ? 5000 : 100;
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.itemIcon = ir.registerIcon("charms/" + this.internalName);

        for (int i = 0; i < 4; i++) {
            charmSlot[i] = ir.registerIcon("slots/" + slots[i]);
        }
    }

    @Override
    protected int defaultColor() {
        return this != ItemList.backpack && this != ItemList.quiver ? super.defaultColor() : 10511680;
    }

    public static Icon getIcon(int type) {
        return charmSlot[type];
    }

    @Override
    public Icon getIconFromMeta(int meta) {
        return this.itemIcon;
    }

    @Override
    public boolean isDyable() {
        return this == ItemList.backpack || this == ItemList.quiver;
    }
}
