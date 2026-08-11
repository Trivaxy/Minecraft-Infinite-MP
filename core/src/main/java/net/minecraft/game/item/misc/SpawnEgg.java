package net.minecraft.game.item.misc;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.EntityManager;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;

public class SpawnEgg extends Item {
    public static List<Icon> icons = new ArrayList<>();
    public static final String[] spawnedEntities = new String[]{
        "Pig",
        "Sheep",
        "Cow",
        "Chicken",
        "Zombie",
        "Skeleton",
        "Spider",
        "Creeper",
        "Decoy",
        "Slime",
        "Wolf",
        "Bunny",
        "Fox",
        "Fish",
        "Jellyfish",
        "Ocelot",
        "Antlion",
        "Horse",
        "Shark",
        "Deer",
        "Goat",
        "Bird",
        "Dolphin",
        "Ostrich",
        "Parrot",
        "Vulture",
        "Camel",
        "Bear",
        "Monkey",
        "Penguin",
        "Frog",
        "Turtle",
        "Alligator",
        "Meerkat",
        "Seal",
        "Mooshroom",
        "Moobloom",
        "Bat",
        "Crab",
        "Spectre",
        "ZombieHorse",
        "SkeletonHorse",
        "Silverfish",
        "Ogre",
        "Termite",
        "Mummy",
        "Guardian",
        "Pharaoh",
        "Stray",
        "Lich",
        "LichKing",
        "Flood",
        "Treant",
        "Bogged",
        "Ghast",
        "ZombiePigman",
        "Imp",
        "MagmaCube",
        "Blaze",
        "Lurker",
        "Witch",
        "Inferno",
        "Breeze",
        "PigMan",
        "Harpy",
        "Pegasus",
        "Wisp",
        "HarpyQueen",
        "Priest",
        "Stalker",
        "Fae",
        "Elkir",
        "Llama",
        "CreeperFire",
        "CreeperFrost",
        "CreeperWind",
        "CreeperMagic",
        "Stratogel"
    };

    public SpawnEgg(int itemid) {
        super(itemid);
        this.setHasSubtypes(true);
        this.maxDamage = 0;
        this.priorityItem = true;
    }

    @Override
    public int getMaxDamage() {
        return 77;
    }

    @Override
    public String getNameFromMeta(int damage) {
        return "item.egg_" + damage;
    }

    @Override
    public Icon getIconFromMeta(int meta) {
        return icons.get(meta);
    }

    @Override
    public void registerIcons(IconRegister ir) {
        super.registerIcons(ir);

        for (int i = 0; i <= this.getMaxDamage(); i++) {
            icons.add(ir.registerIcon("eggs/egg_" + i));
        }
    }

    @Override
    public void getSubItems(int id, CreativeTabs ct, List<ItemStack> ls) {
        for (int i = 0; i <= this.getMaxDamage(); i++) {
            ls.add(new ItemStack(id, 1, i));
        }
    }

    @Override
    public final boolean useOnBlock(ItemStack stack, Player player, World world, int x, int y, int z, int side) {
        int block = world.getBlockId(x, y, z);
        if (side == 0) {
            y--;
        }

        if (side == 1) {
            y++;
        }

        if (side == 2) {
            z--;
        }

        if (side == 3) {
            z++;
        }

        if (side == 4) {
            x--;
        }

        if (side == 5) {
            x++;
        }

        if (world.getBlockId(x, y, z) == 0 || world.getBlockMaterial(x, y, z) == MaterialList.water) {
            Entity entityToSpawn = EntityManager.createEntity(spawnedEntities[stack.getItemDamage()], world);
            if (entityToSpawn != null && !world.isOnline) {
                entityToSpawn.teleportTo(x + 0.5, y + BlockList.blocks[block].maxY, z + 0.5, 0.0F, 0.0F);
                world.spawnEntity(entityToSpawn);
                if (player.properties.gamemode != 1) {
                    stack.stackSize--;
                }

                player.addStat(StatList.useStats[this.id], 1);
            }
        }

        return true;
    }
}
