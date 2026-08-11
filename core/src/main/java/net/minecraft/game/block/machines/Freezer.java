package net.minecraft.game.block.machines;

import java.util.Random;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.statistics.achievement.AchievementList;

public class Freezer extends Block {
    private Icon top;
    private Icon bottom;
    private Icon powerID;

    public Freezer(int blockID, Material material) {
        super(blockID, material);
        this.setTickable(true);
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("machines/" + this.name);
        this.top = ir.registerIcon("machines/" + this.name + "Top");
        this.bottom = ir.registerIcon("machines/" + this.name + "Bottom");
        this.powerID = ir.registerIcon("machines/" + this.name + "Active");
    }

    @Override
    public Icon getIcon(int side, int meta) {
        if (side == 1) {
            return this.top;
        } else if (side == 0) {
            return this.bottom;
        } else {
            return meta == 1 ? this.powerID : this.blockIcon;
        }
    }

    @Override
    public final void onTick(World world, int xCoord, int yCoord, int zCoord, Random random) {
        if (!world.isOnline && world.getBlockMetadata(xCoord, yCoord, zCoord) == 1) {
            Player player = world.getClosestPlayer(xCoord, yCoord, zCoord, 16.0);

            for (int x = xCoord - 2; x <= xCoord + 2; x++) {
                for (int y = yCoord - 2; y <= yCoord + 2; y++) {
                    for (int z = zCoord - 2; z <= zCoord + 2; z++) {
                        if (world.getBlock(x, y, z) == BlockList.waterStill || world.getBlock(x, y, z) == BlockList.waterMoving) {
                            this.freeze(world, player, x, y, z, BlockList.ice.id, 0);
                        } else if (world.getBlock(x, y, z) == BlockList.ice) {
                            this.freeze(world, player, x, y, z, BlockList.packedIce.id, 0);
                        } else if (world.getBlock(x, y, z) == BlockList.lavaStill || world.getBlock(x, y, z) == BlockList.lavaMoving) {
                            this.freeze(world, player, x, y, z, BlockList.obsidian.id, 0);
                        } else if (world.getBlock(x, y, z) == BlockList.quickSoil) {
                            this.freeze(world, player, x, y, z, BlockList.glassTinted.id, 0);
                        } else if (world.getBlock(x, y, z) == BlockList.oreAdamantine) {
                            this.freeze(world, player, x, y, z, BlockList.blockAdamantine.id, 0);
                            if (player != null) {
                                player.triggerAchievement(AchievementList.adamantine);
                            }
                        } else if (world.getBlock(x, y, z) == BlockList.stone && world.getBlockMetadata(x, y, z) == 0) {
                            this.freeze(world, player, x, y, z, BlockList.stone.id, 6);
                        } else if (world.getBlock(x, y, z) == BlockList.cobblestone && world.getBlockMetadata(x, y, z) == 0) {
                            this.freeze(world, player, x, y, z, BlockList.cobblestone.id, 6);
                        } else if (world.getBlock(x, y, z) == BlockList.stoneBrick && world.getBlockMetadata(x, y, z) == 0) {
                            this.freeze(world, player, x, y, z, BlockList.stoneBrick.id, 8);
                        }
                    }
                }
            }
        }
    }

    private void freeze(World world, Player player, int x, int y, int z, int id, int meta) {
        world.setBlockAndMetadataWithNotify(x, y, z, id, meta);
        world.spawnParticle(
            ParticleType.EXPLODE, x, y + 1, z, world.rand.nextGaussian() * 0.02, world.rand.nextGaussian() * 0.02, world.rand.nextGaussian() * 0.02
        );
        if (player != null) {
            player.triggerAchievement(AchievementList.freezer);
        }
    }

    @Override
    public void onPlaced(World world, Player player, int x, int y, int z, int side) {
        super.onPlaced(world, player, x, y, z, side);
        if (!world.isOnline) {
            this.updateState(world, x, y, z);
        }
    }

    @Override
    public void onNeighborChange(World world, int x, int y, int z, int id) {
        if (!world.isOnline) {
            this.updateState(world, x, y, z);
        }
    }

    private void updateState(World world, int x, int y, int z) {
        if (world.hasNeighborSignal(x, y, z)) {
            world.setBlockMetadata(x, y, z, 1);
        } else if (!world.hasNeighborSignal(x, y, z)) {
            world.setBlockMetadata(x, y, z, 0);
        }
    }

    @Override
    public int tickRate() {
        return 20;
    }
}
