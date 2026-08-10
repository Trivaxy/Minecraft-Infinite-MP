package net.minecraft.game.block.misc;

import java.util.Random;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSource;
import net.minecraft.game.world.dimension.VoidDimension;

public class Fire extends Block {
    private int[] flammability = new int[4096];
    private int[] burnRate = new int[4096];
    private Icon[] iconArray;

    public Fire(int blockid) {
        super(blockid, MaterialList.fire);
        this.setTickable(true);
    }

    @Override
    public void afterInit() {
        this.setBurnRate(BlockList.planks.id, 5, 20);
        this.setBurnRate(BlockList.wood.id, 5, 5);
        this.setBurnRate(BlockList.log.id, 5, 5);
        this.setBurnRate(BlockList.cloth.id, 30, 60);
        this.setBurnRate(BlockList.carpet.id, 30, 60);
        this.setBurnRate(BlockList.slabWood.id, 5, 20);
        this.setBurnRate(BlockList.doubleSlabWood.id, 5, 20);
        this.setBurnRate(BlockList.stairWood.id, 5, 20);
        this.setBurnRate(BlockList.panelWood.id, 5, 20);
        this.setBurnRate(BlockList.fence.id, 5, 20);
        this.setBurnRate(BlockList.fenceGate.id, 5, 20);
        this.setBurnRate(BlockList.chair.id, 5, 20);
        this.setBurnRate(BlockList.table.id, 5, 20);
        this.setBurnRate(BlockList.bookshelf.id, 30, 20);
        this.setBurnRate(BlockList.composter.id, 30, 20);
        this.setBurnRate(BlockList.lectern.id, 30, 20);
        this.setBurnRate(BlockList.workbench.id, 30, 20);
        this.setBurnRate(BlockList.leaves.id, 30, 60);
        this.setBurnRate(BlockList.leafPile.id, 30, 60);
        this.setBurnRate(BlockList.vine.id, 15, 100);
        this.setBurnRate(BlockList.caveVine.id, 15, 60);
        this.setBurnRate(BlockList.glowVine.id, 15, 60);
        this.setBurnRate(BlockList.glowLychen.id, 15, 100);
        this.setBurnRate(BlockList.tallGrass.id, 30, 60);
        this.setBurnRate(BlockList.dandelion.id, 60, 100);
        this.setBurnRate(BlockList.rose.id, 60, 100);
        this.setBurnRate(BlockList.petunia.id, 60, 100);
        this.setBurnRate(BlockList.lilac.id, 60, 100);
        this.setBurnRate(BlockList.tulip.id, 60, 100);
        this.setBurnRate(BlockList.daisy.id, 60, 100);
        this.setBurnRate(BlockList.paeonia.id, 60, 100);
        this.setBurnRate(BlockList.whiteTulip.id, 60, 100);
        this.setBurnRate(BlockList.pinkTulip.id, 60, 100);
        this.setBurnRate(BlockList.berryBush.id, 60, 100);
        this.setBurnRate(BlockList.deadBush.id, 60, 100);
        this.setBurnRate(BlockList.sapling.id, 30, 60);
        this.setBurnRate(BlockList.bamboo.id, 60, 60);
        this.setBurnRate(BlockList.mushroomBrown.id, 60, 100);
        this.setBurnRate(BlockList.mushroomRed.id, 60, 100);
        this.setBurnRate(BlockList.mushroomGlowing.id, 60, 100);
        this.setBurnRate(BlockList.jumpPad.id, 60, 100);
        this.setBurnRate(BlockList.tnt.id, 15, 100);
        this.setBurnRate(BlockList.rope.id, 30, 60);
    }

    private void setBurnRate(int id, int ctef, int abtcf) {
        this.flammability[id] = ctef;
        this.burnRate[id] = abtcf;
    }

