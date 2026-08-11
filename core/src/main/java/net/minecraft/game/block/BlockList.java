package net.minecraft.game.block;

import net.minecraft.game.block.building.Bricks;
import net.minecraft.game.block.building.Clay;
import net.minecraft.game.block.building.Cloth;
import net.minecraft.game.block.building.CloudBlock;
import net.minecraft.game.block.building.Cobblestone;
import net.minecraft.game.block.building.ColoredGlass;
import net.minecraft.game.block.building.Coral;
import net.minecraft.game.block.building.Dirt;
import net.minecraft.game.block.building.Farmland;
import net.minecraft.game.block.building.FlowerPetal;
import net.minecraft.game.block.building.Fruit;
import net.minecraft.game.block.building.GemCluster;
import net.minecraft.game.block.building.Glass;
import net.minecraft.game.block.building.Grass;
import net.minecraft.game.block.building.Gravel;
import net.minecraft.game.block.building.HardenedClay;
import net.minecraft.game.block.building.Ice;
import net.minecraft.game.block.building.LayerBlock;
import net.minecraft.game.block.building.LeafPile;
import net.minecraft.game.block.building.Leaves;
import net.minecraft.game.block.building.Log;
import net.minecraft.game.block.building.Lumber;
import net.minecraft.game.block.building.Magma;
import net.minecraft.game.block.building.MetalBlock;
import net.minecraft.game.block.building.Moss;
import net.minecraft.game.block.building.MossyCobblestone;
import net.minecraft.game.block.building.MushroomCap;
import net.minecraft.game.block.building.Obsidian;
import net.minecraft.game.block.building.ObsidianBricks;
import net.minecraft.game.block.building.Ore;
import net.minecraft.game.block.building.PackedIce;
import net.minecraft.game.block.building.Panel;
import net.minecraft.game.block.building.PathBlock;
import net.minecraft.game.block.building.Pillar;
import net.minecraft.game.block.building.Planks;
import net.minecraft.game.block.building.Plantern;
import net.minecraft.game.block.building.PowderSnow;
import net.minecraft.game.block.building.QuickSoil;
import net.minecraft.game.block.building.Sand;
import net.minecraft.game.block.building.Sandstone;
import net.minecraft.game.block.building.Slab;
import net.minecraft.game.block.building.SlowSand;
import net.minecraft.game.block.building.Snow;
import net.minecraft.game.block.building.SoulSoil;
import net.minecraft.game.block.building.Stairs;
import net.minecraft.game.block.building.Stone;
import net.minecraft.game.block.building.StoneBricks;
import net.minecraft.game.block.building.SuspiciousDirt;
import net.minecraft.game.block.building.TintedGlass;
import net.minecraft.game.block.building.VoidGrass;
import net.minecraft.game.block.decoration.Altar;
import net.minecraft.game.block.decoration.Bed;
import net.minecraft.game.block.decoration.Cake;
import net.minecraft.game.block.decoration.Candle;
import net.minecraft.game.block.decoration.Carpet;
import net.minecraft.game.block.decoration.Chain;
import net.minecraft.game.block.decoration.Chair;
import net.minecraft.game.block.decoration.ColoredPane;
import net.minecraft.game.block.decoration.Door;
import net.minecraft.game.block.decoration.Fence;
import net.minecraft.game.block.decoration.FenceGate;
import net.minecraft.game.block.decoration.Flag;
import net.minecraft.game.block.decoration.FlowerPot;
import net.minecraft.game.block.decoration.Grave;
import net.minecraft.game.block.decoration.Ladder;
import net.minecraft.game.block.decoration.Lantern;
import net.minecraft.game.block.decoration.LightningRod;
import net.minecraft.game.block.decoration.Rope;
import net.minecraft.game.block.decoration.Skull;
import net.minecraft.game.block.decoration.Table;
import net.minecraft.game.block.decoration.TikiTorch;
import net.minecraft.game.block.decoration.Torch;
import net.minecraft.game.block.decoration.Trapdoor;
import net.minecraft.game.block.decoration.Wall;
import net.minecraft.game.block.decoration.WindowPane;
import net.minecraft.game.block.liquid.FlowingLiquid;
import net.minecraft.game.block.liquid.StillLiquid;
import net.minecraft.game.block.machines.Battery;
import net.minecraft.game.block.machines.Cog;
import net.minecraft.game.block.machines.CommandBlock;
import net.minecraft.game.block.machines.Crafter;
import net.minecraft.game.block.machines.Detector;
import net.minecraft.game.block.machines.Dispenser;
import net.minecraft.game.block.machines.Fan;
import net.minecraft.game.block.machines.Freezer;
import net.minecraft.game.block.machines.GeneratorBlock;
import net.minecraft.game.block.machines.Lamp;
import net.minecraft.game.block.machines.MovingPiston;
import net.minecraft.game.block.machines.NoteBlock;
import net.minecraft.game.block.machines.Observer;
import net.minecraft.game.block.machines.Piston;
import net.minecraft.game.block.machines.PistonHead;
import net.minecraft.game.block.machines.SolarPanel;
import net.minecraft.game.block.machines.Teleporter;
import net.minecraft.game.block.machines.Transformer;
import net.minecraft.game.block.machines.Vacuum;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.block.misc.Cobweb;
import net.minecraft.game.block.misc.Crystal;
import net.minecraft.game.block.misc.EmptyBlock;
import net.minecraft.game.block.misc.Fire;
import net.minecraft.game.block.misc.Hellfire;
import net.minecraft.game.block.misc.InfestedStone;
import net.minecraft.game.block.misc.MinecartTrack;
import net.minecraft.game.block.misc.Pebble;
import net.minecraft.game.block.misc.Portal;
import net.minecraft.game.block.misc.RespawnAnchor;
import net.minecraft.game.block.misc.SlimeBlock;
import net.minecraft.game.block.misc.Sponge;
import net.minecraft.game.block.misc.StoneSpike;
import net.minecraft.game.block.misc.TNT;
import net.minecraft.game.block.plants.BambooStalk;
import net.minecraft.game.block.plants.BerryBush;
import net.minecraft.game.block.plants.Cactus;
import net.minecraft.game.block.plants.Clover;
import net.minecraft.game.block.plants.CoralFan;
import net.minecraft.game.block.plants.Crops;
import net.minecraft.game.block.plants.Flower;
import net.minecraft.game.block.plants.GlowLychen;
import net.minecraft.game.block.plants.HangingVine;
import net.minecraft.game.block.plants.LilyPad;
import net.minecraft.game.block.plants.Mushroom;
import net.minecraft.game.block.plants.Reeds;
import net.minecraft.game.block.plants.Roots;
import net.minecraft.game.block.plants.Sapling;
import net.minecraft.game.block.plants.Seaweed;
import net.minecraft.game.block.plants.Shrub;
import net.minecraft.game.block.plants.Stem;
import net.minecraft.game.block.plants.TallGrass;
import net.minecraft.game.block.plants.Vine;
import net.minecraft.game.block.plants.WaterLily;
import net.minecraft.game.block.stepsound.StepSoundsList;
import net.minecraft.game.block.tileentity.other.CommandBlockEntity;
import net.minecraft.game.block.tileentity.other.SignEntity;
import net.minecraft.game.block.utility.Anvil;
import net.minecraft.game.block.utility.Barrel;
import net.minecraft.game.block.utility.Beacon;
import net.minecraft.game.block.utility.BlastFurnace;
import net.minecraft.game.block.utility.Bookshelf;
import net.minecraft.game.block.utility.Cauldron;
import net.minecraft.game.block.utility.Chest;
import net.minecraft.game.block.utility.Composter;
import net.minecraft.game.block.utility.Furnace;
import net.minecraft.game.block.utility.Grindstone;
import net.minecraft.game.block.utility.Hellforge;
import net.minecraft.game.block.utility.Jukebox;
import net.minecraft.game.block.utility.Lectern;
import net.minecraft.game.block.utility.LockedChest;
import net.minecraft.game.block.utility.MobSpawner;
import net.minecraft.game.block.utility.ObsidianChest;
import net.minecraft.game.block.utility.PlayerGrave;
import net.minecraft.game.block.utility.Pot;
import net.minecraft.game.block.utility.Sign;
import net.minecraft.game.block.utility.Smoker;
import net.minecraft.game.block.utility.Stonecutter;
import net.minecraft.game.block.utility.Workbench;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.Rarity;
import net.minecraft.game.item.block.BlockItem;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.item.tools.ToolType;
import net.minecraft.statistics.StatList;

