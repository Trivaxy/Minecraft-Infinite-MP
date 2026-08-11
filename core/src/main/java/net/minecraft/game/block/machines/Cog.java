package net.minecraft.game.block.machines;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.game.world.chunk.BlockPosition;

public class Cog extends Block {
    private boolean providePower = true;
    private Set<BlockPosition> blocksNeedingUpdate = new HashSet<>();
    private Icon poweredGear;
    private Icon poweredGearInvert;
    private Icon gear;

    public Cog(int blockid) {
        super(blockid, MaterialList.circuits);
    }

    @Override
    public BoundingBox getCollisionBox(World world, int xCoord, int yCoord, int zCoord) {
        return null;
    }

    @Override
    public int solidMaterial() {
        return 2;
    }

    @Override
    public void setStateBounds(World world, int x, int y, int z) {
        boolean north = this.canConnectTo(world, x, y, z - 1);
        boolean south = this.canConnectTo(world, x, y, z + 1);
        boolean west = this.canConnectTo(world, x - 1, y, z);
        boolean east = this.canConnectTo(world, x + 1, y, z);
        boolean down = this.canConnectTo(world, x, y - 1, z);
        boolean up = this.canConnectTo(world, x, y + 1, z);
        float minX = 0.0F;
        float maxX = 1.0F;
        float minY = 0.0F;
        float maxY = 1.0F;
        float minZ = 0.0F;
        float maxZ = 1.0F;
        float thickness = 0.125F;
        if (down && !north && !south && !west && !east && !up) {
            minY = 0.0F;
            maxY = thickness;
        }

        if (up && !north && !south && !west && !east && !down) {
            minY = 1.0F - thickness;
            maxY = 1.0F;
        }

        if (north && !south && !west && !east && !up && !down) {
            minZ = 0.0F;
            maxZ = thickness;
        }

        if (south && !north && !west && !east && !up && !down) {
            minZ = 1.0F - thickness;
            maxZ = 1.0F;
        }

        if (west && !east && !north && !south && !up && !down) {
            minX = 0.0F;
            maxX = thickness;
        }

        if (east && !west && !north && !south && !up && !down) {
            minX = 1.0F - thickness;
            maxX = 1.0F;
        }

        this.setBounds(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public boolean canConnectTo(World world, int x, int y, int z) {
        return world.isSolid(x, y, z);
    }

    @Override
    public boolean placeableAt(World world, int x, int y, int z) {
        if (world.isBlockFull(x - 1, y, z)) {
            return true;
        } else if (world.isBlockFull(x + 1, y, z)) {
            return true;
        } else if (world.isBlockFull(x, y - 1, z)) {
            return true;
        } else if (world.isBlockFull(x, y + 1, z)) {
            return true;
        } else {
            return world.isBlockFull(x, y, z - 1) ? true : world.isBlockFull(x, y, z + 1);
        }
    }

    @Override
    public final void onNeighborChange(World world, int xCoord, int yCoord, int zCoord, int blockID) {
        if (!world.isOnline) {
            if (this.placeableAt(world, xCoord, yCoord, zCoord)) {
                this.powerNeighbors(world, xCoord, yCoord, zCoord);
            } else {
                this.dropGear(world, xCoord, yCoord, zCoord);
            }
        }
    }

    private boolean dropGear(World world, int x, int y, int z) {
        if (!this.placeableAt(world, x, y, z)) {
            this.dropBlock(world, x, y, z, 0);
            world.setBlockWithNotify(x, y, z, 0);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.GEAR;
    }

    @Override
    public boolean particles3D() {
        return false;
    }

    @Override
    public Icon getIcon(int side, int mdata) {
        if (side == 0) {
            return this.poweredGear;
        } else {
            return side == 1 ? this.poweredGearInvert : this.gear;
        }
    }

    @Override
    public void registerIcons(IconRegister r) {
        this.poweredGear = r.registerIcon("machines/gear");
        this.poweredGearInvert = r.registerIcon("machines/gear_2");
        this.gear = r.registerIcon("machines/gear_3");
    }

    private void powerNeighbors(World world, int x, int y, int z) {
        this.calcSignalRange(world, x, y, z, x, y, z);
        ArrayList<BlockPosition> var5 = new ArrayList<>(this.blocksNeedingUpdate);
        this.blocksNeedingUpdate.clear();

        for (int var6 = 0; var6 < var5.size(); var6++) {
            BlockPosition var7 = var5.get(var6);
            world.updateNeighbors(var7.x, var7.y, var7.z, this.id);
        }
    }

    private void calcSignalRange(World world, int startX, int startY, int startZ, int endX, int endY, int endZ) {
        int startMeta = world.getBlockMetadata(startX, startY, startZ);
        int range = 0;
        this.providePower = false;
        boolean isPowered = world.hasNeighborSignal(startX, startY, startZ);
        this.providePower = true;
        if (isPowered) {
            range = 8;
        } else {
            for (int angle = 0; angle < 18; angle++) {
                int x = startX;
                int z = startY;
                int y = startZ;
                if (angle == 0) {
                    x = startX - 1;
                }

                if (angle == 1) {
                    x++;
                }

                if (angle == 2) {
                    y = startZ - 1;
                }

                if (angle == 3) {
                    y++;
                }

                if (angle == 4) {
                    y = startZ - 1;
                    x = startX - 1;
                }

                if (angle == 5) {
                    x++;
                    y = startZ - 1;
                }

                if (angle == 6) {
                    x = startX - 1;
                    y++;
                }

                if (angle == 7) {
                    y++;
                    x++;
                }

                if (angle == 8) {
                    z = startY - 1;
                }

                if (angle == 9) {
                    z++;
                }

                if (angle == 10) {
                    z++;
                    x = startX - 1;
                }

                if (angle == 11) {
                    z++;
                    x++;
                }

                if (angle == 12) {
                    z++;
                    y = startZ - 1;
                }

                if (angle == 13) {
                    z++;
                    y++;
                }

                if (angle == 14) {
                    z = startY - 1;
                    x = startX - 1;
                }

                if (angle == 15) {
                    z = startY - 1;
                    x++;
                }

                if (angle == 16) {
                    z = startY - 1;
                    y = startZ - 1;
                }

                if (angle == 17) {
                    z = startY - 1;
                    y++;
                }

                if (x != endX || z != endY || y != endZ) {
                    range = this.getMaxRange(world, x, z, y, range);
                }

                if (world.isBlockFull(x, z, y) && !world.isBlockFull(startX, startY + 1, startZ)) {
                    if (x != endX || z != endY || y != endZ) {
                        range = this.getMaxRange(world, x, z, y, range);
                    }
                } else if (!world.isBlockFull(x, startY, y) && (x != endX || z != endY || y != endZ)) {
                    range = this.getMaxRange(world, x, z, y, range);
                }
            }

            if (range > 0) {
                range--;
            } else {
                range = 0;
            }
        }

        if (startMeta != range) {
            world.setBlockMetadataWithNotify(startX, startY, startZ, range);

            for (int angle = 0; angle < 18; angle++) {
                int x = startX;
                int y = startZ;
                int z = startY;
                if (angle == 0) {
                    x = startX - 1;
                }

                if (angle == 1) {
                    x++;
                }

                if (angle == 2) {
                    y = startZ - 1;
                }

                if (angle == 3) {
                    y++;
                }

                if (angle == 4) {
                    y = startZ - 1;
                    x = startX - 1;
                }

                if (angle == 5) {
                    y = startZ - 1;
                    x++;
                }

                if (angle == 6) {
                    x = startX - 1;
                    y++;
                }

                if (angle == 7) {
                    y++;
                    x++;
                }

                if (angle == 8) {
                    z = startY - 1;
                }

                if (angle == 9) {
                    z++;
                }

                if (angle == 10) {
                    z++;
                    x = startX - 1;
                }

                if (angle == 11) {
                    z++;
                    x++;
                }

                if (angle == 12) {
                    z++;
                    y = startZ - 1;
                }

                if (angle == 13) {
                    z++;
                    y++;
                }

                if (angle == 14) {
                    z = startY - 1;
                    x = startX - 1;
                }

                if (angle == 15) {
                    z = startY - 1;
                    x++;
                }

                if (angle == 16) {
                    z = startY - 1;
                    y = startZ - 1;
                }

                if (angle == 17) {
                    z = startY - 1;
                    y++;
                }

                int maxRange = this.getMaxRange(world, x, z, y, -1);
                range = world.getBlockMetadata(startX, startY, startZ);
                if (range > 0) {
                    range--;
                }

                if (maxRange >= 0 && maxRange != range) {
                    this.calcSignalRange(world, x, z, y, startX, startY, startZ);
                }

                maxRange = this.getMaxRange(world, x, z, y, -1);
                range = world.getBlockMetadata(startX, startY, startZ);
                if (range > 0) {
                    range--;
                }

                if (maxRange >= 0 && maxRange != range) {
                    this.calcSignalRange(world, x, z, y, startX, startY, startZ);
                }
            }

            if (startMeta < range || range == 0) {
                this.blocksNeedingUpdate.add(new BlockPosition(startX, startY, startZ));
                this.blocksNeedingUpdate.add(new BlockPosition(startX - 1, startY, startZ));
                this.blocksNeedingUpdate.add(new BlockPosition(startX + 1, startY, startZ));
                this.blocksNeedingUpdate.add(new BlockPosition(startX, startY - 1, startZ));
                this.blocksNeedingUpdate.add(new BlockPosition(startX, startY + 1, startZ));
                this.blocksNeedingUpdate.add(new BlockPosition(startX, startY, startZ - 1));
                this.blocksNeedingUpdate.add(new BlockPosition(startX, startY, startZ + 1));
                this.blocksNeedingUpdate.add(new BlockPosition(startX - 1, startY, startZ - 1));
                this.blocksNeedingUpdate.add(new BlockPosition(startX + 1, startY, startZ + 1));
                this.blocksNeedingUpdate.add(new BlockPosition(startX + 1, startY, startZ - 1));
                this.blocksNeedingUpdate.add(new BlockPosition(startX - 1, startY, startZ + 1));
            }
        }
    }

    private void updateNeighbors(World world, int x, int y, int z) {
        if (world.getBlockId(x, y, z) == this.id) {
            world.updateNeighbors(x, y, z, this.id);
            world.updateNeighbors(x - 1, y, z, this.id);
            world.updateNeighbors(x + 1, y, z, this.id);
            world.updateNeighbors(x, y, z - 1, this.id);
            world.updateNeighbors(x, y, z + 1, this.id);
            world.updateNeighbors(x, y - 1, z, this.id);
            world.updateNeighbors(x, y + 1, z, this.id);
        }
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        if (!world.isOnline) {
            this.powerNeighbors(world, x, y, z);
            world.updateNeighbors(x, y + 1, z, this.id);
            world.updateNeighbors(x, y - 1, z, this.id);
            this.updateNeighbors(world, x - 1, y, z);
            this.updateNeighbors(world, x + 1, y, z);
            this.updateNeighbors(world, x, y, z - 1);
            this.updateNeighbors(world, x, y, z + 1);
            if (world.isBlockFull(x - 1, y, z)) {
                this.updateNeighbors(world, x - 1, y + 1, z);
            } else {
                this.updateNeighbors(world, x - 1, y - 1, z);
            }

            if (world.isBlockFull(x + 1, y, z)) {
                this.updateNeighbors(world, x + 1, y + 1, z);
            } else {
                this.updateNeighbors(world, x + 1, y - 1, z);
            }

            if (world.isBlockFull(x, y, z - 1)) {
                this.updateNeighbors(world, x, y + 1, z - 1);
            } else {
                this.updateNeighbors(world, x, y - 1, z - 1);
            }

            if (world.isBlockFull(x, y, z + 1)) {
                this.updateNeighbors(world, x, y + 1, z + 1);
            } else {
                this.updateNeighbors(world, x, y - 1, z + 1);
            }
        }
    }

    @Override
    public void onBlockRemoval(World world, int x, int y, int z) {
        if (!world.isOnline) {
            world.updateNeighbors(x, y + 1, z, this.id);
            world.updateNeighbors(x, y - 1, z, this.id);
            world.updateNeighbors(x + 1, y, z, this.id);
            world.updateNeighbors(x - 1, y, z, this.id);
            world.updateNeighbors(x, y, z + 1, this.id);
            world.updateNeighbors(x, y, z - 1, this.id);
            this.powerNeighbors(world, x, y, z);
            this.updateNeighbors(world, x - 1, y, z);
            this.updateNeighbors(world, x + 1, y, z);
            this.updateNeighbors(world, x, y, z - 1);
            this.updateNeighbors(world, x, y, z + 1);
            if (world.isBlockFull(x - 1, y, z)) {
                this.updateNeighbors(world, x - 1, y + 1, z);
            } else {
                this.updateNeighbors(world, x - 1, y - 1, z);
            }

            if (world.isBlockFull(x + 1, y, z)) {
                this.updateNeighbors(world, x + 1, y + 1, z);
            } else {
                this.updateNeighbors(world, x + 1, y - 1, z);
            }

            if (world.isBlockFull(x, y, z - 1)) {
                this.updateNeighbors(world, x, y + 1, z - 1);
            } else {
                this.updateNeighbors(world, x, y - 1, z - 1);
            }

            if (world.isBlockFull(x, y, z + 1)) {
                this.updateNeighbors(world, x, y + 1, z + 1);
            } else {
                this.updateNeighbors(world, x, y - 1, z + 1);
            }
        }
    }

    private int getMaxRange(World world, int x, int y, int z, int range) {
        if (world.getBlockId(x, y, z) != this.id) {
            return range;
        }

        int metaState = world.getBlockMetadata(x, y, z);
        return metaState > range ? metaState : range;
    }

    @Override
    public boolean powersDirectly(World world, int x, int y, int z, int meta) {
        return !this.providePower ? false : this.powersIndirectly(world, x, y, z, meta);
    }

    @Override
    public boolean powersIndirectly(World world, int x, int y, int z, int side) {
        if (!this.providePower) {
            return false;
        }

        if (world.getBlockMetadata(x, y, z) == 0) {
            return false;
        }

        if (world.getBlockMetadata(x, y, z) <= 8) {
            return true;
        }

        if (side == 1) {
            return true;
        }

        boolean powersLeft = canPower(world, x - 1, y, z, 1) || !world.isBlockFull(x - 1, y, z) && canPower(world, x - 1, y - 1, z, -1);
        boolean powersRight = canPower(world, x + 1, y, z, 3) || !world.isBlockFull(x + 1, y, z) && canPower(world, x + 1, y - 1, z, -1);
        boolean powersFront = canPower(world, x, y, z - 1, 2) || !world.isBlockFull(x, y, z - 1) && canPower(world, x, y - 1, z - 1, -1);
        boolean powersBack = canPower(world, x, y, z + 1, 0) || !world.isBlockFull(x, y, z + 1) && canPower(world, x, y - 1, z + 1, -1);
        if (!world.isBlockFull(x, y + 1, z)) {
            if (world.isBlockFull(x - 1, y, z) && canPower(world, x - 1, y + 1, z, -1)) {
                powersLeft = true;
            }

            if (world.isBlockFull(x + 1, y, z) && canPower(world, x + 1, y + 1, z, -1)) {
                powersRight = true;
            }

            if (world.isBlockFull(x, y, z - 1) && canPower(world, x, y + 1, z - 1, -1)) {
                powersFront = true;
            }

            if (world.isBlockFull(x, y, z + 1) && canPower(world, x, y + 1, z + 1, -1)) {
                powersBack = true;
            }
        }

        return !powersFront && !powersRight && !powersLeft && !powersBack && side >= 2 && side <= 5
            ? true
            : (
                side == 2 && powersFront && !powersLeft && !powersRight
                    ? true
                    : (
                        side == 3 && powersBack && !powersLeft && !powersRight
                            ? true
                            : (side == 4 && powersLeft && !powersFront && !powersBack ? true : side == 5 && powersRight && !powersFront && !powersBack)
                    )
            );
    }

    @Override
    public boolean providesPower() {
        return this.providePower;
    }

    public static boolean canPower(World world, int x, int y, int z, int state) {
        return world.getBlockId(x, y, z) == BlockList.cog.id ? state == world.getBlockMetadata(x, y, z) : false;
    }

    @Override
    public void spawnParticles(World world, int x, int y, int z, Random random) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta > 0) {
            double offsetX = x + this.minX + (random.nextFloat() - this.maxX) * 0.2;
            double offsetY = y + 0.0625;
            double offsetZ = z + this.minZ + (random.nextFloat() - this.maxZ) * 0.2;
            double dir = meta / 15.0;
            double facingX = dir * 0.6 + 0.4;
            if (meta == 0) {
                facingX = 0.0;
            }

            double facingY = dir * dir * 0.7 - 0.5;
            double facingZ = dir * dir * 0.6 - 0.7;
            if (facingY < 0.0) {
                facingY = 0.0;
            }

            if (facingZ < 0.0) {
                facingZ = 0.0;
            }

            world.spawnParticle(ParticleType.SMOKE, offsetX, offsetY, offsetZ, facingX, facingY, facingZ);
        }
    }
}
