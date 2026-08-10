package net.minecraft.commands.core.world;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.localization.Translator;

public class SeedCommand extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        src.print("§f" + Translator.getInstance().translateKey("commands.seed.set") + ": " + "§a" + ca.getWorld(0).getSeed());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection("" + ca.getWorld(0).getSeed());
        clipboard.setContents(stringSelection, null);
    }

    @Override
    public String name() {
        return "seed";
    }

    @Override
    public Command.UserType requiredUserType() {
        return Command.UserType.USER;
    }

    @Override
    public Command.EnvType envType() {
        return Command.EnvType.BOTH;
    }

    @Override
    public String help() {
        return Translator.getInstance().translateKey("commands.seed");
    }
}
