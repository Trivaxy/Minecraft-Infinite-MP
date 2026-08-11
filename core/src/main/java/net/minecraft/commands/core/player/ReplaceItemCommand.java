package net.minecraft.commands.core.player;

import java.util.Arrays;
import java.util.List;
import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandException;
import net.minecraft.commands.CommandSource;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.localization.Translator;

public class ReplaceItemCommand extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        Player player = ca.demandPlayer(src);
        demandArgCountOf(3, args);
        int slot = parseInt(args[0]);
        int id = 0;
        int data = 0;
        int count = parseInt(args[2]);
        if (args.length == 4) {
            player = ca.getPlayer(args[3]);
            if (player == null) {
                throw new CommandException(Translator.getInstance().translateKey("commands.inv.noplayer"));
            }
        }

        String[] ids = args[1].split(":");
        if (ids.length == 2) {
            try {
                id = parseInt(ids[0]);
            } catch (Exception ex) {
                for (int i = 0; i < ItemList.items.length; i++) {
                    if (ItemList.items[i] != null && ids[0].equalsIgnoreCase(ItemList.items[i].fullName)) {
                        id = ItemList.items[i].id;
                        break;
                    }
                }
            }

            data = parseInt(ids[1]);
        } else {
            try {
                id = parseInt(args[1]);
                data = 0;
            } catch (Exception ex) {
                for (int i = 0; i < ItemList.items.length; i++) {
                    if (ItemList.items[i] != null) {
                        if (ItemList.items[i].hasSubtypes()) {
                            for (int meta = 0; meta < ItemList.items[i].getMaxDamage(); meta++) {
                                if (args[1].equalsIgnoreCase(ItemList.items[i].getNameFromMeta(meta))) {
                                    id = ItemList.items[i].id;
                                    data = meta;
                                    break;
                                }
                            }
                        } else if (args[1].equalsIgnoreCase(ItemList.items[i].fullName)) {
                            id = ItemList.items[i].id;
                            data = 0;
                            break;
                        }
                    }
                }
            }
        }

        if (count < 1) {
            count = 1;
        }

        if (data < 0) {
            data = 0;
        }

        if (id < 1) {
            id = 1;
        }

        if (count > 64) {
            count = 64;
        }

        if (!checkItemId(id, data)) {
            player.inventory.setSlotContents(slot, new ItemStack(id, count, data));
            src.printf(Translator.getInstance().translateKey("commands.item.replace"), slot, id, count);
        } else {
            src.print(Translator.getInstance().translateKey("commands.give.error") + " " + id + ":" + data);
        }
    }

    @Override
    public String name() {
        return "replaceitem";
    }

    @Override
    public Command.UserType requiredUserType() {
        return Command.UserType.OPERATOR;
    }

    @Override
    public Command.EnvType envType() {
        return Command.EnvType.BOTH;
    }

    @Override
    public String help() {
        return "<slot> <id:metadata> <count> " + Translator.getInstance().translateKey("commands.replaceitem");
    }

    @Override
    public List<String> additionalHelp() {
        return Arrays.asList("<slot> <id:metadata> <count> <playername> " + Translator.getInstance().translateKey("commands.replaceitem.player"));
    }
}
