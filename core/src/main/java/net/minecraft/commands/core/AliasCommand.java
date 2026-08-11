package net.minecraft.commands.core;

import java.util.List;
import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.localization.Translator;

public final class AliasCommand extends Command {
    private Command aliascommand;
    private String aliasname;

    public AliasCommand(Command cmd, String cname) {
        this.aliascommand = cmd;
        this.aliasname = cname;
    }

    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource source) {
        this.aliascommand.action(args, ca, source);
    }

    @Override
    public String name() {
        return this.aliasname;
    }

    @Override
    public Command.UserType requiredUserType() {
        return this.aliascommand.requiredUserType();
    }

    @Override
    public Command.EnvType envType() {
        return this.aliascommand.envType();
    }

    @Override
    public String help() {
        return Translator.getInstance().translateKey("commands.alias") + ": " + this.aliascommand.name();
    }

    @Override
    public List<String> getAliases() {
        return null;
    }
}
