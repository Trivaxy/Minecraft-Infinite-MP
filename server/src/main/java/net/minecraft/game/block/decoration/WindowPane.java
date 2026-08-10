package net.minecraft.game.block.decoration;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.building.Slab;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class WindowPane extends Block {
    public static Icon altIcon;

    public WindowPane(int id, Material mat) {
        super(id, mat);
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.PANE;
    }

    @Override
    public void getCollisionBox(World world, int x, int y, int z, BoundingBox box, ArrayList<BoundingBox> list) {
        float minX = 0.4375F;
        float maxX = 0.5625F;
        float minZ = 0.4375F;
        float maxZ = 0.5625F;
        boolean connectNorth = this.canConnectTo(world.getBlockId(x, y, z - 1));
        boolean connectSouth = this.canConnectTo(world.getBlockId(x, y, z + 1));
        boolean connectWest = this.canConnectTo(world.getBlockId(x - 1, y, z));
        boolean connectEast = this.canConnectTo(world.getBlockId(x + 1, y, z));
        if (connectWest && connectEast) {
            minX = 0.0F;
            maxX = 1.0F;
        } else if (connectWest) {
            minX = 0.0F;
        } else if (connectEast) {
            maxX = 1.0F;
        }

        if (connectNorth && connectSouth) {
            minZ = 0.0F;
            maxZ = 1.0F;
        } else if (connectNorth) {
            minZ = 0.0F;
        } else if (connectSouth) {
            maxZ = 1.0F;
        }

        this.setBounds(minX, 0.0, minZ, maxX, 1.0, maxZ);
        super.getCollisionBox(world, x, y, z, box, list);
    }

    @Override
    public void setStateBounds(World world, int x, int y, int z) {
        float minX = 0.4375F;
        float minZ = 0.5625F;
        float maxX = 0.4375F;
        float maxZ = 0.5625F;
        boolean leftConnection = this.canConnectTo(world.getBlockId(x, y, z - 1));
        boolean rightConnection = this.canConnectTo(world.getBlockId(x, y, z + 1));
        boolean frontConnection = this.canConnectTo(world.getBlockId(x - 1, y, z));
        boolean backConnection = this.canConnectTo(world.getBlockId(x + 1, y, z));
        if (frontConnection && backConnection) {
            minX = 0.0F;
            minZ = 1.0F;
        } else if (frontConnection) {
            minX = 0.0F;
        } else if (backConnection) {
            minZ = 1.0F;
        }

        if (leftConnection && !rightConnection) {
            maxX = 0.0F;
        } else if (!leftConnection && rightConnection) {
            maxZ = 1.0F;
        } else if (leftConnection && rightConnection) {
            maxX = 0.0F;
            maxZ = 1.0F;
        }

        this.setBounds(minX, 0.0, maxX, minZ, 1.0, maxZ);
    }

    @Override
    public final int dropID(int id, Random random) {
        return this.id == BlockList.glassPane.id ? ItemList.stick.id : this.id;
    }

    @Override
    public int quantityDropped(Random random) {
        return this.id == BlockList.glassPane.id ? 2 : 1;
    }

    @Override
    public Icon getIcon(int side, int mdata) {
        return this == BlockList.coloredPane ? BlockList.glassStained.getIcon(side, mdata) : this.blockIcon;
    }

    public final boolean canConnectTo(int id) {
        return Block.isOpaque.get(id)
            || id == this.id
            || id == BlockList.glass.id
            || id == BlockList.leaves.id
            || BlockList.blocks[id] instanceof Slab
            || BlockList.blocks[id] instanceof Wall;
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon(this.name);
        altIcon = ir.registerIcon("steelBars");
    }

    @Override
    public boolean particles3D() {
        return false;
    }
}
