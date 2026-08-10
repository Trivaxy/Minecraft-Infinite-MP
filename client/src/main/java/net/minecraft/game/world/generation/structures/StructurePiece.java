package net.minecraft.game.world.generation.structures;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.tileentity.container.BookcaseEntity;
import net.minecraft.game.block.tileentity.container.ChestEntity;
import net.minecraft.game.block.tileentity.container.PotEntity;
import net.minecraft.game.block.tileentity.other.SpawnerEntity;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.game.world.chunk.Chunk;

public abstract class StructurePiece {
    protected Random buildingRng = new Random();
    protected World world;
    public Chunk chunk;
    public StructureBoundingBox aabb;
    public int xAbsolute;
    public int y0;
    public int zAbsolute;
    protected int x1;
    protected int z1;
    protected boolean rotated;

    public StructurePiece(World world, boolean rotated) {
        this.world = world;
        this.rotated = rotated;
    }

    public void place(int xAbsolute, int y0, int zAbsolute) {
        this.xAbsolute = xAbsolute;
        this.y0 = y0;
        this.zAbsolute = zAbsolute;
        this.aabb = new StructureBoundingBox(this.xAbsolute, this.zAbsolute, this.xAbsolute + this.getWidth() - 1, this.zAbsolute + this.getLength() - 1);
    }

    @Override
    public String toString() {
        return this.getClass() + " [" + this.xAbsolute + " " + this.y0 + " " + this.zAbsolute + "] R=" + this.rotated;
    }

    public void clip(int xChunk, int zChunk) {
        this.x1 = xChunk << 4;
        this.z1 = zChunk << 4;
        this.chunk = this.world.getChunk(xChunk, zChunk);
        this.buildingRng.setSeed(this.xAbsolute * 341873128712L + this.zAbsolute * 132897987541L);
    }

    protected abstract int buildingWidth();

    protected abstract int buildingLength();

    protected abstract int buildingHeight();

    public int getWidth() {
        return this.rotated ? this.buildingLength() : this.buildingWidth();
    }

    public int getLength() {
        return this.rotated ? this.buildingWidth() : this.buildingLength();
    }

    public int getHeight() {
        return this.buildingHeight();
    }

    public int translateMetadata(int id, int meta) {
        switch (id) {
            case 17:
                if (meta == 4) {
                    meta = 12;
                } else if (meta == 12) {
                    meta = 4;
                }
                break;
            case 30:
                switch (meta) {
                    case 0:
                        return 3;
                    case 1:
                        return 2;
                    case 2:
                        return 1;
                    case 3:
                        int metax = 0;
                        return metax;
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    default:
                        return meta;
                    case 8:
                        return 11;
                    case 9:
                        return 10;
                    case 10:
                        return 9;
                    case 11:
                        return 8;
                }
            case 50:
                switch (meta) {
                    case 1:
                        return 3;
                    case 2:
                        return 4;
                    case 3:
                        return 1;
                    case 4:
                        int metax = 2;
                        return metax;
                    default:
                        return meta;
                }
            case 64:
                meta ^= 1;
                break;
            case 65:
                switch (meta) {
                    case 2:
                        meta = 4;
                        break;
                    case 3:
                        meta = 5;
                        break;
                    case 4:
                        meta = 2;
                        break;
                    case 5:
                        meta = 3;
                }
        }

        return meta;
    }

    public void translateMetadataList(int[] blocks) {
        for (int i = 0; i < blocks.length; i++) {
            if (blocks[i] > 255) {
                int meta = blocks[i] >> 8;
                int id = blocks[i] & 0xFF;
                meta = this.translateMetadata(id, meta);
                blocks[i] = meta << 8 | id;
            }
        }
    }

