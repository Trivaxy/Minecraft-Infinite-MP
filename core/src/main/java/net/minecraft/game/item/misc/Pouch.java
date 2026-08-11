package net.minecraft.game.item.misc;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;

public class Pouch extends Item {
    private int[] lootTable = new int[]{
        ItemList.beak.id,
        ItemList.leg.id,
        ItemList.glodust.id,
        ItemList.teeth.id,
        ItemList.tongue.id,
        ItemList.eye.id,
        ItemList.tentacle.id,
        ItemList.shell.id,
        ItemList.pincer.id,
        ItemList.horn.id,
        ItemList.claw.id,
        ItemList.paw.id,
        ItemList.slimeBall.id,
        ItemList.ostrichFeather.id,
        BlockList.dandelion.id,
        BlockList.lilac.id,
        BlockList.rose.id,
        BlockList.petunia.id,
        BlockList.mushroomBrown.id,
        BlockList.mushroomRed.id,
        BlockList.mushroomGlowing.id,
        ItemList.gunpowder.id,
        ItemList.reeds.id,
        ItemList.seeds.id,
        ItemList.seedsCotton.id,
        ItemList.seedsMelon.id,
        ItemList.seedsPumpkin.id,
        ItemList.bamboo.id,
        ItemList.berry.id,
        4457
    };

    public Pouch(int itemid) {
        super(itemid);
    }

    @Override
    public ItemStack use(ItemStack stack, World world, Player player) {
        if (player.properties.gamemode != 1) {
            stack.stackSize--;
        }

        int amount = rand.nextInt(3) + 3;

        for (int i = 0; i < amount; i++) {
            if (!player.inventory.addItemToInventory(new ItemStack(this.lootTable[rand.nextInt(this.lootTable.length)], rand.nextInt(3) + 3))) {
                player.dropPlayerItem(new ItemStack(this.lootTable[rand.nextInt(this.lootTable.length)], rand.nextInt(3) + 3));
            }
        }

        world.playEntitySound(player, "random.bundle", 1.0F, rand.nextFloat() * 0.4F + 0.8F);
        player.addStat(StatList.useStats[this.id], 1);
        return stack;
    }
}
