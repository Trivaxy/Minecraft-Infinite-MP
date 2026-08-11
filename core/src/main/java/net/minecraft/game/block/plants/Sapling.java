package net.minecraft.game.block.plants;

import java.util.List;
import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.monster.Treant;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.trees.Tree;
import net.minecraft.game.world.generation.features.trees.coniferous.CedarTree;
import net.minecraft.game.world.generation.features.trees.coniferous.FirTree;
import net.minecraft.game.world.generation.features.trees.coniferous.GoldTree;
import net.minecraft.game.world.generation.features.trees.coniferous.MegaPine;
import net.minecraft.game.world.generation.features.trees.coniferous.OriginTree;
import net.minecraft.game.world.generation.features.trees.coniferous.PineTree;
import net.minecraft.game.world.generation.features.trees.coniferous.SpruceTree;
import net.minecraft.game.world.generation.features.trees.deciduous.AppleTree;
import net.minecraft.game.world.generation.features.trees.deciduous.AspenTree;
import net.minecraft.game.world.generation.features.trees.deciduous.BasicTree;
import net.minecraft.game.world.generation.features.trees.deciduous.BigTree;
import net.minecraft.game.world.generation.features.trees.deciduous.CherryTree;
import net.minecraft.game.world.generation.features.trees.deciduous.MapleTree;
import net.minecraft.game.world.generation.features.trees.deciduous.PrairieTree;
import net.minecraft.game.world.generation.features.trees.tropical.AcaciaTree;
import net.minecraft.game.world.generation.features.trees.tropical.BaobabTree;
import net.minecraft.game.world.generation.features.trees.tropical.CanopyTree;
import net.minecraft.game.world.generation.features.trees.tropical.JungleTree;
import net.minecraft.game.world.generation.features.trees.tropical.MangroveTree;
import net.minecraft.game.world.generation.features.trees.tropical.PalmTree;
import net.minecraft.game.world.generation.features.trees.tropical.RainforestTree;
import net.minecraft.game.world.generation.features.trees.tropical.SmallJungleTree;
import net.minecraft.game.world.generation.features.trees.tropical.WillowTree;
import net.minecraft.statistics.StatList;

public class Sapling extends Flower {
    private Icon[] icons;
    public static String[] SAPLINGS = new String[]{
        "sapling",
        "saplingApple",
        "saplingBirch",
        "saplingSpruce",
        "saplingPalm",
        "saplingAcacia",
        "saplingJungle",
        "saplingMangrove",
        "saplingBaobab",
        "saplingMaple",
        "saplingCherry",
        "saplingOldenOak",
        "saplingMahogany",
        "saplingPurpur",
        "saplingWillow",
        "saplingGold",
        "saplingPale"
    };

    public Sapling(int id) {
        super(id);
        this.setTickable(true);
        float offset = 0.4F;
        this.setBounds(0.5F - offset, 0.0, 0.5F - offset, 0.5F + offset, offset * 2.0F, 0.5F + offset);
        this.hasStates = true;
        this.multipleNames = true;
        this.names = SAPLINGS;
    }

    @Override
    public boolean canGrowHere(int blockid) {
        return blockid == BlockList.voidGrass.id || blockid == BlockList.grass.id || blockid == BlockList.dirt.id;
    }

    @Override
    public void onTick(World w, int x, int y, int z, Random r) {
        if (!w.isOnline) {
            super.onTick(w, x, y, z, r);
            if (w.getBlockBrightness(x, y + 1, z) >= 9 && r.nextInt(125) == 0) {
                if (r.nextInt(1000) == 0) {
                    Treant treant = new Treant(w);
                    treant.setPosition(x, y, z);
                    w.spawnEntity(treant);
                } else {
                    this.growTree(w, null, x, y, z, r);
                }
            }
        }
    }

    @Override
    public void growTree(World world, Player player, int x, int y, int z, Random rand) {
        int damage = world.getBlockMetadata(x, y, z);
        if (!this.tryGrowLargeTree(world, x, y, z, damage, rand)) {
            this.growTree(world, x, y, z, damage, rand);
        }

        if (player != null) {
            player.addStat(StatList.treeGrowth, 1);
        }
    }

