package net.minecraft.game.entity.misc;

import com.mojang.nbt.tag.special.CompoundTag;
import java.util.List;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.block.misc.Portal;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.block.tileentity.container.CrafterEntity;
import net.minecraft.game.block.tileentity.container.VacuumEntity;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.animal.flying.Vulture;
import net.minecraft.game.entity.mob.animal.tameable.Fox;
import net.minecraft.game.entity.mob.monster.Pigman;
import net.minecraft.game.entity.mob.monster.boss.Priest;
import net.minecraft.game.entity.mob.monster.slime.AquaCube;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.block.BlockItem;
import net.minecraft.game.item.crafting.special.FurnaceRecipes;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;
import net.minecraft.statistics.achievement.AchievementList;
import net.minecraft.util.math.MathHelper;

public class DroppedItem extends Entity {
    public ItemStack item;
    public int age = 0;
    public int pickupDelay;
    private int health = 5;
    public float hoverStart = (float)(Math.random() * Math.PI * 2.0);

    public DroppedItem(World world, double xCoord, double yCoord, double zCoord, ItemStack itemStack) {
        super(world);
        this.setSize(0.25F, 0.25F);
        this.setPosition(xCoord, yCoord, zCoord);
        this.pickupDelay = 10;
        this.yOffset = this.height / 2.0F;
        this.item = itemStack;
        this.yaw = (float)(Math.random() * 360.0);
        this.motionX = Math.random() * 0.2 - 0.1;
        this.motionY = 0.2;
        this.motionZ = Math.random() * 0.2 - 0.1;
        this.produceFootsteps = false;
    }

    public DroppedItem(World world) {
        super(world);
        this.setSize(0.25F, 0.25F);
        this.yOffset = this.height / 2.0F;
    }

    @Override
    public boolean canBurn() {
        return this.item.getItem().canBurn();
    }

    @Override
    protected void init() {
    }

