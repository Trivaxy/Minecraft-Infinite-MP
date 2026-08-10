package net.minecraft.client.render.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.options.OptionValues;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.special.BlockParticle;
import net.minecraft.client.particle.special.BlockParticle3D;
import net.minecraft.client.render.Lighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.texture.TextureLoader;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class ParticleRenderer {
    protected World world;
    private List<Particle>[] particles = new List[4];
    private TextureLoader renderer;
    private Random rand = new Random();

    public ParticleRenderer(World world, TextureLoader texLoader) {
        if (world != null) {
            this.world = world;
        }

        this.renderer = texLoader;

        for (int layer = 0; layer < 4; layer++) {
            this.particles[layer] = new ArrayList<>();
        }
    }

    public void addParticle(Particle particle) {
        int index = particle.layer();
        this.particles[index].add(particle);
    }

    public void updateParticles() {
        for (int layer = 0; layer < 4; layer++) {
            for (int index = 0; index < this.particles[layer].size(); index++) {
                Particle particle = this.particles[layer].get(index);
                particle.onTick();
                if (particle.isDead) {
                    this.particles[layer].remove(index--);
                }
            }
        }
    }

    public void clearParticles(World world) {
        this.world = world;

        for (int layer = 0; layer < 4; layer++) {
            this.particles[layer].clear();
        }
    }

    public void renderParticles(Entity entity, float deltaTick) {
        float yaw1 = MathHelper.cos(entity.yaw * (float) Math.PI / 180.0F);
        float yaw2 = MathHelper.sin(entity.yaw * (float) Math.PI / 180.0F);
        float pitch1 = -yaw2 * MathHelper.sin(entity.pitch * (float) Math.PI / 180.0F);
        float pitch2 = yaw1 * MathHelper.sin(entity.pitch * (float) Math.PI / 180.0F);
        float facing = MathHelper.cos(entity.pitch * (float) Math.PI / 180.0F);
        Particle.interpX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * deltaTick;
        Particle.interpY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * deltaTick;
        Particle.interpZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * deltaTick;
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);

        for (int type = 0; type < 4; type++) {
            if (this.particles[type].size() != 0) {
                int tex = 0;
                int index = 0;
                if (type == 0) {
                    tex = this.renderer.getTexture("/particles/particles.png");
                }

                if (type == 1) {
                    tex = this.renderer.getTexture("/terrain.png");
                }

                if (type == 2) {
                    tex = this.renderer.getTexture("/gui/items.png");
                }

                Tessellator tess = Tessellator.instance;
                if (type == 3) {
                    Lighting.enable();

                    for (int var14 = 0; var14 < this.particles[3].size(); var14++) {
                        Particle particle = this.particles[3].get(var14);
                        tess.setBrightness(particle.getBrightnessInt(deltaTick));
                        particle.renderParticle(tess, deltaTick, yaw1, facing, yaw2, pitch1, pitch2);
                    }

                    Lighting.disable();
                } else {
                    GL11.glBindTexture(3553, tex);
                    tess.begin();

                    for (int var13 = 0; var13 < this.particles[type].size(); var13++) {
                        Particle particle = this.particles[type].get(var13);
                        if (!particle.glows) {
                            tess.setBrightness(particle.getBrightnessInt(deltaTick));
                            particle.renderParticle(tess, deltaTick, yaw1, facing, yaw2, pitch1, pitch2);
                        }
                    }

                    tess.draw();
                }
            }
        }

        GL11.glDisable(3042);
    }

    public void renderLitParticles(Entity entity, float tick) {
        float yaw1 = MathHelper.cos(entity.yaw * (float) Math.PI / 180.0F);
        float yaw2 = MathHelper.sin(entity.yaw * (float) Math.PI / 180.0F);
        float pitch1 = -yaw2 * MathHelper.sin(entity.pitch * (float) Math.PI / 180.0F);
        float pitch2 = yaw1 * MathHelper.sin(entity.pitch * (float) Math.PI / 180.0F);
        float facing = MathHelper.cos(entity.pitch * (float) Math.PI / 180.0F);
        Particle.interpX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * tick;
        Particle.interpY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * tick;
        Particle.interpZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * tick;
        if (this.particles[0].size() != 0) {
            GL11.glBindTexture(3553, this.renderer.getTexture("/particles/particles.png"));
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            Tessellator tess = Tessellator.instance;
            tess.begin();
            tess.setBrightness(15728880);

            for (int index = 0; index < this.particles[0].size(); index++) {
                Particle particle = this.particles[0].get(index);
                if (particle.glows) {
                    particle.renderParticle(tess, tick, yaw1, facing, yaw2, pitch1, pitch2);
                }
            }

            tess.draw();
            GL11.glDisable(3042);
        }
    }

    public void spawnBreakBlockParticles(int x, int y, int z, int id, int meta) {
        if (id != 0) {
            Block block = BlockList.blocks[id];
            byte offset = 4;
            if (block == null) {
                return;
            }

            for (int offsetX = 0; offsetX < offset; offsetX++) {
                for (int offsetY = 0; offsetY < offset; offsetY++) {
                    for (int offsetZ = 0; offsetZ < offset; offsetZ++) {
                        double motionX = x + (offsetX + 0.5) / offset;
                        double motionY = y + (offsetY + 0.5) / offset;
                        double motionZ = z + (offsetZ + 0.5) / offset;
                        if (OptionValues.fancyParticles && block.particles3D()) {
                            this.addParticle(
                                new BlockParticle3D(
                                    this.world, motionX, motionY, motionZ, motionX - x - 0.5, motionY - y - 0.5, motionZ - z - 0.5, block, meta, 2
                                )
                            );
                        } else {
                            this.addParticle(
                                new BlockParticle(
                                    this.world, motionX, motionY, motionZ, motionX - x - 0.5, motionY - y - 0.5, motionZ - z - 0.5, block, meta, 2
                                )
                            );
                        }
                    }
                }
            }
        }
    }

    public void spawnBlockHitParticles(int x, int y, int z, int face) {
        int id = this.world.getBlockId(x, y, z);
        if (id != 0) {
            Block block = BlockList.blocks[id];
            float offset = 0.1F;
            double offsetX = x + this.rand.nextDouble() * (block.maxX - block.minX - offset * 2.0F) + offset + block.minX;
            double offsetY = y + this.rand.nextDouble() * (block.maxY - block.minY - offset * 2.0F) + offset + block.minY;
            double offsetZ = z + this.rand.nextDouble() * (block.maxZ - block.minZ - offset * 2.0F) + offset + block.minZ;
            if (face == 0) {
                offsetY = y + block.minY - offset;
            }

            if (face == 1) {
                offsetY = y + block.maxY + offset;
            }

            if (face == 2) {
                offsetZ = z + block.minZ - offset;
            }

            if (face == 3) {
                offsetZ = z + block.maxZ + offset;
            }

            if (face == 4) {
                offsetX = x + block.minX - offset;
            }

            if (face == 5) {
                offsetX = x + block.maxX + offset;
            }

            if (OptionValues.fancyParticles && block.particles3D()) {
                this.addParticle(
                    new BlockParticle3D(this.world, offsetX, offsetY, offsetZ, 0.0, 0.0, 0.0, block, this.world.getBlockMetadata(x, y, z), face)
                        .multiplyVelocity(0.2F)
                        .scaleParticle(0.6F)
                );
            } else {
                this.addParticle(
                    new BlockParticle(this.world, offsetX, offsetY, offsetZ, 0.0, 0.0, 0.0, block, this.world.getBlockMetadata(x, y, z), face)
                        .multiplyVelocity(0.2F)
                        .scaleParticle(0.6F)
                );
            }
        }
    }

    public String getTotalCount() {
        return "" + (this.particles[0].size() + this.particles[1].size() + this.particles[2].size() + this.particles[3].size());
    }
}
