package net.minecraft.game.world.generation.features.underground;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.Feature;
import net.minecraft.util.math.MathHelper;

public class OreLump extends Feature {
    public static final OreLump mud;
    public static final OreLump moss;
    public static final OreLump volcanicSoil;
    public static final OreLump susSand;
    public static final OreLump susGravel;
    public static final OreLump gravel;
    public static final OreLump magma;
    public static final OreLump silverfishDeep;
    public static final OreLump silverfish;
    public static final OreLump coalDeep;
    public static final OreLump coal;
    public static final OreLump copperDeep;
    public static final OreLump copper;
    public static final OreLump ironDeep;
    public static final OreLump iron;
    public static final OreLump goldDeep;
    public static final OreLump gold;
    public static final OreLump diamondDeep;
    public static final OreLump diamond;
    public static final OreLump rubyDeep;
    public static final OreLump ruby;
    public static final OreLump sapphireDeep;
    public static final OreLump sapphire;
    public static final OreLump emeraldDeep;
    public static final OreLump emerald;
    public static final OreLump netherMagma;
    public static final OreLump netherGold;
    public static final OreLump amethyst;
    public static final OreLump damascus;
    public static final OreLump aerDirt;
    public static final OreLump aerIron;
    public static final OreLump topaz;
    public static final OreLump adamantine;
    private int id;
    private int count;
    private int targetblockid;
    private int meta;
    private int targetMeta = 0;

    public OreLump(int id, int count, int targetblockid, int meta) {
        this.id = id;
        this.count = count;
        this.targetblockid = targetblockid;
        this.meta = meta;
    }

    public OreLump(int id, int count, int targetblockid, int meta, int meta2) {
        this(id, count, targetblockid, meta);
        this.targetMeta = meta2;
    }

    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        float range = random.nextFloat() * (float) Math.PI;
        float xOffPos = x + 8 + MathHelper.sin(range) * this.count / 8.0F;
        float xOffNeg = x + 8 - MathHelper.sin(range) * this.count / 8.0F;
        float zOffPos = z + 8 + MathHelper.cos(range) * this.count / 8.0F;
        float zOffNeg = z + 8 - MathHelper.cos(range) * this.count / 8.0F;
        float yOff1 = y + random.nextInt(3) - 2;
        float yOff2 = y + random.nextInt(3) - 2;

