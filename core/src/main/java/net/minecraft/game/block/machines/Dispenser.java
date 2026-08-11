package net.minecraft.game.block.machines;

import java.util.Random;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.block.tileentity.container.DispenserEntity;
import net.minecraft.game.block.utility.ContainerBlock;
import net.minecraft.game.entity.arrow.Arrow;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.entity.projectile.Egg;
import net.minecraft.game.entity.projectile.Fireball;
import net.minecraft.game.entity.projectile.Projectile;
import net.minecraft.game.entity.projectile.Rock;
import net.minecraft.game.entity.projectile.Sandball;
import net.minecraft.game.entity.projectile.Snowball;
import net.minecraft.game.entity.projectile.Windball;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.misc.ArrowItem;
import net.minecraft.game.item.misc.ThrowableItem;
import net.minecraft.game.item.weapons.Bow;
import net.minecraft.game.item.weapons.Crossbow;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSource;
import net.minecraft.util.math.MathHelper;

public class Dispenser extends ContainerBlock {
    private Icon top;
    private Icon front;
    private Icon bottom;

    public Dispenser(int id) {
        super(id, MaterialList.metal);
    }

    @Override
    public int tickRate() {
        return 4;
    }

    @Override
    public void onPlaced(World world, Player player, int x, int y, int z, int side) {
        super.onPlaced(world, player, x, y, z, side);
        int angleFacing = MathHelper.floor_double(player.yaw * 4.0F / 360.0F + 0.5) & 3;
        if (angleFacing == 0) {
            world.setBlockMetadataWithNotify(x, y, z, 2);
        }

        if (angleFacing == 1) {
            world.setBlockMetadataWithNotify(x, y, z, 5);
        }

        if (angleFacing == 2) {
            world.setBlockMetadataWithNotify(x, y, z, 3);
        }

        if (angleFacing == 3) {
            world.setBlockMetadataWithNotify(x, y, z, 4);
        }
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("machines/dispenserSide");
        this.front = ir.registerIcon("machines/dispenserFront");
        this.top = ir.registerIcon("machines/dispenserTop");
        this.bottom = ir.registerIcon("machines/dispenserBottom");
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
            DispenserEntity dispenser = (DispenserEntity)world.getTileEntity(x, y, z);
            player.displayGUIDispenser(dispenser);
        }

        return true;
    }

    private void dispenseItem(World world, int x, int y, int z, Random rand) {
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

        DispenserEntity dispenser = (DispenserEntity)world.getTileEntity(x, y, z);
        ItemStack stack = dispenser.getRandomStack();
        double offsetX = x + xDir * 0.6 + 0.5;
        double offsetY = y + 0.5;
        double offsetZ = z + zDir * 0.6 + 0.5;
        if (stack != null) {
            if (stack.getItem() instanceof ArrowItem) {
                Arrow arrow = new Arrow(world, offsetX, offsetY, offsetZ);
                Class<? extends Arrow> clazz = Bow.arrowsMap.get(stack.itemID);
                if (clazz != null) {
                    try {
                        arrow = clazz.getConstructor(World.class, double.class, double.class, double.class).newInstance(world, offsetX, offsetY, offsetZ);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    clazz = Crossbow.arrowsMap.get(stack.itemID);

                    try {
                        arrow = clazz.getConstructor(World.class, double.class, double.class, double.class).newInstance(world, offsetX, offsetY, offsetZ);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                arrow.setHeading(xDir, 0.1, zDir, 1.1F, 6.0F);
                arrow.shotByPlayer = true;
                world.spawnEntity(arrow);
                world.playBlockSound(x + 0.5F, y + 0.5F, z + 0.5F, "random.bow", 1.0F, world.rand.nextFloat() * 0.4F + 0.8F);
                this.spawnSmoke(world, x, y, z, xDir, zDir, rand);
                return;
            }

            if (stack.getItem() instanceof ThrowableItem) {
                int dmg = ((ThrowableItem)stack.getItem()).type;
                Projectile throwable = new Snowball(world, offsetX, offsetY, offsetZ);
                switch (dmg) {
                    case 1:
                        throwable = new Egg(world, offsetX, offsetY, offsetZ);
                        break;
                    case 2:
                        throwable = new Windball(world, offsetX, offsetY, offsetZ);
                        break;
                    case 3:
                        throwable = new Sandball(world, offsetX, offsetY, offsetZ);
                        break;
                    case 4:
                        throwable = new Rock(world, offsetX, offsetY, offsetZ);
                        break;
                    case 5:
                        throwable = new Fireball(world, offsetX, offsetY, offsetZ);
                }

                throwable.setHeading(xDir, 0.1, zDir, 1.1F, 6.0F);
                world.spawnEntity(throwable);
                world.playBlockSound(x + 0.5F, y + 0.5F, z + 0.5F, "random.bow", 1.0F, world.rand.nextFloat() * 0.4F + 0.8F);
                this.spawnSmoke(world, x, y, z, xDir, zDir, rand);
                return;
            }

            DroppedItem drop = new DroppedItem(world, offsetX, offsetY - 0.3, offsetZ, stack);
            double leOffset = rand.nextDouble() * 0.1 + 0.2;
            drop.motionX = xDir * leOffset;
            drop.motionY = 0.2F;
            drop.motionZ = zDir * leOffset;
            drop.motionX = drop.motionX + rand.nextGaussian() * 0.0075F * 6.0;
            drop.motionY = drop.motionY + rand.nextGaussian() * 0.0075F * 6.0;
            drop.motionZ = drop.motionZ + rand.nextGaussian() * 0.0075F * 6.0;
            world.spawnEntity(drop);
            this.spawnSmoke(world, x, y, z, xDir, zDir, rand);
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
            boolean receivingPower = world.hasNeighborSignal(x, y, z) || world.hasNeighborSignal(x, y + 1, z);
            if (receivingPower) {
                world.forceTick(x, y, z, this.id);
            }
        }
    }

    @Override
    public void onTick(World world, int x, int y, int z, Random rand) {
        if (world.hasNeighborSignal(x, y, z) || world.hasNeighborSignal(x, y + 1, z)) {
            this.dispenseItem(world, x, y, z, rand);
        }
    }

    @Override
    public TileEntity getBlockEntity() {
        return new DispenserEntity();
    }

    @Override
    public void onBlockRemoval(World world, int x, int y, int z) {
        this.clearInventory(world, x, y, z);
        super.onBlockRemoval(world, x, y, z);
    }
}
