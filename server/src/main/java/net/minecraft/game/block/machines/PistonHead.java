package net.minecraft.game.block.machines;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.block.stepsound.StepSoundsList;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.util.math.Direction;

public class PistonHead extends Block {
    private Icon headTexture = null;
    private Icon sideTexture = null;

    public PistonHead(int id) {
        super(id, MaterialList.pulley);
        this.setStepSound(StepSoundsList.metalSound);
        this.setHardness(2.0F);
    }

    public void setHeadTexture(Icon head, Icon side) {
        this.headTexture = head;
        this.sideTexture = side;
    }

    public void clearHeadTexture() {
        this.headTexture = null;
        this.sideTexture = null;
    }

    @Override
    public void dropExact(World world, int xCoord, int yCoord, int zCoord, int meta, float force, int drop, boolean fuck) {
    }

    @Override
    public void onBlockRemoval(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        int dir = Direction.faceToSide[getDirectionMeta(meta)];
        x += Direction.offsetsXForSide[dir];
        y += Direction.offsetsYForSide[dir];
        z += Direction.offsetsZForSide[dir];
        int id = world.getBlockId(x, y, z);
        if (id == BlockList.pulleyBase.id
            || id == BlockList.pulleyStickyBase.id
            || id == BlockList.pulleyBaseActive.id
            || id == BlockList.pulleyStickyBaseActive.id) {
            meta = world.getBlockMetadata(x, y, z);
            if (Piston.isExtended(meta)) {
                BlockList.blocks[id].dropBlock(world, x, y, z, meta);
                world.setBlockWithNotify(x, y, z, 0);
            }
        }
    }

    @Override
    public Icon getIcon(int side, int meta) {
        int dir = getDirectionMeta(meta);
        if (side == dir && this.headTexture != null) {
            return this.headTexture;
        }

        if (this.sideTexture != null) {
            return this.sideTexture;
        }

        if (side == dir) {
            if (this.headTexture != null) {
                return this.headTexture;
            } else {
                return (meta & 8) != 0 ? Piston.registerIcon("blockGold_top") : Piston.registerIcon("blockIron_top");
            }
        } else if (dir < 6 && side == Direction.oppositeSide[dir]) {
            return (meta & 8) != 0 ? Piston.registerIcon("pistonGoldenTop") : Piston.registerIcon("pistonTop");
        } else {
            return (meta & 8) != 0 ? Piston.registerIcon("pistonGoldenSide") : Piston.registerIcon("pistonSide");
        }
    }

    @Override
    public void registerIcons(IconRegister ir) {
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.PISTON_EXTENSION;
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public boolean placeableAt(World world, int x, int y, int z) {
        return false;
    }

    @Override
    public int quantityDropped(Random rand) {
        return 0;
    }

    @Override
    public void getCollisionBox(World world, int x, int y, int z, BoundingBox boundingBox, ArrayList<BoundingBox> surroundingBBs) {
        int meta = world.getBlockMetadata(x, y, z);
        switch (getDirectionMeta(meta)) {
            case 0:
                this.setBounds(0.0, 0.0, 0.0, 1.0, 0.25, 1.0);
                super.getCollisionBox(world, x, y, z, boundingBox, surroundingBBs);
                this.setBounds(0.375, 0.25, 0.375, 0.625, 1.0, 0.625);
                super.getCollisionBox(world, x, y, z, boundingBox, surroundingBBs);
                break;
            case 1:
                this.setBounds(0.0, 0.75, 0.0, 1.0, 1.0, 1.0);
                super.getCollisionBox(world, x, y, z, boundingBox, surroundingBBs);
                this.setBounds(0.375, 0.0, 0.375, 0.625, 0.75, 0.625);
                super.getCollisionBox(world, x, y, z, boundingBox, surroundingBBs);
                break;
            case 2:
                this.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 0.25);
                super.getCollisionBox(world, x, y, z, boundingBox, surroundingBBs);
                this.setBounds(0.25, 0.375, 0.25, 0.75, 0.625, 1.0);
                super.getCollisionBox(world, x, y, z, boundingBox, surroundingBBs);
                break;
            case 3:
                this.setBounds(0.0, 0.0, 0.75, 1.0, 1.0, 1.0);
                super.getCollisionBox(world, x, y, z, boundingBox, surroundingBBs);
                this.setBounds(0.25, 0.375, 0.0, 0.75, 0.625, 0.75);
                super.getCollisionBox(world, x, y, z, boundingBox, surroundingBBs);
                break;
            case 4:
                this.setBounds(0.0, 0.0, 0.0, 0.25, 1.0, 1.0);
                super.getCollisionBox(world, x, y, z, boundingBox, surroundingBBs);
                this.setBounds(0.375, 0.25, 0.25, 0.625, 0.75, 1.0);
                super.getCollisionBox(world, x, y, z, boundingBox, surroundingBBs);
                break;
            case 5:
                this.setBounds(0.75, 0.0, 0.0, 1.0, 1.0, 1.0);
                super.getCollisionBox(world, x, y, z, boundingBox, surroundingBBs);
                this.setBounds(0.0, 0.375, 0.25, 0.75, 0.625, 0.75);
                super.getCollisionBox(world, x, y, z, boundingBox, surroundingBBs);
        }

        this.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
    }

    @Override
    public void setStateBounds(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        switch (getDirectionMeta(meta)) {
            case 0:
                this.setBounds(0.0, 0.0, 0.0, 1.0, 0.25, 1.0);
                break;
            case 1:
                this.setBounds(0.0, 0.75, 0.0, 1.0, 1.0, 1.0);
                break;
            case 2:
                this.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 0.25);
                break;
            case 3:
                this.setBounds(0.0, 0.0, 0.75, 1.0, 1.0, 1.0);
                break;
            case 4:
                this.setBounds(0.0, 0.0, 0.0, 0.25, 1.0, 1.0);
                break;
            case 5:
                this.setBounds(0.75, 0.0, 0.0, 1.0, 1.0, 1.0);
        }
    }

    @Override
    public void onNeighborChange(World world, int x, int y, int z, int i) {
        int direction = getDirectionMeta(world.getBlockMetadata(x, y, z));
        int id = world.getBlockId(x - Direction.offsetsXForSide[direction], y - Direction.offsetsYForSide[direction], z - Direction.offsetsZForSide[direction]);
        if (id != BlockList.pulleyBase.id
            && id != BlockList.pulleyStickyBase.id
            && id != BlockList.pulleyBaseActive.id
            && id != BlockList.pulleyStickyBaseActive.id) {
            world.setBlockWithNotify(x, y, z, 0);
        } else {
            BlockList.blocks[id]
                .onNeighborChange(
                    world, x - Direction.offsetsXForSide[direction], y - Direction.offsetsYForSide[direction], z - Direction.offsetsZForSide[direction], i
                );
        }
    }

    public static int getDirectionMeta(int id) {
        return id & 7;
    }

    @Override
    public int pickID(int meta) {
        return BlockList.pulleyBase.id;
    }
}
