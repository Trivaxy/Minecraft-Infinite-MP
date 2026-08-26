package net.minecraft.smoketest;

import com.mojang.brigadier.CommandDispatcher;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.commands.CommandOutput;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.game.physics.Vector;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.ServerCommands;

/**
 * /stop command smoke test.
 *
 * Tests /stop registration, permission gating, output messaging, and shutdown invocation.
 *
 *   ./gradlew :server:smokeTest -PsmokeClass=net.minecraft.smoketest.StopCommandSmokeTest
 */
public class StopCommandSmokeTest {

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
                Vector.createVector(0.0, 64.0, 0.0), 0.0F, 0.0F, null, true);
    }

    public static void main(String[] args) throws Exception {
        Checks checks = new Checks();

        Commands commands = new Commands();
        CommandDispatcher<CommandSourceStack> dispatcher = commands.getDispatcher();

        final boolean[] shutdownCalled = {false};
        MinecraftServer dummyServer = new MinecraftServer() {
            @Override
            public void initiateShutdown() {
                shutdownCalled[0] = true;
            }
        };

        ServerCommands.register(dispatcher, dummyServer);

        // 1. Check registration
        checks.check("stop is registered in the command tree",
                dispatcher.getRoot().getChild("stop") != null,
                "root child 'stop' not found");

        // 2. Non-op permission denied
        shutdownCalled[0] = false;
        Sink nonOpSink = new Sink();
        commands.perform("stop", source(nonOpSink, CommandSourceStack.LEVEL_ALL));
        checks.check("non-op cannot run /stop", !shutdownCalled[0], "shutdown was called by non-op");
        checks.check("non-op is refused", !nonOpSink.lines.isEmpty(), "the sender was told nothing");

        // 3. Op user allowed
        shutdownCalled[0] = false;
        Sink opSink = new Sink();
        commands.perform("stop", source(opSink, CommandSourceStack.LEVEL_OP));
        checks.check("op can run /stop", shutdownCalled[0], "shutdown was not called by op");
        checks.check("op receives stopping message",
                opSink.last().contains("Stopping the server..."), opSink.all());

        // 4. Console allowed
        shutdownCalled[0] = false;
        Sink consoleSink = new Sink();
        commands.perform("stop", source(consoleSink, CommandSourceStack.LEVEL_OWNER));
        checks.check("console can run /stop", shutdownCalled[0], "shutdown was not called by console");
        checks.check("console receives stopping message",
                consoleSink.last().contains("Stopping the server..."), consoleSink.all());

        // 5. Trailing arguments rejected
        shutdownCalled[0] = false;
        Sink extraArgSink = new Sink();
        commands.perform("stop extra", source(extraArgSink, CommandSourceStack.LEVEL_OP));
        checks.check("trailing arguments do not trigger shutdown", !shutdownCalled[0], "shutdown was called with trailing args");
        checks.check("trailing arguments report syntax error", !extraArgSink.lines.isEmpty(), "no error reported");

        checks.done();
    }
}
