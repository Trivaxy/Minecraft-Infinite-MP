package net.minecraft.smoketest;

import net.minecraft.network.packet.misc.ChatPacket;

/**
 * /tp and the coordinate parser behind it. Needs "tpbot" in run/server/ops.txt.
 *
 * The bot never calls sink(), so between teleports it holds the position the
 * server accepted and every ~ below is measured against a position both ends
 * agree on. The cost is that a bot which never descends is a bot NetServerHandler
 * counts as floating, and 80 such ticks are a kick, so each phase re-anchors with
 * an absolute teleport after a short settle() spends the counter.
 *
 * Nothing here can catch the eye-offset half of ~: EntityPlayerMP sets yOffset to
 * 0, so server-side posY is already the feet, and CommandSourceStack subtracting
 * it is a no-op on this side of the wire. It is single-player, where LocalPlayer
 * keeps yOffset at 1.62, that a raw posY would make /tp ~ ~ ~ climb.
 *
 * Usage: TpSmokeTest <host> <port> [username]
 */
public class TpSmokeTest {

    private static final Checks checks = new Checks();

    private static double anchorX;
    private static double anchorY;
    private static double anchorZ;

    public static void main(String[] args) throws Exception {
        String username = args.length > 2 ? args[2] : "tpbot";
        Bot bot = Bot.join(args, username);
        checks.note("logged in as " + username);
        bot.settle(1500);

        anchorX = bot.posX;
        anchorY = bot.posY;
        anchorZ = bot.posZ;
        checks.note(String.format("settled at %.2f %.2f %.2f", anchorX, anchorY, anchorZ));

        if (!anchor(bot)) {
            checks.abort("`/tp` moved nothing - is \"" + username + "\" in run/server/ops.txt?");
            return;
        }
        checks.check("three absolute coordinates land exactly there",
                at(bot, anchorX, anchorY, anchorZ), where(bot));

        tp(bot, "/tp ~ ~5 ~");
        checks.check("~ keeps the coordinate it stands in and ~5 offsets it",
                at(bot, anchorX, anchorY + 5.0, anchorZ), where(bot));

        tp(bot, "/tp ~ ~-5 ~");
        checks.check("and a negative offset brings it back down",
                at(bot, anchorX, anchorY, anchorZ), where(bot));

        bot.settle(400);
        anchor(bot);

        tp(bot, "/tp ~ ~ ~");
        checks.check("a bare ~ ~ ~ is a no-op",
                at(bot, anchorX, anchorY, anchorZ), where(bot));

        tp(bot, "/tp ~-2.5 ~ ~1.25");
        checks.check("an offset can be fractional",
                at(bot, anchorX - 2.5, anchorY, anchorZ + 1.25), where(bot));

        bot.settle(400);
        checks.check("the sender is told where they went",
                said(bot, "/tp ~ ~ ~", "Teleported to"));

        bot.settle(400);
        checks.check("two coordinates are refused, not read as a partial position",
                said(bot, "/tp 1 2", "Expected three coordinates"));

        bot.settle(400);
        checks.check("and so is a coordinate that is not a number",
                said(bot, "/tp here there everywhere", "Expected"));

        if (bot.kickReason != null) {
            checks.abort("kicked mid-run: " + bot.kickReason);
        }

        checks.done();
    }

    private static boolean anchor(Bot bot) throws Exception {
        bot.chat(String.format("/tp %s %s %s", anchorX, anchorY, anchorZ));
        return bot.followTeleport(3000);
    }

    private static void tp(Bot bot, String command) throws Exception {
        bot.chat(command);
        bot.followTeleport(3000);
    }

    private static boolean at(Bot bot, double x, double y, double z) {
        return Math.abs(bot.posX - x) < 0.01 && Math.abs(bot.posY - y) < 0.01 && Math.abs(bot.posZ - z) < 0.01;
    }

    private static String where(Bot bot) {
        return String.format("at %.2f %.2f %.2f", bot.posX, bot.posY, bot.posZ);
    }

    /** Runs a command and reports whether any chat that followed contained the fragment. */
    private static boolean said(Bot bot, String command, String fragment) throws Exception {
        bot.chat(command);
        return bot.awaitLast(ChatPacket.class, p -> p.message != null && p.message.contains(fragment), 1500) != null;
    }
}
