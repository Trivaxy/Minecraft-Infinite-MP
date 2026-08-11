package net.minecraft.game.block.decoration;

import java.util.List;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.building.Slab;
import net.minecraft.game.block.building.Stairs;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;
import net.minecraft.util.math.MathHelper;

public class Chair extends Block {
    public static final String[] chairs = new String[]{
        "oakChair",
        "birchChair",
        "spruceChair",
        "palmChair",
        "acaciaChair",
        "jungleChair",
        "mangroveChair",
        "baobabChair",
        "scorchedChair",
        "mapleChair",
        "cherryChair",
        "mahoganyChair",
        "oldenOakChair",
        "hellbarkChair",
        "nylliumChair",
        "purpurChair",
        "willowChair",
        "limeChair"
    };

    public Chair(int blockID, Material material) {
        super(blockID, material);
        this.hasStates = true;
        this.multipleNames = true;
        this.names = chairs;
    }

    @Override
    public boolean placeableAt(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y - 1, z);
        int meta = world.getBlockMetadata(x, y - 1, z);
        return block == BlockList.glass
            ? true
            : (
                block == BlockList.table
                    ? true
                    : (
                        block == BlockList.detector
                            ? true
                            : (
                                block instanceof Stairs && (meta & 4) != 0
                                    ? true
                                    : (
                                        block instanceof Slab && meta >= 128
                                            ? true
                                            : (
                                                world.getBlockMaterial(x, y, z) == MaterialList.water
                                                    ? false
                                                    : (!world.isBlockFull(x, y - 1, z) ? false : super.placeableAt(world, x, y, z))
                                            )
                                    )
                            )
                    )
            );
    }

    @Override
    public void onNeighborChange(World world, int x, int y, int z, int id) {
        if (!this.placeableAt(world, x, y, z)) {
            this.dropBlock(world, x, y, z, world.getBlockMetadata(x, y, z));
            world.playEffect(2001, x, y, z, world.getBlockMetadata(x, y, z) << 12 | this.id);
            world.setBlockWithNotify(x, y, z, 0);
        }
    }

    @Override
    public boolean canStay(World world, int xCoord, int yCoord, int zCoord) {
        return world.getBlockMaterial(xCoord, yCoord - 1, zCoord).isSolid();
    }

    @Override
    public final void onPlaced(World world, Player player, int x, int y, int z, int side) {
        super.onPlaced(world, player, x, y, z, side);
        int facingAngle = MathHelper.floor_double(player.yaw * 4.0F / 360.0F + 0.5) & 3;
        int itemDMG = 0;
        if (player.inventory.canUseItem()) {
            itemDMG = player.inventory.getOffHandItem().itemDmg & 248;
        } else {
            itemDMG = player.inventory.getCurrentItem().itemDmg & 248;
        }

        world.setBlockMetadataWithNotify(x, y, z, itemDMG | facingAngle);
    }

    @Override
    public void onBlockRemoval(World world, int xCoord, int yCoord, int zCoord) {
        Player player = world.getClosestPlayer(xCoord, yCoord, zCoord, 2.0);
        if (player != null) {
            player.sitting = false;
        }
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.CHAIR;
    }

    @Override
    public int getMaxMeta() {
        return BlockList.planks.getMaxMeta();
    }

    @Override
    public int dropMeta(int id) {
        return id & 248;
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public final boolean blockActivated(World world, int x, int y, int z, Player player) {
        if (!world.isOnline
            && !player.isInWater()
            && !player.sneaking
            && world.getBlockId(x, y + 1, z) == 0
            && !player.sleeping
            && !player.crawling
            && !player.flying
            && !player.gliding) {
            player.motionX = player.motionY = player.motionZ = 0.0;
            player.resetKeys();
            player.setPosition(x + 0.5, y + 1.7, z + 0.5);
            player.sitting = !player.sitting;
            player.addStat(StatList.chairUse, 1);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Icon getIcon(int side, int mdata) {
        return BlockList.planks.getIcon(side, mdata >> 3);
    }

    @Override
    public int pickMeta(int id) {
        return id & 248;
    }

    @Override
    public int guaranteedMeta(int id) {
        return id & 248;
    }

    @Override
    public void getSubStacks(int id, CreativeTabs ct, List<ItemStack> ls) {
        for (int i = 0; i <= this.getMaxMeta(); i++) {
            ls.add(new ItemStack(id, 1, i * 8));
        }
    }
}
