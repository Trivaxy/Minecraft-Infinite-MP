package net.minecraft.smoketest;

import net.minecraft.network.packet.misc.ChatPacket;
import net.minecraft.network.packet.player.ScorePacket;

/**
 * /score add|remove|set &lt;player&gt; &lt;amount&gt;, on the wire. Needs "scorebot" in
 * run/server/ops.txt, and needs "sidekick" NOT to be in it.
 *
 * In multiplayer the command runs server-side and writes the field on
 * EntityPlayerMP; the value reaches the player through the per-movement diff
 * in onUpdateEntity (score against lastScore, sent as a ScorePacket), so a
 * change is only observable once the bot's movement packets are accepted.
 *
 * No settle(): the spawn in this world is solid ground, so a bot that simply
 * holds the position the server accepted at login keeps hasMoved armed and is
 * never kicked as floating. settle() is the opposite here - its sink walks
 * 0.2 down BEFORE each send, so once the login corrective teleport has
 * cleared hasMoved the bot never again echoes lastPos within the 0.1 re-arm
 * tolerance, and the server silently ignores every packet it sends for the
 * rest of the run (85 measured in one probe: no ticks, no kicks, no score
 * sync, and chat replies still arriving because handleChat is ungated).
 *
 * The first ScorePacket after login is a sentinel: lastScore starts at
 * -99999999, so the first armed tick always sends the score the player
 * actually logged in with, whatever a previous run left behind. Every
 * expected value below is therefore relative to a state this test pinned
 * itself - a set to a value no run of this test leaves behind - because a
 * set to the current score sends nothing (score == lastScore) and would turn
 * the packet checks into coin flips.
 *
 * Run: ./gradlew :server:smokeTest -PsmokeClass=net.minecraft.smoketest.ScoreSmokeTest
 */
public class ScoreSmokeTest {

    private static final String OP = "scorebot";
    private static final String PLAIN = "sidekick";

    // Pinned first, so every later set/add/remove is a real change. No run of
    // this test ends on this value, so it can never already be the score.
    private static final int PIN = 777;

    private static final Checks checks = new Checks();

    public static void main(String[] args) throws Exception {
        Bot op = Bot.join(args, OP);
        checks.note("logged in as " + OP);
        ScorePacket baseline = op.await(ScorePacket.class, 15000);
        if (op.kickReason != null) {
            checks.abort("kicked before the test began: " + op.kickReason);
        }
        if (baseline == null) {
            checks.abort("no baseline ScorePacket in 15s - the server is not ticking " + OP
                    + " (server down, or a wedged save, see B-38c)");
            return;
        }
        checks.note("baseline score " + baseline.score);
        op.drain(1500);

        Bot plain = Bot.join(args, PLAIN);
        plain.drain(1500);
        if (plain.kickReason != null) {
            checks.abort(PLAIN + " was kicked before the test began: " + plain.kickReason);
        }

        // ---------------- the permission gate
        plain.chat("/score set " + OP + " 999");
        checks.check("a plain player is refused",
                said(plain, "do not have permission"), "no refusal came back");
        checks.check("and the refused command changed nothing",
                op.awaitLast(ScorePacket.class, p -> p.score == 999, 1500) == null,
                "a ScorePacket of 999 reached " + OP + " anyway");

        // ---------------- the arithmetic, on the wire
        op.chat("/score set " + OP + " " + PIN);
        checks.check("set writes a new score", arrived(op, PIN), "no ScorePacket of " + PIN);

        op.chat("/score set " + OP + " 100");
        checks.check("and set overwrites, down as well as up", arrived(op, 100), "no ScorePacket of 100");

        op.chat("/score add " + OP + " 25");
        checks.check("add adds", arrived(op, 125), "no ScorePacket of 125");

        op.chat("/score remove " + OP + " 30");
        checks.check("remove subtracts", arrived(op, 95), "no ScorePacket of 95");

        op.chat("/score remove " + OP + " 1000");
        checks.check("and remove stops at zero", arrived(op, 0), "no ScorePacket of 0 - it went negative");

        // A named player's score reaches that player's own client.
        op.chat("/score set " + PLAIN + " 42");
        checks.check("and the named player is the one who receives it",
                plain.await(ScorePacket.class, p -> p.score == 42, 3000) != null,
                "no ScorePacket of 42 reached " + PLAIN);
        op.chat("/score set " + PLAIN + " 0");

        // ---------------- the sender's own chat
        op.chat("/score add " + OP + " 1");
        checks.check("the sender is told the add's result",
                said(op, "Added 1 to " + OP + "'s score: 1"), "no such chat line");

        op.chat("/score remove " + OP + " 1");
        checks.check("the sender is told the remove's result",
                said(op, "Removed 1 from " + OP + "'s score: 0"), "no such chat line");

        op.chat("/score set " + OP + " 50");
        checks.check("and the set's", said(op, "Set " + OP + "'s score to 50"), "no such chat line");

        // ---------------- the rejections
        checks.check("an unknown player is named back",
                said(op, "/score set notaplayer 5", "No such player"), "no such chat line");
        checks.check("a negative amount is refused",
                said(op, "/score add " + OP + " -5", "must be 0 or greater"), "no such chat line");
        checks.check("and so is an amount that is not a number",
                said(op, "/score set " + OP + " here", "Expected integer"), "no such chat line");
        checks.check("a missing amount is a syntax error, not a guess",
                said(op, "/score add " + OP, "Incorrect command"), "no such chat line");
        checks.check("and the usage is spelled out with it",
                said(op, "/score give 200", "/score add <player> <amount>"), "no such chat line");

        // ---------------- close
        if (op.kickReason != null) {
            checks.abort("kicked mid-run: " + op.kickReason);
        }

        checks.done();
    }

    /** The wire half of a phase: a ScorePacket of exactly this value arrives. */
    private static boolean arrived(Bot bot, int score) throws Exception {
        return bot.awaitLast(ScorePacket.class, p -> p.score == score, 3000) != null;
    }

    /** Runs a command and reports whether any chat that followed contained the fragment. */
    private static boolean said(Bot bot, String command, String fragment) throws Exception {
        bot.chat(command);
        return bot.awaitLast(ChatPacket.class, p -> p.message != null && p.message.contains(fragment), 2000) != null;
    }

    /** Reports whether any chat the bot read contained the fragment. */
    private static boolean said(Bot bot, String fragment) throws Exception {
        return bot.awaitLast(ChatPacket.class, p -> p.message != null && p.message.contains(fragment), 2000) != null;
    }
}
