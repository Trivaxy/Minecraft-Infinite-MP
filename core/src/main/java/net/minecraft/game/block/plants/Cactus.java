package net.minecraft.game.block.plants;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.building.LayerBlock;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.animal.mounts.Camel;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class Cactus extends Block {
    private Icon top;
    private Icon bottom;

    public Cactus(int blockID) {
        super(blockID, MaterialList.cactus);
        this.setTickable(true);
        this.compostable = true;
    }

    @Override
    public void blockPunched(World world, int x, int y, int z, Player player) {
        if (player.getCurrentEquippedItem() == null) {
            player.damageEntity(DamageSource.cactus, 1);
        }
    }

    @Override
    public Icon getIcon(int side, int meta) {
        return side == 1 ? this.top : (side == 0 ? this.bottom : this.blockIcon);
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("plants/cactus_side");
        this.top = ir.registerIcon("plants/cactus_top");
        this.bottom = ir.registerIcon("plants/cactus_bottom");
    }

    @Override
    public void onTick(World world, int x, int y, int z, Random rand) {
        if (world.getBlockMaterial(x, y + 1, z) == MaterialList.air) {
            int maxHeight = 1;

            while (world.getBlockId(x, y - maxHeight, z) == this.id) {
                maxHeight++;
            }

            if (maxHeight < 3) {
                int meta = world.getBlockMetadata(x, y, z);
                if (meta == 15) {
                    world.setBlockWithNotify(x, y + 1, z, this.id);
                    world.setBlockMetadataWithNotify(x, y, z, 0);
                } else {
                    world.setBlockMetadataWithNotify(x, y, z, meta + 1);
                }
            }
        }
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.CACTUS;
    }

    @Override
    public boolean placeableAt(World world, int x, int y, int z) {
        return this.canStay(world, x, y, z);
    }

    @Override
    public void onNeighborChange(World world, int x, int y, int z, int id) {
        if (!this.canStay(world, x, y, z)) {
            this.dropBlock(world, x, y, z, world.getBlockMetadata(x, y, z));
            world.playEffect(2001, x, y, z, world.getBlockMetadata(x, y, z) << 12 | this.id);
            world.setBlockWithNotify(x, y, z, 0);
        }
    }

    @Override
    public boolean canStay(World world, int x, int y, int z) {
        if (world.getBlockMaterial(x - 1, y, z).isSolid() && !(BlockList.blocks[world.getBlockId(x - 1, y, z)] instanceof LayerBlock)) {
            return false;
        }

        if (world.getBlockMaterial(x + 1, y, z).isSolid() && !(BlockList.blocks[world.getBlockId(x + 1, y, z)] instanceof LayerBlock)) {
            return false;
        }

        if (world.getBlockMaterial(x, y, z - 1).isSolid() && !(BlockList.blocks[world.getBlockId(x, y, z - 1)] instanceof LayerBlock)) {
            return false;
        }

        if (world.getBlockMaterial(x, y, z + 1).isSolid() && !(BlockList.blocks[world.getBlockId(x, y, z + 1)] instanceof LayerBlock)) {
            return false;
        }

        int id = world.getBlockId(x, y - 1, z);
        return id == BlockList.cactus.id
            || id == BlockList.sand.id
            || id == BlockList.redSand.id
            || id == BlockList.silverSand.id
            || id == BlockList.susSand.id;
    }

    @Override
    public void onEntityCollision(Entity e, World world, int x, int y, int z) {
        if (!(e instanceof Camel)) {
            e.damageEntity(DamageSource.cactus, 1);
        }
    }
}