    @Override
    public BoundingBox getCollisionBox(World world, int xCoord, int yCoord, int zCoord) {
        return null;
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.FIRE;
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public int tickRate() {
        return 40;
    }

    @Override
    public void onTick(World world, int x, int y, int z, Random random) {
        int below = world.getBlockId(x, y - 1, z);
        boolean canStay = below == BlockList.netherrack.id
            || below == BlockList.blockCoal.id
            || below == BlockList.bedrock.id && world.currDim instanceof VoidDimension;
        if (!this.placeableAt(world, x, y, z)) {
            world.setBlockWithNotify(x, y, z, 0);
        }

        if (canStay
            || !world.isRaining()
            || !world.canLightningStrikeAt(x, y, z)
                && !world.canLightningStrikeAt(x - 1, y, z)
                && !world.canLightningStrikeAt(x + 1, y, z)
                && !world.canLightningStrikeAt(x, y, z - 1)
                && !world.canLightningStrikeAt(x, y, z + 1)) {
            int meta = world.getBlockMetadata(x, y, z);
            if (meta < 15) {
                world.setBlockMetadata(x, y, z, meta + random.nextInt(3) / 2);
            }

            world.forceTick(x, y, z, this.id);
            if (!canStay && !this.canSpread(world, x, y, z)) {
                if ((!world.isBlockFull(x, y - 1, z) || meta > 3) && BlockList.ash.placeableAt(world, x, y, z)) {
                    world.setBlockWithNotify(x, y, z, BlockList.ash.id);
                }
            } else if (!canStay && !this.canSpreadTo(world, x, y - 1, z) && meta == 15 && random.nextInt(4) == 0 && BlockList.ash.placeableAt(world, x, y, z)) {
                world.setBlockWithNotify(x, y, z, BlockList.ash.id);
            } else {
                this.trySpread(world, x + 1, y, z, 300, random, meta);
                this.trySpread(world, x - 1, y, z, 300, random, meta);
                this.trySpread(world, x, y - 1, z, 250, random, meta);
                this.trySpread(world, x, y + 1, z, 250, random, meta);
                this.trySpread(world, x, y, z - 1, 300, random, meta);
                this.trySpread(world, x, y, z + 1, 300, random, meta);

                for (int width = x - 1; width <= x + 1; width++) {
                    for (int length = z - 1; length <= z + 1; length++) {
                        for (int height = y - 1; height <= y + 4; height++) {
                            if (width != x || height != y || length != z) {
                                int rate = 100;
                                if (height > y + 1) {
                                    rate += (height - (y + 1)) * 100;
                                }

                                int burn = this.getSpreadChance(world, width, height, length);
                                if (burn > 0) {
                                    int chance = (burn + 40) / (meta + 30);
                                    if (chance > 0
                                        && random.nextInt(rate) <= chance
                                        && (!world.isRaining() || !world.canLightningStrikeAt(width, height, length))
                                        && !world.canLightningStrikeAt(width - 1, height, z)
                                        && !world.canLightningStrikeAt(width + 1, height, length)
                                        && !world.canLightningStrikeAt(width, height, length - 1)
                                        && !world.canLightningStrikeAt(width, height, length + 1)) {
                                        int state = meta + random.nextInt(5) / 4;
                                        if (state > 15) {
                                            state = 15;
                                        }

                                        world.setBlockAndMetadataWithNotify(width, height, length, this.id, state);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (BlockList.ash.placeableAt(world, x, y, z)) {
            world.setBlockWithNotify(x, y, z, BlockList.ash.id);
        }
    }

    private void trySpread(World world, int x, int y, int z, int chance, Random rand, int meta) {
        int rate = this.burnRate[world.getBlockId(x, y, z)];
        if (rand.nextInt(chance) < rate) {
            if (rand.nextInt(meta + 10) < 5 && !world.canLightningStrikeAt(x, y, z)) {
                int state = meta + rand.nextInt(5) / 4;
                if (state > 15) {
                    state = 15;
                }

                world.setBlockAndMetadataWithNotify(x, y, z, this.id, state);
            } else {
                this.burnBlock(world, x, y, z);
            }
        }
    }

    private void burnBlock(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        if (block == BlockList.log
            || block == BlockList.planks
            || block == BlockList.wood
            || block == BlockList.slabWood
            || block == BlockList.doubleSlabWood
            || block == BlockList.fence
            || block == BlockList.table) {
            if (world.getBlockMetadata(x, y, z) != 8) {
                world.setBlockMetadata(x, y, z, 8);
            } else {
                world.setBlockWithNotify(x, y, z, 0);
            }
        } else if (block == BlockList.stairWood || block == BlockList.fenceGate || block == BlockList.chair) {
            if ((world.getBlockMetadata(x, y, z) & 8) != 8) {
                world.setBlockMetadata(x, y, z, 64 | world.getBlockMetadata(x, y, z));
            } else {
                world.setBlockWithNotify(x, y, z, 0);
            }
        } else if (block == BlockList.panelWood) {
            if ((world.getBlockMetadata(x, y, z) & 4) != 8) {
                world.setBlockMetadata(x, y, z, 32 | world.getBlockMetadata(x, y, z));
            } else {
                world.setBlockWithNotify(x, y, z, 0);
            }
        } else if (block == BlockList.tnt) {
            BlockList.tnt.onExplode(world, x, y, z);
            world.setBlockWithNotify(x, y, z, 0);
        } else {
            world.setBlockWithNotify(x, y, z, 0);
        }
    }

    private boolean canSpread(World world, int xCoord, int yCoord, int zCoord) {
        return this.canSpreadTo(world, xCoord + 1, yCoord, zCoord)
            ? true
            : (
                this.canSpreadTo(world, xCoord - 1, yCoord, zCoord)
                    ? true
                    : (
                        this.canSpreadTo(world, xCoord, yCoord - 1, zCoord)
                            ? true
                            : (
                                this.canSpreadTo(world, xCoord, yCoord + 1, zCoord)
                                    ? true
                                    : (this.canSpreadTo(world, xCoord, yCoord, zCoord - 1) ? true : this.canSpreadTo(world, xCoord, yCoord, zCoord + 1))
                            )
                    )
            );
    }

    private int getSpreadChance(World world, int xCoord, int yCoord, int zCoord) {
        if (world.getBlockId(xCoord, yCoord, zCoord) != 0) {
            return 0;
        }

        int chance = this.spreadChance(world, xCoord + 1, yCoord, zCoord, 0);
        chance = this.spreadChance(world, xCoord - 1, yCoord, zCoord, chance);
        chance = this.spreadChance(world, xCoord, yCoord - 1, zCoord, chance);
        chance = this.spreadChance(world, xCoord, yCoord + 1, zCoord, chance);
        chance = this.spreadChance(world, xCoord, yCoord, zCoord - 1, chance);
        return this.spreadChance(world, xCoord, yCoord, zCoord + 1, chance);
    }

    @Override
    public boolean isCollidable() {
        return false;
    }

    public boolean canSpreadTo(WorldSource world, int xCoord, int yCoord, int zCoord) {
        return this.flammability[world.getBlockId(xCoord, yCoord, zCoord)] > 0;
    }

    public int spreadChance(World world, int xCoord, int yCoord, int zCoord, int chance) {
        int rate = this.flammability[world.getBlockId(xCoord, yCoord, zCoord)];
        return rate > chance ? rate : chance;
    }

    @Override
    public boolean placeableAt(World world, int xCoord, int yCoord, int zCoord) {
        return world.isBlockFull(xCoord, yCoord - 1, zCoord) || this.canSpread(world, xCoord, yCoord, zCoord);
    }

    @Override
    public void onNeighborChange(World world, int xCoord, int yCoord, int zCoord, int id) {
        if (!world.isBlockFull(xCoord, yCoord - 1, zCoord) && !this.canSpread(world, xCoord, yCoord, zCoord)) {
            world.setBlockWithNotify(xCoord, yCoord, zCoord, 0);
        }
    }

    @Override
    public void onBlockAdded(World world, int xCoord, int yCoord, int zCoord) {
        if (!world.isBlockFull(xCoord, yCoord - 1, zCoord) && !this.canSpread(world, xCoord, yCoord, zCoord)) {
            world.setBlockWithNotify(xCoord, yCoord, zCoord, 0);
        } else {
            world.forceTick(xCoord, yCoord, zCoord, this.id);
        }
    }

    @Override
    public void spawnParticles(World world, int xCoord, int yCoord, int zCoord, Random rand) {
        if (rand.nextInt(24) == 0) {
            world.playBlockSound(xCoord + 0.5F, yCoord + 0.5F, zCoord + 0.5F, "fire.fire", 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F);
        }

        if (!world.isBlockFull(xCoord, yCoord - 1, zCoord) && !BlockList.fire.canSpreadTo(world, xCoord, yCoord - 1, zCoord)) {
            if (BlockList.fire.canSpreadTo(world, xCoord - 1, yCoord, zCoord)) {
                for (int i = 0; i < 2; i++) {
                    double xOffset = xCoord + rand.nextDouble() * 0.1;
                    double yOffset = yCoord + rand.nextDouble();
                    double zOffset = zCoord + rand.nextDouble();
                    world.spawnParticle(ParticleType.LARGESMOKE, xOffset, yOffset, zOffset, 0.0, 0.0, 0.0);
                }
            }

            if (BlockList.fire.canSpreadTo(world, xCoord + 1, yCoord, zCoord)) {
                for (int i = 0; i < 2; i++) {
                    double xOffset = xCoord + 1 - rand.nextDouble() * 0.1;
                    double yOffset = yCoord + rand.nextDouble();
                    double zOffset = zCoord + rand.nextDouble();
                    world.spawnParticle(ParticleType.LARGESMOKE, xOffset, yOffset, zOffset, 0.0, 0.0, 0.0);
                }
            }

            if (BlockList.fire.canSpreadTo(world, xCoord, yCoord, zCoord - 1)) {
                for (int i = 0; i < 2; i++) {
                    double xOffset = xCoord + rand.nextDouble();
                    double yOffset = yCoord + rand.nextDouble();
                    double zOffset = zCoord + rand.nextDouble() * 0.1;
                    world.spawnParticle(ParticleType.LARGESMOKE, xOffset, yOffset, zOffset, 0.0, 0.0, 0.0);
                }
            }

            if (BlockList.fire.canSpreadTo(world, xCoord, yCoord, zCoord + 1)) {
                for (int i = 0; i < 2; i++) {
                    double xOffset = xCoord + rand.nextDouble();
                    double yOffset = yCoord + rand.nextDouble();
                    double zOffset = zCoord + 1 - rand.nextDouble() * 0.1;
                    world.spawnParticle(ParticleType.LARGESMOKE, xOffset, yOffset, zOffset, 0.0, 0.0, 0.0);
                }
            }

            if (BlockList.fire.canSpreadTo(world, xCoord, yCoord + 1, zCoord)) {
                for (int i = 0; i < 2; i++) {
                    double xOffset = xCoord + rand.nextDouble();
                    double yOffset = yCoord + 1 - rand.nextDouble() * 0.1;
                    double zOffset = zCoord + rand.nextDouble();
                    world.spawnParticle(ParticleType.LARGESMOKE, xOffset, yOffset, zOffset, 0.0, 0.0, 0.0);
                }
            }
        } else {
            for (int i = 0; i < 3; i++) {
                double xOffset = xCoord + rand.nextDouble();
                double yOffset = yCoord + rand.nextDouble() * 0.5 + 0.5;
                double zOffset = zCoord + rand.nextDouble();
                world.spawnParticle(ParticleType.LARGESMOKE, xOffset, yOffset, zOffset, 0.0, 0.0, 0.0);
            }
        }
    }

    public Icon getIcon(int meta) {
        return this.iconArray[meta];
    }

    @Override
    public void registerIcons(IconRegister r) {
        this.iconArray = new Icon[]{r.registerIcon("fire/fire_0"), r.registerIcon("fire/fire_1")};
    }

    @Override
    public Icon getIcon(int side, int meta) {
        return this.iconArray[0];
    }
}
