package net.minecraft.game.world.edit;

import net.minecraft.game.entity.player.Player;
import net.minecraft.game.physics.VectorBasic;
import net.minecraft.game.world.World;
import net.minecraft.game.world.chunk.BlockPosition;
import net.minecraft.game.world.edit.exporter.ExporterBase;

public class WorldEdit {
    public static BlockPosition corner1 = new BlockPosition();
    public static BlockPosition corner2 = new BlockPosition();
    public static int[][][] clipboard;
    public static VectorBasic clipboardDims = VectorBasic.NULL_VECTOR.copy();
    public static int[][][] undo;
    public static BlockPosition undoOrigin = new BlockPosition();
    public static VectorBasic undoDims = VectorBasic.NULL_VECTOR.copy();
    public static boolean hasUndo;
    public static boolean corner1Set = false;
    public static boolean corner2Set = false;
    public static VectorBasic relativeOffset = VectorBasic.NULL_VECTOR.copy();

    public static void init() {
        corner1Set = false;
        corner2Set = false;
        clipboard = (int[][][])null;
        clipboardDims = VectorBasic.NULL_VECTOR.copy();
        undo = (int[][][])null;
        undoDims = VectorBasic.NULL_VECTOR.copy();
        hasUndo = false;
        relativeOffset = VectorBasic.NULL_VECTOR.copy();
    }

    public static boolean checkCorners() {
        return corner1Set && corner2Set;
    }

    public static void setCorner1(int x, int y, int z) {
        corner1.x = x;
        corner1.y = y;
        corner1.z = z;
        corner1Set = true;
    }

    public static void setCorner2(int x, int y, int z) {
        corner2.x = x;
        corner2.y = y;
        corner2.z = z;
        corner2Set = true;
    }

    public static BlockPosition getFrom() {
        return new BlockPosition().set(Math.min(corner1.x, corner2.x), Math.min(corner1.y, corner2.y), Math.min(corner1.z, corner2.z));
    }

    public static BlockPosition getTo() {
        return new BlockPosition().set(Math.max(corner1.x, corner2.x), Math.max(corner1.y, corner2.y), Math.max(corner1.z, corner2.z));
    }

    public static int[][][] initBuffer(VectorBasic dims, BlockPosition from, BlockPosition to) {
        dims.x = to.x - from.x + 1;
        dims.y = to.y - from.y + 1;
        dims.z = to.z - from.z + 1;
        return new int[dims.x][dims.z][dims.y];
    }

    public static void copyToBuffer(int[][][] buffer, World world, BlockPosition from, BlockPosition to) {
        for (int x = from.x; x <= to.x; x++) {
            for (int z = from.z; z <= to.z; z++) {
                for (int y = from.y; y <= to.y; y++) {
                    buffer[x - from.x][z - from.z][y - from.y] = world.getBlockId(x, y, z) | world.getBlockMetadata(x, y, z) << 16;
                }
            }
        }
    }

    public static void pasteToWorld(int[][][] buffer, VectorBasic dims, World world, BlockPosition origin) {
        int x0 = origin.x;
        int y0 = origin.y;
        int z0 = origin.z;
        if (clipboard != null) {
            for (int x = 0; x < dims.x; x++) {
                for (int z = 0; z < dims.z; z++) {
                    for (int y = 0; y < dims.y; y++) {
                        int clip = buffer[x][z][y];
                        world.setBlockAndMetadataWithNotify(x0 + x, y0 + y, z0 + z, clip & 65535, clip >> 16 & 0xFF);
                    }
                }
            }
        }
    }

    public static void copy(World world, Player entityPlayer) {
        int px = (int)Math.floor(entityPlayer.posX);
        int py = (int)Math.floor(entityPlayer.posY);
        int pz = (int)Math.floor(entityPlayer.posZ);
        BlockPosition from = getFrom();
        BlockPosition to = getTo();
        clipboard = initBuffer(clipboardDims, from, to);
        copyToBuffer(clipboard, world, from, to);
        relativeOffset.x = px - from.x;
        relativeOffset.y = py - from.y;
        relativeOffset.z = pz - from.z;
    }

