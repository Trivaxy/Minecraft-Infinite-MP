package net.minecraft.smoketest;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.commands.CommandOutput;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.BlockPosArgument;
import net.minecraft.commands.arguments.Coordinates;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.world.chunk.BlockPosition;

/**
 * The brigadier command framework, with no server and no game.
 *
 * There are no commands registered anywhere after the port, so this registers its own
 * scratch tree and asserts the wiring underneath one: dispatch, requires() gating, the
 * error cursor, and relative coordinates.
 *
 *   ./gradlew :server:smokeTest -PsmokeClass=net.minecraft.smoketest.CommandFrameworkSmokeTest
 */
public class CommandFrameworkSmokeTest {

    /** Collects whatever a command source is told, so a check can read it back. */
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

    private static CommandSourceStack source(Sink sink, int level) {
        return new CommandSourceStack(
                sink, "probe", level, null, null,
                Vector.createVector(10.0, 64.0, -20.0), 0.0F, 0.0F, null, true);
    }

    public static void main(String[] args) throws Exception {
        Checks checks = new Checks();

        Commands commands = new Commands();
        CommandDispatcher<CommandSourceStack> dispatcher = commands.getDispatcher();

        // ---- CoreCommands is wired in: /give is the first command back ----
        checks.check("give is registered",
                dispatcher.getRoot().getChild("give") != null,
                "root has " + dispatcher.getRoot().getChildren().size() + " commands");

        // ---- a scratch tree, standing in for whatever comes back first ----
        final int[] ran = {0};
        final BlockPosition[] seen = {null};

        dispatcher.register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("probe")
                        .then(LiteralArgumentBuilder.<CommandSourceStack>literal("open")
                                .executes(new com.mojang.brigadier.Command<CommandSourceStack>() {
                                    @Override
                                    public int run(CommandContext<CommandSourceStack> ctx) {
                                        ran[0]++;
                                        ctx.getSource().sendSuccess("open ok");
                                        return 1;
                                    }
                                }))
                        .then(LiteralArgumentBuilder.<CommandSourceStack>literal("opped")
                                .requires(new java.util.function.Predicate<CommandSourceStack>() {
                                    @Override
                                    public boolean test(CommandSourceStack src) {
                                        return src.hasPermission(CommandSourceStack.LEVEL_OP);
                                    }
                                })
                                .executes(new com.mojang.brigadier.Command<CommandSourceStack>() {
                                    @Override
                                    public int run(CommandContext<CommandSourceStack> ctx) {
                                        ran[0]++;
                                        return 1;
                                    }
                                }))
                        .then(LiteralArgumentBuilder.<CommandSourceStack>literal("count")
                                .then(RequiredArgumentBuilder.<CommandSourceStack, Integer>argument("n", IntegerArgumentType.integer(1, 10))
                                        .executes(new com.mojang.brigadier.Command<CommandSourceStack>() {
                                            @Override
                                            public int run(CommandContext<CommandSourceStack> ctx) {
                                                return IntegerArgumentType.getInteger(ctx, "n");
                                            }
                                        })))
                        .then(LiteralArgumentBuilder.<CommandSourceStack>literal("at")
                                .then(RequiredArgumentBuilder.<CommandSourceStack, Coordinates>argument("pos", BlockPosArgument.blockPos())
                                        .executes(new com.mojang.brigadier.Command<CommandSourceStack>() {
                                            @Override
                                            public int run(CommandContext<CommandSourceStack> ctx) {
                                                seen[0] = BlockPosArgument.getBlockPos(ctx, "pos");
                                                return 1;
                                            }
                                        }))));

        // ---- dispatch ----
        Sink sink = new Sink();
        CommandSourceStack player = source(sink, CommandSourceStack.LEVEL_ALL);
        commands.perform("probe open", player);
        checks.check("a registered command runs", ran[0] == 1, "ran " + ran[0] + " times");
        checks.check("its output reaches the sender", sink.last().equals("open ok"), sink.all());

        // ---- requires() gating ----
        int before = ran[0];
        Sink deniedSink = new Sink();
        commands.perform("probe opped", source(deniedSink, CommandSourceStack.LEVEL_ALL));
        checks.check("requires() keeps a non-op out", ran[0] == before, "body ran anyway");
        checks.check("and the refusal names permission, not an unknown command",
                deniedSink.last().contains("permission"), deniedSink.all());

        Sink hiddenRootSink = new Sink();
        commands.perform("give stone", source(hiddenRootSink, CommandSourceStack.LEVEL_ALL));
        checks.check("a whole command gated at its root reports permission too",
                hiddenRootSink.last().contains("permission"), hiddenRootSink.all());

        Sink oppedSink = new Sink();
        commands.perform("probe opped", source(oppedSink, CommandSourceStack.LEVEL_OP));
        checks.check("requires() lets an op through", ran[0] == before + 1, "ran " + ran[0]);

