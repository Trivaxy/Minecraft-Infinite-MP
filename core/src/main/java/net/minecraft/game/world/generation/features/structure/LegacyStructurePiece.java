package net.minecraft.game.world.generation.features.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.building.Stairs;
import net.minecraft.game.block.tileentity.container.ChestEntity;
import net.minecraft.game.block.tileentity.other.SpawnerEntity;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.util.random.WeightedRandom;

public abstract class LegacyStructurePiece {
    private String[] mobs = new String[]{"Zombie", "Skeleton", "Spider", "Zombie", "Flood", "Mummy", "Stray", "Lich", "Antlion"};
    public LegacyStructureBoundingBox boundingBox;
    protected int rotation;
    protected int type;

    protected LegacyStructurePiece(int compType) {
        this.type = compType;
        this.rotation = -1;
    }

    public void buildComponent(LegacyStructurePiece piece, List<LegacyStructurePiece> pcs, Random rand) {
    }

    public abstract boolean addComponentParts(World var1, Random var2, LegacyStructureBoundingBox var3);

    public LegacyStructureBoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    public int getComponentType() {
        return this.type;
    }

    public static LegacyStructurePiece findIntersecting(List<? extends LegacyStructurePiece> pcs, LegacyStructureBoundingBox bb) {
        for (LegacyStructurePiece pc : pcs) {
            if (pc.getBoundingBox() != null && pc.getBoundingBox().intersectsWith(bb)) {
                return pc;
            }
        }

        return null;
    }

    protected boolean isFlooded(World world, LegacyStructureBoundingBox bb) {
        int xStart = Math.max(this.boundingBox.minX - 1, bb.minX);
        int yStart = Math.max(this.boundingBox.minY - 1, bb.minY);
        int zStart = Math.max(this.boundingBox.minZ - 1, bb.minZ);
        int xEnd = Math.min(this.boundingBox.maxX + 1, bb.maxX);
        int yEnd = Math.min(this.boundingBox.maxY + 1, bb.maxY);
        int zEnd = Math.min(this.boundingBox.maxZ + 1, bb.maxZ);

        for (int x = xStart; x <= xEnd; x++) {
            for (int z = zStart; z <= zEnd; z++) {
                int id = world.getBlockId(x, yStart, z);
                if (id > 0 && BlockList.blocks[id].material.getIsLiquid()) {
                    return true;
                }

                id = world.getBlockId(x, yEnd, z);
                if (id > 0 && BlockList.blocks[id].material.getIsLiquid()) {
                    return true;
                }
            }
        }

        for (int var12 = xStart; var12 <= xEnd; var12++) {
            for (int z = yStart; z <= yEnd; z++) {
                int id = world.getBlockId(var12, z, zStart);
                if (id > 0 && BlockList.blocks[id].material.getIsLiquid()) {
                    return true;
                }

                id = world.getBlockId(var12, z, zEnd);
                if (id > 0 && BlockList.blocks[id].material.getIsLiquid()) {
                    return true;
                }
            }
        }

        for (int var13 = zStart; var13 <= zEnd; var13++) {
            for (int z = yStart; z <= yEnd; z++) {
                int id = world.getBlockId(xStart, z, var13);
                if (id > 0 && BlockList.blocks[id].material.getIsLiquid()) {
                    return true;
                }

                id = world.getBlockId(xEnd, z, var13);
                if (id > 0 && BlockList.blocks[id].material.getIsLiquid()) {
                    return true;
                }
            }
        }

        return false;
    }

    protected float getAirRatio(World world, LegacyStructureBoundingBox bb) {
        int x1 = Math.max(this.boundingBox.minX - 1, bb.minX);
        int z1 = Math.max(this.boundingBox.minZ - 1, bb.minZ);
        int x2 = Math.min(this.boundingBox.maxX + 1, bb.maxX);
        int z2 = Math.min(this.boundingBox.maxZ + 1, bb.maxZ);
        int y = Math.max(this.boundingBox.minY - 1, bb.minY);
        int totalBlocks = 0;
        int nonOpaque = 0;

        for (int x = x1; x <= x2; x++) {
            for (int z = z1; z <= z2; z++) {
                Block block = BlockList.blocks[world.getBlockId(x, y, z)];
                if (block == null || !block.fullBlock()) {
                    nonOpaque++;
                }

                totalBlocks++;
            }
        }

        return (float)nonOpaque / totalBlocks;
    }

