package net.minecraft.game.block.building;

import java.util.List;
import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.block.BlockItem;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSource;
import net.minecraft.util.math.Direction;

public class Slab extends Block {
    public static String[] STONE_SLABS = new String[]{
        "slabs_cobble", "slabs_cMarble", "slabs_cBasalt", "slabs_cLime", "slabs_cGranite", "slabs_cChlorite", "slabs_cIce", "slabs_cDripstone", "slabs_cHoly"
    };
    public static String[] BRICK_SLABS = new String[]{
        "slabs_sbricks",
        "slabs_mbricks",
        "slabs_crackedBricks",
        "slabs_marbleBrick",
        "slabs_bBricks",
        "slabs_lBricks",
        "slabs_gBricks",
        "slabs_cBricks",
        "slabs_bIce",
        "slabs_dBricks",
        "slabs_dHoly"
    };
    public static String[] BRICK_COLORS = new String[]{
        "slabs_brick",
        "slabs_bricks_0",
        "slabs_bricks_1",
        "slabs_bricks_2",
        "slabs_bricks_3",
        "slabs_bricks_4",
        "slabs_bricks_5",
        "slabs_bricks_6",
        "slabs_bricks_7",
        "slabs_bricks_8",
        "slabs_bricks_9",
        "slabs_bricks_10",
        "slabs_bricks_11",
        "slabs_bricks_12",
        "slabs_bricks_13",
        "slabs_bricks_14",
        "slabs_bricks_15"
    };
    public static String[] SANDSTONE_SLABS = new String[]{
        "slabs_sandstone", "slabs_smoothSandstone", "slabs_redSandstone", "slabs_redSmoothSandstone", "slabs_silverSandstone", "slabs_silverSmoothSandstone"
    };
    public static String[] WOOD_SLABS = new String[]{
        "slabs_oak",
        "slabs_birch",
        "slabs_spruce",
        "slabs_palm",
        "slabs_acacia",
        "slabs_jungle",
        "slabs_mangrove",
        "slabs_baobab",
        "slabs_scorched",
        "slabs_maple",
        "slabs_cherry",
        "slabs_mahogany",
        "slabs_oldenOak",
        "slabs_hellbark",
        "slabs_nyllium",
        "slabs_purpur",
        "slabs_willow",
        "slabs_lime"
    };
    private Icon sideIcon;
    private Block block;
    public boolean doubleSlab;

    public Slab(int blockid, boolean isDouble, Block block) {
        super(blockid, block == null ? MaterialList.rock : block.material);
        this.doubleSlab = isDouble;
        this.block = block;
        this.silkTouch = !this.doubleSlab;
        if (!isDouble) {
            this.setBounds(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);
        }

        if (block != null) {
            this.setHardness(block.hardness);
            this.setResistance(block.blastResistance);
            this.setStepSound(block.stepSound);
            this.setTool(block.getTool());
            this.hasStates = block.hasStates;
            this.multipleNames = block.multipleNames;
            this.name = "slabs_" + block.name;
            if (this.multipleNames) {
                for (int i = 0; i < block.names.length; i++) {
                    this.names[i] = "slabs_" + block.names[i];
                }
            }
        } else {
            this.name = "slabs_stone";
        }

        this.silkTouch = false;
        this.setLightOpacity(255);
    }

    @Override
    public Icon getIcon(int side, int data) {
        if (this.block == null) {
            return side != 0 && side != 1 ? this.sideIcon : this.blockIcon;
        } else {
            return this.block.getIcon(side, data & 127);
        }
    }

    @Override
    public boolean fullBlock() {
        return this.doubleSlab;
    }

    @Override
    public void onPlaced(World world, Player player, int x, int y, int z, int side) {
        super.onPlaced(world, player, x, y, z, side);
        if (side == 0) {
            int meta = world.getBlockMetadata(x, y, z);
            world.setBlockMetadataWithNotify(x, y, z, meta + 128);
        }
    }

