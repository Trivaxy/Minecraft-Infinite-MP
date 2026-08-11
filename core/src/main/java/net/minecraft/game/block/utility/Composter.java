package net.minecraft.game.block.utility;

import net.minecraft.game.ParticleType;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSource;
import net.minecraft.statistics.achievement.AchievementList;

public class Composter extends Block {
    public Icon top;
    public Icon bottom;
    public Icon juice;

    public Composter(int blockid) {
        super(blockid, MaterialList.wood);
    }

    @Override
    public boolean blockActivated(World world, int x, int y, int z, Player player) {
        if (world.isOnline) {
            world.markForUpdate(x, y, z);
            return true;
        }

        boolean compostMainitem = player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem().canCompost();
        boolean compostSideitem = player.inventory.getOffHandItem() != null && player.inventory.getOffHandItem().getItem().canCompost();
        if (compostMainitem || compostSideitem) {
            if (compostMainitem) {
                player.inventory.getCurrentItem().stackSize--;
            } else if (compostSideitem) {
                player.inventory.getOffHandItem().stackSize--;
            }

            if (world.rand.nextInt(2) == 0) {
                world.setBlockMetadataWithNotify(x, y, z, world.getBlockMetadata(x, y, z) + 1);
                if (world.getBlockMetadata(x, y, z) >= 4) {
                    double xOffset = world.rand.nextFloat() * 0.7F + 0.15F;
                    double yOffset = world.rand.nextFloat() * 0.7F + 0.15F;
                    double zOffset = world.rand.nextFloat() * 0.7F + 0.15F;
                    DroppedItem drop = new DroppedItem(world, x + xOffset, y + yOffset, z + zOffset, new ItemStack(ItemList.fertilizer.id, 1));
                    world.spawnEntity(drop);
                    world.setBlockMetadataWithNotify(x, y, z, 0);
                    player.triggerAchievement(AchievementList.compost);
                }

                for (int i = 0; i < 7; i++) {
                    world.spawnParticle(
                        ParticleType.GROW,
                        (double)x + world.rand.nextFloat(),
                        (double)y + world.rand.nextFloat(),
                        (double)z + world.rand.nextFloat(),
                        world.rand.nextGaussian() * 0.02,
                        world.rand.nextGaussian() * 0.02,
                        world.rand.nextGaussian() * 0.02
                    );
                }
            }
        }

        return true;
    }

    @Override
    public Icon getIcon(int side, int meta) {
        return side == 1 ? this.juice : (side == 0 ? this.bottom : this.blockIcon);
    }

    @Override
    public Icon getTexture(WorldSource world, int xCoord, int yCoord, int zCoord, int side) {
        return side == 1 ? this.top : (side == 0 ? this.bottom : this.blockIcon);
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.top = ir.registerIcon("workstations/composter_top");
        this.bottom = ir.registerIcon("workstations/composter_bottom");
        this.blockIcon = ir.registerIcon("workstations/composter_side");
        this.juice = ir.registerIcon("workstations/composter_compost");
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.COMPOSTER;
    }
}
