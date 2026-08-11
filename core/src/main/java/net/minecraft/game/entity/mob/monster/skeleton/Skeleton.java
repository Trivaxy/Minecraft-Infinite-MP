package net.minecraft.game.entity.mob.monster.skeleton;

import com.mojang.nbt.tag.special.CompoundTag;
import com.mojang.nbt.tag.special.ListTag;
import java.util.Iterator;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.CreatureType;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.ai.Task;
import net.minecraft.game.entity.mob.ai.hostile.Aggro;
import net.minecraft.game.entity.mob.ai.hostile.DetectEnemy;
import net.minecraft.game.entity.mob.ai.hostile.RangedAttack;
import net.minecraft.game.entity.mob.ai.passive.FleeFrom;
import net.minecraft.game.entity.mob.ai.passive.FleeSun;
import net.minecraft.game.entity.mob.ai.passive.LookAround;
import net.minecraft.game.entity.mob.ai.passive.LookAt;
import net.minecraft.game.entity.mob.ai.passive.RestrictSun;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.mob.animal.tameable.Wolf;
import net.minecraft.game.entity.mob.follower.Decoy;
import net.minecraft.game.entity.mob.monster.Monster;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.statistics.achievement.AchievementList;
import net.minecraft.util.math.MathHelper;

public class Skeleton extends Monster {
    protected int dropId;
    protected boolean vampiric = true;
    private boolean helmet;
    private boolean armor;
    private boolean legs;
    private boolean boots;
    private Object[][] type = new Object[][]{
        {
                new ItemStack(ItemList.bootsLeather),
                new ItemStack(ItemList.bootsChain),
                new ItemStack(ItemList.bootsSteel),
                new ItemStack(ItemList.bootsGold),
                new ItemStack(ItemList.bootsDiamond),
                new ItemStack(ItemList.bootsCopper)
        },
        {
                new ItemStack(ItemList.legsLeather),
                new ItemStack(ItemList.legsChain),
                new ItemStack(ItemList.legsSteel),
                new ItemStack(ItemList.legsGold),
                new ItemStack(ItemList.legsDiamond),
                new ItemStack(ItemList.legsCopper)
        },
        {
                new ItemStack(ItemList.plateLeather),
                new ItemStack(ItemList.plateChain),
                new ItemStack(ItemList.plateSteel),
                new ItemStack(ItemList.plateGold),
                new ItemStack(ItemList.plateDiamond),
                new ItemStack(ItemList.plateCopper)
        },
        {
                new ItemStack(ItemList.helmetLeather),
                new ItemStack(ItemList.helmetChain),
                new ItemStack(ItemList.helmetSteel),
                new ItemStack(ItemList.helmetGold),
                new ItemStack(ItemList.helmetDiamond),
                new ItemStack(ItemList.helmetCopper)
        }
    };

