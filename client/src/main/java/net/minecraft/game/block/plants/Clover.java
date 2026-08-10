package net.minecraft.game.block.plants;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.World;
import net.minecraft.statistics.achievement.AchievementList;

public class Clover extends Flower {
    public Clover(int blockid, Material material) {
        super(blockid);
        this.material = material;
        this.setBounds(0.0, 0.0, 0.0, 1.0, 0.1F, 1.0);
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.LILYPAD;
    }

    @Override
    public void onEntityCollision(Entity entity, World world, int x, int y, int z) {
        entity.motionY += 2.5;
        entity.fallDistance = 0.0F;
        if (entity instanceof Player) {
            ((Player)entity).triggerAchievement(AchievementList.jumpPad);
        }
    }
}
