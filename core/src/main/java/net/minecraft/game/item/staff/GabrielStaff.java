package net.minecraft.game.item.staff;

import net.minecraft.game.entity.player.Player;
import net.minecraft.game.entity.projectile.Orb;
import net.minecraft.game.entity.projectile.Projectile;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;

public class GabrielStaff extends Staff {
    public static boolean canUseStaff = true;
    private Icon altIcon;

    public GabrielStaff(int itemid) {
        super(itemid);
    }

    @Override
    public ItemStack use(ItemStack stack, World world, Player player) {
        if (canUseStaff && stack.isNotBroken()) {
            Projectile orb = new Orb(world, player, -1);
            world.spawnEntity(orb);
            world.playSoundEffect(player.posX, player.posY, player.posZ, "random.breath", 1.0F, 1.0F / (world.rand.nextFloat() * 0.4F + 0.8F));
            if (player.inventory.canUseItem() && player.inventory.getOffHandItem().getItem() == stack.getItem()) {
                player.swingOffhandItem();
            } else {
                player.swingItem();
            }

            stack.damageItem(1, player);
            player.addStat(StatList.useStats[this.id], 1);
            canUseStaff = false;
        }

        return stack;
    }

    @Override
    public void registerIcons(IconRegister ir) {
        super.registerIcons(ir);
        this.altIcon = ir.registerIcon("staffs/gabrielWandEmpty");
    }

    @Override
    public Icon getIconFromMeta(int meta) {
        return meta == -1 ? this.altIcon : this.itemIcon;
    }
}
