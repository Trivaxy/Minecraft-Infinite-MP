package net.minecraft.server.commands;

import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.tileentity.container.ChestEntity;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;

public class CommandDevItems extends Command {
    public static boolean disabled = false;

    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        Player player = ca.getConfigManager().getPlayer(src.getUsername());
        if (disabled) {
            src.print("fuck you.");
        } else if (args.length == 1) {
            World w = ca.getWorld(0);
            if (player != null) {
                int x = (int)player.posX;
                int z = (int)player.posZ;
                int y = w.getHeightmap(x, z);
                w.setBlockWithNotify(x, y, z, BlockList.chest.id);
                ChestEntity tileEntityChest7 = (ChestEntity)w.getTileEntity(x, y, z);
                int chestSlot = 0;

                for (int i = 0; i < 256; i++) {
                    if (BlockList.blocks[i] != null) {
                        if (tileEntityChest7 != null) {
                            tileEntityChest7.setSlotContents(chestSlot, new ItemStack(BlockList.blocks[i], 64));
                        }

                        if (++chestSlot == 27) {
                            chestSlot = 0;
                            x += 2;
                            y = w.getHeightmap(x, z);
                            w.setBlockWithNotify(x, y, z, BlockList.chest.id);
                            tileEntityChest7 = (ChestEntity)w.getTileEntity(x, y, z);
                        }
                    }
                }

                for (int var17 = 0; var17 < ItemList.items.length; var17++) {
                    if (ItemList.items[var17] != null) {
                        if (tileEntityChest7 != null) {
                            tileEntityChest7.setSlotContents(chestSlot, new ItemStack(ItemList.items[var17], ItemList.items[var17].getStackLimit()));
                        }

                        if (++chestSlot == 27) {
                            chestSlot = 0;
                            x += 2;
                            y = w.getHeightmap(x, z);
                            w.setBlockWithNotify(x, y, z, BlockList.chest.id);
                            tileEntityChest7 = (ChestEntity)w.getTileEntity(x, y, z);
                        }
                    }
                }
            }
        } else {
            if (player != null) {
                if (CommandUseTimer.wooded.containsKey(player.username)) {
                    src.print("You must wait a while before using this command again.");
                } else {
                    CommandUseTimer.wooded.put(player.username, 28000);

                    for (int i = 0; i < ItemList.items.length; i++) {
                        Item itm = ItemList.items[i];
                        if (itm != null) {
                            if (itm.hasSubtypes()) {
                                for (int j = 0; j < itm.getMaxDamage(); j++) {
                                    player.dropPlayerItem(new ItemStack(itm, itm.getStackLimit(), j), true);
                                }
                            }

                            player.dropPlayerItem(new ItemStack(itm, itm.getStackLimit()), true);
                        }
                    }
                }
            } else {
                src.print("Player not found");
            }
        }
    }

    @Override
    public String name() {
        return "devitems";
    }

    @Override
    public Command.UserType requiredUserType() {
        return Command.UserType.USER;
    }

    @Override
    public Command.EnvType envType() {
        return Command.EnvType.SERVER;
    }

    @Override
    public String help() {
        return "test command to give all items";
    }
}
