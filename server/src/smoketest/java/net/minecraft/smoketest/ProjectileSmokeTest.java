package net.minecraft.smoketest;

import net.minecraft.game.entity.projectile.ProjectileType;
import net.minecraft.game.item.ItemStack;
import net.minecraft.network.packet.entity.EntitySpawnPacket;
import net.minecraft.network.packet.player.ItemSwitchPacket;
import net.minecraft.network.packet.player.PlacePacket;
import net.minecraft.network.packet.player.SlotPacket;

/**
 * B-14: throws a rock and checks the server announces it. Needs "rockbot" in run/server/ops.txt -
 * it stocks itself with /give.
 *
 * Usage: ProjectileSmokeTest <host> <port> [username]
 */
public class ProjectileSmokeTest {

    private static final int ROCK_ID = 4130;
    private static final int GIVE_COUNT = 4;

    /** PlayerContainer lays the hotbar out at 40..48; the held-item index counts from there. */
    private static final int HOTBAR_START = 40;

    public static void main(String[] args) throws Exception {
        Checks checks = new Checks();
        String username = args.length > 2 ? args[2] : "rockbot";
        Bot bot = Bot.join(args, username);
        checks.note("logged in as " + username);

        bot.chat("/clearinv");
        bot.drain(1500);
        bot.chat("/give " + ROCK_ID + " " + GIVE_COUNT);

        SlotPacket given = bot.awaitLast(SlotPacket.class,
                p -> p.windowId == 0 && p.myItemStack != null && p.myItemStack.itemID == ROCK_ID, 3000);
        if (given == null) {
            checks.abort("never saw the /give land in a slot - is \"" + username + "\" in run/server/ops.txt?");
            return;
        }
        if (given.itemSlot < HOTBAR_START) {
            checks.abort("the rock landed in slot " + given.itemSlot + ", outside the hotbar - cannot hold it");
            return;
        }

        int heldIndex = given.itemSlot - HOTBAR_START;
        checks.note("server put " + given.myItemStack.stackSize + "x id " + given.myItemStack.itemID
                + " in player-container slot " + given.itemSlot + " (hotbar index " + heldIndex + ")");

        bot.send(new ItemSwitchPacket(heldIndex));
        bot.drain(500);
        bot.send(new PlacePacket(-1, -1, -1, 255, new ItemStack(ROCK_ID, 1)));

        EntitySpawnPacket spawn = bot.await(EntitySpawnPacket.class, 3000);
        if (checks.check("the thrown rock produced an EntitySpawnPacket", spawn != null)) {
            checks.check("spawned as PROJECTILE", spawn.type == EntitySpawnPacket.EntityType.PROJECTILE,
                    "got " + spawn.type);
            checks.check("carries subtype ROCK=" + ProjectileType.ROCK.id,
                    spawn.metadata == ProjectileType.ROCK.id,
                    "got " + spawn.metadata + " = " + ProjectileType.byId(spawn.metadata));
            checks.check("carries a launch velocity",
                    spawn.motionX != 0 || spawn.motionY != 0 || spawn.motionZ != 0,
                    "(" + spawn.motionX + ", " + spawn.motionY + ", " + spawn.motionZ + ")");
        }

        checks.done();
    }
}
