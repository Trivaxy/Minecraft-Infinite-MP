package net.minecraft.game.world.generation.structures.tree;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.tileentity.container.ChestEntity;
import net.minecraft.game.block.tileentity.other.SpawnerEntity;
import net.minecraft.game.entity.mob.monster.Treant;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.structure.LegacyStructureTreasure;
import net.minecraft.util.random.WeightedRandom;

public class DungeonTree extends RoundTree {
    private static final LegacyStructureTreasure[] chestLoot = new LegacyStructureTreasure[]{
        new LegacyStructureTreasure(ItemList.whip.id, 0, 1, 1, 3),
        new LegacyStructureTreasure(ItemList.swordPoison.id, 0, 1, 1, 5),
        new LegacyStructureTreasure(ItemList.bootsThorns.id, 0, 1, 1, 5),
        new LegacyStructureTreasure(ItemList.helmetThorns.id, 0, 1, 1, 5),
        new LegacyStructureTreasure(ItemList.plateThorns.id, 0, 1, 1, 3),
        new LegacyStructureTreasure(ItemList.legsThorns.id, 0, 1, 1, 4),
        new LegacyStructureTreasure(ItemList.natureStaff.id, 0, 1, 1, 3),
        new LegacyStructureTreasure(ItemList.crown.id, 0, 1, 1, 3),
        new LegacyStructureTreasure(ItemList.lifeShard.id, 0, 1, 1, 3)
    };
    public static final DungeonTree INSTANCE = new DungeonTree();

    public DungeonTree() {
        this.treeBranchDensity = 100.0;
        this.treeTrunkHeight = 0.368;
        this.treeFoliageDensity = 1.2;
        this.treeTrunkThickness = 9.0;
        this.treeRoots = TreeRoots.YES;
        this.treeHollowTrunk = true;
        this.height = 57;
    }

    @Override
    public void prepare() {
        super.prepare();
        this.foliageShape = new double[]{2.0, 3.0, 3.0, 3.0, 2.0};
    }

    @Override
    public boolean place(World world, Random rand, int x, int y, int z) {
        this.world = world;
        this.pos[0] = x;
        this.pos[1] = y;
        this.pos[2] = z;
        this.checkParameters();
        if (this.validTreeLocation()) {
            return false;
        }

        this.prepare();
        if (this.treeFoliage) {
            this.makeFoliage();
        }

        if (this.treeWood) {
            this.makeTrunk();
        }

        for (int i = x - 7; i < x + 7; i++) {
            for (int j = z - 7; j < z + 7; j++) {
                world.setBlock(i, y + 10, j, BlockList.wood.id);
                world.setBlock(i, y + 18, j, BlockList.wood.id);
                world.setBlock(i, y + 28, j, BlockList.wood.id);
                if (rand.nextInt(10) == 0) {
                    world.setBlock(i, y + 10, j, 0);
                }

                if (rand.nextInt(10) == 0) {
                    world.setBlock(i, y + 18, j, 0);
                }

                if (rand.nextInt(10) == 0) {
                    world.setBlock(i, y + 28, j, 0);
                }
            }
        }

        for (int i = x - 5; i < x + 5; i++) {
            for (int j = z - 5; j < z + 5; j++) {
                if (rand.nextInt(15) == 0) {
                    placeVines(world, rand, i, y + 9, j);
                }

                if (rand.nextInt(15) == 0) {
                    placeVines(world, rand, i, y + 17, j);
                }

                if (rand.nextInt(15) == 0) {
                    placeVines(world, rand, i, y + 27, j);
                }
            }
        }

        if (rand.nextInt(3) == 0) {
            world.setBlock(x + 5, y, z + 5, BlockList.mobSpawner.id);
            SpawnerEntity spawnerTE = (SpawnerEntity)world.getTileEntity(x + 5, y, z + 5);
            if (spawnerTE != null) {
                spawnerTE.setMob("Treant");
            }
        }

        if (rand.nextInt(3) == 0) {
            world.setBlock(x + 5, y, z - 5, BlockList.mobSpawner.id);
            SpawnerEntity spawnerTE = (SpawnerEntity)world.getTileEntity(x + 5, y, z - 5);
            if (spawnerTE != null) {
                spawnerTE.setMob("Treant");
            }
        }

        if (rand.nextInt(3) == 0) {
            world.setBlock(x - 5, y, z + 5, BlockList.mobSpawner.id);
            SpawnerEntity spawnerTE = (SpawnerEntity)world.getTileEntity(x - 5, y, z + 5);
            if (spawnerTE != null) {
                spawnerTE.setMob("Treant");
            }
        }

        if (rand.nextInt(3) == 0) {
            world.setBlock(x - 5, y, z - 5, BlockList.mobSpawner.id);
            SpawnerEntity spawnerTE = (SpawnerEntity)world.getTileEntity(x - 5, y, z - 5);
            if (spawnerTE != null) {
                spawnerTE.setMob("Treant");
            }
        }

        if (rand.nextInt(3) == 0) {
            world.setBlock(x + 3, y + 11, z + 3, BlockList.mobSpawner.id);
            SpawnerEntity spawnerTE = (SpawnerEntity)world.getTileEntity(x + 3, y + 11, z + 3);
            if (spawnerTE != null) {
                spawnerTE.setMob("Treant");
            }
        }

        if (rand.nextInt(3) == 0) {
            world.setBlock(x + 3, y + 11, z - 3, BlockList.mobSpawner.id);
            SpawnerEntity spawnerTE = (SpawnerEntity)world.getTileEntity(x + 3, y + 11, z - 3);
            if (spawnerTE != null) {
                spawnerTE.setMob("Treant");
            }
        }

        if (rand.nextInt(3) == 0) {
            world.setBlock(x - 3, y + 11, z + 3, BlockList.mobSpawner.id);
            SpawnerEntity spawnerTE = (SpawnerEntity)world.getTileEntity(x - 3, y + 11, z + 3);
            if (spawnerTE != null) {
                spawnerTE.setMob("Treant");
            }
        }

        if (rand.nextInt(3) == 0) {
            world.setBlock(x - 3, y + 11, z - 3, BlockList.mobSpawner.id);
            SpawnerEntity spawnerTE = (SpawnerEntity)world.getTileEntity(x - 3, y + 11, z - 3);
            if (spawnerTE != null) {
                spawnerTE.setMob("Treant");
            }
        }

        if (rand.nextInt(3) == 0) {
            world.setBlock(x + 2, y + 19, z + 2, BlockList.mobSpawner.id);
            SpawnerEntity spawnerTE = (SpawnerEntity)world.getTileEntity(x + 2, y + 19, z + 2);
            if (spawnerTE != null) {
                spawnerTE.setMob("Treant");
            }
        }

        if (rand.nextInt(3) == 0) {
            world.setBlock(x - 2, y + 19, z - 2, BlockList.mobSpawner.id);
            SpawnerEntity spawnerTE = (SpawnerEntity)world.getTileEntity(x - 2, y + 19, z - 2);
            if (spawnerTE != null) {
                spawnerTE.setMob("Treant");
            }
        }

        Treant treantBoss = new Treant(world);
        treantBoss.boss = true;
        treantBoss.tier = 1;
        treantBoss.health = treantBoss.maxHealth = 250;
        treantBoss.setPosAndRot(x, y + 21, z, treantBoss.yaw, 0.0F);
        world.spawnEntity(treantBoss);
        world.setBlockAndMetadata(x, y + 29, z, BlockList.lockedChest.id, 1);
        fillTreasureChestWithLoot(rand, chestLoot, (ChestEntity)world.getTileEntity(x, y + 29, z), 4 + rand.nextInt(5));
        return true;
    }

