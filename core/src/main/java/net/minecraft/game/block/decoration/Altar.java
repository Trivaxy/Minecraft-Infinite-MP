package net.minecraft.game.block.decoration;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSource;
import net.minecraft.game.world.util.Position;
import net.minecraft.localization.Translator;
import net.minecraft.statistics.achievement.AchievementList;

public class Altar extends Block {
    private Icon top;
    private Icon bottom;
    private Icon angelStatue;

    public Altar(int blockid, Material material) {
        super(blockid, material);
        this.setBounds(0.0, 0.0, 0.0, 1.0, 0.75, 1.0);
        this.setLightOpacity(0);
    }

    @Override
    public boolean renderFace(WorldSource world, int xCoord, int yCoord, int zCoord, int side) {
        int currID = world.getBlockId(xCoord, yCoord, zCoord);
        return currID == this.id && side > 1 ? false : super.renderFace(world, xCoord, yCoord, zCoord, side);
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.ALTAR;
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public boolean blockActivated(World world, int x, int y, int z, Player player) {
        if (player.dimension == 2) {
            player.setSpawn(new Position(x, y, z), 2, false);
            player.triggerAchievement(AchievementList.prayer);
            player.addChatMessage(Translator.getInstance().translateKey("shrine.set"));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onBlockRemoval(World world, int xCoord, int yCoord, int zCoord) {
        Player player = world.getClosestPlayer(xCoord, yCoord, zCoord, 16.0);
        if (player != null && player.getSpawn(2) != null && player.getSpawn(2).x == xCoord && player.getSpawn(2).y == yCoord && player.getSpawn(2).z == zCoord) {
            player.setSpawn(null, 2, false);
        }
    }

    @Override
    public Icon getIcon(int side, int meta) {
        if (meta == 1) {
            return this.angelStatue;
        } else if (side == 0) {
            return this.bottom;
        } else {
            return side == 1 ? this.top : this.blockIcon;
        }
    }

    @Override
    public void registerIcons(IconRegister r) {
        super.registerIcons(r);
        this.top = r.registerIcon("shrine_top");
        this.bottom = r.registerIcon("shrine_bottom");
        this.angelStatue = r.registerIcon("angel");
    }
}
