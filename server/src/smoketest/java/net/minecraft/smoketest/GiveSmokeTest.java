package net.minecraft.smoketest;

import net.minecraft.network.packet.misc.ChatPacket;
import net.minecraft.network.packet.player.SlotPacket;

/**
 * /give, in both of its forms. Needs "givebot" in run/server/ops.txt.
 *
 * The self form and the named-player form are sibling argument branches under one literal,
 * so which one brigadier picks is decided by the parse alone - there is no literal to tell
 * them apart. That is the thing worth watching go red.
 *
 * Usage: GiveSmokeTest <host> <port> [username]
 */
public class GiveSmokeTest {

    private static final int SELF_ID = 4096;
    private static final int NAMED_ID = 4103;

    private static final Checks checks = new Checks();

    public static void main(String[] args) throws Exception {
        String username = args.length > 2 ? args[2] : "givebot";
        Bot bot = Bot.join(args, username).sink();
        checks.note("logged in as " + username);
        bot.settle(1500);

        bot.chat("/give " + SELF_ID);
        SlotPacket self = bot.awaitLast(SlotPacket.class,
                p -> p.windowId == 0 && p.myItemStack != null && p.myItemStack.itemID == SELF_ID, 3000);
        if (self == null) {
            checks.abort("`/give " + SELF_ID + "` landed nothing - is \"" + username + "\" in run/server/ops.txt?");
            return;
        }
        checks.check("one argument gives to the sender", true,
                "slot " + self.itemSlot + " holds " + self.myItemStack.stackSize + "x " + SELF_ID);

        bot.chat("/give " + username + " " + NAMED_ID);
        SlotPacket named = bot.awaitLast(SlotPacket.class,
                p -> p.windowId == 0 && p.myItemStack != null && p.myItemStack.itemID == NAMED_ID, 3000);
        checks.check("two arguments give to the named player", named != null,
                "no slot ever held id " + NAMED_ID);

        checks.check("an unknown id is named back, not swallowed as an unknown command",
                said(bot, "/give 999999", "No such item"));

        checks.check("a bare word reports the usage rather than a missing item",
                said(bot, "/give notaplayer", "Usage"));

        checks.check("an unknown player is named back",
                said(bot, "/give notaplayer " + SELF_ID, "No such player"));

        if (bot.kickReason != null) {
            checks.abort("kicked mid-run: " + bot.kickReason);
        }

        checks.done();
    }

    /** Runs a command and reports whether any chat that followed contained the fragment. */
    private static boolean said(Bot bot, String command, String fragment) throws Exception {
        bot.chat(command);
        return bot.awaitLast(ChatPacket.class, p -> p.message != null && p.message.contains(fragment), 3000) != null;
    }
}
