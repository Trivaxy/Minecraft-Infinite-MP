package net.minecraft.smoketest;

import net.minecraft.game.item.ItemStack;
import net.minecraft.network.packet.container.ContainerContentsPacket;
import net.minecraft.network.packet.misc.TransactionPacket;
import net.minecraft.network.packet.player.ClickPacket;
import net.minecraft.network.packet.player.PlayerTeleportPacket;
import net.minecraft.network.packet.player.SlotPacket;

/**
 * Backpack and quiver contents ride in the equipped accessory item's NBT, and
 * PlayerContainer.saveContents rewrites that tag from the container whenever a
 * backpack or quiver slot changes. The server never filled the container back
 * in from the tag at login - only the singleplayer LocalPlayer did - so a
 * rejoining player got an empty quiver, and the first inventory change wrote
 * that emptiness over the item's tag: the arrows were gone for good, tooltip
 * preview and all.
 *
 * Needs a running server and "quiverbot" in run/server/ops.txt (it stocks
 * itself with /give).
 *
 *   phase 1 - equip a quiver, drop a stack of arrows into it, see them land.
 *   phase 2 - reconnect: the login container refresh must still show them.
 *   phase 3 - touch an unrelated slot so saveContents runs, then look again.
 *             This is the half that ate them.
 *
 * PlayerContainer lays out the craft result at 0, the accessory slots at 9..12
 * (the charm slot is 10), the main inventory at 13..39, the hotbar at 40..48,
 * the trash at 50, the backpack at 51..68 and the three quiver slots at 69..71.
 *
 * Run: ./gradlew :server:smokeTest -PsmokeClass=net.minecraft.smoketest.QuiverSmokeTest
 */
public class QuiverSmokeTest {

    // ItemList's static init wants a game context the rig does not have, so the
    // ids are spelled out: an item's id is 4096 + the one its constructor takes.
    private static final int QUIVER = 4109;
    private static final int ARROW = 4102;
    private static final int DIRT = 3;

    private static final int CRAFT_RESULT = 0;
    private static final int CHARM_SLOT = 10;
    private static final int TRASH_SLOT = 50;
    private static final int QUIVER_SLOT = 69;
    private static final int SLOT_COUNT = 72;

    private static final Checks checks = new Checks();
    private static int nextAction = 10;
    private static ItemStack inTrash;

    public static void main(String[] args) throws Exception {
        String username = args.length > 2 ? args[2] : "quiverbot";
        Bot bot = Bot.join(args, username);
        bot.await(PlayerTeleportPacket.class, 15000);
        bot.settle(1000);

        // Whatever a previous run left equipped would make the clicks below
        // land on an occupied slot and be rejected.
        if (!clear(bot)) {
            checks.abort("could not empty the inventory the run starts from");
            return;
        }

        // ---------------- phase 1: equip the quiver and stock it
        SlotPacket quiver = give(bot, QUIVER, username);
        if (quiver == null) {
            checks.abort("`/give " + QUIVER + "` landed nothing - is \"" + username + "\" in run/server/ops.txt?");
            return;
        }
        click(bot, quiver.itemSlot, quiver.myItemStack);
        click(bot, CHARM_SLOT, null);

        SlotPacket arrows = give(bot, ARROW, username);
        if (arrows == null) {
            checks.abort("`/give " + ARROW + "` landed nothing");
            return;
        }
        int stocked = arrows.myItemStack.stackSize;
        click(bot, arrows.itemSlot, arrows.myItemStack);
        click(bot, QUIVER_SLOT, null);

        ItemStack[] stocking = refresh(bot);
        checks.check("the quiver is the equipped accessory", isId(at(stocking, CHARM_SLOT), QUIVER),
                "charm slot = " + describe(at(stocking, CHARM_SLOT)));
        checks.check("arrows go into the quiver", isId(at(stocking, QUIVER_SLOT), ARROW),
                "quiver slot " + QUIVER_SLOT + " = " + describe(at(stocking, QUIVER_SLOT)));

        // ---------------- phase 2: rejoin
        bot.close();
        Thread.sleep(2000);
        Bot back = Bot.join(args, username);
        ItemStack[] onLogin = contents(back);
        back.await(PlayerTeleportPacket.class, 15000);
        back.settle(1000);
        if (onLogin == null) {
            onLogin = refresh(back);
        }

        checks.check("the quiver comes back equipped", isId(at(onLogin, CHARM_SLOT), QUIVER),
                "charm slot = " + describe(at(onLogin, CHARM_SLOT)));
        checks.check("the arrows are in the quiver after the rejoin",
                isId(at(onLogin, QUIVER_SLOT), ARROW) && onLogin[QUIVER_SLOT].stackSize == stocked,
                "quiver slot " + QUIVER_SLOT + " = " + describe(at(onLogin, QUIVER_SLOT))
                        + ", expected " + stocked + "x id " + ARROW);

        // ---------------- phase 3: an unrelated inventory change must not eat them
        SlotPacket dirt = give(back, DIRT, username);
        if (dirt != null) {
            click(back, dirt.itemSlot, dirt.myItemStack);
            click(back, dirt.itemSlot, null);
        }

        ItemStack[] afterTouch = refresh(back);
        checks.check("an inventory change does not empty the quiver",
                isId(at(afterTouch, QUIVER_SLOT), ARROW),
                "quiver slot " + QUIVER_SLOT + " = " + describe(at(afterTouch, QUIVER_SLOT))
                        + " - saveContents wrote the empty container over the item's tag");

        clear(back);
        checks.done();
    }

