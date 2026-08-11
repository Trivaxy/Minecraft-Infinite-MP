package net.minecraft.game.block.building;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class Stairs extends Block {
    private Block modelBlock;

    public Stairs(int blockid, Block block) {
        super(blockid, block.material);
        this.modelBlock = block;
        this.setHardness(block.hardness);
        this.setResistance(block.blastResistance / 3.0F);
        this.setStepSound(block.stepSound);
        this.setTool(block.getTool());
        this.hasStates = block.hasStates;
        this.multipleNames = block.multipleNames;
        this.name = "stairs_" + block.name;
        if (this.multipleNames) {
            for (int i = 0; i < block.names.length; i++) {
                this.names[i] = "stairs_" + block.names[i];
            }
        }
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.STAIRS;
    }

    @Override
    public int getMaxMeta() {
        return this.modelBlock == BlockList.stoneBrick ? this.modelBlock.getMaxMeta() - 1 : this.modelBlock.getMaxMeta();
    }

    @Override
    public void getSubStacks(int id, CreativeTabs ct, List<ItemStack> ls) {
        for (int i = 0; i <= this.getMaxMeta(); i++) {
            ls.add(new ItemStack(id, 1, i << 3));
        }
    }

    @Override
    public void getCollisionBox(World world, int x, int y, int z, BoundingBox aabb, ArrayList<BoundingBox> collidingBoundingBoxes) {
        int metadata = world.getBlockMetadata(x, y, z);
        boolean upsideDown = (metadata & 4) != 0;
        metadata &= 3;
        if (upsideDown) {
            if (metadata == 0) {
                this.setBounds(0.0, 0.5, 0.0, 0.5, 1.0, 1.0);
                super.getCollisionBox(world, x, y, z, aabb, collidingBoundingBoxes);
                this.setBounds(0.5, 0.0, 0.0, 1.0, 1.0, 1.0);
                super.getCollisionBox(world, x, y, z, aabb, collidingBoundingBoxes);
            } else if (metadata == 1) {
                this.setBounds(0.0, 0.0, 0.0, 0.5, 1.0, 1.0);
                super.getCollisionBox(world, x, y, z, aabb, collidingBoundingBoxes);
                this.setBounds(0.5, 0.5, 0.0, 1.0, 1.0, 1.0);
                super.getCollisionBox(world, x, y, z, aabb, collidingBoundingBoxes);
            } else if (metadata == 2) {
                this.setBounds(0.0, 0.5, 0.0, 1.0, 1.0, 0.5);
                super.getCollisionBox(world, x, y, z, aabb, collidingBoundingBoxes);
                this.setBounds(0.0, 0.0, 0.5, 1.0, 1.0, 1.0);
                super.getCollisionBox(world, x, y, z, aabb, collidingBoundingBoxes);
            } else if (metadata == 3) {
                this.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 0.5);
                super.getCollisionBox(world, x, y, z, aabb, collidingBoundingBoxes);
                this.setBounds(0.0, 0.5, 0.5, 1.0, 1.0, 1.0);
                super.getCollisionBox(world, x, y, z, aabb, collidingBoundingBoxes);
            }
        } else if (metadata == 0) {
            this.setBounds(0.0, 0.0, 0.0, 0.5, 0.5, 1.0);
            super.getCollisionBox(world, x, y, z, aabb, collidingBoundingBoxes);
            this.setBounds(0.5, 0.0, 0.0, 1.0, 1.0, 1.0);
            super.getCollisionBox(world, x, y, z, aabb, collidingBoundingBoxes);
        } else if (metadata == 1) {
            this.setBounds(0.0, 0.0, 0.0, 0.5, 1.0, 1.0);
            super.getCollisionBox(world, x, y, z, aabb, collidingBoundingBoxes);
            this.setBounds(0.5, 0.0, 0.0, 1.0, 0.5, 1.0);
            super.getCollisionBox(world, x, y, z, aabb, collidingBoundingBoxes);
        } else if (metadata == 2) {
            this.setBounds(0.0, 0.0, 0.0, 1.0, 0.5, 0.5);
            super.getCollisionBox(world, x, y, z, aabb, collidingBoundingBoxes);
            this.setBounds(0.0, 0.0, 0.5, 1.0, 1.0, 1.0);
            super.getCollisionBox(world, x, y, z, aabb, collidingBoundingBoxes);
        } else if (metadata == 3) {
            this.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 0.5);
            super.getCollisionBox(world, x, y, z, aabb, collidingBoundingBoxes);
            this.setBounds(0.0, 0.0, 0.5, 1.0, 0.5, 1.0);
            super.getCollisionBox(world, x, y, z, aabb, collidingBoundingBoxes);
        }

        this.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
    }

    @Override
    public void setStateBounds(World world, int x, int y, int z) {
        this.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
    }

    @Override
    public float getExplosionResistance(Entity entity, int metadata) {
        return this.modelBlock.getExplosionResistance(entity, metadata);
    }

    @Override
    public int renderPass() {
        return this.modelBlock.renderPass();
    }

    @Override
    public int dropMeta(int id) {
        return id & 248;
    }

    @Override
    public Icon getIcon(int side, int metadataValue) {
        int type = metadataValue >> 3;
        if (type > this.getMaxMeta()) {
            type = 0;
        }

        return this.modelBlock.getIcon(side, type);
    }

    @Override
    public Icon getSideIcon(int textureIndexSlot) {
        return this.modelBlock.getSideIcon(textureIndexSlot);
    }

    @Override
    public void registerIcons(IconRegister ir) {
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
    public void onPlaced(World world, Player player, int x, int y, int z, int side) {
        super.onPlaced(world, player, x, y, z, side);
        int playerRotation = MathHelper.floor_double(player.yaw * 4.0F / 360.0F + 0.5) & 3;
        int rotation = -1;
        ItemStack stack = player.inventory.getCurrentItem();
        if (player.inventory.canUseItem()) {
            stack = player.inventory.getOffHandItem();
        }

        int cleanMeta = stack.getItemDamage() & 248;
        int upsideDown = world.getBlockId(x, y + 1, z) != 0 && side == 0 ? 4 : 0;
        if (playerRotation == 0) {
            rotation = 2;
        }

        if (playerRotation == 1) {
            rotation = 1;
        }

        if (playerRotation == 2) {
            rotation = 3;
        }

        if (playerRotation == 3) {
            rotation = 0;
        }

        world.setBlockMetadataWithNotify(x, y, z, cleanMeta | upsideDown | rotation);
    }
}
