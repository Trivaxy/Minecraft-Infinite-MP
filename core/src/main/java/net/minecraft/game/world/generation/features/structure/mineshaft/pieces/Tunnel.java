package net.minecraft.game.world.generation.features.structure.mineshaft.pieces;

import java.util.List;
import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.tileentity.other.SpawnerEntity;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.generation.features.structure.LegacyStructureBoundingBox;
import net.minecraft.game.world.generation.features.structure.LegacyStructurePiece;
import net.minecraft.game.world.generation.features.structure.LegacyStructureTreasure;
import net.minecraft.game.world.generation.features.structure.mineshaft.MineshaftPiece;

public class Tunnel extends MineshaftPiece {
    private static final String SPIDER_MOB_ID = "Spider";
    private static final LegacyStructureTreasure[] lootArray = new LegacyStructureTreasure[]{
        new LegacyStructureTreasure(ItemList.ingotCopper.id, 0, 1, 5, 10),
        new LegacyStructureTreasure(ItemList.ingotIron.id, 0, 1, 3, 5),
        new LegacyStructureTreasure(ItemList.ingotGold.id, 0, 1, 3, 2),
        new LegacyStructureTreasure(ItemList.emerald.id, 0, 1, 3, 3),
        new LegacyStructureTreasure(ItemList.ruby.id, 0, 1, 3, 3),
        new LegacyStructureTreasure(ItemList.sapphire.id, 0, 1, 3, 3),
        new LegacyStructureTreasure(ItemList.diamond.id, 0, 1, 2, 3),
        new LegacyStructureTreasure(ItemList.coal.id, 0, 3, 8, 10),
        new LegacyStructureTreasure(ItemList.bread.id, 0, 1, 3, 15),
        new LegacyStructureTreasure(ItemList.pickaxeSteel.id, 0, 1, 1, 1),
        new LegacyStructureTreasure(BlockList.minecartTrack.id, 0, 4, 8, 1),
        new LegacyStructureTreasure(ItemList.seedsMelon.id, 0, 2, 4, 10),
        new LegacyStructureTreasure(ItemList.seedsPumpkin.id, 0, 2, 4, 10)
    };
    private final boolean hasRails;
    private final boolean hasSpiders;
    private boolean spawnerPlaced;
    private int sectionCount;

    public Tunnel(int id, Random rand, LegacyStructureBoundingBox bb, int rot) {
        super(id);
        this.rotation = rot;
        this.boundingBox = bb;
        this.hasRails = rand.nextInt(2) == 0;
        this.hasSpiders = !this.hasRails && rand.nextInt(15) == 0;
        if (this.rotation != 2 && this.rotation != 0) {
            this.sectionCount = bb.getXSize() / 5;
        } else {
            this.sectionCount = bb.getZSize() / 5;
        }
    }

    public static LegacyStructureBoundingBox findValidPlacement(List<LegacyStructurePiece> pcs, Random rand, int x, int y, int z, int rot) {
        LegacyStructureBoundingBox bb = new LegacyStructureBoundingBox(x, y, z, x, y + 2, z);

        int i;
        for (i = rand.nextInt(3) + 2; i > 0; i--) {
            int offset = i * 5;
            switch (rot) {
                case 0:
                    bb.maxX = x + 2;
                    bb.maxZ = z + (offset - 1);
                    break;
                case 1:
                    bb.minX = x - (offset - 1);
                    bb.maxZ = z + 2;
                    break;
                case 2:
                    bb.maxX = x + 2;
                    bb.minZ = z - (offset - 1);
                    break;
                case 3:
                    bb.maxX = x + (offset - 1);
                    bb.maxZ = z + 2;
            }

            if (LegacyStructurePiece.findIntersecting(pcs, bb) == null) {
                break;
            }
        }

        return i > 0 ? bb : null;
    }

