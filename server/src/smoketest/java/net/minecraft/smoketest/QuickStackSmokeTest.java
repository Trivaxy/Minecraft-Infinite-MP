package net.minecraft.smoketest;

import net.minecraft.game.item.ItemStack;
import net.minecraft.network.packet.container.ContainerContentsPacket;
import net.minecraft.network.packet.misc.TransactionPacket;
import net.minecraft.network.packet.player.ClickPacket;
import net.minecraft.network.packet.player.PlayerTeleportPacket;
import net.minecraft.network.packet.player.SlotPacket;

/**
 * B-20: number-key quickstack (hover a slot, press 1..9 to swap it with that
 * hotbar slot) ran fully client-side before - the server never learned the
 * swap. Needs a running server and "swapbot" in run/server/ops.txt; the bot
 * stocks itself with /clearinv + /give.
 *
 * The client encodes the swap as a vanilla-protocol ClickPacket with
 * isActive=true and the hotbar index in place of the mouse button, offset by 2
 * so it cannot collide with a real left/right click
 * (ContainerScreen.update / Container.swapWithHotbarSlot).
 *
 *   phase 1 (into hotbar) - hovering a main-inventory slot, press 2: the full
 *                           contents refresh the server answers every swap with
 *                           must show dirt in hotbar index 1 (packet slot 41)
 *                           and nothing in the hovered slot.
 *   phase 2 (out of hotbar) - press 2 again hovering an inventory slot: dirt
 *                           moves out of hotbar index 1 into it.
 *   phase 3 (guard) - an out-of-range hotbar index is ignored: the server still
 *                           answers with a refresh, but nothing has moved.
 *   phase 4 (dupe) - taking a craft result must still eat the ingredients.
 *
 * PlayerContainer lays the main inventory at 13..39 and the hotbar at 40..48,
 * so hotbar index N is packet slot 40 + N.
 *
 * Run: ./gradlew :server:smokeTest -PsmokeClass=net.minecraft.smoketest.QuickStackSmokeTest
 */
public class QuickStackSmokeTest {

    private static final int DIRT = 3;
    private static final int HOTBAR = 1; // hotbar index; key "2"
    private static final int HOTBAR_PACKET_SLOT = 40 + HOTBAR;
    private static final int MAIN_START = 13;
    private static final int HOTBAR_CLICK_OFFSET = 2;
    private static final int WOOD = 17;
    private static final int PLANKS = 5;
    private static final int CRAFT_RESULT = 0;  // PlayerContainer lays the result at 0,
    private static final int CRAFT_GRID = 1;    // then the 2x2 grid at 1..4

    private static final Checks checks = new Checks();
    private static int nextAction = 10;

