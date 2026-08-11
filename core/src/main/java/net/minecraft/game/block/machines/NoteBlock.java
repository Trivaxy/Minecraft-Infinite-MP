package net.minecraft.game.block.machines;

import net.minecraft.game.ParticleType;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.block.tileentity.other.NoteBlockEntity;
import net.minecraft.game.block.utility.ContainerBlock;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;

public class NoteBlock extends ContainerBlock {
    public NoteBlock(int id) {
        super(id, MaterialList.wood);
    }

    @Override
    public void onNeighborChange(World world, int x, int y, int z, int id) {
        if (id > 0 && BlockList.blocks[id].providesPower()) {
            boolean powered = world.hasDirectSignal(x, y, z);
            NoteBlockEntity musicBox = (NoteBlockEntity)world.getTileEntity(x, y, z);
            if (musicBox.playedSound != powered) {
                if (powered) {
                    musicBox.triggerNote(world, x, y, z);
                }

                musicBox.playedSound = powered;
            }
        }
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("machines/" + this.name);
    }

    @Override
    public Icon getIcon(int side, int meta) {
        return side != 1 && side != 0 ? this.blockIcon : BlockList.jukeBox.blockIcon;
    }

    @Override
    public boolean blockActivated(World world, int x, int y, int z, Player player) {
        if (!world.isOnline) {
            NoteBlockEntity noteBlock = (NoteBlockEntity)world.getTileEntity(x, y, z);
            noteBlock.changePitch();
            noteBlock.triggerNote(world, x, y, z);
            player.addStat(StatList.noteUse, 1);
        }

        return true;
    }

    @Override
    public void blockPunched(World world, int x, int y, int z, Player player) {
        if (!world.isOnline) {
            NoteBlockEntity noteBlock = (NoteBlockEntity)world.getTileEntity(x, y, z);
            noteBlock.triggerNote(world, x, y, z);
        }
    }

    @Override
    public TileEntity getBlockEntity() {
        return new NoteBlockEntity();
    }

    @Override
    public void blockEvent(World world, int x, int y, int z, int type, int meta) {
        float pitch = (float)Math.pow(2.0, (meta - 12) / 12.0);
        String snd = "harp";
        if (type == 1) {
            snd = "bd";
        }

        if (type == 2) {
            snd = "snare";
        }

        if (type == 3) {
            snd = "hat";
        }

        if (type == 4) {
            snd = "bassattack";
        }

        if (type == 5) {
            snd = "pling";
        }

        if (type == 6) {
            snd = "guitar";
        }

        if (type == 7) {
            snd = "bell";
        }

        if (type == 8) {
            snd = "flute";
        }

        if (type == 9) {
            snd = "xylobone";
        }

        world.playBlockSound(x + 0.5F, y + 0.5F, z + 0.5F, "note." + snd, 3.0F, pitch);
        world.spawnParticle(ParticleType.NOTE, x + 0.5F, y + 1.2F, z + 0.5F, meta / 24.0F, 0.0, 0.0);
    }
}
