package net.minecraft.game.item.staff;

import net.minecraft.game.ParticleType;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.plants.Crops;
import net.minecraft.game.block.plants.Flower;
import net.minecraft.game.block.plants.Stem;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;

public class NatureStaff extends Staff {
    public NatureStaff(int itemid) {
        super(itemid);
    }

    @Override
    public boolean useOnBlock(ItemStack stack, Player player, World world, int x, int y, int z, int side) {
        if (stack.isNotBroken() && !world.isOnline) {
            int id = world.getBlockId(x, y, z);
            int meta = world.getBlockMetadata(x, y, z);
            boolean condition = false;
            if (BlockList.blocks[id] instanceof Flower) {
                ((Flower)BlockList.blocks[id]).growTree(world, player, x, y, z, world.rand);
                condition = true;
            }

            if (BlockList.blocks[id] instanceof Stem || BlockList.blocks[id] instanceof Crops) {
                BlockList.blocks[id].fertilize(world, x, y, z, -1);
                condition = true;
            }

            if (id == BlockList.voidRock.id) {
                world.setBlockWithNotify(x, y, z, BlockList.voidGrass.id);
                condition = true;
            }

            if (id == BlockList.dirt.id) {
                world.setBlockAndMetadataWithNotify(x, y, z, BlockList.grass.id, meta == 6 ? 3 : meta);
                condition = true;
            }

            if (id == BlockList.grass.id) {
                this.growPlants(world, stack, player, x, y, z, id, meta);
                condition = true;
            } else if (id == BlockList.voidGrass.id) {
                this.growSpecificPlant(world, stack, player, x, y, z, id, rand.nextInt(2), BlockList.glowGrass.id);
                condition = true;
            } else if (id == BlockList.sand.id) {
                this.growSpecificPlant(world, stack, player, x, y, z, id, 0, BlockList.duneGrass.id);
                condition = true;
            } else if (id == BlockList.moss.id) {
                this.growSpecificPlant(world, stack, player, x, y, z, id, 2, BlockList.tallGrass.id);
                condition = true;
            }

            if (condition) {
                world.playSoundEffect(player, player.posX, player.posY, player.posZ, "random.breath", 1.0F, 1.0F / (world.rand.nextFloat() * 0.4F + 0.8F));
                stack.damageItem(1, player);
                player.addStat(StatList.useStats[this.id], 1);
                this.showParticles(world, x, y, z);
            }

            return condition;
        } else {
            return false;
        }
    }

    public void growSpecificPlant(World world, ItemStack stack, Player player, int x, int y, int z, int id, int meta, int grow) {
        label33:
        for (int i = 0; i < world.getWorldHeight(); i++) {
            int blockX = x;
            int blockY = y + 1;
            int blockZ = z;

            for (int sectionHeight = 0; sectionHeight < i / 16; sectionHeight++) {
                blockX += rand.nextInt(3) - 1;
                blockY += (rand.nextInt(3) - 1) * rand.nextInt(3) / 2;
                blockZ += rand.nextInt(3) - 1;
                if (world.getBlockId(blockX, blockY - 1, blockZ) != id || world.isBlockFull(blockX, blockY, blockZ)) {
                    continue label33;
                }
            }

            if (world.getBlockId(blockX, blockY, blockZ) == 0) {
                this.showParticles(world, blockX, blockY, blockZ);
                if (rand.nextInt(10) != 0 && world.getBlockMetadata(blockX, blockY - 1, blockZ) != 4) {
                    world.setBlockAndMetadataWithNotify(blockX, blockY, blockZ, grow, meta);
                }
            }
        }
    }

