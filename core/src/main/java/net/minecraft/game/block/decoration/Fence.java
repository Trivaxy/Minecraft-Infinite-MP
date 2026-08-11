package net.minecraft.game.block.decoration;

import java.util.List;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSource;

public class Fence extends Block {
    public static final String[] fenceType = new String[]{
        "oakFence",
        "birchFence",
        "spruceFence",
        "palmFence",
        "acaciaFence",
        "jungleFence",
        "mangroveFence",
        "baobabFence",
        "scorchedFence",
        "mapleFence",
        "cherryFence",
        "mahoganyFence",
        "oldenOakFence",
        "hellbarkFence",
        "nylliumFence",
        "purpurFence",
        "willowFence",
        "limeFence"
    };
    public String[] namesAlt = new String[]{
        "oakFenceStripped",
        "birchFenceStripped",
        "spruceFenceStripped",
        "palmFenceStripped",
        "acaciaFenceStripped",
        "jungleFenceStripped",
        "mangroveFenceStripped",
        "baobabFenceStripped",
        "scorchedFenceStripped",
        "mapleFenceStripped",
        "cherryFenceStripped",
        "mahoganyFenceStripped",
        "oldenOakFenceStripped",
        "hellbarkFenceStripped",
        "nylliumFenceStripped",
        "purpurFenceStripped",
        "willowFenceStripped",
        "limeFenceStripped"
    };

    public Fence(int id) {
        super(id, MaterialList.wood);
        this.hasStates = true;
        this.multipleNames = true;
        this.names = fenceType;
    }

    @Override
    public BoundingBox getCollisionBox(World world, int x, int y, int z) {
        boolean left = this.connectsTo(world, x, y, z - 1);
        boolean right = this.connectsTo(world, x, y, z + 1);
        boolean front = this.connectsTo(world, x - 1, y, z);
        boolean back = this.connectsTo(world, x + 1, y, z);
        float minX = 0.375F;
        float maxX = 0.625F;
        float minZ = 0.375F;
        float maxZ = 0.625F;
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

        return new BoundingBox((double)x + minX, y, (double)z + minZ, (double)x + maxX, y + 1.5, (double)z + maxZ);
    }

    @Override
    public void setStateBounds(World world, int x, int y, int z) {
        boolean left = this.connectsTo(world, x, y, z - 1);
        boolean right = this.connectsTo(world, x, y, z + 1);
        boolean front = this.connectsTo(world, x - 1, y, z);
        boolean back = this.connectsTo(world, x + 1, y, z);
        double minX = 0.375;
        double maxX = 0.625;
        double minZ = 0.375;
        double maxZ = 0.625;
        if (left) {
            minZ = 0.0;
        }

        if (right) {
            maxZ = 1.0;
        }

        if (front) {
            minX = 0.0;
        }

        if (back) {
            maxX = 1.0;
        }

        this.setBounds(minX, 0.0, minZ, maxX, 1.0, maxZ);
    }

    public boolean connectsTo(WorldSource world, int x, int y, int z) {
        int adjacentID = world.getBlockId(x, y, z);
        if (adjacentID != this.id && adjacentID != BlockList.fenceGate.id) {
            Block block = BlockList.blocks[adjacentID];
            return block != null && block.fullBlock();
        } else {
            return true;
        }
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.FENCE;
    }

    @Override
    public int dropMeta(int id) {
        return id;
    }

    @Override
    public int getMaxMeta() {
        return BlockList.planks.getMaxMeta();
    }

    @Override
    public Icon getIcon(int side, int mdata) {
        if (mdata >= 128) {
            mdata -= 128;
        }

        return BlockList.planks.getIcon(side, mdata);
    }

    @Override
    public void getSubStacks(int id, CreativeTabs ct, List<ItemStack> ls) {
        for (int i = 0; i <= this.getMaxMeta(); i++) {
            ls.add(new ItemStack(id, 1, i));
        }
    }
}
