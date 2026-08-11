package net.minecraft.game.block.utility;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.block.tileentity.other.SignEntity;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class Sign extends ContainerBlock {
    private final Class<SignEntity> signEntity;
    private final int type;
    private final int[] textColors = new int[]{
        11141120, 16755200, 16777045, 5635925, 2752298, 4063131, 5636095, 5615103, 5592575, 8913035, 11141290, 16733695, 15940202, 4209214, 9342610, 16777215
    };

    public Sign(int blockid, Class<SignEntity> class2, int type) {
        super(blockid, MaterialList.wood);
        this.signEntity = class2;
        float offset = 0.25F;
        this.setBounds(0.5F - offset, 0.0, 0.5F - offset, 0.5F + offset, 1.0, 0.5F + offset);
        this.type = type;
        this.hasStates = true;
        this.silkTouch = false;
        this.enableStats = false;
    }

    @Override
    public BoundingBox getCollisionBox(World world, int x, int y, int z) {
        return null;
    }

    @Override
    public BoundingBox getOutline(World world, int x, int y, int z) {
        this.setStateBounds(world, x, y, z);
        return super.getOutline(world, x, y, z);
    }

    @Override
    public void setStateBounds(World world, int x, int y, int z) {
        if (this.type == 1) {
            int meta = world.getBlockMetadata(x, y, z) & 15;
            float yStart = 0.28125F;
            float yEnd = 0.78125F;
            float offset = 0.125F;
            this.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
            if (meta == 2) {
                this.setBounds(0.0, yStart, 1.0F - offset, 1.0, yEnd, 1.0);
            }

            if (meta == 3) {
                this.setBounds(0.0, yStart, 0.0, 1.0, yEnd, offset);
            }

            if (meta == 4) {
                this.setBounds(1.0F - offset, yStart, 0.0, 1.0, yEnd, 1.0);
            }

            if (meta == 5) {
                this.setBounds(0.0, yStart, 0.0, offset, yEnd, 1.0);
            }
        }
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.NONE;
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public TileEntity getBlockEntity() {
        try {
            return this.signEntity.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean placeableAt(World world, int x, int y, int z) {
        return world.getBlockId(x, y, z) == 0
            && y < world.getWorldHeight()
            && (
                world.getBlockId(x, y - 1, z) == BlockList.fence.id
                    || world.getBlockId(x, y + 1, z) == BlockList.fence.id
                    || world.isSolid(x, y - 1, z)
                    || world.isSolid(x, y + 1, z)
                    || world.isSolid(x - 1, y, z)
                    || world.isSolid(x + 1, y, z)
                    || world.isSolid(x, y, z - 1)
                    || world.isSolid(x, y, z + 1)
            );
    }

    @Override
    public int dropID(int blockid, Random random) {
        return ItemList.sign.id;
    }

    @Override
    public int pickID(int blockid) {
        return ItemList.sign.id;
    }

    @Override
    public void onNeighborChange(World world, int x, int y, int z, int id) {
        boolean popOff = false;
        if (this.type == 0) {
            if (!world.getBlockMaterial(x, y - 1, z).isSolid()) {
                popOff = true;
            }
        } else if (this.type == 2) {
            if (!world.getBlockMaterial(x, y + 1, z).isSolid()) {
                popOff = true;
            }
        } else {
            int meta = world.getBlockMetadata(x, y, z) & 15;
            popOff = true;
            if (meta == 2 && world.getBlockMaterial(x, y, z + 1).isSolid()) {
                popOff = false;
            }

            if (meta == 3 && world.getBlockMaterial(x, y, z - 1).isSolid()) {
                popOff = false;
            }

            if (meta == 4 && world.getBlockMaterial(x + 1, y, z).isSolid()) {
                popOff = false;
            }

            if (meta == 5 && world.getBlockMaterial(x - 1, y, z).isSolid()) {
                popOff = false;
            }
        }

        if (popOff) {
            this.dropBlock(world, x, y, z, world.getBlockMetadata(x, y, z));
            world.setBlockWithNotify(x, y, z, 0);
        }
    }

    @Override
    public final boolean blockActivated(World world, int x, int y, int z, Player player) {
        boolean check = false;
        if (player.inventory.getCurrentItem() == null) {
            player.displayGUIEditSign((SignEntity)world.getTileEntity(x, y, z));
            return true;
        }

        ItemStack heldItem = player.inventory.getCurrentItem();
        ItemStack offhand = player.inventory.getOffHandItem();
        if (offhand != null && (offhand.getItem() == ItemList.dyePowder || offhand.getItem() == ItemList.coal || offhand.getItem() == ItemList.glodust)) {
            heldItem = player.inventory.getOffHandItem();
        }

        SignEntity sign = (SignEntity)world.getTileEntity(x, y, z);
        if (heldItem.getItem() == ItemList.dyePowder) {
            sign.textColor = this.textColors[heldItem.getItemDamage()];
            check = true;
        } else if (heldItem.getItem() == ItemList.coal) {
            sign.textColor = 0;
            sign.glowing = false;
            check = true;
        } else {
            if (heldItem.getItem() != ItemList.glodust || sign.glowing) {
                player.displayGUIEditSign((SignEntity)world.getTileEntity(x, y, z));
                return true;
            }

            sign.glowing = true;
            if (sign.textColor == 0) {
                sign.textColor = this.textColors[15];
            }

            check = true;
        }

        if (check && player.properties.gamemode != 1) {
            heldItem.stackSize--;
        }

        return check;
    }

    @Override
    public Icon getIcon(int side, int mdata) {
        return this != BlockList.signStanding2 && this != BlockList.signHanging2 && this != BlockList.signWall2
            ? BlockList.planks.getIcon(side, mdata >> 4)
            : BlockList.planks.getIcon(side, mdata + 256 >> 4);
    }

    @Override
    public void registerIcons(IconRegister r) {
    }

    @Override
    public int dropMeta(int id) {
        return this != BlockList.signStanding2 && this != BlockList.signWall2 && this != BlockList.signHanging2 ? id >> 4 : (id >> 4) + 16;
    }

    @Override
    public int pickMeta(int id) {
        return this.dropMeta(id);
    }

    @Override
    protected int guaranteedMeta(int id) {
        return this.dropMeta(id);
    }
}
