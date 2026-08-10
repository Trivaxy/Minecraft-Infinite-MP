package net.minecraft.game.block.liquid;

import java.util.Random;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSource;

public abstract class LiquidBlock extends Block {
    public Icon[] icons;
    protected int fluidType = 1;

    protected LiquidBlock(int id, Material mat) {
        super(id, mat);
        if (mat == MaterialList.lava) {
            this.fluidType = 2;
        }

        this.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        this.setTickable(true);
    }

    @Override
    public void onEntityWalking(World world, int xCoord, int yCoord, int zCoord, Entity entity) {
        if ((this.material == MaterialList.water || this.material == MaterialList.lava)
            && entity instanceof Player
            && world.getBlockId(xCoord, yCoord + 1, zCoord) == 0
            && ((Player)entity).inventory.accessorySlots[0] != null
            && ((Player)entity).inventory.accessorySlots[0].getItem() == ItemList.ring) {
            for (int x = xCoord - 2; x < xCoord + 2; x++) {
                for (int z = zCoord - 2; z < zCoord + 2; z++) {
                    if (world.getBlockMaterial(x, yCoord, z) == MaterialList.water) {
                        world.setBlock(x, yCoord, z, BlockList.ice.id);
                    } else if (world.getBlockMaterial(x, yCoord, z) == MaterialList.lava) {
                        world.setBlock(x, yCoord, z, BlockList.obsidian.id);
                    }
                }
            }
        }
    }

    public static float getHeight(int fluidSize) {
        if (fluidSize >= 8) {
            fluidSize = 0;
        }

        return (fluidSize + 1) / 9.0F;
    }

    @Override
    public Icon getIcon(int side, int meta) {
        return side != 0 && side != 1 ? this.icons[1] : this.icons[0];
    }

    protected int getFlowDecay(World world, int xCoord, int yCoord, int zCoord) {
        return world.getBlockMaterial(xCoord, yCoord, zCoord) != this.material ? -1 : world.getBlockMetadata(xCoord, yCoord, zCoord);
    }