    private static void fillTreasureChestWithLoot(Random random, LegacyStructureTreasure[] pcs, ChestEntity chest, int slot) {
        if (chest != null) {
            for (int slots = 0; slots < slot; slots++) {
                LegacyStructureTreasure structurePieceTreasure5 = WeightedRandom.getRandomItem(random, pcs);
                int amount = structurePieceTreasure5.minStackSize
                    + random.nextInt(structurePieceTreasure5.maxStackSize - structurePieceTreasure5.minStackSize + 1);
                if (ItemList.items[structurePieceTreasure5.itemID].getStackLimit() >= amount) {
                    chest.setSlotContents(
                        random.nextInt(chest.getSize()), new ItemStack(structurePieceTreasure5.itemID, amount, structurePieceTreasure5.itemMetadata)
                    );
                } else {
                    for (int i = 0; i < amount; i++) {
                        chest.setSlotContents(
                            random.nextInt(chest.getSize()), new ItemStack(structurePieceTreasure5.itemID, 1, structurePieceTreasure5.itemMetadata)
                        );
                    }
                }
            }
        }
    }

    private static boolean placeVines(World world, Random random, int x, int y, int z) {
        int length = random.nextInt(12) + 3;
        boolean canPlace = true;

        for (int y0 = y; y0 <= y - 1 - length; y0--) {
            byte size = 2;
            if (y0 == y) {
                size = 0;
            }

            if (y0 >= y + 1 + length - 2) {
                size = 2;
            }

            for (int x0 = x - size; x0 <= x + size && canPlace; x0++) {
                for (int z0 = z - size; z0 <= z + size && canPlace; z0++) {
                    if (y0 < 0 || y0 >= world.getWorldHeight()) {
                        canPlace = true;
                    } else if (world.getBlockId(x0, y0, z0) != 0) {
                        canPlace = false;
                    }
                }
            }
        }

        if (!canPlace) {
            return false;
        }

        if (world.getBlockId(x, y + 1, z) == BlockList.wood.id && y < world.getWorldHeight() - length - 1) {
            if (world.getBlockId(x, y + 1, z) == BlockList.wood.id) {
                for (int height = 0; height < length; height++) {
                    if (world.getBlockId(x, y - height, z) == 0 && world.getBlockId(x, y - height - 1, z) == 0) {
                        world.setBlockAndMetadata(x, y - height, z, BlockList.caveVine.id, 15);
                        world.setBlockMetadata(x, y - height, z, 0);
                    }
                }
            }

            for (int height = 0; height < length; height++) {
                if (world.getBlockId(x, y - height + 1, z) == BlockList.caveVine.id
                    && world.getBlockId(x, y - height - 1, z) == BlockList.caveVine.id
                    && length > 2) {
                    world.setBlockMetadata(x, y - height, z, 15);
                }

                if (world.isSolid(x, y - height + 1, z)
                    && world.getBlockId(x, y - height - 1, z) == BlockList.caveVine.id
                    && world.getBlockId(x, y - height, z) == BlockList.caveVine.id) {
                    world.setBlockMetadata(x, y - height, z, 15);
                }
            }

            return true;
        } else {
            return false;
        }
    }
}
