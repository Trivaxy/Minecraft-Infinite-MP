package net.minecraft.game.container.tileentities;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.container.Container;
import net.minecraft.game.container.inventory.BasicInventory;
import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.container.inventory.PlayerInventory;
import net.minecraft.game.container.slot.Slot;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.enchantment.EnchantmentHelper;
import net.minecraft.game.item.enchantment.EnchantmentList;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;
import net.minecraft.statistics.achievement.AchievementList;

public class GrindstoneContainer extends Container {
    public Inventory grindstoneInv = new BasicInventory("Grindstone", 2);
    private World world;
    private Player player;
    public boolean canReduce = false;
    private int xPos;
    private int yPos;
    private int zPos;

    public GrindstoneContainer(PlayerInventory playerInv, World world, int x, int y, int z) {
        this.player = playerInv.player;
        this.world = world;
        this.xPos = x;
        this.yPos = y;
        this.zPos = z;
        this.addSlot(new GrindstoneContainer.ReduceSlot(this.grindstoneInv, 0, 56, 28, false));
        this.addSlot(new GrindstoneContainer.ReduceSlot(this.grindstoneInv, 1, 116, 28, true));

        for (int column = 0; column < 3; column++) {
            for (int row = 0; row < 9; row++) {
                this.addSlot(new Slot(playerInv, row + column * 9 + 9, 8 + row * 18, 89 + column * 18));
            }
        }

        for (int var8 = 0; var8 < 9; var8++) {
            this.addSlot(new Slot(playerInv, var8, 8 + var8 * 18, 147));
        }
    }

    @Override
    public void onClose(Player player) {
        super.onClose(player);
        if (!this.world.isOnline) {
            ItemStack stack = this.grindstoneInv.getStackInSlot(0);
            if (stack != null && !player.inventory.addItemToInventory(stack)) {
                player.dropPlayerItem(stack);
            }
        }
    }

    @Override
    public boolean canUse(Player player) {
        return this.world.getBlockId(this.xPos, this.yPos, this.zPos) != BlockList.grindstone.id
            ? false
            : player.getDistanceSq(this.xPos + 0.5, this.yPos + 0.5, this.zPos + 0.5) <= 64.0;
    }

    @Override
    public ItemStack getStackInSlot(int slotIndex) {
        ItemStack stack = null;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasStack()) {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();
            if (slotIndex == 1 && slot.canTakeStack(this.player)) {
                if (!this.isSlotFree(slotStack, 2, 38, true)) {
                    return null;
                }

                slot.onChanged();
            } else if (slotIndex != 0) {
                if (slotIndex >= 2 && slotIndex < 38 && !this.isSlotFree(slotStack, 0, 1, false)) {
                    return null;
                }
            } else if (!this.isSlotFree(slotStack, 2, 38, false)) {
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

    class ReduceSlot extends Slot {
        int[] enchants = new int[]{
            EnchantmentList.sharpness.id, EnchantmentList.efficiency.id, EnchantmentList.unbreaking.id, EnchantmentList.protection.id, EnchantmentList.power.id
        };
        private boolean isOutput = false;

        public ReduceSlot(Inventory inv, int slotIndex, int x, int y, boolean output) {
            super(inv, slotIndex, x, y);
            this.isOutput = output;
        }

        @Override
        public boolean validItem(ItemStack stack) {
            return this.isOutput ? false : stack != null && (stack.getItem().outputCount > 0 || stack.getItem() == ItemList.scroll);
        }

        @Override
        public void putStack(ItemStack stack) {
            super.putStack(stack);
            ItemStack inputStack = GrindstoneContainer.this.grindstoneInv.getStackInSlot(0);
            if (inputStack != null && (inputStack.getItem() == ItemList.scroll || inputStack.itemDmg == 0)) {
                if (inputStack.getEnchants() != null) {
                    int meta = 0;

                    for (int i = 0; i < this.enchants.length; i++) {
                        if (EnchantmentHelper.getLevel(this.enchants[i], inputStack) > 0) {
                            meta = i + 1;
                        }
                    }

                    GrindstoneContainer.this.grindstoneInv.setSlotContents(1, new ItemStack(ItemList.scroll.id, 1, meta));
                    GrindstoneContainer.this.canReduce = true;
                } else if (inputStack.getItem().outputId > 0 && inputStack.getItem().outputCount > 0) {
                    GrindstoneContainer.this.grindstoneInv
                        .setSlotContents(1, new ItemStack(inputStack.getItem().outputId, inputStack.getItem().outputCount, inputStack.getItem().outputMeta));
                    GrindstoneContainer.this.canReduce = true;
                }
            }
        }

        @Override
        public void onPickup(ItemStack stack) {
            ItemStack inputStack = GrindstoneContainer.this.grindstoneInv.getStackInSlot(0);
            if (inputStack != null) {
                if (stack.getItem() == ItemList.scrap) {
                    GrindstoneContainer.this.player.triggerAchievement(AchievementList.grindr);
                }

                GrindstoneContainer.this.player.addStat(StatList.itemsReducedStat, 1);
                if (GrindstoneContainer.this.player.properties.gamemode != 1) {
                    Player var10000 = GrindstoneContainer.this.player;
                    var10000.score = var10000.score - inputStack.getRepairCost();
                }

                GrindstoneContainer.this.grindstoneInv.setSlotContents(0, null);
                GrindstoneContainer.this.world
                    .playBlockSound(
                        GrindstoneContainer.this.xPos, GrindstoneContainer.this.yPos, GrindstoneContainer.this.zPos, "random.grindstone", 1.0F, 1.0F
                    );
            }

            GrindstoneContainer.this.grindstoneInv.setSlotContents(1, null);
        }

        @Override
        public boolean canTakeStack(Player player) {
            ItemStack inputStack = GrindstoneContainer.this.grindstoneInv.getStackInSlot(0);
            return inputStack == null || player.score >= inputStack.getRepairCost() || player.properties.gamemode == 1 || !this.isOutput;
        }
    }
}
