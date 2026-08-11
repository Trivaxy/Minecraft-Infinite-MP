package net.minecraft.game.block.decoration;

import java.util.Random;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.building.Slab;
import net.minecraft.game.block.building.Stairs;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.physics.TargetingSystem;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.world.World;

public class Torch extends Block {
    public Torch(int blockid) {
        super(blockid, MaterialList.circuits);
        this.setTickable(true);
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
        return Block.RenderType.TORCH;
    }

    @Override
    public boolean placeableAt(World world, int xCoord, int yCoord, int zCoord) {
        Block blockBelow = world.getBlock(xCoord, yCoord - 1, zCoord);
        int meta = world.getBlockMetadata(xCoord, yCoord - 1, zCoord);
        if (blockBelow != BlockList.fence
            && !(blockBelow instanceof Wall)
            && blockBelow != BlockList.table
            && (!(blockBelow instanceof Slab) || meta < 128)
            && (!(blockBelow instanceof Stairs) || (meta & 4) == 0)) {
            return world.isBlockFull(xCoord - 1, yCoord, zCoord)
                ? true
                : (
                    world.isBlockFull(xCoord + 1, yCoord, zCoord)
                        ? true
                        : (
                            world.isBlockFull(xCoord, yCoord, zCoord - 1)
                                ? true
                                : (world.isBlockFull(xCoord, yCoord, zCoord + 1) ? true : world.isBlockFull(xCoord, yCoord - 1, zCoord))
                        )
                );
        } else {
            return true;
        }
    }

