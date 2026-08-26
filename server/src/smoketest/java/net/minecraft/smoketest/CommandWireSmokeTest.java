package net.minecraft.smoketest;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.network.packet.misc.ChatPacket;

/**
 * The command path over the wire, against a running server.
 *
 * The framework probe (CommandFrameworkSmokeTest) runs the dispatcher directly and cannot
 * tell whether NetServerHandler is still connected to it. This sends a real ChatPacket
 * beginning with '/' and reads the server's answer back.
 *
 * Every command was deleted in the brigadier port, so the only correct answer to any
 * command at all is the unknown-command message - and the server must stay up.
 *
 *   ./gradlew :server:runServer            (in another terminal)
 *   ./gradlew :server:smokeTest -PsmokeClass=net.minecraft.smoketest.CommandWireSmokeTest
 *
 * No op needed.
 */
public class CommandWireSmokeTest {

    private static final Predicate<ChatPacket> ANY = new Predicate<ChatPacket>() {
        @Override
        public boolean test(ChatPacket p) {
            return true;
        }
    };

    public static void main(String[] args) throws Exception {
        Checks checks = new Checks();

        Bot bot = Bot.join(args, "cmdbot");
        bot.settle(1500);

        // an unknown command comes back as chat, addressed to the sender alone
        List<ChatPacket> replies = collect(bot, "/thereisnosuchcommand", 3000);
        checks.check("the server answers a command over the wire", !replies.isEmpty(),
                "no ChatPacket came back at all - NetServerHandler is not reaching the dispatcher");
        checks.check("and the answer is the unknown-command message",
                said(replies, "Unknown command"), text(replies));

        // a bare slash must not throw: perform() trims to empty and returns
        List<ChatPacket> bare = collect(bot, "/", 1500);
        checks.check("a bare slash is harmless", bot.kickReason == null, "kicked: " + bot.kickReason);
        checks.note("bare slash replied: " + text(bare));

        // whitespace-only, the case CommandsManager's split(" ") turned into a [""] argv
        collect(bot, "/    ", 1500);
        checks.check("a whitespace-only command is harmless", bot.kickReason == null, "kicked: " + bot.kickReason);

        // ordinary chat must still be ordinary chat rather than a parse attempt
        List<ChatPacket> spoken = collect(bot, "hello", 2000);
        checks.check("ordinary chat still relays", said(spoken, "hello"), text(spoken));

        checks.check("the server is still up at the end", bot.kickReason == null, "kicked: " + bot.kickReason);

        checks.done();
    }

    private static List<ChatPacket> collect(Bot bot, String message, long windowMs) throws Exception {
        bot.chat(message);
        return bot.awaitAll(ChatPacket.class, ANY, windowMs);
    }

    private static boolean said(List<ChatPacket> packets, String needle) {
        for (ChatPacket p : packets) {
            if (p.message != null && p.message.toLowerCase().contains(needle.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    private static String text(List<ChatPacket> packets) {
        StringBuilder sb = new StringBuilder();

        for (ChatPacket p : packets) {
            sb.append('[').append(p.message).append(']');
        }

        return sb.length() == 0 ? "(nothing)" : sb.toString();
    }
}
