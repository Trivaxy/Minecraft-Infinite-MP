package net.minecraft.game.block.misc;

import java.util.Random;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.World;
import net.minecraft.game.world.util.Position;
import net.minecraft.localization.Translator;
import net.minecraft.statistics.achievement.AchievementList;

public class RespawnAnchor extends Block {
    public RespawnAnchor(int blockid, Material material) {
        super(blockid, material);
    }

    @Override
    public boolean blockActivated(World world, int x, int y, int z, Player player) {
        if (player.dimension == 1) {
            player.setSpawn(new Position(x, y, z), 1, false);
            player.triggerAchievement(AchievementList.obsidian);
            player.addChatMessage(Translator.getInstance().translateKey("anchor.set"));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onBlockRemoval(World world, int xCoord, int yCoord, int zCoord) {
        Player player = world.getClosestPlayer(xCoord, yCoord, zCoord, 16.0);
        if (player != null && player.getSpawn(1) != null && player.getSpawn(1).x == xCoord && player.getSpawn(1).y == yCoord && player.getSpawn(1).z == zCoord) {
            player.setSpawn(null, 1, false);
        }
    }

    @Override
    public void spawnParticles(World world, int x, int y, int z, Random random) {
        double offset = 0.05;

        for (int i = 0; i < 5; i++) {
            double offsetX = x + random.nextDouble();
            double offsetY = y + random.nextDouble();
            double offsetZ = z + random.nextDouble();
            switch (i) {
                case 0:
                    offsetY = y - offset;
                    break;
                case 1:
                    offsetZ = z - offset;
                    break;
                case 2:
                    offsetZ = z + 1 + offset;
                    break;
                case 3:
                    offsetX = x - offset;
                    break;
                case 4:
                    offsetX = x + 1 + offset;
            }

            if (random.nextInt(10) == 0) {
                world.spawnParticle(ParticleType.DRIPWATER, offsetX, offsetY, offsetZ, 0.0, 0.0, 0.0);
            }
        }
    }
}
