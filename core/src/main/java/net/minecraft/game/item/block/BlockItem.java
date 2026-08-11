package net.minecraft.game.item.block;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.building.HardenedClay;
import net.minecraft.game.block.building.LayerBlock;
import net.minecraft.game.block.building.LeafPile;
import net.minecraft.game.block.building.Panel;
import net.minecraft.game.block.building.Slab;
import net.minecraft.game.block.building.Stairs;
import net.minecraft.game.block.decoration.Chair;
import net.minecraft.game.block.decoration.Fence;
import net.minecraft.game.block.decoration.FenceGate;
import net.minecraft.game.block.decoration.Table;
import net.minecraft.game.block.decoration.Trapdoor;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.block.stepsound.StepSound;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.physics.TargetingSystem;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.statistics.achievement.AchievementList;
import net.minecraft.util.math.MathHelper;

public class BlockItem extends Item {
    private int blockID;
    private Icon icon;
    public Block block;
    private static Map<Block, Integer> slabTypes = new HashMap<Block, Integer>() {
        private static final long serialVersionUID = 1L;

        {
            this.put(BlockList.slabStone, BlockList.doubleSlab.id);
            this.put(BlockList.slabWood, BlockList.doubleSlabWood.id);
            this.put(BlockList.slabBrick, BlockList.doubleBrick.id);
            this.put(BlockList.slabMoss, BlockList.doubleSlabMoss.id);
            this.put(BlockList.slabObsidian, BlockList.doubleSlabObsidian.id);
            this.put(BlockList.slabCobble, BlockList.doubleSlabCobble.id);
            this.put(BlockList.slabStoneBrick, BlockList.doubleSlabStoneBrick.id);
            this.put(BlockList.slabSandstone, BlockList.doubleSlabSandstone.id);
            this.put(BlockList.slabCloth, BlockList.doubleSlabCloth.id);
        }
    };

    public BlockItem(int id) {
        super(id);
        this.blockID = id + 4096;
        this.maxDamage = 0;
        this.block = BlockList.blocks[this.blockID];
        this.fullName = "tile." + this.block.name;
        this.description = "block." + this.block.name + ".desc";
        this.priorityItem = true;
        this.rarity = this.block.rarity;
        this.setHasSubtypes(this.block.hasStates);
    }

    @Override
    public boolean canCompost() {
        return this.block.compostable;
    }

    @Override
    public int getMaxDamage() {
        return this.block.getMaxMeta();
    }

    @Override
    public CreativeTabs creativeCategory() {
        return this.block.creativeCategory();
    }

    @Override
    public Icon getIconFromMeta(int meta) {
        return this.icon != null ? this.icon : this.block.getIcon(2, meta);
    }

    @Override
    public int getAtlas() {
        return 0;
    }

    @Override
    public final ItemStack use(ItemStack heldStack, World world, Player player) {
        float pitch = player.prevPitch + (player.pitch - player.prevPitch);
        float yaw = player.prevYaw + (player.yaw - player.prevYaw);
        double x = player.prevX + (player.posX - player.prevX);
        double y = player.prevY + (player.posY - player.prevY);
        double z = player.prevZ + (player.posZ - player.prevZ);
        Vector lookVector = new Vector(x, y, z);
        float angle = MathHelper.cos(-yaw * (float) (Math.PI / 180.0) - (float) Math.PI);
        yaw = MathHelper.sin(-yaw * (float) (Math.PI / 180.0) - (float) Math.PI);
        float offset = -MathHelper.cos(-pitch * (float) (Math.PI / 180.0));
        pitch = MathHelper.sin(-pitch * (float) (Math.PI / 180.0));
        yaw *= offset;
        angle *= offset;
        Vector vectorOff = lookVector.addVector(yaw * 5.0F, pitch * 5.0F, angle * 5.0F);
        TargetingSystem target = world.rayTrace(lookVector, vectorOff, true);
        if (target != null && !world.isOnline && BlockList.blocks[heldStack.itemID].placeableInWater()) {
            if (target.target == TargetingSystem.HitType.TILE) {
                int targetX = target.blockX;
                int targetY = target.blockY;
                int targetZ = target.blockZ;
                if (world.getBlockMaterial(targetX, targetY, targetZ) == MaterialList.water
                    && world.getBlockMetadata(targetX, targetY, targetZ) == 0
                    && target.sideHit == 1) {
                    world.setBlockAndMetadataWithNotify(targetX, targetY + 1, targetZ, this.blockID, heldStack.itemDmg);
                    world.playBlockSound(targetX, targetY + 1, targetZ, "step.grass", 1.0F, 0.8F);
                    player.swingItem();
                    if (player.properties.gamemode != 1) {
                        heldStack.stackSize--;
                    }
                }
            }

            return heldStack;
        } else {
            return heldStack;
        }
    }