    public boolean isOpaqueRelative(int x, int y, int z) {
        y += this.y0;
        int blockID;
        if (this.rotated) {
            z += this.xAbsolute - this.x1;
            x += this.zAbsolute - this.z1;
            if (x < 0 || z < 0 || x > 15 || z > 15) {
                return true;
            }

            blockID = this.chunk.getBlockID(z, y, x);
        } else {
            x += this.xAbsolute - this.x1;
            z += this.zAbsolute - this.z1;
            if (x < 0 || z < 0 || x > 15 || z > 15) {
                return true;
            }

            blockID = this.chunk.getBlockID(x, y, z);
        }

        return Block.isOpaque.get(blockID);
    }

    public boolean setBlockRelative(int x, int y, int z, int id, int meta) {
        y += this.y0;
        if (this.rotated) {
            meta = this.translateMetadata(id, meta);
            z += this.xAbsolute - this.x1;
            x += this.zAbsolute - this.z1;
            return x >= 0 && z >= 0 && x <= 15 && z <= 15 ? this.chunk.setBlockAndState(z, y, x, id, meta) : false;
        } else {
            x += this.xAbsolute - this.x1;
            z += this.zAbsolute - this.z1;
            return x >= 0 && z >= 0 && x <= 15 && z <= 15 ? this.chunk.setBlockAndState(x, y, z, id, meta) : false;
        }
    }

    public int getBlockRelative(int x, int y, int z) {
        y += this.y0;
        if (this.rotated) {
            z += this.xAbsolute - this.x1;
            x += this.zAbsolute - this.z1;
            return x >= 0 && z >= 0 && x <= 15 && z <= 15 ? this.chunk.getBlockID(z, y, x) : -1;
        } else {
            x += this.xAbsolute - this.x1;
            z += this.zAbsolute - this.z1;
            return x >= 0 && z >= 0 && x <= 15 && z <= 15 ? this.chunk.getBlockID(x, y, z) : -1;
        }
    }

    public boolean setBlockColumnRelative(int x, int z, int[] blocks) {
        if (this.rotated) {
            this.translateMetadataList(blocks);
            z += this.xAbsolute - this.x1;
            x += this.zAbsolute - this.z1;
            return x >= 0 && z >= 0 && x <= 15 && z <= 15 ? this.chunk.setBlockColumn(z, this.y0, x, blocks) : false;
        } else {
            x += this.xAbsolute - this.x1;
            z += this.zAbsolute - this.z1;
            return x >= 0 && z >= 0 && x <= 15 && z <= 15 ? this.chunk.setBlockColumn(x, this.y0, z, blocks) : false;
        }
    }

    public boolean drawClippedBoxRelative(int x1, int y1, int z1, int x2, int y2, int z2, int id, int meta, boolean onSolid) {
        y1 += this.y0;
        y2 += this.y0;
        if (this.rotated) {
            meta = this.translateMetadata(id, meta);
            z1 += this.xAbsolute - this.x1;
            z2 += this.xAbsolute - this.x1;
            x1 += this.zAbsolute - this.z1;
            x2 += this.zAbsolute - this.z1;
            if (x1 > 15 || x2 < 0 || z1 > 15 || z2 < 0) {
                return false;
            }

            if (x1 < 0) {
                x1 = 0;
            }

            if (x2 > 15) {
                x2 = 15;
            }

            if (z1 < 0) {
                z1 = 0;
            }

            if (z2 > 15) {
                z2 = 15;
            }

            for (int x = x1; x <= x2; x++) {
                for (int z = z1; z <= z2; z++) {
                    for (int y = y1; y <= y2; y++) {
                        Block block = BlockList.blocks[this.chunk.getBlockID(z, y, x)];
                        if (onSolid || block != null && block.fullBlock()) {
                            this.chunk.setBlockAndState(z, y, x, id, meta);
                        }
                    }
                }
            }
        } else {
            x1 += this.xAbsolute - this.x1;
            x2 += this.xAbsolute - this.x1;
            z1 += this.zAbsolute - this.z1;
            z2 += this.zAbsolute - this.z1;
            if (x1 > 15 || x2 < 0 || z1 > 15 || z2 < 0) {
                return false;
            }

            if (x1 < 0) {
                x1 = 0;
            }

            if (x2 > 15) {
                x2 = 15;
            }

            if (z1 < 0) {
                z1 = 0;
            }

            if (z2 > 15) {
                z2 = 15;
            }

            for (int x = x1; x <= x2; x++) {
                for (int z = z1; z <= z2; z++) {
                    for (int y = y1; y <= y2; y++) {
                        Block block = BlockList.blocks[this.chunk.getBlockID(z, y, x)];
                        if (onSolid || block != null && block.fullBlock()) {
                            this.chunk.setBlockAndState(x, y, z, id, meta);
                        }
                    }
                }
            }
        }

        return true;
    }

