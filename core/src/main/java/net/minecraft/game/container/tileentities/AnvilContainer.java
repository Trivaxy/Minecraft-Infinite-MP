package net.minecraft.game.container.tileentities;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.container.Container;
import net.minecraft.game.container.inventory.BasicInventory;
import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.container.inventory.PlayerInventory;
import net.minecraft.game.container.inventory.WorkbenchInventory;
import net.minecraft.game.container.slot.Slot;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.Rarity;
import net.minecraft.game.item.enchantment.Enchantment;
import net.minecraft.game.item.enchantment.EnchantmentList;
import net.minecraft.game.item.misc.Gem;
import net.minecraft.game.item.tools.Armor;
import net.minecraft.game.item.tools.Tool;
import net.minecraft.game.world.World;
import net.minecraft.localization.Translator;
import net.minecraft.statistics.StatList;
import net.minecraft.statistics.achievement.AchievementList;
import net.minecraft.util.math.MathHelper;

public class AnvilContainer extends Container {
    private Inventory outputSlot = new WorkbenchInventory();
    private Inventory inputSlots = new AnvilContainer.InventoryRepair(this, "Repair", 2);
    private World world;
    public final Player player;
    public String customName;
    private int posX;
    private int posY;
    private int posZ;
    private int stackSize = 0;

    public AnvilContainer(PlayerInventory playerInventory, World world, int x, int y, int z, Player player) {
        this.world = world;
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        this.player = player;
        this.addSlot(new Slot(this.inputSlots, 0, 27, 47));
        this.addSlot(new Slot(this.inputSlots, 1, 76, 47));
        this.addSlot(new AnvilContainer.SlotRepair(this.outputSlot, 2, 134, 47, world, x, y, z, this.player));

        for (int column = 0; column < 3; column++) {
            for (int row = 0; row < 9; row++) {
                this.addSlot(new Slot(playerInventory, row + column * 9 + 9, 8 + row * 18, 98 + column * 18));
            }
        }

        for (int var9 = 0; var9 < 9; var9++) {
            this.addSlot(new Slot(playerInventory, var9, 8 + var9 * 18, 156));
        }
    }

    @Override
    public void updateInventory(Inventory inventory) {
        super.updateInventory(inventory);
        if (inventory == this.inputSlots) {
            this.updateRepairOutput();
        }
    }

