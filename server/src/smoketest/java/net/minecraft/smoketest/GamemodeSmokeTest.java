package net.minecraft.smoketest;

import net.minecraft.game.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.container.CreativeSlotPacket;
import net.minecraft.network.packet.misc.ChatPacket;
import net.minecraft.network.packet.player.DigPacket;
import net.minecraft.network.packet.player.GamemodePacket;
import net.minecraft.network.packet.player.ItemPacket;
import net.minecraft.network.packet.player.SlotPacket;
import net.minecraft.network.packet.world.UpdateBlockPacket;

/**
 * /gamemode, and the server half of what a gamemode means. Needs "modebot" in
 * run/server/ops.txt.
 *
 * Gamemode used to be a client-only idea, so the checks worth having are the ones
 * that prove the server both knows and acts on it: the packet that carries it, the
 * floating kick creative is now exempt from, the break it now answers instantly and
 * without drops, and the creative slot request it now accepts - but only from a
 * player it agrees is in creative.
 *
 * The dig probe aims at the blocks a landed bot is standing over rather than at
 * fixed coordinates: there is no /setblock any more to prepare a known one. Digging
 * them in survival first is also the rig's own precondition - if nothing breaks in
 * survival and nothing breaks in creative either, the bot was not standing over
 * anything, and the creative check says so. Each run leaves the world a few blocks
 * deeper here.
 *
 * The pickaxe is the point of the drops check: what stone drops is decided by
 * canHarvestBlock, so bare-handed that check would be green against any build. It
 * arrives through the creative slot packet rather than /give, which puts it in a
 * known slot - the equipped one - instead of wherever the last run left room.
 *
 * The bot never calls sink(), so it holds whatever position it is given, which is
 * what the two flight checks need and why the run ends with the survival one.
 *
 * Usage: GamemodeSmokeTest <host> <port>
 */
public class GamemodeSmokeTest {

    private static final String NAME = "modebot";

    private static final int SURVIVAL = 0;
    private static final int CREATIVE = 1;

    /** PlayerContainer puts the nine hotbar slots last, and the bot never switches off the first. */
    private static final int HELD_SLOT = 40;

    /** Item ids are their registration number plus 4096; touching ItemList here drags in statics that need a real game. */
    private static final int DIAMOND_PICKAXE = 4118;

    private static final int BROKE = 0;
    private static final int DROPPED = 1;

    private static final Checks checks = new Checks();

    public static void main(String[] args) throws Exception {
        Bot bot = Bot.join(args, NAME);
        GamemodePacket atLogin = bot.await(GamemodePacket.class, 5000);
        if (atLogin == null) {
            checks.abort("no gamemode was sent at login");
            return;
        }
        checks.check("login carries the gamemode the server has for this player", atLogin.gamemode == SURVIVAL,
                "it sent " + atLogin.gamemode + " - did an earlier run leave " + NAME + " in another mode?");

        if (!bot.land(30000)) {
            checks.abort("the bot never landed, so there is no ground to dig");
            return;
        }

        int footX = (int)Math.floor(bot.posX);
        int footZ = (int)Math.floor(bot.posZ);
        int under = (int)Math.floor(bot.posY) - 1;
        checks.note(String.format("landed at %.2f %.2f %.2f", bot.posX, bot.posY, bot.posZ));

        checks.check("a survival dig does not break solid ground on the first packet",
                !digColumn(bot, footX, under, footZ)[BROKE], "something broke instantly");

        bot.send(new CreativeSlotPacket(HELD_SLOT, pickaxe()));
        checks.check("a survival player cannot put an item in their own slot",
                bot.await(SlotPacket.class, p -> p.windowId == 0 && p.itemSlot == HELD_SLOT, 2000) != null,
                "the server neither refused it nor put the slot back");

        if (!setMode(bot, "creative", CREATIVE)) {
            checks.abort("`/gamemode creative` was not answered - is \"" + NAME + "\" in run/server/ops.txt?");
            return;
        }
        checks.check("the sender is told what they were set to",
                said(bot, "Set own gamemode to creative"), "no chat confirmed it");

        bot.send(new CreativeSlotPacket(HELD_SLOT, pickaxe()));
        bot.drain(1000);

        setMode(bot, "survival", SURVIVAL);
        boolean[] survivalDig = fullDig(bot, footX, under - 4, footZ);
        checks.check("a creative player can, and the server keeps what they were given", survivalDig[DROPPED],
                "breaking with the pickaxe dropped nothing, so the server never took it");

        setMode(bot, "creative", CREATIVE);
        boolean[] creativeDig = digColumn(bot, footX, under, footZ);
        checks.check("and the same dig breaks the block instantly in creative", creativeDig[BROKE],
                "nothing broke - the bot may not have been standing over anything");
        checks.check("with nothing dropped for it, pickaxe in hand or not",
                creativeDig[BROKE] && !creativeDig[DROPPED],
                creativeDig[BROKE] ? "an item was spawned for a creative break" : "nothing broke, so nothing was proven");

        bot.chat("/tp ~ ~6 ~");
        bot.followTeleport(3000);
        bot.drain(12000);
        checks.check("a creative player is not kicked for floating", bot.kickReason == null, "they were: " + bot.kickReason);

        checks.check("the numeric form is accepted too", setMode(bot, "0", SURVIVAL), "no gamemode came back for `/gamemode 0`");
        checks.check("and so is naming a player",
                setMode(bot, "creative " + NAME, CREATIVE) && said(bot, "Set " + NAME + "'s gamemode to creative"),
                "no chat said what " + NAME + " was set to");

        bot.chat("/gamemode flying");
        checks.check("an unknown mode is refused", said(bot, "No such gamemode"), "it was not named back");

        bot.close();

        Bot back = Bot.join(args, NAME);
        GamemodePacket afterRelog = back.await(GamemodePacket.class, 5000);
        checks.check("the mode outlives the connection", afterRelog != null && afterRelog.gamemode == CREATIVE,
                afterRelog == null ? "nothing was sent at login" : "it came back as " + afterRelog.gamemode);
        back.settle(1500);

        checks.check("and can be set back", setMode(back, "survival", SURVIVAL), "no gamemode came back for `/gamemode survival`");

        back.chat("/tp ~ ~6 ~");
        back.followTeleport(3000);
        back.drain(12000);
        checks.check("a survival player floating there still is", back.kickReason != null && back.kickReason.contains("Flying"),
                "they were not kicked: " + back.kickReason);

        checks.done();
    }

