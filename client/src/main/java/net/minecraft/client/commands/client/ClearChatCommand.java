package net.minecraft.client.commands.client;

import net.minecraft.client.Minecraft;
import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.localization.Translator;

public class ClearChatCommand extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        Minecraft.getMinecraft().HUD.chatOverlay.clearChatMessages();
    }

    @Override
    public String name() {
        return "clearchat";
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
        return Translator.getInstance().translateKey("commands.chat.clear");
    }
}