    @Override
    public void onTick() {
        super.onTick();
        if (this.pickupDelay > 0) {
            this.pickupDelay--;
        }

        this.prevX = this.posX;
        this.prevY = this.posY;
        this.prevZ = this.posZ;
        if (this.world.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))
                == BlockList.waterStill.id
            && this.world.getBlockMetadata(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) == 0) {
            this.motionY += 0.004F;
        } else {
            this.motionY -= 0.04F;
        }

        if (this.world.getBlockMaterial(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))
            == MaterialList.lava) {
            this.motionY = 0.2F;
            this.motionX = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F;
            this.motionZ = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F;
            this.world.playEntitySound(this, "random.fizz", 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
        }

        this.pushByFan();
        this.pushOutOfBlocks(this.posX, this.posY, this.posZ);
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.98F;
        this.motionY *= 0.98F;
        this.motionZ *= 0.98F;
        if (this.onGround) {
            this.motionX *= 0.7F;
            this.motionZ *= 0.7F;
            this.motionY *= -0.5;
        }

        this.age++;
        if (this.age >= 6000) {
            this.setDead();
        }

        if (this.isInVacuum() && !this.world.isOnline) {
            TileEntity vacuum = this.world
                .getTileEntity(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY - 1.0), MathHelper.floor_double(this.posZ));
            int block = this.world.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY - 1.0), MathHelper.floor_double(this.posZ));
            if (block == BlockList.vacuum.id && vacuum != null && vacuum instanceof VacuumEntity && ((VacuumEntity)vacuum).storeStack(this.item)) {
                this.setDead();
            }

            if (block == BlockList.crafter.id && vacuum != null && vacuum instanceof CrafterEntity && ((CrafterEntity)vacuum).storeStack(this.item)) {
                this.setDead();
            }
        }

        if (this.isInHellfire(this.boundingBox, FurnaceRecipes.smelting().getSmeltingResult(this.item.itemID, this.item.itemDmg) != null)
            && !this.world.isOnline) {
            if (this.world.getClosestPlayer(this, 16.0) != null) {
                this.world.getClosestPlayer(this, 16.0).triggerAchievement(AchievementList.indev);
            }

            this.convertHellfireItem();
        }

        if (this.item.itemID > 0 && this.item.getItem() != null) {
            if (this.item.getItem() == ItemList.singularity) {
                this.activatePortal();
            }
        } else {
            this.setDead();
        }

        Player closestPlayer = this.world.getClosestPlayer(this.posX, this.posY, this.posZ, 8.0);
        if (closestPlayer != null
            && closestPlayer.inventory.accessorySlots[0] != null
            && closestPlayer.inventory.accessorySlots[0].getItem() == ItemList.magnet) {
            this.onCollideWith(closestPlayer);
        }

        if (this.health > 0) {
            List<Entity> entities = this.world.getEntities(this, this.boundingBox.expand(1.0, 0.0, 1.0));
            if (entities != null) {
                for (int entity = 0; entity < entities.size(); entity++) {
                    entities.get(entity).onCollideWith(this);
                }
            }
        }
    }

    private void pushByFan() {
        for (int x = -5; x < 6; x++) {
            for (int z = -5; z < 6; z++) {
                int id = this.world.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ) + z);
                int id2 = this.world.getBlockId(MathHelper.floor_double(this.posX) + x, MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
                int movement = 1;
                if (id == BlockList.fan.id) {
                    switch (this.world
                        .getBlockMetadata(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ) + z)) {
                        case 6:
                            if (z > 0) {
                                this.motionZ -= movement * 0.001;
                            }
                            break;
                        case 7:
                            if (z < 0) {
                                this.motionZ += movement * 0.001;
                            }
                    }
                }

                if (id2 == BlockList.fan.id) {
                    switch (this.world
                        .getBlockMetadata(MathHelper.floor_double(this.posX) + x, MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))) {
                        case 8:
                            if (x > 0) {
                                this.motionX -= movement * 0.001;
                            }
                            break;
                        case 9:
                            if (x < 0) {
                                this.motionX += movement * 0.001;
                            }
                    }
                }
            }
        }
    }

    private void activatePortal() {
        boolean validPortalLocation = true;
        Portal.breakPortal = false;

        for (int i = -1; i < 1; i++) {
            if (this.world.getBlock(MathHelper.floor_double(this.posX + 2.0), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ + i))
                    != BlockList.endPortalFrame
                || this.world.getBlock(MathHelper.floor_double(this.posX - 2.0), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ + i))
                    != BlockList.endPortalFrame
                || this.world.getBlock(MathHelper.floor_double(this.posX + i), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ + 2.0))
                    != BlockList.endPortalFrame
                || this.world.getBlock(MathHelper.floor_double(this.posX + i), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ - 2.0))
                    != BlockList.endPortalFrame) {
                validPortalLocation = false;
            }
        }

        if (validPortalLocation) {
            for (int x = MathHelper.floor_double(this.posX) - 4; x <= MathHelper.floor_double(this.posX) + 4; x++) {
                for (int z = MathHelper.floor_double(this.posZ) - 4; z <= MathHelper.floor_double(this.posZ) + 4; z++) {
                    double distX = x - MathHelper.floor_double(this.posX);
                    double distZ = z - MathHelper.floor_double(this.posZ);
                    double dist = MathHelper.sqrt_double(distX * distX + distZ * distZ);
                    if (dist <= 1.5) {
                        this.world.setBlock(x, MathHelper.floor_double(this.posY), z, BlockList.endPortal.id);
                        this.setDead();
                    }
                }
            }

            this.world.spawnEntity(new LightningBolt(this.world, this.posX, this.posY, this.posZ));
        }

        Portal.breakPortal = true;
    }

    protected final void convertHellfireItem() {
        if (!this.world.isOnline) {
            ItemStack item = this.item;
            ItemStack smeltResult = FurnaceRecipes.smelting().getSmeltingResult(this.item.itemID, this.item.itemDmg);
            if (smeltResult != null && this.world.rand.nextFloat() <= 1.0F) {
                double offsetX = this.world.rand.nextDouble() * 0.7 + 0.15;
                double offsetY = this.world.rand.nextDouble() * 0.7 + 0.15;
                double offsetZ = this.world.rand.nextDouble() * 0.7 + 0.15;
                DroppedItem smeltedItem = new DroppedItem(
                    this.world,
                    this.posX + offsetX,
                    this.posY + offsetY,
                    this.posZ + offsetZ,
                    new ItemStack(smeltResult.itemID, item.stackSize, smeltResult.itemDmg)
                );
                this.world.spawnEntity(smeltedItem);
            }

            this.setDead();
        }
    }

    @Override
    public boolean isInWater() {
        return this.world.handleWaterMovement(this.boundingBox, MaterialList.water, this);
    }

    private boolean pushOutOfBlocks(double x, double y, double z) {
        int posX = MathHelper.floor_double(x);
        int posY = MathHelper.floor_double(y);
        int posZ = MathHelper.floor_double(z);
        double distX = x - posX;
        double distY = y - posY;
        double distZ = z - posZ;
        if (Block.isOpaque.get(this.world.getBlockId(posX, posY, posZ))
            && this.world.getBlockId(posX, posY, posZ) != BlockList.quickSand.id
            && this.world.getBlockId(posX, posY, posZ) != BlockList.powderSnow.id) {
            boolean blockEast = !Block.isOpaque.get(this.world.getBlockId(posX - 1, posY, posZ));
            boolean blockWest = !Block.isOpaque.get(this.world.getBlockId(posX + 1, posY, posZ));
            boolean blockUp = !Block.isOpaque.get(this.world.getBlockId(posX, posY - 1, posZ));
            boolean blockDown = !Block.isOpaque.get(this.world.getBlockId(posX, posY + 1, posZ));
            boolean blockNorth = !Block.isOpaque.get(this.world.getBlockId(posX, posY, posZ - 1));
            boolean blockSouth = !Block.isOpaque.get(this.world.getBlockId(posX, posY, posZ + 1));
            byte side = -1;
            double magicFactor = 9999.0;
            if (blockEast && distX < magicFactor) {
                magicFactor = distX;
                side = 0;
            }

            if (blockWest && 1.0 - distX < magicFactor) {
                magicFactor = 1.0 - distX;
                side = 1;
            }

            if (blockUp && distY < magicFactor) {
                magicFactor = distY;
                side = 2;
            }

            if (blockDown && 1.0 - distY < magicFactor) {
                magicFactor = 1.0 - distY;
                side = 3;
            }

            if (blockNorth && distZ < magicFactor) {
                magicFactor = distZ;
                side = 4;
            }

            if (blockSouth && 1.0 - distZ < magicFactor) {
                magicFactor = 1.0 - distZ;
                side = 5;
            }

            float offset = this.rand.nextFloat() * 0.2F + 0.1F;
            if (side == 0) {
                this.motionX = -offset;
            }

            if (side == 1) {
                this.motionX = offset;
            }

            if (side == 2) {
                this.motionY = -offset;
            }

            if (side == 3) {
                this.motionY = offset;
            }

            if (side == 4) {
                this.motionZ = -offset;
            }

            if (side == 5) {
                this.motionZ = offset;
            }
        }

        return false;
    }

    public boolean isInVacuum() {
        return this.world.handleWaterMovement(this.boundingBox.expand(0.1F, 0.1F, 0.1F), MaterialList.vacuum, this);
    }

    @Override
    public void dealFireDamage(int damage) {
        if (this.canBurn()) {
            this.damageEntity(DamageSource.inFire, damage);
        }
    }

    @Override
    public boolean damageEntity(DamageSource src, int amount) {
        this.setAttacked();
        this.health -= amount;
        if (this.health <= 0) {
            this.setDead();
        }

        return false;
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        tag.setShort("Health", (byte)this.health);
        tag.setShort("Age", (short)this.age);
        tag.setCompoundTag("Item", this.item.writeToNBT(new CompoundTag()));
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        this.health = tag.getShort("Health") & 255;
        this.age = tag.getShort("Age");
        this.item = new ItemStack(tag.getCompoundTag("Item"));
    }

    @Override
    public void onCollideWith(Entity entity) {
        if (!this.world.isOnline && this.pickupDelay <= 0 && this.item.stackSize > 0 && !this.isDead) {
            if (entity instanceof Player) {
                Player player = (Player)entity;
                int size = this.item.stackSize;
                if (player.inventory.addItemToInventory(this.item)) {
                    this.world.playEntitySound(this, "random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                    player.onItemPickup(this);
                    player.addStat(StatList.collectStat, size);
                    player.addStat(StatList.pickupStats[this.item.itemID], size);
                    this.setDead();
                }
            } else if (entity instanceof Fox) {
                Fox fox = (Fox)entity;
                if (fox.heldItem == null && fox.isTamed() && !fox.sleeping) {
                    fox.heldItem = this.item;
                    this.world.playEntitySound(this, "random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                    this.setDead();
                }
            } else if (entity instanceof Vulture && this.item.getItem() == ItemList.rottenFlesh) {
                ((Vulture)entity).heal(2);
                this.world.playEntitySound(this, "random.eat", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                this.item.stackSize--;
                if (this.item.stackSize <= 0) {
                    this.setDead();
                }
            } else if (entity instanceof Pigman && !(entity instanceof Priest) && this.item.getItem() == ItemList.ingotIron) {
                Pigman pigman = (Pigman)entity;
                if (pigman.item == null) {
                    pigman.item = this.item;
                    this.world.playEntitySound(this, "random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                    this.item.stackSize--;
                    if (this.item.stackSize <= 0) {
                        this.setDead();
                    }
                }
            } else if (entity instanceof AquaCube && this.item.itemID < 4096 && this.item.getItem() instanceof BlockItem) {
                AquaCube gel = (AquaCube)entity;
                Block block = BlockList.blocks[this.item.itemID];
                if (gel.block == null
                    && gel.tamed
                    && block != null
                    && (
                        block.getRenderType() == Block.RenderType.NORMAL
                            || block.getRenderType() == Block.RenderType.CHEST
                            || block.getRenderType() == Block.RenderType.SLIME
                    )) {
                    gel.block = BlockList.blocks[this.item.itemID];
                    gel.meta = this.item.itemDmg;
                    this.item.stackSize--;
                    if (this.item.stackSize <= 0) {
                        this.setDead();
                    }
                }
            } else if (entity instanceof DroppedItem) {
                DroppedItem drop = (DroppedItem)entity;
                if (drop.pickupDelay <= 0 && drop.item.itemID == this.item.itemID && drop.item.itemDmg == this.item.itemDmg) {
                    int space = this.item.getStackLimit() - this.item.stackSize;
                    int transfer = Math.min(space, drop.item.stackSize);
                    this.item.stackSize += transfer;
                    drop.item.stackSize -= transfer;
                    if (drop.item.stackSize <= 0) {
                        drop.setDead();
                    }
                }
            }
        }
    }

    public boolean isInHellfire(BoundingBox aabb, boolean sizzleOut) {
        int minX = MathHelper.floor_double(aabb.minX);
        int maxX = MathHelper.floor_double(aabb.maxX + 1.0);
        int minY = MathHelper.floor_double(aabb.minY);
        int maxY = MathHelper.floor_double(aabb.maxY + 1.0);
        int minZ = MathHelper.floor_double(aabb.minZ);
        int maxZ = MathHelper.floor_double(aabb.maxZ + 1.0);

        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    if (this.world.getBlockId(x, y, z) == BlockList.hellfire.id && sizzleOut) {
                        this.world.setBlock(x, y, z, 0);
                        this.world.playBlockSound(x, y, z, "random.fizz", 0.5F, 2.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.8F);
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
