package net.minecraft.game.item.misc;

import net.minecraft.game.entity.player.Player;
import net.minecraft.game.entity.projectile.EnderEye;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.game.world.chunk.BlockPosition;
import net.minecraft.game.world.generation.structures.misc.Slipgate;
import net.minecraft.localization.Translator;
import net.minecraft.statistics.achievement.AchievementList;
import net.minecraft.util.math.MathHelper;

public class Eye extends Item {
    public Eye(int itemid) {
        super(itemid);
    }

    @Override
    public ItemStack use(ItemStack stack, World world, Player player) {
        if (!world.isOnline) {
            BlockPosition chunk = world.findClosestStructure(
                Slipgate.class, MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posY), MathHelper.floor_double(player.posZ)
            );
            if (chunk != null) {
                EnderEye eye = new EnderEye(world, player.posX, player.posY + 1.62 - player.yOffset, player.posZ);
                eye.setFacing(chunk.x, chunk.y, chunk.z);
                world.spawnEntity(eye);
                world.playEntitySound(player, "random.throw", 0.5F, 0.4F / (rand.nextFloat() * 0.4F + 0.8F));
                if (player.properties.gamemode != 1) {
                    stack.stackSize--;
                }

                player.swingItem();
                player.triggerAchievement(AchievementList.eye);
            } else {
                player.addChatMessage("§4" + Translator.getInstance().translateKey("stronghold.none"));
            }
        }

        return stack;
    }
}
