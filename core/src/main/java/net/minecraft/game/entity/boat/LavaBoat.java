package net.minecraft.game.entity.boat;

import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;
import net.minecraft.statistics.achievement.AchievementList;
import net.minecraft.util.math.MathHelper;

public class LavaBoat extends Boat {
    public LavaBoat(World world) {
        super(world);
        this.mat = MaterialList.lava;
    }

    public LavaBoat(World world, double x, double y, double z) {
        super(world, x, y, z);
        this.mat = MaterialList.lava;
    }

    @Override
    public void dealFireDamage(int damage) {
    }

    @Override
    public void onTick() {
        super.onTick();
        this.fire = 0;
    }

    @Override
    protected void onDead() {
        this.dropItem(ItemList.boatObsidian.id, 1, 0.0F);
        this.setDead();
    }

    @Override
    public boolean interact(Player player) {
        if ((this.rider == null || !(this.rider instanceof Player) || this.rider == player) && !this.world.isOnline) {
            player.mountEntity(this);
            player.triggerAchievement(AchievementList.boat);
            if (player.mount == null) {
                for (int i = MathHelper.floor_double(player.posX - 3.0); i < MathHelper.floor_double(player.posX + 3.0); i++) {
                    for (int j = MathHelper.floor_double(player.posZ - 3.0); j < MathHelper.floor_double(player.posZ + 3.0); j++) {
                        for (int y = (int)player.posY - 5; y < (int)player.posY + 5; y++) {
                            if (this.world.getBlock(i, y, j) == null && this.world.isSolid(i, y - 1, j)) {
                                player.setPosition(i + 0.5F, y + 2, j + 0.5F);
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return true;
    }
}