public class BlockList {
    public static final Block[] blocks = new Block[4096];
    public static final Block stone = new Stone(1)
        .setTool(ToolType.PICKAXE)
        .setHardness(1.5F)
        .setResistance(10.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("stone")
        .setCategory(CreativeTabs.tabNature);
    public static final Grass grass = (Grass)new Grass(2)
        .setTool(ToolType.SHOVEL)
        .setHardness(0.6F)
        .setStepSound(StepSoundsList.grassSound)
        .setName("grass")
        .setCategory(CreativeTabs.tabNature);
    public static final Block dirt = new Dirt(3)
        .setHardness(0.5F)
        .setTool(ToolType.SHOVEL)
        .setStepSound(StepSoundsList.gravelSound)
        .setName("dirt")
        .setCategory(CreativeTabs.tabNature);
    public static final Block cobblestone = new Cobblestone(4)
        .setTool(ToolType.PICKAXE)
        .setHardness(2.0F)
        .setResistance(10.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("cobblestone")
        .setCategory(CreativeTabs.tabBlocks);
    public static final Block planks = new Planks(5)
        .setHardness(2.0F)
        .setTool(ToolType.AXE)
        .setResistance(5.0F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("wood")
        .setCategory(CreativeTabs.tabBlocks);
    public static final Block sapling = new Sapling(6)
        .setHardness(0.0F)
        .setStepSound(StepSoundsList.grassSound)
        .setName("sapling")
        .setCategory(CreativeTabs.tabNature);
    public static final Block bedrock = new Block(7, MaterialList.rock)
        .setHardness(-1.0F)
        .setResistance(6000000.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("bedrock")
        .setCategory(CreativeTabs.tabNature);
    public static final Block waterMoving = new FlowingLiquid(8, MaterialList.water).setHardness(-1.0F).setLightOpacity(3).setName("water");
    public static final Block waterStill = new StillLiquid(9, MaterialList.water).setHardness(-1.0F).setLightOpacity(3).setName("water");
    public static final Block lavaMoving = new FlowingLiquid(10, MaterialList.lava).setHardness(-1.0F).setLightValue(1.0F).setLightOpacity(255).setName("lava");
    public static final Block lavaStill = new StillLiquid(11, MaterialList.lava).setHardness(-1.0F).setLightValue(1.0F).setLightOpacity(255).setName("lava");
    public static final Block sand = new Sand(12, MaterialList.sand)
        .setTool(ToolType.SHOVEL)
        .setHardness(0.5F)
        .setStepSound(StepSoundsList.sandSound)
        .setName("sand")
        .setCategory(CreativeTabs.tabNature);
    public static final Block gravel = new Gravel(13)
        .setHardness(0.6F)
        .setTool(ToolType.SHOVEL)
        .setStepSound(StepSoundsList.gravelSound)
        .setName("gravel")
        .setCategory(CreativeTabs.tabNature);
    public static final Block oreGold = new Ore(14, 4277, true)
        .setTool(ToolType.PICKAXE)
        .setHardness(3.0F)
        .setResistance(5.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("oreGold")
        .setCategory(CreativeTabs.tabNature);
    public static final Block oreIron = new Ore(15, 4276, true)
        .setTool(ToolType.PICKAXE)
        .setHardness(3.0F)
        .setResistance(5.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("oreIron")
        .setCategory(CreativeTabs.tabNature);
    public static final Block oreCoal = new Ore(16, 4103, true)
        .setTool(ToolType.PICKAXE)
        .setHardness(3.0F)
        .setResistance(5.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("oreCoal")
        .setCategory(CreativeTabs.tabNature);
    public static final Block wood = new Log(17)
        .setTool(ToolType.AXE)
        .setHardness(2.0F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("tree")
        .setCategory(CreativeTabs.tabNature);
    public static final Leaves leaves = (Leaves)new Leaves(18)
        .setTool(ToolType.SHEARS)
        .setHardness(0.2F)
        .setLightOpacity(1)
        .setStepSound(StepSoundsList.grassSound)
        .setName("leaves")
        .setCategory(CreativeTabs.tabNature);
    public static final Block sponge = new Sponge(19, 2, MaterialList.water)
        .setTool(ToolType.HOE)
        .setHardness(0.6F)
        .setStepSound(StepSoundsList.grassSound)
        .setName("sponge")
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block glass = new Glass(20, MaterialList.glass, false)
        .setTool(ToolType.PICKAXE)
        .setHardness(0.3F)
        .setStepSound(StepSoundsList.glassSound)
        .setName("glass")
        .setCategory(CreativeTabs.tabBlocks);
    public static final Block slabWood = new Slab(21, false, planks).setCategory(CreativeTabs.tabBlocks).setName("slabWood");
    public static final Block doubleSlabWood = new Slab(22, true, planks);
    public static final Block slabCobble = new Slab(23, false, cobblestone).setCategory(CreativeTabs.tabBlocks).setName("slabCobble");
    public static final Block doubleSlabCobble = new Slab(24, true, cobblestone);
    public static final Block blockCoal = new Block(25, MaterialList.rock)
        .setTool(ToolType.PICKAXE)
        .setHardness(3.0F)
        .setResistance(10.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("coal")
        .setCategory(CreativeTabs.tabBlocks);
    public static final Flower petunia = (Flower)new Flower(26)
        .setHardness(0.0F)
        .setStepSound(StepSoundsList.grassSound)
        .setName("petunia")
        .setCategory(CreativeTabs.tabNature);
    public static final Flower lilac = (Flower)new Flower(27)
        .setHardness(0.0F)
        .setStepSound(StepSoundsList.grassSound)
        .setName("lilac")
        .setCategory(CreativeTabs.tabNature);
    public static final Block reeds = new Reeds(28).setHardness(0.0F).setName("reeds").setStepSound(StepSoundsList.grassSound);
    public static final Block clay = new Clay(29)
        .setTool(ToolType.SHOVEL)
        .setHardness(0.5F)
        .setName("clay")
        .setStepSound(StepSoundsList.gravelSound)
        .setCategory(CreativeTabs.tabNature);
    public static final Block bed = new Bed(30, 4195).setTool(ToolType.AXE).setHardness(0.3F).setName("bed_red").setStepSound(StepSoundsList.woodSound);
    public static final Block fenceGate = new FenceGate(31)
        .setTool(ToolType.AXE)
        .setHardness(2.0F)
        .setResistance(5.0F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("fenceGate")
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block wall = new Wall(32, cobblestone).setCategory(CreativeTabs.tabDecoration).setName("wallStone");
    public static final Block glassPane = new WindowPane(33, MaterialList.glass)
        .setTool(ToolType.PICKAXE)
        .setHardness(0.3F)
        .setStepSound(StepSoundsList.glassSound)
        .setName("glassPane")
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block ironBars = new WindowPane(34, MaterialList.metal)
        .setTool(ToolType.PICKAXE)
        .setHardness(1.5F)
        .setStepSound(StepSoundsList.metalSound)
        .setName("ironBars")
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block cloth = new Cloth()
        .setHardness(0.8F)
        .setStepSound(StepSoundsList.clothSound)
        .setTool(ToolType.SHEARS)
        .setName("cloth")
        .setCategory(CreativeTabs.tabColor);
    public static final Block fence = new Fence(36)
        .setHardness(2.0F)
        .setStepSound(StepSoundsList.woodSound)
        .setTool(ToolType.AXE)
        .setName("fence")
        .setCategory(CreativeTabs.tabDecoration);
    public static final Flower dandelion = (Flower)new Flower(37)
        .setHardness(0.0F)
        .setStepSound(StepSoundsList.grassSound)
        .setName("flower")
        .setCategory(CreativeTabs.tabNature);
    public static final Flower rose = (Flower)new Flower(38)
        .setHardness(0.0F)
        .setStepSound(StepSoundsList.grassSound)
        .setName("rose")
        .setCategory(CreativeTabs.tabNature);
    public static final Flower mushroomBrown = (Flower)new Mushroom(39)
        .setHardness(0.0F)
        .setStepSound(StepSoundsList.grassSound)
        .setLightValue(0.125F)
        .setName("mushroom_brown")
        .setCategory(CreativeTabs.tabNature);
    public static final Flower mushroomRed = (Flower)new Mushroom(40)
        .setHardness(0.0F)
        .setStepSound(StepSoundsList.grassSound)
        .setName("mushroom_red")
        .setCategory(CreativeTabs.tabNature);
    public static final Block blockGold = new MetalBlock(41, false)
        .setTool(ToolType.PICKAXE)
        .setHardness(3.0F)
        .setResistance(10.0F)
        .setStepSound(StepSoundsList.metalSound)
        .setName("blockGold")
        .setCategory(CreativeTabs.tabBlocks);
    public static final Block blockIron = new MetalBlock(42, false)
        .setTool(ToolType.PICKAXE)
        .setHardness(5.0F)
        .setResistance(10.0F)
        .setStepSound(StepSoundsList.metalSound)
        .setName("blockIron")
        .setCategory(CreativeTabs.tabBlocks);
    public static final Block doubleSlab = new Slab(43, true, null)
        .setTool(ToolType.PICKAXE)
        .setHardness(2.0F)
        .setResistance(10.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("double_slab");
    public static final Block slabStone = new Slab(44, false, null)
        .setTool(ToolType.PICKAXE)
        .setHardness(2.0F)
        .setResistance(10.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setCategory(CreativeTabs.tabBlocks);
    public static final Block brick = new Bricks(45, MaterialList.rock)
        .setTool(ToolType.PICKAXE)
        .setHardness(2.0F)
        .setResistance(10.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("brick")
        .setCategory(CreativeTabs.tabColor);
    public static final Block tnt = new TNT(46)
        .setHardness(0.0F)
        .setStepSound(StepSoundsList.grassSound)
        .setName("tnt")
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block bookshelf = new Bookshelf(47)
        .setTool(ToolType.AXE)
        .setHardness(1.5F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("bookshelf")
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block cobblestoneMossy = new MossyCobblestone(48, MaterialList.rock)
        .setTool(ToolType.PICKAXE)
        .setHardness(2.0F)
        .setResistance(10.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("stoneMoss")
        .setCategory(CreativeTabs.tabBlocks);
    public static final Block obsidian = new Obsidian(49, MaterialList.rock)
        .setTool(ToolType.PICKAXE)
        .setHardness(10.0F)
        .setResistance(2000.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("obsidian")
        .setCategory(CreativeTabs.tabNature);
    public static final Block torch = new Torch(50)
        .setHardness(0.0F)
        .setLightValue(1.0F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("torch")
        .setCategory(CreativeTabs.tabDecoration);
    public static final Fire fire = (Fire)new Fire(51).setHardness(0.0F).setLightValue(1.0F).setStepSound(StepSoundsList.woodSound).setName("fire");
    public static final Block mobSpawner = new MobSpawner(52)
        .setTool(ToolType.PICKAXE)
        .setHardness(5.0F)
        .setStepSound(StepSoundsList.metalSound)
        .setName("mobSpawner")
        .setRarity(Rarity.RARE)
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block stairWood = new Stairs(53, planks).setCategory(CreativeTabs.tabBlocks).setName("stairWood");
    public static final Block chest = new Chest(54)
        .setTool(ToolType.AXE)
        .setHardness(2.5F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("chest")
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block cog = new Cog(55)
        .setTool(ToolType.PICKAXE)
        .setHardness(0.5F)
        .setStepSound(StepSoundsList.metalSound)
        .setName("gear")
        .setCategory(CreativeTabs.tabMachines);
    public static final Block oreDiamond = new Ore(56, 4104, true)
        .setTool(ToolType.PICKAXE)
        .setHardness(3.0F)
        .setResistance(5.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("oreDiamond")
        .setRarity(Rarity.RARE)
        .setCategory(CreativeTabs.tabNature);
    public static final Block blockDiamond = new MetalBlock(57, false)
        .setTool(ToolType.PICKAXE)
        .setHardness(5.0F)
        .setResistance(10.0F)
        .setStepSound(StepSoundsList.metalSound)
        .setName("blockDiamond")
        .setRarity(Rarity.RARE)
        .setCategory(CreativeTabs.tabBlocks);
    public static final Block workbench = new Workbench(58)
        .setTool(ToolType.AXE)
        .setHardness(2.5F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("workbench")
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block crops = new Crops(59, 7).setHardness(0.0F).setStepSound(StepSoundsList.grassSound).setName("crops");
    public static final Block tilledField = new Farmland(60)
        .setTool(ToolType.SHOVEL)
        .setHardness(0.6F)
        .setStepSound(StepSoundsList.gravelSound)
        .setName("farmland");
    public static final Block stoneOvenIdle = new Furnace(61, false)
        .setTool(ToolType.PICKAXE)
        .setHardness(3.5F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("furnace")
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block stoneOvenActive = new Furnace(62, true)
        .setTool(ToolType.PICKAXE)
        .setHardness(3.5F)
        .setStepSound(StepSoundsList.stoneSound)
        .setLightValue(0.875F)
        .setName("furnaceLit");
    public static final Block doorWood = new Door(64, 4164, 0, MaterialList.wood)
        .setTool(ToolType.AXE)
        .setHardness(3.0F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("doorWood");
    public static final Block ladder = new Ladder(65)
        .setTool(ToolType.AXE)
        .setHardness(0.4F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("ladder")
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block minecartTrack = new MinecartTrack(66)
        .setTool(ToolType.PICKAXE)
        .setHardness(1.0F)
        .setStepSound(StepSoundsList.metalSound)
        .setName("rail")
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block stairStone = new Stairs(67, cobblestone).setCategory(CreativeTabs.tabBlocks).setName("stairStone");
    public static final Block signStanding = new Sign(63, SignEntity.class, 0)
        .setTool(ToolType.AXE)
        .setHardness(1.0F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("sign");
    public static final Block signWall = new Sign(68, SignEntity.class, 1)
        .setTool(ToolType.AXE)
        .setHardness(1.0F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("sign");
    public static final Block signHanging = new Sign(69, SignEntity.class, 2)
        .setTool(ToolType.AXE)
        .setHardness(1.0F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("sign");
    public static final Block chair = new Chair(70, MaterialList.wood)
        .setTool(ToolType.AXE)
        .setHardness(1.5F)
        .setResistance(3.0F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("chair")
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block table = new Table(71, MaterialList.wood)
        .setTool(ToolType.AXE)
        .setHardness(1.5F)
        .setResistance(3.0F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("table")
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block trapdoorWood = new Trapdoor(72, MaterialList.wood)
        .setTool(ToolType.AXE)
        .setHardness(2.0F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("trapdoor")
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block stoneBrick = new StoneBricks(73)
        .setTool(ToolType.PICKAXE)
        .setHardness(2.5F)
        .setResistance(10.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("stoneBrick")
        .setCategory(CreativeTabs.tabBlocks);
    public static final Block log = new Lumber(74)
        .setTool(ToolType.AXE)
        .setHardness(2.0F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("log")
        .setCategory(CreativeTabs.tabBlocks);
    public static final Block flowerPot = new FlowerPot(75).setTool(ToolType.PICKAXE).setHardness(0.5F).setStepSound(StepSoundsList.potSound).setName("pot");
    public static final Block cake = new Cake(76).setTool(ToolType.HOE).setHardness(0.5F).setStepSound(StepSoundsList.clothSound).setName("pieTop");
    public static final Block steelOvenIdle = new BlastFurnace(77, false)
        .setTool(ToolType.PICKAXE)
        .setHardness(3.5F)
        .setStepSound(StepSoundsList.metalSound)
        .setName("blastFurnace")
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block steelOvenActive = new BlastFurnace(78, true)
        .setTool(ToolType.PICKAXE)
        .setHardness(3.5F)
        .setStepSound(StepSoundsList.metalSound)
        .setLightValue(0.875F)
        .setName("blastFurnaceLit");
    public static final Block cactus = new Cactus(79)
        .setTool(ToolType.HOE)
        .setHardness(0.4F)
        .setStepSound(StepSoundsList.clothSound)
        .setName("cactus")
        .setCategory(CreativeTabs.tabNature);
    public static final Block cobweb = new Cobweb(80, MaterialList.web)
        .setTool(ToolType.SHEARS)
        .setHardness(1.5F)
        .setStepSound(StepSoundsList.clothSound)
        .setName("web")
        .setCategory(CreativeTabs.tabNature);
    public static final Block pathBlock = new PathBlock(81).setTool(ToolType.SHOVEL).setHardness(0.6F).setStepSound(StepSoundsList.grassSound).setName("path");
    public static final Block carpet = new Carpet()
        .setTool(ToolType.SHEARS)
        .setHardness(0.5F)
        .setStepSound(StepSoundsList.clothSound)
        .setName("carpet")
        .setCategory(CreativeTabs.tabColor);
    public static final Block glassStained = new ColoredGlass(83, MaterialList.glass, false)
        .setTool(ToolType.PICKAXE)
        .setHardness(0.3F)
        .setName("glassStained")
        .setStepSound(StepSoundsList.glassSound)
        .setCategory(CreativeTabs.tabColor);
    public static final Block railBooster = new MinecartTrack(84)
        .setTool(ToolType.PICKAXE)
        .setName("railPowered")
        .setHardness(0.7F)
        .setStepSound(StepSoundsList.metalSound)
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block barrel = new Barrel(85)
        .setTool(ToolType.AXE)
        .setHardness(2.5F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("barrel")
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block rope = new Rope(86, MaterialList.rope)
        .setStepSound(StepSoundsList.clothSound)
        .setName("rope")
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block slimeBlock = new SlimeBlock(87, MaterialList.slime)
        .setTool(ToolType.HOE)
        .setHardness(0.5F)
        .setStepSound(StepSoundsList.slimeSound)
        .setName("slimeBlock")
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block jukeBox = new Jukebox(88)
        .setTool(ToolType.AXE)
        .setHardness(2.0F)
        .setResistance(10.0F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("jukebox")
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block obsidianBrick = new ObsidianBricks(89)
        .setTool(ToolType.PICKAXE)
        .setHardness(5.0F)
        .setResistance(2000.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("obsidianBrick")
        .setCategory(CreativeTabs.tabBlocks);
    public static final Block generator = new GeneratorBlock(90, false)
        .setTool(ToolType.PICKAXE)
        .setHardness(4.0F)
        .setResistance(10.0F)
        .setName("generator")
        .setStepSound(StepSoundsList.metalSound)
        .setCategory(CreativeTabs.tabMachines);
    public static final Block generatorActive = new GeneratorBlock(91, true)
        .setTool(ToolType.PICKAXE)
        .setHardness(4.0F)
        .setResistance(10.0F)
        .setLightValue(0.875F)
        .setName("activeGenerator")
        .setStepSound(StepSoundsList.metalSound);
    public static final Block transformer = new Transformer(92, MaterialList.metal)
        .setTool(ToolType.PICKAXE)
        .setName("transformer")
        .setHardness(2.0F)
        .setResistance(4.0F)
        .setStepSound(StepSoundsList.metalSound)
        .setCategory(CreativeTabs.tabMachines);
    public static final Block regulator = new Transformer(93, MaterialList.metal)
        .setTool(ToolType.PICKAXE)
        .setName("regulator")
        .setHardness(2.0F)
        .setResistance(4.0F)
        .setStepSound(StepSoundsList.metalSound)
        .setCategory(CreativeTabs.tabMachines);
    public static final Block detector = new Detector(94, MaterialList.metal)
        .setTool(ToolType.PICKAXE)
        .setHardness(2.0F)
        .setStepSound(StepSoundsList.metalSound)
        .setName("detector")
        .setCategory(CreativeTabs.tabMachines);
    public static final Block pulleyBase = new Piston(95, false, false).setTool(ToolType.PICKAXE).setName("piston").setCategory(CreativeTabs.tabMachines);
    public static final Block pulleyStickyBase = new Piston(96, true, false)
        .setTool(ToolType.PICKAXE)
        .setName("pistonSticky")
        .setCategory(CreativeTabs.tabMachines);
    public static final PistonHead pulleyExtension = new PistonHead(97);
    public static final MovingPiston pulleyMoving = new MovingPiston(98);
    public static final Block pulleyBaseActive = new Piston(99, false, true).setTool(ToolType.PICKAXE).setName("piston");
    public static final Block pulleyStickyBaseActive = new Piston(100, true, true).setTool(ToolType.PICKAXE).setName("pistonSticky");
    public static final Block vacuum = new Vacuum(101)
        .setName("vacuum")
        .setTool(ToolType.PICKAXE)
        .setHardness(2.0F)
        .setResistance(4.0F)
        .setStepSound(StepSoundsList.metalSound)
        .setCategory(CreativeTabs.tabMachines);
    public static final Block doorSteel = new Door(102, 4206, 2, MaterialList.metal)
        .setTool(ToolType.PICKAXE)
        .setHardness(5.0F)
        .setStepSound(StepSoundsList.metalSound)
        .setName("doorSteel");
    public static final Block trapdoorSteel = new Trapdoor(103, MaterialList.metal)
        .setTool(ToolType.PICKAXE)
        .setHardness(2.0F)
        .setStepSound(StepSoundsList.metalSound)
        .setName("trapdoorSteel")
        .setCategory(CreativeTabs.tabMachines);
    public static final Block noteBlock = new NoteBlock(104)
        .setName("noteBlock")
        .setTool(ToolType.AXE)
        .setHardness(0.8F)
        .setStepSound(StepSoundsList.woodSound)
        .setCategory(CreativeTabs.tabMachines);
    public static final Block dispenser = new Dispenser(105)
        .setName("dispenser")
        .setTool(ToolType.PICKAXE)
        .setHardness(0.8F)
        .setStepSound(StepSoundsList.metalSound)
        .setCategory(CreativeTabs.tabMachines);
    public static final Block plantern = new Plantern(106, MaterialList.pumpkin)
        .setTool(ToolType.AXE)
        .setHardness(1.0F)
        .setLightValue(1.0F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("plantern")
        .setCategory(CreativeTabs.tabBlocks);
    public static final Block doorGlass = new Door(107, 4220, 4, MaterialList.glass)
        .setTool(ToolType.AXE)
        .setHardness(1.5F)
        .setStepSound(StepSoundsList.glassSound)
        .setName("doorGlass");
    public static final Block trapdoorGlass = new Trapdoor(108, MaterialList.glass)
        .setTool(ToolType.AXE)
        .setHardness(1.0F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("trapdoorGlass")
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block concrete = new Block(109, MaterialList.rock)
        .setTool(ToolType.PICKAXE)
        .setHardness(2.0F)
        .setResistance(10.0F)
        .setName("stoneslab_top")
        .setStepSound(StepSoundsList.stoneSound)
        .setCategory(CreativeTabs.tabBlocks);
    public static final Block deadBush = new Shrub(110)
        .setTool(ToolType.SHEARS)
        .setHardness(0.0F)
        .setName("shrub")
        .setStepSound(StepSoundsList.grassSound)
        .setCategory(CreativeTabs.tabNature);
    public static final Block sandStone = new Sandstone(111)
        .setTool(ToolType.PICKAXE)
        .setHardness(1.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("sandstone")
        .setCategory(CreativeTabs.tabBlocks);
    public static final Block quickSand = new Sand(112, MaterialList.quicksand)
        .setTool(ToolType.SHOVEL)
        .setHardness(0.7F)
        .setStepSound(StepSoundsList.sandSound)
        .setName("quickSand")
        .setCategory(CreativeTabs.tabNature);
    public static final Block doorBirch = new Door(113, 4183, 6, MaterialList.wood)
        .setTool(ToolType.AXE)
        .setHardness(3.0F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("doorBirch");
    public static final Block doorSpruce = new Door(114, 4184, 8, MaterialList.wood)
        .setTool(ToolType.AXE)
        .setHardness(3.0F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("doorSpruce");
    public static final Block doorPalm = new Door(115, 4185, 10, MaterialList.wood)
        .setTool(ToolType.AXE)
        .setHardness(3.0F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("doorPalm");
    public static final Block tallGrass = new TallGrass(116)
        .setTool(ToolType.SHEARS)
        .setHardness(0.0F)
        .setStepSound(StepSoundsList.grassSound)
        .setName("tallGrass")
        .setCategory(CreativeTabs.tabNature);
    public static final Block snowLayer = new LayerBlock(117, MaterialList.snow)
        .setTool(ToolType.SHOVEL)
        .setHardness(0.1F)
        .setStepSound(StepSoundsList.snowSound)
        .setName("snow")
        .setCategory(CreativeTabs.tabNature);
    public static final Block snowBlock = new Snow(118, MaterialList.snow)
        .setTool(ToolType.SHOVEL)
        .setHardness(0.5F)
        .setStepSound(StepSoundsList.snowSound)
        .setName("snow")
        .setCategory(CreativeTabs.tabNature);
    public static final Block ice = new Ice(119, MaterialList.ice)
        .setTool(ToolType.PICKAXE)
        .setHardness(0.3F)
        .setLightOpacity(1)
        .setStepSound(StepSoundsList.glassSound)
        .setName("ice")
        .setCategory(CreativeTabs.tabNature);
    public static final Flower berryBush = (Flower)new BerryBush(120)
        .setHardness(0.0F)
        .setName("bush")
        .setStepSound(StepSoundsList.grassSound)
        .setCategory(CreativeTabs.tabNature);
    public static final Block mudBrick = new Block(121, MaterialList.ground)
        .setTool(ToolType.SHOVEL)
        .setHardness(0.5F)
        .setStepSound(StepSoundsList.gravelSound)
        .setName("mudBricks")
        .setCategory(CreativeTabs.tabBlocks);
    public static final Block doorAcacia = new Door(122, 4188, 12, MaterialList.wood)
        .setTool(ToolType.AXE)
        .setHardness(3.0F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("doorAcacia");
    public static final Block doorJungle = new Door(123, 4189, 14, MaterialList.wood)
        .setTool(ToolType.AXE)
        .setHardness(3.0F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("doorJungle");
    public static final Block doorMangrove = new Door(124, 4190, 16, MaterialList.wood)
        .setTool(ToolType.AXE)
        .setHardness(3.0F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("doorMangrove");
    public static final Block doorBaobab = new Door(125, 4191, 18, MaterialList.wood)
        .setTool(ToolType.AXE)
        .setHardness(3.0F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("doorBaobab");
    public static final Block hayBlock = new Pillar(126, MaterialList.ground)
        .setTool(ToolType.HOE)
        .setHardness(0.5F)
        .setName("hay")
        .setStepSound(StepSoundsList.grassSound)
        .setCategory(CreativeTabs.tabBlocks);
    public static final Block pumpkin = new Fruit(127)
        .setHardness(1.0F)
        .setTool(ToolType.AXE)
        .setStepSound(StepSoundsList.woodSound)
        .setName("pumpkin")
        .setCategory(CreativeTabs.tabNature);
    public static final Block melon = new Fruit(128)
        .setHardness(1.0F)
        .setTool(ToolType.AXE)
        .setStepSound(StepSoundsList.woodSound)
        .setName("melon")
        .setCategory(CreativeTabs.tabNature);
    public static final Block pumpkinStem = new Stem(129, pumpkin).setHardness(0.0F).setName("pumpkinStem").setStepSound(StepSoundsList.grassSound);
    public static final Block melonStem = new Stem(130, melon).setHardness(0.0F).setName("melonStem").setStepSound(StepSoundsList.grassSound);
    public static final Block vine = new Vine(131)
        .setTool(ToolType.SHEARS)
        .setHardness(0.3F)
        .setStepSound(StepSoundsList.grassSound)
        .setName("vine")
        .setCategory(CreativeTabs.tabNature);
    public static final Block lilyPad = new LilyPad(132).setStepSound(StepSoundsList.grassSound).setName("lilyPad").setCategory(CreativeTabs.tabNature);
    public static final Block roots = new HangingVine(133)
        .setTool(ToolType.SHEARS)
        .setHardness(0.0F)
        .setName("crimsonRoots")
        .setStepSound(StepSoundsList.grassSound)
        .setCategory(CreativeTabs.tabNature);
    public static final Block mushroomCap = new MushroomCap(134)
        .setTool(ToolType.AXE)
        .setHardness(1.0F)
        .setName("cap")
        .setStepSound(StepSoundsList.woodSound)
        .setCategory(CreativeTabs.tabNature);
    public static final Flower mushroomGlowing = (Flower)new Mushroom(135)
        .setHardness(0.0F)
        .setStepSound(StepSoundsList.grassSound)
        .setLightValue(0.7F)
        .setName("gloshroom")
        .setCategory(CreativeTabs.tabNature);
    public static final Block coral = new Coral(136, MaterialList.wood)
        .setTool(ToolType.HOE)
        .setHardness(0.5F)
        .setName("coral")
        .setStepSound(StepSoundsList.grassSound)
        .setCategory(CreativeTabs.tabNature);
    public static final Block magma = new Magma(137, MaterialList.magma)
        .setTool(ToolType.PICKAXE)
        .setHardness(2.0F)
        .setName("magma")
        .setLightValue(0.75F)
        .setStepSound(StepSoundsList.stoneSound)
        .setCategory(CreativeTabs.tabNature);
    public static final Block seaweed = new Seaweed(138)
        .setLightOpacity(3)
        .setName("seaweed")
        .setStepSound(StepSoundsList.grassSound)
        .setCategory(CreativeTabs.tabNature);
    public static final Block carrots = new Crops(139, 3).setHardness(0.0F).setStepSound(StepSoundsList.grassSound).setName("carrots");
    public static final Block potatoes = new Crops(140, 3).setHardness(0.0F).setStepSound(StepSoundsList.grassSound).setName("potatoes");
    public static final Block cotton = new Crops(141, 3).setHardness(0.0F).setStepSound(StepSoundsList.grassSound).setName("cotton");
    public static final Block coralFan = new CoralFan(142)
        .setHardness(0.5F)
        .setName("coralFan")
        .setLightValue(0.75F)
        .setStepSound(StepSoundsList.grassSound)
        .setCategory(CreativeTabs.tabNature);
    public static final Block flowerPetal = new FlowerPetal(143)
        .setTool(ToolType.AXE)
        .setHardness(1.0F)
        .setName("petal")
        .setStepSound(StepSoundsList.woodSound)
        .setCategory(CreativeTabs.tabNature);
    public static final Block packedIce = new PackedIce(144, MaterialList.ice)
        .setTool(ToolType.PICKAXE)
        .setHardness(0.5F)
        .setName("ice_packed")
        .setStepSound(StepSoundsList.glassSound)
        .setCategory(CreativeTabs.tabNature);
    public static final Block redSand = new Sand(145, MaterialList.sand)
        .setTool(ToolType.SHOVEL)
        .setHardness(0.5F)
        .setStepSound(StepSoundsList.sandSound)
        .setName("red_sand")
        .setCategory(CreativeTabs.tabNature);
    public static final Block hardenedClay = new HardenedClay(146, MaterialList.rock)
        .setTool(ToolType.PICKAXE)
        .setHardness(1.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("hardened_clay")
        .setCategory(CreativeTabs.tabColor);
    public static final Block ash = new LayerBlock(147, MaterialList.ash)
        .setTool(ToolType.SHOVEL)
        .setHardness(0.1F)
        .setStepSound(StepSoundsList.clothSound)
        .setName("ash")
        .setCategory(CreativeTabs.tabNature);
    public static final Block moss = new Moss(148, MaterialList.ground)
        .setTool(ToolType.HOE)
        .setHardness(0.3F)
        .setStepSound(StepSoundsList.grassSound)
        .setName("moss")
        .setCategory(CreativeTabs.tabNature);
    public static final Block bone = new Pillar(149, MaterialList.bone)
        .setTool(ToolType.AXE)
        .setHardness(0.8F)
        .setStepSound(StepSoundsList.boneSound)
        .setName("bone")
        .setCategory(CreativeTabs.tabNature);
    public static final Block doorScorched = new Door(150, 4233, 20, MaterialList.wood)
        .setTool(ToolType.AXE)
        .setHardness(3.0F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("doorScorched");
    public static final Block doorMaple = new Door(151, 4235, 22, MaterialList.wood)
        .setTool(ToolType.AXE)
        .setHardness(3.0F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("doorMaple");
    public static final Block doorCherry = new Door(152, 4236, 24, MaterialList.wood)
        .setTool(ToolType.AXE)
        .setHardness(3.0F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("doorCherry");
    public static final Block powderSnow = new PowderSnow(153, MaterialList.quicksand)
        .setTool(ToolType.SHOVEL)
        .setHardness(0.3F)
        .setStepSound(StepSoundsList.snowSound)
        .setName("snowPowder")
        .setCategory(CreativeTabs.tabNature);
    public static final Block silverSand = new Sand(154, MaterialList.sand)
        .setTool(ToolType.SHOVEL)
        .setHardness(0.5F)
        .setStepSound(StepSoundsList.sandSound)
        .setName("silverSand")
        .setCategory(CreativeTabs.tabNature);
    public static final Block bamboo = new BambooStalk(155)
        .setHardness(0.3F)
        .setTool(ToolType.AXE)
        .setStepSound(StepSoundsList.boneSound)
        .setName("bambooStalk");
    public static final Block bambooBlock = new Pillar(156, MaterialList.wood)
        .setTool(ToolType.AXE)
        .setHardness(1.3F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("bamboo")
        .setCategory(CreativeTabs.tabBlocks);
    public static final Block leafPile = new LeafPile(157, MaterialList.leaves)
        .setTool(ToolType.SHEARS)
        .setHardness(0.1F)
        .setStepSound(StepSoundsList.grassSound)
        .setName("leafPile")
        .setCategory(CreativeTabs.tabNature);
    public static final Block doorMahogany = new Door(158, 4238, 26, MaterialList.wood)
        .setTool(ToolType.AXE)
        .setHardness(3.0F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("doorMahogany");
    public static final Block doorOldenOak = new Door(159, 4239, 28, MaterialList.wood)
        .setTool(ToolType.AXE)
        .setHardness(3.0F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("doorOldenoak");
    public static final Block tallTorch = new TikiTorch(160)
        .setHardness(0.0F)
        .setLightValue(1.0F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("tikiTorch")
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block spike = new StoneSpike(161, MaterialList.rock)
        .setTool(ToolType.PICKAXE)
        .setHardness(1.5F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("spike")
        .setCategory(CreativeTabs.tabNature);
    public static final Block chain = new Chain(162)
        .setTool(ToolType.PICKAXE)
        .setHardness(0.5F)
        .setResistance(10.0F)
        .setStepSound(StepSoundsList.metalSound)
        .setName("chain")
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block icicle = new StoneSpike(163, MaterialList.ice)
        .setTool(ToolType.PICKAXE)
        .setHardness(0.5F)
        .setStepSound(StepSoundsList.glassSound)
        .setName("spikeIce")
        .setCategory(CreativeTabs.tabNature);
    public static final Block glowLychen = new GlowLychen(164)
        .setTool(ToolType.SHEARS)
        .setLightValue(0.9F)
        .setHardness(0.0F)
        .setStepSound(StepSoundsList.grassSound)
        .setName("glowLychen")
        .setCategory(CreativeTabs.tabNature);
    public static final Block caveVine = new HangingVine(165)
        .setTool(ToolType.SHEARS)
        .setHardness(0.3F)
        .setStepSound(StepSoundsList.grassSound)
        .setName("caveVine")
        .setCategory(CreativeTabs.tabNature);
    public static final Block oreRuby = new Ore(166, 4266, true)
        .setTool(ToolType.PICKAXE)
        .setHardness(3.0F)
        .setResistance(5.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("oreRuby")
        .setRarity(Rarity.RARE)
        .setCategory(CreativeTabs.tabNature);
    public static final Block oreSapphire = new Ore(167, 4267, true)
        .setTool(ToolType.PICKAXE)
        .setHardness(3.0F)
        .setResistance(5.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("oreSapphire")
        .setRarity(Rarity.RARE)
        .setCategory(CreativeTabs.tabNature);
    public static final Block oreEmerald = new Ore(168, 4268, true)
        .setTool(ToolType.PICKAXE)
        .setHardness(3.0F)
        .setResistance(5.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("oreEmerald")
        .setRarity(Rarity.RARE)
        .setCategory(CreativeTabs.tabNature);
    public static final Block blockRuby = new MetalBlock(169, false)
        .setTool(ToolType.PICKAXE)
        .setHardness(5.0F)
        .setResistance(10.0F)
        .setStepSound(StepSoundsList.metalSound)
        .setName("blockRuby")
        .setRarity(Rarity.RARE)
        .setCategory(CreativeTabs.tabBlocks);
    public static final Block blockSapphire = new MetalBlock(170, false)
        .setTool(ToolType.PICKAXE)
        .setHardness(5.0F)
        .setResistance(10.0F)
        .setStepSound(StepSoundsList.metalSound)
        .setName("blockSapphire")
        .setRarity(Rarity.RARE)
        .setCategory(CreativeTabs.tabBlocks);
    public static final Block blockEmerald = new MetalBlock(171, false)
        .setTool(ToolType.PICKAXE)
        .setHardness(5.0F)
        .setResistance(10.0F)
        .setStepSound(StepSoundsList.metalSound)
        .setName("blockEmerald")
        .setRarity(Rarity.RARE)
        .setCategory(CreativeTabs.tabBlocks);
    public static final Block clusterBlock = new GemCluster(172)
        .setTool(ToolType.PICKAXE)
        .setHardness(3.0F)
        .setResistance(5.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("cluster")
        .setRarity(Rarity.RARE)
        .setCategory(CreativeTabs.tabNature);
    public static final Block crystalBlock = new Crystal(173)
        .setTool(ToolType.PICKAXE)
        .setLightValue(0.8F)
        .setHardness(0.3F)
        .setStepSound(StepSoundsList.glassSound)
        .setName("crystal")
        .setRarity(Rarity.RARE)
        .setCategory(CreativeTabs.tabNature);
    public static final Block lantern = new Lantern(174)
        .setTool(ToolType.PICKAXE)
        .setLightValue(1.0F)
        .setHardness(1.0F)
        .setStepSound(StepSoundsList.metalSound)
        .setName("lantern")
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block anvil = new Anvil(175)
        .setTool(ToolType.PICKAXE)
        .setHardness(5.0F)
        .setStepSound(StepSoundsList.metalSound)
        .setResistance(2000.0F)
        .setName("anvil")
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block silverfish = new InfestedStone(176)
        .setTool(ToolType.PICKAXE)
        .setHardness(1.5F)
        .setResistance(10.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("stoneInfested")
        .setCategory(CreativeTabs.tabBlocks);
    public static final Block cauldron = new Cauldron(177)
        .setTool(ToolType.PICKAXE)
        .setHardness(3.0F)
        .setStepSound(StepSoundsList.metalSound)
        .setResistance(2000.0F)
        .setName("cauldron");
    public static final Block megaSponge = new Sponge(178, 5, MaterialList.water)
        .setTool(ToolType.HOE)
        .setHardness(0.6F)
        .setStepSound(StepSoundsList.grassSound)
        .setName("spongeSuper")
        .setRarity(Rarity.EPIC)
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block lockedChest = new LockedChest(179)
        .setTool(ToolType.PICKAXE)
        .setRarity(Rarity.RARE)
        .setHardness(1.0F)
        .setResistance(6000000.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("lockedChest")
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block observer = new Observer(180, MaterialList.metal)
        .setTool(ToolType.PICKAXE)
        .setHardness(2.0F)
        .setStepSound(StepSoundsList.metalSound)
        .setName("observer")
        .setCategory(CreativeTabs.tabMachines);
    public static final Block pebble = new Pebble(181, MaterialList.circuits).setHardness(0.0F).setStepSound(StepSoundsList.stoneSound).setName("rock");
    public static final Block teleporter = new Teleporter(182, MaterialList.metal)
        .setTool(ToolType.PICKAXE)
        .setHardness(1.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setRarity(Rarity.RARE)
        .setName("teleporter")
        .setCategory(CreativeTabs.tabMachines);
    public static final Block seaLantern = new Block(183, MaterialList.glass)
        .setTool(ToolType.PICKAXE)
        .setLightValue(0.9F)
        .setHardness(1.2F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("seaLantern")
        .setCategory(CreativeTabs.tabBlocks);
    public static final Block rod = new LightningRod(184, MaterialList.metal)
        .setTool(ToolType.PICKAXE)
        .setName("lightningRod")
        .setHardness(1.5F)
        .setResistance(2.0F)
        .setStepSound(StepSoundsList.metalSound)
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block pot = new Pot(185, MaterialList.circuits)
        .setTool(ToolType.PICKAXE)
        .setHardness(0.3F)
        .setStepSound(StepSoundsList.potSound)
        .setName("bigPot")
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block barrier = new EmptyBlock(186, MaterialList.air)
        .setHardness(-1.0F)
        .setResistance(6000000.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("barrier")
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block light = new EmptyBlock(187, MaterialList.air)
        .setHardness(-1.0F)
        .setResistance(6000000.0F)
        .setLightValue(1.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("light")
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block daylightSensor = new SolarPanel(188, MaterialList.circuits)
        .setTool(ToolType.PICKAXE)
        .setHardness(1.3F)
        .setStepSound(StepSoundsList.metalSound)
        .setName("daylightSensor")
        .setCategory(CreativeTabs.tabMachines);
    public static final Block duneGrass = new TallGrass(189)
        .setTool(ToolType.SHEARS)
        .setHardness(0.0F)
        .setStepSound(StepSoundsList.grassSound)
        .setName("duneGrass")
        .setCategory(CreativeTabs.tabNature);
    public static final Block sandLayer = new LayerBlock(190, MaterialList.sand)
        .setTool(ToolType.SHOVEL)
        .setHardness(0.1F)
        .setStepSound(StepSoundsList.sandSound)
        .setName("sand")
        .setCategory(CreativeTabs.tabNature);
    public static final Block netherrack = new Block(191, MaterialList.rock)
        .setTool(ToolType.PICKAXE)
        .setHardness(0.4F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("netherrack")
        .setCategory(CreativeTabs.tabNature);
    public static final Block slowSand = new SlowSand(192, MaterialList.soulsand)
        .setTool(ToolType.SHOVEL)
        .setHardness(0.5F)
        .setStepSound(StepSoundsList.sandSound)
        .setName("soulsand")
        .setCategory(CreativeTabs.tabNature);
    public static final Block glowStone = new Block(193, MaterialList.glass)
        .setTool(ToolType.PICKAXE)
        .setHardness(0.3F)
        .setStepSound(StepSoundsList.glassSound)
        .setName("glowstone")
        .setLightValue(1.0F)
        .setCategory(CreativeTabs.tabNature);
    public static final Block blockDamascus = new MetalBlock(194, true)
        .setTool(ToolType.PICKAXE)
        .setHardness(5.0F)
        .setResistance(10.0F)
        .setStepSound(StepSoundsList.metalSound)
        .setName("blockDamascus")
        .setRarity(Rarity.EPIC)
        .setCategory(CreativeTabs.tabBlocks);
    public static final Block hellforge = new Hellforge(195)
        .setTool(ToolType.PICKAXE)
        .setHardness(1.5F)
        .setStepSound(StepSoundsList.stoneSound)
        .setLightValue(0.875F)
        .setRarity(Rarity.EPIC)
        .setName("hellforge")
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block lamp = new Lamp(196, MaterialList.glass)
        .setTool(ToolType.PICKAXE)
        .setName("lamp")
        .setHardness(0.5F)
        .setStepSound(StepSoundsList.glassSound)
        .setCategory(CreativeTabs.tabMachines);
    public static final Block lampLit = new Lamp(197, MaterialList.glass)
        .setTool(ToolType.PICKAXE)
        .setName("lampLit")
        .setHardness(0.5F)
        .setLightValue(1.0F)
        .setStepSound(StepSoundsList.glassSound);
    public static final Block battery = new Battery(198, MaterialList.battery)
        .setTool(ToolType.PICKAXE)
        .setName("battery")
        .setHardness(1.0F)
        .setStepSound(StepSoundsList.metalSound)
        .setCategory(CreativeTabs.tabMachines);
    public static final Block fan = new Fan(199)
        .setName("fan")
        .setTool(ToolType.PICKAXE)
        .setHardness(2.0F)
        .setResistance(4.0F)
        .setStepSound(StepSoundsList.metalSound)
        .setCategory(CreativeTabs.tabMachines);
    public static final Block lavaSponge = new Sponge(200, 3, MaterialList.lava)
        .setTool(ToolType.HOE)
        .setHardness(0.6F)
        .setStepSound(StepSoundsList.grassSound)
        .setName("spongeLava")
        .setRarity(Rarity.EPIC)
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block netherGold = new Ore(201, 4255, false)
        .setTool(ToolType.PICKAXE)
        .setHardness(0.7F)
        .setResistance(5.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("gildedBloodstone")
        .setCategory(CreativeTabs.tabNature);
    public static final Block oreDamascus = new Ore(202, 4334, false)
        .setTool(ToolType.PICKAXE)
        .setHardness(1.5F)
        .setResistance(5.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("oreDamascus")
        .setRarity(Rarity.EPIC)
        .setCategory(CreativeTabs.tabNature);
    public static final Block shroomLight = new MushroomCap(203)
        .setTool(ToolType.AXE)
        .setHardness(0.5F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("shroomlight")
        .setLightValue(1.0F)
        .setCategory(CreativeTabs.tabNature);
    public static final Hellfire hellfire = (Hellfire)new Hellfire(204)
        .setHardness(0.0F)
        .setLightValue(1.0F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("hellfire");
    public static final Flower witherRose = (Flower)new Flower(205)
        .setHardness(0.0F)
        .setStepSound(StepSoundsList.grassSound)
        .setName("witherRose")
        .setCategory(CreativeTabs.tabNature);
    public static final Flower fireFlower = (Flower)new Flower(206)
        .setHardness(0.0F)
        .setLightValue(1.0F)
        .setStepSound(StepSoundsList.grassSound)
        .setName("torchFlower")
        .setCategory(CreativeTabs.tabNature);
    public static final Flower mushroomNylium = (Flower)new Mushroom(207)
        .setHardness(0.0F)
        .setStepSound(StepSoundsList.grassSound)
        .setLightValue(0.7F)
        .setName("mushroomNylium")
        .setCategory(CreativeTabs.tabNature);
    public static final Flower mushroomCrimson = (Flower)new Mushroom(208)
        .setHardness(0.0F)
        .setStepSound(StepSoundsList.grassSound)
        .setLightValue(0.5F)
        .setName("mushroomCrimson")
        .setCategory(CreativeTabs.tabNature);
    public static final Block obsidianCrying = new RespawnAnchor(209, MaterialList.rock)
        .setTool(ToolType.PICKAXE)
        .setHardness(5.0F)
        .setResistance(1000.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("obsidianCrying")
        .setRarity(Rarity.EPIC)
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block oreAmethyst = new Ore(210, 4335, false)
        .setTool(ToolType.PICKAXE)
        .setHardness(1.5F)
        .setResistance(5.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("oreAmethyst")
        .setRarity(Rarity.EPIC)
        .setCategory(CreativeTabs.tabNature);
    public static final Block blockAmethyst = new MetalBlock(211, false)
        .setTool(ToolType.PICKAXE)
        .setHardness(5.0F)
        .setResistance(10.0F)
        .setStepSound(StepSoundsList.metalSound)
        .setName("blockAmethyst")
        .setRarity(Rarity.EPIC)
        .setCategory(CreativeTabs.tabBlocks);
    public static final Block glowRoots = new Roots(212).setHardness(0.0F).setLightValue(0.8F).setName("glowBerry").setStepSound(StepSoundsList.grassSound);
    public static final Block candle = new Candle(213)
        .setHardness(0.0F)
        .setLightValue(1.0F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("candle")
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block composter = new Composter(214)
        .setTool(ToolType.AXE)
        .setHardness(0.6F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("composter")
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block flag = new Flag(215).setTool(ToolType.AXE).setHardness(0.3F).setStepSound(StepSoundsList.woodSound).setName("flag");
    public static final Block railStopper = new MinecartTrack(216)
        .setTool(ToolType.PICKAXE)
        .setName("railStopper")
        .setHardness(0.7F)
        .setStepSound(StepSoundsList.metalSound)
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block doorCrimson = new Door(217, 4349, 30, MaterialList.wood)
        .setTool(ToolType.AXE)
        .setHardness(3.0F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("doorCrimson");
    public static final Block doorNylium = new Door(218, 4350, 32, MaterialList.wood)
        .setTool(ToolType.AXE)
        .setHardness(3.0F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("doorNylium");
    public static final Block oreCopper = new Ore(219, 4364, true)
        .setTool(ToolType.PICKAXE)
        .setHardness(3.0F)
        .setResistance(5.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("oreCopper")
        .setCategory(CreativeTabs.tabNature);
    public static final Block blockCopper = new MetalBlock(220, false)
        .setTool(ToolType.PICKAXE)
        .setHardness(5.0F)
        .setResistance(10.0F)
        .setStepSound(StepSoundsList.metalSound)
        .setName("blockCopper")
        .setCategory(CreativeTabs.tabBlocks);
    public static final Block susSand = new Sand(221, MaterialList.sand)
        .setTool(ToolType.SHOVEL)
        .setHardness(0.5F)
        .setStepSound(StepSoundsList.susSandSound)
        .setName("sus_sand")
        .setCategory(CreativeTabs.tabNature);
    public static final Block blockTopaz = new MetalBlock(222, false)
        .setTool(ToolType.PICKAXE)
        .setHardness(5.0F)
        .setResistance(10.0F)
        .setStepSound(StepSoundsList.metalSound)
        .setName("blockTopaz")
        .setRarity(Rarity.EPIC)
        .setCategory(CreativeTabs.tabBlocks);
    public static final Block copperGrating = new WindowPane(223, MaterialList.metal)
        .setTool(ToolType.PICKAXE)
        .setHardness(1.5F)
        .setStepSound(StepSoundsList.metalSound)
        .setName("grate")
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block susGravel = new Sand(224, MaterialList.sand)
        .setTool(ToolType.SHOVEL)
        .setHardness(0.5F)
        .setStepSound(StepSoundsList.susSound)
        .setName("sus_gravel")
        .setCategory(CreativeTabs.tabNature);
    public static final Block smokerIdle = new Smoker(225, false)
        .setTool(ToolType.PICKAXE)
        .setHardness(3.5F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("smoker")
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block smokerActive = new Smoker(226, true)
        .setTool(ToolType.PICKAXE)
        .setHardness(3.5F)
        .setStepSound(StepSoundsList.stoneSound)
        .setLightValue(0.875F)
        .setName("smokerLit");
    public static final Block lectern = new Lectern(227)
        .setTool(ToolType.AXE)
        .setHardness(1.25F)
        .setResistance(5.0F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("enchanter")
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block stonecutter = new Stonecutter(228)
        .setTool(ToolType.PICKAXE)
        .setHardness(3.5F)
        .setResistance(10.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("stonecutter")
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block panelStone = new Panel(229, null)
        .setTool(ToolType.PICKAXE)
        .setHardness(2.0F)
        .setResistance(10.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setCategory(CreativeTabs.tabBlocks);
    public static final Block panelWood = new Panel(230, planks).setCategory(CreativeTabs.tabBlocks).setName("panelWood");
    public static final Block crafter = new Crafter(231)
        .setTool(ToolType.PICKAXE)
        .setHardness(3.5F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("crafter")
        .setCategory(CreativeTabs.tabMachines);
    public static final Block beetroots = new Crops(232, 3).setHardness(0.0F).setStepSound(StepSoundsList.grassSound).setName("beetroot");
    public static final Block ember = new Block(233, MaterialList.wood)
        .setTool(ToolType.AXE)
        .setHardness(1.0F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("ember")
        .setCategory(CreativeTabs.tabNature);
    public static final Block grave = new Grave(234, MaterialList.rock)
        .setTool(ToolType.PICKAXE)
        .setHardness(1.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("grave")
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block skull = new Skull(235)
        .setTool(ToolType.AXE)
        .setHardness(1.0F)
        .setStepSound(StepSoundsList.boneSound)
        .setName("skull")
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block jumpPad = new Clover(236, MaterialList.plants)
        .setHardness(0.0F)
        .setStepSound(StepSoundsList.grassSound)
        .setName("jumppad")
        .setCategory(CreativeTabs.tabNature);
    public static final Block cloud = new CloudBlock(237, MaterialList.cloud)
        .setHardness(0.3F)
        .setLightOpacity(1)
        .setStepSound(StepSoundsList.clothSound)
        .setName("cloud")
        .setCategory(CreativeTabs.tabNature);
    public static final Block oreTopaz = new Ore(238, 4240, false)
        .setTool(ToolType.PICKAXE)
        .setHardness(3.0F)
        .setResistance(5.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("oreTopaz")
        .setRarity(Rarity.RARE)
        .setCategory(CreativeTabs.tabNature);
    public static final Block oreAdamantine = new Ore(239, 239, false)
        .setTool(ToolType.PICKAXE)
        .setHardness(3.0F)
        .setResistance(5.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("oreAdamantine")
        .setRarity(Rarity.EPIC)
        .setCategory(CreativeTabs.tabNature);
    public static final Block aetherIron = new Ore(240, 4256, false)
        .setTool(ToolType.PICKAXE)
        .setHardness(3.0F)
        .setResistance(5.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("gildedHolystone")
        .setCategory(CreativeTabs.tabNature);
    public static final Block blockAdamantine = new MetalBlock(241, false)
        .setTool(ToolType.PICKAXE)
        .setHardness(5.0F)
        .setResistance(10.0F)
        .setStepSound(StepSoundsList.metalSound)
        .setName("blockAdamantine")
        .setRarity(Rarity.EPIC)
        .setCategory(CreativeTabs.tabBlocks);
    public static final Block quickSoil = new QuickSoil(242, MaterialList.quicksoil)
        .setTool(ToolType.SHOVEL)
        .setHardness(0.5F)
        .setStepSound(StepSoundsList.sandSound)
        .setName("holySand")
        .setCategory(CreativeTabs.tabNature);
    public static final Flower tulip = (Flower)new Flower(243)
        .setHardness(0.0F)
        .setStepSound(StepSoundsList.grassSound)
        .setName("tulip")
        .setCategory(CreativeTabs.tabNature);
    public static final Flower daisy = (Flower)new Flower(244)
        .setHardness(0.0F)
        .setStepSound(StepSoundsList.grassSound)
        .setName("daisy")
        .setCategory(CreativeTabs.tabNature);
    public static final Flower whiteTulip = (Flower)new Flower(245)
        .setHardness(0.0F)
        .setStepSound(StepSoundsList.grassSound)
        .setName("tulip_white")
        .setCategory(CreativeTabs.tabNature);
    public static final Flower pinkTulip = (Flower)new Flower(246)
        .setHardness(0.0F)
        .setStepSound(StepSoundsList.grassSound)
        .setName("tulip_pink")
        .setCategory(CreativeTabs.tabNature);
    public static final Flower paeonia = (Flower)new Flower(247)
        .setHardness(0.0F)
        .setStepSound(StepSoundsList.grassSound)
        .setName("paeonia")
        .setCategory(CreativeTabs.tabNature);
    public static final Block glassTinted = new TintedGlass(248, MaterialList.glass)
        .setTool(ToolType.PICKAXE)
        .setLightOpacity(255)
        .setHardness(0.3F)
        .setStepSound(StepSoundsList.glassSound)
        .setName("tintedGlass")
        .setCategory(CreativeTabs.tabBlocks);
    public static final Block chestObsidian = new ObsidianChest(249)
        .setTool(ToolType.PICKAXE)
        .setHardness(5.5F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("chestObsidian")
        .setRarity(Rarity.EPIC)
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block freezer = new Freezer(250, MaterialList.rock)
        .setTool(ToolType.PICKAXE)
        .setHardness(2.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("freezer")
        .setRarity(Rarity.EPIC)
        .setCategory(CreativeTabs.tabMachines);
    public static final Block altar = new Altar(251, MaterialList.rock)
        .setTool(ToolType.PICKAXE)
        .setHardness(1.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("shrine")
        .setRarity(Rarity.EPIC)
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block glowVine = new HangingVine(252)
        .setTool(ToolType.SHEARS)
        .setHardness(0.3F)
        .setLightValue(0.9F)
        .setStepSound(StepSoundsList.grassSound)
        .setName("glowVine")
        .setCategory(CreativeTabs.tabNature);
    public static final Block doorPurpur = new Door(253, 4427, 34, MaterialList.wood)
        .setTool(ToolType.AXE)
        .setHardness(3.0F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("doorCrimson");
    public static final Block doorWillow = new Door(254, 4428, 36, MaterialList.wood)
        .setTool(ToolType.AXE)
        .setHardness(3.0F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("doorNylium");
    public static final Block signStanding2 = new Sign(255, SignEntity.class, 0)
        .setTool(ToolType.AXE)
        .setHardness(1.0F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("sign");
    public static final Block signWall2 = new Sign(256, SignEntity.class, 1)
        .setTool(ToolType.AXE)
        .setHardness(1.0F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("sign");
    public static final Block signHanging2 = new Sign(257, SignEntity.class, 2)
        .setTool(ToolType.AXE)
        .setHardness(1.0F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("sign");
    public static final Block voidRock = new Block(258, MaterialList.rock)
        .setHardness(1.5F)
        .setTool(ToolType.PICKAXE)
        .setResistance(10.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("voidRock")
        .setCategory(CreativeTabs.tabNature);
    public static final Block cmdBlock = new CommandBlock(259, MaterialList.rock)
        .setTool(ToolType.PICKAXE)
        .setHardness(1.5F)
        .setResistance(10.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("commandBlock")
        .setCategory(CreativeTabs.tabMachines);
    public static final Block bedOrange = new Bed(260, 4254)
        .setTool(ToolType.AXE)
        .setHardness(0.3F)
        .setName("bed_orange")
        .setStepSound(StepSoundsList.woodSound);
    public static final Block bedYellow = new Bed(261, 4430)
        .setTool(ToolType.AXE)
        .setHardness(0.3F)
        .setName("bed_yellow")
        .setStepSound(StepSoundsList.woodSound);
    public static final Block bedChartreuse = new Bed(262, 4431)
        .setTool(ToolType.AXE)
        .setHardness(0.3F)
        .setName("bed_chartreuse")
        .setStepSound(StepSoundsList.woodSound);
    public static final Block bedGreen = new Bed(263, 4432).setTool(ToolType.AXE).setHardness(0.3F).setName("bed_green").setStepSound(StepSoundsList.woodSound);
    public static final Block bedSpringGreen = new Bed(264, 4433)
        .setTool(ToolType.AXE)
        .setHardness(0.3F)
        .setName("bed_springgreen")
        .setStepSound(StepSoundsList.woodSound);
    public static final Block bedCyan = new Bed(265, 4434).setTool(ToolType.AXE).setHardness(0.3F).setName("bed_cyan").setStepSound(StepSoundsList.woodSound);
    public static final Block bedBlue = new Bed(266, 4435).setTool(ToolType.AXE).setHardness(0.3F).setName("bed_blue").setStepSound(StepSoundsList.woodSound);
    public static final Block bedAquamarine = new Bed(267, 4436)
        .setTool(ToolType.AXE)
        .setHardness(0.3F)
        .setName("bed_ultramarine")
        .setStepSound(StepSoundsList.woodSound);
    public static final Block bedPurple = new Bed(268, 4437)
        .setTool(ToolType.AXE)
        .setHardness(0.3F)
        .setName("bed_purple")
        .setStepSound(StepSoundsList.woodSound);
    public static final Block bedMagenta = new Bed(269, 4438)
        .setTool(ToolType.AXE)
        .setHardness(0.3F)
        .setName("bed_magenta")
        .setStepSound(StepSoundsList.woodSound);
    public static final Block bedPink = new Bed(270, 4439).setTool(ToolType.AXE).setHardness(0.3F).setName("bed_pink").setStepSound(StepSoundsList.woodSound);
    public static final Block bedRose = new Bed(271, 4440).setTool(ToolType.AXE).setHardness(0.3F).setName("bed_rose").setStepSound(StepSoundsList.woodSound);
    public static final Block bedBlack = new Bed(272, 4441).setTool(ToolType.AXE).setHardness(0.3F).setName("bed_black").setStepSound(StepSoundsList.woodSound);
    public static final Block bedDarkGray = new Bed(273, 4442)
        .setTool(ToolType.AXE)
        .setHardness(0.3F)
        .setName("bed_drkgray")
        .setStepSound(StepSoundsList.woodSound);
    public static final Block bedLightGray = new Bed(274, 4443)
        .setTool(ToolType.AXE)
        .setHardness(0.3F)
        .setName("bed_lghtgray")
        .setStepSound(StepSoundsList.woodSound);
    public static final Block bedWhite = new Bed(275, 4444).setTool(ToolType.AXE).setHardness(0.3F).setName("bed_white").setStepSound(StepSoundsList.woodSound);
    public static final Block bedBrown = new Bed(276, 4375).setTool(ToolType.AXE).setHardness(0.3F).setName("bed_brown").setStepSound(StepSoundsList.woodSound);
    public static final Block voidGrass = new VoidGrass(277, MaterialList.rock)
        .setTool(ToolType.PICKAXE)
        .setHardness(1.0F)
        .setResistance(2.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("grassPale")
        .setCategory(CreativeTabs.tabNature);
    public static final Block glowGrass = new TallGrass(278)
        .setTool(ToolType.SHEARS)
        .setHardness(0.0F)
        .setLightValue(0.8F)
        .setStepSound(StepSoundsList.grassSound)
        .setName("tallPaleGrass")
        .setCategory(CreativeTabs.tabNature);
    public static final Block doorLime = new Door(279, 4445, 38, MaterialList.wood)
        .setTool(ToolType.AXE)
        .setHardness(3.0F)
        .setStepSound(StepSoundsList.woodSound)
        .setName("doorLime");
    public static final Block endPortal = new Portal(280, MaterialList.portal).setHardness(-1.0F).setName("portal").setResistance(6000000.0F);
    public static final Block endPortalFrame = new Block(281, MaterialList.rock)
        .setTool(ToolType.PICKAXE)
        .setStepSound(StepSoundsList.glassSound)
        .setLightValue(0.0F)
        .setHardness(5.0F)
        .setName("portalFrame")
        .setRarity(Rarity.EPIC)
        .setResistance(6000000.0F)
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block playerGrave = new PlayerGrave(282, MaterialList.circuits)
        .setTool(ToolType.PICKAXE)
        .setHardness(1.0F)
        .setResistance(6000000.0F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("grave");
    public static final Block beacon = new Beacon(283, MaterialList.rock)
        .setTool(ToolType.PICKAXE)
        .setStepSound(StepSoundsList.glassSound)
        .setHardness(5.0F)
        .setLightValue(0.8F)
        .setName("beacon")
        .setRarity(Rarity.EPIC)
        .setResistance(6000000.0F)
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block grindstone = new Grindstone(284, MaterialList.rock)
        .setTool(ToolType.PICKAXE)
        .setStepSound(StepSoundsList.stoneSound)
        .setHardness(1.3F)
        .setName("grindstone")
        .setCategory(CreativeTabs.tabDecoration);
    public static final Block waterLily = new WaterLily(285).setStepSound(StepSoundsList.grassSound).setName("waterLily").setCategory(CreativeTabs.tabNature);
    public static final Block pillar = new Pillar(286, MaterialList.rock)
        .setTool(ToolType.PICKAXE)
        .setHardness(1.3F)
        .setStepSound(StepSoundsList.stoneSound)
        .setName("pillar")
        .setCategory(CreativeTabs.tabBlocks);
    public static final Block seaGrass = new Seaweed(287)
        .setHardness(0.0F)
        .setStepSound(StepSoundsList.grassSound)
        .setName("doubleGrass")
        .setCategory(CreativeTabs.tabNature);
    public static final Block seaFern = new Seaweed(288)
        .setTool(ToolType.SHEARS)
        .setLightValue(0.9F)
        .setHardness(0.0F)
        .setStepSound(StepSoundsList.grassSound)
        .setName("doubleFern")
        .setCategory(CreativeTabs.tabNature);
    public static final Block coloredPane = new ColoredPane(289, MaterialList.glass)
        .setTool(ToolType.PICKAXE)
        .setHardness(0.3F)
        .setStepSound(StepSoundsList.glassSound)
        .setName("glassPane")
        .setCategory(CreativeTabs.tabColor);
    public static final Block susDirt = new SuspiciousDirt(290, MaterialList.ground)
        .setTool(ToolType.SHOVEL)
        .setHardness(0.5F)
        .setStepSound(StepSoundsList.susSound)
        .setName("sus_dirt")
        .setCategory(CreativeTabs.tabNature);
    public static final Block soulSoil = new SoulSoil(291, MaterialList.sand)
        .setTool(ToolType.SHOVEL)
        .setHardness(0.5F)
        .setStepSound(StepSoundsList.sandSound)
        .setName("soulSoil")
        .setCategory(CreativeTabs.tabNature);
    public static final Block stairBrick = new Stairs(292, stoneBrick).setCategory(CreativeTabs.tabBlocks);
    public static final Block stairSandstone = new Stairs(293, sandStone).setCategory(CreativeTabs.tabBlocks);
    public static final Block stairRedBrick = new Stairs(294, brick).setCategory(CreativeTabs.tabColor);
    public static final Block stairMoss = new Stairs(295, cobblestoneMossy).setCategory(CreativeTabs.tabBlocks);
    public static final Block stairObsidianBrick = new Stairs(296, obsidianBrick).setCategory(CreativeTabs.tabBlocks);
    public static final Block panelCobblestone = new Panel(297, cobblestone).setCategory(CreativeTabs.tabBlocks).setName("panelCobble");
    public static final Block panelStoneBrick = new Panel(298, stoneBrick).setCategory(CreativeTabs.tabBlocks).setName("panelBrick");
    public static final Block panelSandstone = new Panel(299, sandStone).setCategory(CreativeTabs.tabBlocks);
    public static final Block panelBrick = new Panel(300, brick).setCategory(CreativeTabs.tabColor);
    public static final Block panelMoss = new Panel(301, cobblestoneMossy).setCategory(CreativeTabs.tabBlocks);
    public static final Block panelObsidian = new Panel(302, obsidianBrick).setCategory(CreativeTabs.tabBlocks);
    public static final Block slabStoneBrick = new Slab(303, false, stoneBrick).setCategory(CreativeTabs.tabBlocks).setName("slabBrick");
    public static final Block doubleSlabStoneBrick = new Slab(304, true, stoneBrick);
    public static final Block slabSandstone = new Slab(305, false, sandStone).setCategory(CreativeTabs.tabBlocks);
    public static final Block doubleSlabSandstone = new Slab(306, true, sandStone);
    public static final Block slabBrick = new Slab(307, false, brick).setCategory(CreativeTabs.tabColor);
    public static final Block doubleBrick = new Slab(308, true, brick);
    public static final Block slabMoss = new Slab(309, false, cobblestoneMossy).setCategory(CreativeTabs.tabBlocks);
    public static final Block doubleSlabMoss = new Slab(310, true, cobblestoneMossy);
    public static final Block slabObsidian = new Slab(311, false, obsidianBrick).setCategory(CreativeTabs.tabBlocks);
    public static final Block doubleSlabObsidian = new Slab(312, true, obsidianBrick);
    public static final Block wallStoneBrick = new Wall(313, stoneBrick).setCategory(CreativeTabs.tabDecoration).setName("wallSBrick");
    public static final Block wallSandstone = new Wall(314, sandStone).setCategory(CreativeTabs.tabDecoration).setName("wallSandstone");
    public static final Block wallBrick = new Wall(315, brick).setCategory(CreativeTabs.tabColor).setName("wallBrick");
    public static final Block wallMoss = new Wall(316, cobblestoneMossy).setCategory(CreativeTabs.tabDecoration);
    public static final Block wallObsidian = new Wall(317, obsidianBrick).setCategory(CreativeTabs.tabDecoration);
    public static final Block nyliumRoots = new Roots(318)
        .setTool(ToolType.SHEARS)
        .setHardness(0.0F)
        .setName("rootsNylium")
        .setStepSound(StepSoundsList.grassSound)
        .setCategory(CreativeTabs.tabNature);
    public static final Block stairCloth = new Stairs(319, cloth).setCategory(CreativeTabs.tabColor);
    public static final Block slabCloth = new Slab(320, false, cloth).setCategory(CreativeTabs.tabColor).setName("slabColor");
    public static final Block doubleSlabCloth = new Slab(321, true, cloth);
    public static final Block panelCloth = new Panel(322, cloth).setCategory(CreativeTabs.tabColor).setName("panelColor");

    static {
        for (int id = 0; id < 4096; id++) {
            if (blocks[id] != null) {
                if (ItemList.items[id] != null && !(ItemList.items[id] instanceof BlockItem)) {
                    throw new Error("Item id " + (id - 4096) + "(shifted index " + id + ") is overlapping with block id " + id);
                }

                ItemList.items[id] = new BlockItem(id - 4096);
                blocks[id].afterInit();
            }
        }

        CommandBlockEntity.CommandsManagerCore.init();
        StatList.initBlockStats();
    }
}