    // ---------------- protocol helpers ----------------

    private static SlotPacket give(Bot bot, int id, String username) throws Exception {
        bot.chat("/give " + username + " " + id);
        return bot.awaitLast(SlotPacket.class,
                p -> p.windowId == 0 && p.myItemStack != null && p.myItemStack.itemID == id, 4000);
    }

    /**
     * The server checks the stack we claim the slot held against what it
     * computed and resyncs on a mismatch, so `expected` is the stack that was in
     * the slot before the click - null when dropping onto an empty slot.
     */
    private static void click(Bot bot, int slot, ItemStack expected) throws Exception {
        send(bot, slot, expected);
        TransactionPacket answer = bot.await(TransactionPacket.class, 2000);
        checks.check("click on slot " + slot + " accepted", answer != null && answer.transactionOngoing,
                answer == null ? "no TransactionPacket came back" : "the server rejected it and resynced");
    }

    private static void send(Bot bot, int slot, ItemStack expected) throws Exception {
        ClickPacket pkt = new ClickPacket();
        pkt.window_Id = 0;
        pkt.inventorySlot = (short) slot;
        pkt.mouseClick = 0;
        pkt.action = (short) nextAction++;
        pkt.isActive = false;
        pkt.itemStack = expected == null ? null : expected.copy();
        bot.send(pkt);
    }

    /**
     * An ordinary click is answered with a transaction and the single slots that
     * moved, never the whole window. A click whose claimed stack does not match
     * what the server computed makes it resync everything instead, so an
     * impossible claim on the empty craft result - a no-op with an empty cursor
     * - is how the rig asks for the container state.
     */
    private static ItemStack[] refresh(Bot bot) throws Exception {
        TransactionPacket rejection = null;
        // A click that lands while the server is still finishing the login is
        // dropped without an answer, so the ask is worth repeating.
        for (int attempt = 0; attempt < 3 && rejection == null; attempt++) {
            send(bot, CRAFT_RESULT, new ItemStack(DIRT, 1, 0));
            rejection = bot.await(TransactionPacket.class, p -> !p.transactionOngoing, 3000);
        }

        ItemStack[] state = contents(bot);
        // A rejected click blocks the container (Container.canUseMP) until the
        // client acknowledges the transaction it was rejected with - without
        // this the server ignores every click that follows.
        if (rejection != null) {
            bot.send(new TransactionPacket(0, rejection.transactionId, true));
            bot.drain(150);
        }

        return state;
    }

    private static ItemStack[] contents(Bot bot) throws Exception {
        ContainerContentsPacket packet = bot.awaitLast(ContainerContentsPacket.class,
                p -> p.windowId == 0, 4000, 500);
        return packet == null ? null : packet.itemStack;
    }

    /**
     * Empties every slot into the trash slot, so a run always starts from the
     * same inventory. The charm goes first: unequipping it writes the quiver
     * back into the item and empties the quiver slots by itself.
     */
    private static boolean clear(Bot bot) throws Exception {
        ItemStack[] state = refresh(bot);
        if (state == null) {
            return false;
        }
        inTrash = at(state, TRASH_SLOT);
        if (at(state, CHARM_SLOT) != null) {
            trash(bot, CHARM_SLOT, state[CHARM_SLOT]);
            state = refresh(bot);
            if (state == null) {
                return false;
            }
        }

        for (int slot = 0; slot < SLOT_COUNT; slot++) {
            if (slot != TRASH_SLOT && at(state, slot) != null) {
                trash(bot, slot, state[slot]);
            }
        }

        ItemStack[] cleared = refresh(bot);
        boolean empty = cleared != null;
        for (int slot = 0; slot < SLOT_COUNT; slot++) {
            if (slot != TRASH_SLOT && at(cleared, slot) != null) {
                checks.note("slot " + slot + " still holds " + describe(cleared[slot]));
                empty = false;
            }
        }

        return empty;
    }

    private static void trash(Bot bot, int slot, ItemStack held) throws Exception {
        send(bot, slot, held);
        send(bot, TRASH_SLOT, inTrash);
        inTrash = held;
        bot.drain(120);
    }

    // ---------------- assertions ----------------

    private static ItemStack at(ItemStack[] state, int slot) {
        return state == null || slot >= state.length ? null : state[slot];
    }

    private static boolean isId(ItemStack stack, int id) {
        return stack != null && stack.itemID == id;
    }

    private static String describe(ItemStack stack) {
        return stack == null ? "empty" : stack.stackSize + "x id " + stack.itemID;
    }
}