    public static int clear(World world) {
        return fill(world, 0, 0);
    }

    public static int fill(World world, int blockID, int meta) {
        BlockPosition from = getFrom();
        BlockPosition to = getTo();
        int cleared = 0;
        undoOrigin = from.copy();
        undo = initBuffer(undoDims, undoOrigin, to);
        copyToBuffer(undo, world, undoOrigin, to);
        hasUndo = true;

        for (int x = from.x; x <= to.x; x++) {
            for (int z = from.z; z <= to.z; z++) {
                for (int y = from.y; y <= to.y; y++) {
                    world.setBlockAndMetadataWithNotify(x, y, z, blockID, meta);
                    cleared++;
                }
            }
        }

        return cleared;
    }

    public static int substitute(World world, int existingBlockID, int existingMeta, int blockID, int meta) {
        BlockPosition from = getFrom();
        BlockPosition to = getTo();
        int cleared = 0;
        undoOrigin = from.copy();
        undo = initBuffer(undoDims, undoOrigin, to);
        copyToBuffer(undo, world, undoOrigin, to);
        hasUndo = true;

        for (int x = from.x; x <= to.x; x++) {
            for (int z = from.z; z <= to.z; z++) {
                for (int y = from.y; y <= to.y; y++) {
                    int worldBlockID = world.getBlockId(x, y, z);
                    int worldMeta = world.getBlockMetadata(x, y, z);
                    if (worldBlockID == existingBlockID && (existingMeta == -1 || worldMeta == existingMeta)) {
                        world.setBlockAndMetadataWithNotify(x, y, z, blockID, meta);
                    }

                    cleared++;
                }
            }
        }

        return cleared;
    }

    public static void cut(World world, Player entityPlayer) {
        copy(world, entityPlayer);
        clear(world);
    }

    public static void paste(World world, Player entityPlayer) {
        int px = (int)Math.floor(entityPlayer.posX);
        int py = (int)Math.floor(entityPlayer.posY);
        int pz = (int)Math.floor(entityPlayer.posZ);
        undoOrigin = new BlockPosition().set(px, py, pz);
        BlockPosition to = new BlockPosition().set(px + clipboardDims.x - 1, py + clipboardDims.y - 1, pz + clipboardDims.z - 1);
        undo = initBuffer(undoDims, undoOrigin, to);
        copyToBuffer(undo, world, undoOrigin, to);
        hasUndo = true;
        pasteToWorld(clipboard, clipboardDims, world, new BlockPosition().set(px, py, pz));
    }

    public static void undo(World world) {
        pasteToWorld(undo, undoDims, world, undoOrigin);
        hasUndo = false;
    }

    public static void rotate_ccw() {
        if (clipboard != null) {
            int[][][] aux = new int[clipboardDims.z][clipboardDims.x][clipboardDims.y];

            for (int x = 0; x < clipboardDims.x; x++) {
                for (int z = 0; z < clipboardDims.z; z++) {
                    aux[clipboardDims.z - 1 - z][x] = clipboard[x][z];
                }
            }

            int t = clipboardDims.x;
            clipboardDims.x = clipboardDims.z;
            clipboardDims.z = t;
            clipboard = aux;
        }
    }

    public static void rotate_cw() {
        if (clipboard != null) {
            int[][][] aux = new int[clipboardDims.z][clipboardDims.x][clipboardDims.y];

            for (int x = 0; x < clipboardDims.x; x++) {
                for (int z = 0; z < clipboardDims.z; z++) {
                    aux[z][clipboardDims.x - 1 - x] = clipboard[x][z];
                }
            }

            int t = clipboardDims.x;
            clipboardDims.x = clipboardDims.z;
            clipboardDims.z = t;
            clipboard = aux;
        }
    }

    public static boolean export(World world, int args, boolean flag, Player entityPlayer, String filename, ExporterBase exporter, String arg) {
        copy(world, entityPlayer);
        return exporter.export(clipboard, clipboardDims, filename, arg);
    }

    public static int clipboardSize() {
        return clipboardDims.x * clipboardDims.y * clipboardDims.z;
    }
}
