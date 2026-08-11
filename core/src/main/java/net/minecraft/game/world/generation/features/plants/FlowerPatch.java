package net.minecraft.game.world.generation.features.plants;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.sky.SkyBiome;
import net.minecraft.game.world.generation.features.Feature;

public class FlowerPatch extends Feature {
    public static final FlowerPatch yellowFlower = new FlowerPatch(BlockList.dandelion.id);
    public static final FlowerPatch redFlower = new FlowerPatch(BlockList.rose.id);
    public static final FlowerPatch blueFlower = new FlowerPatch(BlockList.lilac.id);
    public static final FlowerPatch purpleFlower = new FlowerPatch(BlockList.petunia.id);
    public static final FlowerPatch tulip = new FlowerPatch(BlockList.tulip.id);
    public static final FlowerPatch daisy = new FlowerPatch(BlockList.daisy.id);
    public static final FlowerPatch blackFlower = new FlowerPatch(BlockList.witherRose.id);
    public static final FlowerPatch orangeFlower = new FlowerPatch(BlockList.fireFlower.id);
    public static final FlowerPatch pinkFlower = new FlowerPatch(BlockList.pinkTulip.id);
    public static final FlowerPatch whiteFlower = new FlowerPatch(BlockList.whiteTulip.id);
    public static final FlowerPatch cyanFlower = new FlowerPatch(BlockList.paeonia.id);
    public static final FlowerPatch glowLychen = new FlowerPatch(BlockList.glowLychen.id);
    public static final FlowerPatch pebbles = new FlowerPatch(BlockList.pebble.id);
    public static final FlowerPatch redshrooms = new FlowerPatch(BlockList.mushroomRed.id);
    public static final FlowerPatch mushrooms = new FlowerPatch(BlockList.mushroomBrown.id);
    public static final FlowerPatch glowshrooms = new FlowerPatch(BlockList.mushroomGlowing.id);
    public static final FlowerPatch berryBush = new FlowerPatch(BlockList.berryBush.id);
    public static final FlowerPatch jumpPad = new FlowerPatch(BlockList.jumpPad.id);
    public static final FlowerPatch nylium = new FlowerPatch(BlockList.mushroomNylium.id);
    public static final FlowerPatch crimson = new FlowerPatch(BlockList.mushroomCrimson.id);
    public static final FlowerPatch pebblesDEEP = new FlowerPatch(BlockList.pebble.id);
    public static final FlowerPatch lilyPad = new FlowerPatch(BlockList.lilyPad.id);
    public static final FlowerPatch waterLily = new FlowerPatch(BlockList.waterLily.id);
    private int plntBlockId;

    public FlowerPatch(int id) {
        this.plntBlockId = id;
    }

    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        int idCheck = 0;
        int meta = 0;
        int loop = 64;
        if (this.plntBlockId == BlockList.pebble.id) {
            loop = 8;
        }

        for (int attempts = 0; attempts < loop; attempts++) {
            int x0 = x + random.nextInt(8) - random.nextInt(8);
            int y0 = y + random.nextInt(4) - random.nextInt(4);
            int z0 = z + random.nextInt(8) - random.nextInt(8);
            if ((world.getBlockId(x0, y0, z0) == idCheck || world.getBlockId(x0, y0, z0) == BlockList.snowLayer.id)
                && BlockList.blocks[this.plntBlockId].canStay(world, x0, y0, z0)) {
                if (this.plntBlockId == BlockList.berryBush.id) {
                    meta = 15;
                } else if (this == pebblesDEEP) {
                    meta = 2;
                } else if (this.plntBlockId == BlockList.pebble.id) {
                    if (world.getBiomeAt(x0, z0) instanceof SkyBiome) {
                        meta = 8;
                    } else if (world.getBiomeAt(x0, z0).getTemperatureEnum() == Biome.Temperature.COLD || world.getBiomeAt(x0, z0).defaultStoneType != 1) {
                        meta = world.getBiomeAt(x0, z0).defaultStoneType;
                    }
                } else if (this.plntBlockId == BlockList.waterLily.id) {
                    meta = random.nextInt(3);
                }

                world.setBlockAndMetadataWithNotify(x0, y0, z0, this.plntBlockId, meta);
            }
        }

        return true;
    }
}
