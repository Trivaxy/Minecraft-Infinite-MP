package net.minecraft.game.world.biomes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import net.minecraft.game.world.biomes.cold.Fjord;
import net.minecraft.game.world.biomes.cold.Glacier;
import net.minecraft.game.world.biomes.cold.Muskeg;
import net.minecraft.game.world.biomes.cold.SnowyBiome;
import net.minecraft.game.world.biomes.cold.Taiga;
import net.minecraft.game.world.biomes.cold.Tundra;
import net.minecraft.game.world.biomes.hell.BasaltDeltas;
import net.minecraft.game.world.biomes.hell.EchoingAbyss;
import net.minecraft.game.world.biomes.hell.GoldCaverns;
import net.minecraft.game.world.biomes.hell.HellBiome;
import net.minecraft.game.world.biomes.hell.HellDesert;
import net.minecraft.game.world.biomes.hell.HellPlains;
import net.minecraft.game.world.biomes.hell.HellSwamp;
import net.minecraft.game.world.biomes.hell.HellWoods;
import net.minecraft.game.world.biomes.hot.Desert;
import net.minecraft.game.world.biomes.hot.Mesa;
import net.minecraft.game.world.biomes.hot.Outback;
import net.minecraft.game.world.biomes.hot.Pampas;
import net.minecraft.game.world.biomes.hot.Savanna;
import net.minecraft.game.world.biomes.hot.VolcanicWasteland;
import net.minecraft.game.world.biomes.hot.Wasteland;
import net.minecraft.game.world.biomes.ocean.Beach;
import net.minecraft.game.world.biomes.ocean.ColdOcean;
import net.minecraft.game.world.biomes.ocean.Crag;
import net.minecraft.game.world.biomes.ocean.FloatingIsland;
import net.minecraft.game.world.biomes.ocean.Ocean;
import net.minecraft.game.world.biomes.ocean.PolarIsland;
import net.minecraft.game.world.biomes.ocean.TropicalIsland;
import net.minecraft.game.world.biomes.ocean.WarmOcean;
import net.minecraft.game.world.biomes.sky.GoldenForest;
import net.minecraft.game.world.biomes.sky.SkyBiome;
import net.minecraft.game.world.biomes.sky.SkyCliffs;
import net.minecraft.game.world.biomes.sky.SkyPlains;
import net.minecraft.game.world.biomes.sky.SkySwamp;
import net.minecraft.game.world.biomes.sky.SkyTaiga;
import net.minecraft.game.world.biomes.temperate.CherryGrove;
import net.minecraft.game.world.biomes.temperate.DefaultBiome;
import net.minecraft.game.world.biomes.temperate.FlowerForest;
import net.minecraft.game.world.biomes.temperate.Forest;
import net.minecraft.game.world.biomes.temperate.MapleForest;
import net.minecraft.game.world.biomes.temperate.Meadow;
import net.minecraft.game.world.biomes.temperate.Mountains;
import net.minecraft.game.world.biomes.temperate.Orchard;
import net.minecraft.game.world.biomes.temperate.Plains;
import net.minecraft.game.world.biomes.temperate.Shroomland;
import net.minecraft.game.world.biomes.temperate.Springs;
import net.minecraft.game.world.biomes.temperate.Woodland;
import net.minecraft.game.world.biomes.warm.BambooJungle;
import net.minecraft.game.world.biomes.warm.Bog;
import net.minecraft.game.world.biomes.warm.Jungle;
import net.minecraft.game.world.biomes.warm.Marshland;
import net.minecraft.game.world.biomes.warm.Prairie;
import net.minecraft.game.world.biomes.warm.Rainforest;
import net.minecraft.game.world.biomes.warm.SparseJungle;
import net.minecraft.game.world.biomes.warm.Swamp;