        for (int amount = 0; amount <= this.count; amount++) {
            double off1 = xOffPos + (double)(xOffNeg - xOffPos) * amount / this.count;
            double off2 = yOff1 + (double)(yOff2 - yOff1) * amount / this.count;
            double off3 = zOffPos + (double)(zOffNeg - zOffPos) * amount / this.count;
            double off4 = random.nextDouble() * this.count / 16.0;
            double off5 = (MathHelper.sin(amount * (float) Math.PI / this.count) + 1.0F) * off4 + 1.0;
            double off6 = (MathHelper.sin(amount * (float) Math.PI / this.count) + 1.0F) * off4 + 1.0;
            int minX = MathHelper.floor_double(off1 - off5 / 2.0);
            int minZ = MathHelper.floor_double(off2 - off6 / 2.0);
            int minY = MathHelper.floor_double(off3 - off5 / 2.0);
            int maxX = MathHelper.floor_double(off1 + off5 / 2.0);
            int maxZ = MathHelper.floor_double(off2 + off6 / 2.0);
            int maxY = MathHelper.floor_double(off3 + off5 / 2.0);

            for (int x0 = minX; x0 <= maxX; x0++) {
                double xDist = (x0 + 0.5 - off1) / (off5 / 2.0);
                if (xDist * xDist < 1.0) {
                    for (int z0 = minZ; z0 <= maxZ; z0++) {
                        double zDist = (z0 + 0.5 - off2) / (off6 / 2.0);
                        if (xDist * xDist + zDist * zDist < 1.0) {
                            for (int y0 = minY; y0 <= maxY; y0++) {
                                double yDist = (y0 + 0.5 - off3) / (off5 / 2.0);
                                if (xDist * xDist + zDist * zDist + yDist * yDist < 1.0
                                    && world.getBlockId(x0, z0, y0) == this.targetblockid
                                    && world.getBlockMetadata(x0, z0, y0) == this.targetMeta) {
                                    world.setBlockAndMetadata(x0, z0, y0, this.id, this.meta);
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    static {
        mud = new OreLump(BlockList.dirt.id, 48, BlockList.stone.id, 2);
        moss = new OreLump(BlockList.moss.id, 48, BlockList.stone.id, 0);
        volcanicSoil = new OreLump(BlockList.dirt.id, 32, BlockList.stone.id, 5, 2);
        susSand = new OreLump(BlockList.susSand.id, 32, BlockList.sand.id, 0);
        susGravel = new OreLump(BlockList.susGravel.id, 32, BlockList.stone.id, 0);
        gravel = new OreLump(BlockList.gravel.id, 32, BlockList.stone.id, 0);
        magma = new OreLump(BlockList.magma.id, 32, BlockList.stone.id, 0, 2);
        silverfishDeep = new OreLump(BlockList.silverfish.id, 16, BlockList.stone.id, 1, 1);
        silverfish = new OreLump(BlockList.silverfish.id, 16, BlockList.stone.id, 0, 0);
        coalDeep = new OreLump(BlockList.oreCoal.id, 16, BlockList.stone.id, 1, 2);
        coal = new OreLump(BlockList.oreCoal.id, 16, BlockList.stone.id, 0);
        copperDeep = new OreLump(BlockList.oreCopper.id, 8, BlockList.stone.id, 1, 2);
        copper = new OreLump(BlockList.oreCopper.id, 8, BlockList.stone.id, 0);
        ironDeep = new OreLump(BlockList.oreIron.id, 8, BlockList.stone.id, 1, 2);
        iron = new OreLump(BlockList.oreIron.id, 8, BlockList.stone.id, 0);
        goldDeep = new OreLump(BlockList.oreGold.id, 8, BlockList.stone.id, 1, 2);
        gold = new OreLump(BlockList.oreGold.id, 8, BlockList.stone.id, 0);
        diamondDeep = new OreLump(BlockList.oreDiamond.id, 8, BlockList.stone.id, 1, 2);
        diamond = new OreLump(BlockList.oreDiamond.id, 8, BlockList.stone.id, 0);
        rubyDeep = new OreLump(BlockList.oreRuby.id, 4, BlockList.stone.id, 1, 2);
        ruby = new OreLump(BlockList.oreRuby.id, 4, BlockList.stone.id, 0);
        sapphireDeep = new OreLump(BlockList.oreSapphire.id, 4, BlockList.stone.id, 1, 2);
        sapphire = new OreLump(BlockList.oreSapphire.id, 4, BlockList.stone.id, 0);
        emeraldDeep = new OreLump(BlockList.oreEmerald.id, 4, BlockList.stone.id, 1, 2);
        emerald = new OreLump(BlockList.oreEmerald.id, 4, BlockList.stone.id, 0);
        netherMagma = new OreLump(BlockList.magma.id, 32, BlockList.netherrack.id, 0);
        netherGold = new OreLump(BlockList.netherGold.id, 16, BlockList.netherrack.id, 0);
        amethyst = new OreLump(BlockList.oreAmethyst.id, 4, BlockList.netherrack.id, 0);
        damascus = new OreLump(BlockList.oreDamascus.id, 8, BlockList.netherrack.id, 0);
        aerDirt = new OreLump(BlockList.dirt.id, 32, BlockList.stone.id, 7, 8);
        aerIron = new OreLump(BlockList.aetherIron.id, 16, BlockList.stone.id, 0, 8);
        topaz = new OreLump(BlockList.oreTopaz.id, 4, BlockList.stone.id, 0, 8);
        adamantine = new OreLump(BlockList.oreAdamantine.id, 8, BlockList.stone.id, 0, 8);
    }
}
