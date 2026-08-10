package net.minecraft.server.commands;

import java.util.Set;
import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;

public class CommandWhitelist extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        this.handleWhitelist(args, ca, src);
    }

    @Override
    public String name() {
        return "whitelist";
    }

    @Override
    public Command.UserType requiredUserType() {
        return Command.UserType.OPERATOR;
    }

    @Override
    public Command.EnvType envType() {
        return Command.EnvType.SERVER;
    }

    @Override
    public String help() {
        return "<on|off|add|remove|reload> <username> configures whitelist";
    }

    private void handleWhitelist(String[] argv, CommandAccessor ca, CommandSource src) {
        if (argv.length >= 1) {
            String string5 = argv[0].toLowerCase();
            if ("on".equals(string5)) {
                sendNoticeToOps(ca, src, "Turned on white-listing");
                ca.getProperties().set("white-list", true);
            } else if ("off".equals(string5)) {
                sendNoticeToOps(ca, src, "Turned off white-listing");
                ca.getProperties().set("white-list", false);
            } else if ("list".equals(string5)) {
                Set<String> set6 = ca.getConfigManager().getWhiteListedIPs();
                StringBuilder string7 = new StringBuilder();

                for (String s : set6) {
                    String string9 = s;
                    string7.append(string9).append(" ");
                }

                src.print("White-listed players: " + string7);
            } else if ("add".equals(string5) && argv.length == 2) {
                String string10 = argv[1].toLowerCase();
                ca.getConfigManager().whiteList(string10);
                sendNoticeToOps(ca, src, "Added " + string10 + " to white-list");
            } else if ("remove".equals(string5) && argv.length == 2) {
                String string10 = argv[1].toLowerCase();
                ca.getConfigManager().unWhiteList(string10);
                sendNoticeToOps(ca, src, "Removed " + string10 + " from white-list");
            } else if ("reload".equals(string5)) {
                ca.getConfigManager().reloadWhiteList();
                sendNoticeToOps(ca, src, "Reloaded white-list from file");
            } else {
                src.print("§4Unknown mode");
            }
        }
    }
}
