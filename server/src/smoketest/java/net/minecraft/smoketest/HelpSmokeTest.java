package net.minecraft.smoketest;

import com.mojang.brigadier.CommandDispatcher;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.commands.CommandOutput;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.game.physics.Vector;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.ServerCommands;

/**
 * /help, with no server and no game.
 *
 * What this pins down is the part that is easy to get wrong: /help must filter by requires()
 * the same way dispatch does, in both the listing and the per-command view.
 *
 *   ./gradlew :server:smokeTest -PsmokeClass=net.minecraft.smoketest.HelpSmokeTest
 */
public class HelpSmokeTest {

    private static final class Sink implements CommandOutput {
        final List<String> lines = new ArrayList<>();

        @Override
        public void sendCommandMessage(String message) {
            lines.add(message);
        }

        String last() {
            return lines.isEmpty() ? "" : lines.get(lines.size() - 1);
        }

        String all() {
            return lines.toString();
        }
    }

    private static List<String> names(Sink sink) {
        List<String> names = new ArrayList<>();
        for (String line : sink.lines) {
            int slash = line.indexOf('/');
            if (slash < 0) {
                continue;
            }

            String rest = line.substring(slash + 1);
            int space = rest.indexOf(' ');
            String name = space < 0 ? rest : rest.substring(0, space);
            if (names.isEmpty() || !names.get(names.size() - 1).equals(name)) {
                names.add(name);
            }
        }

        return names;
    }

    private static CommandSourceStack source(Sink sink, int level) {
        return new CommandSourceStack(
                sink, "probe", level, null, null,
                Vector.createVector(0.0, 64.0, 0.0), 0.0F, 0.0F, null, true);
    }

    public static void main(String[] args) throws Exception {
        Checks checks = new Checks();

        Commands commands = new Commands();
        CommandDispatcher<CommandSourceStack> dispatcher = commands.getDispatcher();
        ServerCommands.register(dispatcher, new MinecraftServer() {
            @Override
            public void initiateShutdown() {
            }
        });

        Sink opSink = new Sink();
        int listed = commands.perform("help", source(opSink, CommandSourceStack.LEVEL_OP));
        checks.check("/help lists every command an op can run", listed == 11, "listed " + listed + ": " + opSink.all());
        checks.check("and lists them in a stable order",
                names(opSink).equals(Arrays.asList(
                        "ban", "deop", "gamemode", "give", "help", "kick", "op", "score", "stop", "tp", "unban")),
                names(opSink).toString());
        checks.check("and spells out the usage of each, not just the name",
                opSink.all().contains("/score set <player> <amount>") && opSink.all().contains("/kick <player>"),
                opSink.all());

        Sink plainSink = new Sink();
        int plainListed = commands.perform("help", source(plainSink, CommandSourceStack.LEVEL_ALL));
        checks.check("a non-op is shown only what they can run", plainListed == 1, "listed " + plainListed + ": " + plainSink.all());
        checks.check("and op commands are not named to them",
                !plainSink.all().contains("/give") && !plainSink.all().contains("/kick")
                        && !plainSink.all().contains("/ban") && !plainSink.all().contains("/stop")
                        && !plainSink.all().contains("/tp") && !plainSink.all().contains("/op")
                        && !plainSink.all().contains("/gamemode"), plainSink.all());

        Sink usageSink = new Sink();
        commands.perform("help give", source(usageSink, CommandSourceStack.LEVEL_OP));
        checks.check("/help <command> shows every usage form",
                usageSink.all().contains("/give <item>") && usageSink.all().contains("/give <player> <item>"), usageSink.all());
        checks.check("and nothing but usage lines",
                usageSink.lines.size() == 3, usageSink.all());

        Sink noArgSink = new Sink();
        commands.perform("help stop", source(noArgSink, CommandSourceStack.LEVEL_OP));
        checks.check("a command with no arguments prints one bare usage line",
                noArgSink.all().contains("/stop") && !noArgSink.all().contains("/stop "), noArgSink.all());

        Sink hiddenSink = new Sink();
        commands.perform("help stop", source(hiddenSink, CommandSourceStack.LEVEL_ALL));
        checks.check("/help does not confirm a command the sender may not run",
                hiddenSink.last().contains("Unknown command"), hiddenSink.all());
        checks.check("and does not leak it as a permission failure either",
                !hiddenSink.last().contains("permission"), hiddenSink.all());

        Sink missingSink = new Sink();
        commands.perform("help thereisnosuchcommand", source(missingSink, CommandSourceStack.LEVEL_OP));
        checks.check("an unknown command name is refused",
                missingSink.last().contains("Unknown command"), missingSink.all());

        Sink selfSink = new Sink();
        commands.perform("help help", source(selfSink, CommandSourceStack.LEVEL_ALL));
        checks.check("/help describes itself",
                selfSink.all().contains("/help <command>"), selfSink.all());

        checks.done();
    }
}
