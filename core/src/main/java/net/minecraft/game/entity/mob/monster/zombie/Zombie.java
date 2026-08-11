package net.minecraft.game.entity.mob.monster.zombie;

import com.mojang.nbt.tag.special.CompoundTag;
import com.mojang.nbt.tag.special.ListTag;
import java.util.Iterator;
import net.minecraft.game.entity.mob.CreatureType;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.Task;
import net.minecraft.game.entity.mob.ai.hostile.Aggro;
import net.minecraft.game.entity.mob.ai.hostile.Attack;
import net.minecraft.game.entity.mob.ai.hostile.ChangeCourse;
import net.minecraft.game.entity.mob.ai.hostile.DetectEnemy;
import net.minecraft.game.entity.mob.ai.passive.FleeSun;
import net.minecraft.game.entity.mob.ai.passive.InteractDoor;
import net.minecraft.game.entity.mob.ai.passive.LookAround;
import net.minecraft.game.entity.mob.ai.passive.LookAt;
import net.minecraft.game.entity.mob.ai.passive.RestrictSun;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.mob.follower.Decoy;
import net.minecraft.game.entity.mob.monster.Monster;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class Zombie extends Monster {
    private boolean helmet;
    private boolean armor;
    private boolean legs;
    private boolean boots;
    protected boolean vampiric = true;
    private Object[][] armorType = new Object[][]{
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
    private Item[] items = new Item[]{
        ItemList.axeSteel,
        ItemList.swordSteel,
        ItemList.shovelSteel,
        ItemList.pickaxeSteel,
        ItemList.hoeSteel,
        ItemList.axeCopper,
        ItemList.swordCopper,
        ItemList.shovelCopper,
        ItemList.pickaxeCopper,
        ItemList.hoeCopper,
        ItemList.axeStone,
        ItemList.swordStone,
        ItemList.shovelStone,
        ItemList.pickaxeStone,
        ItemList.hoeStone,
        ItemList.axeGold,
        ItemList.swordGold,
        ItemList.shovelGold,
        ItemList.pickaxeGold,
        ItemList.hoeGold,
        ItemList.axeDiamond,
        ItemList.swordDiamond,
        ItemList.shovelDiamond,
        ItemList.pickaxeDiamond,
        ItemList.hoeDiamond
    };

    public Zombie(World world) {
        super(world);
        this.attackStrength = 5;
        this.moveSpeed = 0.23F;
        this.scoreValue = 100;
        this.texture = "/mob/zombie/zombie.png";
        this.equipRandomArmor(world);
        this.behaviors.addTask(0, new Swim(this));
        this.behaviors.addTask(1, new Zombie.BreakDoor(this));
        this.behaviors.addTask(2, new RestrictSun(this));
        this.behaviors.addTask(3, new FleeSun(this, this.moveSpeed));
        this.behaviors.addTask(4, new Attack(this, this.moveSpeed, false, true));
        this.behaviors.addTask(5, new ChangeCourse(this, this.moveSpeed));
        this.behaviors.addTask(6, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(7, new LookAt(this, Player.class, 8.0F));
        this.behaviors.addTask(8, new LookAround(this));
        this.detections.addTask(1, new Aggro(this, false));
        this.detections.addTask(2, new DetectEnemy(this, Decoy.class, 16.0F, 0, true));
        this.detections.addTask(3, new DetectEnemy(this, Player.class, 16.0F, 0, true));
    }

    protected void equipRandomArmor(World world) {
        if (world != null && !world.isOnline) {
            double chance = 0.1 * world.getSettings().getDifficulty();
            if (world.bloodMoon) {
                chance = 0.75;
            }

            this.helmet = Math.random() < chance;
            this.armor = Math.random() < chance;
            this.legs = Math.random() < chance;
            this.boots = Math.random() < chance;
            if (this.boots) {
                this.armorInventory[0] = (ItemStack)this.armorType[0][this.rand.nextInt(6)];
            }

            if (this.legs) {
                this.armorInventory[1] = (ItemStack)this.armorType[1][this.rand.nextInt(6)];
            }

            if (this.armor) {
                this.armorInventory[2] = (ItemStack)this.armorType[2][this.rand.nextInt(6)];
            }

            if (this.helmet) {
                this.armorInventory[3] = (ItemStack)this.armorType[3][this.rand.nextInt(6)];
            }

            if (this.rand.nextFloat() <= chance) {
                this.heldItem = this.items[this.rand.nextInt(this.items.length)];
            }
        }
    }

    @Override
    public CreatureType getCreatureType() {
        return CreatureType.UNDEAD;
    }

    @Override
    public int getSpawnID() {
        return 4;
    }

    @Override
    public double getYOffset() {
        return -0.3;
    }

    @Override
    public void mobTick() {
        if (this.world.isDaytime() && this.vampiric) {
            float brightness = this.getBrightness(1.0F);
            if (brightness > 0.5F
                && this.getEquipmentInSlot(3) == null
                && this.world.isBlockExposed(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))
                && this.rand.nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F) {
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
        return ItemList.rottenFlesh.id;
    }

    @Override
    public String getSound() {
        return "mob.zombie";
    }

    @Override
    protected String getHurtSound() {
        return "mob.zombiehurt";
    }

    @Override
    protected String getDeathSound() {
        return "mob.zombiedeath";
    }

    @Override
    public void playStepSound() {
        this.world.playEntitySound(this, "mob.zombiestep", 0.15F, 1.0F);
    }

    @Override
    protected void dropFewItems(int i) {
        int maxCount = this.rand.nextInt(3);
        if (this.armor || this.helmet || this.boots || this.legs) {
            for (int count = 0; count < maxCount; count++) {
                this.dropItem(ItemList.chainmail.id, 1);
            }
        }

        maxCount = this.rand.nextInt(3);

        for (int count = 0; count < maxCount; count++) {
            this.dropItem(this.getDrop(), 1);
        }
    }

    @Override
    public void onDeath(DamageSource entity) {
        super.onDeath(entity);
        if (!this.world.isOnline && entity.getEntity() instanceof Player) {
            Player player = (Player)entity.getEntity();
            int armorChance = this.rand.nextInt(25);
            int toolChance = this.rand.nextInt(40);
            if (player.getLuckModifier()) {
                armorChance = this.rand.nextInt(10);
                toolChance = this.rand.nextInt(25);
            }

            if (armorChance == 0) {
                for (int i = 0; i < this.armorInventory.length; i++) {
                    if (this.armorInventory[i] != null) {
                        this.dropItem(this.armorInventory[i].itemID, 1, this.rand.nextInt(this.armorInventory[i].getMaxDamage()), 0.0F);
                    }
                }
            }

            if (toolChance == 0 && this.heldItem != null) {
                this.dropItem(this.heldItem.id, 1, this.rand.nextInt(this.heldItem.getMaxDamage()), 0.0F);
            }
        }
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        ListTag<CompoundTag> armors = new ListTag<>();

        for (int slot = 0; slot < this.armorInventory.length; slot++) {
            if (this.armorInventory[slot] != null) {
                CompoundTag armorTag;
                (armorTag = new CompoundTag()).setByte("Slot", (byte)slot);
                this.armorInventory[slot].writeToNBT(armorTag);
                armors.add(armorTag);
            }
        }

        tag.setTag("Inventory", armors);
        if (this.heldItem != null) {
            tag.setInteger("Item", this.heldItem.id);
        }
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        ListTag<CompoundTag> inventory = tag.getTagList("Inventory");
        this.armorInventory = new ItemStack[4];

        for (int slotAt = 0; slotAt < inventory.size(); slotAt++) {
            CompoundTag armorTag = inventory.get(slotAt);
            int slot = armorTag.getByte("Slot") & 255;
            if (slot >= 0 && slot < this.armorInventory.length) {
                this.armorInventory[slot] = new ItemStack(armorTag);
            }
        }

        if (tag.getInteger("Item") > 0) {
            this.heldItem = ItemList.items[tag.getInteger("Item")];
        }
    }

    protected void clearAttributes() {
        Iterator<Task> it = this.behaviors.tasks.iterator();

        while (it.hasNext()) {
            Task entry = it.next();
            if (entry.action instanceof Attack) {
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

    public class BreakDoor extends InteractDoor {
        private int breakTime;

        public BreakDoor(Mob mob) {
            super(mob);
        }

        @Override
        public boolean canExecute() {
            return !super.canExecute() ? false : !this.targetDoor.isOpen(this.host.world, this.mobX, this.mobY, this.mobZ);
        }

        @Override
        public void start() {
            super.start();
            this.breakTime = 240;
        }

        @Override
        public boolean continueExecuting() {
            return this.breakTime >= 0
                && !this.targetDoor.isOpen(this.host.world, this.mobX, this.mobY, this.mobZ)
                && this.host.getDistanceSq(this.mobX, this.mobY, this.mobZ) < 4.0;
        }

        @Override
        public void tick() {
            super.tick();
            if (this.host.rand.nextInt(20) == 0) {
                this.host.world.playEntitySound(this.host, "random.wood", 1.0F, (this.host.rand.nextFloat() - this.host.rand.nextFloat()) * 0.2F + 1.0F);
            }

            if (--this.breakTime == 0 && (this.host.world.settings.getDifficulty() == 3 || this.host.world.bloodMoon)) {
                this.host.world.setBlockWithNotify(this.mobX, this.mobY, this.mobZ, 0);
                this.host.world.playEntitySound(this.host, "random.woodbreak", 1.0F, (this.host.rand.nextFloat() - this.host.rand.nextFloat()) * 0.2F + 1.0F);
            }
        }
    }
}
