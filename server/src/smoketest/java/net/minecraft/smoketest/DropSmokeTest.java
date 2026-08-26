package net.minecraft.smoketest;

import net.minecraft.network.packet.player.DigPacket;
import net.minecraft.network.packet.player.ItemPacket;
import net.minecraft.network.packet.player.SlotPacket;

/**
 * Where a thrown item leaves the player, in every pose. Needs "dropbot" in run/server/ops.txt.
 *
 * B-59: the spawn was posY - 0.3, and posY meant the eyes on a LocalPlayer and the feet on an
 * EntityPlayerMP, so multiplayer threw the item from 0.3 blocks under the floor. B-60 removed
 * that split - posY is the feet everywhere now, and getEyeHeight() is the only thing that knows
 * where the eye is. The three phases below are that table read back off the wire: standing 1.62,
 * sneaking 1.42, crawling 0.56, each minus the 0.3 the drop sits below the eye.
 *
 * The feet check matters as much as the height: a pose change must move the eye without moving
 * the player, which is exactly what the old setCrawling (yOffset plus a 1.3 fudge) got wrong.
 *
 * Usage: DropSmokeTest <host> <port> [username]
 */
public class DropSmokeTest {

    private static final int ITEM_ID = 4096;
    private static final int SNEAK = 1;
    private static final int CRAWL = 2;

    private static final Checks checks = new Checks();

    public static void main(String[] args) throws Exception {
        String username = args.length > 2 ? args[2] : "dropbot";
        Bot bot = Bot.join(args, username);
        checks.note("logged in as " + username);

        // never sink()s: land() does its own sinking and hands back the position the server
        // accepted, and a bot that keeps sinking afterwards drifts away from it.
        if (!bot.land(15000)) {
            checks.abort("never landed - no ground position to measure the drop against");
            return;
        }

        double feet = bot.posY;
        checks.note(String.format("standing on %.2f", feet));

        drop(bot, feet, "standing", 1.62);

        bot.sendState(CRAWL, true);
        bot.drain(500);
        drop(bot, feet, "crawling", 0.56);
        bot.sendState(CRAWL, false);
        bot.drain(500);

        bot.sendState(SNEAK, true);
        bot.drain(500);
        drop(bot, feet, "sneaking", 1.42);
        bot.sendState(SNEAK, false);
        bot.drain(500);

        if (bot.kickReason != null) {
            checks.abort("kicked mid-run: " + bot.kickReason);
        }

        checks.done();
    }

    /** Gives the bot an item, throws it, and checks where it came out. */
    private static void drop(Bot bot, double feet, String pose, double eyeHeight) throws Exception {
        bot.chat("/give " + ITEM_ID);
        SlotPacket given = bot.awaitLast(SlotPacket.class,
                p -> p.windowId == 0 && p.myItemStack != null && p.myItemStack.itemID == ITEM_ID, 3000);
        if (given == null) {
            checks.abort("`/give " + ITEM_ID + "` landed nothing - is the bot in run/server/ops.txt?");
            return;
        }

        bot.send(new DigPacket(4, 0, 0, 0, 0));
        ItemPacket item = bot.await(ItemPacket.class,
                p -> p.itemStack != null && p.itemStack.itemID == ITEM_ID, 5000);
        if (item == null) {
            checks.check(pose + ": the throw spawns an item at all", false, "no item entity arrived");
            return;
        }

        double want = eyeHeight - 0.3;
        double got = item.yPosition / 32.0 - feet;
        checks.check(pose + ": the item leaves the eye, not the floor", Math.abs(got - want) < 0.25,
                String.format("spawned %.2f above the feet, wanted about %.2f", got, want));

        double motionY = item.pitch / 128.0;
        checks.check(pose + ": the item is thrown upwards", motionY > 0.0,
                String.format("initial motionY was %.3f", motionY));
    }
}
