package net.minecraft.game.block.utility;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.block.tileentity.other.CauldronEntity;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSource;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.statistics.StatList;
import net.minecraft.statistics.achievement.AchievementList;

public class Cauldron extends ContainerBlock {
    public Icon top;
    public Icon bottom;
    public Icon legs;
    public Icon juice;

    public Cauldron(int id) {
        super(id, MaterialList.metal);
        this.setTickable(true);
        this.enableStats = false;
        this.silkTouch = false;
    }

    @Override
    public void onTick(World world, int xCoord, int yCoord, int zCoord, Random rand) {
        super.onTick(world, xCoord, yCoord, zCoord, rand);
        TileEntity tileEntity = world.getTileEntity(xCoord, yCoord, zCoord);
        if (tileEntity != null
            && tileEntity instanceof CauldronEntity
            && world.isRaining()
            && world.isBlockExposed(xCoord, yCoord, zCoord)
            && world.getBiomeAt(xCoord, zCoord).getTemperatureEnum() != Biome.Temperature.HOT) {
            ((CauldronEntity)tileEntity).increaseLiquidLevel();
            world.markForUpdate(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public Icon getIcon(int side, int meta) {
        return side == 1 ? this.top : (side == 0 ? this.bottom : this.blockIcon);
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.top = ir.registerIcon("workstations/cauldronTop");
        this.bottom = ir.registerIcon("workstations/cauldronBottom");
        this.blockIcon = ir.registerIcon("workstations/cauldronSide");
        this.legs = ir.registerIcon("workstations/cauldronLegs");
        this.juice = ir.registerIcon("liquid/potionjuice");
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.CAULDRON;
    }

    @Override
    public TileEntity getBlockEntity() {
        return new CauldronEntity();
    }

    @Override
    public boolean blockActivated(World world, int x, int y, int z, Player player) {
        if (world.isOnline) {
            world.markForUpdate(x, y, z);
            return true;
        }

        ItemStack currentStack = player.inventory.getCurrentItem();
        ItemStack offhand = player.inventory.getOffHandItem();
        if (offhand != null && (offhand.getItem() == ItemList.glassBottle || offhand.getItem() == ItemList.potion || offhand.getItem().getPotionPrefix() >= 0)) {
            currentStack = offhand;
        }

        if (currentStack == null) {
            return false;
        }

        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity != null && tileEntity instanceof CauldronEntity) {
            CauldronEntity cauldron = (CauldronEntity)tileEntity;
            if (currentStack.itemID == ItemList.bucketWater.id) {
                if (cauldron.setPotion()) {
                    if (player.properties.gamemode != 1 && currentStack.itemID == ItemList.bucketWater.id) {
                        player.inventory.setSlotContents(player.inventory.currentSlot, new ItemStack(ItemList.bucketEmpty));
                        player.addStat(StatList.cauldronUse, 1);
                    }

                    world.markForUpdate(x, y, z);
                    world.playBlockSound(x, y, z, "random.emptyBucket", 1.0F, 1.0F / (world.rand.nextFloat() * 0.4F + 1.2F));
                }

                return true;
            } else if (currentStack.itemID == ItemList.glassBottle.id) {
                if (!cauldron.hasLiquid()) {
                    ItemStack potion = new ItemStack(ItemList.potion, 1, cauldron.getPotion());
                    if (!player.inventory.addItemToInventory(potion)) {
                        world.spawnEntity(new DroppedItem(world, x + 0.5, y + 1.5, z + 0.5, potion));
                    }

                    currentStack.stackSize--;
                    player.addStat(StatList.potionsBrewed, 1);
                    player.triggerAchievement(AchievementList.brewPotion);
                    cauldron.decreaseLiquidLevel();
                    world.markForUpdate(x, y, z);
                    world.playEntitySound(player, "random.bottle", 1.0F, world.rand.nextFloat() * 0.4F + 0.8F);
                }

                return true;
            } else {
                if (currentStack.getItem().isDyable()) {
                    currentStack.getItem().removeColor(currentStack);
                    cauldron.decreaseLiquidLevel();
                    world.markForUpdate(x, y, z);
                    return true;
                }

                if (cauldron.activateCauldron(currentStack)) {
                    currentStack.stackSize--;
                    System.out.println(currentStack.stackSize);
                    if (currentStack.itemID == ItemList.potion.id) {
                        player.inventory.addItemToInventory(new ItemStack(ItemList.glassBottle));
                    }

                    world.markForUpdate(x, y, z);
                    world.playEntitySound(player, "random.bottle", 1.0F, world.rand.nextFloat() * 0.4F + 0.8F);
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    public static Icon getIcon(String texture) {
        if (texture.equals("cauldron_inner")) {
            return ((Cauldron)BlockList.cauldron).bottom;
        } else {
            return texture.equals("potionjuice") ? ((Cauldron)BlockList.cauldron).juice : null;
        }
    }

    @Override
    public boolean renderFace(WorldSource world, int xCoord, int yCoord, int zCoord, int side) {
        return side == 0 ? false : super.renderFace(world, xCoord, yCoord, zCoord, side);
    }

    @Override
    public int dropID(int meta, Random rand) {
        return ItemList.cauldron.id;
    }

    @Override
    public int pickID(int blockid) {
        return ItemList.cauldron.id;
    }
}