        // ---- the error cursor: the thing the old framework could not do at all ----
        boolean cursorAtBadToken = false;
        String cursorDetail = "no exception thrown";
        try {
            dispatcher.execute("probe count 99", player);
        } catch (CommandSyntaxException e) {
            cursorAtBadToken = e.getCursor() == "probe count ".length();
            cursorDetail = "cursor=" + e.getCursor() + " expected=" + "probe count ".length() + " context=" + e.getContext();
        }
        checks.check("a bad argument reports the cursor that produced it", cursorAtBadToken, cursorDetail);

        // an out-of-range int is a syntax error the sender hears about, where the old
        // dispatcher logged AIOOBE at WARNING and told the player nothing
        Sink rangeSink = new Sink();
        commands.perform("probe count 99", source(rangeSink, CommandSourceStack.LEVEL_ALL));
        checks.check("and perform() reports it rather than swallowing it",
                !rangeSink.lines.isEmpty(), "the sender was told nothing");

        // ---- unknown command ----
        Sink unknownSink = new Sink();
        commands.perform("thereisnosuchcommand", source(unknownSink, CommandSourceStack.LEVEL_ALL));
        checks.check("an unknown command says so", !unknownSink.lines.isEmpty(), "silence");
        checks.check("and is not mistaken for a permission failure",
                !unknownSink.last().contains("permission"), unknownSink.all());

        Sink badArgSink = new Sink();
        commands.perform("probe count 99", source(badArgSink, CommandSourceStack.LEVEL_OP));
        checks.check("a real argument error is not mistaken for a permission failure",
                !badArgSink.last().contains("permission"), badArgSink.all());

        // ---- a known command, used wrong, answers with its usage ----
        Sink wrongSubSink = new Sink();
        commands.perform("probe nosuchsub", source(wrongSubSink, CommandSourceStack.LEVEL_ALL));
        checks.check("a known command used wrong is not called unknown",
                !wrongSubSink.all().contains("Unknown command"), wrongSubSink.all());
        checks.check("it is called incorrect instead",
                wrongSubSink.all().contains("Incorrect command"), wrongSubSink.all());
        checks.check("and every usage form is spelled out",
                wrongSubSink.all().contains("/probe open") && wrongSubSink.all().contains("/probe count <n>")
                        && wrongSubSink.all().contains("/probe at <pos>"), wrongSubSink.all());
        checks.check("usage filters by requires() the way dispatch does",
                !wrongSubSink.all().contains("/probe opped"), wrongSubSink.all());

        Sink oppedUsageSink = new Sink();
        commands.perform("probe nosuchsub", source(oppedUsageSink, CommandSourceStack.LEVEL_OP));
        checks.check("and an op is shown the form they alone can run",
                oppedUsageSink.all().contains("/probe opped"), oppedUsageSink.all());

        // a complete parse that lands on a node with no executes() is the same mistake
        Sink bareSink = new Sink();
        commands.perform("probe", source(bareSink, CommandSourceStack.LEVEL_ALL));
        checks.check("a command stopped short of anything runnable gets usage too",
                bareSink.all().contains("Incorrect command") && bareSink.all().contains("/probe open"), bareSink.all());

        // the specific message is worth more than a usage dump, so it is kept
        checks.check("an argument that parsed and was rejected keeps its own message",
                !badArgSink.all().contains("Incorrect command"), badArgSink.all());

        checks.check("and an unknown command is still unknown, not incorrect",
                unknownSink.all().contains("Unknown command"), unknownSink.all());

        // ---- relative coordinates ----
        // the source stands at (10, 64, -20)
        commands.perform("probe at ~ ~2 ~", player);
        checks.check("`~` resolves against the source position",
                seen[0] != null && seen[0].x == 10 && seen[0].y == 66 && seen[0].z == -20,
                seen[0] == null ? "not parsed" : seen[0].x + "," + seen[0].y + "," + seen[0].z);

        seen[0] = null;
        commands.perform("probe at 1 2 3", player);
        checks.check("absolute coordinates are absolute",
                seen[0] != null && seen[0].x == 1 && seen[0].y == 2 && seen[0].z == 3,
                seen[0] == null ? "not parsed" : seen[0].x + "," + seen[0].y + "," + seen[0].z);

        // Command.parseRelativeCoords did input.replaceAll("~", "") and parsed the rest, so
        // "1~2" read as 12. brigadier's reader stops at the tilde instead.
        seen[0] = null;
        Sink junkSink = new Sink();
        commands.perform("probe at 1~2 2 3", source(junkSink, CommandSourceStack.LEVEL_ALL));
        checks.check("a malformed coordinate is rejected, not silently repaired",
                seen[0] == null, seen[0] == null ? "" : "parsed as " + seen[0].x);

        checks.done();
    }
}
