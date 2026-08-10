package net.minecraft.game.block.building;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.block.misc.TransparentBlock;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSource;
import net.minecraft.game.world.dimension.Overworld;

public class Leaves extends TransparentBlock {
    private static final float[][] LEAF_COLORS = new float[18][];
    private static final int[][] DECAY_CHECK = new int[][]{
        {-1, -1, -1},
        {-1, -1, 0},
        {-1, -1, 1},
        {-1, 0, -1},
        {-1, 0, 0},
        {-1, 0, 1},
        {-1, 1, -1},
        {-1, 1, 0},
        {-1, 1, 1},
        {0, -1, -1},
        {0, -1, 0},
        {0, -1, 1},
        {0, 0, -1},
        {0, 0, 1},
        {0, 1, -1},
        {0, 1, 0},
        {0, 1, 1},
        {1, -1, -1},
        {1, -1, 0},
        {1, -1, 1},
        {1, 0, -1},
        {1, 0, 0},
        {1, 0, 1},
        {1, 1, -1},
        {1, 1, 0},
        {1, 1, 1}
    };
    public Icon[] standard;
    public Icon[] fast;
    private boolean lightLvl;
    public static String[] LEAVES = new String[]{
        "leaves",
        "leavesApple",
        "leavesBirch",
        "leavesSpruce",
        "leavesPalm",
        "leavesAcacia",
        "leavesJungle",
        "leavesMangrove",
        "leavesBaobab",
        "leavesMaple",
        "leavesCherry",
        "leavesBamboo",
        "leavesOldenOak",
        "leavesMahogany",
        "leavesPurpur",
        "leavesWillow",
        "leavesGold",
        "leavesSnow"
    };

    public Leaves(int id) {
        super(id, MaterialList.leaves, false);
        this.hasStates = true;
        this.multipleNames = true;
        this.names = LEAVES;
        this.particleGravity = 0.3F;
        this.compostable = true;
        this.setTickable(true);
    }

    @Override
    public void onTick(World world, int x, int y, int z, Random rand) {
        if (!world.isOnline) {
            int meta = world.getBlockMetadata(x, y, z);
            if (meta < 128) {
                int MAX_DISTANCE = 8;
                int MAX_NODES = 256;
                ArrayDeque<int[]> queue = new ArrayDeque<>();
                HashSet<Long> visited = new HashSet<>();
                queue.add(new int[]{x, y, z, 0});
                visited.add(this.pack(x, y, z));
                boolean foundWood = false;
                int nodesChecked = 0;

                while (!queue.isEmpty()) {
                    int[] node = queue.poll();
                    int cx = node[0];
                    int cy = node[1];
                    int cz = node[2];
                    int dist = node[3];
                    if (++nodesChecked > 256) {
                        break;
                    }

                    int id = world.getBlockId(cx, cy, cz);
                    if (id == BlockList.wood.id || id == BlockList.bamboo.id) {
                        foundWood = true;
                        break;
                    }

                    if (dist < 8 && id == this.id) {
                        for (int[] d : DECAY_CHECK) {
                            int nx = cx + d[0];
                            int ny = cy + d[1];
                            int nz = cz + d[2];
                            long key = this.pack(nx, ny, nz);
                            if (!visited.contains(key)) {
                                int nid = world.getBlockId(nx, ny, nz);
                                if (nid == this.id || nid == BlockList.wood.id || nid == BlockList.bamboo.id) {
                                    visited.add(key);
                                    queue.add(new int[]{nx, ny, nz, dist + 1});
                                }
                            }
                        }
                    }
                }

                if (!foundWood) {
                    this.dropBlock(world, x, y, z, meta);
                    if (world.setBlockWithNotify(x, y, z, 0)) {
                        world.playEffect(2003, x, y, z, meta << 12 | this.id);
                    }
                }
            }
        }
    }

    private long pack(int x, int y, int z) {
        return (x & 67108863L) << 38 | (z & 67108863L) << 12 | y & 4095L;
    }

    @Override
    public void onPlaced(World world, Player player, int x, int y, int z, int id) {
        super.onPlaced(world, player, x, y, z, id);
        world.setBlockMetadata(x, y, z, world.getBlockMetadata(x, y, z) + 128);
    }

    @Override
    public Icon getIcon(int side, int mdata) {
        return this.opacity ? this.standard[mdata & 127] : this.fast[mdata & 127];
    }

    @Override
    public int quantityDropped(Random random) {
        return random.nextInt(10) == 0 ? 1 : 0;
    }

    @Override
    public int dropID(int blockid, Random random) {
        return blockid == 11 ? 0 : BlockList.sapling.id;
    }

    @Override
    public int guaranteedID(int blockid) {
        return blockid == 11 ? 0 : BlockList.sapling.id;
    }

    private int altDrop(int id, Random random) {
        switch (id) {
            case 1:
                return ItemList.appleRed.id;
            case 16:
                return ItemList.nuggetGold.id;
            default:
                return 0;
        }
    }

    @Override
    public int getMaxMeta() {
        return 17;
    }

