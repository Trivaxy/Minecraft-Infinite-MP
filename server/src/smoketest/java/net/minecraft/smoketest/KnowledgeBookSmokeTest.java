package net.minecraft.smoketest;

import net.minecraft.game.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.container.ContainerContentsPacket;
import net.minecraft.network.packet.misc.TransactionPacket;
import net.minecraft.network.packet.player.ClickPacket;
import net.minecraft.network.packet.player.SlotPacket;

/**
 * B-06: sends both slot indices the knowledge book can produce for one inventory slot and checks
 * the server's verdict. Needs "bookbot" in run/server/ops.txt - it stocks itself with /give.
 *
 * Usage: KnowledgeBookSmokeTest <host> <port> [username]
 */
public class KnowledgeBookSmokeTest {

    private static final int GIVE_ID = 1;
    private static final int GIVE_COUNT = 5;

    /**
     * Both containers wrap the same PlayerInventory, so an inventory id sits 12 slots apart in
     * each: PlayerContainer holds the main inventory at 13..39 and the hotbar at 40..48,
     * LoreContainer at 1..27 and 28..36.
     */
    private static final int BOOK_INDEX_OFFSET = 12;

    private static final Checks checks = new Checks();

    public static void main(String[] args) throws Exception {
        String username = args.length > 2 ? args[2] : "bookbot";
        Bot bot = Bot.join(args, username);
        checks.note("logged in as " + username);

        // Login sends a SlotPacket per occupied slot, so a /give that merges into a slot we
        // already hold would leave us claiming a stale count. Start empty.
        bot.chat("/clearinv");
        bot.drain(1500);
        bot.chat("/give " + GIVE_ID + " " + GIVE_COUNT);

        SlotPacket given = bot.awaitLast(SlotPacket.class,
                p -> p.windowId == 0 && p.myItemStack != null && p.myItemStack.itemID == GIVE_ID, 3000);
        if (given == null) {
            checks.abort("never saw the /give land in a slot - is \"" + username + "\" in run/server/ops.txt?");
            return;
        }

        int playerIndex = given.itemSlot;
        int bookIndex = playerIndex - BOOK_INDEX_OFFSET;
        ItemStack stack = given.myItemStack;
        checks.note("server put " + stack.stackSize + "x id " + stack.itemID
                + " in player-container slot " + playerIndex
                + "  (the book would call that slot " + bookIndex + ")");

        Verdict bug = click(bot, bookIndex, stack, (short) 1);
        checks.check("server answered the book-index click", bug.answered);
        checks.check("book index " + bookIndex + " REJECTED (this is the snap-back)", !bug.accepted);
        checks.check("rejection came with a full container resync", bug.resynced);
        // Until this ack lands the server silently ignores every further click on the window.
        bot.send(new TransactionPacket(0, (short) 1, true));

        Verdict fixed = click(bot, playerIndex, stack, (short) 2);
        checks.check("server answered the translated click", fixed.answered);
        checks.check("player index " + playerIndex + " ACCEPTED (item picked up)", fixed.accepted);

        click(bot, playerIndex, null, (short) 3);

        checks.done();
    }

    private static class Verdict {
        boolean answered;
        boolean accepted;
        boolean resynced;
    }

    private static Verdict click(Bot bot, int slotIndex, ItemStack claimed, short action) throws Exception {
        bot.send(new ClickPacket(0, slotIndex, 0, false, claimed == null ? null : claimed.copy(), action));

        Verdict verdict = new Verdict();
        long deadline = System.currentTimeMillis() + 3000L;
        while (System.currentTimeMillis() < deadline) {
            Packet next = bot.read();
            if (bot.kickReason != null) {
                checks.abort("server kicked us: \"" + bot.kickReason + "\"");
            }
            if (next instanceof ContainerContentsPacket) {
                verdict.resynced = true;
            }
            if (next instanceof TransactionPacket && ((TransactionPacket) next).transactionId == action) {
                verdict.answered = true;
                verdict.accepted = ((TransactionPacket) next).transactionOngoing;
                if (verdict.accepted) {
                    break;
                }
                deadline = Math.min(deadline, System.currentTimeMillis() + 500L);
            }
        }

        return verdict;
    }
}