    protected float getAirRatioInBB(World world, LegacyStructureBoundingBox bb) {
        int startX = Math.max(this.boundingBox.minX - 1, bb.minX);
        int startY = Math.max(this.boundingBox.minY - 1, bb.minY);
        int startZ = Math.max(this.boundingBox.minZ - 1, bb.minZ);
        int endX = Math.min(this.boundingBox.maxX + 1, bb.maxX);
        int endY = Math.min(this.boundingBox.maxY + 1, bb.maxY);
        int endZ = Math.min(this.boundingBox.maxZ + 1, bb.maxZ);
        int totalBlocks = 0;
        int nonOpaque = 0;

        for (int x = startX; x <= endX; x++) {
            for (int z = startZ; z <= endZ; z++) {
                int id = world.getBlockId(x, startY, z);
                if (id == 0) {
                    nonOpaque++;
                }

                totalBlocks++;
                id = world.getBlockId(x, endY, z);
                if (id == 0) {
                    nonOpaque++;
                }

                totalBlocks++;
            }
        }

        for (int var17 = startX; var17 <= endX; var17++) {
            for (int z = startY; z <= endY; z++) {
                int id = world.getBlockId(var17, z, startZ);
                if (id == 0) {
                    nonOpaque++;
                }

                totalBlocks++;
                id = world.getBlockId(var17, z, endZ);
                if (id == 0) {
                    nonOpaque++;
                }

                totalBlocks++;
            }
        }

        for (int var18 = startZ; var18 <= endZ; var18++) {
            for (int z = startY; z <= endY; z++) {
                int id = world.getBlockId(startX, z, var18);
                if (id == 0) {
                    nonOpaque++;
                }

                totalBlocks++;
                id = world.getBlockId(endX, z, var18);
                if (id == 0) {
                    nonOpaque++;
                }

                totalBlocks++;
            }
        }

        return (float)nonOpaque / totalBlocks;
    }

    protected int getXWithOffset(int minx, int maxx) {
        switch (this.rotation) {
            case 0:
            case 2:
                return this.boundingBox.minX + minx;
            case 1:
                return this.boundingBox.maxX - maxx;
            case 3:
                return this.boundingBox.minX + maxx;
            default:
                return minx;
        }
    }

    protected int getYWithOffset(int y) {
        return this.rotation == -1 ? y : y + this.boundingBox.minY;
    }

    protected int getZWithOffset(int minz, int maxz) {
        switch (this.rotation) {
            case 0:
                return this.boundingBox.minZ + maxz;
            case 1:
            case 3:
                return this.boundingBox.minZ + minz;
            case 2:
                return this.boundingBox.maxZ - maxz;
            default:
                return maxz;
        }
    }

    protected boolean isBlockExposed(World level, int x, int y, int z, LegacyStructureBoundingBox bb) {
        int xo = this.getXWithOffset(x, z);
        int yo = this.getYWithOffset(y);
        int zo = this.getZWithOffset(x, z);
        return bb.isVecInside(xo, yo, zo) && level.isBlockExposed(xo, yo, zo);
    }

