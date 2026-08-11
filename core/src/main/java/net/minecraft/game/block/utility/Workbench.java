package net.minecraft.game.block.utility;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class Workbench extends Block {
    private Icon top;
    private Icon front;
    private Icon bottom;

    public Workbench(int blockid) {
        super(blockid, MaterialList.wood);
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("workstations/workbench_side");
        this.top = ir.registerIcon("workstations/workbench_top");
        this.front = ir.registerIcon("workstations/workbench_front");
        this.bottom = ir.registerIcon("workstations/workbench_bottom");
    }

    @Override
    public Icon getIcon(int side, int meta) {
        return side == 1 ? this.top : (side == 0 ? this.bottom : (side != 2 && side != 4 ? this.blockIcon : this.front));
    }

    @Override
    public boolean blockActivated(World world, int xCoord, int yCoord, int zCoord, Player player) {
        if (!world.isOnline) {
            player.displayWorkbenchGUI(xCoord, yCoord, zCoord, false);
        }

        return true;
    }
}
