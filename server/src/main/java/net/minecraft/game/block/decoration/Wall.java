package net.minecraft.game.block.decoration;

import java.util.List;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSource;

public class Wall extends Block {
    private Block modelBlock;

    public Wall(int id, Block block) {
        super(id, block.material);
        this.setHardness(block.hardness);
        this.setResistance(block.blastResistance);
        this.setStepSound(block.stepSound);
        this.setTool(block.getTool());
        this.modelBlock = block;
        this.hasStates = block.hasStates;
        this.multipleNames = block.multipleNames;
        this.name = "wall" + block.name;
        if (this.multipleNames) {
            for (int i = 0; i < block.names.length; i++) {
                this.names[i] = "wall" + block.names[i];
            }
        }
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.WALL;
    }

    @Override
    public void setStateBounds(World world, int x, int y, int z) {
        boolean left = this.canConnectWallTo(world, x, y, z - 1);
        boolean right = this.canConnectWallTo(world, x, y, z + 1);
        boolean front = this.canConnectWallTo(world, x - 1, y, z);
        boolean back = this.canConnectWallTo(world, x + 1, y, z);
        boolean above = this.canConnectWallTo(world, x, y + 1, z);
        float minX = 0.25F;
        float maxX = 0.75F;
        float minZ = 0.25F;
        float maxZ = 0.75F;
        float maxY = 1.0F;
        if (left) {
            minZ = 0.0F;
        }

        if (right) {
            maxZ = 1.0F;
        }

        if (front) {
            minX = 0.0F;
        }

        if (back) {
            maxX = 1.0F;
        }

        if (left && right && !front && !back) {
            if (above) {
                maxY = 1.0F;
            } else {
                maxY = 0.8125F;
            }

            minX = 0.3125F;
            maxX = 0.6875F;
        } else if (!left && !right && front && back) {
            if (above) {
                maxY = 1.0F;
            } else {
                maxY = 0.8125F;
            }

            minZ = 0.3125F;
            maxZ = 0.6875F;
        }

        this.setBounds(minX, 0.0, minZ, maxX, maxY, maxZ);
    }

    @Override
    public BoundingBox getCollisionBox(World world, int x, int y, int z) {
        this.setStateBounds(world, x, y, z);
        this.maxY = 1.5;
        return super.getCollisionBox(world, x, y, z);
    }

    public boolean canConnectWallTo(WorldSource world, int x, int y, int z) {
        int id = world.getBlockId(x, y, z);
        if (id != BlockList.fenceGate.id
            && id != BlockList.glassPane.id
            && id != BlockList.ironBars.id
            && id != BlockList.copperGrating.id
            && id != BlockList.coloredPane.id
            && !world.isSolid(x, y, z)) {
            Block block = BlockList.blocks[id];
            return block != null && block.fullBlock() || block instanceof Wall;
        } else {
            return true;
        }
    }

    @Override
    public int dropMeta(int id) {
        return id;
    }

    @Override
    public int getMaxMeta() {
        if (this.modelBlock == null) {
            return 0;
        } else {
            return this.modelBlock == BlockList.stoneBrick ? this.modelBlock.getMaxMeta() - 1 : this.modelBlock.getMaxMeta();
        }
    }

    @Override
    public Icon getIcon(int side, int meta) {
        return this.modelBlock.getIcon(side, meta);
    }

    @Override
    public void getSubStacks(int id, CreativeTabs ct, List<ItemStack> ls) {
        for (int i = 0; i <= this.getMaxMeta(); i++) {
            ls.add(new ItemStack(id, 1, i));
        }
    }
}