    protected int getMetadataWithOffset(int id, int meta) {
        if (id == BlockList.minecartTrack.id) {
            if (this.rotation == 1 || this.rotation == 3) {
                return meta == 1 ? 0 : 1;
            }
        } else if (id != BlockList.doorWood.id && id != BlockList.doorSteel.id) {
            if (!(BlockList.blocks[id] instanceof Stairs)) {
                if (id == BlockList.ladder.id) {
                    if (this.rotation == 0) {
                        if (meta == 2) {
                            return 3;
                        }

                        if (meta == 3) {
                            return 2;
                        }
                    } else if (this.rotation == 1) {
                        if (meta == 2) {
                            return 4;
                        }

                        if (meta == 3) {
                            return 5;
                        }

                        if (meta == 4) {
                            return 2;
                        }

                        if (meta == 5) {
                            return 3;
                        }
                    } else if (this.rotation == 3) {
                        if (meta == 2) {
                            return 5;
                        }

                        if (meta == 3) {
                            return 4;
                        }

                        if (meta == 4) {
                            return 2;
                        }

                        if (meta == 5) {
                            return 3;
                        }
                    }
                }
            } else {
                if (this.rotation == 0) {
                    if (meta == 2) {
                        return 3;
                    }

                    if (meta == 3) {
                        return 2;
                    }

                    return meta;
                }

                if (this.rotation == 1) {
                    if (meta == 0) {
                        return 2;
                    }

                    if (meta == 1) {
                        return 3;
                    }

                    if (meta == 2) {
                        return 0;
                    }

                    if (meta == 3) {
                        return 1;
                    }
                } else if (this.rotation == 3) {
                    if (meta == 0) {
                        return 2;
                    }

                    if (meta == 1) {
                        return 3;
                    }

                    if (meta == 2) {
                        return 1;
                    }

                    if (meta == 3) {
                        return 0;
                    }
                }
            }
        } else if (this.rotation == 0) {
            if (meta == 0) {
                return 2;
            }

            if (meta == 2) {
                return 0;
            }
        } else {
            if (this.rotation == 1) {
                return meta + 1 & 3;
            }

            if (this.rotation == 3) {
                return meta + 3 & 3;
            }
        }

        return meta;
    }

    protected void placeBlock(World world, int blockid, int metadata, int bbx, int bby, int bbz, LegacyStructureBoundingBox bb) {
        int x = this.getXWithOffset(bbx, bbz);
        int y = this.getYWithOffset(bby);
        int z = this.getZWithOffset(bbx, bbz);
        if (bb.isVecInside(x, y, z)) {
            world.setBlockAndMetadata(x, y, z, blockid, metadata);
        }
    }

    protected void placeBlockClip(World world, int blockid, int metadata, int bbx, int bby, int bbz, LegacyStructureBoundingBox bb) {
        int x = this.getXWithOffset(bbx, bbz);
        int y = this.getYWithOffset(bby);
        int z = this.getZWithOffset(bbx, bbz);
        if (bb.isVecInsideHorizontally(x, y, z, world.getWorldHeight())) {
            world.setBlockAndMetadata(x, y, z, blockid, metadata);
        }
    }

    protected int getBlock(World world, int x, int y, int z, LegacyStructureBoundingBox bb) {
        int realX = this.getXWithOffset(x, z);
        int realY = this.getYWithOffset(y);
        int realZ = this.getZWithOffset(x, z);
        return !bb.isVecInside(realX, realY, realZ) ? 0 : world.getBlockId(realX, realY, realZ);
    }

    protected int getBlockClip(World world, int x, int y, int z, LegacyStructureBoundingBox bb) {
        int realX = this.getXWithOffset(x, z);
        int realY = this.getYWithOffset(y);
        int realZ = this.getZWithOffset(x, z);
        return bb.isVecInsideHorizontally(realX, realY, realZ, world.getWorldHeight()) ? world.getBlockId(realX, realY, realZ) : -1;
    }

    protected void fill(
        World world, LegacyStructureBoundingBox bb, int startX, int startY, int startZ, int endX, int endY, int endZ, int id1, int id2, boolean bo
    ) {
        for (int yy0 = startY; yy0 <= endY; yy0++) {
            for (int xx0 = startX; xx0 <= endX; xx0++) {
                for (int zz0 = startZ; zz0 <= endZ; zz0++) {
                    if (!bo || this.getBlock(world, xx0, yy0, zz0, bb) != 0) {
                        if (yy0 != startY && yy0 != endY && xx0 != startX && xx0 != endX && zz0 != startZ && zz0 != endZ) {
                            this.placeBlock(world, id2, 0, xx0, yy0, zz0, bb);
                        } else {
                            this.placeBlock(world, id1, 0, xx0, yy0, zz0, bb);
                        }
                    }
                }
            }
        }
    }

