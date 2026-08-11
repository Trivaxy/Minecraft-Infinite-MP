package net.minecraft.game.block.utility;

import java.util.Random;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.block.tileentity.smelters.SmokerEntity;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class Smoker extends Furnace {
    public Smoker(int blockid, boolean isActive) {
        super(blockid, isActive);
    }

    @Override
    public void spawnParticles(World world, int xCoord, int yCoord, int zCoord, Random rand) {
        if (this.active) {
            world.spawnParticle(ParticleType.EXPLODE, xCoord + 0.5F, yCoord + 1.0F, zCoord + 0.5F, 0.0, 0.0, 0.0);
        }

        super.spawnParticles(world, xCoord, yCoord, zCoord, rand);
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("workstations/smoker_side");
        this.front = ir.registerIcon(this.active ? "workstations/smoker_on" : "workstations/smoker_front");
        this.top = ir.registerIcon("workstations/smoker_top");
        this.bottom = ir.registerIcon("workstations/smoker_bottom");
    }

    @Override
    public boolean blockActivated(World world, int xCoord, int yCoord, int zCoord, Player player) {
        if (!world.isOnline) {
            SmokerEntity smoker = (SmokerEntity)world.getTileEntity(xCoord, yCoord, zCoord);
            player.displayGUISmoker(smoker);
        }

        return true;
    }

    public static void updateFurnaceBlockState(boolean isActive, World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        keepInventory = true;
        if (isActive) {
            world.setBlockWithNotify(x, y, z, BlockList.smokerActive.id);
        } else {
            world.setBlockWithNotify(x, y, z, BlockList.smokerIdle.id);
        }

        keepInventory = false;
        world.setBlockMetadataWithNotify(x, y, z, meta);
        tileEntity.validate();
        world.setBlockTileEntity(x, y, z, tileEntity);
    }

    @Override
    public TileEntity getBlockEntity() {
        return new SmokerEntity();
    }

    @Override
    public int dropID(int blockid, Random random) {
        return BlockList.smokerIdle.id;
    }

    @Override
    public int pickID(int blockid) {
        return BlockList.smokerIdle.id;
    }
}
