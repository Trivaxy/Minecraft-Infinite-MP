package net.minecraft.game.item.misc;

import java.util.List;
import net.minecraft.game.container.player.PlayerContainer;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class Cape extends Item {
    private static final String[] items = new String[]{
        "white",
        "lightGray",
        "darkGray",
        "rose",
        "magenta",
        "purple",
        "violet",
        "ultramarine",
        "capri",
        "cyan",
        "springGreen",
        "green",
        "chartreuse",
        "yellow",
        "orange",
        "red",
        "black",
        "brown"
    };
    private Icon[] icons = new Icon[18];

    public Cape(int itemid, boolean hasStates) {
        super(itemid);
        this.setHasSubtypes(hasStates);
        if (itemid == 333) {
            this.maxDamage = 1024;
            this.outputCount = 5;
            this.outputMeta = 4;
            this.info.add("item.desc.seraph");
            this.info.add(this.maxDamage + " item.desc.durability");
        } else {
            this.maxDamage = 18;
        }

        this.unbreakable = true;
        this.maxStackSize = 1;
    }

    @Override
    public final ItemStack use(ItemStack stack, World world, Player player) {
        ItemStack charm = stack.copy();
        if (player.inventory.canUseItem()) {
            player.inventory.offhandSlot[0] = player.inventory.accessorySlots[2];
        } else {
            player.inventory.mainInventory[player.inventory.currentSlot] = player.inventory.accessorySlots[2];
        }

        for (int i = 0; i < ((PlayerContainer)player.openContainer).backpackContents.getSize(); i++) {
            ((PlayerContainer)player.openContainer).backpackContents.setSlotContents(i, null);
        }

        for (int i = 0; i < ((PlayerContainer)player.openContainer).quiverContents.getSize(); i++) {
            ((PlayerContainer)player.openContainer).quiverContents.setSlotContents(i, null);
        }

        player.inventory.accessorySlots[2] = charm;
        world.playEntitySound(player, "random.bundle", 1.0F, rand.nextFloat() * 0.4F + 0.8F);
        return stack;
    }

    @Override
    public int getMaxDamage() {
        return this.maxDamage;
    }

    @Override
    public boolean hasSubtypes() {
        return this.id == ItemList.cape.id;
    }

    @Override
    public boolean isRepairable(ItemStack item, ItemStack repair) {
        return this.id != ItemList.elytra.id ? false : repair != null && (repair.itemID == ItemList.extract.id || repair.itemID == this.id);
    }

    @Override
    public int repairCost() {
        return 3000;
    }

    @Override
    public String getNameFromMeta(int damage) {
        return this.hasSubtypes ? "item." + items[damage] + "Cape" : super.getNameFromMeta(damage);
    }

    @Override
    public void registerIcons(IconRegister ir) {
        if (this.hasSubtypes) {
            for (int i = 0; i < this.getMaxDamage(); i++) {
                this.icons[i] = ir.registerIcon("capes/" + items[i] + "Cape");
            }
        } else {
            this.itemIcon = ir.registerIcon("capes/" + this.internalName);
        }
    }

    @Override
    public Icon getIconFromMeta(int meta) {
        return this.hasSubtypes ? this.icons[meta] : this.itemIcon;
    }

    @Override
    public void getSubItems(int id, CreativeTabs ct, List<ItemStack> l) {
        int max = 1;
        if (this.id == ItemList.cape.id) {
            max = this.getMaxDamage();
        }

        for (int i = 0; i < max; i++) {
            l.add(new ItemStack(id, 1, i));
        }
    }
}
