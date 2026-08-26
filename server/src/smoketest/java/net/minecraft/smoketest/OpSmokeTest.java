package net.minecraft.smoketest;

import net.minecraft.network.packet.misc.ChatPacket;

/**
 * /op and /deop, against the existing ops.txt. Needs "opbot" in run/server/ops.txt,
 * and needs "sidekick" NOT to be in it.
 *
 * The interesting half is not the confirmation line, it is that the permission a
 * source is built with is read fresh per command (NetServerHandler.createCommandSource
 * asks isOp every time), so an op granted mid-session takes effect on the sidekick's
 * very next command and a deop takes it away again. Both directions are checked by
 * making the sidekick run an op-gated command, not by trusting the chat.
 *
 * The op is written to ops.txt, so this test always deops what it opped.
 *
 * Two bots exist here, but only ever one of them is READING at a time: one bot per JVM
 * is a chunk-decode constraint on concurrent decoding (see Bot).
 *
 * Usage: OpSmokeTest <host> <port>
 */
public class OpSmokeTest {

    private static final String OP = "opbot";
    private static final String TARGET = "sidekick";

    private static final Checks checks = new Checks();

    public static void main(String[] args) throws Exception {
        Bot op = Bot.join(args, OP).sink();
        op.settle(1500);

        op.chat("/deop " + TARGET);
        if (!said(op, "not an operator")) {
            checks.abort("`/deop " + TARGET + "` said nothing - is \"" + OP + "\" in run/server/ops.txt,"
                    + " or was " + TARGET + " left opped by an earlier run?");
            return;
        }
        checks.check("deopping someone who is not an operator says so", true, "");

        Bot target = Bot.join(args, TARGET).sink();
        target.settle(1500);

        target.chat("/op " + TARGET);
        checks.check("a plain player cannot op themselves",
                said(target, "do not have permission"), "no refusal came back");

        op.chat("/op " + TARGET);
        checks.check("the sender is told who was opped",
                said(op, "Opped " + TARGET), "no chat said \"Opped " + TARGET + "\"");

        target.chat("/op " + TARGET);
        checks.check("and the op takes effect on the target's next command",
                said(target, "already an operator"), "they were still refused, or told nothing");

        op.chat("/deop " + TARGET);
        checks.check("the sender is told who was de-opped",
                said(op, "De-opped " + TARGET), "no chat said \"De-opped " + TARGET + "\"");

        target.chat("/op " + TARGET);
        checks.check("and the deop takes it away again",
                said(target, "do not have permission"), "they could still run an op command");
        target.close();

        op.chat("/deop " + TARGET);
        checks.check("deopping twice is refused, not a silent no-op",
                said(op, "not an operator"), "no second refusal came back");

        if (op.kickReason != null) {
            checks.abort("kicked mid-run: " + op.kickReason);
        }

        checks.done();
    }

    /** Whether any chat the bot read contained the fragment. */
    private static boolean said(Bot bot, String fragment) throws Exception {
        return bot.awaitLast(ChatPacket.class, p -> p.message != null && p.message.contains(fragment), 2000) != null;
    }
}
