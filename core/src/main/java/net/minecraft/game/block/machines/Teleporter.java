package net.minecraft.game.block.machines;

import java.util.List;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.game.world.util.Position;
import net.minecraft.localization.Translator;
import net.minecraft.statistics.StatList;
import net.minecraft.statistics.achievement.AchievementList;
import net.minecraft.util.math.MathHelper;

public class Teleporter extends Block {
    public static final String[] types = new String[]{
        "teleporterRed",
        "teleporterBlue",
        "teleporterGreen",
        "teleporterPurple",
        "teleporterOrange",
        "teleporter",
        "teleporterRedActive",
        "teleporterBlueActive",
        "teleporterGreenActive",
        "teleporterPurpleActive",
        "teleporterOrangeActive",
        "teleporterActive"
    };
    private Icon[] icons;

    public Teleporter(int blockid, Material material) {
        super(blockid, material);
        this.hasStates = true;
        this.multipleNames = true;
        this.names = types;
    }

    @Override
    public Icon getIcon(int side, int meta) {
        if (meta < 0 || meta >= this.icons.length) {
            meta = 0;
        }

        return this.icons[meta];
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.icons = new Icon[types.length];

        for (int index = 0; index < this.icons.length; index++) {
            this.icons[index] = ir.registerIcon("teleporters/" + types[index]);
        }
    }

    @Override
    public int getMaxMeta() {
        return 11;
    }

    @Override
    public int pickMeta(int id) {
        return id;
    }

    @Override
    public int dropMeta(int id) {
        return id;
    }

    @Override
    public void getSubStacks(int id, CreativeTabs ct, List<ItemStack> ls) {
        for (int i = 0; i <= this.getMaxMeta(); i++) {
            ls.add(new ItemStack(id, 1, i));
        }
    }

    @Override
    public boolean blockActivated(World world, int xCoord, int yCoord, int zCoord, Player player) {
        player.sitting = false;
        int meta = world.getBlockMetadata(xCoord, yCoord, zCoord);
        boolean state = false;
        if (player.hook != null) {
            return false;
        }

        if (meta < 6) {
            if (player.teleporterPositionsOut.get(player.dimension)[meta] != null) {
                player.setPosition(
                    MathHelper.floor(player.teleporterPositionsOut.get(player.dimension)[meta].x) + 0.5,
                    MathHelper.floor(player.teleporterPositionsOut.get(player.dimension)[meta].y) + 3.0,
                    MathHelper.floor(player.teleporterPositionsOut.get(player.dimension)[meta].z) + 0.5
                );
                state = true;
            }
        } else if (player.teleporterPositionsIn.get(player.dimension)[meta - 6] != null) {
            player.setPosition(
                MathHelper.floor(player.teleporterPositionsIn.get(player.dimension)[meta - 6].x) + 0.5,
                MathHelper.floor(player.teleporterPositionsIn.get(player.dimension)[meta - 6].y) + 3.0,
                MathHelper.floor(player.teleporterPositionsIn.get(player.dimension)[meta - 6].z) + 0.5
            );
            state = true;
        }

        if (state) {
            world.playSoundEffect(player.posX, player.posY, player.posZ, "random.breath", 1.0F, 1.0F / (world.rand.nextFloat() * 0.4F + 0.8F));

            for (int i = 0; i < 20; i++) {
                world.spawnParticle(
                    ParticleType.EXPLODE,
                    player.posX + world.rand.nextFloat() * player.width * 2.0F - player.width,
                    player.posY + world.rand.nextFloat() * player.height - 1.0,
                    player.posZ + world.rand.nextFloat() * player.width * 2.0F - player.width,
                    world.rand.nextGaussian() * 0.02,
                    world.rand.nextGaussian() * 0.02,
                    world.rand.nextGaussian() * 0.02
                );
            }

            player.addStat(StatList.teleporterUse, 1);
            player.triggerAchievement(AchievementList.teleport);
        } else {
            player.addChatMessage(Translator.getInstance().translateKey("tile.teleporter.occupied"));
        }

        return true;
    }

    @Override
    public void onBlockRemoval(World world, int xCoord, int yCoord, int zCoord) {
        Player player = world.getClosestPlayer(xCoord, yCoord, zCoord, 8.0);
        int meta = world.getBlockMetadata(xCoord, yCoord, zCoord);
        if (player != null) {
            if (meta < 6) {
                Position in = player.teleporterPositionsIn.get(player.dimension)[meta];
                if (in != null && xCoord == in.x && yCoord == in.y && zCoord == in.z) {
                    player.teleporterPositionsIn.get(player.dimension)[meta] = null;
                }
            } else {
                Position out = player.teleporterPositionsOut.get(player.dimension)[meta - 6];
                if (out != null && xCoord == out.x && yCoord == out.y && zCoord == out.z) {
                    player.teleporterPositionsOut.get(player.dimension)[meta - 6] = null;
                }
            }
        }
    }

    @Override
    public void onPlaced(World world, Player player, int x, int y, int z, int side) {
        super.onPlaced(world, player, x, y, z, side);
        ItemStack stack = player.getCurrentEquippedItem();
        if (player.inventory.canUseItem()) {
            stack = player.inventory.getOffHandItem();
        }

        if (stack.itemDmg < 6) {
            player.teleporterPositionsIn.get(player.dimension)[stack.itemDmg] = new Position(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
        } else {
            player.teleporterPositionsOut.get(player.dimension)[stack.itemDmg - 6] = new Position(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
        }
    }
}
