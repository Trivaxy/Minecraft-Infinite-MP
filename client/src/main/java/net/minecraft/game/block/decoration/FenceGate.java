package net.minecraft.game.block.decoration;

import java.util.List;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class FenceGate extends Block {
    public static final String[] fenceType = new String[]{
        "oakFenceGate",
        "birchFenceGate",
        "spruceFenceGate",
        "palmFenceGate",
        "acaciaFenceGate",
        "jungleFenceGate",
        "mangroveFenceGate",
        "baobabFenceGate",
        "scorchedFenceGate",
        "mapleFenceGate",
        "cherryFenceGate",
        "mahoganyFenceGate",
        "oldenOakFenceGate",
        "hellbarkFenceGate",
        "nylliumFenceGate",
        "purpurFenceGate",
        "willowFenceGate",
        "limeFenceGate"
    };

    public FenceGate(int id) {
        super(id, MaterialList.wood);
        this.hasStates = true;
        this.multipleNames = true;
        this.names = fenceType;
    }

    @Override
    public BoundingBox getCollisionBox(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z) & 7;
        if (isOpen(meta)) {
            return null;
        } else {
            return meta != 2 && meta != 0
                ? new BoundingBox(x + 0.375, y, z, x + 0.625, y + 1.5, z + 1)
                : new BoundingBox(x, y, z + 0.375, x + 1, y + 1.5, z + 0.625);
        }
    }

    @Override
    public void setStateBounds(World world, int x, int y, int z) {
        int rot = getOpenRotation(world.getBlockMetadata(x, y, z));
        if (rot != 2 && rot != 0) {
            this.setBounds(0.375, 0.0, 0.0, 0.625, 1.0, 1.0);
        } else {
            this.setBounds(0.0, 0.0, 0.375, 1.0, 1.0, 0.625);
        }
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.FENCE_GATE;
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

        int meta = MathHelper.floor_double(player.yaw * 4.0F / 360.0F + 0.5) & 3;
        world.setBlockMetadataWithNotify(x, y, z, itemDMG | meta);
    }

    @Override
    public boolean blockActivated(World world, int x, int y, int z, Player player) {
        int metadata = world.getBlockMetadata(x, y, z);
        if (!world.isOnline) {
            if (isOpen(metadata)) {
                world.setBlockMetadataWithNotify(x, y, z, metadata & -5);
            } else {
                int facing = MathHelper.floor_double(player.yaw * 4.0F / 360.0F + 0.5) & 3;
                int rot = getOpenRotation(metadata);
                if (rot == (facing + 2 & 3)) {
                    metadata = metadata & 248 | facing;
                }

                world.setBlockMetadataWithNotify(x, y, z, metadata | 4);
            }
        }

        if (isOpen(metadata)) {
            world.playBlockSound(x + 0.5F, y + 0.5F, z + 0.5F, "random.door_open", 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
        } else {
            world.playBlockSound(x + 0.5F, y + 0.5F, z + 0.5F, "random.door_close", 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
        }

        return true;
    }

    public static boolean isOpen(int id) {
        return (id & 4) != 0;
    }

    public static int getOpenRotation(int id) {
        return id & 3;
    }

    @Override
    public int pickMeta(int id) {
        return id & 248;
    }

    @Override
    public int dropMeta(int id) {
        return id & 248;
    }

    @Override
    public int guaranteedMeta(int id) {
        return id & 248;
    }

    @Override
    public int getMaxMeta() {
        return 17;
    }

    @Override
    public Icon getIcon(int side, int mdata) {
        return BlockList.planks.getIcon(side, mdata >> 3);
    }

    @Override
    public void getSubStacks(int id, CreativeTabs ct, List<ItemStack> ls) {
        for (int i = 0; i <= this.getMaxMeta(); i++) {
            ls.add(new ItemStack(id, 1, i * 8));
        }
    }
}