    @Override
    public String getNameFromMeta(int damage) {
        Block block = this.block;
        if (block == BlockList.pumpkin && damage == 1) {
            return "tile.pumpkinCarved";
        }

        if (block.multipleNames && damage >= 0) {
            if (block instanceof LeafPile || block instanceof Stairs || block instanceof Chair || block instanceof FenceGate || block instanceof Trapdoor) {
                return "tile." + this.block.names[damage >> 3];
            }

            if (block instanceof Panel) {
                return "tile." + this.block.names[damage >> 2];
            }

            if (block instanceof Table) {
                if (damage == 255) {
                    return "tile.glassTable";
                }

                return "tile." + this.block.names[damage];
            }

            if (block instanceof Fence) {
                if (damage >= 128) {
                    return "tile." + ((Fence)this.block).namesAlt[damage - 128];
                }

                return "tile." + this.block.names[damage];
            }

            if (damage < this.block.names.length) {
                return "tile." + this.block.names[damage];
            }

            if (block instanceof HardenedClay && damage > block.getMaxMeta()) {
                return "tile.hardened_clay";
            }
        }

        return this.fullName;
    }

    @Override
    public void registerIcons(IconRegister r) {
        if (this.blockID == BlockList.tallTorch.id) {
            this.icon = r.registerIcon("tikiTorch");
        } else if (this.blockID == BlockList.spike.id) {
            this.icon = r.registerIcon("spikeTop");
        } else if (this.blockID == BlockList.icicle.id) {
            this.icon = r.registerIcon("spikeTopIce");
        } else if (this.blockID == BlockList.lantern.id) {
            this.icon = r.registerIcon("lanternIcon");
        }
    }

    @Override
    public boolean useOnBlock(ItemStack stack, Player player, World world, int xCoord, int yCoord, int zCoord, int side) {
        if (player.isUsingItem()) {
            return false;
        }

        Block block = world.getBlock(xCoord, yCoord, zCoord);
        if (block != null && !block.material.isGroundCover() && (!(block instanceof LayerBlock) || this.block instanceof LayerBlock)) {
            if (side == 0) {
                yCoord--;
            }

            if (side == 1) {
                yCoord++;
            }

            if (side == 2) {
                zCoord--;
            }

            if (side == 3) {
                zCoord++;
            }

            if (side == 4) {
                xCoord--;
            }

            if (side == 5) {
                xCoord++;
            }
        }

        if (stack.stackSize != 0 && !BlockList.blocks[stack.itemID].placeableInWater()) {
            int yValue = yCoord;
            block = BlockList.blocks[world.getBlockId(xCoord, yCoord - 1, zCoord)];
            this.stackBlocks(xCoord, yCoord, zCoord, world, player, stack, BlockList.chain.id);
            this.stackBlocks(xCoord, yCoord, zCoord, world, player, stack, BlockList.rope.id);
            if (side == 0) {
                block = BlockList.blocks[world.getBlockId(xCoord, yCoord + 1, zCoord)];
            }

            boolean canPlaceSlab = side <= 1 && this.block instanceof Slab && block != null && block instanceof Slab && !((Slab)block).doubleSlab;
            if (canPlaceSlab) {
                if (side == 0) {
                    yValue = yCoord + 1;
                } else {
                    yValue = yCoord - 1;
                }
            }

            if (!canPlaceSlab
                && world.canPlace(this.blockID, xCoord, yValue, zCoord, false, side)
                && world.setBlockAndMetadataWithNotify(xCoord, yValue, zCoord, this.blockID, stack.getItemDamage())) {
                this.block.onPlaced(world, player, xCoord, yValue, zCoord, side);
                StepSound snd = this.block.getStepSound(stack.getItemDamage());
                world.playBlockSound(xCoord + 0.5F, yCoord + 0.5F, zCoord + 0.5F, snd.getSound(), (snd.getVolume() + 1.0F) / 2.0F, snd.getPitch() * 0.8F);
                if (player.properties.gamemode != 1) {
                    stack.stackSize--;
                }
            }

            if (canPlaceSlab) {
                this.placeSlab(world, stack, player, xCoord, yValue, zCoord, side, this.block, stack.getItemDamage());
            }

            return true;
        } else {
            return false;
        }
    }