    @Override
    public void dropBlockItem(World world, int x, int y, int z, int meta, float force, boolean fortune) {
        if (!world.isOnline) {
            int quantity = this.quantityDropped(world.rand);

            for (int i = 0; i < quantity; i++) {
                float offsetX = world.rand.nextFloat() * 0.7F + 0.15F;
                float offsetY = world.rand.nextFloat() * 0.7F + 0.15F;
                float offsetZ = world.rand.nextFloat() * 0.7F + 0.15F;
                if (world.rand.nextFloat() <= force && this.dropID(meta, world.rand) > 0) {
                    DroppedItem item = new DroppedItem(
                        world, x + offsetX, y + offsetY, z + offsetZ, new ItemStack(this.dropID(meta, world.rand), 1, this.dropMeta(meta))
                    );
                    world.spawnEntity(item);
                }

                if (world.rand.nextFloat() <= force / 2.0F && this.altDrop(meta, world.rand) > 0) {
                    DroppedItem item = new DroppedItem(world, x + offsetX, y + offsetY, z + offsetZ, new ItemStack(this.altDrop(meta, world.rand), 1, 0));
                    world.spawnEntity(item);
                }
            }
        }
    }

    @Override
    public int dropMeta(int id) {
        if (id >= 128) {
            id -= 128;
        }

        if (id > 11 && id <= this.getMaxMeta()) {
            id--;
        }

        return id;
    }

    @Override
    protected int guaranteedMeta(int id) {
        if (id >= 128) {
            id -= 128;
        }

        return id;
    }

    @Override
    public int pickMeta(int id) {
        return id & 127;
    }

    @Override
    public boolean fullBlock() {
        return !this.opacity;
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.standard = new Icon[LEAVES.length * 2];
        this.fast = new Icon[LEAVES.length * 2];

        for (int index = 0; index < LEAVES.length; index++) {
            this.standard[index] = ir.registerIcon("leaves/" + LEAVES[index]);
            this.fast[index] = ir.registerIcon("leaves/" + LEAVES[index] + "_opaque");
        }
    }

    @Override
    public void getSubStacks(int id, CreativeTabs ct, List<ItemStack> ls) {
        for (int i = 0; i <= this.getMaxMeta(); i++) {
            ls.add(new ItemStack(id, 1, i));
        }
    }

    public void setGraphicsLevel(boolean lvl) {
        this.opacity = lvl;
    }

    @Override
    public boolean particles3D() {
        return false;
    }

    @Override
    public void spawnParticles(World world, int x, int y, int z, Random random) {
        int meta = world.getBlockMetadata(x, y, z) & 127;
        double xOffset = x + random.nextDouble();
        double yOffset = y + this.minY - 0.1;
        double zOffset = z + random.nextDouble();
        if (world.getBlockId(x, y - 1, z) == 0 && random.nextInt(225) == 0) {
            float[] color = meta >= 0 && meta < LEAF_COLORS.length ? LEAF_COLORS[meta] : null;
            if (color != null) {
                world.spawnParticle(ParticleType.LEAVES, xOffset, yOffset, zOffset, color[0], color[1], color[2]);
            }
        }

        if (world.isRaining() && !world.canSnowAt(x, y, z, true) && y > world.getSeaLevel() && world.currDim instanceof Overworld && random.nextInt(10) == 0) {
            world.spawnParticle(ParticleType.DRIPWATER, xOffset, yOffset, zOffset, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public int getBrightness(WorldSource world, int xCoord, int yCoord, int zCoord) {
        return world.getBlockId(xCoord, yCoord, zCoord) == this.id && this.lightLvl
            ? super.getBrightness(world, xCoord, yCoord, zCoord) / 2
            : super.getBrightness(world, xCoord, yCoord, zCoord);
    }

    public void setLightingLevel(boolean leavesLight) {
        this.lightLvl = leavesLight;
    }

    static {
        float[] pink = new float[]{0.996F, 0.69F, 0.816F};
        float[] yellow = new float[]{0.878F, 0.714F, 0.165F};
        float[] red = new float[]{1.0F, 0.345F, 0.259F};
        float[] blue = new float[]{0.455F, 0.627F, 0.808F};
        float[] alpha = new float[]{0.341F, 0.878F, 0.243F};
        float[] green = new float[]{0.22F, 0.757F, 0.122F};
        float[] olive = new float[]{0.3804F, 0.5608F, 0.1216F};
        float[] lime = new float[]{0.5686F, 0.7843F, 0.1569F};
        float[] dark = new float[]{0.102F, 0.4745F, 0.2078F};
        float[] purple = new float[]{0.4784F, 0.3059F, 0.7216F};
        float[] gray = new float[]{0.5059F, 0.5059F, 0.5059F};
        LEAF_COLORS[0] = alpha;
        LEAF_COLORS[1] = alpha;
        LEAF_COLORS[2] = yellow;
        LEAF_COLORS[3] = dark;
        LEAF_COLORS[4] = green;
        LEAF_COLORS[5] = olive;
        LEAF_COLORS[6] = green;
        LEAF_COLORS[7] = olive;
        LEAF_COLORS[8] = lime;
        LEAF_COLORS[9] = red;
        LEAF_COLORS[10] = pink;
        LEAF_COLORS[11] = olive;
        LEAF_COLORS[12] = green;
        LEAF_COLORS[13] = olive;
        LEAF_COLORS[14] = purple;
        LEAF_COLORS[15] = blue;
        LEAF_COLORS[16] = yellow;
        LEAF_COLORS[17] = gray;
    }
}
