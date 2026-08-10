package net.minecraft.commands.core.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.enchantment.Enchantment;
import net.minecraft.game.item.enchantment.EnchantmentList;
import net.minecraft.localization.Translator;

public class EnchantCommand extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        Player player = ca.demandPlayer(src);
        ItemStack item = player.getCurrentEquippedItem();
        if (item != null) {
            if (item.isItemEnchanted() && "clear".equalsIgnoreCase(args[0])) {
                item.removeEnchant(item);
                src.print(Translator.getInstance().translateKey("commands.enchant.clear"));
                return;
            }

            if ("list".equalsIgnoreCase(args[0])) {
                for (int i = 0; i < EnchantmentList.enchantments.length; i++) {
                    if (EnchantmentList.enchantments[i] != null) {
                        src.print(EnchantmentList.enchantments[i].name.toUpperCase());
                    }
                }

                return;
            }

            demandArgCountOf(2, args);
            int level = parseInt(args[1]);
            if (level <= 0) {
                src.print(Translator.getInstance().translateKey("commands.enchant.error"));
                return;
            }

            boolean fail = false;

            for (int i = 0; i < EnchantmentList.enchantments.length; i++) {
                Enchantment enchant = EnchantmentList.enchantments[i];
                if (enchant != null) {
                    if (args[0].equalsIgnoreCase(enchant.name)) {
                        item.addEnchantment(enchant, level);
                        src.printf(Translator.getInstance().translateKey("commands.enchant.success"), args[0], level);
                        return;
                    }

                    fail = true;
                }
            }

            if (fail) {
                src.print(Translator.getInstance().translateKey("commands.enchant.unknown"));
            }
        } else {
            src.print(Translator.getInstance().translateKey("commands.enchant.fail"));
        }
    }

    @Override
    public String name() {
        return "enchant";
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
        return "<enchant> <level> " + Translator.getInstance().translateKey("commands.enchant");
    }

    @Override
    public List<String> additionalHelp() {
        return Arrays.asList(
            "<clear> " + Translator.getInstance().translateKey("commands.enchant.help"),
            "<list> " + Translator.getInstance().translateKey("commands.enchant.list")
        );
    }

    @Override
    public List<String[]> suggestions() {
        List<String[]> list = new ArrayList<>();
        List<String> enchantments = new ArrayList<>();

        for (int i = 0; i < EnchantmentList.enchantments.length; i++) {
            if (EnchantmentList.enchantments[i] != null) {
                enchantments.add(EnchantmentList.enchantments[i].name);
            }
        }

        enchantments.add("clear");
        enchantments.add("list");
        String[] effects = enchantments.toArray(new String[0]);
        list.add(effects);
        list.add(new String[]{"1", "2", "3", "4", "5"});
        return list;
    }
}