    @Override
    public void buildComponent(LegacyStructurePiece piece, List<LegacyStructurePiece> pcs, Random rand) {
        int type = this.getComponentType();
        int dir = rand.nextInt(4);
        switch (this.rotation) {
            case 0:
                if (dir <= 1) {
                    getNextComponent(
                        piece, pcs, rand, this.boundingBox.minX, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.maxZ + 1, this.rotation, type
                    );
                } else if (dir == 2) {
                    getNextComponent(
                        piece, pcs, rand, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.maxZ - 3, 1, type
                    );
                } else {
                    getNextComponent(
                        piece, pcs, rand, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.maxZ - 3, 3, type
                    );
                }
                break;
            case 1:
                if (dir <= 1) {
                    getNextComponent(
                        piece, pcs, rand, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ, this.rotation, type
                    );
                } else if (dir == 2) {
                    getNextComponent(piece, pcs, rand, this.boundingBox.minX, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ - 1, 2, type);
                } else {
                    getNextComponent(piece, pcs, rand, this.boundingBox.minX, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.maxZ + 1, 0, type);
                }
                break;
            case 2:
                if (dir <= 1) {
                    getNextComponent(
                        piece, pcs, rand, this.boundingBox.minX, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ - 1, this.rotation, type
                    );
                } else if (dir == 2) {
                    getNextComponent(piece, pcs, rand, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ, 1, type);
                } else {
                    getNextComponent(piece, pcs, rand, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ, 3, type);
                }
                break;
            case 3:
                if (dir <= 1) {
                    getNextComponent(
                        piece, pcs, rand, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ, this.rotation, type
                    );
                } else if (dir == 2) {
                    getNextComponent(
                        piece, pcs, rand, this.boundingBox.maxX - 3, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ - 1, 2, type
                    );
                } else {
                    getNextComponent(
                        piece, pcs, rand, this.boundingBox.maxX - 3, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.maxZ + 1, 0, type
                    );
                }
        }

        if (type < 8) {
            if (this.rotation != 2 && this.rotation != 0) {
                for (int x = this.boundingBox.minX + 3; x + 3 <= this.boundingBox.maxX; x += 5) {
                    int z = rand.nextInt(5);
                    if (z == 0) {
                        getNextComponent(piece, pcs, rand, x, this.boundingBox.minY, this.boundingBox.minZ - 1, 2, type + 1);
                    } else if (z == 1) {
                        getNextComponent(piece, pcs, rand, x, this.boundingBox.minY, this.boundingBox.maxZ + 1, 0, type + 1);
                    }
                }
            } else {
                for (int x = this.boundingBox.minZ + 3; x + 3 <= this.boundingBox.maxZ; x += 5) {
                    int z = rand.nextInt(5);
                    if (z == 0) {
                        getNextComponent(piece, pcs, rand, this.boundingBox.minX - 1, this.boundingBox.minY, x, 1, type + 1);
                    } else if (z == 1) {
                        getNextComponent(piece, pcs, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, x, 3, type + 1);
                    }
                }
            }
        }
    }