    public Skeleton(World world) {
        super(world);
        this.texture = "/mob/skeleton/skeleton.png";
        this.heldItem = ItemList.bow;
        this.scoreValue = 100;
        this.moveSpeed = 0.25F;
        this.dropId = ItemList.arrowFlint.id;
        if (world != null && !world.isOnline) {
            double chance = 0.1;
            if (world.bloodMoon) {
                chance = 0.75;
            }

            this.helmet = Math.random() < chance;
            this.armor = Math.random() < chance;
            this.legs = Math.random() < chance;
            this.boots = Math.random() < chance;
            if (this.boots) {
                this.armorInventory[0] = (ItemStack)this.type[0][this.rand.nextInt(5)];
            }

            if (this.legs) {
                this.armorInventory[1] = (ItemStack)this.type[1][this.rand.nextInt(5)];
            }

            if (this.armor) {
                this.armorInventory[2] = (ItemStack)this.type[2][this.rand.nextInt(5)];
            }

            if (this.helmet) {
                this.armorInventory[3] = (ItemStack)this.type[3][this.rand.nextInt(5)];
            }
        }

        this.behaviors.addTask(1, new Swim(this));
        this.behaviors.addTask(2, new RestrictSun(this));
        this.behaviors.addTask(3, new FleeSun(this, this.moveSpeed));
        this.behaviors.addTask(4, new FleeFrom(this, Wolf.class, 8.0F, this.moveSpeed, this.moveSpeed * 2.0F));
        this.behaviors.addTask(5, new RangedAttack(this, this.moveSpeed, 0, 50));
        this.behaviors.addTask(6, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(7, new LookAt(this, Player.class, 8.0F));
        this.behaviors.addTask(8, new LookAround(this));
        this.detections.addTask(1, new Aggro(this, false));
        this.detections.addTask(2, new DetectEnemy(this, Decoy.class, 16.0F, 0, true));
        this.detections.addTask(3, new DetectEnemy(this, Player.class, 16.0F, 0, true));
    }

    @Override
    public CreatureType getCreatureType() {
        return CreatureType.UNDEAD;
    }

    @Override
    public double getYOffset() {
        return -0.3;
    }

    @Override
    public int getSpawnID() {
        return 5;
    }

    @Override
    public void mobTick() {
        if (this.world.isDaytime() && this.vampiric) {
            float light = this.getBrightness(1.0F);
            if (light > 0.5F
                && this.getEquipmentInSlot(3) == null
                && this.world.isBlockExposed(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))
                && this.rand.nextFloat() * 30.0F < (light - 0.4F) * 2.0F) {
                this.fire = 300;
            }
        }

        super.mobTick();
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public int getDrop() {
        return ItemList.bone.id;
    }

    @Override
    public final String getSound() {
        return "mob.skeleton";
    }

    @Override
    protected final String getHurtSound() {
        return "mob.skeletonhurt";
    }

    @Override
    protected final String getDeathSound() {
        return "mob.skeletondeath";
    }

    @Override
    public void playStepSound() {
        this.world.playEntitySound(this, "mob.skeletonstep", 0.15F, 1.0F);
    }

    @Override
    public void onDeath(DamageSource src) {
        super.onDeath(src);
        if (!this.world.isOnline) {
            if (this.dropId > 0) {
                for (int i = 0; i < this.rand.nextInt(3); i++) {
                    this.dropItem(this.dropId, 1);
                }
            }

            int bowChance = this.rand.nextInt(40);
            int armorChance = this.rand.nextInt(25);
            Entity entity = src.getEntity();
            if (entity != null && entity instanceof Player) {
                if (((Player)entity).getLuckModifier()) {
                    armorChance = this.rand.nextInt(25);
                }

                if (bowChance == 0) {
                    this.dropItem(ItemList.bow.id, 1, this.rand.nextInt(ItemList.bow.getMaxDamage()), 0.0F);
                }
            }

            if (entity != null && entity instanceof Player) {
                if (((Player)entity).getLuckModifier()) {
                    armorChance = this.rand.nextInt(10);
                }

                if (armorChance == 0) {
                    for (int i = 0; i < this.armorInventory.length; i++) {
                        if (this.armorInventory[i] != null) {
                            this.dropItem(this.armorInventory[i].itemID, 1, this.rand.nextInt(this.armorInventory[i].getMaxDamage()), 0.0F);
                        }
                    }
                }
            }
        }

        if (src.getEntity() instanceof Player) {
            Player player = (Player)src.getEntity();
            double distX = player.posX - this.posX;
            double distZ = player.posZ - this.posZ;
            if (distX * distX + distZ * distZ >= 2500.0) {
                player.triggerAchievement(AchievementList.sniperDuel);
            }
        }
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        ListTag<CompoundTag> tagList = new ListTag<>();

        for (int slot = 0; slot < this.armorInventory.length; slot++) {
            if (this.armorInventory[slot] != null) {
                CompoundTag inventoryTag;
                (inventoryTag = new CompoundTag()).setByte("Slot", (byte)slot);
                this.armorInventory[slot].writeToNBT(inventoryTag);
                tagList.add(inventoryTag);
            }
        }

        tag.setTag("Inventory", tagList);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        ListTag<CompoundTag> inventory = tag.getTagList("Inventory");
        this.armorInventory = new ItemStack[4];

        for (int slot = 0; slot < inventory.size(); slot++) {
            CompoundTag invTag = inventory.get(slot);
            int slotFor = invTag.getByte("Slot") & 255;
            if (slotFor >= 0 && slotFor < this.armorInventory.length) {
                this.armorInventory[slotFor] = new ItemStack(invTag);
            }
        }
    }

    protected void clearAttributes() {
        Iterator<Task> it = this.behaviors.tasks.iterator();

        while (it.hasNext()) {
            Task entry = it.next();
            if (entry.action instanceof RangedAttack) {
                it.remove();
            }

            if (entry.action instanceof FleeSun) {
                it.remove();
            }

            if (entry.action instanceof RestrictSun) {
                it.remove();
            }
        }

        for (int i = 0; i < this.armorInventory.length; i++) {
            this.armorInventory[i] = null;
        }
    }
}
