package net.minecraft.smoketest;

import net.minecraft.network.packet.misc.ChatPacket;

/**
 * /kick. Needs "kickbot" in run/server/ops.txt.
 *
 * The bot kicks itself, because one bot per JVM is the only safe arrangement (see Bot).
 * That still exercises the whole path - resolve the name, answer the sender, drop the
 * connection - and it is why the command answers the sender BEFORE it kicks.
 *
 * Usage: KickSmokeTest <host> <port> [username]
 */
public class KickSmokeTest {

    private static final Checks checks = new Checks();

    public static void main(String[] args) throws Exception {
        String username = args.length > 2 ? args[2] : "kickbot";
        Bot bot = Bot.join(args, username).sink();
        bot.settle(1500);

        bot.chat("/kick notaplayer");
        boolean named = bot.awaitLast(ChatPacket.class,
                p -> p.message != null && p.message.contains("No such player"), 3000) != null;
        if (!named) {
            checks.abort("`/kick notaplayer` said nothing - is \"" + username + "\" in run/server/ops.txt?");
            return;
        }
        checks.check("an unknown player is named back", true, "");
        checks.check("naming an unknown player does not disconnect the sender",
                bot.kickReason == null, "kicked with: " + bot.kickReason);

        bot.chat("/kick " + username);
        boolean answered = bot.awaitLast(ChatPacket.class,
                p -> p.message != null && p.message.contains("Kicked " + username), 3000) != null;
        checks.check("the sender is told who was kicked", answered, "no chat said \"Kicked " + username + "\"");

        bot.drain(3000);
        checks.check("the target is disconnected", bot.kickReason != null, username + " is still connected");
        checks.check("the target is told why",
                bot.kickReason != null && bot.kickReason.contains("Kicked from the server"),
                "reason was: " + bot.kickReason);

        checks.done();
    }
}
