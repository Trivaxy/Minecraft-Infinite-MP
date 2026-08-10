package net.minecraft.game.block.utility;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.building.Sand;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSource;
import net.minecraft.util.math.MathHelper;

public class Anvil extends Sand {
    private static final String[] iconNames = new String[]{"workstations/anvil_top", "workstations/anvil_top_damaged_1", "workstations/anvil_top_damaged_2"};
    public int iconState = 0;
    private Icon[] icons;

    public Anvil(int id) {
        super(id, MaterialList.metal);
        this.hasStates = true;
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public Icon getIcon(int side, int meta) {
        if (this.iconState == 3 && side == 1) {
            int state = (meta >> 4) % this.icons.length;
            return this.icons[state];
        } else {
            return this.blockIcon;
        }
    }

    @Override
    public int getMaxMeta() {
        return 3;
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("workstations/anvil_base");
        this.icons = new Icon[iconNames.length];

        for (int index = 0; index < this.icons.length; index++) {
            this.icons[index] = ir.registerIcon(iconNames[index]);
        }
    }

    @Override
    public void onPlaced(World world, Player player, int x, int y, int z, int side) {
        super.onPlaced(world, player, x, y, z, side);
        int dir = MathHelper.floor_double(player.yaw * 4.0F / 360.0F + 0.5) & 3;
        int meta = world.getBlockMetadata(x, y, z) >> 2;
        dir = ++dir % 4;
        if (dir == 0) {
            world.setBlockMetadataWithNotify(x, y, z, 2 | meta << 2);
        }

        if (dir == 1) {
            world.setBlockMetadataWithNotify(x, y, z, 3 | meta << 2);
        }

        if (dir == 2) {
            world.setBlockMetadataWithNotify(x, y, z, 0 | meta << 2);
        }

        if (dir == 3) {
            world.setBlockMetadataWithNotify(x, y, z, 1 | meta << 2);
        }
    }

    @Override
    public boolean blockActivated(World world, int x, int y, int z, Player player) {
        if (!world.isOnline) {
            player.displayGUIAnvil(x, y, z);
        }

        return true;
    }

    @Override
    public void modifyOutline(WorldSource world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z) & 3;
        if (meta != 3 && meta != 1) {
            this.setBounds(0.125, 0.0, 0.0, 0.875, 1.0, 1.0);
        } else {
            this.setBounds(0.0, 0.0, 0.125, 1.0, 1.0, 0.875);
        }
    }

    @Override
    public int dropID(int blockid, Random random) {
        return 0;
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.ANVIL;
    }

    @Override
    public boolean renderFace(WorldSource world, int x, int y, int z, int side) {
        return true;
    }
}