    private boolean tryGrowLargeTree(World world, int x, int y, int z, int damage, Random rand) {
        Tree tree = null;
        boolean succ = false;

        for (int dx = 0; dx >= -1; dx--) {
            for (int dz = 0; dz >= -1; dz--) {
                if (this.adjacentSapling(world, x + dx, y, z + dz, damage)
                    && this.adjacentSapling(world, x + dx + 1, y, z + dz, damage)
                    && this.adjacentSapling(world, x + dx, y, z + dz + 1, damage)
                    && this.adjacentSapling(world, x + dx + 1, y, z + dz + 1, damage)) {
                    world.setBlock(x + dx, y, z + dz, 0);
                    world.setBlock(x + dx + 1, y, z + dz, 0);
                    world.setBlock(x + dx, y, z + dz + 1, 0);
                    world.setBlock(x + dx + 1, y, z + dz + 1, 0);
                    switch (damage) {
                        case 3:
                            tree = new MegaPine();
                        case 4:
                        case 5:
                        case 7:
                        case 9:
                        case 10:
                        case 11:
                        default:
                            break;
                        case 6:
                            tree = new JungleTree();
                            break;
                        case 8:
                            tree = new BaobabTree();
                            break;
                        case 12:
                            tree = new CanopyTree();
                            break;
                        case 13:
                            tree = new FirTree(rand.nextInt(6) + 8, true);
                    }

                    int xx = x + dx;
                    int yy = y;
                    int zz = z + dz;
                    if (tree != null) {
                        succ = tree.place(world, rand, xx, yy, zz);
                    }

                    if (!succ) {
                        world.setBlockAndMetadata(x + dx, y, z + dz, this.id, damage);
                        world.setBlockAndMetadata(x + dx + 1, y, z + dz, this.id, damage);
                        world.setBlockAndMetadata(x + dx, y, z + dz + 1, this.id, damage);
                        world.setBlockAndMetadata(x + dx + 1, y, z + dz + 1, this.id, damage);
                    }
                    break;
                }
            }
        }

        return succ;
    }

    private boolean adjacentSapling(World world, int x, int y, int z, int damage) {
        return world.getBlockId(x, y, z) == this.id && world.getBlockMetadata(x, y, z) == damage;
    }

    private void growTree(World world, int x, int y, int z, int damage, Random rand) {
        Tree tree = null;
        boolean succ = false;
        world.setBlock(x, y, z, 0);
        switch (damage) {
            case 0:
                tree = new BasicTree(world.getBiomeAt(x, z).fillerMeta);
                break;
            case 1:
                tree = new AppleTree();
                break;
            case 2:
                tree = new AspenTree();
                break;
            case 3:
                if (rand.nextBoolean()) {
                    tree = new SpruceTree();
                } else {
                    tree = new PineTree();
                }
                break;
            case 4:
                tree = new PalmTree();
                break;
            case 5:
                tree = new AcaciaTree();
                break;
            case 6:
                tree = new SmallJungleTree();
                break;
            case 7:
                tree = new MangroveTree(true);
                break;
            case 8:
                tree = new PrairieTree(7, 8);
                break;
            case 9:
                tree = new MapleTree();
                break;
            case 10:
                tree = new CherryTree();
                break;
            case 11:
                tree = new BigTree();
                break;
            case 12:
                tree = new RainforestTree(world.rand.nextInt(3));
                break;
            case 13:
                if (rand.nextBoolean()) {
                    tree = new CedarTree();
                } else {
                    tree = new FirTree(rand.nextInt(4) + 4, false);
                }
                break;
            case 14:
                tree = new WillowTree(rand.nextInt(4) + 6);
                break;
            case 15:
                tree = new GoldTree();
                break;
            case 16:
                tree = new OriginTree();
        }

        if (tree != null) {
            succ = tree.place(world, rand, x, y, z);
        }

        if (!succ) {
            world.setBlockAndMetadata(x, y, z, this.id, damage);
        }
    }

    @Override
    public Icon getIcon(int side, int mdata) {
        if (mdata < 0 || mdata >= this.icons.length) {
            mdata = 0;
        }

        return this.icons[mdata];
    }

    @Override
    public int getMaxMeta() {
        return 16;
    }

    @Override
    public int dropMeta(int id) {
        return id;
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.icons = new Icon[SAPLINGS.length * 2];

        for (int index = 0; index < SAPLINGS.length; index++) {
            this.icons[index] = ir.registerIcon("plants/" + SAPLINGS[index]);
        }
    }

    @Override
    public void getSubStacks(int id, CreativeTabs ct, List<ItemStack> ls) {
        for (int i = 0; i <= this.getMaxMeta(); i++) {
            ls.add(new ItemStack(id, 1, i));
        }
    }
}
