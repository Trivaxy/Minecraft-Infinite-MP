package net.minecraft.game.block.utility;

import java.util.Random;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.block.tileentity.other.JukeboxEntity;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class Jukebox extends ContainerBlock {
    private Icon top;
    private Icon bottom;

    public Jukebox(int id) {
        super(id, MaterialList.wood);
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("workstations/musicBox");
        this.top = ir.registerIcon("workstations/jukeBoxTop");
        this.bottom = ir.registerIcon("workstations/jukeBoxBottom");
    }

    @Override
    public Icon getIcon(int side, int meta) {
        return side == 1 ? this.top : (side == 0 ? this.bottom : this.blockIcon);
    }

    @Override
    public boolean blockActivated(World world, int x, int y, int z, Player player) {
        if (world.getBlockMetadata(x, y, z) == 0) {
            return false;
        }

        this.ejectRecord(world, x, y, z);
        return true;
    }

    public void playMusic(World world, int x, int y, int z, int recordID) {
        if (!world.isOnline) {
            JukeboxEntity jukebox = (JukeboxEntity)world.getTileEntity(x, y, z);
            jukebox.record = recordID;
            jukebox.onChanged();
            world.setBlockMetadataWithNotify(x, y, z, 1);
        }
    }

    public void ejectRecord(World world, int x, int y, int z) {
        if (!world.isOnline) {
            JukeboxEntity jukebox = (JukeboxEntity)world.getTileEntity(x, y, z);
            int recordID = jukebox.record;
            if (recordID > 0) {
                world.playRecord((String)null, x, y, z, true);
                jukebox.record = 0;
                jukebox.onChanged();
                world.setBlockMetadataWithNotify(x, y, z, 0);
                float offset = 0.7F;
                float xOffset = world.rand.nextFloat() * offset + (1.0F - offset) * 0.5F;
                float yOffset = world.rand.nextFloat() * offset + (1.0F - offset) * 0.2F + 0.6F;
                float zOffset = world.rand.nextFloat() * offset + (1.0F - offset) * 0.5F;
                DroppedItem drop = new DroppedItem(world, x + xOffset, y + yOffset, z + zOffset, new ItemStack(recordID, 1, 0));
                world.spawnEntity(drop);
            }
        }
    }

    @Override
    public void onBlockRemoval(World world, int x, int y, int z) {
        this.ejectRecord(world, x, y, z);
        super.onBlockRemoval(world, x, y, z);
    }

    @Override
    public void dropBlockItem(World world, int x, int y, int z, int meta, float f, boolean fortune) {
        if (!world.isOnline) {
            super.dropBlockItem(world, x, y, z, meta, f, fortune);
        }
    }

    @Override
    public void spawnParticles(World world, int x, int y, int z, Random rand) {
        if (world.getTileEntity(x, y, z) != null && ((JukeboxEntity)world.getTileEntity(x, y, z)).record > 0) {
            world.spawnParticle(ParticleType.NOTE, x + 0.5F, y + 1.2F, z + 0.5F, rand.nextInt(3) / 24.0F, 0.0, 0.0);
        }
    }

    @Override
    public TileEntity getBlockEntity() {
        return new JukeboxEntity();
    }
}