    private void placeSlab(World world, ItemStack stack, Player player, int x, int y, int z, int side, Block block, int meta) {
        int placedMeta = world.getBlockMetadata(x, y, z) & 127;
        int placedID = slabTypes.get(block);
        if ((side != 0 || world.getBlockMetadata(x, y, z) > 127) && (side != 1 || world.getBlockMetadata(x, y, z) <= 127)) {
            if (world.getBlockId(x, y, z) == block.id && placedMeta == meta) {
                StepSound snd = this.block.getStepSound(stack.getItemDamage());
                world.playBlockSound(x + 0.5F, y + 0.5F, z + 0.5F, snd.getSound(), (snd.getVolume() + 1.0F) / 2.0F, snd.getPitch() * 0.8F);
                if (player.properties.gamemode != 1) {
                    stack.stackSize--;
                }

                world.setBlockAndMetadataWithNotify(x, y, z, placedID, meta);
            }
        }
    }

    private void stackBlocks(int xCoord, int yCoord, int zCoord, World world, Player player, ItemStack stack, int placeID) {
        int bottomMostBlock = 0;
        int id = world.getBlockId(xCoord, yCoord, zCoord);

        for (int i = yCoord; i > 0; i--) {
            if (world.getBlockId(xCoord, i + 1, zCoord) == placeID && world.getBlockId(xCoord, i, zCoord) == 0) {
                bottomMostBlock = i;
            }
        }

        if (id == placeID && this.blockID == placeID) {
            if (player.properties.gamemode != 1 && world.getBlockId(xCoord, bottomMostBlock, zCoord) == 0 && bottomMostBlock != 0) {
                stack.stackSize--;
            }

            if (world.getBlock(xCoord, bottomMostBlock, zCoord) == null) {
                world.setBlockWithNotify(xCoord, bottomMostBlock, zCoord, placeID);
            }
        }
    }

    @Override
    public void getSubItems(int id, CreativeTabs ct, List<ItemStack> l) {
        this.block.getSubStacks(id, ct, l);
    }

    @Override
    public boolean hasSubtypes() {
        return this.block.hasStates;
    }

    @Override
    public void triggerAchievement(Player player) {
        if (this.id == BlockList.workbench.id) {
            player.triggerAchievement(AchievementList.buildWorkBench);
        } else if (this.id == BlockList.stoneOvenIdle.id) {
            player.triggerAchievement(AchievementList.buildFurnace);
        } else if (this.id == BlockList.wood.id || this.id == BlockList.log.id) {
            player.triggerAchievement(AchievementList.getWood);
        } else if (this.id == BlockList.chestObsidian.id) {
            player.triggerAchievement(AchievementList.chest);
        }
    }

    public static Block getSlab(int doubleSlabId) {
        for (Entry<Block, Integer> entry : slabTypes.entrySet()) {
            if (entry.getValue() == doubleSlabId) {
                return entry.getKey();
            }
        }

        return null;
    }
}
