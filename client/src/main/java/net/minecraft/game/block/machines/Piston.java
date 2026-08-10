package net.minecraft.game.block.machines;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.block.stepsound.StepSoundsList;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.block.tileentity.other.PistonEntity;
import net.minecraft.game.block.utility.ContainerBlock;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class Piston extends Block {
    public boolean sticky;
    private boolean extended;
    private Icon innerTop;
    private Icon bottom;
    private Icon top;

    public Piston(int id, boolean sticky, boolean edit) {
        super(id, MaterialList.pulley);
        this.sticky = sticky;
        this.extended = edit;
        this.setStepSound(StepSoundsList.metalSound);
        this.setHardness(2.0F);
        this.silkTouch = false;
    }

    public Icon extensionTexture() {
        return this.top;
    }

    public Icon sideTexture() {
        return this.sticky ? registerIcon("pistonGoldenSide") : registerIcon("pistonSide");
    }

    @Override
    public Icon getIcon(int side, int meta) {
        int facing = getOrientation(meta);
        return facing > 5
            ? this.top
            : (
                side == facing
                    ? (
                        !isExtended(meta)
                                && this.minX <= 0.0
                                && this.minY <= 0.0
                                && this.minZ <= 0.0
                                && this.maxX >= 1.0
                                && this.maxY >= 1.0
                                && this.maxZ >= 1.0
                            ? this.top
                            : this.innerTop
                    )
                    : (side == Direction.oppositeSide[facing] ? this.bottom : this.blockIcon)
            );
    }

    public static Icon registerIcon(String name) {
        if (name.equals("pistonSide")) {
            return BlockList.pulleyBase.blockIcon;
        } else if (name.equals("pistonGoldenSide")) {
            return BlockList.pulleyStickyBase.blockIcon;
        } else if (name.equals("blockIron_top")) {
            return ((Piston)BlockList.pulleyBase).top;
        } else if (name.equals("blockGold_top")) {
            return ((Piston)BlockList.pulleyStickyBase).top;
        } else if (name.equals("pistonTop")) {
            return ((Piston)BlockList.pulleyBase).innerTop;
        } else {
            return name.equals("pistonGoldenTop") ? ((Piston)BlockList.pulleyStickyBase).innerTop : null;
        }
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon(this.sticky ? "machines/pistonGoldenSide" : "machines/pistonSide");
        this.top = ir.registerIcon(this.sticky ? "machines/blockGold_top" : "machines/blockIron_top");
        this.innerTop = ir.registerIcon(this.sticky ? "machines/pistonGoldenTop" : "machines/pistonTop");
        this.bottom = ir.registerIcon(this.sticky ? "machines/pistonGoldenBottom" : "machines/pistonBottom");
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.PISTON_BASE;
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public int dropID(int id, Random random) {
        if (this.id == BlockList.pulleyBaseActive.id) {
            return BlockList.pulleyBase.id;
        } else {
            return this.id == BlockList.pulleyStickyBaseActive.id ? BlockList.pulleyStickyBase.id : this.id;
        }
    }

    @Override
    public boolean blockActivated(World world, int x, int y, int z, Player player) {
        int meta = world.getBlockMetadata(x, y, z);
        int facing = getOrientation(meta);
        boolean pwr = this.isIndirectlyPowered(world, x, y, z, facing);
        if (!pwr && !world.isOnline) {
            if (meta != 7) {
                if (!isExtended(meta)) {
                    if (canExtend(world, x, y, z, facing)) {
                        int id = BlockList.pulleyBase.id;
                        if (this.id == id) {
                            id = BlockList.pulleyBaseActive.id;
                        } else if (this.id == BlockList.pulleyBaseActive.id) {
                            id = BlockList.pulleyBase.id;
                        } else {
                            id = BlockList.pulleyStickyBaseActive.id;
                        }

                        world.setBlockAndMetadata(x, y, z, id, facing | 8);
                        world.blockEvent(x, y, z, 0, facing);
                    }
                } else if (isExtended(meta)) {
                    int id = BlockList.pulleyBaseActive.id;
                    if (this.id == id) {
                        id = BlockList.pulleyBase.id;
                    } else if (this.id == BlockList.pulleyBase.id) {
                        id = BlockList.pulleyBaseActive.id;
                    } else {
                        id = BlockList.pulleyStickyBase.id;
                    }

                    world.setBlockAndMetadata(x, y, z, id, facing);
                    world.blockEvent(x, y, z, 1, facing);
                }
            }

            player.addStat(StatList.pistonUse, 1);
            return true;
        } else {
            return true;
        }
    }

    @Override
    public void onPlaced(World world, Player player, int x, int y, int z, int side) {
        super.onPlaced(world, player, x, y, z, side);
        int i6 = determineOrientation(world, x, y, z, player);
        world.setBlockMetadataWithNotify(x, y, z, i6);
        if (!world.isOnline) {
            this.updateState(world, x, y, z);
        }
    }

    @Override
    public void onNeighborChange(World world, int x, int y, int z, int id) {
        if (!world.isOnline) {
            world.forceTick(x, y, z, this.id);
        }
    }

    @Override
    public void onTick(World world, int x, int y, int z, Random random) {
        if (this.extended) {
            this.updateStateReverse(world, x, y, z);
        } else {
            this.updateState(world, x, y, z);
        }
    }

    @Override
    public int tickRate() {
        return 1;
    }

    private void updateState(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        int facing = getOrientation(meta);
        boolean pwr = this.isIndirectlyPowered(world, x, y, z, facing);
        if (meta != 7) {
            if (pwr && !isExtended(meta)) {
                if (canExtend(world, x, y, z, facing)) {
                    world.setBlockMetadata(x, y, z, facing | 8);
                    world.blockEvent(x, y, z, 0, facing);
                }
            } else if (!pwr && isExtended(meta)) {
                world.setBlockMetadata(x, y, z, facing);
                world.blockEvent(x, y, z, 1, facing);
            }
        }
    }

    private void updateStateReverse(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        int facing = getOrientation(meta);
        boolean pwr = this.isIndirectlyPowered(world, x, y, z, facing);
        if (meta != 7) {
            if (!pwr && !isExtended(meta)) {
                if (canExtend(world, x, y, z, facing)) {
                    world.setBlockMetadata(x, y, z, facing | 8);
                    world.blockEvent(x, y, z, 0, facing);
                }
            } else if (pwr && isExtended(meta)) {
                world.setBlockMetadata(x, y, z, facing);
                world.blockEvent(x, y, z, 1, facing);
            }
        }
    }

    private boolean isIndirectlyPowered(World world, int x, int y, int z, int side) {
        if (side != 0 && world.getSignal(x, y - 1, z, 0)) {
            return true;
        } else if (side != 1 && world.getSignal(x, y + 1, z, 1)) {
            return true;
        } else if (side != 2 && world.getSignal(x, y, z - 1, 2)) {
            return true;
        } else if (side != 3 && world.getSignal(x, y, z + 1, 3)) {
            return true;
        } else if (side != 5 && world.getSignal(x + 1, y, z, 5)) {
            return true;
        } else if (side != 4 && world.getSignal(x - 1, y, z, 4)) {
            return true;
        } else if (world.getSignal(x, y, z, 0)) {
            return true;
        } else if (world.getSignal(x, y + 2, z, 1)) {
            return true;
        } else if (world.getSignal(x, y + 1, z - 1, 2)) {
            return true;
        } else if (world.getSignal(x, y + 1, z + 1, 3)) {
            return true;
        } else {
            return world.getSignal(x - 1, y + 1, z, 4) ? true : world.getSignal(x + 1, y + 1, z, 5);
        }
    }

    @Override
    public void blockEvent(World world, int x, int y, int z, int type, int meta) {
        if (type == 0) {
            if (this.tryExtend(world, x, y, z, meta)) {
                world.setBlockMetadataWithNotify(x, y, z, meta | 8);
                world.playBlockSound(x + 0.5F, y + 0.5F, z + 0.5F, "tile.piston.out", 0.5F, world.rand.nextFloat() * 0.25F + 0.6F);
            } else {
                world.setBlockMetadata(x, y, z, meta);
            }
        } else if (type == 1) {
            TileEntity TE = world.getTileEntity(x + Direction.offsetsXForSide[meta], y + Direction.offsetsYForSide[meta], z + Direction.offsetsZForSide[meta]);
            if (TE != null && TE instanceof PistonEntity) {
                ((PistonEntity)TE).remove();
            }

            world.setBlockAndMetadata(x, y, z, BlockList.pulleyMoving.id, meta);
            world.setBlockTileEntity(x, y, z, MovingPiston.getTileEntity(this.id, meta, meta, false, true));
            if (this.sticky) {
                int xOffset = x + Direction.offsetsXForSide[meta] * 2;
                int yOffset = y + Direction.offsetsYForSide[meta] * 2;
                int zOffset = z + Direction.offsetsZForSide[meta] * 2;
                int theID = world.getBlockId(xOffset, yOffset, zOffset);
                int theMeta = world.getBlockMetadata(xOffset, yOffset, zOffset);
                boolean extending = false;
                if (theID == BlockList.pulleyMoving.id) {
                    TileEntity tileEntity15 = world.getTileEntity(xOffset, yOffset, zOffset);
                    if (tileEntity15 != null && tileEntity15 instanceof PistonEntity) {
                        PistonEntity tileEntityPiston16 = (PistonEntity)tileEntity15;
                        if (tileEntityPiston16.getPistonOrientation() == meta && tileEntityPiston16.isExtending()) {
                            tileEntityPiston16.remove();
                            theID = tileEntityPiston16.getStoredBlock();
                            theMeta = tileEntityPiston16.getBlockMetadata();
                            extending = true;
                        }
                    }
                }

                if (extending
                    || theID <= 0
                    || !canPushBlock(theID, world, xOffset, yOffset, zOffset, false)
                    || BlockList.blocks[theID].solidMaterial() != 0
                        && theID != BlockList.pulleyBase.id
                        && theID != BlockList.pulleyStickyBase.id
                        && theID != BlockList.pulleyBaseActive.id
                        && theID != BlockList.pulleyStickyBaseActive.id) {
                    if (!extending) {
                        world.setBlockWithNotify(
                            x + Direction.offsetsXForSide[meta], y + Direction.offsetsYForSide[meta], z + Direction.offsetsZForSide[meta], 0
                        );
                    }
                } else {
                    x += Direction.offsetsXForSide[meta];
                    y += Direction.offsetsYForSide[meta];
                    z += Direction.offsetsZForSide[meta];
                    world.setBlockAndMetadata(x, y, z, BlockList.pulleyMoving.id, theMeta);
                    world.setBlockTileEntity(x, y, z, MovingPiston.getTileEntity(theID, theMeta, meta, false, false));
                    world.setBlockWithNotify(xOffset, yOffset, zOffset, 0);
                }
            } else {
                world.setBlockWithNotify(x + Direction.offsetsXForSide[meta], y + Direction.offsetsYForSide[meta], z + Direction.offsetsZForSide[meta], 0);
            }

            world.playBlockSound(x + 0.5F, y + 0.5F, z + 0.5F, "tile.piston.in", 0.5F, world.rand.nextFloat() * 0.15F + 0.6F);
        }
    }

    @Override
    public void forceBounds(int id) {
        this.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
    }

    @Override
    public void getCollisionBox(World world, int x, int y, int z, BoundingBox boundingBox, ArrayList<BoundingBox> surroundingBBs) {
        this.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        super.getCollisionBox(world, x, y, z, boundingBox, surroundingBBs);
    }

    @Override
    public BoundingBox getCollisionBox(World world, int x, int y, int z) {
        this.setStateBounds(world, x, y, z);
        return super.getCollisionBox(world, x, y, z);
    }

    public static int getOrientation(int id) {
        return id & 7;
    }

    public static boolean isExtended(int id) {
        return (id & 8) != 0;
    }

    private static int determineOrientation(World world, int x, int y, int z, Player player) {
        if (MathHelper.abs((float)player.posX - x) < 2.0F && MathHelper.abs((float)player.posZ - z) < 2.0F) {
            double offset = player.posY + 1.82 - player.yOffset;
            if (offset - y > 2.0) {
                return 1;
            }

            if (y - offset > 0.0) {
                return 0;
            }
        }

        int facing = MathHelper.floor_double(player.yaw * 4.0F / 360.0F + 0.5) & 3;
        return facing == 0 ? 2 : (facing == 1 ? 5 : (facing == 2 ? 3 : (facing == 3 ? 4 : 0)));
    }

    private static boolean canPushBlock(int pushID, World world, int x, int y, int z, boolean extended) {
        int border = world.settings.border - 1;
        if (border <= 0 || x >= -border && z >= -border && x < border && z < border) {
            if (pushID == BlockList.obsidian.id) {
                return false;
            }

            if (pushID != BlockList.pulleyBase.id
                && pushID != BlockList.pulleyStickyBase.id
                && pushID != BlockList.pulleyBaseActive.id
                && pushID != BlockList.pulleyStickyBaseActive.id) {
                if (BlockList.blocks[pushID].getHardness() < 0.0F) {
                    return false;
                }

                if (BlockList.blocks[pushID].solidMaterial() == 2) {
                    return false;
                }

                if (!extended && BlockList.blocks[pushID].solidMaterial() == 1) {
                    return false;
                }
            } else if (isExtended(world.getBlockMetadata(x, y, z))) {
                return false;
            }

            return !(BlockList.blocks[pushID] instanceof ContainerBlock);
        } else {
            return false;
        }
    }

    private static boolean canExtend(World world, int x, int y, int z, int side) {
        int xOff = x + Direction.offsetsXForSide[side];
        int yOff = y + Direction.offsetsYForSide[side];
        int zOff = z + Direction.offsetsZForSide[side];

        for (int it = 0; it < 13; it++) {
            if (yOff <= 0 || yOff >= world.getWorldHeight() - 1) {
                return false;
            }

            int i9 = world.getBlockId(xOff, yOff, zOff);
            if (i9 != 0) {
                if (!canPushBlock(i9, world, xOff, yOff, zOff, true)) {
                    return false;
                }

                if (BlockList.blocks[i9].solidMaterial() != 1) {
                    if (it == 12) {
                        return false;
                    }

                    xOff += Direction.offsetsXForSide[side];
                    yOff += Direction.offsetsYForSide[side];
                    zOff += Direction.offsetsZForSide[side];
                    continue;
                }
            }
            break;
        }

        return true;
    }

    private boolean tryExtend(World world, int x, int y, int z, int side) {
        int xOff = x + Direction.offsetsXForSide[side];
        int yOff = y + Direction.offsetsYForSide[side];
        int zOff = z + Direction.offsetsZForSide[side];
        int it = 0;

        while (it < 13) {
            if (yOff <= 0 || yOff >= 255) {
                return false;
            }

            int id = world.getBlockId(xOff, yOff, zOff);
            if (id == 0) {
                break;
            }

            if (!canPushBlock(id, world, xOff, yOff, zOff, true)) {
                return false;
            }

            if (BlockList.blocks[id].solidMaterial() != 1) {
                if (it == 12) {
                    return false;
                }

                xOff += Direction.offsetsXForSide[side];
                yOff += Direction.offsetsYForSide[side];
                zOff += Direction.offsetsZForSide[side];
                it++;
            } else {
                BlockList.blocks[id].dropBlock(world, xOff, yOff, zOff, world.getBlockMetadata(xOff, yOff, zOff));
                if (id == BlockList.crops.id) {
                    BlockList.blocks[id].onBroken(world, xOff, yOff, zOff, world.getBlockMetadata(xOff, yOff, zOff));
                }

                world.setBlockWithNotify(xOff, yOff, zOff, 0);
                break;
            }
        }

        while (xOff != x || yOff != y || zOff != z) {
            int newX = xOff - Direction.offsetsXForSide[side];
            int newY = yOff - Direction.offsetsYForSide[side];
            int newZ = zOff - Direction.offsetsZForSide[side];
            int newID = world.getBlockId(newX, newY, newZ);
            int newMeta = world.getBlockMetadata(newX, newY, newZ);
            if (newID == this.id && newX == x && newY == y && newZ == z) {
                world.setBlockAndMetadata(xOff, yOff, zOff, BlockList.pulleyMoving.id, side | (this.sticky ? 8 : 0));
                world.setBlockTileEntity(
                    xOff, yOff, zOff, MovingPiston.getTileEntity(BlockList.pulleyExtension.id, side | (this.sticky ? 8 : 0), side, true, false)
                );
            } else {
                world.setBlockAndMetadata(xOff, yOff, zOff, BlockList.pulleyMoving.id, newMeta);
                world.setBlockTileEntity(xOff, yOff, zOff, MovingPiston.getTileEntity(newID, newMeta, side, true, false));
            }

            xOff = newX;
            yOff = newY;
            zOff = newZ;
        }

        return true;
    }
}