    protected void fill(
        World world,
        LegacyStructureBoundingBox bb,
        int startX,
        int startY,
        int startZ,
        int endX,
        int endY,
        int endZ,
        int id1,
        int id2,
        int meta1,
        int meta2,
        boolean bo
    ) {
        for (int yy0 = startY; yy0 <= endY; yy0++) {
            for (int xx0 = startX; xx0 <= endX; xx0++) {
                for (int zz0 = startZ; zz0 <= endZ; zz0++) {
                    if (!bo || this.getBlock(world, xx0, yy0, zz0, bb) != 0) {
                        if (yy0 != startY && yy0 != endY && xx0 != startX && xx0 != endX && zz0 != startZ && zz0 != endZ) {
                            this.placeBlock(world, id2, meta1, xx0, yy0, zz0, bb);
                        } else {
                            this.placeBlock(world, id1, meta2, xx0, yy0, zz0, bb);
                        }
                    }
                }
            }
        }
    }

    protected void fill(
        World world,
        LegacyStructureBoundingBox bb,
        int startX,
        int startY,
        int startZ,
        int endX,
        int endY,
        int endZ,
        boolean someStupidFlag,
        Random rand,
        LegacyStructureBlocks blocks
    ) {
        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                for (int z = startZ; z <= endZ; z++) {
                    if (!someStupidFlag || this.getBlock(world, x, y, z, bb) != 0) {
                        blocks.selectBlocks(rand, x, y, z, y == startY || y == endY || x == startX || x == endX || z == startZ || z == endZ);
                        this.placeBlock(world, blocks.getBlockId(), blocks.getBlockMetadata(), x, y, z, bb);
                    }
                }
            }
        }
    }

    protected void fill(
        World world,
        LegacyStructureBoundingBox bb,
        Random rand,
        float chance,
        int startX,
        int startY,
        int startZ,
        int endX,
        int endY,
        int endZ,
        int id,
        int meta,
        boolean check
    ) {
        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                for (int z = startZ; z <= endZ; z++) {
                    if (rand.nextFloat() <= chance && (!check || this.getBlock(world, x, y, z, bb) != 0)) {
                        if (y != startY && y != endY && x != startX && x != endX && z != startZ && z != endZ) {
                            this.placeBlock(world, meta, 0, x, y, z, bb);
                        } else {
                            this.placeBlock(world, id, 0, x, y, z, bb);
                        }
                    }
                }
            }
        }
    }

    protected void placeRandomly(World world, LegacyStructureBoundingBox bb, Random random3, float chance, int x, int y, int z, int id, int meta) {
        if (random3.nextFloat() < chance) {
            this.placeBlock(world, id, meta, x, y, z, bb);
        }
    }

    protected void fillDownwards(World world, int id, int meta, int x, int y, int z, LegacyStructureBoundingBox bb) {
        int realX = this.getXWithOffset(x, z);
        int realY = this.getYWithOffset(y);
        int realZ = this.getZWithOffset(x, z);
        if (bb.isVecInside(realX, realY, realZ)) {
            while ((world.isAir(realX, realY, realZ) || world.getBlockMaterial(realX, realY, realZ).getIsLiquid()) && realY > 1) {
                world.setBlockAndMetadata(realX, realY, realZ, id, meta);
                realY--;
            }
        }
    }

    protected void placeChest(World world, LegacyStructureBoundingBox bb, Random rand, int x, int y, int z, LegacyStructureTreasure[] treasure, int amount) {
        int realX = this.getXWithOffset(x, z);
        int realY = this.getYWithOffset(y);
        int realZ = this.getZWithOffset(x, z);
        if (bb.isVecInside(realX, realY, realZ) && world.getBlockId(realX, realY, realZ) != BlockList.chest.id) {
            world.setBlockWithNotify(realX, realY, realZ, BlockList.chest.id);
            ChestEntity chest = (ChestEntity)world.getTileEntity(realX, realY, realZ);
            if (chest != null) {
                addLoot(rand, treasure, chest, amount);
            }
        }
    }

    protected void placeLockedChest(World world, LegacyStructureBoundingBox bb, Random rand, int x, int y, int z, LegacyStructureTreasure[] treasure, int items) {
        int realX = this.getXWithOffset(x, z);
        int realY = this.getYWithOffset(y);
        int realZ = this.getZWithOffset(x, z);
        if (bb.isVecInside(realX, realY, realZ) && world.getBlockId(realX, realY, realZ) != BlockList.lockedChest.id) {
            world.setBlock(realX, realY, realZ, BlockList.lockedChest.id);
            world.setBlockMetadata(realX, realY, realZ, 1);
            ChestEntity chest = (ChestEntity)world.getTileEntity(realX, realY, realZ);
            if (chest != null) {
                addLoot(rand, treasure, chest, items);
            }
        }
    }

    protected void placeSpawner(World world, LegacyStructureBoundingBox bb, Random rand, int x, int y, int z, int type) {
        int realX = this.getXWithOffset(x, z);
        int realY = this.getYWithOffset(y);
        int realZ = this.getZWithOffset(x, z);
        if (bb.isVecInside(realX, realY, realZ) && world.getBlockId(realX, realY, realZ) != BlockList.mobSpawner.id) {
            world.setBlock(realX, realY, realZ, BlockList.mobSpawner.id);
            SpawnerEntity spawner = (SpawnerEntity)world.getTileEntity(realX, realY, realZ);
            if (spawner != null) {
                spawner.setMob(this.mobs[type]);
            }
        }
    }

    private static void addLoot(Random rand, LegacyStructureTreasure[] treasure, ChestEntity chest, int amount) {
        for (int i = 0; i < amount; i++) {
            LegacyStructureTreasure loot = WeightedRandom.getRandomItem(rand, treasure);
            int stackSize = loot.minStackSize + rand.nextInt(loot.maxStackSize - loot.minStackSize + 1);
            if (ItemList.items[loot.itemID].getStackLimit() >= stackSize) {
                chest.setSlotContents(rand.nextInt(chest.getSize()), new ItemStack(loot.itemID, stackSize, loot.itemMetadata));
            } else {
                for (int index = 0; index < stackSize; index++) {
                    chest.setSlotContents(rand.nextInt(chest.getSize()), new ItemStack(loot.itemID, 1, loot.itemMetadata));
                }
            }
        }
    }

    protected float getFractionSolid(World world, LegacyStructureBoundingBox bb) {
        int startX = Math.max(this.boundingBox.minX - 1, bb.minX);
        int startY = Math.max(this.boundingBox.minY - 1, bb.minY);
        int startZ = Math.max(this.boundingBox.minZ - 1, bb.minZ);
        int endX = Math.min(this.boundingBox.maxX + 1, bb.maxX);
        int endY = Math.min(this.boundingBox.maxY + 1, bb.maxY);
        int endZ = Math.min(this.boundingBox.maxZ + 1, bb.maxZ);
        int countSolid = 0;
        int countAll = 0;

        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                for (int z = startZ; z < endZ; z++) {
                    countAll++;
                    if (world.isSolid(x, y, z)) {
                        countSolid++;
                    }
                }
            }
        }

        return (float)countSolid / countAll;
    }

    protected float getFractionExposed(World world, LegacyStructureBoundingBox bb) {
        int startX = Math.max(this.boundingBox.minX - 1, bb.minX);
        int startZ = Math.max(this.boundingBox.minZ - 1, bb.minZ);
        int endX = Math.min(this.boundingBox.maxX + 1, bb.maxX);
        int endY = Math.min(this.boundingBox.maxY + 1, bb.maxY);
        int endZ = Math.min(this.boundingBox.maxZ + 1, bb.maxZ);
        int contSeeTheSky = 0;
        int countAll = 0;

        for (int x = startX; x < endX; x++) {
            for (int z = startZ; z < endZ; z++) {
                countAll++;
                if (world.isBlockExposed(x, endY, z)) {
                    contSeeTheSky++;
                }
            }
        }

        return (float)contSeeTheSky / countAll;
    }
}
