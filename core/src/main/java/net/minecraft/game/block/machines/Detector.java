package net.minecraft.game.block.machines;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class Detector extends Block {
    public Icon onState;
    public Icon offState;

    public Detector(int blockID, Material material) {
        super(blockID, material);
        this.setTickable(true);
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.DETECTOR;
    }

    @Override
    public int tickRate() {
        return 1;
    }

    @Override
    public void onTick(World world, int x, int y, int z, Random rand) {
        this.changeState(world, x, y, z, 0);
    }

    @Override
    public void onNeighborChange(World world, int x, int y, int z, int id) {
        this.changeState(world, x, y, z, 0);
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("machines/" + this.name);
        this.onState = ir.registerIcon("machines/detectorCoreLit");
        this.offState = ir.registerIcon("machines/detectorCore");
    }

    @Override
    public void onEntityCollision(Entity e, World world1, int i2, int i3, int i4) {
        this.changeState(world1, i2, i3, i4, 4);
    }

    private void changeState(World world, int x, int y, int z, int state) {
        if (!world.isOnline && world.getBlockMetadata(x, y, z) != state) {
            world.setBlockMetadataWithNotify(x, y, z, state);
        }
    }
}