public class BiomeList {
    protected static Logger logger = Logger.getLogger("Minecraft");
    public static final int LAST_VALID_INDEX;
    public static final int NETHER_BIOMES = 8;
    public static final int AETHER_BIOMES = 8;
    public static Biome[] biomes = new Biome[256];
    public static List<Biome>[] climateBiomes = new List[16];
    public static boolean[] enabledBiomes = new boolean[256];
    public static final Biome biomeDefault = new Biome(255)
        .setCaveFloor(32)
        .setDepthAndScale(0.3F, 1.0F)
        .setClimate(Biome.Temperature.TEMPERATE, Biome.Humidity.TEMPERATE)
        .setName("biome.default");
    public static final Biome cherryGrove;
    public static final Biome shrubland;
    public static final Biome meadow;
    public static final Biome birchForest;
    public static final Biome flowerForest;
    public static final Biome dryTaiga;
    public static final Biome megaTaiga;
    public static final Biome bambooJungle;
    public static final Biome sparseJungle;
    public static final Biome outback;
    public static final Biome pampas;
    public static final Biome marshland;
    public static final Biome bog;
    public static final Biome frozenShrubland;
    public static final Biome field;
    public static final Biome muskeg;
    public static final Biome mapleHills;
    public static final Biome snowyWoodland;
    public static final Biome shroomland;
    public static final Biome orchard;
    public static final Biome glacier;
    public static final Biome volcano;
    public static final Biome plateau;
    public static final Biome prairie;
    public static final Biome springs;
    public static final Biome plains;
    public static final Biome mountains;
    public static final Biome forest;
    public static final Biome valleyHills;
    public static final Biome woodlandHills;
    public static final Biome jungle;
    public static final Biome rainforest;
    public static final Biome swamp;
    public static final Biome wasteland;
    public static final Biome desert;
    public static final Biome savanna;
    public static final Biome mesa;
    public static final Biome tundra;
    public static final Biome snowyHills;
    public static final Biome taiga;
    public static final Biome fjord;
    public static final Biome ocean;
    public static final Biome shore;
    public static final Biome frozenOcean;
    public static final Biome frozenShore;
    public static final Biome warmOcean;
    public static final Biome warmShore;
    public static final Biome crag;
    public static final Biome beach;
    public static final Biome snowyBeach;
    public static final Biome stoneBeach;
    public static final Biome duneBeach;
    public static final Biome lushBeach;
    public static final Biome island;
    public static final Biome frozenIsland;
    public static final Biome springFloating;
    public static final Biome winterFloating;
    public static final Biome autumnFloating;
    public static final Biome summerFloating;
    public static final Biome hell;
    public static final Biome hellForest;
    public static final Biome hellPlains;
    public static final Biome hellSwamp;
    public static final Biome hellDesert;
    public static final Biome hellDeltas;
    public static final Biome hellGold;
    public static final Biome hellFrozen;
    public static final Biome sky;
    public static final Biome skyTaiga;
    public static final Biome skyTaigaFrozen;
    public static final Biome skySwamp;
    public static final Biome skyPlains;
    public static final Biome skyGold;
    public static final Biome skySwampFrozen;
    public static final Biome skyMountain;
    public static final Biome biomeVoid;