    @Override
    public BoundingBox getOutline(World world, int x, int y, int z) {
        this.setStateBounds(world, x, y, z);
        return super.getOutline(world, x, y, z);
    }

    @Override
    public BoundingBox getCollisionBox(World world, int x, int y, int z) {
        this.setBounds(world.getBlockMetadata(x, y, z));
        return super.getCollisionBox(world, x, y, z);
    }

    @Override
    public void modifyOutline(WorldSource world, int x, int y, int z) {
        this.setBounds(world.getBlockMetadata(x, y, z));
    }

    @Override
    public void setStateBounds(World world, int x, int y, int z) {
        this.setBounds(world.getBlockMetadata(x, y, z));
    }

    @Override
    public void forceBounds(int id) {
        if (!this.doubleSlab) {
            this.setBounds(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);
        }
    }

    public void setBounds(int meta) {
        if (this.doubleSlab) {
            this.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        } else if (meta >= 128) {
            this.setBounds(0.0, 0.5, 0.0, 1.0, 1.0, 1.0);
        } else {
            this.setBounds(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);
        }
    }

    @Override
    public int quantityDropped(Random random) {
        return this.doubleSlab ? 2 : 1;
    }

    @Override
    public int getMaxMeta() {
        if (this.block == null) {
            return 0;
        } else {
            return this.block == BlockList.stoneBrick ? this.block.getMaxMeta() - 1 : this.block.getMaxMeta();
        }
    }

    @Override
    public int dropMeta(int id) {
        return id & 127;
    }

    @Override
    public int pickMeta(int id) {
        return id & 127;
    }

    @Override
    public int guaranteedMeta(int id) {
        return id & 127;
    }

    @Override
    public int dropID(int blockid, Random random) {
        return this.doubleSlab ? BlockItem.getSlab(this.id).id : this.id;
    }

    @Override
    public int pickID(int blockid) {
        return this.doubleSlab ? BlockItem.getSlab(this.id).id : this.id;
    }

    @Override
    public void registerIcons(IconRegister r) {
        this.blockIcon = r.registerIcon("stoneslab_top");
        this.sideIcon = r.registerIcon("stoneslab_side");
    }

    @Override
    public boolean renderFace(WorldSource world, int x, int y, int z, int side) {
        if (this.doubleSlab) {
            return super.renderFace(world, x, y, z, side);
        }

        int neighborX = x + Direction.offsetsXForSide[Direction.faceToSide[side]];
        int neighborY = y + Direction.offsetsYForSide[Direction.faceToSide[side]];
        int neighborZ = z + Direction.offsetsZForSide[Direction.faceToSide[side]];
        int neighborMeta = world.getBlockMetadata(neighborX, neighborY, neighborZ);
        boolean neighborIsTopSlab = (neighborMeta & 128) != 0;
        int currentMeta = world.getBlockMetadata(x, y, z);
        boolean currentIsTopSlab = (currentMeta & 128) != 0;
        boolean isSingleSlab = isBlockSingleSlab(world.getBlockId(x, y, z));
        if (neighborIsTopSlab) {
            if (side == 0) {
                return true;
            } else {
                return side == 1 ? super.renderFace(world, x, y, z, side) : !isSingleSlab || !currentIsTopSlab;
            }
        } else if (side == 1) {
            return true;
        } else {
            return side == 0 ? super.renderFace(world, x, y, z, side) : !isSingleSlab || currentIsTopSlab;
        }
    }

    private static boolean isBlockSingleSlab(int id) {
        Block block = BlockList.blocks[id];
        return block instanceof Slab ? !((Slab)block).doubleSlab : false;
    }

    @Override
    public void getSubStacks(int id, CreativeTabs ct, List<ItemStack> ls) {
        for (int i = 0; i <= this.getMaxMeta(); i++) {
            ls.add(new ItemStack(id, 1, i));
        }
    }
}
