package net.minecraft.game.block.decoration;

import java.util.Random;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.building.Slab;
import net.minecraft.game.block.building.Stairs;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class TikiTorch extends Block {
    private Icon lowerIcon;

    public TikiTorch(int blockid) {
        super(blockid, MaterialList.circuits);
        float offset = 0.06F;
        this.setBounds(0.5F - offset, 0.0, 0.5F - offset, 0.5F + offset, 1.0, 0.5F + offset);
    }

    @Override
    public Icon getIcon(int side, int meta) {
        return meta == 0 ? this.lowerIcon : this.blockIcon;
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("tikiTorch_upper");
        this.lowerIcon = ir.registerIcon("tikiTorch_lower");
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.TIKI_TORCH;
    }

    @Override
    public boolean placeableAt(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y - 1, z);
        int meta = world.getBlockMetadata(x, y - 1, z);
        return block == this || block == BlockList.table || block == BlockList.glass || block == BlockList.detector
            ? true
            : (
                block instanceof Stairs && (meta & 4) != 0
                    ? true
                    : (
                        block instanceof Slab && meta >= 128
                            ? true
                            : (
                                world.getBlockMaterial(x, y, z) == MaterialList.water
                                    ? false
                                    : (!world.isBlockFull(x, y - 1, z) ? false : super.placeableAt(world, x, y, z))
                            )
                    )
            );
    }

    @Override
    public void onPlaced(World world, Player player, int xCoord, int yCoord, int zCoord, int nya4) {
        world.setBlockAndMetadataWithNotify(xCoord, yCoord + 1, zCoord, this.id, 1);
    }

    @Override
    public void onNeighborChange(World world, int xCoord, int yCoord, int zCoord, int i5) {
        int meta = world.getBlockMetadata(xCoord, yCoord, zCoord);
        if (meta == 0 && world.getBlockId(xCoord, yCoord + 1, zCoord) != this.id
            || world.getBlockId(xCoord, yCoord - 1, zCoord) == 0
            || meta == 1 && world.getBlockId(xCoord, yCoord - 1, zCoord) != this.id) {
            this.dropBlock(world, xCoord, yCoord, zCoord, meta);
            world.setBlockWithNotify(xCoord, yCoord, zCoord, 0);
            world.playEffect(2001, xCoord, yCoord, zCoord, world.getBlockMetadata(xCoord, yCoord, zCoord) << 12 | this.id);
        }
    }

    @Override
    public void spawnParticles(World world, int xCoord, int yCoord, int zCoord, Random random) {
        int meta = world.getBlockMetadata(xCoord, yCoord, zCoord);
        if (meta == 1) {
            world.spawnParticle(ParticleType.SMOKE, xCoord + 0.5, yCoord + 1.0, zCoord + 0.5, 0.0, 0.0, 0.0);
            world.spawnParticle(ParticleType.FLAME, xCoord + 0.5, yCoord + 1.0, zCoord + 0.5, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public int dropID(int blockid, Random rand) {
        return blockid == 1 ? 0 : this.id;
    }

    @Override
    public boolean particles3D() {
        return false;
    }
}