    static {
        for (int i = 0; i < 16; i++) {
            climateBiomes[i] = new ArrayList<>(Collections.singletonList(biomeDefault));
        }

        cherryGrove = new CherryGrove(17)
            .setName("biome.cherry")
            .setClimate(Biome.Temperature.TEMPERATE, Biome.Humidity.DRY)
            .setDepthAndScale(0.2F, 0.7F)
            .setColor(16739839);
        shrubland = new Plains(18)
            .setName("biome.shrubland")
            .setClimate(Biome.Temperature.TEMPERATE, Biome.Humidity.DRY)
            .setDepthAndScale(0.3F, 0.3F)
            .setColor(11918216);
        meadow = new Meadow(19)
            .setName("biome.meadow")
            .setClimate(Biome.Temperature.TEMPERATE, Biome.Humidity.DRY)
            .setDepthAndScale(2.7F, 0.3F)
            .setColor(6337413);
        birchForest = new Forest(20).setName("biome.forestBirch").setClimate(Biome.Temperature.TEMPERATE, Biome.Humidity.MOIST).setColor(2985545);
        flowerForest = new FlowerForest(21).setName("biome.forestFlowers").setClimate(Biome.Temperature.TEMPERATE, Biome.Humidity.TEMPERATE).setColor(16733194);
        dryTaiga = new Taiga(22).setName("biome.taigaDry").setClimate(Biome.Temperature.COLD, Biome.Humidity.MOIST).setColor(1456435);
        megaTaiga = new Taiga(23).setName("biome.taigaMega").setClimate(Biome.Temperature.COLD, Biome.Humidity.MOIST).setColor(3378817);
        bambooJungle = new BambooJungle(24).setName("biome.jungleBamboo").setClimate(Biome.Temperature.WARM, Biome.Humidity.MOIST).setColor(6458135);
        sparseJungle = new SparseJungle(25).setName("biome.jungleSmall").setClimate(Biome.Temperature.WARM, Biome.Humidity.MOIST).setColor(2900485);
        outback = new Outback(26)
            .setName("biome.outback")
            .setClimate(Biome.Temperature.HOT, Biome.Humidity.DRY)
            .disableRain()
            .setDepthAndScale(0.2F, 0.3F)
            .setColor(13274213);
        pampas = new Pampas(27)
            .setName("biome.pampas")
            .setClimate(Biome.Temperature.HOT, Biome.Humidity.DRY)
            .disableRain()
            .setDepthAndScale(1.0F, 0.5F)
            .setColor(12431967);
        marshland = new Marshland(28)
            .setName("biome.marsh")
            .setClimate(Biome.Temperature.WARM, Biome.Humidity.WET)
            .setDepthAndScale(0.0F, 0.1F)
            .setColor(430654);
        bog = new Bog(29).setName("biome.bog").setClimate(Biome.Temperature.WARM, Biome.Humidity.WET).setDepthAndScale(0.1F, 0.1F).setColor(430654);
        frozenShrubland = new Tundra(30)
            .setName("biome.steppes")
            .setClimate(Biome.Temperature.COLD, Biome.Humidity.DRY)
            .setDepthAndScale(0.3F, 0.3F)
            .setColor(16777215);
        field = new Tundra(31).setName("biome.field").setClimate(Biome.Temperature.COLD, Biome.Humidity.DRY).setDepthAndScale(0.3F, 0.7F).setColor(16777215);
        muskeg = new Muskeg(32)
            .setName("biome.muskeg")
            .setClimate(Biome.Temperature.COLD, Biome.Humidity.DRY)
            .enableSnow()
            .setDepthAndScale(0.1F, 0.6F)
            .setColor(16777215);
        mapleHills = new MapleForest(33)
            .setName("biome.forestMaple")
            .setClimate(Biome.Temperature.TEMPERATE, Biome.Humidity.WET)
            .setDepthAndScale(0.0F, 1.2F)
            .setColor(16197126);
        snowyWoodland = new Woodland(34)
            .setName("biome.woodlandSnow")
            .setClimate(Biome.Temperature.COLD, Biome.Humidity.WET)
            .enableSnow()
            .setDepthAndScale(0.0F, 1.2F)
            .setColor(5813002);
        shroomland = new Shroomland(35)
            .setName("biome.shroomland")
            .setClimate(Biome.Temperature.TEMPERATE, Biome.Humidity.MOIST)
            .setDepthAndScale(0.3F, 1.0F)
            .setColor(10037401);
        orchard = new Orchard(36)
            .setName("biome.orchard")
            .setClimate(Biome.Temperature.TEMPERATE, Biome.Humidity.WET)
            .setDepthAndScale(0.3F, 1.0F)
            .setColor(5832458);
        glacier = new Glacier(37)
            .setName("biome.glacier")
            .setDepthAndScale(0.4F, 1.8F)
            .setClimate(Biome.Temperature.COLD, Biome.Humidity.WET)
            .enableSnow()
            .setColor(14479521);
        volcano = new VolcanicWasteland(38)
            .setName("biome.volcano")
            .setDepthAndScale(0.5F, 1.0F)
            .setClimate(Biome.Temperature.HOT, Biome.Humidity.WET)
            .disableRain()
            .setColor(2631977);
        plateau = new Mesa(39)
            .setName("biome.plateau")
            .setDepthAndScale(2.7F, 0.1F)
            .setClimate(Biome.Temperature.HOT, Biome.Humidity.WET)
            .disableRain()
            .setColor(14238997);
        prairie = new Prairie(40)
            .setName("biome.prairie")
            .setDepthAndScale(0.2F, 0.8F)
            .setClimate(Biome.Temperature.WARM, Biome.Humidity.MOIST)
            .setColor(5470985);
        springs = new Springs(41)
            .setName("biome.springs")
            .setClimate(Biome.Temperature.TEMPERATE, Biome.Humidity.MOIST)
            .setDepthAndScale(1.7F, 1.2F)
            .setColor(6316128);
        plains = new Plains(1)
            .setName("biome.plains")
            .setClimate(Biome.Temperature.TEMPERATE, Biome.Humidity.DRY)
            .setDepthAndScale(0.1F, 0.3F)
            .setColor(9286496)
            .addAsMainClimateBiome();
        mountains = new Mountains(3)
            .setName("biome.mountains")
            .setClimate(Biome.Temperature.TEMPERATE, Biome.Humidity.TEMPERATE)
            .setDepthAndScale(1.7F, 1.7F)
            .setColor(6316128)
            .addAsMainClimateBiome();
        forest = new Forest(2)
            .setName("biome.forestSeasonal")
            .setClimate(Biome.Temperature.TEMPERATE, Biome.Humidity.MOIST)
            .setColor(353825)
            .addAsMainClimateBiome();
        valleyHills = new DefaultBiome(4)
            .setCaveFloor(32)
            .setName("biome.default")
            .setClimate(Biome.Temperature.TEMPERATE, Biome.Humidity.WET)
            .setDepthAndScale(0.3F, 1.0F)
            .setColor(5832458)
            .addAsMainClimateBiome();
        woodlandHills = new Woodland(5)
            .setName("biome.woodland")
            .setClimate(Biome.Temperature.WARM, Biome.Humidity.DRY)
            .setDepthAndScale(0.0F, 1.2F)
            .setColor(5813002)
            .addAsMainClimateBiome();
        jungle = new Jungle(6).setName("biome.jungle").setClimate(Biome.Temperature.WARM, Biome.Humidity.TEMPERATE).setColor(5470985).addAsMainClimateBiome();
        rainforest = new Rainforest(7)
            .setName("biome.rainforest")
            .setDepthAndScale(0.1F, 0.7F)
            .setClimate(Biome.Temperature.WARM, Biome.Humidity.MOIST)
            .setColor(5470985)
            .addAsMainClimateBiome();
        swamp = new Swamp(8)
            .setName("biome.swamp")
            .setClimate(Biome.Temperature.WARM, Biome.Humidity.WET)
            .setDepthAndScale(0.0F, 0.1F)
            .setColor(522674)
            .addAsMainClimateBiome();
        wasteland = new Wasteland(12)
            .setName("biome.wasteland")
            .setDepthAndScale(1.0F, 0.5F)
            .setClimate(Biome.Temperature.HOT, Biome.Humidity.DRY)
            .disableRain()
            .setColor(2631977)
            .addAsMainClimateBiome();
        desert = new Desert(9)
            .setName("biome.desert")
            .setClimate(Biome.Temperature.HOT, Biome.Humidity.TEMPERATE)
            .disableRain()
            .setColor(16421912)
            .addAsMainClimateBiome();
        savanna = new Savanna(10).setName("biome.savanna").setClimate(Biome.Temperature.HOT, Biome.Humidity.MOIST).setColor(12431967).addAsMainClimateBiome();
        mesa = new Mesa(11)
            .setName("biome.mesa")
            .setDepthAndScale(0.4F, 1.8F)
            .setClimate(Biome.Temperature.HOT, Biome.Humidity.WET)
            .disableRain()
            .setColor(14238997)
            .addAsMainClimateBiome();
        tundra = new Tundra(13)
            .setName("biome.tundra")
            .setClimate(Biome.Temperature.COLD, Biome.Humidity.DRY)
            .setDepthAndScale(0.1F, 0.3F)
            .enableSnow()
            .setColor(16777215)
            .addAsMainClimateBiome();
        snowyHills = new SnowyBiome(16)
            .setCaveFloor(32)
            .setName("biome.snow")
            .setClimate(Biome.Temperature.COLD, Biome.Humidity.TEMPERATE)
            .setDepthAndScale(0.3F, 1.0F)
            .enableSnow()
            .setColor(13497591)
            .addAsMainClimateBiome();
        taiga = new Taiga(14)
            .setName("biome.taiga")
            .setClimate(Biome.Temperature.COLD, Biome.Humidity.MOIST)
            .enableSnow()
            .setColor(747097)
            .addAsMainClimateBiome();
        fjord = new Fjord(15)
            .setName("biome.fjord")
            .setClimate(Biome.Temperature.COLD, Biome.Humidity.WET)
            .setDepthAndScale(0.4F, 1.8F)
            .setColor(16777215)
            .addAsMainClimateBiome();
        ocean = new Ocean(0).setName("biome.ocean").setClimate(Biome.Temperature.TEMPERATE, Biome.Humidity.WET).setDepthAndScale(-1.8F, 0.1F).setColor(112);
        shore = new Ocean(42)
            .setName("biome.shore")
            .setClimate(Biome.Temperature.TEMPERATE, Biome.Humidity.WET)
            .setDepthAndScale(-1.0F, 0.1F)
            .setColor(4145082);
        frozenOcean = new ColdOcean(43)
            .setName("biome.oceanCold")
            .setClimate(Biome.Temperature.COLD, Biome.Humidity.WET)
            .setDepthAndScale(-1.8F, 0.1F)
            .setColor(19312);
        frozenShore = new ColdOcean(44)
            .setName("biome.shoreCold")
            .setClimate(Biome.Temperature.COLD, Biome.Humidity.WET)
            .setDepthAndScale(-1.0F, 0.1F)
            .setColor(5477813);
        warmOcean = new WarmOcean(45)
            .setName("biome.oceanWarm")
            .setClimate(Biome.Temperature.WARM, Biome.Humidity.WET)
            .setDepthAndScale(-1.8F, 0.1F)
            .setColor(4445678);
        warmShore = new WarmOcean(46)
            .setName("biome.shoreWarm")
            .setClimate(Biome.Temperature.WARM, Biome.Humidity.WET)
            .setDepthAndScale(-1.0F, 0.1F)
            .setColor(176357);
        crag = new Crag(47).setName("biome.crag").setClimate(Biome.Temperature.TEMPERATE, Biome.Humidity.MOIST).setDepthAndScale(-1.8F, 1.8F).setColor(112);
        beach = new Beach(48, Biome.Temperature.TEMPERATE)
            .setName("biome.beach")
            .setClimate(Biome.Temperature.TEMPERATE, Biome.Humidity.TEMPERATE)
            .setDepthAndScale(0.0F, 0.1F)
            .setColor(16440917);
        snowyBeach = new Beach(49, Biome.Temperature.COLD)
            .setName("biome.beachFrozen")
            .setClimate(Biome.Temperature.COLD, Biome.Humidity.DRY)
            .setDepthAndScale(0.0F, 0.1F)
            .enableSnow()
            .setColor(16445632);
        stoneBeach = new Beach(50, null)
            .setName("biome.beachStone")
            .setClimate(Biome.Temperature.TEMPERATE, Biome.Humidity.DRY)
            .setDepthAndScale(0.2F, 0.3F)
            .setColor(16440917);
        duneBeach = new Beach(51, Biome.Temperature.HOT)
            .setName("biome.beachDune")
            .disableRain()
            .setClimate(Biome.Temperature.TEMPERATE, Biome.Humidity.DRY)
            .setDepthAndScale(0.0F, 0.1F)
            .setColor(16197126);
        lushBeach = new Beach(52, Biome.Temperature.WARM)
            .setName("biome.beachLush")
            .setClimate(Biome.Temperature.TEMPERATE, Biome.Humidity.WET)
            .setDepthAndScale(0.0F, 0.1F)
            .setColor(16197126);
        island = new TropicalIsland(53)
            .setName("biome.island")
            .setClimate(Biome.Temperature.TEMPERATE, Biome.Humidity.MOIST)
            .setDepthAndScale(0.5F, 0.6F)
            .setColor(9959386);
        frozenIsland = new PolarIsland(54)
            .setName("biome.polar")
            .setClimate(Biome.Temperature.COLD, Biome.Humidity.MOIST)
            .setDepthAndScale(0.5F, 0.6F)
            .enableSnow()
            .setColor(16777215);
        springFloating = new FloatingIsland(55, Biome.Temperature.TEMPERATE)
            .setName("biome.floating")
            .setClimate(Biome.Temperature.TEMPERATE, Biome.Humidity.MOIST)
            .setDepthAndScale(-1.8F, 0.1F)
            .setColor(9959386);
        winterFloating = new FloatingIsland(56, Biome.Temperature.COLD)
            .setName("biome.floatingCold")
            .setClimate(Biome.Temperature.COLD, Biome.Humidity.MOIST)
            .setDepthAndScale(-1.8F, 0.1F)
            .setColor(9959386);
        autumnFloating = new FloatingIsland(57, Biome.Temperature.HOT)
            .setName("biome.floatingHot")
            .setClimate(Biome.Temperature.HOT, Biome.Humidity.MOIST)
            .setDepthAndScale(-1.8F, 0.1F)
            .setColor(9959386);
        summerFloating = new FloatingIsland(58, Biome.Temperature.WARM)
            .setName("biome.floatingWarm")
            .setClimate(Biome.Temperature.WARM, Biome.Humidity.MOIST)
            .setDepthAndScale(-1.8F, 0.1F)
            .setColor(9959386);
        hell = new HellBiome(59).setName("biome.hell").setDepthAndScale(0.3F, 1.0F).setClimate(Biome.Temperature.HOT, Biome.Humidity.TEMPERATE);
        hellForest = new HellWoods(60).setName("biome.hellForest").setClimate(Biome.Temperature.TEMPERATE, Biome.Humidity.MOIST);
        hellPlains = new HellPlains(61).setName("biome.hellPlains").setClimate(Biome.Temperature.WARM, Biome.Humidity.DRY).setDepthAndScale(0.3F, 0.3F);
        hellSwamp = new HellSwamp(62).setName("biome.hellSwamp").setClimate(Biome.Temperature.HOT, Biome.Humidity.WET).setDepthAndScale(0.0F, 0.1F);
        hellDesert = new HellDesert(63).setName("biome.hellDesert").setClimate(Biome.Temperature.COLD, Biome.Humidity.DRY);
        hellDeltas = new BasaltDeltas(64).setName("biome.basaltDeltas").setClimate(Biome.Temperature.HOT, Biome.Humidity.MOIST).setDepthAndScale(0.4F, 1.8F);
        hellGold = new GoldCaverns(65).setName("biome.gildedHills").setClimate(Biome.Temperature.WARM, Biome.Humidity.TEMPERATE).setDepthAndScale(1.7F, 1.7F);
        hellFrozen = new EchoingAbyss(66).setName("biome.warpedHell").setClimate(Biome.Temperature.COLD, Biome.Humidity.WET).setDepthAndScale(0.5F, 0.6F);
        sky = new SkyBiome(67).setName("biome.sky").setDepthAndScale(0.3F, 1.0F).setClimate(Biome.Temperature.TEMPERATE, Biome.Humidity.TEMPERATE);
        skyTaiga = new SkyTaiga(68, Biome.Temperature.TEMPERATE).setName("biome.skyForest").setClimate(Biome.Temperature.TEMPERATE, Biome.Humidity.TEMPERATE);
        skyTaigaFrozen = new SkyTaiga(69, Biome.Temperature.COLD)
            .setName("biome.skyPeaks")
            .setClimate(Biome.Temperature.COLD, Biome.Humidity.TEMPERATE)
            .enableSnow();
        skySwamp = new SkySwamp(70, Biome.Temperature.TEMPERATE).setName("biome.skySwamp").setClimate(Biome.Temperature.TEMPERATE, Biome.Humidity.WET);
        skyPlains = new SkyPlains(71).setName("biome.skyPlains").setClimate(Biome.Temperature.TEMPERATE, Biome.Humidity.DRY);
        skyGold = new GoldenForest(72).setName("biome.skyGold").setClimate(Biome.Temperature.WARM, Biome.Humidity.DRY);
        skySwampFrozen = new SkySwamp(73, Biome.Temperature.COLD)
            .setName("biome.skyJungle")
            .setClimate(Biome.Temperature.COLD, Biome.Humidity.WET)
            .enableSnow();
        skyMountain = new SkyCliffs(74).setName("biome.skyMesa").setClimate(Biome.Temperature.COLD, Biome.Humidity.TEMPERATE);
        biomeVoid = new VoidBiome(75).setName("biome.void");
        Arrays.fill(enabledBiomes, true);
        LAST_VALID_INDEX = Biome.getLastValidIndex(biomes);
    }
}
