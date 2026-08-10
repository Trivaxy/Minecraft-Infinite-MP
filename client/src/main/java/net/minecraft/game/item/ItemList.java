package net.minecraft.game.item;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.CreatureType;
import net.minecraft.game.item.block.BedItem;
import net.minecraft.game.item.block.DoorItem;
import net.minecraft.game.item.block.FlagItem;
import net.minecraft.game.item.block.PlaceableItem;
import net.minecraft.game.item.block.Seeds;
import net.minecraft.game.item.block.SignItem;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.item.food.FoodItem;
import net.minecraft.game.item.food.MilkBucket;
import net.minecraft.game.item.food.SoupBowl;
import net.minecraft.game.item.misc.ArrowItem;
import net.minecraft.game.item.misc.BoatItem;
import net.minecraft.game.item.misc.Book;
import net.minecraft.game.item.misc.Bowl;
import net.minecraft.game.item.misc.Bucket;
import net.minecraft.game.item.misc.Cape;
import net.minecraft.game.item.misc.Coal;
import net.minecraft.game.item.misc.CrystalItem;
import net.minecraft.game.item.misc.DarkMatter;
import net.minecraft.game.item.misc.DebugStick;
import net.minecraft.game.item.misc.Dye;
import net.minecraft.game.item.misc.EmptyMap;
import net.minecraft.game.item.misc.Eye;
import net.minecraft.game.item.misc.Fertilizer;
import net.minecraft.game.item.misc.Gem;
import net.minecraft.game.item.misc.GlowstickItem;
import net.minecraft.game.item.misc.Horn;
import net.minecraft.game.item.misc.Ingot;
import net.minecraft.game.item.misc.Ingredient;
import net.minecraft.game.item.misc.ItemFrame;
import net.minecraft.game.item.misc.Key;
import net.minecraft.game.item.misc.LifeShard;
import net.minecraft.game.item.misc.LightGem;
import net.minecraft.game.item.misc.MapItem;
import net.minecraft.game.item.misc.MinecartItem;
import net.minecraft.game.item.misc.Nametag;
import net.minecraft.game.item.misc.OreChunk;
import net.minecraft.game.item.misc.PaintingItem;
import net.minecraft.game.item.misc.Pouch;
import net.minecraft.game.item.misc.RecipeBook;
import net.minecraft.game.item.misc.Record;
import net.minecraft.game.item.misc.Scrap;
import net.minecraft.game.item.misc.Scroll;
import net.minecraft.game.item.misc.SpawnEgg;
import net.minecraft.game.item.misc.ThrowableItem;
import net.minecraft.game.item.misc.WolfArmor;
import net.minecraft.game.item.misc.WrittenBook;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.item.potion.Potion;
import net.minecraft.game.item.staff.CloudStaff;
import net.minecraft.game.item.staff.GabrielStaff;
import net.minecraft.game.item.staff.HealingStaff;
import net.minecraft.game.item.staff.ImpStaff;
import net.minecraft.game.item.staff.LichStaff;
import net.minecraft.game.item.staff.NatureStaff;
import net.minecraft.game.item.staff.PharaohStaff;
import net.minecraft.game.item.staff.SnowmanStaff;
import net.minecraft.game.item.tools.Armor;
import net.minecraft.game.item.tools.ArmorMaterial;
import net.minecraft.game.item.tools.Axe;
import net.minecraft.game.item.tools.Brush;
import net.minecraft.game.item.tools.Charm;
import net.minecraft.game.item.tools.FireStriker;
import net.minecraft.game.item.tools.FishingRod;
import net.minecraft.game.item.tools.GrapplingHook;
import net.minecraft.game.item.tools.Hoe;
import net.minecraft.game.item.tools.MagicMirror;
import net.minecraft.game.item.tools.Pickaxe;
import net.minecraft.game.item.tools.Screwdriver;
import net.minecraft.game.item.tools.Shears;
import net.minecraft.game.item.tools.Shovel;
import net.minecraft.game.item.tools.ToolMaterial;
import net.minecraft.game.item.weapons.Bow;
import net.minecraft.game.item.weapons.Crossbow;
import net.minecraft.game.item.weapons.Musket;
import net.minecraft.game.item.weapons.RaphaelRing;
import net.minecraft.game.item.weapons.Scythe;
import net.minecraft.game.item.weapons.Spear;
import net.minecraft.game.item.weapons.Sword;
import net.minecraft.game.item.weapons.TridentItem;
import net.minecraft.game.item.weapons.Whip;
import net.minecraft.statistics.StatList;

