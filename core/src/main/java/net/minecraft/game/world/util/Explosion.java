package net.minecraft.game.world.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.misc.LingeringCloud;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.entity.projectile.Windball;
import net.minecraft.game.item.potion.EffectData;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.world.World;
import net.minecraft.game.world.chunk.BlockPosition;
import net.minecraft.statistics.achievement.AchievementList;
import net.minecraft.util.math.MathHelper;

public class Explosion {
    private Random rng = new Random();
    private World world;
    public Entity exploder;
    public EffectData effect;
    public Set<BlockPosition> destroyedBlocks = new HashSet<>();
    public double x;
    public double y;
    public double z;
    public int damage;
    public float blastRadius;
    public boolean destroyBlocks = true;
    public boolean isMagic = false;
    public boolean isWind = false;
    public boolean isFlaming = false;
    public boolean isFrost = false;

    public Explosion(World world, Entity exploder, double x, double y, double z, float rad) {
        this.world = world;
        this.exploder = exploder;
        this.blastRadius = rad;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void explode() {
        float radius = this.blastRadius;
        byte max = 16;

        for (int x = 0; x < max; x++) {
            for (int y = 0; y < max; y++) {
                for (int z = 0; z < max; z++) {
                    if (x == 0 || x == max - 1 || y == 0 || y == max - 1 || z == 0 || z == max - 1) {
                        double distX = x / (max - 1.0F) * 2.0F - 1.0F;
                        double distY = y / (max - 1.0F) * 2.0F - 1.0F;
                        double distZ = z / (max - 1.0F) * 2.0F - 1.0F;
                        double dist = Math.sqrt(distX * distX + distY * distY + distZ * distZ);
                        distX /= dist;
                        distY /= dist;
                        distZ /= dist;
                        float totalRad = this.blastRadius * (0.7F + this.world.rand.nextFloat() * 0.6F);
                        double explosionX = this.x;
                        double explosionY = this.y;
                        double explosionZ = this.z;

                        for (float rads = 0.3F; totalRad > 0.0F; totalRad -= rads * 0.75F) {
                            int posX = MathHelper.floor_double(explosionX);
                            int posY = MathHelper.floor_double(explosionY);
                            int posZ = MathHelper.floor_double(explosionZ);
                            int id = this.world.getBlockId(posX, posY, posZ);
                            if (id > 0) {
                                totalRad -= (BlockList.blocks[id].getExplosionResistance(this.exploder, this.world.getBlockMetadata(posX, posY, posZ)) + 0.3F)
                                    * rads;
                            }

                            if (totalRad > 0.0F) {
                                this.destroyedBlocks.add(new BlockPosition(posX, posY, posZ));
                            }

                            explosionX += distX * rads;
                            explosionY += distY * rads;
                            explosionZ += distZ * rads;
                        }
                    }
                }
            }
        }

        this.blastRadius *= 2.0F;
        int left = MathHelper.floor_double(this.x - this.blastRadius - 1.0);
        int right = MathHelper.floor_double(this.x + this.blastRadius + 1.0);
        int below = MathHelper.floor_double(this.y - this.blastRadius - 1.0);
        int above = MathHelper.floor_double(this.y + this.blastRadius + 1.0);
        int back = MathHelper.floor_double(this.z - this.blastRadius - 1.0);
        int front = MathHelper.floor_double(this.z + this.blastRadius + 1.0);
        List<Entity> entities = this.world.getEntities(this.exploder, BoundingBox.getBoundingBox(left, below, back, right, above, front));
        Vector pos = Vector.createVector(this.x, this.y, this.z);

        for (int index = 0; index < entities.size(); index++) {
            Entity entity = entities.get(index);
            double dist = entity.getDistance(this.x, this.y, this.z) / this.blastRadius;
            if (dist <= 1.0) {
                double distX = entity.posX - this.x;
                double distY = entity.posY - this.y;
                double distZ = entity.posZ - this.z;
                double distTotal = MathHelper.sqrt_double(distX * distX + distY * distY + distZ * distZ);
                distX /= distTotal;
                distY /= distTotal;
                distZ /= distTotal;
                double density = this.getBlockDensity(pos, entity.boundingBox);
                if (this.isWind || this.isMagic) {
                    density = 1.0;
                }

                double force = (1.0 - dist) * density;
                if (this.isWind) {
                    force *= 1.5;
                }

                if (this.isMagic && this.effect == null || !this.isMagic) {
                    if (!this.isWind) {
                        entity.damageEntity(DamageSource.explosion, (int)((force * force + force) / 2.0 * 8.0 * this.blastRadius + 1.0));
                    }

                    if (this.isFrost) {
                        entity.freeze = 100;
                    } else {
                        entity.motionX += distX * force;
                        entity.motionY += distY * force;
                        entity.motionZ += distZ * force;
                    }

                    if (entity instanceof Player && this.isWind && (this.exploder == null || this.exploder instanceof Windball)) {
                        ((Player)entity).triggerAchievement(AchievementList.wind);
                    }
                }
            }
        }

        this.blastRadius = radius;
        ArrayList<BlockPosition> blocks = new ArrayList<>();
        blocks.addAll(this.destroyedBlocks);
    }

    public void addParticles(boolean flag) {
        if (!this.isMagic && !this.isWind && !this.isFrost) {
            this.world
                .playSoundEffect(
                    this.x, this.y, this.z, "random.explode", 4.0F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F
                );
        }

        if (this.isWind || this.isFrost) {
            this.world.spawnParticle(ParticleType.HUGEEXPLODE, this.x, this.y, this.z, 0.0, 0.0, 0.0);
        }

        ArrayList<BlockPosition> blocks = new ArrayList<>();
        blocks.addAll(this.destroyedBlocks);

        for (int index = blocks.size() - 1; index >= 0; index--) {
            BlockPosition pos = blocks.get(index);
            int blockX = pos.x;
            int blockY = pos.y;
            int blockZ = pos.z;
            int id = this.world.getBlockId(blockX, blockY, blockZ);
            if (flag) {
                double offX = blockX + this.world.rand.nextFloat();
                double offY = blockY + this.world.rand.nextFloat();
                double offZ = blockZ + this.world.rand.nextFloat();
                double distX = offX - this.x;
                double distY = offY - this.y;
                double distZ = offZ - this.z;
                double dist = MathHelper.sqrt_double(distX * distX + distY * distY + distZ * distZ);
                distX /= dist;
                distY /= dist;
                distZ /= dist;
                double rad = 0.5 / (dist / this.blastRadius + 0.1);
                rad *= this.world.rand.nextFloat() * this.world.rand.nextFloat() + 0.3F;
                distX *= rad;
                distY *= rad;
                distZ *= rad;
                if (!this.isMagic && !this.isWind && !this.isFrost) {
                    this.world
                        .spawnParticle(
                            ParticleType.EXPLODE, (offX + this.x * 1.0) / 2.0, (offY + this.y * 1.0) / 2.0, (offZ + this.z * 1.0) / 2.0, distX, distY, distZ
                        );
                }

                if (this.effect == null && !this.isWind) {
                    this.world.spawnParticle(ParticleType.SMOKE, offX, offY, offZ, distX, distY, distZ);
                }
            }

            if (id > 0 && this.world.settings.explosions && this.destroyBlocks) {
                if (!this.isMagic) {
                    BlockList.blocks[id].dropBlock(this.world, blockX, blockY, blockZ, this.world.getBlockMetadata(blockX, blockY, blockZ));
                    if (!this.world.getBlockMaterial(blockX, blockY, blockZ).getIsLiquid()
                        && this.world.getBlock(blockX, blockY, blockZ) != BlockList.playerGrave) {
                        this.world.setBlockWithNotify(blockX, blockY, blockZ, 0);
                    }

                    if (this.isFlaming) {
                        int space = this.world.getBlockId(blockX, blockY, blockZ);
                        int below = this.world.getBlockId(blockX, blockY - 1, blockZ);
                        if (space == 0 && Block.isOpaque.get(below) && this.rng.nextInt(3) == 0) {
                            this.world.setBlockWithNotify(blockX, blockY, blockZ, BlockList.fire.id);
                        }
                    }

                    BlockList.blocks[id].onExplode(this.world, blockX, blockY, blockZ);
                } else {
                    BoundingBox bb = new BoundingBox(blockX, blockY + 1, blockZ, blockX + 1, blockY + 2, blockZ + 1);
                    if (this.world.getBlockId(blockX, blockY, blockZ) != 0
                        && this.world.isSolid(blockX, blockY, blockZ)
                        && this.world.getEntitiesOfType(LingeringCloud.class, bb).isEmpty()) {
                        this.world.spawnEntity(new LingeringCloud(this.world, blockX, blockY + 1, blockZ, this.effect, this.damage));
                    }
                }
            }
        }
    }

    public float getBlockDensity(Vector vec, BoundingBox aabb) {
        double bbx = 1.0 / ((aabb.maxX - aabb.minX) * 2.0 + 1.0);
        double bby = 1.0 / ((aabb.maxY - aabb.minY) * 2.0 + 1.0);
        double bbz = 1.0 / ((aabb.maxZ - aabb.minZ) * 2.0 + 1.0);
        int iterations = 0;
        int size = 0;

        for (float x = 0.0F; x <= 1.0F; x = (float)(x + bbx)) {
            for (float y = 0.0F; y <= 1.0F; y = (float)(y + bby)) {
                for (float z = 0.0F; z <= 1.0F; z = (float)(z + bbz)) {
                    double distX = aabb.minX + (aabb.maxX - aabb.minX) * x;
                    double distY = aabb.minY + (aabb.maxY - aabb.minY) * y;
                    double distZ = aabb.minZ + (aabb.maxZ - aabb.minZ) * z;
                    if (this.world.rayTraceBlocks(Vector.createVector(distX, distY, distZ), vec) == null) {
                        iterations++;
                    }

                    size++;
                }
            }
        }

        return (float)iterations / size;
    }
}
