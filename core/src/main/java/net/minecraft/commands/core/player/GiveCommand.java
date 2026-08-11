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

public class GiveCommand extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource cs) {
        if (args.length != 2 && args.length != 1) {
            if (args.length != 3) {
                throw new CommandException.NotEnoughArgs(Translator.getInstance().translateKey("commands.base.error.notenoughargs"));
            }

            String username = args[0];
            String id = args[1];
            String cnt = args[2];
            this.giveItem(username, id, cnt, ca, cs);
        } else {
            String id = args[0];
            String count = null;
            if (args.length == 2) {
                count = args[1];
            }

            if (!this.isNumber(id) && !id.contains(":")) {
                this.giveItem(id, count, null, ca, cs);
                return;
            }

            this.giveItem(cs.getUsername(), id, count, ca, cs);
        }
    }

    private void giveItem(String playername, String item, String cnt, CommandAccessor ca, CommandSource cs) {
        int id = 0;
        int data = 0;
        int count = 0;
        Player player = ca.getConfigManager().getPlayer(playername);
        if (player == null) {
            cs.print(Translator.getInstance().translateKey("commands.inv.noplayer"));
        } else {
            String[] ids = item.split(":");
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
                    id = parseInt(ids[0]);
                    data = 0;
                } catch (Exception ex) {
                    for (int i = 0; i < ItemList.items.length; i++) {
                        if (ItemList.items[i] != null) {
                            if (ItemList.items[i].hasSubtypes()) {
                                for (int meta = 0; meta < ItemList.items[i].getMaxDamage(); meta++) {
                                    if (item.equalsIgnoreCase(ItemList.items[i].getNameFromMeta(meta))) {
                                        id = ItemList.items[i].id;
                                        data = meta;
                                        break;
                                    }
                                }
                            } else if (item.equalsIgnoreCase(ItemList.items[i].fullName)) {
                                id = ItemList.items[i].id;
                                data = 0;
                                break;
                            }
                        }
                    }
                }
            }

            if (cnt != null) {
                count = parseInt(cnt);
            } else {
                count = 64;
            }

            if (count < 1) {
                count = 1;
            }

            if (data < 0) {
                data = 0;
            }

            if (count > 64) {
                count = 64;
            }

            if (!checkItemId(id, data)) {
                sendNoticeToOps(ca, cs, String.format(Translator.getInstance().translateKey("commands.give.success"), player.username, count, id, data));
                if (!player.inventory.addItemToInventory(new ItemStack(id, count, data))) {
                    player.dropPlayerItem(new ItemStack(id, count, data));
                }
            } else {
                cs.print(Translator.getInstance().translateKey("commands.give.error") + " " + id + ":" + data);
            }
        }
    }

    @Override
    public String name() {
        return "give";
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
        return "<id | name> " + Translator.getInstance().translateKey("commands.give");
    }

    @Override
    public List<String> additionalHelp() {
        return Arrays.asList(
            "<id | name> <count> " + Translator.getInstance().translateKey("commands.give.count"),
            "<id:metadata> " + Translator.getInstance().translateKey("commands.give.metadata"),
            "<id:metadata> <count> " + Translator.getInstance().translateKey("commands.give.full"),
            "<player> <id | name> <count> " + Translator.getInstance().translateKey("commands.give.player"),
            "<player> <id:metadata> <count> " + Translator.getInstance().translateKey("commands.give.player.full")
        );
    }

    private boolean isNumber(String input) {
        int i = Integer.MIN_VALUE;

        try {
            i = parseInt(input);
        } catch (Exception var4) {
        }

        return i != Integer.MIN_VALUE;
    }
}
