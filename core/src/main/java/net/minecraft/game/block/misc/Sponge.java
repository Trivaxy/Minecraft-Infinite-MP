package net.minecraft.game.block.misc;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;
import net.minecraft.statistics.achievement.AchievementList;

public class Sponge extends Block {
    int range;
    Material absorptionMaterial;

    public Sponge(int id, int rng, Material mat) {
        super(id, MaterialList.sponge);
        this.range = rng;
        this.absorptionMaterial = mat;
    }

    @Override
    public void onBlockAdded(World world, int xCoord, int yCoord, int zCoord) {
        for (int x = xCoord - this.range; x <= xCoord + this.range; x++) {
            for (int y = yCoord - this.range; y <= yCoord + this.range; y++) {
                for (int z = zCoord - this.range; z <= zCoord + this.range; z++) {
                    if (world.getBlockMaterial(x, y, z) == this.absorptionMaterial) {
                        world.setBlock(x, y, z, 0);
                    }
                }
            }
        }
    }

    @Override
    public void onPlaced(World world, Player player, int x, int y, int z, int side) {
        player.addStat(StatList.useStats[this.id], 1);
        player.addStat(StatList.blocksPlacedStat, 1);
        player.triggerAchievement(AchievementList.spong);
    }

    @Override
    public void onBlockRemoval(World world, int xCoord, int yCoord, int zCoord) {
        for (int x = xCoord - this.range - 1; x <= xCoord + this.range + 1; x++) {
            for (int y = yCoord - this.range - 1; y <= yCoord + this.range + 1; y++) {
                for (int z = zCoord - this.range - 1; z <= zCoord + this.range + 1; z++) {
                    if (world.getBlockMaterial(x, y, z) == this.absorptionMaterial) {
                        world.updateNeighbors(x, y, z, world.getBlockId(x, y, z));
                    }
                }
            }
        }
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("sponges/" + this.name);
    }
}