    public void growPlants(World world, ItemStack stack, Player player, int x, int y, int z, int id, int meta) {
        label102:
        for (int height = 0; height < world.getWorldHeight(); height++) {
            int blockX = x;
            int blockY = y + 1;
            int blockZ = z;

            for (int i = 0; i < height / 16; i++) {
                blockX += rand.nextInt(3) - 1;
                blockY += (rand.nextInt(3) - 1) * rand.nextInt(3) / 2;
                blockZ += rand.nextInt(3) - 1;
                if (world.getBlockId(blockX, blockY - 1, blockZ) != BlockList.grass.id || world.isBlockFull(blockX, blockY, blockZ)) {
                    continue label102;
                }
            }

            if (world.getBlockId(blockX, blockY, blockZ) == 0) {
                this.showParticles(world, blockX, blockY, blockZ);
                if (rand.nextInt(10) != 0 && world.getBlockMetadata(blockX, blockY - 1, blockZ) != 4) {
                    meta = world.getBlockMetadata(blockX, blockY - 1, blockZ);
                    if (meta >= 5) {
                        meta--;
                    }

                    if ((meta == 6 || meta == 7 || meta == 8) && rand.nextBoolean()) {
                        meta += 3;
                    }

                    world.setBlockAndMetadataWithNotify(blockX, blockY, blockZ, BlockList.tallGrass.id, meta);
                } else if (rand.nextInt(6) == 0) {
                    if (rand.nextInt(3) != 0 && world.getBlockMetadata(blockX, blockY - 1, blockZ) <= 3) {
                        world.setBlockAndMetadataWithNotify(blockX, blockY, blockZ, BlockList.sapling.id, rand.nextInt(2));
                    }
                } else {
                    int flower = 0;
                    if (world.getBlockMetadata(blockX, blockY - 1, blockZ) == 4) {
                        flower = BlockList.mushroomBrown.id;
                        switch (rand.nextInt(3)) {
                            case 1:
                                flower = BlockList.mushroomRed.id;
                                break;
                            case 2:
                                flower = BlockList.mushroomGlowing.id;
                        }
                    } else if (world.getBlockMetadata(blockX, blockY - 1, blockZ) > 6) {
                        flower = BlockList.pinkTulip.id;
                        switch (rand.nextInt(5)) {
                            case 1:
                                flower = BlockList.whiteTulip.id;
                                break;
                            case 2:
                                flower = BlockList.paeonia.id;
                                break;
                            case 3:
                                flower = BlockList.berryBush.id;
                                break;
                            case 4:
                                flower = BlockList.jumpPad.id;
                        }
                    } else if (world.getBlockMetadata(blockX, blockY - 1, blockZ) != 5 && world.getBlockMetadata(blockX, blockY - 1, blockZ) != 6) {
                        flower = BlockList.dandelion.id;
                        switch (rand.nextInt(6)) {
                            case 1:
                                flower = BlockList.rose.id;
                                break;
                            case 2:
                                flower = BlockList.petunia.id;
                                break;
                            case 3:
                                flower = BlockList.lilac.id;
                                break;
                            case 4:
                                flower = BlockList.tulip.id;
                                break;
                            case 5:
                                flower = BlockList.daisy.id;
                        }
                    } else {
                        flower = BlockList.fireFlower.id;
                        switch (rand.nextInt(4)) {
                            case 1:
                                flower = BlockList.witherRose.id;
                                break;
                            case 2:
                                flower = BlockList.mushroomCrimson.id;
                                break;
                            case 3:
                                flower = BlockList.mushroomNylium.id;
                        }
                    }

                    world.setBlockWithNotify(blockX, blockY, blockZ, flower);
                }
            }
        }
    }

    @Override
    public boolean isRepairable(ItemStack stack1, ItemStack stack2) {
        return stack2 == null ? false : stack2.itemID == ItemList.emerald.id || stack2.itemID == this.id;
    }

    @Override
    public int repairCost() {
        return 1000;
    }

    private void showParticles(World world, int posX, int posY, int posZ) {
        for (int i = 0; i < 7; i++) {
            world.spawnParticle(
                ParticleType.GROW,
                (double)posX + world.rand.nextFloat(),
                (double)posY + world.rand.nextFloat(),
                (double)posZ + world.rand.nextFloat(),
                world.rand.nextGaussian() * 0.02,
                world.rand.nextGaussian() * 0.02,
                world.rand.nextGaussian() * 0.02
            );
        }
    }
}