    public boolean drawClippedBoxRandomizer(int x1, int y1, int z1, int x2, int y2, int z2, int id, int id2, int meta, int chance, boolean forcePlace) {
        y1 += this.y0;
        y2 += this.y0;
        if (this.rotated) {
            meta = this.translateMetadata(id, meta);
            z1 += this.xAbsolute - this.x1;
            z2 += this.xAbsolute - this.x1;
            x1 += this.zAbsolute - this.z1;
            x2 += this.zAbsolute - this.z1;
            if (x1 > 15 || x2 < 0 || z1 > 15 || z2 < 0) {
                return false;
            }

            if (x1 < 0) {
                x1 = 0;
            }

            if (x2 > 15) {
                x2 = 15;
            }

            if (z1 < 0) {
                z1 = 0;
            }

            if (z2 > 15) {
                z2 = 15;
            }

            for (int x = x1; x <= x2; x++) {
                for (int z = z1; z <= z2; z++) {
                    for (int y = y1; y <= y2; y++) {
                        Block block = BlockList.blocks[this.chunk.getBlockID(z, y, x)];
                        if (forcePlace || block != null && block.fullBlock()) {
                            this.chunk.setBlockAndState(z, y, x, this.world.rand.nextInt(chance) == 0 ? id : id2, meta);
                        }
                    }
                }
            }
        } else {
            x1 += this.xAbsolute - this.x1;
            x2 += this.xAbsolute - this.x1;
            z1 += this.zAbsolute - this.z1;
            z2 += this.zAbsolute - this.z1;
            if (x1 > 15 || x2 < 0 || z1 > 15 || z2 < 0) {
                return false;
            }

            if (x1 < 0) {
                x1 = 0;
            }

            if (x2 > 15) {
                x2 = 15;
            }

            if (z1 < 0) {
                z1 = 0;
            }

            if (z2 > 15) {
                z2 = 15;
            }

            for (int x = x1; x <= x2; x++) {
                for (int z = z1; z <= z2; z++) {
                    for (int y = y1; y <= y2; y++) {
                        Block block = BlockList.blocks[this.chunk.getBlockID(x, y, z)];
                        if (forcePlace || block != null && block.fullBlock()) {
                            this.chunk.setBlockAndState(x, y, z, this.world.rand.nextInt(chance) == 0 ? id : id2, meta);
                        }
                    }
                }
            }
        }

        return true;
    }

    public void addChestRelative(int x, int y, int z) {
        if (this.rotated) {
            this.addChest(z + this.xAbsolute, y + this.y0, x + this.zAbsolute);
        } else {
            this.addChest(x + this.xAbsolute, y + this.y0, z + this.zAbsolute);
        }
    }

    public void addSpawnerRelative(int x, int y, int z, String mobID) {
        if (this.rotated) {
            this.addSpawner(z + this.xAbsolute, this.y0 + y, x + this.zAbsolute, mobID);
        } else {
            this.addSpawner(x + this.xAbsolute, this.y0 + y, z + this.zAbsolute, mobID);
        }
    }

    public void addBookshelfRelative(int x, int y, int z) {
        if (this.rotated) {
            this.addBookshelf(z + this.xAbsolute, y + this.y0, x + this.zAbsolute);
        } else {
            this.addBookshelf(x + this.xAbsolute, y + this.y0, z + this.zAbsolute);
        }
    }

