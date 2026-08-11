package net.minecraft.game.item.misc;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.boat.Boat;
import net.minecraft.game.entity.boat.LavaBoat;
import net.minecraft.game.entity.boat.Raft;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.physics.TargetingSystem;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;
import net.minecraft.util.math.MathHelper;

public class BoatItem extends Item {
    public BoatItem(int id) {
        super(id);
        this.maxStackSize = 1;
        this.priorityItem = true;
    }

    @Override
    public ItemStack use(ItemStack stack, World world, Player player) {
        float offset = 1.0F;
        float pitch = player.prevPitch + (player.pitch - player.prevPitch) * offset;
        float yaw = player.prevYaw + (player.yaw - player.prevYaw) * offset;
        double x = player.prevX + (player.posX - player.prevX) * offset;
        double y = player.prevY + (player.posY - player.prevY) * offset + 1.62 - player.yOffset;
        double z = player.prevZ + (player.posZ - player.prevZ) * offset;
        Vector position = Vector.createVector(x, y, z);
        float yaw1 = MathHelper.cos(-yaw * (float) (Math.PI / 180.0) - (float) Math.PI);
        float yaw2 = MathHelper.sin(-yaw * (float) (Math.PI / 180.0) - (float) Math.PI);
        float pitch1 = -MathHelper.cos(-pitch * (float) (Math.PI / 180.0));
        float pitch2 = MathHelper.sin(-pitch * (float) (Math.PI / 180.0));
        float angle1 = yaw2 * pitch1;
        float angle2 = yaw1 * pitch1;
        double scale = 5.0;
        Vector lookVector = position.addVector(angle1 * scale, pitch2 * scale, angle2 * scale);
        TargetingSystem target = world.rayTrace(position, lookVector, true);
        if (target == null) {
            return stack;
        }

        if (target.target == TargetingSystem.HitType.TILE) {
            int hitX = target.blockX;
            int hitY = target.blockY;
            int hitZ = target.blockZ;
            if (!world.isOnline) {
                if (world.getBlockId(hitX, hitY, hitZ) == BlockList.snowLayer.id) {
                    hitY--;
                }

                if (this.id == ItemList.raft.id) {
                    world.spawnEntity(new Raft(world, hitX + 0.5F, hitY + 1.0F, hitZ + 0.5F));
                } else if (this.id == ItemList.boatObsidian.id) {
                    world.spawnEntity(new LavaBoat(world, hitX + 0.5F, hitY + 1.0F, hitZ + 0.5F));
                } else {
                    world.spawnEntity(new Boat(world, hitX + 0.5F, hitY + 1.0F, hitZ + 0.5F));
                }
            }

            if (player.properties.gamemode != 1) {
                stack.stackSize--;
            }

            if (player.inventory.canUseItem()) {
                player.swingOffhandItem();
            } else {
                player.swingItem();
            }

            player.addStat(StatList.useStats[this.id], 1);
        }

        return stack;
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.itemIcon = ir.registerIcon("boats/" + this.internalName);
    }
}