    private static ItemStack pickaxe() {
        return new ItemStack(DIAMOND_PICKAXE, 1);
    }

    /** Runs /gamemode and reports whether the server answered with that mode. */
    private static boolean setMode(Bot bot, String argument, int expected) throws Exception {
        bot.chat("/gamemode " + argument);
        return bot.await(GamemodePacket.class, p -> p.gamemode == expected, 3000) != null;
    }

    /** Digs down the few blocks a landed bot could be standing over; the server refuses anything past six. */
    private static boolean[] digColumn(Bot bot, int x, int top, int z) throws Exception {
        for (int y = top; y > top - 4; y--) {
            boolean[] result = dig(bot, x, y, z, 0);
            if (result[BROKE]) {
                return result;
            }
        }

        return new boolean[2];
    }

    /** Arms the dig, waits out the break time, then asks the server to finish it. */
    private static boolean[] fullDig(Bot bot, int x, int y, int z) throws Exception {
        bot.send(new DigPacket(0, x, y, z, 1));
        bot.drain(1000);
        return dig(bot, x, y, z, 2);
    }

    /**
     * One dig packet and what the server did with it. The break and the drop it would
     * spawn arrive in the same tick, so both are read from one window: waiting for the
     * block first swallows the item packet that proves the other half.
     */
    private static boolean[] dig(Bot bot, int x, int y, int z, int status) throws Exception {
        bot.send(new DigPacket(status, x, y, z, 1));
        boolean[] result = new boolean[2];
        long deadline = System.currentTimeMillis() + 2000;

        while (System.currentTimeMillis() < deadline) {
            Packet packet = bot.read();
            if (packet instanceof UpdateBlockPacket) {
                UpdateBlockPacket block = (UpdateBlockPacket)packet;
                if (block.xPosition == x && block.yPosition == y && block.zPosition == z && block.type == 0) {
                    result[BROKE] = true;
                }
            } else if (packet instanceof ItemPacket && near((ItemPacket)packet, x, y, z)) {
                result[DROPPED] = true;
            }
        }

        return result;
    }

    private static boolean near(ItemPacket packet, int x, int y, int z) {
        return Math.abs(packet.xPosition / 32.0 - x) < 3.0 && Math.abs(packet.yPosition / 32.0 - y) < 6.0 && Math.abs(packet.zPosition / 32.0 - z) < 3.0;
    }

    /** Whether any chat the bot read contained the fragment. */
    private static boolean said(Bot bot, String fragment) throws Exception {
        return bot.awaitLast(ChatPacket.class, p -> p.message != null && p.message.contains(fragment), 1500) != null;
    }
}