    @Override
    public boolean addComponentParts(World world, Random rand, LegacyStructureBoundingBox bb) {
        if (this.isFlooded(world, bb)) {
            return false;
        }

        if ((world.getBiomeAt(bb.maxX, bb.maxZ) != BiomeList.mesa || !(this.getFractionSolid(world, bb) < 0.4F))
            && !(this.getFractionExposed(world, bb) > 0.5F)) {
            int fractions = this.sectionCount * 5 - 1;
            this.fill(world, bb, 0, 0, 0, 2, 2, fractions, 0, 0, false);
            if (this.hasSpiders) {
                this.fill(world, bb, rand, 0.6F, 0, 0, 0, 2, 1, fractions, BlockList.cobweb.id, 0, false);
            }

            for (int sect = 0; sect < this.sectionCount; sect++) {
                int offset = 2 + sect * 5;
                if (this.getBlock(world, 0, 3, offset, bb) == 0 && this.getBlock(world, 1, 3, offset, bb) == 0 && this.getBlock(world, 2, 3, offset, bb) == 0) {
                    if (world.rand.nextBoolean()) {
                        if (this.getBlockClip(world, 0, -2, offset, bb) == 0 && !this.isBlockExposed(world, 0, 1, offset, bb)) {
                            this.placeBlock(world, BlockList.fence.id, 0, 0, 0, offset, bb);

                            for (int y = 1; this.getBlockClip(world, 0, y, offset, bb) == 0; y++) {
                                this.placeBlockClip(world, BlockList.chain.id, 0, 0, y, offset, bb);
                            }
                        }

                        if (this.getBlockClip(world, 2, -2, offset, bb) == 0 && !this.isBlockExposed(world, 2, 1, offset, bb)) {
                            this.placeBlock(world, BlockList.fence.id, 0, 2, 0, offset, bb);

                            for (int y = 1; this.getBlockClip(world, 2, y, offset, bb) == 0; y++) {
                                this.placeBlockClip(world, BlockList.chain.id, 0, 2, y, offset, bb);
                            }
                        }
                    } else if (world.rand.nextBoolean()) {
                        if (this.getBlockClip(world, 0, -2, offset, bb) == 0 && !this.isBlockExposed(world, 0, 1, offset, bb)) {
                            for (int y = -2; this.getBlockClip(world, 0, y, offset, bb) == 0; y--) {
                                this.placeBlockClip(world, BlockList.wood.id, 0, 0, y, offset, bb);
                            }
                        }

                        if (this.getBlockClip(world, 2, -2, offset, bb) == 0 && !this.isBlockExposed(world, 2, 1, offset, bb)) {
                            for (int y = -2; this.getBlockClip(world, 2, y, offset, bb) == 0; y--) {
                                this.placeBlockClip(world, BlockList.wood.id, 0, 2, y, offset, bb);
                            }
                        }
                    }
                } else {
                    this.fill(world, bb, 0, 0, offset, 0, 1, offset, BlockList.fence.id, 0, false);
                    this.fill(world, bb, 2, 0, offset, 2, 1, offset, BlockList.fence.id, 0, false);
                    this.placeBlock(world, BlockList.planks.id, 0, 0, 2, offset, bb);
                    this.placeBlock(world, BlockList.slabWood.id, 128, 1, 2, offset, bb);
                    this.placeBlock(world, BlockList.planks.id, 0, 2, 2, offset, bb);
                    this.placeRandomly(world, bb, rand, 0.1F, 0, 2, offset - 1, BlockList.cobweb.id, 0);
                    this.placeRandomly(world, bb, rand, 0.1F, 2, 2, offset - 1, BlockList.cobweb.id, 0);
                    this.placeRandomly(world, bb, rand, 0.1F, 0, 2, offset + 1, BlockList.cobweb.id, 0);
                    this.placeRandomly(world, bb, rand, 0.1F, 2, 2, offset + 1, BlockList.cobweb.id, 0);
                    this.placeRandomly(world, bb, rand, 0.05F, 0, 2, offset - 2, BlockList.cobweb.id, 0);
                    this.placeRandomly(world, bb, rand, 0.05F, 2, 2, offset - 2, BlockList.cobweb.id, 0);
                    this.placeRandomly(world, bb, rand, 0.05F, 0, 2, offset + 2, BlockList.cobweb.id, 0);
                    this.placeRandomly(world, bb, rand, 0.05F, 2, 2, offset + 2, BlockList.cobweb.id, 0);
                    this.placeRandomly(world, bb, rand, 0.05F, 0, 2, offset + 2, BlockList.torch.id, 0);
                    this.placeRandomly(world, bb, rand, 0.05F, 2, 2, offset + 2, BlockList.torch.id, 0);
                }

                if (rand.nextInt(100) == 0) {
                    this.placeChest(world, bb, rand, 2, 0, offset - 1, lootArray, 3 + rand.nextInt(4));
                }

                if (rand.nextInt(100) == 0) {
                    this.placeChest(world, bb, rand, 0, 0, offset + 1, lootArray, 3 + rand.nextInt(4));
                }

                if (this.hasSpiders && this.getFractionExposed(world, bb) < 0.4F) {
                    if (!this.spawnerPlaced) {
                        int height = this.getYWithOffset(0);
                        int i12 = offset - 1 + rand.nextInt(3);
                        int i13 = this.getXWithOffset(1, i12);
                        i12 = this.getZWithOffset(1, i12);
                        if (bb.isVecInside(i13, height, i12)) {
                            this.spawnerPlaced = true;
                            world.setBlockWithNotify(i13, height, i12, BlockList.mobSpawner.id);
                            SpawnerEntity tileEntityMobSpawner14 = (SpawnerEntity)world.getTileEntity(i13, height, i12);
                            if (tileEntityMobSpawner14 != null) {
                                tileEntityMobSpawner14.setMob("Spider");
                            }
                        }
                    }

                    if (this.spawnerPlaced) {
                        for (int y = 0; y <= 2; y++) {
                            for (int x = 0; x <= 2; x++) {
                                for (int z = 0; z <= 4; z++) {
                                    if (this.getBlock(world, x, y, z, bb) == 0) {
                                        this.placeBlock(world, BlockList.cobweb.id, 0, x, y, z, bb);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            for (int var11 = 0; var11 <= 2; var11++) {
                for (int offset = 0; offset <= fractions; offset++) {
                    int height = this.getBlock(world, var11, -1, offset, bb);
                    if (height == 0) {
                        this.placeBlock(world, BlockList.planks.id, 0, var11, -1, offset, bb);
                    }
                }
            }

            if (this.hasRails && this.getBlockClip(world, 0, 0, 0, bb) != 0) {
                for (int var12 = 0; var12 <= fractions; var12++) {
                    int offset = this.getBlock(world, 1, -1, var12, bb);
                    if (offset > 0 && Block.isOpaque.get(offset)) {
                        this.placeRandomly(
                            world, bb, rand, 0.7F, 1, 0, var12, BlockList.minecartTrack.id, this.getMetadataWithOffset(BlockList.minecartTrack.id, 0)
                        );
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }
}