    public static void main(String[] args) throws Exception {
        Bot bot = Bot.join(args, "swapbot");

        // The server only mutates a player from its movement handlers once
        // hasMoved re-arms, which needs the login teleport echoed back exactly.
        // Reading the teleport is what arms the bot's own position echo.
        bot.await(PlayerTeleportPacket.class, 15000);

        bot.chat("/clearinv");
        bot.drain(1000);
        bot.chat("/give " + DIRT + " 1");
        SlotPacket given = awaitGivenStack(bot, DIRT);
        int initialSlot = given.itemSlot;
        checks.check("dirt starts outside the tested hotbar index", initialSlot != HOTBAR_PACKET_SLOT,
                "the /give landed in packet slot " + initialSlot + " - that is hotbar index " + HOTBAR + " itself");
        checks.check("dirt starts in the main inventory", initialSlot >= MAIN_START,
                "the /give landed in packet slot " + initialSlot + ", outside 13..39 - reroute the test slot");

        bot.chat("/give " + DIRT + " 1");
        SlotPacket hoverGiven = awaitGivenStack(bot, DIRT);

        // ---------------- phase 1: hover an inventory slot, press 2 -> dirt into hotbar index 1
        ItemStack[] afterIn = swapAndReceive(bot, hoverGiven.itemSlot, HOTBAR);
        checks.check("swap into hotbar: server refreshed the container", afterIn != null,
                "no ContainerContentsPacket answer");
        checks.check("swap into hotbar: dirt in hotbar index " + HOTBAR + " (packet slot " + HOTBAR_PACKET_SLOT + ")",
                afterIn != null && isId(afterIn[HOTBAR_PACKET_SLOT], DIRT),
                afterIn == null ? "" : "slot " + HOTBAR_PACKET_SLOT + " = " + describe(afterIn[HOTBAR_PACKET_SLOT]));
        checks.check("swap into hotbar: hovered slot " + hoverGiven.itemSlot + " is empty",
                afterIn != null && afterIn[hoverGiven.itemSlot] == null,
                afterIn == null ? "" : "slot " + hoverGiven.itemSlot + " still holds " + describe(afterIn[hoverGiven.itemSlot]));

        // ---------------- phase 2: press 2 again -> dirt back out into the hovered slot
        ItemStack[] afterOut = swapAndReceive(bot, hoverGiven.itemSlot, HOTBAR);
        checks.check("swap out of hotbar: server refreshed the container", afterOut != null,
                "no ContainerContentsPacket answer");
        checks.check("swap out of hotbar: dirt back in slot " + hoverGiven.itemSlot,
                afterOut != null && isId(afterOut[hoverGiven.itemSlot], DIRT),
                afterOut == null ? "" : "slot " + hoverGiven.itemSlot + " = " + describe(afterOut[hoverGiven.itemSlot]));
        checks.check("swap out of hotbar: hotbar index " + HOTBAR + " empty again",
                afterOut != null && afterOut[HOTBAR_PACKET_SLOT] == null,
                afterOut == null ? "" : "slot " + HOTBAR_PACKET_SLOT + " = " + describe(afterOut[HOTBAR_PACKET_SLOT]));

        ItemStack straightUp = afterOut == null || afterOut[hoverGiven.itemSlot] == null
                ? null : afterOut[hoverGiven.itemSlot].copy();

        // ---------------- phase 3: an out-of-range hotbar index is ignored, nothing moves
        swap(bot, hoverGiven.itemSlot, 100, (short) 3);
        ItemStack[] afterGuard = drainContents(bot);
        checks.check("out-of-range hotbar index ignored: dirt still in slot " + hoverGiven.itemSlot,
                afterGuard == null || stacksEqual(afterGuard[hoverGiven.itemSlot], straightUp),
                afterGuard == null ? "" : "slot " + hoverGiven.itemSlot + " = " + describe(afterGuard[hoverGiven.itemSlot]));
        checks.check("out-of-range hotbar index ignored: hotbar index " + HOTBAR + " still empty",
                afterGuard == null || afterGuard[HOTBAR_PACKET_SLOT] == null,
                afterGuard == null ? "" : "slot " + HOTBAR_PACKET_SLOT + " = " + describe(afterGuard[HOTBAR_PACKET_SLOT]));

        // ---------------- phase 4: taking a craft result must still eat the ingredients
        //
        // The first cut of the swap skipped canTakeStack and never called
        // onPickup, and its validItem guard was itself skipped whenever the
        // destination hotbar slot was empty - so this handed over the planks
        // with the log still sitting in the grid. Reverted, this phase goes red
        // on "the log was consumed" while everything above stays green.
        bot.chat("/give " + WOOD + " 1");
        SlotPacket wood = awaitGivenStack(bot, WOOD);
        checks.check("wood for the craft landed in the main inventory", wood.itemSlot >= MAIN_START,
                "the /give landed in packet slot " + wood.itemSlot + ", outside 13..39");

        click(bot, wood.itemSlot, 0, new ItemStack(WOOD, 1, 0));   // onto the cursor
        click(bot, CRAFT_GRID, 0, null);                           // into the 2x2 grid
        ItemStack[] staged = drainContents(bot);
        checks.check("wood is in the craft grid and 4 planks are on offer",
                staged == null || (isId(staged[CRAFT_GRID], WOOD) && isId(staged[CRAFT_RESULT], PLANKS)),
                staged == null ? "no refresh to read" : "grid = " + describe(staged[CRAFT_GRID])
                        + ", result = " + describe(staged[CRAFT_RESULT]));

        ItemStack[] afterCraft = swapAndReceive(bot, CRAFT_RESULT, HOTBAR);
        checks.check("craft result: planks arrived in hotbar index " + HOTBAR,
                afterCraft != null && isId(afterCraft[HOTBAR_PACKET_SLOT], PLANKS),
                afterCraft == null ? "" : "slot " + HOTBAR_PACKET_SLOT + " = " + describe(afterCraft[HOTBAR_PACKET_SLOT]));
        checks.check("craft result: the log was consumed (no free craft)",
                afterCraft != null && afterCraft[CRAFT_GRID] == null,
                afterCraft == null ? "" : "grid slot " + CRAFT_GRID + " still holds "
                        + describe(afterCraft[CRAFT_GRID]) + " - the ingredients were not eaten");
        checks.check("craft result: the result slot did not restock itself",
                afterCraft != null && afterCraft[CRAFT_RESULT] == null,
                afterCraft == null ? "" : "slot " + CRAFT_RESULT + " = " + describe(afterCraft[CRAFT_RESULT]));

        bot.chat("/clearinv");
        checks.done();
    }

