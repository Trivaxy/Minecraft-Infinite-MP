package net.minecraft.game.block.utility;

import java.util.Random;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.block.tileentity.other.SpawnerEntity;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.misc.SpawnEgg;
import net.minecraft.game.world.World;

public class MobSpawner extends ContainerBlock {
    public MobSpawner(int blockid) {
        super(blockid, MaterialList.rock);
        this.silkTouch = false;
    }

    @Override
    public TileEntity getBlockEntity() {
        return new SpawnerEntity();
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public boolean particles3D() {
        return false;
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public boolean blockActivated(World world, int x, int y, int z, Player player) {
        SpawnerEntity spawner = (SpawnerEntity)world.getTileEntity(x, y, z);
        if (world.isOnline) {
            return true;
        } else if (player.inventory.getOffHandItem() != null && player.inventory.getOffHandItem().getItem() == ItemList.spawnEgg) {
            spawner.mobID = SpawnEgg.spawnedEntities[player.inventory.getOffHandItem().itemDmg];
            return true;
        } else if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == ItemList.spawnEgg) {
            spawner.mobID = SpawnEgg.spawnedEntities[player.getCurrentEquippedItem().itemDmg];
            return true;
        } else {
            return false;
        }
    }
}
