package net.minecraft.game.block.decoration;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSource;
import net.minecraft.game.world.util.Position;
import net.minecraft.localization.Translator;
import net.minecraft.util.math.Direction;

public class Bed extends Block {
    public static final int[][] orientation = new int[][]{{0, 1}, {-1, 0}, {0, -1}, {1, 0}};
    private Icon[] endIcons;
    private Icon[] sideIcons;
    private Icon[] topIcons;
    private int dropID;

    public Bed(int id, int drop) {
        super(id, MaterialList.circuits);
        this.dropID = drop;
        this.setBounds();
        this.silkTouch = false;
        this.enableStats = false;
    }

    @Override
    public boolean blockActivated(World world, int x, int y, int z, Player player) {
        int metadata = world.getBlockMetadata(x, y, z);
        if (!isFoot(metadata)) {
            int direction = getDirection(metadata);
            x += orientation[direction][0];
            z += orientation[direction][1];
            if (world.getBlockId(x, y, z) != this.id) {
                return true;
            }

            metadata = world.getBlockMetadata(x, y, z);
        }

        if (isOccupied(metadata)) {
            player.addChatMessage(Translator.getInstance().translateKey("tile.bed.occupied"));
            return true;
        }

        Bed.Status state = player.goToSleep(x, y, z);
        if (state == Bed.Status.OK) {
            setOccupied(world, x, y, z, true);
            player.resetKeys();
            return true;
        }

        if (state == Bed.Status.SET_POSITION) {
            player.addChatMessage(Translator.getInstance().translateKey("tile.bed.setPosition"));
            return true;
        }

        if (state == Bed.Status.NOT_POSSIBLE_NOW) {
            player.addChatMessage(Translator.getInstance().translateKey("tile.bed.noSleep"));
        } else if (state == Bed.Status.NOT_POSSIBLE_HERE) {
            player.addChatMessage(Translator.getInstance().translateKey("tile.bed.cursed"));
        }

        return true;
    }

    @Override
    public boolean blocksPathfinder(WorldSource world, int x, int y, int z) {
        return true;
    }

    @Override
    public boolean particles3D() {
        return false;
    }

    @Override
    public Icon getIcon(int side, int meta) {
        if (side == 0) {
            return BlockList.planks.getSideIcon(side);
        }

        int direction = getDirection(meta);
        int facing = Direction.bedDirection[direction][side];
        int foot = isFoot(meta) ? 1 : 0;
        return (foot != 1 || facing != 2) && (foot != 0 || facing != 3)
            ? (facing != 5 && facing != 4 ? this.topIcons[foot] : this.sideIcons[foot])
            : this.endIcons[foot];
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.topIcons = new Icon[]{ir.registerIcon("beds/" + this.name + "_feet_top"), ir.registerIcon("beds/" + this.name + "_head_top")};
        this.endIcons = new Icon[]{ir.registerIcon("beds/" + this.name + "_feet_end"), ir.registerIcon("beds/bed_head_end")};
        this.sideIcons = new Icon[]{ir.registerIcon("beds/" + this.name + "_feet_side"), ir.registerIcon("beds/" + this.name + "_head_side")};
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.BED;
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public void modifyOutline(WorldSource world, int x, int y, int z) {
        this.setBounds();
    }

    @Override
    public void onNeighborChange(World world, int x, int y, int z, int id) {
        int meta = world.getBlockMetadata(x, y, z);
        int direction = getDirection(meta);
        if (isFoot(meta)) {
            if (world.getBlockId(x - orientation[direction][0], y, z - orientation[direction][1]) != this.id) {
                world.setBlockWithNotify(x, y, z, 0);
            }
        } else if (world.getBlockId(x + orientation[direction][0], y, z + orientation[direction][1]) != this.id) {
            world.setBlockWithNotify(x, y, z, 0);
            if (!world.isOnline) {
                this.dropBlock(world, x, y, z, meta);
            }
        }
    }

    @Override
    public void onBlockRemoval(World world, int xCoord, int yCoord, int zCoord) {
        Player player = world.getClosestPlayer(xCoord, yCoord, zCoord, 16.0);
        if (player != null && player.getSpawn(0) != null && player.getSpawn(0).x == xCoord && player.getSpawn(0).y == yCoord && player.getSpawn(0).z == zCoord) {
            player.setSpawn(null, 0, false);
        }
    }

    @Override
    public int dropID(int meta, Random rand) {
        return isFoot(meta) ? 0 : this.dropID;
    }

    @Override
    public int pickID(int meta) {
        return this.dropID;
    }

    @Override
    public int pickMeta(int id) {
        return 0;
    }

    private void setBounds() {
        this.setBounds(0.0, 0.0, 0.0, 1.0, 0.5625, 1.0);
    }

    public static int getDirection(int meta) {
        return meta & 3;
    }

    public static boolean isFoot(int meta) {
        return (meta & 8) != 0;
    }

    public static boolean isOccupied(int meta) {
        return (meta & 4) != 0;
    }

    public static void setOccupied(World world, int x, int y, int z, boolean state) {
        int meta = world.getBlockMetadata(x, y, z);
        if (state) {
            meta |= 4;
        } else {
            meta &= -5;
        }

        world.setBlockMetadataWithNotify(x, y, z, meta);
    }

    public static Position nearestFreePos(World level, int x, int y, int z, int yoffset) {
        int meta = level.getBlockMetadata(x, y, z);
        int dir = getDirection(meta);

        for (int side = 0; side <= 1; side++) {
            int xOffset = x - orientation[dir][0] * side - 1;
            int zOffset = z - orientation[dir][1] * side - 1;
            int maxX = xOffset + 2;
            int maxZ = zOffset + 2;

            for (int posX = xOffset; posX <= maxX; posX++) {
                for (int posZ = zOffset; posZ <= maxZ; posZ++) {
                    if (level.isBlockFull(posX, y - 1, posZ)
                        && (!level.isBlockFull(posX, y, posZ) || level.isAir(posX, y, posZ))
                        && level.isAir(posX, y + 1, posZ)) {
                        if (yoffset <= 0) {
                            return new Position(posX, y, posZ);
                        }

                        yoffset--;
                    }
                }
            }
        }

        return null;
    }

    public enum Status {
        OK,
        NOT_POSSIBLE_HERE,
        NOT_POSSIBLE_NOW,
        TOO_FAR_AWAY,
        OTHER_PROBLEM,
        SET_POSITION;
    }
}