    // ---------------- protocol helpers ----------------

    private static void swap(Bot bot, int slot, int hotbarIndex, short action) throws Exception {
        ClickPacket swap = new ClickPacket();
        swap.window_Id = 0;
        swap.inventorySlot = (short) slot;
        // mouse buttons 0/1 are the real clicks, so the hotbar index rides at
        // +2 - keep this in step with MultiplayerMode.containerSwapWithHotbar
        swap.mouseClick = hotbarIndex + HOTBAR_CLICK_OFFSET;
        swap.action = action;
        swap.isActive = true;
        swap.itemStack = null;
        bot.send(swap);
    }

    /**
     * An ordinary click, used to stage the craft in phase 4. The server checks
     * the stack we claim was in the slot against what it computed and rejects
     * with a resync on a mismatch, so `expected` has to be the stack the slot
     * held *before* the click - null when dropping into an empty slot.
     */
    private static void click(Bot bot, int slot, int button, ItemStack expected) throws Exception {
        ClickPacket pkt = new ClickPacket();
        pkt.window_Id = 0;
        pkt.inventorySlot = (short) slot;
        pkt.mouseClick = button;
        pkt.action = (short) nextAction++;
        pkt.isActive = false;
        pkt.itemStack = expected;
        bot.send(pkt);

        TransactionPacket answer = bot.await(TransactionPacket.class, 1500);
        checks.check("click on slot " + slot + " accepted", answer != null && answer.transactionOngoing,
                answer == null ? "no TransactionPacket came back"
                        : "the server rejected it and resynced - the staged craft is not what this test assumes");
    }

    private static ItemStack[] swapAndReceive(Bot bot, int slot, int hotbarIndex) throws Exception {
        swap(bot, slot, hotbarIndex, (short) 0);
        return drainContents(bot);
    }

    /**
     * The swap path never sends a TransactionPacket, so answer proof is the
     * contents refresh it emits whether or not the swap took. Take the LAST one
     * received - phase 1's SlotPacket single-slot updates arrive ahead of it.
     */
    private static ItemStack[] drainContents(Bot bot) throws Exception {
        ContainerContentsPacket contents = bot.awaitLast(ContainerContentsPacket.class,
                p -> p.windowId == 0, 3000, 500);
        return contents == null ? null : contents.itemStack;
    }

    private static SlotPacket awaitGivenStack(Bot bot, int id) throws Exception {
        SlotPacket found = bot.awaitLast(SlotPacket.class,
                p -> p.windowId == 0 && p.myItemStack != null && p.myItemStack.itemID == id, 4000);
        checks.check("the /give landed somewhere (" + describe(found == null ? null : found.myItemStack) + ")",
                found != null, "never saw a SlotPacket for id " + id + " - is swapbot in run/server/ops.txt?");
        if (found == null) {
            SlotPacket dummy = new SlotPacket();
            dummy.itemSlot = -1;
            return dummy;
        }
        return found;
    }

    // ---------------- assertions ----------------

    private static boolean isId(ItemStack stack, int id) {
        return stack != null && stack.itemID == id;
    }

    private static boolean stacksEqual(ItemStack a, ItemStack b) {
        if (a == null || b == null) {
            return a == b;
        }
        return a.itemID == b.itemID && a.stackSize == b.stackSize && a.itemDmg == b.itemDmg;
    }

    private static String describe(ItemStack stack) {
        return stack == null ? "empty" : stack.stackSize + "x id " + stack.itemID;
    }
}
