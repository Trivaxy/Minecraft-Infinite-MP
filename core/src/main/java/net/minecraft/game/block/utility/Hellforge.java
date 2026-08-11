package net.minecraft.game.block.utility;

import java.util.Random;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.block.tileentity.smelters.HellforgeEntity;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class Hellforge extends Furnace {
    public Hellforge(int blockid) {
        super(blockid, true);
    }

    @Override
    public void spawnParticles(World world, int xCoord, int yCoord, int zCoord, Random rand) {
        int meta = world.getBlockMetadata(xCoord, yCoord, zCoord);
        float xOffset = xCoord + 0.5F;
        float yOffset = yCoord + 0.0F + rand.nextFloat() * 6.0F / 16.0F;
        float zOffset = zCoord + 0.5F;
        float offsetHorizontal = 0.52F;
        float offsetVertical = rand.nextFloat() * 0.6F - 0.3F;
        if (meta == 4) {
            world.spawnParticle(ParticleType.SMOKE, xOffset - offsetHorizontal, yOffset, zOffset + offsetVertical, 0.0, 0.0, 0.0);
            world.spawnParticle(ParticleType.BLUEFLAME, xOffset - offsetHorizontal, yOffset, zOffset + offsetVertical, 0.0, 0.0, 0.0);
        } else if (meta == 5) {
            world.spawnParticle(ParticleType.SMOKE, xOffset + offsetHorizontal, yOffset, zOffset + offsetVertical, 0.0, 0.0, 0.0);
            world.spawnParticle(ParticleType.BLUEFLAME, xOffset + offsetHorizontal, yOffset, zOffset + offsetVertical, 0.0, 0.0, 0.0);
        } else if (meta == 2) {
            world.spawnParticle(ParticleType.SMOKE, xOffset + offsetVertical, yOffset, zOffset - offsetHorizontal, 0.0, 0.0, 0.0);
            world.spawnParticle(ParticleType.BLUEFLAME, xOffset + offsetVertical, yOffset, zOffset - offsetHorizontal, 0.0, 0.0, 0.0);
        } else if (meta == 3) {
            world.spawnParticle(ParticleType.SMOKE, xOffset + offsetVertical, yOffset, zOffset + offsetHorizontal, 0.0, 0.0, 0.0);
            world.spawnParticle(ParticleType.BLUEFLAME, xOffset + offsetVertical, yOffset, zOffset + offsetHorizontal, 0.0, 0.0, 0.0);
        }

        HellforgeEntity hellforge = (HellforgeEntity)world.getTileEntity(xCoord, yCoord, zCoord);
        if (hellforge != null && hellforge.fuel >= 4500) {
            world.spawnParticle(ParticleType.EXPLODE, xCoord + 0.5F, yCoord + 1.0F, zCoord + 0.5F, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public boolean blockActivated(World world, int x, int y, int z, Player player) {
        if (!world.isOnline) {
            HellforgeEntity hellforge = (HellforgeEntity)world.getTileEntity(x, y, z);
            player.displayGUIHellforge(hellforge);
        }

        return true;
    }

    @Override
    public TileEntity getBlockEntity() {
        return new HellforgeEntity();
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("workstations/hellforge_side");
        this.front = ir.registerIcon("workstations/hellforge");
        this.top = ir.registerIcon("workstations/hellforge_top");
        this.bottom = ir.registerIcon("workstations/hellforge_bottom");
    }

    @Override
    public void onBlockRemoval(World world, int x, int y, int z) {
        HellforgeEntity hellforge = (HellforgeEntity)world.getTileEntity(x, y, z);
        if (hellforge.fuel >= 1500) {
            world.createExplosion(null, x, y, z, 2.0F);
        }

        super.onBlockRemoval(world, x, y, z);
    }

    @Override
    public int dropID(int blockid, Random random) {
        return BlockList.hellforge.id;
    }

    @Override
    public int pickID(int blockid) {
        return BlockList.hellforge.id;
    }
}
