package net.minecraft.game.block.plants;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.physics.TargetingSystem;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.util.math.Direction;

public class Vine extends Block {
    public Vine(int id) {
        super(id, MaterialList.circuits);
        this.setTickable(true);
        this.particleGravity = 0.6F;
        this.compostable = true;
    }

    @Override
    public BoundingBox getCollisionBox(World world, int x, int y, int z) {
        return null;
    }

    @Override
    public BoundingBox getOutline(World world, int x, int y, int z) {
        this.setStateBounds(world, x, y, z);
        return super.getOutline(world, x, y, z);
    }

    @Override
    public TargetingSystem rayTrace(World world, int x, int y, int z, Vector source, Vector target) {
        this.setStateBounds(world, x, y, z);
        return super.rayTrace(world, x, y, z, source, target);
    }

    @Override
    public void setStateBounds(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        float offset = 0.125F;
        if (meta == 2) {
            this.setBounds(0.0, 0.0, 1.0F - offset, 1.0, 1.0, 1.0);
        }

        if (meta == 3) {
            this.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, offset);
        }

        if (meta == 4) {
            this.setBounds(1.0F - offset, 0.0, 0.0, 1.0, 1.0, 1.0);
        }

        if (meta == 5) {
            this.setBounds(0.0, 0.0, 0.0, offset, 1.0, 1.0);
        }
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.VINE;
    }

    @Override
    public boolean placeableAt(World world, int x, int y, int z) {
        if (world.isBlockFull(x - 1, y, z) || world.getBlockId(x - 1, y, z) == BlockList.leaves.id) {
            return true;
        } else if (world.isBlockFull(x + 1, y, z) || world.getBlockId(x + 1, y, z) == BlockList.leaves.id) {
            return true;
        } else {
            return world.isBlockFull(x, y, z - 1) || world.getBlockId(x, y, z - 1) == BlockList.leaves.id
                ? true
                : world.isBlockFull(x, y, z + 1) || world.getBlockId(x, y, z + 1) == BlockList.leaves.id;
        }
    }

    @Override
    public void onPlaced(World world, Player player, int x, int y, int z, int side) {
        world.setBlockMetadataWithNotify(x, y, z, side);
    }

    @Override
    public void onNeighborChange(World world, int x, int y, int z, int side) {
        int meta = world.getBlockMetadata(x, y, z);
        boolean drop = false;
        if (meta == 2 && (world.isBlockFull(x, y, z + 1) || world.getBlock(x, y, z + 1) == BlockList.leaves)) {
            drop = true;
        }

        if (meta == 3 && (world.isBlockFull(x, y, z - 1) || world.getBlock(x, y, z - 1) == BlockList.leaves)) {
            drop = true;
        }

        if (meta == 4 && (world.isBlockFull(x + 1, y, z) || world.getBlock(x + 1, y, z) == BlockList.leaves)) {
            drop = true;
        }

        if (meta == 5 && (world.isBlockFull(x - 1, y, z) || world.getBlock(x - 1, y, z) == BlockList.leaves)) {
            drop = true;
        }

        if (!drop && !this.canVineStay(world, x, y, z) && !world.isOnline) {
            world.setBlockWithNotify(x, y, z, 0);
            this.dropBlock(world, x, y, z, 0);
        }
    }

    @Override
    public void onTick(World world, int x, int y, int z, Random random) {
        if (!world.isOnline && world.rand.nextInt(4) == 0) {
            int radius = 4;
            int vineLimit = 5;
            boolean tooManyNearby = false;

            label84:
            for (int cx = x - radius; cx <= x + radius; cx++) {
                for (int cz = z - radius; cz <= z + radius; cz++) {
                    for (int cy = y - 1; cy <= y + 1; cy++) {
                        if (world.getBlockId(cx, cy, cz) == this.id) {
                            if (--vineLimit <= 0) {
                                tooManyNearby = true;
                                break label84;
                            }
                        }
                    }
                }
            }

            int side = world.getBlockMetadata(x, y, z);
            int roll = random.nextInt(6);
            if (roll == 1 && y < 255 && world.isAir(x, y + 1, z)) {
                if (tooManyNearby) {
                    return;
                }

                if (this.canAttach(world, x, y + 1, z, side)) {
                    world.setBlockAndMetadataWithNotify(x, y + 1, z, this.id, side);
                }
            } else if (roll >= 2 && roll <= 5) {
                if (tooManyNearby) {
                    return;
                }

                int dx = 0;
                int dz = 0;
                switch (roll) {
                    case 2:
                        dz = 1;
                        break;
                    case 3:
                        dz = -1;
                        break;
                    case 4:
                        dx = 1;
                        break;
                    case 5:
                        dx = -1;
                }

                int nx = x + dx;
                int nz = z + dz;
                if (world.isAir(nx, y, nz)) {
                    if (this.canAttach(world, nx, y, nz, side)) {
                        world.setBlockAndMetadataWithNotify(nx, y, nz, this.id, side);
                    } else {
                        int opposite = this.getOppositeSide(roll);
                        if (this.canAttach(world, nx, y, nz, opposite)) {
                            world.setBlockAndMetadataWithNotify(nx, y, nz, this.id, opposite);
                        }
                    }
                }
            } else if (y > 1 && world.isAir(x, y - 1, z)) {
                world.setBlockAndMetadataWithNotify(x, y - 1, z, this.id, side);
            }
        }
    }

    private boolean canAttach(World world, int x, int y, int z, int side) {
        switch (side) {
            case 2:
                return this.isValidSupport(world, x, y, z + 1);
            case 3:
                return this.isValidSupport(world, x, y, z - 1);
            case 4:
                return this.isValidSupport(world, x + 1, y, z);
            case 5:
                return this.isValidSupport(world, x - 1, y, z);
            default:
                return false;
        }
    }

    private boolean isValidSupport(World world, int x, int y, int z) {
        return world.isBlockFull(x, y, z) || world.getBlock(x, y, z) == BlockList.leaves;
    }

    private int getOppositeSide(int side) {
        switch (side) {
            case 2:
                return 3;
            case 3:
                return 2;
            case 4:
                return 5;
            case 5:
                return 4;
            default:
                return side;
        }
    }

    @Override
    public int quantityDropped(Random rand) {
        return 1;
    }

    @Override
    public boolean particles3D() {
        return false;
    }

    @Override
    public int tickRate() {
        return 20;
    }

    @Override
    public boolean placeableAtSide(World world, int x, int y, int z, int side) {
        switch (side) {
            case 1:
                return this.canBePlacedOn(world.getBlockId(x, y + 1, z));
            case 2:
                return this.canBePlacedOn(world.getBlockId(x, y, z + 1));
            case 3:
                return this.canBePlacedOn(world.getBlockId(x, y, z - 1));
            case 4:
                return this.canBePlacedOn(world.getBlockId(x + 1, y, z));
            case 5:
                return this.canBePlacedOn(world.getBlockId(x - 1, y, z));
            default:
                return false;
        }
    }

    private boolean canBePlacedOn(int id) {
        return id == 0 ? false : BlockList.blocks[id].fullBlock();
    }

    private boolean canVineStay(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        int side = meta;
        if (meta > 0) {
            for (int face = 0; face <= 3; face++) {
                int calcFace = 1 << face;
                if ((meta & calcFace) != 0
                    && !this.canBePlacedOn(world.getBlockId(x + Direction.offsetX[face], y, z + Direction.offsetZ[face]))
                    && (world.getBlockId(x, y + 1, z) != this.id || (world.getBlockMetadata(x, y + 1, z) & calcFace) == 0)) {
                    side &= ~calcFace;
                }
            }
        }

        if (side == 0 && !this.canBePlacedOn(world.getBlockId(x, y + 1, z))) {
            return false;
        }

        if (side != meta) {
            world.setBlockMetadataWithNotify(x, y, z, side);
        }

        return true;
    }

    @Override
    public int dropID(int id, Random rand) {
        return 0;
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("plants/" + this.name);
    }
}
