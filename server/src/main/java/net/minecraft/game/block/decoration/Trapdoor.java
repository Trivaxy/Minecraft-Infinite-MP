package net.minecraft.game.block.decoration;

import java.util.List;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.block.stepsound.StepSound;
import net.minecraft.game.block.stepsound.StepSoundsList;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.physics.TargetingSystem;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSource;
import net.minecraft.statistics.StatList;
import net.minecraft.util.math.MathHelper;

public class Trapdoor extends Block {
    public static final String[] trapdoors = new String[]{
        "trapdoor",
        "trapdoor_birch",
        "trapdoor_spruce",
        "trapdoor_palm",
        "trapdoor_acacia",
        "trapdoor_jungle",
        "trapdoor_mangrove",
        "trapdoor_baobab",
        "trapdoor_scorched",
        "trapdoor_maple",
        "trapdoor_cherry",
        "trapdoor_mahogany",
        "trapdoor_oldenOak",
        "trapdoor_hellbark",
        "trapdoor_nylium",
        "trapdoor_purpur",
        "trapdoor_willow"
    };
    public static final String[] trapdoorsAlt = new String[]{"trapdoor_glass", "trapdoor_willow", "trapdoor_lime"};
    private Icon[] icons;
    private Icon[] icons2;

    public Trapdoor(int id, Material mat) {
        super(id, mat);
        this.setBounds(0.0, 0.0, 0.0, 1.5, 1.0, 1.0);
        this.multipleNames = true;
        this.hasStates = true;
        this.names = trapdoors;
        if (mat == MaterialList.metal) {
            this.names = new String[]{"trapdoor_steel"};
        } else if (mat == MaterialList.glass) {
            this.names = trapdoorsAlt;
        }
    }

    @Override
    public boolean blocksPathfinder(WorldSource world, int x, int y, int z) {
        return (world.getBlockMetadata(x, y, z) & 4) != 0;
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public boolean particles3D() {
        return false;
    }

    @Override
    public BoundingBox getOutline(World world, int x, int y, int z) {
        this.setStateBounds(world, x, y, z);
        return super.getOutline(world, x, y, z);
    }

    @Override
    public BoundingBox getCollisionBox(World world, int x, int y, int z) {
        this.setStateBounds(world, x, y, z);
        return super.getCollisionBox(world, x, y, z);
    }

    @Override
    public void modifyOutline(WorldSource world, int x, int y, int z) {
        this.forceBounds(world.getBlockMetadata(x, y, z));
    }

    @Override
    public void setStateBounds(World world, int x, int y, int z) {
        this.forceBounds(world.getBlockMetadata(x, y, z));
    }

    @Override
    public void forceBounds(int meta) {
        float offset = 0.1875F;
        this.setBounds(0.0, 0.0, 0.0, 1.0, offset, 1.0);
        if (meta >= 128) {
            this.setBounds(0.0, 0.8125, 0.0, 1.0, 1.0, 1.0);
        }

        if (isOpen(meta)) {
            if ((meta & 3) == 0) {
                this.setBounds(0.0, 0.0, 1.0F - offset, 1.0, 1.0, 1.0);
            }

            if ((meta & 3) == 1) {
                this.setBounds(0.0, 0.0, 0.0, offset, 1.0, 1.0);
            }

            if ((meta & 3) == 3) {
                this.setBounds(1.0F - offset, 0.0, 0.0, 1.0, 1.0, 1.0);
            }

            if ((meta & 3) == 2) {
                this.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, offset);
            }
        }
    }