public class ItemList {
    public static final Item[] items = new Item[10240];
    public static final Item shovelSteel = new Shovel(0, ToolMaterial.IRON).setInternalName("shovelIron").setCreativeTab(CreativeTabs.tabTools);
    public static final Item pickaxeSteel = new Pickaxe(1, ToolMaterial.IRON).setInternalName("pickaxeIron").setCreativeTab(CreativeTabs.tabTools);
    public static final Item axeSteel = new Axe(2, ToolMaterial.IRON).setInternalName("hatchetIron").setCreativeTab(CreativeTabs.tabTools);
    public static final Item striker = new FireStriker(3).setInternalName("flintAndSteel").setCreativeTab(CreativeTabs.tabTools);
    public static final Item appleRed = new FoodItem(4, 4, 0.3F).setCompostable(true).setInternalName("apple").setCreativeTab(CreativeTabs.tabFood);
    public static final Item bow = new Bow(5).setInternalName("bow").setCreativeTab(CreativeTabs.tabCombat);
    public static final Item arrow = new ArrowItem(6, true)
        .setInternalName("arrow")
        .setItemDescription("8 DMG")
        .setItemDescription("item.desc.bolt")
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item coal = new Coal(7).setInternalName("coal").setCreativeTab(CreativeTabs.tabMaterials);
    public static final Item diamond = new Gem(8).setInternalName("diamond").setRarity(Rarity.RARE).setCreativeTab(CreativeTabs.tabMaterials);
    public static final Item ingotIron = new Ingot(9).setInternalName("ingotIron").setCreativeTab(CreativeTabs.tabMaterials);
    public static final Item ingotGold = new Ingot(10).setInternalName("ingotGold").setCreativeTab(CreativeTabs.tabMaterials);
    public static final Item swordSteel = new Sword(11, ToolMaterial.IRON).setInternalName("swordIron").setCreativeTab(CreativeTabs.tabCombat);
    public static final Item lifeShard = new LifeShard(12)
        .setInternalName("lifeShard")
        .setItemDescription("item.desc.lifeShard")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item quiver = new Charm(13, 4, 1).setInternalName("quiver").setCreativeTab(CreativeTabs.tabCombat);
    public static final Item bowlSussy = new SoupBowl(14, 10, 1.0F)
        .setInternalName("susStew")
        .setItemDescription("item.desc.sus")
        .setCreativeTab(CreativeTabs.tabFood);
    public static final Item spyGlass = new Charm(15, 2, 3).setInternalName("telescope").setMaxStackSize(1).setCreativeTab(CreativeTabs.tabTools);
    public static final Item swordStone = new Sword(16, ToolMaterial.STONE).setInternalName("swordStone").setCreativeTab(CreativeTabs.tabCombat);
    public static final Item shovelStone = new Shovel(17, ToolMaterial.STONE).setInternalName("shovelStone").setCreativeTab(CreativeTabs.tabTools);
    public static final Item pickaxeStone = new Pickaxe(18, ToolMaterial.STONE).setInternalName("pickaxeStone").setCreativeTab(CreativeTabs.tabTools);
    public static final Item axeStone = new Axe(19, ToolMaterial.STONE).setInternalName("hatchetStone").setCreativeTab(CreativeTabs.tabTools);
    public static final Item swordDiamond = new Sword(20, ToolMaterial.DIAMOND)
        .setInternalName("swordDiamond")
        .setRarity(Rarity.RARE)
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item shovelDiamond = new Shovel(21, ToolMaterial.DIAMOND)
        .setInternalName("shovelDiamond")
        .setRarity(Rarity.RARE)
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item pickaxeDiamond = new Pickaxe(22, ToolMaterial.DIAMOND)
        .setInternalName("pickaxeDiamond")
        .setRarity(Rarity.RARE)
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item axeDiamond = new Axe(23, ToolMaterial.DIAMOND)
        .setInternalName("hatchetDiamond")
        .setRarity(Rarity.RARE)
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item stick = new Item(24).setInternalName("stick").setCreativeTab(CreativeTabs.tabMaterials);
    public static final Item bowlEmpty = new Bowl(25).setInternalName("bowl").setCreativeTab(CreativeTabs.tabMaterials);
    public static final Item bowlSoup = new SoupBowl(26, 10, 1.0F).setInternalName("mushroomStew").setCreativeTab(CreativeTabs.tabFood);
    public static final Item swordGold = new Sword(27, ToolMaterial.GOLD).setInternalName("swordGold").setCreativeTab(CreativeTabs.tabCombat);
    public static final Item shovelGold = new Shovel(28, ToolMaterial.GOLD).setInternalName("shovelGold").setCreativeTab(CreativeTabs.tabTools);
    public static final Item pickaxeGold = new Pickaxe(29, ToolMaterial.GOLD).setInternalName("pickaxeGold").setCreativeTab(CreativeTabs.tabTools);
    public static final Item axeGold = new Axe(30, ToolMaterial.GOLD).setInternalName("hatchetGold").setCreativeTab(CreativeTabs.tabTools);
    public static final Item silk = new Item(31).setInternalName("string").setCreativeTab(CreativeTabs.tabMaterials);
    public static final Item feather = new Item(32).setInternalName("feather").setCreativeTab(CreativeTabs.tabMaterials);
    public static final Item gunpowder = new Item(33).setInternalName("sulphur").setCreativeTab(CreativeTabs.tabMaterials);
    public static final Item rock = new ThrowableItem(34, 4).setInternalName("pebble").setCreativeTab(CreativeTabs.tabMaterials);
    public static final Item hoeStone = new Hoe(35, ToolMaterial.STONE).setInternalName("hoeStone").setCreativeTab(CreativeTabs.tabTools);
    public static final Item hoeSteel = new Hoe(36, ToolMaterial.IRON).setInternalName("hoeIron").setCreativeTab(CreativeTabs.tabTools);
    public static final Item hoeDiamond = new Hoe(37, ToolMaterial.DIAMOND)
        .setInternalName("hoeDiamond")
        .setRarity(Rarity.RARE)
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item hoeGold = new Hoe(38, ToolMaterial.GOLD).setInternalName("hoeGold").setCreativeTab(CreativeTabs.tabTools);
    public static final Item seeds = new Seeds(39, BlockList.crops.id).setInternalName("seeds").setCompostable(true).setCreativeTab(CreativeTabs.tabNature);
    public static final Item wheat = new Item(40).setInternalName("wheat").setCompostable(true).setCreativeTab(CreativeTabs.tabMaterials);
    public static final Item bread = new FoodItem(41, 5, 0.6F)
        .setPotionEffect(EffectList.haste.id, 300, 0)
        .setInternalName("bread")
        .setCreativeTab(CreativeTabs.tabFood);
    public static final Item helmetLeather = new Armor(42, ArmorMaterial.CLOTH, 0, 0).setInternalName("helmetCloth").setCreativeTab(CreativeTabs.tabCombat);
    public static final Item plateLeather = new Armor(43, ArmorMaterial.CLOTH, 0, 1).setInternalName("chestplateCloth").setCreativeTab(CreativeTabs.tabCombat);
    public static final Item legsLeather = new Armor(44, ArmorMaterial.CLOTH, 0, 2).setInternalName("leggingsCloth").setCreativeTab(CreativeTabs.tabCombat);
    public static final Item bootsLeather = new Armor(45, ArmorMaterial.CLOTH, 0, 3).setInternalName("bootsCloth").setCreativeTab(CreativeTabs.tabCombat);
    public static final Item helmetChain = new Armor(46, ArmorMaterial.CHAIN, 1, 0).setInternalName("helmetChain").setCreativeTab(CreativeTabs.tabCombat);
    public static final Item plateChain = new Armor(47, ArmorMaterial.CHAIN, 1, 1).setInternalName("chestplateChain").setCreativeTab(CreativeTabs.tabCombat);
    public static final Item legsChain = new Armor(48, ArmorMaterial.CHAIN, 1, 2).setInternalName("leggingsChain").setCreativeTab(CreativeTabs.tabCombat);
    public static final Item bootsChain = new Armor(49, ArmorMaterial.CHAIN, 1, 3).setInternalName("bootsChain").setCreativeTab(CreativeTabs.tabCombat);
    public static final Item helmetSteel = new Armor(50, ArmorMaterial.IRON, 2, 0).setInternalName("helmetIron").setCreativeTab(CreativeTabs.tabCombat);
    public static final Item plateSteel = new Armor(51, ArmorMaterial.IRON, 2, 1).setInternalName("chestplateIron").setCreativeTab(CreativeTabs.tabCombat);
    public static final Item legsSteel = new Armor(52, ArmorMaterial.IRON, 2, 2).setInternalName("leggingsIron").setCreativeTab(CreativeTabs.tabCombat);
    public static final Item bootsSteel = new Armor(53, ArmorMaterial.IRON, 2, 3).setInternalName("bootsIron").setCreativeTab(CreativeTabs.tabCombat);
    public static final Item helmetDiamond = new Armor(54, ArmorMaterial.DIAMOND, 3, 0)
        .setInternalName("helmetDiamond")
        .setRarity(Rarity.RARE)
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item plateDiamond = new Armor(55, ArmorMaterial.DIAMOND, 3, 1)
        .setInternalName("chestplateDiamond")
        .setRarity(Rarity.RARE)
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item legsDiamond = new Armor(56, ArmorMaterial.DIAMOND, 3, 2)
        .setInternalName("leggingsDiamond")
        .setRarity(Rarity.RARE)
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item bootsDiamond = new Armor(57, ArmorMaterial.DIAMOND, 3, 3)
        .setInternalName("bootsDiamond")
        .setRarity(Rarity.RARE)
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item helmetGold = new Armor(58, ArmorMaterial.GOLD, 4, 0).setInternalName("helmetGold").setCreativeTab(CreativeTabs.tabCombat);
    public static final Item plateGold = new Armor(59, ArmorMaterial.GOLD, 4, 1).setInternalName("chestplateGold").setCreativeTab(CreativeTabs.tabCombat);
    public static final Item legsGold = new Armor(60, ArmorMaterial.GOLD, 4, 2).setInternalName("leggingsGold").setCreativeTab(CreativeTabs.tabCombat);
    public static final Item bootsGold = new Armor(61, ArmorMaterial.GOLD, 4, 3).setInternalName("bootsGold").setCreativeTab(CreativeTabs.tabCombat);
    public static final Item flint = new Item(62).setInternalName("flint").setCreativeTab(CreativeTabs.tabMaterials);
    public static final Item porkRaw = new FoodItem(63, 3, 0.3F)
        .setPotionEffect(EffectList.hunger.id, 200, 0)
        .setInternalName("porkchopRaw")
        .setCreativeTab(CreativeTabs.tabFood);
    public static final Item porkCooked = new FoodItem(64, 8, 0.8F)
        .setPotionEffect(EffectList.strength.id, 200, 0)
        .setInternalName("porkchopCooked")
        .setCreativeTab(CreativeTabs.tabFood);
    public static final Item painting = new PaintingItem(65).setInternalName("painting").setCreativeTab(CreativeTabs.tabDecoration);
    public static final Item appleGold = new FoodItem(66, 20, 1.0F)
        .setAlwaysEdible()
        .setInternalName("appleGold")
        .setRarity(Rarity.RARE)
        .setItemDescription("potion.absorption")
        .setItemDescription("potion.regeneration")
        .setItemDescription("potion.resistance")
        .setMaxStackSize(1)
        .setCreativeTab(CreativeTabs.tabFood);
    public static final Item sign = new SignItem(67).setInternalName("sign").setCreativeTab(CreativeTabs.tabDecoration);
    public static final Item doorWood = new DoorItem(68, BlockList.doorWood).setInternalName("doorWood").setCreativeTab(CreativeTabs.tabDecoration);
    public static final Item bucketEmpty = new Bucket(69, 0).setInternalName("bucket").setCreativeTab(CreativeTabs.tabTools);
    public static final Item bucketWater = new Bucket(70, BlockList.waterMoving.id)
        .setMaxStackSize(1)
        .setInternalName("bucketWater")
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item bucketLava = new Bucket(71, BlockList.lavaMoving.id)
        .setMaxStackSize(1)
        .setInternalName("bucketLava")
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item minecart = new MinecartItem(72, 0).setInternalName("minecart").setCreativeTab(CreativeTabs.tabTools);
    public static final Item saddle = new Item(73).setInternalName("saddle").setPriorityItem().setMaxStackSize(1).setCreativeTab(CreativeTabs.tabTools);
    public static final Item spawnEgg = new SpawnEgg(74).setInternalName("spawnEgg").setCreativeTab(CreativeTabs.tabMisc);
    public static final Item leather = new Item(75).setInternalName("leather").setCreativeTab(CreativeTabs.tabMaterials);
    public static final Item bone = new Item(76).setInternalName("bone").setCompostable(true).setCreativeTab(CreativeTabs.tabMaterials);
    public static final Item dyePowder = new Dye(77).setInternalName("dye_0").setCreativeTab(CreativeTabs.tabMaterials);
    public static final Item clay = new Item(78).setInternalName("clay").setCreativeTab(CreativeTabs.tabMaterials);
    public static final Item brick = new Item(79).setInternalName("brick").setCreativeTab(CreativeTabs.tabMaterials);
    public static final Item paper = new Item(80).setInternalName("paper").setCreativeTab(CreativeTabs.tabMaterials);
    public static final Item book = new Item(81).setInternalName("book").setCreativeTab(CreativeTabs.tabMaterials);
    public static final Item reeds = new PlaceableItem(82, BlockList.reeds)
        .setCompostable(true)
        .setInternalName("reeds")
        .setCreativeTab(CreativeTabs.tabNature);
    public static final Item minecartChest = new MinecartItem(83, 1).setInternalName("minecartStorage").setCreativeTab(CreativeTabs.tabTools);
    public static final Item minecartFurnace = new MinecartItem(84, 2).setInternalName("minecartFurnace").setCreativeTab(CreativeTabs.tabTools);
    public static final Item minecartExplosive = new MinecartItem(85, 3).setInternalName("minecartExplosive").setCreativeTab(CreativeTabs.tabTools);
    public static final Item rottenFlesh = new FoodItem(86, 5, 0.1F)
        .setPotionEffect(EffectList.poison.id, 100, 0)
        .setCompostable(true)
        .setInternalName("rottenFlesh")
        .setCreativeTab(CreativeTabs.tabFood);
    public static final Item doorBirch = new DoorItem(87, BlockList.doorBirch).setInternalName("doorBirch").setCreativeTab(CreativeTabs.tabDecoration);
    public static final Item doorSpruce = new DoorItem(88, BlockList.doorSpruce).setInternalName("doorSpruce").setCreativeTab(CreativeTabs.tabDecoration);
    public static final Item doorPalm = new DoorItem(89, BlockList.doorPalm).setInternalName("doorPalm").setCreativeTab(CreativeTabs.tabDecoration);
    public static final Item snowball = new ThrowableItem(90, 0).setInternalName("snowBall").setCreativeTab(CreativeTabs.tabMaterials);
    public static final Item berry = new FoodItem(91, 1, 0.1F).setCompostable(true).setInternalName("berry").setCreativeTab(CreativeTabs.tabFood);
    public static final Item doorAcacia = new DoorItem(92, BlockList.doorAcacia).setInternalName("doorAcacia").setCreativeTab(CreativeTabs.tabDecoration);
    public static final Item doorJungle = new DoorItem(93, BlockList.doorJungle).setInternalName("doorJungle").setCreativeTab(CreativeTabs.tabDecoration);
    public static final Item doorMangrove = new DoorItem(94, BlockList.doorMangrove).setInternalName("doorMangrove").setCreativeTab(CreativeTabs.tabDecoration);
    public static final Item doorBaobab = new DoorItem(95, BlockList.doorBaobab).setInternalName("doorBaobab").setCreativeTab(CreativeTabs.tabDecoration);
    public static final Item melonSlice = new FoodItem(96, 2, 0.2F).setCompostable(true).setInternalName("melon").setCreativeTab(CreativeTabs.tabFood);
    public static final Item seedsMelon = new Seeds(97, BlockList.melonStem.id)
        .setCompostable(true)
        .setInternalName("seedsMelon")
        .setCreativeTab(CreativeTabs.tabNature);
    public static final Item seedsPumpkin = new Seeds(98, BlockList.pumpkinStem.id)
        .setCompostable(true)
        .setInternalName("seedsPumpkin")
        .setCreativeTab(CreativeTabs.tabNature);
    public static final Item bedRed = new BedItem(99, BlockList.bed).setInternalName("bed").setCreativeTab(CreativeTabs.tabColor);
    public static final Item flowerPot = new PlaceableItem(100, BlockList.flowerPot).setInternalName("flowerPot").setCreativeTab(CreativeTabs.tabDecoration);
    public static final Item cake = new PlaceableItem(101, BlockList.cake).setInternalName("pie").setMaxStackSize(1).setCreativeTab(CreativeTabs.tabFood);
    public static final Item slimeBall = new Ingredient(102)
        .setInternalName("slimeball")
        .setCreativeTab(CreativeTabs.tabMaterials)
        .setPotionPrefix(8, "+6+1!14");
    public static final Item recordGold = new Record(103, "Cobble Man", "Soybean").setInternalName("recordGold").setCreativeTab(CreativeTabs.tabTools);
    public static final Item recordGreen = new Record(104, "Ruins", "Soybean").setInternalName("recordGreen").setCreativeTab(CreativeTabs.tabTools);
    public static final Item recordRed = new Record(105, "Home", "Soybean").setInternalName("recordRed").setCreativeTab(CreativeTabs.tabTools);
    public static final Item recordTeal = new Record(106, "Jank Zone", "Soybean").setInternalName("recordTeal").setCreativeTab(CreativeTabs.tabTools);
    public static final Item recordPurple = new Record(107, "Fearless", "Soybean").setInternalName("recordPurple").setCreativeTab(CreativeTabs.tabTools);
    public static final Item recordCactus = new Record(108, "Magnetic Circuit", "Notch").setInternalName("recordCactus").setCreativeTab(CreativeTabs.tabTools);
    public static final Item itemFrame = new ItemFrame(109).setInternalName("itemFrame").setCreativeTab(CreativeTabs.tabDecoration);
    public static final Item doorSteel = new DoorItem(110, BlockList.doorSteel).setInternalName("doorSteel").setCreativeTab(CreativeTabs.tabMachines);
    public static final Item boat = new BoatItem(111).setInternalName("boat").setCreativeTab(CreativeTabs.tabTools);
    public static final Item fishingRod = new FishingRod(112).setInternalName("fishingRod").setCreativeTab(CreativeTabs.tabTools);
    public static final Item fishRaw = new FoodItem(113, 2, 0.3F)
        .setPotionEffect(EffectList.hunger.id, 200, 0)
        .setInternalName("fishRaw")
        .setCreativeTab(CreativeTabs.tabFood);
    public static final Item fishCooked = new FoodItem(114, 5, 0.6F)
        .setPotionEffect(EffectList.waterBreathing.id, 300, 0)
        .setInternalName("fishCooked")
        .setCreativeTab(CreativeTabs.tabFood);
    public static final Item compass = new Item(115).setInternalName("compass").setMaxStackSize(1).setCreativeTab(CreativeTabs.tabTools);
    public static final Item clock = new Item(116).setInternalName("clock").setMaxStackSize(1).setCreativeTab(CreativeTabs.tabTools);
    public static final EmptyMap mapEmpty = (EmptyMap)new EmptyMap(117).setInternalName("emptyMap").setCreativeTab(CreativeTabs.tabTools);
    public static final MapItem map = (MapItem)new MapItem(118).setInternalName("map");
    public static final Item bookAndQuill = new Book(119).setInternalName("writableBook").setCreativeTab(CreativeTabs.tabTools);
    public static final Item writtenBook = new WrittenBook(120).setInternalName("writtenBook");
    public static final Crossbow crossbow = (Crossbow)new Crossbow(121).setInternalName("crossbow").setCreativeTab(CreativeTabs.tabCombat);
    public static final Item arrowFlint = new ArrowItem(122, false)
        .setInternalName("flintArrow")
        .setItemDescription("4 DMG")
        .setItemDescription("item.desc.arrow")
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item recordOrange = new Record(123, "Hero's Journey", "Soybean").setInternalName("recordOrange").setCreativeTab(CreativeTabs.tabTools);
    public static final Item doorGlass = new DoorItem(124, BlockList.doorGlass).setInternalName("doorGlass").setCreativeTab(CreativeTabs.tabDecoration);
    public static final Item recordHell = new Record(125, "Mytheria", "Soybean").setInternalName("recordHell").setCreativeTab(CreativeTabs.tabTools);
    public static final Item recordSky = new Record(126, "Valley of Spirits", "Soybean").setInternalName("recordSky").setCreativeTab(CreativeTabs.tabTools);
    public static final Item recordBlue = new Record(127, "The Traveler", "Soybean").setInternalName("recordBlue").setCreativeTab(CreativeTabs.tabTools);
    public static final Item recordWhite = new Record(128, "Snowshoe", "Soybean").setInternalName("recordWhite").setCreativeTab(CreativeTabs.tabTools);
    public static final Item pumpkinPie = new FoodItem(129, 10, 0.8F)
        .setPotionEffect(EffectList.swiftness.id, 200, 0)
        .setInternalName("pumpkinPie")
        .setCreativeTab(CreativeTabs.tabFood);
    public static final Item carrot = new FoodItem(130, 3, 0.3F).setCompostable(true).setInternalName("carrot").setCreativeTab(CreativeTabs.tabFood);
    public static final Item potato = new FoodItem(131, 2, 0.2F).setCompostable(true).setInternalName("potato").setCreativeTab(CreativeTabs.tabFood);
    public static final Item bakedPotato = new FoodItem(132, 6, 0.4F)
        .setPotionEffect(EffectList.resistance.id, 150, 0)
        .setInternalName("potatoBaked")
        .setCreativeTab(CreativeTabs.tabFood);
    public static final Item seedsCotton = new Seeds(133, BlockList.cotton.id)
        .setCompostable(true)
        .setInternalName("seedsCotton")
        .setCreativeTab(CreativeTabs.tabNature);
    public static final Item cotton = new Item(134).setInternalName("cotton").setCreativeTab(CreativeTabs.tabMaterials);
    public static final Item ash = new Item(135).setInternalName("ashes").setCreativeTab(CreativeTabs.tabMaterials);
    public static final Item fertilizer = new Fertilizer(136).setInternalName("fertilizer").setCreativeTab(CreativeTabs.tabMaterials);
    public static final Item doorScorched = new DoorItem(137, BlockList.doorScorched)
        .setInternalName("doorScorched")
        .setCreativeTab(CreativeTabs.tabDecoration);
    public static final Item brush = new Brush(138).setInternalName("brush").setMaxStackSize(1).setCreativeTab(CreativeTabs.tabTools);
    public static final Item doorMaple = new DoorItem(139, BlockList.doorMaple).setInternalName("doorMaple").setCreativeTab(CreativeTabs.tabDecoration);
    public static final Item doorCherry = new DoorItem(140, BlockList.doorCherry).setInternalName("doorCherry").setCreativeTab(CreativeTabs.tabDecoration);
    public static final Item bamboo = new PlaceableItem(141, BlockList.bamboo)
        .setCompostable(true)
        .setInternalName("bambooStick")
        .setCreativeTab(CreativeTabs.tabNature);
    public static final Item doorMahogany = new DoorItem(142, BlockList.doorMahogany)
        .setInternalName("doorMahogany")
        .setCreativeTab(CreativeTabs.tabDecoration);
    public static final Item doorOldenOak = new DoorItem(143, BlockList.doorOldenOak)
        .setInternalName("doorOldenOak")
        .setCreativeTab(CreativeTabs.tabDecoration);
    public static final Item topaz = new Gem(144).setInternalName("topaz").setRarity(Rarity.EPIC).setCreativeTab(CreativeTabs.tabMaterials);
    public static final Item arrowLightning = new ArrowItem(145, false)
        .setInternalName("arrowZeus")
        .setRarity(Rarity.RARE)
        .setItemDescription("4 DMG")
        .setItemDescription("item.desc.arrow")
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item raft = new BoatItem(146).setInternalName("raft").setCreativeTab(CreativeTabs.tabTools);
    public static final Item tentacle = new Ingredient(147).setInternalName("tentacle").setCreativeTab(CreativeTabs.tabMaterials).setPotionPrefix(6, "+9!6+14");
    public static final Item pincer = new Ingredient(148).setInternalName("pincer").setCreativeTab(CreativeTabs.tabMaterials).setPotionPrefix(9, "+9!14-3");
    public static final Item extract = new Ingredient(149).setInternalName("extract").setCreativeTab(CreativeTabs.tabMaterials).setPotionPrefix(17, "+18!9-1");
    public static final Item glodust = new Ingredient(150).setInternalName("glodust").setCreativeTab(CreativeTabs.tabMaterials).setPotionPrefix(7, "+0!7+14");
    public static final Item muttonRaw = new FoodItem(151, 3, 0.3F)
        .setPotionEffect(EffectList.hunger.id, 200, 0)
        .setInternalName("muttonRaw")
        .setCreativeTab(CreativeTabs.tabFood);
    public static final Item muttonCooked = new FoodItem(152, 8, 0.8F)
        .setPotionEffect(EffectList.strength.id, 200, 0)
        .setInternalName("muttonCooked")
        .setCreativeTab(CreativeTabs.tabFood);
    public static final Item horn = new Ingredient(153).setInternalName("horn").setCreativeTab(CreativeTabs.tabMaterials).setPotionPrefix(4, "+4!14+2");
    public static final Item chimkenRaw = new FoodItem(154, 2, 0.2F)
        .setPotionEffect(EffectList.hunger.id, 200, 0)
        .setInternalName("chimkenRaw")
        .setCreativeTab(CreativeTabs.tabFood);
    public static final Item chimkenCooked = new FoodItem(155, 6, 0.6F)
        .setPotionEffect(EffectList.strength.id, 200, 0)
        .setInternalName("chimkenCooked")
        .setCreativeTab(CreativeTabs.tabFood);
    public static final Item rabbitRaw = new FoodItem(156, 2, 0.2F)
        .setPotionEffect(EffectList.hunger.id, 200, 0)
        .setInternalName("rabbitRaw")
        .setCreativeTab(CreativeTabs.tabFood);
    public static final Item rabbitCooked = new FoodItem(157, 6, 0.6F)
        .setPotionEffect(EffectList.jump.id, 300, 0)
        .setInternalName("rabbitCooked")
        .setCreativeTab(CreativeTabs.tabFood);
    public static final Item bedOrange = new BedItem(158, BlockList.bedOrange).setInternalName("bed_orange").setCreativeTab(CreativeTabs.tabColor);
    public static final Item nuggetGold = new Item(159).setInternalName("goldNugget").setCreativeTab(CreativeTabs.tabMaterials);
    public static final Item nuggetIron = new Item(160).setInternalName("ironNugget").setCreativeTab(CreativeTabs.tabMaterials);
    public static final Item wolfArmor = new WolfArmor(161).setInternalName("wolfArmor").setMaxStackSize(1).setCreativeTab(CreativeTabs.tabTools);
    public static final Item horseArmor = new Item(162).setInternalName("horseArmor").setMaxStackSize(1).setCreativeTab(CreativeTabs.tabTools);
    public static final Item paw = new Ingredient(163).setInternalName("paw").setCreativeTab(CreativeTabs.tabMaterials).setPotionPrefix(14, "+3!4-13");
    public static final Item ostrichFeather = new Ingredient(164)
        .setInternalName("leg")
        .setCreativeTab(CreativeTabs.tabMaterials)
        .setPotionPrefix(3, "+14!3-2");
    public static final Item tongue = new Ingredient(165).setInternalName("tongue").setCreativeTab(CreativeTabs.tabMaterials).setPotionPrefix(13, "+11!8+13");
    public static final Item shell = new Ingredient(166).setInternalName("shell").setCreativeTab(CreativeTabs.tabMaterials).setPotionPrefix(12, "+5-12!14");
    public static final Item teeth = new Ingredient(167).setInternalName("teeth").setCreativeTab(CreativeTabs.tabMaterials).setPotionPrefix(16, "+14!4-1");
    public static final Item beak = new Ingredient(168).setInternalName("beak").setCreativeTab(CreativeTabs.tabMaterials).setPotionPrefix(11, "+0+8!14");
    public static final Item claw = new Ingredient(169).setInternalName("claw").setCreativeTab(CreativeTabs.tabMaterials).setPotionPrefix(1, "+12!8+1");
    public static final Item ruby = new Gem(170).setInternalName("ruby").setRarity(Rarity.RARE).setCreativeTab(CreativeTabs.tabMaterials);
    public static final Item sapphire = new Gem(171).setInternalName("sapphire").setRarity(Rarity.RARE).setCreativeTab(CreativeTabs.tabMaterials);
    public static final Item emerald = new Gem(172).setInternalName("emerald").setRarity(Rarity.RARE).setCreativeTab(CreativeTabs.tabMaterials);
    public static final Item arrowRope = new ArrowItem(173, false)
        .setInternalName("ropeArrow")
        .setItemDescription("4 DMG")
        .setItemDescription("item.desc.arrow")
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item nameTag = new Nametag(174).setMaxStackSize(1).setInternalName("nameTag").setCreativeTab(CreativeTabs.tabTools);
    public static final Item glassBottle = new Item(175).setInternalName("glassBottle").setCreativeTab(CreativeTabs.tabMaterials);
    public static final Potion potion = (Potion)new Potion(176, false).setMaxStackSize(1).setInternalName("potion").setCreativeTab(CreativeTabs.tabFood);
    public static final Item cauldron = new PlaceableItem(177, BlockList.cauldron).setInternalName("cauldron").setCreativeTab(CreativeTabs.tabDecoration);
    public static final Potion splashPotion = (Potion)new Potion(178, true).setMaxStackSize(1).setInternalName("potion").setCreativeTab(CreativeTabs.tabFood);
    public static final Item bucketMilk = new MilkBucket(179).setInternalName("milk").setCreativeTab(CreativeTabs.tabFood);
    public static final Item rawIron = new OreChunk(180).setInternalName("rawIron").setCreativeTab(CreativeTabs.tabMaterials);
    public static final Item rawGold = new OreChunk(181).setInternalName("rawGold").setCreativeTab(CreativeTabs.tabMaterials);
    public static final Item eye = new Ingredient(182).setInternalName("eye").setCreativeTab(CreativeTabs.tabMaterials).setPotionPrefix(15, "+14!8-5");
    public static final Item recordBlack = new Record(183, "Tunnel Vision", "Soybean").setInternalName("recordBlack").setCreativeTab(CreativeTabs.tabTools);
    public static final Item arrowFlame = new ArrowItem(184, false)
        .setInternalName("arrowFire")
        .setItemDescription("4 DMG")
        .setRarity(Rarity.RARE)
        .setItemDescription("item.desc.arrow")
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item arrowFrost = new ArrowItem(185, false)
        .setInternalName("arrowFrost")
        .setItemDescription("4 DMG")
        .setRarity(Rarity.RARE)
        .setItemDescription("item.desc.arrow")
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item arrowPoison = new ArrowItem(186, false)
        .setInternalName("arrowPoison")
        .setItemDescription("4 DMG")
        .setRarity(Rarity.RARE)
        .setItemDescription("item.desc.arrow")
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item arrowExplosive = new ArrowItem(187, true)
        .setInternalName("arrowExplosive")
        .setItemDescription("8 DMG")
        .setItemDescription("item.desc.bolt")
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item arrowSpawn = new ArrowItem(188, false)
        .setInternalName("arrowSpawn")
        .setItemDescription("4 DMG")
        .setRarity(Rarity.RARE)
        .setItemDescription("item.desc.arrow")
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item coin = new Charm(189, 3, 3, 256)
        .setReduceAmount(5, 4)
        .setInternalName("coin")
        .setItemDescription("item.desc.fortune")
        .setItemDescription("item.desc.looting")
        .setItemDescription("item.desc.midas")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item shield = new Charm(190, 1, 2, 256)
        .setReduceAmount(0, 6)
        .setInternalName("shield")
        .setItemDescription("item.desc.explosion")
        .setItemDescription("item.desc.deflect")
        .setItemDescription("item.desc.ancient")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item leg = new Ingredient(191).setInternalName("birdLeg").setCreativeTab(CreativeTabs.tabMaterials).setPotionPrefix(18, "+14-11+3");
    public static final Item bracelet = new Charm(192, 1, 3, 1024)
        .setReduceAmount(0, 3)
        .setInternalName("pendant")
        .setItemDescription("item.desc.unbreaking")
        .setItemDescription("item.desc.ancient")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item mirror = new MagicMirror(193)
        .setInternalName("mirror")
        .setReduceAmount(0, 4)
        .setItemDescription("item.desc.respawn")
        .setItemDescription("item.desc.ancient")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item gloves = new Charm(194, 0, 3, 256)
        .setInternalName("gloves")
        .setReduceAmount(0, 2)
        .setItemDescription("item.desc.climb")
        .setItemDescription("item.desc.unarmed")
        .setItemDescription("item.desc.ancient")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item swordSpider = new Sword(195, ToolMaterial.RUBY, CreatureType.ARTHROPOD)
        .setReduceAmount(0, 1)
        .setInternalName("swordSpider")
        .setItemDescription("item.desc.ancient")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item swordPoison = new Sword(196, ToolMaterial.EMERALD)
        .setReduceAmount(2, 2)
        .setInternalName("swordLeaf")
        .setItemDescription("item.desc.poison")
        .setItemDescription("item.desc.nature")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item swordFrost = new Sword(197, ToolMaterial.SAPPHIRE)
        .setReduceAmount(3, 2)
        .setInternalName("swordIce")
        .setItemDescription("item.desc.ice")
        .setItemDescription("item.desc.lich")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item natureStaff = new NatureStaff(198)
        .setInternalName("natureStaff")
        .setReduceAmount(2, 2)
        .setItemDescription("item.desc.nature")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item necklace = new Charm(199, 2, 0, 256)
        .setInternalName("necklace")
        .setReduceAmount(3, 1)
        .setItemDescription("item.desc.thunder")
        .setItemDescription("item.desc.tide")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item ring = new Charm(200, 2, 3)
        .setInternalName("ring")
        .setReduceAmount(3, 1)
        .setItemDescription("item.desc.frost")
        .setItemDescription("item.desc.lich")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item helmetThorns = new Armor(201, ArmorMaterial.EMERALD, 6, 0)
        .setReduceAmount(2, 5)
        .setInternalName("thornsHelmet")
        .setItemDescription("item.desc.thorns")
        .setItemDescription("item.desc.nature")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item plateThorns = new Armor(202, ArmorMaterial.EMERALD, 6, 1)
        .setReduceAmount(2, 8)
        .setInternalName("thornsChestplate")
        .setItemDescription("item.desc.thorns")
        .setItemDescription("item.desc.nature")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item legsThorns = new Armor(203, ArmorMaterial.EMERALD, 6, 2)
        .setReduceAmount(2, 7)
        .setInternalName("thornsLeggings")
        .setItemDescription("item.desc.thorns")
        .setItemDescription("item.desc.nature")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item bootsThorns = new Armor(204, ArmorMaterial.EMERALD, 6, 3)
        .setReduceAmount(2, 4)
        .setInternalName("thornsBoots")
        .setItemDescription("item.desc.thorns")
        .setItemDescription("item.desc.nature")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item crown = new Charm(205, 0, 0, 256)
        .setInternalName("crown")
        .setReduceAmount(2, 4)
        .setItemDescription("item.desc.stoneWall")
        .setItemDescription("item.desc.nature")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item whip = new Whip(206, 512)
        .setInternalName("whip")
        .setReduceAmount(2, 3)
        .setItemDescription("item.desc.aoe")
        .setItemDescription("item.desc.nature")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item battleAxe = new Sword(207, ToolMaterial.RUBY)
        .setReduceAmount(0, 4)
        .setInternalName("battleAxe")
        .setItemDescription("item.desc.knockback")
        .setItemDescription("item.desc.ancient")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item trident = new TridentItem(208)
        .setInternalName("trident")
        .setReduceAmount(5, 5)
        .setItemDescription("item.desc.spear")
        .setItemDescription("item.desc.thunder")
        .setItemDescription("item.desc.tide")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item helmetPoseidon = new Armor(209, ArmorMaterial.TIDE, 7, 0)
        .setReduceAmount(3, 5)
        .setInternalName("helmetPoseidon")
        .setItemDescription("item.desc.aqua")
        .setItemDescription("item.desc.breath")
        .setItemDescription("item.desc.tide")
        .setItemDescription("item.desc.fullset")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item platePoseidon = new Armor(210, ArmorMaterial.TIDE, 7, 1)
        .setReduceAmount(3, 8)
        .setInternalName("chestplatePoseidon")
        .setItemDescription("item.desc.aqua")
        .setItemDescription("item.desc.breath")
        .setItemDescription("item.desc.tide")
        .setItemDescription("item.desc.fullset")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item legsPoseidon = new Armor(211, ArmorMaterial.TIDE, 7, 2)
        .setReduceAmount(3, 7)
        .setInternalName("leggingsPoseidon")
        .setItemDescription("item.desc.aqua")
        .setItemDescription("item.desc.breath")
        .setItemDescription("item.desc.tide")
        .setItemDescription("item.desc.fullset")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item bootsPoseidon = new Armor(212, ArmorMaterial.TIDE, 7, 3)
        .setReduceAmount(3, 4)
        .setInternalName("bootsPoseidon")
        .setItemDescription("item.desc.aqua")
        .setItemDescription("item.desc.breath")
        .setItemDescription("item.desc.tide")
        .setItemDescription("item.desc.fullset")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item lichStaff = new LichStaff(213)
        .setInternalName("lich")
        .setReduceAmount(3, 2)
        .setItemDescription("item.desc.summon")
        .setItemDescription("item.desc.lich")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item shieldRepulsion = new Charm(214, 2, 2, 256)
        .setReduceAmount(3, 4)
        .setInternalName("repulsionShield")
        .setItemDescription("item.desc.knockback")
        .setItemDescription("item.desc.tide")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item quiverGold = new Charm(215, 3, 1, 256)
        .setReduceAmount(5, 3)
        .setInternalName("quiverGold")
        .setItemDescription("item.desc.infinity")
        .setItemDescription("item.desc.midas")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item snowmanStaff = new SnowmanStaff(216)
        .setInternalName("snowman")
        .setReduceAmount(3, 2)
        .setItemDescription("item.desc.summon")
        .setItemDescription("item.desc.lich")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item gabrielStaff = new GabrielStaff(217)
        .setInternalName("gabrielWand")
        .setReduceAmount(3, 2)
        .setItemDescription("item.desc.teleport")
        .setItemDescription("item.desc.seraph")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item pouch = new Pouch(218).setInternalName("pouch").setRarity(Rarity.RARE).setCreativeTab(CreativeTabs.tabTools);
    public static final Item pharaohStaff = new PharaohStaff(219)
        .setInternalName("staffPharaoh")
        .setReduceAmount(5, 2)
        .setItemDescription("item.desc.summon")
        .setItemDescription("item.desc.midas")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item raphaelRing = new RaphaelRing(220)
        .setInternalName("raphaelRing")
        .setReduceAmount(5, 4)
        .setItemDescription("item.desc.homing")
        .setItemDescription("item.desc.seraph")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item seaPickle = new FoodItem(221, 2, 0.2F)
        .setCompostable(true)
        .setInternalName("seaPickle")
        .setItemDescription("item.desc.air")
        .setRarity(Rarity.RARE)
        .setCreativeTab(CreativeTabs.tabFood);
    public static final Item backpack = new Charm(222, 4, 1).setRarity(Rarity.RARE).setInternalName("backpack").setCreativeTab(CreativeTabs.tabTools);
    public static final Item greenBook = new RecipeBook(223).setInternalName("recipeBook").setRarity(Rarity.RARE).setCreativeTab(CreativeTabs.tabTools);
    public static final Item key = new Key(224).setInternalName("key").setRarity(Rarity.RARE).setBurnRate(false).setCreativeTab(CreativeTabs.tabTools);
    public static final Item helmetLich = new Armor(225, ArmorMaterial.SAPPHIRE, 8, 0)
        .setReduceAmount(3, 5)
        .setInternalName("helmetLich")
        .setItemDescription("item.desc.frostimmunity")
        .setItemDescription("item.desc.freezing")
        .setItemDescription("item.desc.lich")
        .setItemDescription("item.desc.fullset")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item plateLich = new Armor(226, ArmorMaterial.SAPPHIRE, 8, 1)
        .setReduceAmount(3, 8)
        .setInternalName("chestplateLich")
        .setItemDescription("item.desc.frostimmunity")
        .setItemDescription("item.desc.freezing")
        .setItemDescription("item.desc.lich")
        .setItemDescription("item.desc.fullset")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item legsLich = new Armor(227, ArmorMaterial.SAPPHIRE, 8, 2)
        .setReduceAmount(3, 7)
        .setInternalName("leggingsLich")
        .setItemDescription("item.desc.frostimmunity")
        .setItemDescription("item.desc.freezing")
        .setItemDescription("item.desc.lich")
        .setItemDescription("item.desc.fullset")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item bootsLich = new Armor(228, ArmorMaterial.SAPPHIRE, 8, 3)
        .setReduceAmount(3, 4)
        .setInternalName("bootsLich")
        .setItemDescription("item.desc.frostimmunity")
        .setItemDescription("item.desc.freezing")
        .setItemDescription("item.desc.lich")
        .setItemDescription("item.desc.fullset")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item cape = new Cape(229, true).setInternalName("cape").setMaxStackSize(1).setCreativeTab(CreativeTabs.tabColor);
    public static final Item ingotDamascus = new Ingot(230)
        .setInternalName("ingotDamascus")
        .setBurnRate(false)
        .setRarity(Rarity.EPIC)
        .setCreativeTab(CreativeTabs.tabMaterials);
    public static final Item swordDamascus = new Sword(231, ToolMaterial.DAMASCUS)
        .setInternalName("swordDamascus")
        .setBurnRate(false)
        .setRarity(Rarity.EPIC)
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item shovelDamascus = new Shovel(232, ToolMaterial.DAMASCUS)
        .setInternalName("shovelDamascus")
        .setBurnRate(false)
        .setRarity(Rarity.EPIC)
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item pickaxeDamascus = new Pickaxe(233, ToolMaterial.DAMASCUS)
        .setInternalName("pickaxeDamascus")
        .setBurnRate(false)
        .setRarity(Rarity.EPIC)
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item axeDamascus = new Axe(234, ToolMaterial.DAMASCUS)
        .setInternalName("hatchetDamascus")
        .setBurnRate(false)
        .setRarity(Rarity.EPIC)
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item pickaxeAdamantine = new Pickaxe(235, ToolMaterial.ADAMANTINE)
        .setInternalName("pickaxeAdamantine")
        .setRarity(Rarity.EPIC)
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item arrowEffect = new ArrowItem(236, false)
        .setInternalName("arrowSpecial")
        .setRarity(Rarity.RARE)
        .setItemDescription("4 DMG")
        .setItemDescription("item.desc.arrow")
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item swordVampire = new Sword(237, ToolMaterial.RUBY)
        .setReduceAmount(1, 1)
        .setInternalName("swordVampire")
        .setItemDescription("item.desc.vampiric")
        .setItemDescription("item.desc.demon")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item rawDamascus = new OreChunk(238)
        .setInternalName("rawDamascus")
        .setBurnRate(false)
        .setCreativeTab(CreativeTabs.tabMaterials)
        .setRarity(Rarity.EPIC);
    public static final Item amethyst = new Gem(239).setInternalName("amethyst").setRarity(Rarity.EPIC).setCreativeTab(CreativeTabs.tabMaterials);
    public static final Item dreamCatcher = new Item(240)
        .setInternalName("dreamcatcher")
        .setMaxStackSize(1)
        .setRarity(Rarity.RARE)
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item boatObsidian = new BoatItem(241)
        .setInternalName("boatObsidian")
        .setRarity(Rarity.EPIC)
        .setBurnRate(false)
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item wax = new FoodItem(242, 2, 0.2F)
        .setPotionEffect(EffectList.nightVision.id, 300, 0)
        .setCompostable(true)
        .setInternalName("wax")
        .setCreativeTab(CreativeTabs.tabFood)
        .setRarity(Rarity.RARE);
    public static final Item compassAmethyst = new Item(243)
        .setInternalName("compassAmethyst")
        .setMaxStackSize(1)
        .setRarity(Rarity.EPIC)
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item carrotGold = new FoodItem(244, 5, 0.6F)
        .setPotionEffect(EffectList.regeneration.id, 150, 0)
        .setInternalName("carrotGolden")
        .setCreativeTab(CreativeTabs.tabFood)
        .setRarity(Rarity.RARE);
    public static final Item bark = new FoodItem(245, 2, 0.2F)
        .setPotionEffect(EffectList.slowFall.id, 300, 0)
        .setCompostable(true)
        .setInternalName("bark")
        .setRarity(Rarity.RARE)
        .setCreativeTab(CreativeTabs.tabFood);
    public static final Item stickCarrot = new Item(246).setInternalName("carrotOnAStick").setCreativeTab(CreativeTabs.tabTools);
    public static final Item rabbitStew = new SoupBowl(247, 20, 1.2F).setInternalName("rabbitStew").setCreativeTab(CreativeTabs.tabFood);
    public static final Item debugStick = new DebugStick(248).setRarity(Rarity.EPIC).setInternalName("debugStick").setCreativeTab(CreativeTabs.tabTools);
    public static final Item flag = new FlagItem(249).setInternalName("flag").setCreativeTab(CreativeTabs.tabColor);
    public static final Musket musket = (Musket)new Musket(250).setInternalName("musket").setRarity(Rarity.EPIC).setCreativeTab(CreativeTabs.tabCombat);
    public static final Musket musketLoaded = (Musket)new Musket(251).setInternalName("musket_loaded").setRarity(Rarity.EPIC);
    public static final Item musketRound = new Item(252).setInternalName("bullet").setItemDescription("20 DMG").setCreativeTab(CreativeTabs.tabCombat);
    public static final Item doorCrimson = new DoorItem(253, BlockList.doorCrimson).setInternalName("doorCrimson").setCreativeTab(CreativeTabs.tabDecoration);
    public static final Item doorNylium = new DoorItem(254, BlockList.doorNylium).setInternalName("doorNylium").setCreativeTab(CreativeTabs.tabDecoration);
    public static final Item fireball = new ThrowableItem(255, 5)
        .setInternalName("fireball")
        .setCreativeTab(CreativeTabs.tabMaterials)
        .setPotionPrefix(5, "+3!4-13");
    public static final Item axeAdamantine = new Axe(256, ToolMaterial.ADAMANTINE)
        .setInternalName("hatchetAdamantine")
        .setRarity(Rarity.EPIC)
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item chainmail = new Item(257).setInternalName("chainmail").setCreativeTab(CreativeTabs.tabMaterials);
    public static final Item helmetCopper = new Armor(258, ArmorMaterial.COPPER, 5, 0).setInternalName("helmetCopper").setCreativeTab(CreativeTabs.tabCombat);
    public static final Item plateCopper = new Armor(259, ArmorMaterial.COPPER, 5, 1)
        .setInternalName("chestplateCopper")
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item legsCopper = new Armor(260, ArmorMaterial.COPPER, 5, 2).setInternalName("leggingsCopper").setCreativeTab(CreativeTabs.tabCombat);
    public static final Item bootsCopper = new Armor(261, ArmorMaterial.COPPER, 5, 3).setInternalName("bootsCopper").setCreativeTab(CreativeTabs.tabCombat);
    public static final Item swordCopper = new Sword(262, ToolMaterial.COPPER).setInternalName("swordCopper").setCreativeTab(CreativeTabs.tabCombat);
    public static final Item shovelCopper = new Shovel(263, ToolMaterial.COPPER).setInternalName("shovelCopper").setCreativeTab(CreativeTabs.tabTools);
    public static final Item pickaxeCopper = new Pickaxe(264, ToolMaterial.COPPER).setInternalName("pickaxeCopper").setCreativeTab(CreativeTabs.tabTools);
    public static final Item axeCopper = new Axe(265, ToolMaterial.COPPER).setInternalName("hatchetCopper").setCreativeTab(CreativeTabs.tabTools);
    public static final Item hoeCopper = new Hoe(266, ToolMaterial.COPPER).setInternalName("hoeCopper").setCreativeTab(CreativeTabs.tabTools);
    public static final Item ingotCopper = new Ingot(267).setInternalName("ingotCopper").setCreativeTab(CreativeTabs.tabMaterials);
    public static final Item rawCopper = new OreChunk(268).setInternalName("rawCopper").setCreativeTab(CreativeTabs.tabMaterials);
    public static final Item shovelAdamantine = new Shovel(269, ToolMaterial.ADAMANTINE)
        .setInternalName("shovelAdamantine")
        .setRarity(Rarity.EPIC)
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item screwdriver = new Screwdriver(270).setInternalName("screwdriver").setMaxStackSize(1).setCreativeTab(CreativeTabs.tabTools);
    public static final Item recordCopper = new Record(271, "Heartbreaker", "Soybean").setInternalName("recordCopper").setCreativeTab(CreativeTabs.tabTools);
    public static final Item shears = new Shears(272).setInternalName("shears").setMaxStackSize(1).setCreativeTab(CreativeTabs.tabTools);
    public static final Item scroll = new Scroll(273).setInternalName("scroll").setRarity(Rarity.RARE).setMaxStackSize(1).setCreativeTab(CreativeTabs.tabTools);
    public static final Item magnet = new Charm(274, 0, 3)
        .setReduceAmount(0, 5)
        .setInternalName("magnet")
        .setItemDescription("item.desc.attract")
        .setItemDescription("item.desc.ancient")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item recordBroken = new Record(275, "Disc XV", "Soybean").setInternalName("recordBroken").setCreativeTab(CreativeTabs.tabTools);
    public static final Item recordVoid = new Record(276, "Eclipsed", "Soybean").setInternalName("recordVoid").setCreativeTab(CreativeTabs.tabTools);
    public static final Item hoeDamascus = new Hoe(277, ToolMaterial.DAMASCUS)
        .setInternalName("hoeDamascus")
        .setBurnRate(false)
        .setRarity(Rarity.EPIC)
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item tear = new Ingredient(278).setInternalName("tear").setCreativeTab(CreativeTabs.tabMaterials).setPotionPrefix(2, "+5-6!7");
    public static final Item bedBrown = new BedItem(279, BlockList.bedBrown).setInternalName("bed_brown").setCreativeTab(CreativeTabs.tabColor);
    public static final Item spectacles = new Charm(280, 6, 0)
        .setReduceAmount(0, 4)
        .setInternalName("mineHat")
        .setItemDescription("item.desc.ancient")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item swordPheonix = new Sword(281, ToolMaterial.RUBY)
        .setReduceAmount(1, 2)
        .setInternalName("phoenixSword")
        .setItemDescription("item.desc.fire")
        .setItemDescription("item.desc.ifrit")
        .setBurnRate(false)
        .setCreativeTab(CreativeTabs.tabCombat)
        .setRarity(Rarity.LEGENDARY);
    public static final Item shovelPheonix = new Shovel(282, ToolMaterial.RUBY)
        .setReduceAmount(1, 1)
        .setInternalName("phoenixShovel")
        .setItemDescription("item.desc.fire")
        .setItemDescription("item.desc.ifrit")
        .setBurnRate(false)
        .setCreativeTab(CreativeTabs.tabTools)
        .setRarity(Rarity.LEGENDARY);
    public static final Item pickaxePheonix = new Pickaxe(283, ToolMaterial.RUBY)
        .setReduceAmount(1, 3)
        .setInternalName("phoenixPickaxe")
        .setItemDescription("item.desc.fire")
        .setItemDescription("item.desc.ifrit")
        .setBurnRate(false)
        .setCreativeTab(CreativeTabs.tabTools)
        .setRarity(Rarity.LEGENDARY);
    public static final Item axePheonix = new Axe(284, ToolMaterial.RUBY)
        .setReduceAmount(1, 3)
        .setInternalName("phoenixAxe")
        .setItemDescription("item.desc.fire")
        .setItemDescription("item.desc.ifrit")
        .setBurnRate(false)
        .setCreativeTab(CreativeTabs.tabTools)
        .setRarity(Rarity.LEGENDARY);
    public static final Item helmetPheonix = new Armor(285, ArmorMaterial.RUBY, 9, 0)
        .setReduceAmount(1, 5)
        .setInternalName("helmetPheonix")
        .setItemDescription("item.desc.fireimmunity")
        .setItemDescription("item.desc.ifrit")
        .setBurnRate(false)
        .setItemDescription("item.desc.fullset")
        .setCreativeTab(CreativeTabs.tabCombat)
        .setRarity(Rarity.LEGENDARY);
    public static final Item platePheonix = new Armor(286, ArmorMaterial.RUBY, 9, 1)
        .setReduceAmount(1, 8)
        .setInternalName("chestplatePheonix")
        .setItemDescription("item.desc.fireimmunity")
        .setItemDescription("item.desc.ifrit")
        .setBurnRate(false)
        .setItemDescription("item.desc.fullset")
        .setCreativeTab(CreativeTabs.tabCombat)
        .setRarity(Rarity.LEGENDARY);
    public static final Item legsPheonix = new Armor(287, ArmorMaterial.RUBY, 9, 2)
        .setReduceAmount(1, 7)
        .setInternalName("leggingsPheonix")
        .setItemDescription("item.desc.fireimmunity")
        .setItemDescription("item.desc.ifrit")
        .setBurnRate(false)
        .setItemDescription("item.desc.fullset")
        .setCreativeTab(CreativeTabs.tabCombat)
        .setRarity(Rarity.LEGENDARY);
    public static final Item bootsPheonix = new Armor(288, ArmorMaterial.RUBY, 9, 3)
        .setReduceAmount(1, 4)
        .setInternalName("bootsPheonix")
        .setItemDescription("item.desc.fireimmunity")
        .setItemDescription("item.desc.ifrit")
        .setBurnRate(false)
        .setItemDescription("item.desc.fullset")
        .setCreativeTab(CreativeTabs.tabCombat)
        .setRarity(Rarity.LEGENDARY);
    public static final Item capeInvisibility = new Cape(289, false)
        .setInternalName("invisibilityCloak")
        .setItemDescription("item.desc.tide")
        .setReduceAmount(5, 2)
        .setMaxStackSize(1)
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item capeAgility = new Cape(290, false)
        .setInternalName("agilityCape")
        .setItemDescription("item.desc.demon")
        .setReduceAmount(0, 2)
        .setMaxStackSize(1)
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item ringRegen = new Charm(291, 1, 3, 256)
        .setInternalName("ringHeal")
        .setReduceAmount(0, 3)
        .setItemDescription("potion.regeneration")
        .setItemDescription("item.desc.ancient")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item impStaff = new ImpStaff(292)
        .setInternalName("impStaff")
        .setReduceAmount(1, 1)
        .setItemDescription("item.desc.summon")
        .setItemDescription("item.desc.ifrit")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item hornCopper = new Horn(293).setMaxStackSize(1).setInternalName("hornCopper").setCreativeTab(CreativeTabs.tabTools);
    public static final Item swordMichael = new Sword(294, ToolMaterial.MICHAEL, true)
        .setReduceAmount(1, 2)
        .setInternalName("swordMichael")
        .setItemDescription("item.desc.soul")
        .setItemDescription("item.desc.seraph")
        .setBurnRate(false)
        .setCreativeTab(CreativeTabs.tabCombat)
        .setRarity(Rarity.LEGENDARY);
    public static final Item gauntletUriel = new Pickaxe(295, ToolMaterial.SPECIAL)
        .setReduceAmount(5, 3)
        .setInternalName("gauntletUriel")
        .setItemDescription("item.desc.destroy")
        .setItemDescription("item.desc.seraph")
        .setBurnRate(false)
        .setCreativeTab(CreativeTabs.tabTools)
        .setRarity(Rarity.LEGENDARY);
    public static final Item grapplingHook = new GrapplingHook(296)
        .setInternalName("lead")
        .setItemDescription("item.desc.demon")
        .setReduceAmount(1, 3)
        .setCreativeTab(CreativeTabs.tabTools)
        .setMaxStackSize(1)
        .setRarity(Rarity.LEGENDARY);
    public static final Item seedsBeetroot = new Seeds(297, BlockList.beetroots.id)
        .setInternalName("seedsBeetroot")
        .setCompostable(true)
        .setCreativeTab(CreativeTabs.tabNature);
    public static final Item beetroot = new FoodItem(298, 2, 0.2F)
        .setPotionEffect(EffectList.fireResistance.id, 200, 0)
        .setCompostable(true)
        .setInternalName("beetroot")
        .setRarity(Rarity.RARE)
        .setCreativeTab(CreativeTabs.tabFood);
    public static final Item bowlBeetroot = new SoupBowl(299, 6, 0.6F).setInternalName("beetrootStew").setCreativeTab(CreativeTabs.tabFood);
    public static final Item discFragment = new Item(300).setInternalName("discFragment").setRarity(Rarity.RARE).setCreativeTab(CreativeTabs.tabMaterials);
    public static final Item shieldDamascus = new Charm(301, 5, 2, 256)
        .setInternalName("shieldArcane")
        .setItemDescription("item.desc.parry")
        .setRarity(Rarity.EPIC)
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item swordValkyrie = new Sword(302, ToolMaterial.TOPAZ, CreatureType.UNDEAD)
        .setReduceAmount(4, 2)
        .setInternalName("valkyrieSword")
        .setItemDescription("item.desc.smite")
        .setItemDescription("item.desc.reach")
        .setItemDescription("item.desc.paladin")
        .setCreativeTab(CreativeTabs.tabCombat)
        .setRarity(Rarity.LEGENDARY);
    public static final Item shovelValkyrie = new Shovel(303, ToolMaterial.TOPAZ)
        .setReduceAmount(4, 1)
        .setInternalName("valkyrieShovel")
        .setItemDescription("item.desc.silkTouch")
        .setItemDescription("item.desc.reach")
        .setItemDescription("item.desc.paladin")
        .setCreativeTab(CreativeTabs.tabTools)
        .setRarity(Rarity.LEGENDARY);
    public static final Item pickaxeValkyrie = new Pickaxe(304, ToolMaterial.TOPAZ)
        .setReduceAmount(4, 3)
        .setInternalName("valkyriePickaxe")
        .setItemDescription("item.desc.silkTouch")
        .setItemDescription("item.desc.reach")
        .setItemDescription("item.desc.paladin")
        .setCreativeTab(CreativeTabs.tabTools)
        .setRarity(Rarity.LEGENDARY);
    public static final Item axeValkyrie = new Axe(305, ToolMaterial.TOPAZ)
        .setReduceAmount(4, 3)
        .setInternalName("valkyrieAxe")
        .setItemDescription("item.desc.silkTouch")
        .setItemDescription("item.desc.reach")
        .setItemDescription("item.desc.paladin")
        .setCreativeTab(CreativeTabs.tabTools)
        .setRarity(Rarity.LEGENDARY);
    public static final Item helmetValkyrie = new Armor(306, ArmorMaterial.TOPAZ, 10, 0)
        .setReduceAmount(4, 5)
        .setInternalName("helmetValkyrie")
        .setItemDescription("item.desc.featherfall")
        .setItemDescription("item.desc.paladin")
        .setItemDescription("item.desc.fullset")
        .setCreativeTab(CreativeTabs.tabCombat)
        .setRarity(Rarity.LEGENDARY);
    public static final Item plateValkyrie = new Armor(307, ArmorMaterial.TOPAZ, 10, 1)
        .setReduceAmount(4, 8)
        .setInternalName("plateValkyrie")
        .setItemDescription("item.desc.featherfall")
        .setItemDescription("item.desc.paladin")
        .setItemDescription("item.desc.fullset")
        .setCreativeTab(CreativeTabs.tabCombat)
        .setRarity(Rarity.LEGENDARY);
    public static final Item legsValkyrie = new Armor(308, ArmorMaterial.TOPAZ, 10, 2)
        .setReduceAmount(4, 7)
        .setInternalName("leggingsValkyrie")
        .setItemDescription("item.desc.featherfall")
        .setItemDescription("item.desc.paladin")
        .setItemDescription("item.desc.fullset")
        .setCreativeTab(CreativeTabs.tabCombat)
        .setRarity(Rarity.LEGENDARY);
    public static final Item bootsValkyrie = new Armor(309, ArmorMaterial.TOPAZ, 10, 3)
        .setReduceAmount(4, 4)
        .setInternalName("bootsValkyrie")
        .setItemDescription("item.desc.featherfall")
        .setItemDescription("item.desc.paladin")
        .setItemDescription("item.desc.fullset")
        .setCreativeTab(CreativeTabs.tabCombat)
        .setRarity(Rarity.LEGENDARY);
    public static final Item hoeAdamantine = new Hoe(310, ToolMaterial.ADAMANTINE)
        .setInternalName("hoeAdamantine")
        .setRarity(Rarity.EPIC)
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item swordAdamantine = new Sword(311, ToolMaterial.ADAMANTINE)
        .setInternalName("swordAdamantine")
        .setRarity(Rarity.EPIC)
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item mace = new Sword(312, ToolMaterial.TOPAZ)
        .setInternalName("mace")
        .setItemDescription("item.desc.paladin")
        .setReduceAmount(4, 5)
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item healingStaff = new HealingStaff(313)
        .setInternalName("staffHealing")
        .setReduceAmount(5, 2)
        .setItemDescription("item.desc.midas")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item windAmulet = new Charm(314, 1, 0)
        .setReduceAmount(5, 1)
        .setInternalName("bottleCloud")
        .setItemDescription("item.desc.jump")
        .setItemDescription("item.desc.midas")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item belialScythe = new Scythe(315)
        .setInternalName("scythe")
        .setReduceAmount(0, 2)
        .setItemDescription("item.desc.seraph")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item cloudStaff = new CloudStaff(316)
        .setInternalName("staffCloud")
        .setRarity(Rarity.LEGENDARY)
        .setReduceAmount(3, 2)
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item windCharge = new ThrowableItem(317, 2)
        .setInternalName("windCharge")
        .setCreativeTab(CreativeTabs.tabMaterials)
        .setPotionPrefix(10, "+11!8-16");
    public static final Item harp = new Whip(318, 256)
        .setInternalName("harp")
        .setReduceAmount(5, 3)
        .setItemDescription("item.desc.calm")
        .setItemDescription("item.desc.midas")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item arrowCalming = new ArrowItem(319, false)
        .setInternalName("arrowCupid")
        .setRarity(Rarity.RARE)
        .setItemDescription("4 DMG")
        .setItemDescription("item.desc.arrow")
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item rosary = new Charm(320, 2, 0)
        .setInternalName("bottleFairy")
        .setItemDescription("item.desc.oneUp")
        .setItemDescription("item.desc.paladin")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item arrowPiercing = new ArrowItem(321, true)
        .setInternalName("arrowPiercing")
        .setItemDescription("8 DMG")
        .setItemDescription("item.desc.bolt")
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item helmetDamascus = new Armor(322, ArmorMaterial.DAMASCUS, 11, 0)
        .setInternalName("helmetDamascus")
        .setBurnRate(false)
        .setCreativeTab(CreativeTabs.tabCombat)
        .setRarity(Rarity.EPIC);
    public static final Item plateDamascus = new Armor(323, ArmorMaterial.DAMASCUS, 11, 1)
        .setInternalName("plateDamascus")
        .setBurnRate(false)
        .setCreativeTab(CreativeTabs.tabCombat)
        .setRarity(Rarity.EPIC);
    public static final Item legsDamascus = new Armor(324, ArmorMaterial.DAMASCUS, 11, 2)
        .setInternalName("leggingsDamascus")
        .setBurnRate(false)
        .setCreativeTab(CreativeTabs.tabCombat)
        .setRarity(Rarity.EPIC);
    public static final Item bootsDamascus = new Armor(325, ArmorMaterial.DAMASCUS, 11, 3)
        .setInternalName("bootsDamascus")
        .setBurnRate(false)
        .setCreativeTab(CreativeTabs.tabCombat)
        .setRarity(Rarity.EPIC);
    public static final Item helmetAdamantine = new Armor(326, ArmorMaterial.ADAMANTINE, 12, 0)
        .setInternalName("helmetAdamantine")
        .setCreativeTab(CreativeTabs.tabCombat)
        .setRarity(Rarity.EPIC);
    public static final Item plateAdamantine = new Armor(327, ArmorMaterial.ADAMANTINE, 12, 1)
        .setInternalName("plateAdamantine")
        .setCreativeTab(CreativeTabs.tabCombat)
        .setRarity(Rarity.EPIC);
    public static final Item legsAdamantine = new Armor(328, ArmorMaterial.ADAMANTINE, 12, 2)
        .setInternalName("leggingsAdamantine")
        .setCreativeTab(CreativeTabs.tabCombat)
        .setRarity(Rarity.EPIC);
    public static final Item bootsAdamantine = new Armor(329, ArmorMaterial.ADAMANTINE, 12, 3)
        .setInternalName("bootsAdamantine")
        .setCreativeTab(CreativeTabs.tabCombat)
        .setRarity(Rarity.EPIC);
    public static final Item recordSea = new Record(330, "Castaway", "Soybean").setInternalName("recordSea").setCreativeTab(CreativeTabs.tabTools);
    public static final Item doorPurpur = new DoorItem(331, BlockList.doorPurpur).setInternalName("doorPurpur").setCreativeTab(CreativeTabs.tabDecoration);
    public static final Item doorWillow = new DoorItem(332, BlockList.doorWillow).setInternalName("doorWillow").setCreativeTab(CreativeTabs.tabDecoration);
    public static final Item elytra = new Cape(333, false).setInternalName("elytra").setRarity(Rarity.LEGENDARY).setCreativeTab(CreativeTabs.tabTools);
    public static final Item bedYellow = new BedItem(334, BlockList.bedYellow).setInternalName("bed_yellow").setCreativeTab(CreativeTabs.tabColor);
    public static final Item bedChartreuse = new BedItem(335, BlockList.bedChartreuse).setInternalName("bed_chartreuse").setCreativeTab(CreativeTabs.tabColor);
    public static final Item bedGreen = new BedItem(336, BlockList.bedGreen).setInternalName("bed_green").setCreativeTab(CreativeTabs.tabColor);
    public static final Item bedSpringGreen = new BedItem(337, BlockList.bedSpringGreen)
        .setInternalName("bed_springgreen")
        .setCreativeTab(CreativeTabs.tabColor);
    public static final Item bedCyan = new BedItem(338, BlockList.bedCyan).setInternalName("bed_cyan").setCreativeTab(CreativeTabs.tabColor);
    public static final Item bedBlue = new BedItem(339, BlockList.bedBlue).setInternalName("bed_blue").setCreativeTab(CreativeTabs.tabColor);
    public static final Item bedAqua = new BedItem(340, BlockList.bedAquamarine).setInternalName("bed_ultramarine").setCreativeTab(CreativeTabs.tabColor);
    public static final Item bedPurple = new BedItem(341, BlockList.bedPurple).setInternalName("bed_purple").setCreativeTab(CreativeTabs.tabColor);
    public static final Item bedMagenta = new BedItem(342, BlockList.bedMagenta).setInternalName("bed_magenta").setCreativeTab(CreativeTabs.tabColor);
    public static final Item bedPink = new BedItem(343, BlockList.bedPink).setInternalName("bed_pink").setCreativeTab(CreativeTabs.tabColor);
    public static final Item bedRose = new BedItem(344, BlockList.bedRose).setInternalName("bed_rose").setCreativeTab(CreativeTabs.tabColor);
    public static final Item bedBlack = new BedItem(345, BlockList.bedBlack).setInternalName("bed_black").setCreativeTab(CreativeTabs.tabColor);
    public static final Item bedDarkGray = new BedItem(346, BlockList.bedDarkGray).setInternalName("bed_drkgray").setCreativeTab(CreativeTabs.tabColor);
    public static final Item bedLightGray = new BedItem(347, BlockList.bedLightGray).setInternalName("bed_lghtgray").setCreativeTab(CreativeTabs.tabColor);
    public static final Item bedWhite = new BedItem(348, BlockList.bedWhite).setInternalName("bed_white").setCreativeTab(CreativeTabs.tabColor);
    public static final Item doorLime = new DoorItem(349, BlockList.doorLime).setInternalName("doorLime").setCreativeTab(CreativeTabs.tabDecoration);
    public static final Item glowStick = new GlowstickItem(350).setInternalName("glowstick").setCreativeTab(CreativeTabs.tabTools);
    public static final Item singularity = new Item(351)
        .setInternalName("singularity")
        .setRarity(Rarity.EPIC)
        .setMaxStackSize(1)
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item pearl = new DarkMatter(352).setInternalName("pearl").setRarity(Rarity.RARE).setCreativeTab(CreativeTabs.tabMaterials);
    public static final Item scrap = new Scrap(353).setInternalName("scrap").setRarity(Rarity.RARE).setCreativeTab(CreativeTabs.tabMaterials);
    public static final Item bucketFish = new Bucket(354, BlockList.waterMoving.id)
        .setMaxStackSize(1)
        .setInternalName("bucketFish")
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item egg = new ThrowableItem(355, 1).setInternalName("egg").setCreativeTab(CreativeTabs.tabMaterials);
    public static final Item enderEye = new Eye(356).setInternalName("enderEye").setRarity(Rarity.RARE).setCreativeTab(CreativeTabs.tabTools);
    public static final Item eggBoiled = new FoodItem(357, 4, 0.5F)
        .setPotionEffect(EffectList.stamina.id, 300, 0)
        .setInternalName("egg_boiled")
        .setCreativeTab(CreativeTabs.tabFood);
    public static final Item recordPig = new Record(358, "Flight School", "Soybean").setInternalName("recordPiglin").setCreativeTab(CreativeTabs.tabTools);
    public static final Item endCrystal = new CrystalItem(359).setRarity(Rarity.LEGENDARY).setInternalName("end_crystal").setCreativeTab(CreativeTabs.tabTools);
    public static final Item paxel = new Pickaxe(360, ToolMaterial.DARKMATTER)
        .setReduceAmount(0, 2)
        .setInternalName("paxel")
        .setItemDescription("item.desc.ancient")
        .setCreativeTab(CreativeTabs.tabTools)
        .setRarity(Rarity.LEGENDARY);
    public static final Item tail = new Ingredient(361).setInternalName("tail").setCreativeTab(CreativeTabs.tabMaterials).setPotionPrefix(19, "+8!14-2");
    public static final Item sandball = new ThrowableItem(362, 3).setInternalName("sandBall").setCreativeTab(CreativeTabs.tabMaterials);
    public static final Item spearStone = new Spear(363, ToolMaterial.STONE).setInternalName("spearStone").setCreativeTab(CreativeTabs.tabCombat);
    public static final Item spearCopper = new Spear(364, ToolMaterial.COPPER).setInternalName("spearCopper").setCreativeTab(CreativeTabs.tabCombat);
    public static final Item spearSteel = new Spear(365, ToolMaterial.IRON).setInternalName("spearSteel").setCreativeTab(CreativeTabs.tabCombat);
    public static final Item spearGold = new Spear(366, ToolMaterial.GOLD).setInternalName("spearGold").setCreativeTab(CreativeTabs.tabCombat);
    public static final Item spearDiamond = new Spear(367, ToolMaterial.DIAMOND)
        .setInternalName("spearDiamond")
        .setRarity(Rarity.RARE)
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item spearDamascus = new Spear(368, ToolMaterial.DAMASCUS)
        .setInternalName("spearDamascus")
        .setBurnRate(false)
        .setRarity(Rarity.EPIC)
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item spearAdamantine = new Spear(369, ToolMaterial.ADAMANTINE)
        .setInternalName("spearAdamantine")
        .setRarity(Rarity.EPIC)
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item arrowGlowing = new ArrowItem(370, false)
        .setInternalName("arrowGlowing")
        .setItemDescription("4 DMG")
        .setRarity(Rarity.RARE)
        .setItemDescription("item.desc.arrow")
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item lightGem = new LightGem(371)
        .setInternalName("lightGem")
        .setMaxStackSize(1)
        .setRarity(Rarity.EPIC)
        .setCreativeTab(CreativeTabs.tabTools);
    public static final Item itemFrameGlowing = new ItemFrame(372).setInternalName("itemFrameGlowing").setCreativeTab(CreativeTabs.tabDecoration);
    public static final Crossbow crossboner = (Crossbow)new Crossbow(373)
        .setInternalName("crossbow_bone")
        .setItemDescription("item.desc.multishot")
        .setRarity(Rarity.RARE)
        .setCreativeTab(CreativeTabs.tabCombat);
    public static final Item bucketSlime = new Bucket(374, 0).setMaxStackSize(1).setInternalName("bucketSlime").setCreativeTab(CreativeTabs.tabTools);
    public static final Item axeSpecial = new Axe(375, ToolMaterial.IRON)
        .setInternalName("hatchet")
        .setRarity(Rarity.LEGENDARY)
        .setCreativeTab(CreativeTabs.tabCombat);

    static {
        StatList.initItemStats();
    }
}