    @Override
    public void onPlaced(World world, Player player, int xCoord, int yCoord, int zCoord, int side) {
        super.onPlaced(world, player, xCoord, yCoord, zCoord, side);
        int meta = world.getBlockMetadata(xCoord, yCoord, zCoord);
        if (side == 1 && world.isBlockFull(xCoord, yCoord - 1, zCoord)) {
            meta = 5;
        }

        if (side == 2 && world.isBlockFull(xCoord, yCoord, zCoord + 1)) {
            meta = 4;
        }

        if (side == 3 && world.isBlockFull(xCoord, yCoord, zCoord - 1)) {
            meta = 3;
        }

        if (side == 4 && world.isBlockFull(xCoord + 1, yCoord, zCoord)) {
            meta = 2;
        }

        if (side == 5 && world.isBlockFull(xCoord - 1, yCoord, zCoord)) {
            meta = 1;
        }

        world.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, meta);
    }

    @Override
    public void onTick(World world, int xCoord, int yCoord, int zCoord, Random random5) {
        super.onTick(world, xCoord, yCoord, zCoord, random5);
    }

    @Override
    public void onBlockAdded(World world, int xCoord, int yCoord, int zCoord) {
        if (world.isBlockFull(xCoord - 1, yCoord, zCoord)) {
            world.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 1);
        } else if (world.isBlockFull(xCoord + 1, yCoord, zCoord)) {
            world.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 2);
        } else if (world.isBlockFull(xCoord, yCoord, zCoord - 1)) {
            world.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 3);
        } else if (world.isBlockFull(xCoord, yCoord, zCoord + 1)) {
            world.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 4);
        } else if (world.isBlockFull(xCoord, yCoord - 1, zCoord)) {
            world.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 5);
        }

        this.dropTorch(world, xCoord, yCoord, zCoord);
    }

    @Override
    public final void onNeighborChange(World world, int xCoord, int yCoord, int zCoord, int blockID) {
        if (this.dropTorch(world, xCoord, yCoord, zCoord)) {
            int meta = world.getBlockMetadata(xCoord, yCoord, zCoord);
            boolean drop = false;
            if (!world.isBlockFull(xCoord - 1, yCoord, zCoord) && meta == 1) {
                if (world.isBlockFull(xCoord + 1, yCoord, zCoord)) {
                    world.setBlockMetadata(xCoord, yCoord, zCoord, 2);
                } else if (world.isBlockFull(xCoord, yCoord, zCoord - 1)) {
                    world.setBlockMetadata(xCoord, yCoord, zCoord, 3);
                } else if (world.isBlockFull(xCoord, yCoord, zCoord + 1)) {
                    world.setBlockMetadata(xCoord, yCoord, zCoord, 4);
                } else {
                    world.setBlockMetadata(xCoord, yCoord, zCoord, 0);
                }
            } else if (!world.isBlockFull(xCoord + 1, yCoord, zCoord) && meta == 2) {
                if (world.isBlockFull(xCoord - 1, yCoord, zCoord)) {
                    world.setBlockMetadata(xCoord, yCoord, zCoord, 1);
                } else if (world.isBlockFull(xCoord, yCoord, zCoord - 1)) {
                    world.setBlockMetadata(xCoord, yCoord, zCoord, 3);
                } else if (world.isBlockFull(xCoord, yCoord, zCoord + 1)) {
                    world.setBlockMetadata(xCoord, yCoord, zCoord, 4);
                } else {
                    world.setBlockMetadata(xCoord, yCoord, zCoord, 0);
                }
            } else if (!world.isBlockFull(xCoord, yCoord, zCoord - 1) && meta == 3) {
                if (world.isBlockFull(xCoord - 1, yCoord, zCoord)) {
                    world.setBlockMetadata(xCoord, yCoord, zCoord, 1);
                } else if (world.isBlockFull(xCoord + 1, yCoord, zCoord)) {
                    world.setBlockMetadata(xCoord, yCoord, zCoord, 2);
                } else if (world.isBlockFull(xCoord, yCoord, zCoord + 1)) {
                    world.setBlockMetadata(xCoord, yCoord, zCoord, 4);
                } else {
                    world.setBlockMetadata(xCoord, yCoord, zCoord, 0);
                }
            } else if (!world.isBlockFull(xCoord, yCoord, zCoord + 1) && meta == 4) {
                if (world.isBlockFull(xCoord - 1, yCoord, zCoord)) {
                    world.setBlockMetadata(xCoord, yCoord, zCoord, 1);
                } else if (world.isBlockFull(xCoord, yCoord, zCoord - 1)) {
                    world.setBlockMetadata(xCoord, yCoord, zCoord, 3);
                } else if (world.isBlockFull(xCoord + 1, yCoord, zCoord)) {
                    world.setBlockMetadata(xCoord, yCoord, zCoord, 2);
                } else {
                    world.setBlockMetadata(xCoord, yCoord, zCoord, 0);
                }
            }

            if (!world.isBlockFull(xCoord, yCoord - 1, zCoord) && meta == 5) {
                drop = true;
            }

            if (drop) {
                this.dropBlock(world, xCoord, yCoord, zCoord, world.getBlockMetadata(xCoord, yCoord, zCoord));
                world.setBlockWithNotify(xCoord, yCoord, zCoord, 0);
            }
        }
    }

    private boolean dropTorch(World world, int xCoord, int yCoord, int zCoord) {
        if (!this.placeableAt(world, xCoord, yCoord, zCoord)) {
            this.dropBlock(world, xCoord, yCoord, zCoord, world.getBlockMetadata(xCoord, yCoord, zCoord));
            world.setBlockWithNotify(xCoord, yCoord, zCoord, 0);
            world.playEffect(2001, xCoord, yCoord, zCoord, world.getBlockMetadata(xCoord, yCoord, zCoord) << 12 | this.id);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public TargetingSystem rayTrace(World world, int xCoord, int yCoord, int zCoord, Vector start, Vector end) {
        int meta = world.getBlockMetadata(xCoord, yCoord, zCoord);
        float offset = 0.15F;
        if (meta == 1) {
            this.setBounds(0.0, 0.2F, 0.5F - offset, offset * 2.0F, 0.8F, 0.5F + offset);
        } else if (meta == 2) {
            this.setBounds(1.0F - offset * 2.0F, 0.2F, 0.5F - offset, 1.0, 0.8F, 0.5F + offset);
        } else if (meta == 3) {
            this.setBounds(0.5F - offset, 0.2F, 0.0, 0.5F + offset, 0.8F, offset * 2.0F);
        } else if (meta == 4) {
            this.setBounds(0.5F - offset, 0.2F, 1.0F - offset * 2.0F, 0.5F + offset, 0.8F, 1.0);
        } else {
            offset = 0.1F;
            this.setBounds(0.5F - offset, 0.0, 0.5F - offset, 0.5F + offset, 0.6F, 0.5F + offset);
        }

        return super.rayTrace(world, xCoord, yCoord, zCoord, start, end);
    }

    @Override
    public void spawnParticles(World world, int xCoord, int yCoord, int zCoord, Random rand) {
        int meta = world.getBlockMetadata(xCoord, yCoord, zCoord);
        double xOffset = xCoord + 0.5;
        double yOffset = yCoord + 0.7;
        double zOffset = zCoord + 0.5;
        double maxHeight = 0.22;
        double angle = 0.27;
        if (meta == 1) {
            world.spawnParticle(ParticleType.SMOKE, xOffset - angle, yOffset + maxHeight, zOffset, 0.0, 0.0, 0.0);
            world.spawnParticle(ParticleType.FLAME, xOffset - angle, yOffset + maxHeight, zOffset, 0.0, 0.0, 0.0);
        } else if (meta == 2) {
            world.spawnParticle(ParticleType.SMOKE, xOffset + angle, yOffset + maxHeight, zOffset, 0.0, 0.0, 0.0);
            world.spawnParticle(ParticleType.FLAME, xOffset + angle, yOffset + maxHeight, zOffset, 0.0, 0.0, 0.0);
        } else if (meta == 3) {
            world.spawnParticle(ParticleType.SMOKE, xOffset, yOffset + maxHeight, zOffset - angle, 0.0, 0.0, 0.0);
            world.spawnParticle(ParticleType.FLAME, xOffset, yOffset + maxHeight, zOffset - angle, 0.0, 0.0, 0.0);
        } else if (meta == 4) {
            world.spawnParticle(ParticleType.SMOKE, xOffset, yOffset + maxHeight, zOffset + angle, 0.0, 0.0, 0.0);
            world.spawnParticle(ParticleType.FLAME, xOffset, yOffset + maxHeight, zOffset + angle, 0.0, 0.0, 0.0);
        } else {
            world.spawnParticle(ParticleType.SMOKE, xOffset, yOffset, zOffset, 0.0, 0.0, 0.0);
            world.spawnParticle(ParticleType.FLAME, xOffset, yOffset, zOffset, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public boolean particles3D() {
        return false;
    }
}