    protected int getEffectiveFlowDecay(WorldSource world, int xCoord, int yCoord, int zCoord) {
        if (world.getBlockMaterial(xCoord, yCoord, zCoord) != this.material) {
            return -1;
        }

        int meta = world.getBlockMetadata(xCoord, yCoord, zCoord);
        if (meta >= 8) {
            meta = 0;
        }

        return meta;
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public boolean canCollideWith(int id, boolean check) {
        return check && id == 0;
    }

    @Override
    public boolean renderFace(WorldSource w, int x, int y, int z, int s) {
        Block block = w.getBlock(x, y, z);
        Block blockAbove = w.getBlock(x, y + 1, z);
        if (this.material == MaterialList.water
            && block != null
            && block.material != this.material
            && blockAbove != null
            && blockAbove.material == this.material) {
            return false;
        } else {
            return block != null && block.material == this.material ? false : (s == 1 ? true : super.renderFace(w, x, y, z, s));
        }
    }

    @Override
    public BoundingBox getCollisionBox(World world, int xCoord, int yCoord, int zCoord) {
        return null;
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.FLUID;
    }

    @Override
    public int dropID(int blockid, Random rand) {
        return 0;
    }

    @Override
    public int quantityDropped(Random rand) {
        return 0;
    }

    private Vector getFlowVector(WorldSource world, int xCoord, int yCoord, int zCoord) {
        Vector vec = Vector.createVector(0.0, 0.0, 0.0);
        int decay = this.getEffectiveFlowDecay(world, xCoord, yCoord, zCoord);

        for (int side = 0; side < 4; side++) {
            int xOff = xCoord;
            int zOff = zCoord;
            if (side == 0) {
                xOff = xCoord - 1;
            }

            if (side == 1) {
                zOff = zCoord - 1;
            }

            if (side == 2) {
                xOff++;
            }

            if (side == 3) {
                zOff++;
            }

            int flow = this.getEffectiveFlowDecay(world, xOff, yCoord, zOff);
            if (flow < 0) {
                if (!world.getBlockMaterial(xOff, yCoord, zOff).isSolid()) {
                    flow = this.getEffectiveFlowDecay(world, xOff, yCoord - 1, zOff);
                    if (flow >= 0) {
                        int mult = flow - (decay - 8);
                        vec = vec.addVector((xOff - xCoord) * mult, (yCoord - yCoord) * mult, (zOff - zCoord) * mult);
                    }
                }
            } else if (flow >= 0) {
                int mult = flow - decay;
                vec = vec.addVector((xOff - xCoord) * mult, (yCoord - yCoord) * mult, (zOff - zCoord) * mult);
            }
        }

        if (world.getBlockMetadata(xCoord, yCoord, zCoord) >= 8) {
            boolean flowing = false;
            if (flowing || this.renderFace(world, xCoord, yCoord, zCoord - 1, 2)) {
                flowing = true;
            }

            if (flowing || this.renderFace(world, xCoord, yCoord, zCoord + 1, 3)) {
                flowing = true;
            }

            if (flowing || this.renderFace(world, xCoord - 1, yCoord, zCoord, 4)) {
                flowing = true;
            }

            if (flowing || this.renderFace(world, xCoord + 1, yCoord, zCoord, 5)) {
                flowing = true;
            }

            if (flowing || this.renderFace(world, xCoord, yCoord + 1, zCoord - 1, 2)) {
                flowing = true;
            }

            if (flowing || this.renderFace(world, xCoord, yCoord + 1, zCoord + 1, 3)) {
                flowing = true;
            }

            if (flowing || this.renderFace(world, xCoord - 1, yCoord + 1, zCoord, 4)) {
                flowing = true;
            }

            if (flowing || this.renderFace(world, xCoord + 1, yCoord + 1, zCoord, 5)) {
                flowing = true;
            }

            if (flowing) {
                vec = vec.normalize().addVector(0.0, -6.0, 0.0);
            }
        }

        return vec.normalize();
    }

    @Override
    public void multiplyEntityVelocity(World world, int xCoord, int yCoord, int zCoord, Entity entity, Vector vec) {
        if (entity instanceof DroppedItem) {
            Vector flow = this.getFlowVector(world, xCoord, yCoord, zCoord);
            vec.xCoord = vec.xCoord + flow.xCoord;
            vec.yCoord = vec.yCoord + flow.yCoord;
            vec.zCoord = vec.zCoord + flow.zCoord;
        }
    }

    @Override
    public int tickRate() {
        return this.material == MaterialList.water ? 5 : (this.material == MaterialList.lava ? 30 : 0);
    }

    @Override
    public int getBrightness(WorldSource world, int x, int y, int z) {
        int brightness = world.getBrightness(x, y, z, 0);
        int brightnessAbove = world.getBrightness(x, y + 1, z, 0);
        int alpha = brightness & 0xFF;
        int alphaAbove = brightnessAbove & 0xFF;
        int opacity = brightness >> 16 & 0xFF;
        int opacityAbove = brightnessAbove >> 16 & 0xFF;
        return (alpha > alphaAbove ? alpha : alphaAbove) | (opacity > opacityAbove ? opacity : opacityAbove) << 16;
    }

    @Override
    public int renderPass() {
        return this.material == MaterialList.water ? 1 : 0;
    }

    @Override
    public boolean blocksPathfinder(WorldSource w, int x, int y, int z) {
        return this.material == MaterialList.lava;
    }

    @Override
    public void spawnParticles(World world, int xCoord, int yCoord, int zCoord, Random rand) {
        if (rand.nextInt(64) == 0) {
            int meta = world.getBlockMetadata(xCoord, yCoord, zCoord);
            if (meta > 0 && meta < 8) {
                if (this.material == MaterialList.water) {
                    world.playSoundEffect(
                        xCoord + 0.5F, yCoord + 0.5F, zCoord + 0.5F, "liquid.water", rand.nextFloat() * 0.25F + 0.75F, rand.nextFloat() * 1.0F + 0.5F
                    );
                } else {
                    world.playSoundEffect(
                        xCoord + 0.5F, yCoord + 0.5F, zCoord + 0.5F, "liquid.lava", rand.nextFloat() * 0.25F + 0.75F, rand.nextFloat() * 1.0F + 0.5F
                    );
                }
            }
        }

        if (this.material == MaterialList.lava
            && world.getBlockMaterial(xCoord, yCoord + 1, zCoord) == MaterialList.air
            && !world.isBlockFull(xCoord, yCoord + 1, zCoord)
            && rand.nextInt(100) == 0) {
            double xOff = xCoord + rand.nextFloat();
            double yOff = yCoord + this.maxY;
            double zOff = zCoord + rand.nextFloat();
            world.spawnParticle(ParticleType.LAVA, xOff, yOff, zOff, 0.0, 0.0, 0.0);
            world.playBlockSound(
                xCoord + 0.5F, yCoord + 0.5F, zCoord + 0.5F, "liquid.lavapop", rand.nextFloat() * 0.25F + 0.75F, rand.nextFloat() * 0.75F + 0.75F
            );
        }

        if (this.material == MaterialList.water && rand.nextInt(10) == 0) {
            byte meta = (byte)world.getBlockMetadata(xCoord, yCoord, zCoord);
            if (meta <= 0 || meta >= 8) {
                world.spawnParticle(ParticleType.SUSPENDED, xCoord + rand.nextDouble(), yCoord + rand.nextDouble(), zCoord + rand.nextDouble(), 0.0, 0.0, 0.0);
            }
        }

        if (rand.nextInt(10) == 0 && world.isSolid(xCoord, yCoord - 1, zCoord)) {
            double xOff = xCoord + rand.nextDouble();
            double yOff = yCoord - 1.05;
            double zOff = zCoord + rand.nextDouble();
            if (this.material == MaterialList.water) {
                world.spawnParticle(ParticleType.DRIPWATER, xOff, yOff, zOff, 0.0, 0.0, 0.0);
            } else {
                world.spawnParticle(ParticleType.DRIPLAVA, xOff, yOff, zOff, 0.0, 0.0, 0.0);
            }
        }

        if (world.getBlockMetadata(xCoord, yCoord, zCoord) == 0) {
            for (int i = 0; i < 10; i++) {
                byte id = (byte)world.getBlockId(xCoord, yCoord + 1, zCoord);
                double offX = xCoord + rand.nextDouble();
                double offY = yCoord + this.maxY;
                double offZ = zCoord + rand.nextDouble();
                if (id == BlockList.waterStill.id && world.getBlockMetadata(xCoord, yCoord + 1, zCoord) > 8) {
                    world.spawnParticle(ParticleType.FOAM, offX, offY, offZ, 0.0, 0.0, 0.0);
                }
            }
        }
    }

    public static double getFlowDirection(WorldSource world, int xCoord, int yCoord, int zCoord, Material material) {
        Vector flowVec = null;
        if (material == MaterialList.water) {
            flowVec = ((LiquidBlock)BlockList.waterMoving).getFlowVector(world, xCoord, yCoord, zCoord);
        }

        if (material == MaterialList.lava) {
            flowVec = ((LiquidBlock)BlockList.lavaMoving).getFlowVector(world, xCoord, yCoord, zCoord);
        }

        return flowVec.xCoord == 0.0 && flowVec.zCoord == 0.0 ? -1000.0 : Math.atan2(flowVec.zCoord, flowVec.xCoord) - (Math.PI / 2);
    }

    @Override
    public void onBlockAdded(World world, int xCoord, int yCoord, int zCoord) {
        if (!world.physicsDisabled()) {
            this.checkForHarden(world, xCoord, yCoord, zCoord);
        }
    }

    @Override
    public void onNeighborChange(World world, int xCoord, int yCoord, int zCoord, int i5) {
        if (!world.physicsDisabled()) {
            this.checkForHarden(world, xCoord, yCoord, zCoord);
        }
    }

    private void checkForHarden(World world, int xCoord, int yCoord, int zCoord) {
        if (world.getBlockId(xCoord, yCoord, zCoord) == this.id && this.material == MaterialList.lava) {
            boolean harden = false;
            if (harden || world.getBlockMaterial(xCoord, yCoord, zCoord - 1) == MaterialList.water) {
                harden = true;
            }

            if (harden || world.getBlockMaterial(xCoord, yCoord, zCoord + 1) == MaterialList.water) {
                harden = true;
            }

            if (harden || world.getBlockMaterial(xCoord - 1, yCoord, zCoord) == MaterialList.water) {
                harden = true;
            }

            if (harden || world.getBlockMaterial(xCoord + 1, yCoord, zCoord) == MaterialList.water) {
                harden = true;
            }

            if (harden || world.getBlockMaterial(xCoord, yCoord + 1, zCoord) == MaterialList.water) {
                harden = true;
            }

            if (harden) {
                int meta = world.getBlockMetadata(xCoord, yCoord, zCoord);
                if (meta == 0) {
                    world.setBlockWithNotify(xCoord, yCoord, zCoord, BlockList.obsidian.id);
                } else if (meta <= 4) {
                    world.setBlockWithNotify(xCoord, yCoord, zCoord, BlockList.cobblestone.id);
                }

                this.triggerLavaMixEffects(world, xCoord, yCoord, zCoord);
            }
        }
    }

    protected void triggerLavaMixEffects(World world, int xCoord, int yCoord, int zCoord) {
        world.playSoundEffect(xCoord + 0.5F, yCoord + 0.5F, zCoord + 0.5F, "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

        for (int i5 = 0; i5 < 8; i5++) {
            world.spawnParticle(ParticleType.LARGESMOKE, xCoord + Math.random(), yCoord + 1.2, zCoord + Math.random(), 0.0, 0.0, 0.0);
        }
    }

    @Override
    public void registerIcons(IconRegister ir) {
        if (this.material == MaterialList.lava) {
            this.icons = new Icon[]{ir.registerIcon("liquid/lava"), ir.registerIcon("liquid/lava_flow")};
        } else {
            this.icons = new Icon[]{ir.registerIcon("liquid/water"), ir.registerIcon("liquid/water_flow")};
        }
    }

    public static Icon getIcon_static(String name) {
        if ("water".equals(name)) {
            return ((LiquidBlock)BlockList.waterMoving).icons[0];
        } else if ("water_flow".equals(name)) {
            return ((LiquidBlock)BlockList.waterMoving).icons[1];
        } else if ("lava".equals(name)) {
            return ((LiquidBlock)BlockList.lavaMoving).icons[0];
        } else {
            return "lava_flow".equals(name) ? ((LiquidBlock)BlockList.lavaMoving).icons[1] : null;
        }
    }
}
