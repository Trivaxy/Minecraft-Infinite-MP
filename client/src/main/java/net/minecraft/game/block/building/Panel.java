package net.minecraft.game.block.building;

import java.util.List;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSource;
import net.minecraft.util.math.MathHelper;

public class Panel extends Block {
    private Block block;

    public Panel(int blockid, Block block) {
        super(blockid, block == null ? MaterialList.rock : block.material);
        this.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 0.5);
        this.block = block;
        if (this.block != null) {
            this.setHardness(block.hardness);
            this.setResistance(block.blastResistance / 3.0F);
            this.setStepSound(block.stepSound);
            this.setTool(block.getTool());
            this.hasStates = block.hasStates;
            this.multipleNames = block.multipleNames;
            this.name = "panels_" + block.name;
            if (this.multipleNames) {
                for (int i = 0; i < block.names.length; i++) {
                    this.names[i] = "panels_" + block.names[i];
                }
            }
        } else {
            this.name = "panels_stone";
        }

        this.setLightOpacity(0);
    }

    @Override
    public Icon getIcon(int side, int data) {
        return this.block == null ? this.blockIcon : this.block.getIcon(side, data / 4);
    }

    @Override
    public boolean fullBlock() {
        return false;
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
        int meta = world.getBlockMetadata(x, y, z) & 3;
        if (meta == 3
            && world.getBlock(x + 1, y, z) instanceof Panel
            && ((world.getBlockMetadata(x + 1, y, z) & 3) == 1 || (world.getBlockMetadata(x + 1, y, z) & 3) == 0)) {
            this.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        }

        if (meta == 2
            && world.getBlock(x - 1, y, z) instanceof Panel
            && ((world.getBlockMetadata(x - 1, y, z) & 3) == 0 || (world.getBlockMetadata(x - 1, y, z) & 3) == 1)) {
            this.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        }

        if (meta == 0
            && world.getBlock(x, y, z - 1) instanceof Panel
            && ((world.getBlockMetadata(x, y, z - 1) & 3) == 3 || (world.getBlockMetadata(x, y, z - 1) & 3) == 2)) {
            this.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        }

        if (meta == 1
            && world.getBlock(x, y, z + 1) instanceof Panel
            && ((world.getBlockMetadata(x, y, z + 1) & 3) == 3 || (world.getBlockMetadata(x, y, z + 1) & 3) == 2)) {
            this.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        }
    }

    @Override
    public void forceBounds(int id) {
        this.setBounds(0.0, 0.0, 0.3F, 1.0, 1.0, 0.8F);
    }

    @Override
    public void onPlaced(World world, Player player, int x, int y, int z, int side) {
        super.onPlaced(world, player, x, y, z, side);
        int meta = MathHelper.floor_double(player.yaw * 4.0F / 360.0F + 0.5) & 3;
        ItemStack heldStack = player.getCurrentEquippedItem();
        if (player.inventory.getOffHandItem() != null && player.inventory.getOffHandItem().getItem().id == this.id) {
            heldStack = player.inventory.getOffHandItem();
        }

        if (heldStack != null) {
            if (meta == 0) {
                world.setBlockMetadataWithNotify(x, y, z, heldStack.itemDmg);
            }

            if (meta == 1) {
                world.setBlockMetadataWithNotify(x, y, z, heldStack.itemDmg + 3);
            }

            if (meta == 2) {
                world.setBlockMetadataWithNotify(x, y, z, heldStack.itemDmg + 1);
            }

            if (meta == 3) {
                world.setBlockMetadataWithNotify(x, y, z, heldStack.itemDmg + 2);
            }
        }
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
        return id & 252;
    }

    @Override
    public int pickMeta(int id) {
        return id & 252;
    }

    @Override
    public int guaranteedMeta(int id) {
        return id & 252;
    }

    public void setBounds(int meta) {
        switch (meta & 3) {
            case 0:
                this.setBounds(0.0, 0.0, 0.5, 1.0, 1.0, 1.0);
                break;
            case 1:
                this.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 0.5);
                break;
            case 2:
                this.setBounds(0.5, 0.0, 0.0, 1.0, 1.0, 1.0);
                break;
            case 3:
                this.setBounds(0.0, 0.0, 0.0, 0.5, 1.0, 1.0);
        }
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.PANEL;
    }

    @Override
    public void registerIcons(IconRegister r) {
        this.blockIcon = r.registerIcon("stoneslab_top");
    }

    @Override
    public void getSubStacks(int id, CreativeTabs ct, List<ItemStack> ls) {
        for (int i = 0; i <= this.getMaxMeta(); i++) {
            ls.add(new ItemStack(id, 1, i * 4));
        }
    }
}
