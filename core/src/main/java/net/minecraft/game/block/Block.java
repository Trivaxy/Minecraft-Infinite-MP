package net.minecraft.game.block;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import net.minecraft.game.block.building.Gravel;
import net.minecraft.game.block.building.Leaves;
import net.minecraft.game.block.building.Ore;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.plants.Crops;
import net.minecraft.game.block.plants.TallGrass;
import net.minecraft.game.block.stepsound.StepSound;
import net.minecraft.game.block.stepsound.StepSoundsList;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.Rarity;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.item.recipe.special.FurnaceRecipes;
import net.minecraft.game.item.tools.Shears;
import net.minecraft.game.item.tools.Tool;
import net.minecraft.game.item.tools.ToolMaterial;
import net.minecraft.game.item.tools.ToolType;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.physics.TargetingSystem;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSource;
import net.minecraft.statistics.StatList;

public class Block {
    public static final BitSet canTick = new BitSet(4096);
    public static final BitSet isOpaque = new BitSet(4096);
    public static final BitSet isContainer = new BitSet(4096);
    public static final int[] lightOpacity = new int[4096];
    public static final int[] lightValue = new int[4096];
    public String[] names = new String[256];
    public StepSound stepSound;
    public Material material;
    private ToolType tool = ToolType.NONE;
    public Icon blockIcon;
    public String name;
    private CreativeTabs category;
    public Rarity rarity = Rarity.COMMON;
    public final int id;
    public float hardness;
    public float blastResistance;
    public float particleGravity;
    public float slipperiness = 0.6F;
    public boolean hasStates = false;
    public boolean silkTouch = true;
    protected boolean enableStats = true;
    public boolean multipleNames = false;
    public boolean compostable = false;
    public double minX;
    public double minY;
    public double minZ;
    public double maxX;
    public double maxY;
    public double maxZ;

    protected Block(int blockid, Material material) {
        this.stepSound = StepSoundsList.stoneSound;
        this.particleGravity = 1.0F;
        if (BlockList.blocks[blockid] != null) {
            throw new Error("Slot " + blockid + " is already occupied by " + BlockList.blocks[blockid] + " when adding " + this);
        }

        BlockList.blocks[blockid] = this;
        this.material = material;
        this.id = blockid;
        this.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        isOpaque.set(blockid, this.fullBlock());
        lightOpacity[blockid] = this.fullBlock() ? 255 : 0;
        isContainer.clear(blockid);
    }

    protected Block setStepSound(StepSound soundName) {
        this.stepSound = soundName;
        return this;
    }

    protected Block setLightOpacity(int opacity) {
        lightOpacity[this.id] = opacity;
        return this;
    }

    protected Block setLightValue(float maxLightLevel) {
        lightValue[this.id] = (int)(15.0F * maxLightLevel);
        return this;
    }

    protected Block setResistance(float explosionResistance) {
        this.blastResistance = explosionResistance * 3.0F;
        return this;
    }

    protected Block setRarity(Rarity opacity) {
        this.rarity = opacity;
        return this;
    }

    protected Block setHardness(float blockHardness) {
        this.hardness = blockHardness;
        if (this.blastResistance < blockHardness * 5.0F) {
            this.blastResistance = blockHardness * 5.0F;
        }

        return this;
    }

    public Block setName(String name) {
        this.name = name;
        return this;
    }

    public Block setCategory(CreativeTabs ct) {
        this.category = ct;
        return this;
    }

    protected Block setTool(ToolType tool) {
        this.tool = tool;
        return this;
    }

    protected void setTickable(boolean tick) {
        canTick.set(this.id, tick);
    }

    protected Block disableStats() {
        this.enableStats = false;
        return this;
    }

    public Block.RenderType getRenderType() {
        return Block.RenderType.NORMAL;
    }

    public ToolType getTool() {
        return this.tool;
    }

