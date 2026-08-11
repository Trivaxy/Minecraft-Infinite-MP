package net.minecraft.game.block.machines;

import com.mojang.nbt.tag.special.CompoundTag;
import java.util.Random;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.block.tileentity.container.CrafterEntity;
import net.minecraft.game.block.utility.ContainerBlock;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSource;
import net.minecraft.statistics.achievement.AchievementList;
import net.minecraft.util.math.MathHelper;

public class Crafter extends ContainerBlock {
    private Icon top;
    private Icon front;
    private Icon bottom;

    public Crafter(int id) {
        super(id, MaterialList.vacuum);
        this.setTickable(true);
    }

    @Override
    public int tickRate() {
        return 4;
    }

    @Override
    public void onPlaced(World world, Player player, int x, int y, int z, int id) {
        super.onPlaced(world, player, x, y, z, id);
        int facing = MathHelper.floor_double(player.yaw * 4.0F / 360.0F + 0.5) & 3;
        if (facing == 0) {
            world.setBlockMetadataWithNotify(x, y, z, 2);
        }

        if (facing == 1) {
            world.setBlockMetadataWithNotify(x, y, z, 5);
        }

        if (facing == 2) {
            world.setBlockMetadataWithNotify(x, y, z, 3);
        }

        if (facing == 3) {
            world.setBlockMetadataWithNotify(x, y, z, 4);
        }
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("machines/crafter_south");
        this.front = ir.registerIcon("machines/crafter_north");
        this.top = ir.registerIcon("machines/crafter_top");
        this.bottom = ir.registerIcon("machines/crafter_bottom");
    }

    @Override
    public Icon getIcon(int side, int meta) {
        return side == 1 ? this.top : (side == 0 ? this.bottom : (side == 3 ? this.front : this.blockIcon));
    }

    @Override
    public Icon getTexture(WorldSource world, int xCoord, int yCoord, int zCoord, int side) {
        if (side == 0) {
            return this.bottom;
        } else if (side == 1) {
            return this.top;
        } else {
            return side != world.getBlockMetadata(xCoord, yCoord, zCoord) ? this.blockIcon : this.front;
        }
    }

    @Override
    public boolean blockActivated(World world, int x, int y, int z, Player player) {
        if (!world.isOnline) {
            CrafterEntity crafter = (CrafterEntity)world.getTileEntity(x, y, z);
            player.displayGUICrafter(crafter);
        }

        return true;
    }

    private void craftItem(World world, int x, int y, int z, Random rand) {
        int meta = world.getBlockMetadata(x, y, z);
        byte xDir = 0;
        byte zDir = 0;
        if (meta == 3) {
            zDir = 1;
        } else if (meta == 2) {
            zDir = -1;
        } else if (meta == 5) {
            xDir = 1;
        } else {
            xDir = -1;
        }

        CrafterEntity crafter = (CrafterEntity)world.getTileEntity(x, y, z);
        ItemStack craftResult = crafter.craftResult.getStackInSlot(0);
        double xOffset = x + xDir * 0.6 + 0.5;
        double yOffset = y + 0.5;
        double zOffset = z + zDir * 0.6 + 0.5;
        boolean canDispenseItem = true;

        for (int i = 0; i < crafter.inventory.getSize(); i++) {
            if (crafter.inventory.getStackInSlot(i) != null && crafter.inventory.getStackInSlot(i).stackSize <= 1) {
                canDispenseItem = false;
            }
        }

        if (craftResult != null && canDispenseItem) {
            DroppedItem drop = new DroppedItem(world, xOffset, yOffset - 0.3, zOffset, craftResult);
            double offset = rand.nextDouble() * 0.1 + 0.2;
            drop.motionX = xDir * offset;
            drop.motionY = 0.2F;
            drop.motionZ = zDir * offset;
            drop.motionX = drop.motionX + rand.nextGaussian() * 0.0075F * 6.0;
            drop.motionY = drop.motionY + rand.nextGaussian() * 0.0075F * 6.0;
            drop.motionZ = drop.motionZ + rand.nextGaussian() * 0.0075F * 6.0;
            world.spawnEntity(drop);
            this.spawnSmoke(world, x, y, z, xDir, zDir, rand);
            crafter.decreaseStackSizes();
            if (craftResult.itemID == BlockList.crafter.id) {
                world.getClosestPlayer(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z), 6.0)
                    .triggerAchievement(AchievementList.crafter);
            }
        }