    public void addChest(int x, int y, int z) {
        if (x >= this.x1 && x <= this.x1 + 15 && z >= this.z1 && z <= this.z1 + 15) {
            this.world.setBlockWithNotify(x, y, z, BlockList.chest.id);
            ChestEntity tileEntityChest = (ChestEntity)this.world.getTileEntity(x, y, z);
            if (tileEntityChest != null && tileEntityChest.getSize() > 0) {
                int ni = this.getChestNumberOfItems();
                int level = this.world.rand.nextInt(10);

                for (int i = 0; i < ni; i++) {
                    int randomSlot = this.world.rand.nextInt(27);
                    ItemStack itemStack = this.getTreasure(level, this.world.rand);
                    tileEntityChest.setSlotContents(randomSlot, itemStack);
                }
            }
        }
    }

    public void addPot(int x, int y, int z) {
        if (x >= this.x1 && x <= this.x1 + 15 && z >= this.z1 && z <= this.z1 + 15) {
            this.world.setBlockWithNotify(x, y, z, BlockList.pot.id);
            PotEntity tileEntityChest = (PotEntity)this.world.getTileEntity(x, y, z);
            if (tileEntityChest != null && tileEntityChest.getSize() > 0) {
                int ni = this.getPotNumberOfItems();
                int level = this.world.rand.nextInt(10);

                for (int i = 0; i < ni; i++) {
                    ItemStack itemStack = this.getLoot(level, this.world.rand);
                    tileEntityChest.setSlotContents(i, itemStack);
                }
            }
        }
    }

    public void addSpawner(int x, int y, int z, String mobID) {
        if (x >= this.x1 && x <= this.x1 + 15 && z >= this.z1 && z <= this.z1 + 15) {
            this.world.setBlockWithNotify(x, y, z, BlockList.mobSpawner.id);
            SpawnerEntity tileEntityMobSpawner = (SpawnerEntity)this.world.getTileEntity(x, y, z);
            if (tileEntityMobSpawner != null) {
                tileEntityMobSpawner.setMob(mobID);
            }
        }
    }

    public void addBookshelf(int x, int y, int z) {
        if (x >= this.x1 && x <= this.x1 + 15 && z >= this.z1 && z <= this.z1 + 15) {
            this.world.setBlockWithNotify(x, y, z, BlockList.bookshelf.id);
            BookcaseEntity tileEntityChest = (BookcaseEntity)this.world.getTileEntity(x, y, z);
            if (tileEntityChest != null && tileEntityChest.getSize() > 0) {
                int ni = tileEntityChest.getSize();
                int metadata = 0;

                for (int i = 0; i < ni; i++) {
                    if (this.world.rand.nextInt(2) == 0) {
                        ItemStack stack = new ItemStack(ItemList.book, this.world.rand.nextInt(5) + 1);
                        tileEntityChest.setSlotContents(i, stack);
                        metadata++;
                    }
                }

                this.world.setBlockMetadataWithNotify(x, y, z, metadata);
            }
        }
    }

    protected ItemStack getTreasure(int level, Random rand) {
        return new ItemStack(ItemList.ingotGold);
    }

    protected ItemStack getLoot(int level, Random rand) {
        return new ItemStack(ItemList.ingotGold);
    }

    protected int getChestNumberOfItems() {
        return this.world.rand.nextInt(4) + 4;
    }

    protected int getPotNumberOfItems() {
        return this.world.rand.nextInt(4) + 1;
    }

    public abstract void generate();

    public void populate(int chunkX, int chunkZ) {
        this.clip(chunkX, chunkZ);
        if (this.buildingFitsCheck()) {
            this.build();
        }
    }

    public int getLandSurfaceAtCenter(int x, int z) {
        return this.world.getSurfaceHeightmap(x + this.getWidth() / 2, z + this.getLength() / 2);
    }

    public abstract boolean buildingFitsCheck();

    public abstract void build();
}
