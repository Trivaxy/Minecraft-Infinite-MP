package net.minecraft.game.entity.minecart;

import com.mojang.nbt.tag.special.CompoundTag;
import java.util.List;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.misc.MinecartTrack;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class Minecart extends Entity {
    private static final int[][][] matrix = new int[][][]{
        {{0, 0, -1}, {0, 0, 1}},
        {{-1, 0, 0}, {1, 0, 0}},
        {{-1, -1, 0}, {1, 0, 0}},
        {{-1, 0, 0}, {1, -1, 0}},
        {{0, 0, -1}, {0, -1, 1}},
        {{0, -1, -1}, {0, 0, 1}},
        {{0, 0, 1}, {1, 0, 0}},
        {{0, 0, 1}, {-1, 0, 0}},
        {{0, 0, -1}, {-1, 0, 0}},
        {{0, 0, -1}, {1, 0, 0}}
    };
    private boolean reverse;
    public int damageTaken;
    public int hitTime;
    public int direction;
    public int type;
    protected int drop;
    private int increments;
    private double posXMP;
    private double posYMP;
    private double posZMP;
    private double yawMP;
    private double pitchMP;

    public Minecart(World world) {
        super(world);
        this.setSize(0.98F, 0.7F);
        this.yOffset = this.height / 2.0F;
        this.damageTaken = 0;
        this.hitTime = 0;
        this.direction = 1;
        this.reverse = false;
        this.preventSpawning = true;
        this.produceFootsteps = false;
        this.drop = ItemList.minecart.id;
    }

    public Minecart(World world, double x, double y, double z) {
        this(world);
        this.setPosition(x, y + this.yOffset, z);
        this.motionX = 0.0;
        this.motionY = 0.0;
        this.motionZ = 0.0;
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
        this.produceFootsteps = false;
    }

    @Override
    protected void init() {
    }

    @Override
    public void teleportInServer(double x, double y, double z, float yaw, float pitch, int increments) {
        this.posXMP = x;
        this.posYMP = y;
        this.posZMP = z;
        this.yawMP = yaw;
        this.pitchMP = pitch;
        this.increments = increments + 2;
    }

    @Override
    public void setVelocity(double x, double y, double z) {
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
    }

    @Override
    public BoundingBox getCollisionBox(Entity entity) {
        return entity.boundingBox;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public double getYOffset() {
        return -0.25;
    }

    @Override
    public boolean damageEntity(DamageSource src, int amount) {
        if (!this.world.isOnline && !this.isDead) {
            this.direction = -this.direction;
            this.hitTime = 10;
            this.damageTaken += amount * 10;
            if (this.damageTaken > 40 && !this.isDead) {
                this.dropItem(this.drop, 1);
                this.setDead();
            }
        }

        return true;
    }

    @Override
    public boolean isSelectable() {
        return !this.isDead;
    }

    @Override
    public void setDead() {
        if (this.rider != null) {
            this.rider.mountEntity(null);
        }

        super.setDead();
    }

    @Override
    public void onTick() {
        if (this.hitTime > 0) {
            this.hitTime--;
        }

        if (this.damageTaken > 0) {
            this.damageTaken--;
        }

        if (!this.world.isOnline || this.increments <= 0) {
            this.prevX = this.posX;
            this.prevY = this.posY;
            this.prevZ = this.posZ;
            this.motionY -= 0.04F;
            int x = MathHelper.floor_double(this.posX);
            int y = MathHelper.floor_double(this.posY);
            int z = MathHelper.floor_double(this.posZ);
            double maxOffset = 0.8;
            if (this.world.getBlock(x, y - 1, z) instanceof MinecartTrack) {
                y--;
            }

            if (this.world.getBlock(x, y, z) instanceof MinecartTrack) {
                this.moveCartOnTrack(x, y, z, maxOffset);
            } else {
                this.moveCart(maxOffset);
            }

            int minX = MathHelper.floor_double(this.boundingBox.minX - 0.999);
            int minY = MathHelper.floor_double(this.boundingBox.minY - 0.999);
            int minZ = MathHelper.floor_double(this.boundingBox.minZ - 0.999);
            int maxX = MathHelper.floor_double(this.boundingBox.maxX + 0.999);
            int maxY = MathHelper.floor_double(this.boundingBox.maxY + 0.999);
            int maxZ = MathHelper.floor_double(this.boundingBox.maxZ + 0.999);

            for (int posX = minX; posX <= maxX; posX++) {
                for (int posY = minY; posY <= maxY; posY++) {
                    for (int posZ = minZ; posZ <= maxZ; posZ++) {
                        int id = this.world.getBlockId(posX, posY, posZ);
                        if (id > 0) {
                            BlockList.blocks[id].onEntityCollision(this, this.world, posX, posY, posZ);
                        }
                    }
                }
            }

            this.pitch = 0.0F;
            double distX = this.prevX - this.posX;
            double distZ = this.prevZ - this.posZ;
            double distTotal = distX * distX + distZ * distZ;
            if (distTotal > 0.001) {
                this.yaw = (float)(Math.atan2(distZ, distX) * 180.0 / Math.PI);
                if (this.reverse) {
                    this.yaw += 180.0F;
                }
            }

            double newYaw = MathHelper.computeAngle(this.yaw - this.prevYaw);
            if (newYaw < -170.0 || newYaw >= 170.0) {
                this.yaw += 180.0F;
                this.reverse = !this.reverse;
            }

            this.setRotation(this.yaw, this.pitch);
            List<Entity> entities = this.world.getEntities(this, this.boundingBox.expand(0.2F, 0.0, 0.2F));
            if (entities != null && entities.size() > 0) {
                for (int var25 = 0; var25 < entities.size(); var25++) {
                    Entity entity = entities.get(var25);
                    if (entity != this.rider && entity.isPushable() && entity instanceof Minecart) {
                        entity.applyCollision(this);
                    }
                }
            }

            if (this.rider != null && this.rider.isDead) {
                this.rider = null;
            }

            if (this.world.settings.border > 0) {
                if (this.posX > this.world.settings.border - 1) {
                    this.motionX--;
                }

                if (this.posZ > this.world.settings.border - 1) {
                    this.motionZ--;
                }

                if (this.posX <= -this.world.settings.border + 1) {
                    this.motionX++;
                }

                if (this.posZ <= -this.world.settings.border + 1) {
                    this.motionZ++;
                }
            }
        } else if (this.increments > 0) {
            double x = this.posX + (this.posXMP - this.posX) / this.increments;
            double y = this.posY + (this.posYMP - this.posY) / this.increments;
            double z = this.posZ + (this.posZMP - this.posZ) / this.increments;
            double yaw = MathHelper.computeAngle((float)this.yawMP - this.yaw);
            this.yaw = (float)(this.yaw + yaw / this.increments);
            this.pitch = (float)(this.pitch + (this.pitchMP - this.pitch) / this.increments);
            this.increments--;
            this.setPosition(x, y, z);
            this.setRotation(this.yaw, this.pitch);
        } else {
            this.setPosition(this.posX, this.posY, this.posZ);
            this.setRotation(this.yaw, this.pitch);
        }
    }

    protected void moveCart(double maxOffset) {
        if (this.motionX < -maxOffset) {
            this.motionX = -maxOffset;
        }

        if (this.motionX > maxOffset) {
            this.motionX = maxOffset;
        }

        if (this.motionZ < -maxOffset) {
            this.motionZ = -maxOffset;
        }

        if (this.motionZ > maxOffset) {
            this.motionZ = maxOffset;
        }

        if (this.onGround) {
            this.motionX *= 0.5;
            this.motionY *= 0.5;
            this.motionZ *= 0.5;
        }

        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        if (!this.onGround) {
            this.motionX *= 0.95F;
            this.motionY *= 0.95F;
            this.motionZ *= 0.95F;
        }
    }

    protected void moveCartOnTrack(int x, int y, int z, double maxOffset) {
        double offset = 0.0078125;
        Vector lastPos = this.getPos(this.posX, this.posY, this.posZ);
        int meta = this.world.getBlockMetadata(x, y, z);
        int id = this.world.getBlockId(x, y, z);
        this.posY = y;
        if (meta >= 2 && meta <= 5) {
            this.posY = y + 1;
        }

        if (meta == 2) {
            this.motionX -= offset;
        }

        if (meta == 3) {
            this.motionX += offset;
        }

        if (meta == 4) {
            this.motionZ += offset;
        }

        if (meta == 5) {
            this.motionZ -= offset;
        }

        int[][] directions = matrix[meta];
        double dirX = directions[1][0] - directions[0][0];
        double dirZ = directions[1][2] - directions[0][2];
        double dist = Math.sqrt(dirX * dirX + dirZ * dirZ);
        double currSpeed = this.motionX * dirX + this.motionZ * dirZ;
        if (currSpeed < 0.0) {
            dirX = -dirX;
            dirZ = -dirZ;
        }

        double motion = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
        this.motionX = motion * dirX / dist;
        this.motionZ = motion * dirZ / dist;
        if (this.rider != null) {
            double max = this.rider.motionX * this.rider.motionX + this.rider.motionZ * this.rider.motionZ;
            double min = this.motionX * this.motionX + this.motionZ * this.motionZ;
            if (max > 1.0E-4 && min < 0.01) {
                this.motionX = this.motionX + this.rider.motionX * 0.1;
                this.motionZ = this.motionZ + this.rider.motionZ * 0.1;
            }
        }

        double directionalMovement = 0.0;
        double momentumX = x + 0.5 + directions[0][0] * 0.5;
        double momentumZ = z + 0.5 + directions[0][2] * 0.5;
        double momentumX2 = x + 0.5 + directions[1][0] * 0.5;
        double momentumZ2 = z + 0.5 + directions[1][2] * 0.5;
        dirX = momentumX2 - momentumX;
        dirZ = momentumZ2 - momentumZ;
        if (dirX == 0.0) {
            this.posX = x + 0.5;
            directionalMovement = this.posZ - z;
        } else if (dirZ == 0.0) {
            this.posZ = z + 0.5;
            directionalMovement = this.posX - x;
        } else {
            double distX = this.posX - momentumX;
            double distZ = this.posZ - momentumZ;
            directionalMovement = (distX * dirX + distZ * dirZ) * 2.0;
        }

        this.posX = momentumX + dirX * directionalMovement;
        this.posZ = momentumZ + dirZ * directionalMovement;
        this.setPosition(this.posX, this.posY + this.yOffset, this.posZ);
        double distX = this.motionX;
        double distZ = this.motionZ;
        if (this.rider != null) {
            distX *= 0.75;
            distZ *= 0.75;
        }

        if (distX < -maxOffset) {
            distX = -maxOffset;
        }

        if (distX > maxOffset) {
            distX = maxOffset;
        }

        if (distZ < -maxOffset) {
            distZ = -maxOffset;
        }

        if (distZ > maxOffset) {
            distZ = maxOffset;
        }

        this.moveEntity(distX, 0.0, distZ);
        if (directions[0][1] != 0 && MathHelper.floor_double(this.posX) - x == directions[0][0] && MathHelper.floor_double(this.posZ) - z == directions[0][2]) {
            this.setPosition(this.posX, this.posY + directions[0][1], this.posZ);
        } else if (directions[1][1] != 0
            && MathHelper.floor_double(this.posX) - x == directions[1][0]
            && MathHelper.floor_double(this.posZ) - z == directions[1][2]) {
            this.setPosition(this.posX, this.posY + directions[1][1], this.posZ);
        }

        if (id == BlockList.railBooster.id) {
            this.motionX *= 1.1;
            this.motionY *= 0.0;
            this.motionZ *= 1.1;
        } else if (id == BlockList.railStopper.id) {
            this.motionX *= 0.5;
            this.motionY *= 0.0;
            this.motionZ *= 0.5;
        } else {
            this.motionX *= 0.997F;
            this.motionY *= 0.0;
            this.motionZ *= 0.997F;
        }

        if (this.motionX > maxOffset) {
            this.motionX = maxOffset;
        }

        if (this.motionZ > maxOffset) {
            this.motionZ = maxOffset;
        }

        if (this.motionX < -maxOffset) {
            this.motionX = -maxOffset;
        }

        if (this.motionZ < -maxOffset) {
            this.motionZ = -maxOffset;
        }

        if (this.type == 2 && this.rider == null) {
            this.pushCart();
        }

        Vector currPos = this.getPos(this.posX, this.posY, this.posZ);
        if (currPos != null && lastPos != null) {
            double distTravelled = (lastPos.yCoord - currPos.yCoord) * 0.05;
            motion = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            if (motion > 0.0) {
                this.motionX = this.motionX / motion * (motion + distTravelled);
                this.motionZ = this.motionZ / motion * (motion + distTravelled);
            }

            this.setPosition(this.posX, currPos.yCoord, this.posZ);
        }

        int posX = MathHelper.floor_double(this.posX);
        int posZ = MathHelper.floor_double(this.posZ);
        if (posX != x || posZ != z) {
            motion = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.motionX = motion * (posX - x);
            this.motionZ = motion * (posZ - z);
        }
    }

    protected void pushCart() {
    }

    public Vector getPosOffset(double x, double y, double z, double offset) {
        int posX = MathHelper.floor_double(x);
        int posY = MathHelper.floor_double(y);
        int posZ = MathHelper.floor_double(z);
        if (this.world.getBlock(posX, posY - 1, posZ) instanceof MinecartTrack) {
            posY--;
        }

        if (this.world.getBlock(posX, posY, posZ) instanceof MinecartTrack) {
            int meta = this.world.getBlockMetadata(posX, posY, posZ);
            y = posY;
            if (meta >= 2 && meta <= 5) {
                y = posY + 1;
            }

            int[][] directions = matrix[meta];
            double dirX = directions[1][0] - directions[0][0];
            double dirZ = directions[1][2] - directions[0][2];
            double distTravelled = Math.sqrt(dirX * dirX + dirZ * dirZ);
            dirX /= distTravelled;
            dirZ /= distTravelled;
            x += dirX * offset;
            z += dirZ * offset;
            if (directions[0][1] != 0 && MathHelper.floor_double(x) - posX == directions[0][0] && MathHelper.floor_double(z) - posZ == directions[0][2]) {
                y += directions[0][1];
            } else if (directions[1][1] != 0 && MathHelper.floor_double(x) - posX == directions[1][0] && MathHelper.floor_double(z) - posZ == directions[1][2]) {
                y += directions[1][1];
            }

            return this.getPos(x, y, z);
        } else {
            return null;
        }
    }

    public Vector getPos(double x, double y, double z) {
        int posX = MathHelper.floor_double(x);
        int posY = MathHelper.floor_double(y);
        int posZ = MathHelper.floor_double(z);
        if (this.world.getBlock(posX, posY - 1, posZ) instanceof MinecartTrack) {
            posY--;
        }

        if (this.world.getBlock(posX, posY, posZ) instanceof MinecartTrack) {
            int meta = this.world.getBlockMetadata(posX, posY, posZ);
            y = posY;
            if (meta >= 2 && meta <= 5) {
                y = posY + 1;
            }

            int[][] directions = matrix[meta];
            double dist = 0.0;
            double startX = posX + 0.5 + directions[0][0] * 0.5;
            double startY = posY + 0.5 + directions[0][1] * 0.5;
            double startZ = posZ + 0.5 + directions[0][2] * 0.5;
            double endX = posX + 0.5 + directions[1][0] * 0.5;
            double endY = posY + 0.5 + directions[1][1] * 0.5;
            double endZ = posZ + 0.5 + directions[1][2] * 0.5;
            double distX = endX - startX;
            double distY = (endY - startY) * 2.0;
            double distZ = endZ - startZ;
            if (distX == 0.0) {
                x = posX + 0.5;
                dist = z - posZ;
            } else if (distZ == 0.0) {
                z = posZ + 0.5;
                dist = x - posX;
            } else {
                double travelX = x - startX;
                double travelZ = z - startZ;
                dist = (travelX * distX + travelZ * distZ) * 2.0;
            }

            x = startX + distX * dist;
            y = startY + distY * dist;
            z = startZ + distZ * dist;
            if (distY < 0.0) {
                y++;
            }

            if (distY > 0.0) {
                y += 0.5;
            }

            return Vector.createVector(x, y, z);
        } else {
            return null;
        }
    }

    @Override
    protected void writeToNBT(CompoundTag tag) {
        tag.setInteger("Type", this.type);
    }

    @Override
    protected void readFromNBT(CompoundTag tag) {
        this.type = tag.getInteger("Type");
    }

    @Override
    public void applyCollision(Entity entity) {
        if (!this.world.isOnline && entity != this.rider) {
            double distX = entity.posX - this.posX;
            double distZ = entity.posZ - this.posZ;
            double dist = distX * distX + distZ * distZ;
            if (dist >= 1.0E-4F) {
                dist = MathHelper.sqrt_double(dist);
                distX /= dist;
                distZ /= dist;
                double distOff = 1.0 / dist;
                if (distOff > 1.0) {
                    distOff = 1.0;
                }

                distX *= distOff;
                distZ *= distOff;
                distX *= 0.1F;
                distZ *= 0.1F;
                distX *= 1.0;
                distZ *= 1.0;
                distX *= 0.5;
                distZ *= 0.5;
                if (entity instanceof Minecart) {
                    double travelX = entity.posX - this.posX;
                    double travelZ = entity.posZ - this.posZ;
                    Vector currPos = Vector.createVector(travelX, 0.0, travelZ).normalize();
                    Vector travelDir = Vector.createVector(
                            MathHelper.cos(this.yaw * (float) Math.PI / 180.0F), 0.0, MathHelper.sin(this.yaw * (float) Math.PI / 180.0F)
                        )
                        .normalize();
                    double distMax = Math.abs(currPos.dotProduct(travelDir));
                    if (distMax < 0.8F) {
                        return;
                    }

                    double motionX = entity.motionX + this.motionX;
                    double motionZ = entity.motionZ + this.motionZ;
                    this.collide((Minecart)entity, motionX, motionZ, distX, distZ);
                } else {
                    this.addVelocity(-distX, 0.0, -distZ);
                    entity.addVelocity(distX / 4.0, 0.0, distZ / 4.0);
                }
            }
        }
    }

    protected void collide(Minecart entity, double motionX, double motionZ, double distX, double distZ) {
        if (entity.type == 2) {
            motionX /= 2.0;
            motionZ /= 2.0;
            this.motionX *= 0.2F;
            this.motionZ *= 0.2F;
            this.addVelocity(motionX - distX, 0.0, motionZ - distZ);
            entity.motionX *= 0.2F;
            entity.motionZ *= 0.2F;
            entity.addVelocity(motionX + distX, 0.0, motionZ + distZ);
        } else {
            motionX /= 2.0;
            motionZ /= 2.0;
            this.motionX *= 0.2F;
            this.motionZ *= 0.2F;
            this.addVelocity(motionX - distX, 0.0, motionZ - distZ);
            entity.motionX *= 0.2F;
            entity.motionZ *= 0.2F;
            entity.addVelocity(motionX + distX, 0.0, motionZ + distZ);
        }
    }

    @Override
    public boolean interact(Player player) {
        if (this.rider != null && this.rider instanceof Player && this.rider != player) {
            return false;
        }

        if (!this.world.isOnline) {
            player.mountEntity(this);
        }

        return true;
    }

    @Override
    public void hurtAnim() {
        this.direction = -this.direction;
        this.hitTime = 10;
        this.damageTaken = this.damageTaken + this.damageTaken * 10;
    }
}