        world.playBlockSound(x + 0.5F, y + 0.5F, z + 0.5F, "random.click", 1.0F, world.rand.nextFloat() * 0.4F + 0.8F);
    }

    private void spawnSmoke(World world, int x, int y, int z, int xDir, int zDir, Random rand) {
        int facing = xDir + 1 + (zDir + 1) * 3;
        int xOff = facing % 3 - 1;
        int zOff = facing / 3 % 3 - 1;
        double offsetX = x + xOff * 0.6 + 0.5;
        double offsetY = y + 0.5;
        double offsetZ = z + zOff * 0.6 + 0.5;

        for (int i = 0; i < 10; i++) {
            double force = rand.nextDouble() * 0.2 + 0.01;
            double particleX = offsetX + xOff * 0.01 + (rand.nextDouble() - 0.5) * zOff * 0.5;
            double particleY = offsetY + (rand.nextDouble() - 0.5) * 0.5;
            double particleZ = offsetZ + zOff * 0.01 + (rand.nextDouble() - 0.5) * xOff * 0.5;
            double forceX = xOff * force + rand.nextGaussian() * 0.01;
            double forceY = -0.03 + rand.nextGaussian() * 0.01;
            double forceZ = zOff * force + rand.nextGaussian() * 0.01;
            world.spawnParticle(ParticleType.SMOKE, particleX, particleY, particleZ, forceX, forceY, forceZ);
        }
    }

    @Override
    public void onNeighborChange(World world, int x, int y, int z, int id) {
        if (id > 0 && BlockList.blocks[id].providesPower()) {
            boolean beingPowered = world.hasNeighborSignal(x, y, z) || world.hasNeighborSignal(x, y + 1, z);
            if (beingPowered) {
                world.forceTick(x, y, z, this.id);
            }
        }
    }

    @Override
    public void onTick(World world, int x, int y, int z, Random rand) {
        if (world.hasNeighborSignal(x, y, z) || world.hasNeighborSignal(x, y + 1, z)) {
            this.craftItem(world, x, y, z, rand);
        }
    }

    @Override
    public TileEntity getBlockEntity() {
        return new CrafterEntity();
    }

    @Override
    public void onBlockRemoval(World world, int x, int y, int z) {
        CrafterEntity inv = (CrafterEntity)world.getTileEntity(x, y, z);
        if (inv != null) {
            for (int slot = 0; slot < inv.inventory.getSize(); slot++) {
                ItemStack stack = inv.inventory.getStackInSlot(slot);
                if (stack != null) {
                    float offsetX = world.rand.nextFloat() * 0.8F + 0.1F;
                    float offsetY = world.rand.nextFloat() * 0.8F + 0.1F;
                    float offsetZ = world.rand.nextFloat() * 0.8F + 0.1F;
                    CompoundTag nbt = null;
                    if (stack.hasTag()) {
                        nbt = stack.getTag();
                    }

                    ItemStack theItem = new ItemStack(stack.itemID, stack.stackSize, stack.itemDmg, stack.customName);
                    if (nbt != null) {
                        theItem = new ItemStack(stack.itemID, stack.stackSize, stack.itemDmg, stack.customName, nbt);
                    }

                    DroppedItem drop = new DroppedItem(world, (double)x + offsetX, (double)y + offsetY, (double)z + offsetZ, theItem);
                    drop.motionX = (float)world.rand.nextGaussian() * 0.05F;
                    drop.motionY = (float)world.rand.nextGaussian() * 0.05F + 0.2F;
                    drop.motionZ = (float)world.rand.nextGaussian() * 0.05F;
                    world.spawnEntity(drop);
                }
            }

            super.onBlockRemoval(world, x, y, z);
        }
    }
}
