package net.minecraft.game.block.decoration;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.mob.monster.skeleton.Skeleton;
import net.minecraft.game.entity.mob.monster.zombie.Zombie;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.tools.Armor;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.world.World;
import net.minecraft.statistics.achievement.AchievementList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.random.WeightedRandom;

public class Grave extends Block {
    private static Grave.LootHolder[] graveLoot;

    public Grave(int blockid, Material material) {
        super(blockid, material);
        this.silkTouch = false;
    }

    @Override
    public BoundingBox getCollisionBox(World world, int xCoord, int yCoord, int zCoord) {
        return null;
    }

    @Override
    public void onPlaced(World world, Player player, int x, int y, int z, int side) {
        super.onPlaced(world, player, x, y, z, side);
        int facingDir = MathHelper.floor_double(player.yaw * 4.0F / 360.0F + 0.5) & 3;
        if (facingDir == 0) {
            world.setBlockMetadataWithNotify(x, y, z, 2);
        }

        if (facingDir == 1) {
            world.setBlockMetadataWithNotify(x, y, z, 5);
        }

        if (facingDir == 2) {
            world.setBlockMetadataWithNotify(x, y, z, 3);
        }

        if (facingDir == 3) {
            world.setBlockMetadataWithNotify(x, y, z, 4);
        }
    }

    @Override
    public boolean placeableAt(World world, int x, int y, int z) {
        return !world.isBlockFull(x, y - 1, z) ? false : super.placeableAt(world, x, y, z);
    }

    @Override
    public void onNeighborChange(World world, int x, int y, int z, int id) {
        if (!world.getBlockMaterial(x, y - 1, z).isSolid()) {
            this.dropBlock(world, x, y, z, world.getBlockMetadata(x, y, z));
            world.playEffect(2001, x, y, z, world.getBlockMetadata(x, y, z) << 12 | this.id);
            world.setBlockWithNotify(x, y, z, 0);
        }
    }

    @Override
    public void onBroken(World world, int xCoord, int yCoord, int zCoord, int f) {
        if (world.rand.nextInt(4) == 0) {
            if (world.rand.nextBoolean()) {
                Skeleton skellieBum = new Skeleton(world);
                skellieBum.setPosition(xCoord, yCoord, zCoord);
                world.spawnEntity(skellieBum);
            } else {
                Zombie zambi = new Zombie(world);
                zambi.setPosition(xCoord, yCoord, zCoord);
                world.spawnEntity(zambi);
            }

            Player playerEntity = world.getClosestPlayer(MathHelper.floor_double(xCoord), MathHelper.floor_double(yCoord), MathHelper.floor_double(zCoord), 8.0);
            if (playerEntity != null) {
                playerEntity.triggerAchievement(AchievementList.graveRobbin);
            }
        } else {
            Grave.LootHolder randomItem = WeightedRandom.getRandomItem(world.rand, getGraves());
            ItemStack stack = new ItemStack(randomItem.itemId, 1 + world.rand.nextInt(8));
            if (randomItem.itemId == ItemList.scroll.id || randomItem.itemId == ItemList.scrap.id) {
                stack = new ItemStack(randomItem.itemId, 1, world.rand.nextInt(6));
            } else if (randomItem.itemId == ItemList.crossboner.id
                || randomItem.itemId == ItemList.bow.id
                || randomItem.itemId == ItemList.swordStone.id
                || ItemList.items[randomItem.itemId] instanceof Armor) {
                stack = new ItemStack(randomItem.itemId, 1, world.rand.nextInt(stack.getMaxDamage()));
            } else if (randomItem.itemId == ItemList.dreamCatcher.id) {
                stack = new ItemStack(randomItem.itemId, 1);
            }

            DroppedItem drop = new DroppedItem(world, xCoord, yCoord + 1, zCoord, stack);
            world.spawnEntity(drop);
        }
    }

    @Override
    public boolean canStay(World world, int xCoord, int yCoord, int zCoord) {
        return !world.getBlockMaterial(xCoord, yCoord - 1, zCoord).isSolid();
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.GRAVE;
    }

    @Override
    public int dropID(int blockid, Random random2) {
        return 0;
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public Icon getIcon(int side, int mdata) {
        return BlockList.slowSand.getIcon(side, 0);
    }

    public static Grave.LootHolder[] getGraves() {
        if (graveLoot == null) {
            graveLoot = new Grave.LootHolder[]{
                new Grave.LootHolder(20, ItemList.coal),
                new Grave.LootHolder(15, BlockList.skull),
                new Grave.LootHolder(10, ItemList.scroll),
                new Grave.LootHolder(5, ItemList.crossboner),
                new Grave.LootHolder(3, ItemList.dreamCatcher),
                new Grave.LootHolder(1, ItemList.scrap),
                new Grave.LootHolder(5, ItemList.diamond),
                new Grave.LootHolder(8, ItemList.ruby),
                new Grave.LootHolder(8, ItemList.sapphire),
                new Grave.LootHolder(8, ItemList.emerald),
                new Grave.LootHolder(8, ItemList.amethyst),
                new Grave.LootHolder(15, ItemList.ingotGold),
                new Grave.LootHolder(25, ItemList.bone),
                new Grave.LootHolder(20, ItemList.ash),
                new Grave.LootHolder(20, ItemList.musketRound),
                new Grave.LootHolder(30, ItemList.rottenFlesh),
                new Grave.LootHolder(15, ItemList.book),
                new Grave.LootHolder(15, ItemList.bow),
                new Grave.LootHolder(25, ItemList.arrow),
                new Grave.LootHolder(20, ItemList.swordStone),
                new Grave.LootHolder(12, ItemList.helmetChain),
                new Grave.LootHolder(10, ItemList.plateChain),
                new Grave.LootHolder(12, ItemList.legsChain),
                new Grave.LootHolder(13, ItemList.bootsChain)
            };
        }

        return graveLoot;
    }

    public static class LootHolder extends WeightedRandom.Choice {
        public final int itemId;

        public LootHolder(int weight, Item item) {
            super(weight);
            this.itemId = item.id;
        }

        public LootHolder(int weight, Block cock) {
            super(weight);
            this.itemId = cock.id;
        }
    }
}