    public void updateRepairOutput() {
        ItemStack itemToRepair = this.inputSlots.getStackInSlot(0);
        this.stackSize = 0;
        if (itemToRepair == null) {
            this.outputSlot.setSlotContents(0, null);
            return;
        }

        ItemStack repairCopy = itemToRepair.copy();
        ItemStack repairMaterial = this.inputSlots.getStackInSlot(1);
        boolean modified = false;
        if (repairMaterial != null) {
            if (repairCopy.isStackDamageable()
                && (
                    ItemList.items[repairCopy.itemID].isRepairable(itemToRepair, repairMaterial)
                        || ItemList.items[repairCopy.itemID].isEnchantable(itemToRepair, repairMaterial)
                )) {
                int itemDamage = Math.min(repairCopy.getItemDamage(), repairCopy.getMaxDamage() / 4);
                if (itemDamage <= 0 && repairMaterial.getItem() != ItemList.scroll) {
                    this.outputSlot.setSlotContents(0, null);
                    return;
                }

                if (repairMaterial.getItem() == ItemList.scroll) {
                    Enchantment enchant = EnchantmentList.enchantments[repairMaterial.getItemDamage()];
                    int lvl = 1;
                    int tier = 0;
                    if (repairCopy.getItem() instanceof Tool) {
                        tier = ((Tool)repairCopy.getItem()).toolMaterial.getHarvestLevel();
                    } else if (repairCopy.getItem() instanceof Armor) {
                        tier = ((Armor)repairCopy.getItem()).tier.getTier();
                    }

                    if (tier > 0) {
                        lvl = enchant.getMaxLevel() + 1 - tier;
                    }

                    lvl = MathHelper.clamp(lvl, enchant.getMinLevel(), enchant.getMaxLevel());
                    repairCopy.addEnchantment(enchant, lvl);
                    this.stackSize = 1;
                } else {
                    int used = 0;

                    while (itemDamage > 0 && used < repairMaterial.stackSize) {
                        repairCopy.setItemDamage(repairCopy.getItemDamage() - itemDamage);
                        itemDamage = Math.min(repairCopy.getItemDamage(), repairCopy.getMaxDamage() / 4);
                        used++;
                    }

                    this.stackSize = used;
                }

                modified = true;
            } else {
                if (repairCopy.itemID != repairMaterial.itemID || !repairCopy.isStackDamageable()) {
                    this.outputSlot.setSlotContents(0, null);
                    return;
                }

                int remaining = itemToRepair.getMaxDamage() - itemToRepair.getItemDamage();
                int materialRemaining = repairMaterial.getMaxDamage() - repairMaterial.getItemDamage();
                int restored = remaining + materialRemaining + repairCopy.getMaxDamage() * 12 / 100;
                int newDamage = Math.max(repairCopy.getMaxDamage() - restored, 0);
                if (newDamage >= repairCopy.getItemDamage()) {
                    this.outputSlot.setSlotContents(0, null);
                    return;
                }

                repairCopy.setItemDamage(newDamage);
                this.stackSize = 1;
                modified = true;
            }
        }

        if (this.isRename(itemToRepair)) {
            repairCopy.setItemName(this.customName);
            modified = true;
        } else if ((this.customName == null || this.customName.isEmpty()) && itemToRepair.hasDisplayName()) {
            repairCopy.setItemName("");
            modified = true;
        }

        this.outputSlot.setSlotContents(0, modified ? repairCopy : null);
    }

    private boolean isRename(ItemStack itemToRepair) {
        if (this.customName == null || this.customName.isEmpty() || this.customName.equals(itemToRepair.customName)) {
            return false;
        } else {
            String defaultName = Translator.getInstance().translateNamedKey(itemToRepair.getItemName(itemToRepair.itemDmg));
            return !this.customName.equals(defaultName) || itemToRepair.hasDisplayName();
        }
    }

    @Override
    public void onClose(Player player) {
        super.onClose(player);
        if (!this.world.isOnline) {
            for (int slot = 0; slot < this.inputSlots.getSize(); slot++) {
                ItemStack item = this.inputSlots.getStackInSlot(slot);
                if (item != null && !player.inventory.addItemToInventory(item)) {
                    player.dropPlayerItem(item);
                }
            }
        }
    }

    @Override
    public boolean canUse(Player player) {
        return this.world.getBlockId(this.posX, this.posY, this.posZ) != BlockList.anvil.id
            ? false
            : player.getDistanceSq(this.posX + 0.5, this.posY + 0.5, this.posZ + 0.5) <= 64.0;
    }

    @Override
    public ItemStack getStackInSlot(int slotIndex) {
        ItemStack stack = null;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasStack() && slot.canTakeStack(this.player)) {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();
            if (slotIndex == 2) {
                if (!this.isSlotFree(slotStack, 3, 39, true)) {
                    return null;
                }

                slot.onChanged();
            } else if (slotIndex != 0 && slotIndex != 1) {
                if (slotIndex >= 3 && slotIndex < 39 && !this.isSlotFree(slotStack, 0, 2, false)) {
                    return null;
                }
            } else if (!this.isSlotFree(slotStack, 3, 39, false)) {
                return null;
            }

            if (slotStack.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onChanged();
            }

            if (slotStack.stackSize == stack.stackSize) {
                return null;
            }

            slot.onPickup(slotStack);
        }

