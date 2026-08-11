package net.minecraft.game.item.tools;

import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class Armor extends Item {
    private static final int[] defValues = new int[]{3, 8, 6, 3};
    private static final int[] durValues = new int[]{11, 16, 15, 13};
    public static final String[] slots = new String[]{"slot_empty_helmet", "slot_empty_chestplate", "slot_empty_leggings", "slot_empty_boots"};
    public static final String[] armors = new String[]{
        "cloth", "chain", "iron", "diamond", "gold", "copper", "thorns", "poseidon", "lich", "phoenix", "valkyrie", "damascus", "adamantine"
    };
    public static Icon[] armorSlot = new Icon[]{null, null, null, null};
    public final ArmorMaterial tier;
    public final int type;
    public final int defense;
    public final int defenseOLD;
    public final int renderIndex;

    public Armor(int itemid, ArmorMaterial mat, int renderIndex, int armorType) {
        super(itemid);
        this.tier = mat;
        this.type = armorType;
        this.renderIndex = renderIndex;
        this.defense = mat.getDefense(armorType);
        this.defenseOLD = defValues[armorType];
        this.maxDamage = mat.getDurability(armorType);
        this.maxStackSize = 1;
        this.info.add(this.maxDamage + " item.desc.durability");
        this.info.add(this.defense + " item.desc.defense");
        switch (this.type) {
            case 0:
                this.outputCount = 5;
                break;
            case 1:
                this.outputCount = 8;
                break;
            case 2:
                this.outputCount = 7;
                break;
            case 3:
                this.outputCount = 4;
        }

        this.outputId = mat.getRepairID();
    }

    @Override
    public final ItemStack use(ItemStack itemStack, World world, Player entityPlayer) {
        ItemStack charm = itemStack.copy();
        if (entityPlayer.inventory.canUseItem()) {
            entityPlayer.inventory.offhandSlot[0] = entityPlayer.inventory.armorSlots[3 - this.type];
        } else {
            entityPlayer.inventory.mainInventory[entityPlayer.inventory.currentSlot] = entityPlayer.inventory.armorSlots[3 - this.type];
        }

        entityPlayer.inventory.armorSlots[3 - this.type] = charm;
        world.playEntitySound(entityPlayer, "random.bundle", 1.0F, rand.nextFloat() * 0.4F + 0.8F);
        return itemStack;
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.itemIcon = ir.registerIcon("armors/" + this.internalName);
        armorSlot[this.type] = ir.registerIcon("slots/" + slots[this.type]);
    }

    public static Icon getIcon(int type) {
        return armorSlot[type];
    }

    @Override
    public boolean isEnchantable(ItemStack stack1, ItemStack stack2) {
        return stack2 == null ? false : stack2.getItem() == ItemList.scroll && (stack2.getItemDamage() == 4 || stack2.getItemDamage() == 3);
    }

    @Override
    public boolean isRepairable(ItemStack stack1, ItemStack stack2) {
        return stack2 == null ? false : stack2.itemID == this.tier.getRepairID() || stack2.itemID == this.id;
    }

    @Override
    public int repairCost() {
        return 500 * this.tier.getTier();
    }

    @Override
    protected int defaultColor() {
        return this.tier == ArmorMaterial.CLOTH ? 10511680 : super.defaultColor();
    }

    @Override
    public boolean isDyable() {
        return this.tier == ArmorMaterial.CLOTH;
    }

    static int[] getDurability() {
        return durValues;
    }
}