    @Override
    public boolean blockActivated(World world, int x, int y, int z, Player player) {
        if (this.material != MaterialList.metal) {
            int meta = world.getBlockMetadata(x, y, z);
            world.setBlockMetadataWithNotify(x, y, z, meta ^ 4);
            if (isOpen(meta)) {
                world.playBlockSound(x + 0.5F, y + 0.5F, z + 0.5F, "random.door_open", 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
            } else {
                world.playBlockSound(x + 0.5F, y + 0.5F, z + 0.5F, "random.door_close", 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
            }

            player.addStat(StatList.doorUse, 1);
        }

        return true;
    }

    public void onPowered(World world, int x, int y, int z, boolean pwr) {
        boolean open = (world.getBlockMetadata(x, y, z) & 4) > 0;
        if (open != pwr) {
            int meta = world.getBlockMetadata(x, y, z);
            world.setBlockMetadataWithNotify(x, y, z, meta ^ 4);
            if (isOpen(meta)) {
                world.playBlockSound(x + 0.5F, y + 0.5F, z + 0.5F, "random.door_open", 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
            } else {
                world.playBlockSound(x + 0.5F, y + 0.5F, z + 0.5F, "random.door_close", 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
            }
        }
    }

    @Override
    public void onNeighborChange(World world, int x, int y, int z, int id) {
        if (!world.isOnline && id > 0 && BlockList.blocks[id].providesPower()) {
            boolean powered = world.hasNeighborSignal(x, y, z);
            this.onPowered(world, x, y, z, powered);
        }
    }

    @Override
    public TargetingSystem rayTrace(World world, int x, int y, int z, Vector start, Vector end) {
        this.setStateBounds(world, x, y, z);
        return super.rayTrace(world, x, y, z, start, end);
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("trapdoors/" + this.name);
        this.icons = new Icon[trapdoors.length];
        this.icons2 = new Icon[trapdoorsAlt.length];

        for (int index = 0; index < this.icons.length; index++) {
            this.icons[index] = ir.registerIcon("trapdoors/" + trapdoors[index]);
        }

        for (int index = 0; index < this.icons2.length; index++) {
            this.icons2[index] = ir.registerIcon("trapdoors/" + trapdoorsAlt[index]);
        }
    }

    @Override
    public Icon getTexture(WorldSource world, int x, int y, int z, int side) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta > 128) {
            meta -= 128;
        }

        if (this != BlockList.trapdoorGlass || meta >= 8) {
            return super.getTexture(world, x, y, z, side);
        } else if (meta <= 3 && side != 0 && side != 1) {
            return BlockList.trapdoorWood.blockIcon;
        } else if ((meta == 4 || meta == 6) && side != 2 && side != 3) {
            return BlockList.trapdoorWood.blockIcon;
        } else if ((meta == 7 || meta == 5) && side != 4 && side != 5) {
            return BlockList.trapdoorWood.blockIcon;
        } else if (meta != 5 || side != 4 && side != 5) {
            return meta != 6 || side != 2 && side != 3 ? super.getTexture(world, x, y, z, side) : this.blockIcon;
        } else {
            return this.blockIcon;
        }
    }

    @Override
    public StepSound getStepSound(int meta) {
        return this == BlockList.trapdoorGlass && meta < 8 ? StepSoundsList.glassSound : this.stepSound;
    }

    @Override
    public Icon getIcon(int side, int mdata) {
        if (mdata >= 128) {
            mdata -= 128;
        }

        if (this != BlockList.trapdoorGlass) {
            return this == BlockList.trapdoorWood ? this.icons[mdata >> 3] : this.blockIcon;
        }

        if (mdata < 8) {
            if (mdata <= 3 && side != 0 && side != 1) {
                return BlockList.trapdoorWood.blockIcon;
            } else if ((mdata == 4 || mdata == 5) && side != 2 && side != 3) {
                return BlockList.trapdoorWood.blockIcon;
            } else {
                return (mdata == 6 || mdata == 7) && side != 4 && side != 5 ? BlockList.trapdoorWood.blockIcon : this.blockIcon;
            }
        } else {
            return this.icons2[mdata >> 3];
        }
    }

    @Override
    public void onPlaced(World world, Player player, int x, int y, int z, int side) {
        super.onPlaced(world, player, x, y, z, side);
        int itemDMG = 0;
        if (player.inventory.canUseItem()) {
            itemDMG = player.inventory.getOffHandItem().itemDmg & 248;
        } else {
            itemDMG = player.inventory.getCurrentItem().itemDmg & 248;
        }

        int facing = MathHelper.floor_double(player.yaw * 4.0F / 360.0F + 0.5) & 3;
        if (side == 0) {
            world.setBlockMetadataWithNotify(x, y, z, itemDMG | facing + 128);
        } else {
            world.setBlockMetadataWithNotify(x, y, z, itemDMG | facing);
        }
    }

    public static boolean isOpen(int id) {
        return (id & 4) != 0;
    }

    @Override
    public int pickMeta(int id) {
        if (id >= 128) {
            id -= 128;
        }

        return id & 248;
    }

    @Override
    public int dropMeta(int id) {
        if (id >= 128) {
            id -= 128;
        }

        return id & 248;
    }

    @Override
    public int guaranteedMeta(int id) {
        if (id >= 128) {
            id -= 128;
        }

        return id & 248;
    }

    @Override
    public int getMaxMeta() {
        if (this.material == MaterialList.wood) {
            return 15;
        } else {
            return this.material == MaterialList.glass ? 2 : 0;
        }
    }

    @Override
    public void getSubStacks(int id, CreativeTabs ct, List<ItemStack> ls) {
        for (int i = 0; i <= this.getMaxMeta(); i++) {
            ls.add(new ItemStack(id, 1, i * 8));
        }
    }
}