    public void setBounds(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public void setStateBounds(World w, int x, int y, int z) {
    }

    public int getBrightness(WorldSource world, int xCoord, int yCoord, int zCoord) {
        return world.getBrightness(xCoord, yCoord, zCoord, lightValue[this.id]);
    }

    public boolean renderFace(WorldSource world, int xCoord, int yCoord, int zCoord, int side) {
        if (side == 0 && this.minY > 0.0) {
            return true;
        } else if (side == 1 && this.maxY < 1.0) {
            return true;
        } else if (side == 2 && this.minZ > 0.0) {
            return true;
        } else if (side == 3 && this.maxZ < 1.0) {
            return true;
        } else if (side == 4 && this.minX > 0.0) {
            return true;
        } else {
            return side == 5 && this.maxX < 1.0 ? true : !world.isBlockFull(xCoord, yCoord, zCoord);
        }
    }

    public Icon getTexture(WorldSource world, int xCoord, int yCoord, int zCoord, int side) {
        return this.getIcon(side, world.getBlockMetadata(xCoord, yCoord, zCoord));
    }

    public Icon getIcon(int side, int mdata) {
        return this.blockIcon;
    }

    public Icon getSideIcon(int side) {
        return this.getIcon(side, 0);
    }

    public void afterInit() {
    }

    public BoundingBox getOutline(World world, int xCoord, int yCoord, int zCoord) {
        return BoundingBox.getBoundingBox(
            xCoord + this.minX, yCoord + this.minY, zCoord + this.minZ, xCoord + this.maxX, yCoord + this.maxY, zCoord + this.maxZ
        );
    }

    public void getCollisionBox(World world, int xCoord, int yCoord, int zCoord, BoundingBox aabb, ArrayList<BoundingBox> arrayList) {
        BoundingBox boundingBox = this.getCollisionBox(world, xCoord, yCoord, zCoord);
        if (boundingBox != null && aabb.intersects(boundingBox)) {
            arrayList.add(boundingBox);
        }
    }

    public BoundingBox getCollisionBox(World world, int xCoord, int yCoord, int zCoord) {
        return BoundingBox.getBoundingBox(
            xCoord + this.minX, yCoord + this.minY, zCoord + this.minZ, xCoord + this.maxX, yCoord + this.maxY, zCoord + this.maxZ
        );
    }

    public boolean fullBlock() {
        return true;
    }

    public boolean canCollideWith(int meta, boolean check) {
        return this.isCollidable();
    }

    public boolean isCollidable() {
        return true;
    }

    public void onTick(World world, int xCoord, int yCoord, int zCoord, Random rand) {
    }

    public void spawnParticles(World world, int xCoord, int yCoord, int zCoord, Random rand) {
    }

    public void onBroken(World world, int xCoord, int yCoord, int zCoord, int meta) {
    }

    public void onPlaced(World world, Player player, int x, int y, int z, int side) {
        player.addStat(StatList.useStats[this.id], 1);
        player.addStat(StatList.blocksPlacedStat, 1);
    }

    public void onBlockAdded(World world, int xCoord, int yCoord, int zCoord) {
    }

    public void onBlockRemoval(World world, int xCoord, int yCoord, int zCoord) {
    }

    public void onNeighborChange(World world, int xCoord, int yCoord, int zCoord, int id) {
    }

    public int tickRate() {
        return 10;
    }

    public int quantityDropped(Random random) {
        return 1;
    }

    public int dropID(int blockid, Random random) {
        return this.id;
    }

    public int guaranteedID(int id) {
        return this.id;
    }

    public int dropMeta(int id) {
        return 0;
    }

    protected int guaranteedMeta(int id) {
        return this.hasStates ? id : 0;
    }

    public int getMaxMeta() {
        return 0;
    }

    public float breakStrength(Player entityPlayer, int metadata) {
        return this.hardness < 0.0F
            ? 0.0F
            : (
                !entityPlayer.canHarvestBlock(this)
                    ? entityPlayer.getStrVsBlock(this) / this.hardness / 100.0F
                    : entityPlayer.getStrVsBlock(this) / this.hardness / 30.0F
            );
    }

    public ItemStack getCarverContents(int id, int dmg) {
        return null;
    }

    public void dropItem(World world, Player player, int x, int y, int z, int meta) {
        ItemStack stack;
        if (player.getUsing()) {
            stack = player.inventory.getOffHandItem();
        } else {
            stack = player.getCurrentEquippedItem();
        }

        if (stack != null && stack.getItem() instanceof Tool && stack.canHarvestBlock(this)) {
            if (((Tool)stack.getItem()).toolMaterial == ToolMaterial.RUBY
                && FurnaceRecipes.smelting().getSmeltingResult(this.id, world.getBlockMetadata(x, y, z)) != null) {
                this.instaSmelt(world, x, y, z, FurnaceRecipes.smelting().getSmeltingResult(this.id, world.getBlockMetadata(x, y, z)), 1.0F);
                return;
            }

            if (((Tool)stack.getItem()).toolMaterial == ToolMaterial.TOPAZ && this.silkTouch) {
                this.dropExact(world, x, y, z, meta, 1.0F, this.id, true);
                return;
            }

            if (((Tool)stack.getItem()).toolMaterial == ToolMaterial.ADAMANTINE
                && world.rand.nextInt(10) == 0
                && this.category == CreativeTabs.tabNature
                && this != BlockList.oreAdamantine) {
                this.dropBlockItem(world, x, y, z, meta, 1.0F, true);
                return;
            }
        }

        if (stack != null && stack.getItem() instanceof Shears && this.tool == ToolType.SHEARS && stack.canHarvestBlock(this)) {
            this.dropExact(world, x, y, z, meta, 1.0F, this.id, true);
        } else {
            if (this.id != BlockList.oreAdamantine.id && this.quantityDropped(world.rand) != 4) {
                if (!(this instanceof Ore) && !(this instanceof Crops)) {
                    if ((this instanceof Gravel || this instanceof TallGrass || this instanceof Leaves) && player.getLuckModifier()) {
                        this.dropExact(world, x, y, z, this.dropMeta(meta), 1.0F, this.guaranteedID(meta), true);
                        return;
                    }
                } else if (player.getLuckModifier()) {
                    this.dropBlockItem(world, x, y, z, meta, 1.0F, true);
                    return;
                }
            }

            this.dropBlock(world, x, y, z, meta);
            player.addExhaustion(0.025F);
        }
    }

    public void dropBlock(World world, int xCoord, int yCoord, int zCoord, int meta) {
        this.dropBlockItem(world, xCoord, yCoord, zCoord, meta, 1.0F, false);
    }

    public void dropBlockItem(World world, int xCoord, int yCoord, int zCoord, int meta, float forceOffset, boolean multiply) {
        if (!world.isOnline && world.settings.blockDrops) {
            int quantity = this.quantityDropped(world.rand);
            if (multiply) {
                quantity += world.rand.nextInt(3);
            }

            for (int i = 0; i < quantity; i++) {
                if (world.rand.nextFloat() <= forceOffset) {
                    int id = this.dropID(meta, world.rand);
                    if (id > 0) {
                        float offset = 0.7F;
                        double xOffset = world.rand.nextFloat() * offset + (1.0F - offset) * 0.5;
                        double yOffset = world.rand.nextFloat() * offset + (1.0F - offset) * 0.5;
                        double zOffset = world.rand.nextFloat() * offset + (1.0F - offset) * 0.5;
                        DroppedItem drop = new DroppedItem(
                            world, xCoord + xOffset, yCoord + yOffset, zCoord + zOffset, new ItemStack(id, 1, this.dropMeta(meta))
                        );
                        world.spawnEntity(drop);
                    }
                }
            }
        }
    }

    public void dropExact(World world, int xCoord, int yCoord, int zCoord, int id, float dropOffset, int drop, boolean exact) {
        if (!world.isOnline && world.settings.blockDrops) {
            int meta = 0;
            if (this.hasStates && exact) {
                meta = this.guaranteedMeta(id);
            }

            if (world.rand.nextFloat() <= dropOffset) {
                int dropID = drop;
                int count = 1;
                if (dropID > 0) {
                    float offset = 0.7F;
                    double xOffset = world.rand.nextFloat() * offset + (1.0F - offset) * 0.5;
                    double yOffset = world.rand.nextFloat() * offset + (1.0F - offset) * 0.5;
                    double zOffset = world.rand.nextFloat() * offset + (1.0F - offset) * 0.5;
                    DroppedItem droppedItem = new DroppedItem(world, xCoord + xOffset, yCoord + yOffset, zCoord + zOffset, new ItemStack(dropID, count, meta));
                    world.spawnEntity(droppedItem);
                }
            }
        }
    }

    public void instaSmelt(World world, int xCoord, int yCoord, int zCoord, ItemStack stack, float dropOffset) {
        if (!world.isOnline && world.settings.blockDrops && world.rand.nextFloat() <= dropOffset) {
            float offset = 0.7F;
            double xOffset = world.rand.nextFloat() * offset + (1.0F - offset) * 0.5;
            double yOffset = world.rand.nextFloat() * offset + (1.0F - offset) * 0.5;
            double zOffset = world.rand.nextFloat() * offset + (1.0F - offset) * 0.5;
            DroppedItem droppedItem = new DroppedItem(
                world, xCoord + xOffset, yCoord + yOffset, zCoord + zOffset, new ItemStack(stack.itemID, 1, stack.itemDmg)
            );
            world.spawnEntity(droppedItem);
        }
    }

    public float getExplosionResistance(Entity entity, int metadata) {
        return this.blastResistance / 5.0F;
    }

    public TargetingSystem rayTrace(World world, int x, int y, int z, Vector source, Vector target) {
        this.setStateBounds(world, x, y, z);
        source = source.addVector(-x, -y, -z);
        target = target.addVector(-x, -y, -z);
        Vector minX = source.getIntermediateX(target, this.minX);
        Vector maxX = source.getIntermediateX(target, this.maxX);
        Vector minY = source.getIntermediateY(target, this.minY);
        Vector maxY = source.getIntermediateY(target, this.maxY);
        Vector minZ = source.getIntermediateZ(target, this.minZ);
        Vector maxZ = source.getIntermediateZ(target, this.maxZ);
        if (!this.vecBoundX(minX)) {
            minX = null;
        }

        if (!this.vecBoundX(maxX)) {
            maxX = null;
        }

        if (!this.vecBoundY(minY)) {
            minY = null;
        }

        if (!this.vecBoundY(maxY)) {
            maxY = null;
        }

        if (!this.vecBoundZ(minZ)) {
            minZ = null;
        }

        if (!this.vecBoundZ(maxZ)) {
            maxZ = null;
        }

        Vector ray = null;
        if (minX != null && (ray == null || source.distanceTo(minX) < source.distanceTo(ray))) {
            ray = minX;
        }

        if (maxX != null && (ray == null || source.distanceTo(maxX) < source.distanceTo(ray))) {
            ray = maxX;
        }

        if (minY != null && (ray == null || source.distanceTo(minY) < source.distanceTo(ray))) {
            ray = minY;
        }

        if (maxY != null && (ray == null || source.distanceTo(maxY) < source.distanceTo(ray))) {
            ray = maxY;
        }

        if (minZ != null && (ray == null || source.distanceTo(minZ) < source.distanceTo(ray))) {
            ray = minZ;
        }

        if (maxZ != null && (ray == null || source.distanceTo(maxZ) < source.distanceTo(ray))) {
            ray = maxZ;
        }

        if (ray == null) {
            return null;
        }

        byte side = -1;
        if (ray == minX) {
            side = 4;
        }

        if (ray == maxX) {
            side = 5;
        }

        if (ray == minY) {
            side = 0;
        }

        if (ray == maxY) {
            side = 1;
        }

        if (ray == minZ) {
            side = 2;
        }

        if (ray == maxZ) {
            side = 3;
        }

        return new TargetingSystem(x, y, z, side, ray.addVector(x, y, z));
    }

    private boolean vecBoundX(Vector vec) {
        return vec == null ? false : vec.yCoord >= this.minY && vec.yCoord <= this.maxY && vec.zCoord >= this.minZ && vec.zCoord <= this.maxZ;
    }

    private boolean vecBoundY(Vector vec) {
        return vec == null ? false : vec.xCoord >= this.minX && vec.xCoord <= this.maxX && vec.zCoord >= this.minZ && vec.zCoord <= this.maxZ;
    }

    private boolean vecBoundZ(Vector vec) {
        return vec == null ? false : vec.xCoord >= this.minX && vec.xCoord <= this.maxX && vec.yCoord >= this.minY && vec.yCoord <= this.maxY;
    }

    public void onExplode(World world, int xCoord, int yCoord, int zCoord) {
    }

    public int renderPass() {
        return 0;
    }

    public boolean particles3D() {
        return true;
    }

    public boolean placeableAt(World world, int xCoord, int yCoord, int zCoord) {
        return true;
    }

    public boolean blockActivated(World world, int xCoord, int yCoord, int zCoord, Player entityPlayer) {
        return false;
    }

    public void onEntityWalking(World world, int xCoord, int yCoord, int zCoord, Entity entity) {
    }

    public void onEntityCollision(Entity e, World world, int x, int y, int z) {
    }

    public void blockPunched(World world, int xCoord, int yCoord, int zCoord, Player entityPlayer) {
    }

    public void multiplyEntityVelocity(World world, int xCoord, int yCoord, int zCoord, Entity entity, Vector vec) {
    }

    public void modifyOutline(WorldSource blockAccess, int xCoord, int yCoord, int zCoord) {
    }

    public int getRenderColor(WorldSource blockAccess, int xCoord, int yCoord, int zCoord) {
        return 16777215;
    }

    public void registerIcons(IconRegister ir) {
        if (this.name == null) {
            System.out.println("name null for tile " + this);
        }

        this.blockIcon = ir.registerIcon(this.name);
    }

    public StepSound getStepSound(int meta) {
        return this.stepSound;
    }

    public CreativeTabs creativeCategory() {
        return this.category;
    }

    public void getSubStacks(int id, CreativeTabs ct, List<ItemStack> ls) {
        ls.add(new ItemStack(id, 1, 0));
    }

    public float getAOSmoothing(WorldSource world, int x, int y, int z) {
        return world.isBlockFull(x, y, z) ? 0.2F : 1.0F;
    }

    public boolean canStay(World world, int x, int y, int z) {
        return false;
    }

    public void forceBounds(int id) {
    }

    public boolean fertilize(World world, int x, int y, int z, int meta) {
        return false;
    }

    public int pickID(int meta) {
        return this.id;
    }

    public int pickMeta(int id) {
        return this.hasStates ? id : -1;
    }

    public boolean powersDirectly(World w, int x, int y, int z, int id) {
        return false;
    }

    public boolean powersIndirectly(World w, int x, int y, int z, int id) {
        return false;
    }

    public boolean providesPower() {
        return false;
    }

    public boolean isRotatable(int id) {
        return false;
    }

    public void blockEvent(World world, int x, int y, int z, int id, int meta) {
    }

    public float getHardness() {
        return this.hardness;
    }

    public int solidMaterial() {
        return this.material.getMaterialMobility();
    }

    public boolean placeableAtSide(World w, int x, int y, int z, int side) {
        return true;
    }

    public boolean placeableInWater() {
        return false;
    }

    public boolean tracksStats() {
        return this.enableStats;
    }

    public int getRotation(int x, int y, int z, int side) {
        return -1;
    }

    protected int rotationRand(int x, int y, int z, int s) {
        int positionSeed = 1;
        positionSeed *= positionSeed * 1481765933 + 1481765933;
        positionSeed += x;
        positionSeed *= positionSeed * 1481765933 + 1481765933;
        positionSeed += y;
        positionSeed *= positionSeed * 1481765933 + 1481765933;
        positionSeed += z;
        positionSeed *= positionSeed * 1481765933 + 1481765933;
        positionSeed += s;
        positionSeed *= positionSeed * 1481765933 + 1481765933;
        positionSeed += x;
        positionSeed *= positionSeed * 1481765933 + 1481765933;
        positionSeed += y;
        positionSeed *= positionSeed * 1481765933 + 1481765933;
        positionSeed += z;
        positionSeed *= positionSeed * 1481765933 + 1481765933;
        positionSeed += s;
        return positionSeed >> (positionSeed & 31) & 3;
    }

    public boolean blocksPathfinder(WorldSource w, int x, int y, int z) {
        return this.material.isSolid();
    }

    public enum RenderType {
        NONE,
        NORMAL,
        CROSS,
        TORCH,
        FIRE,
        FLUID,
        GEAR,
        CROPS,
        LADDER,
        RAIL,
        STAIRS,
        FENCE,
        FENCE_GATE,
        WALL,
        BED,
        PANE,
        CHAIR,
        TABLE,
        FLOWER_POT,
        CAKE,
        DETECTOR,
        PISTON_BASE,
        PISTON_EXTENSION,
        BRANCH,
        LOG,
        BARREL,
        CACTUS,
        STEM,
        VINE,
        LILYPAD,
        TIKI_TORCH,
        LANTERN,
        CRYSTAL,
        ANVIL,
        CAULDRON,
        CHEST,
        PEBBLES,
        ROD,
        POT,
        CANDLE,
        COMPOSTER,
        FLAG,
        SAW,
        GRAVE,
        PANEL,
        ALTAR,
        LECTERN,
        BEACON,
        GRINDSTONE,
        SLIME,
        WATERLILY;
    }
}