        return stack;
    }

    public void updateItemName(String name) {
        this.customName = name == null ? "" : name;
        this.updateRepairOutput();
    }

    class InventoryRepair extends BasicInventory {
        InventoryRepair(AnvilContainer cont, String name, int slots) {
            super(name, slots);
        }

        @Override
        public void onChanged() {
            super.onChanged();
            AnvilContainer.this.updateInventory(this);
        }
    }

    class SlotRepair extends Slot {
        final World world;
        final Player player;
        final int blockX;
        final int blockY;
        final int blockZ;

        SlotRepair(Inventory inventory, int slotIndex, int slotX, int slotY, World world, int x, int y, int z, Player playa) {
            super(inventory, slotIndex, slotX, slotY);
            this.world = world;
            this.blockX = x;
            this.blockY = y;
            this.blockZ = z;
            this.player = playa;
        }

        @Override
        public boolean validItem(ItemStack stack) {
            return false;
        }

        @Override
        public boolean canTakeStack(Player player) {
            return this.getStack() != null && (player.score >= this.getStack().getRepairCost() || player.properties.gamemode == 1) && this.hasStack();
        }

        @Override
        public void onPickup(ItemStack stack) {
            if (AnvilContainer.this.inputSlots.getStackInSlot(0).getItem().getRarity() == Rarity.LEGENDARY
                && AnvilContainer.this.inputSlots.getStackInSlot(1) != null
                && AnvilContainer.this.inputSlots.getStackInSlot(1).getItem() instanceof Gem) {
                this.player.triggerAchievement(AchievementList.repair);
            }

            if (this.player.properties.gamemode != 1) {
                this.player.score = this.player.score - stack.getRepairCost();
            }

            AnvilContainer.this.inputSlots.setSlotContents(0, null);
            this.player.addStat(StatList.itemsEnchantedStat, 1);
            if (AnvilContainer.this.stackSize > 0) {
                ItemStack repairStack = AnvilContainer.this.inputSlots.getStackInSlot(1);
                if (repairStack != null && repairStack.getItem() == ItemList.scroll) {
                    this.player.triggerAchievement(AchievementList.enchant);
                }

                if (repairStack != null && repairStack.stackSize > AnvilContainer.this.stackSize) {
                    repairStack.stackSize = repairStack.stackSize - AnvilContainer.this.stackSize;
                    AnvilContainer.this.inputSlots.setSlotContents(1, repairStack);
                } else {
                    AnvilContainer.this.inputSlots.setSlotContents(1, null);
                }
            } else {
                AnvilContainer.this.inputSlots.setSlotContents(1, null);
            }

            if (!this.world.isOnline && this.world.getBlockId(this.blockX, this.blockY, this.blockZ) == BlockList.anvil.id) {
                int anvilMeta = this.world.getBlockMetadata(this.blockX, this.blockY, this.blockZ);
                int damageUpper = anvilMeta & 3;
                int damageLower = anvilMeta >> 2;
                if (++damageLower > 8) {
                    if (AnvilContainer.this.inputSlots.getStackInSlot(1) != null) {
                        DroppedItem fuck = new DroppedItem(this.world, this.blockX, this.blockY, this.blockZ, AnvilContainer.this.inputSlots.getStackInSlot(1));
                        this.world.spawnEntity(fuck);
                    }

                    this.world.setBlockWithNotify(this.blockX, this.blockY, this.blockZ, 0);
                    this.world.playBlockSound(this.blockX, this.blockY, this.blockZ, "random.anvilBreak", 1.0F, 1.0F);
                } else {
                    this.world.setBlockMetadataWithNotify(this.blockX, this.blockY, this.blockZ, damageUpper | damageLower << 2);
                    this.world.playBlockSound(this.blockX, this.blockY, this.blockZ, "random.anvilUse", 1.0F, 1.0F);
                }
            } else if (!this.world.isOnline) {
                this.world.playBlockSound(this.blockX, this.blockY, this.blockZ, "random.anvilUse", 1.0F, 1.0F);
            }
        }
    }
}
