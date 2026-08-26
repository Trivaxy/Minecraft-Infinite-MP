package net.minecraft.smoketest;

import net.minecraft.network.packet.misc.ChatPacket;

/**
 * /ban and /unban, against the existing BanList. Needs "banbot" in run/server/ops.txt.
 *
 * The ban itself is only half the behaviour - the other half is the login refusal it
 * causes, which is server state that outlives the connection, so this test always
 * unbans what it banned and proves the pardon by logging back in.
 *
 * Two bots exist here, but only ever one of them is READING at a time: one bot per JVM
 * is a chunk-decode constraint on concurrent decoding (see Bot).
 *
 * Usage: BanSmokeTest <host> <port>
 */
public class BanSmokeTest {

    private static final String OP = "banbot";
    private static final String TARGET = "bannedbot";
    private static final String REASON = "no cheating please";

    private static final Checks checks = new Checks();

    public static void main(String[] args) throws Exception {
        Bot op = Bot.join(args, OP).sink();
        op.settle(1500);

        op.chat("/unban " + TARGET);
        boolean refused = op.awaitLast(ChatPacket.class,
                p -> p.message != null && p.message.contains("not banned"), 3000) != null;
        if (!refused) {
            checks.abort("`/unban " + TARGET + "` said nothing - is \"" + OP + "\" in run/server/ops.txt,"
                    + " or was " + TARGET + " left banned by an earlier run?");
            return;
        }
        checks.check("unbanning someone who is not banned says so", true, "");

        Bot target = Bot.join(args, TARGET).sink();
        target.settle(1500);
        checks.check("the target can log in before the ban", target.kickReason == null, target.kickReason);

        op.chat("/ban " + TARGET + " " + REASON);
        target.drain(3000);
        checks.check("banning an online player disconnects them", target.kickReason != null,
                TARGET + " is still connected");
        checks.check("and tells them the reason they were given",
                target.kickReason != null && target.kickReason.contains("banned from this server")
                        && target.kickReason.contains(REASON),
                "they were told: " + target.kickReason);
        target.close();

        checks.check("the sender is told who was banned",
                op.awaitLast(ChatPacket.class, p -> p.message != null && p.message.contains("Banned " + TARGET), 3000) != null,
                "no chat said \"Banned " + TARGET + "\"");

        checks.check("the ban outlives the connection and refuses the next login",
                loginRefused(args, REASON), "the ban did not stop " + TARGET + " logging back in");

        op.chat("/unban " + TARGET);
        checks.check("the sender is told who was unbanned",
                op.awaitLast(ChatPacket.class, p -> p.message != null && p.message.contains("Unbanned " + TARGET), 3000) != null,
                "no chat said \"Unbanned " + TARGET + "\"");

        Bot pardoned = Bot.join(args, TARGET).sink();
        pardoned.settle(1500);
        checks.check("and the pardoned player can log back in", pardoned.kickReason == null, pardoned.kickReason);
        pardoned.close();

        op.chat("/ban " + TARGET);
        op.drain(1000);
        checks.check("a ban with no reason falls back to BanEntry's default",
                loginRefused(args, "Banned by an operator"), "the no-reason ban did not refuse the login as expected");

        op.chat("/unban " + TARGET);
        checks.check("and that one is pardoned too",
                op.awaitLast(ChatPacket.class, p -> p.message != null && p.message.contains("Unbanned " + TARGET), 3000) != null,
                "no chat said \"Unbanned " + TARGET + "\"");

        Bot.closeAll();
        checks.done();
    }

    /** A login that the server refuses shows up as a throw out of Bot.join, carrying the reason. */
    private static boolean loginRefused(String[] args, String reason) {
        try {
            Bot.join(args, TARGET).close();
            return false;
        } catch (Exception e) {
            String message = String.valueOf(e.getMessage());
            checks.note("login refused with: " + message.replace('\n', ' '));
            return message.contains("banned") && message.contains(reason);
        }
    }
}
