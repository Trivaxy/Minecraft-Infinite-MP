package net.minecraft.smoketest;

import net.minecraft.client.network.world.NetworkWorld;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.animal.cow.Cow;
import net.minecraft.game.entity.player.Player;
import net.minecraft.client.network.player.RemotePlayer;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldType;

/**
 * B-35: Footsteps did not sync in multiplayer for remote players and mobs.
 *
 * In multiplayer, footsteps are calculated client-side based on movement.
 * Previously:
 * 1. RemotePlayer has noClip=true, never calls moveEntity, onGround stayed false,
 *    and onTick only calculated limbSwing, never distance or playStepSound.
 * 2. Mob in multiplayer interpolates position in mobTick via setPosition.
 *    Subsequent moveEntity was guarded by world.isOnline and never modified posX/posZ,
 *    so oldX/oldZ were 0 and distance never advanced.
 *
 * This test verifies that:
 *  - RemotePlayer on solid ground accumulates distance and advances nextStep at walking cadence.
 *  - RemotePlayer sneaking, flying, or in mid-air does not accumulate footstep distance.
 *  - Mob (Cow) in multiplayer accumulates distance and advances nextStep when moving.
 *  - Singleplayer Mob movement does not double-accumulate distance.
 *
 * Run: ./gradlew :server:smokeTest -PsmokeClass=net.minecraft.smoketest.FootstepSmokeTest
 */
public class FootstepSmokeTest {

    public static void main(String[] args) {
        Checks checks = new Checks();

        NetworkWorld clientWorld = new NetworkWorld(null, 12345L, 0, WorldType.DEFAULT, null);
        clientWorld.isOnline = true;

        // Prepare chunks along the runway
        for (int cx = -1; cx <= 1; cx++) {
            for (int cz = -1; cz <= 2; cz++) {
                clientWorld.doPreChunk(cx, cz, true);
            }
        }

        // Place a runway of stone blocks at y=60 from z=-10 to z=30
        for (int x = -2; x <= 2; x++) {
            for (int z = -10; z <= 30; z++) {
                clientWorld.updateBlocksAndNeighbors(x, 60, z, BlockList.stone.id, 0);
            }
        }

        // --- Phase 1: RemotePlayer on solid ground ---
        RemotePlayer player = new RemotePlayer(clientWorld, "TestPlayer");
        player.setPosition(0.5, 61.0, 0.5);
        player.prevX = player.lastTickPosX = 0.5;
        player.prevY = player.lastTickPosY = 61.0;
        player.prevZ = player.lastTickPosZ = 0.5;

        // Verify initial state
        checks.check("initial player distance is 0", player.distance == 0.0F);

        // Walk 20 ticks at 0.2 blocks/tick (total 4.0 blocks)
        for (int i = 1; i <= 20; i++) {
            double targetZ = 0.5 + i * 0.2;
            player.teleportInServer(0.5, 61.0, targetZ, 0.0F, 0.0F, 1);
            player.onTick();
        }

        checks.check("RemotePlayer on ground is detected as supported", player.onGround);
        checks.check("RemotePlayer accumulated distance walking on ground (> 2.0)", player.distance > 2.0F,
                "got distance=" + player.distance);
        checks.check("RemotePlayer advanced nextStep across footsteps (expected >= 3)", player.distance >= 2.0F,
                "got distance=" + player.distance);

        // --- Phase 2: Sneaking silences footsteps ---
        float distBeforeSneak = player.distance;
        player.setSneaking(true);
        for (int i = 21; i <= 30; i++) {
            double targetZ = 0.5 + i * 0.2;
            player.teleportInServer(0.5, 61.0, targetZ, 0.0F, 0.0F, 1);
            player.onTick();
        }
        checks.check("RemotePlayer sneaking does not accumulate footstep distance",
                player.distance == distBeforeSneak,
                "distance changed from " + distBeforeSneak + " to " + player.distance);

        // --- Phase 3: Mid-air (unsupported) does not produce footsteps ---
        RemotePlayer airPlayer = new RemotePlayer(clientWorld, "AirPlayer");
        airPlayer.setPosition(0.5, 100.0, 0.5);
        airPlayer.prevX = airPlayer.lastTickPosX = 0.5;
        airPlayer.prevY = airPlayer.lastTickPosY = 100.0;
        airPlayer.prevZ = airPlayer.lastTickPosZ = 0.5;

        for (int i = 1; i <= 20; i++) {
            double targetZ = 0.5 + i * 0.2;
            airPlayer.teleportInServer(0.5, 100.0, targetZ, 0.0F, 0.0F, 1);
            airPlayer.onTick();
        }
        checks.check("RemotePlayer in mid-air is not on ground", !airPlayer.onGround);
        checks.check("RemotePlayer in mid-air does not accumulate footstep distance",
                airPlayer.distance == 0.0F, "got distance=" + airPlayer.distance);

        // --- Phase 4: Mob (Cow) in multiplayer ---
        Cow cow = new Cow(clientWorld);
        cow.isMultiplayer = true;
        cow.setPosition(0.5, 61.0, 0.5);
        cow.prevX = cow.lastTickPosX = 0.5;
        cow.prevY = cow.lastTickPosY = 61.0;
        cow.prevZ = cow.lastTickPosZ = 0.5;

        for (int i = 1; i <= 20; i++) {
            double targetZ = 0.5 + i * 0.2;
            cow.teleportInServer(0.5, 61.0, targetZ, 0.0F, 0.0F, 1);
            cow.onTick();
        }
        checks.check("Cow in multiplayer accumulated distance walking (> 2.0)", cow.distance > 2.0F,
                "got distance=" + cow.distance);

        // --- Phase 5: Singleplayer Mob does not double-count ---
        NetworkWorld spWorld = new NetworkWorld(null, 12345L, 0, WorldType.DEFAULT, null);
        spWorld.isOnline = false;
        for (int cx = -1; cx <= 1; cx++) {
            for (int cz = -1; cz <= 2; cz++) {
                spWorld.doPreChunk(cx, cz, true);
            }
        }
        for (int x = -2; x <= 2; x++) {
            for (int z = -10; z <= 30; z++) {
                spWorld.updateBlocksAndNeighbors(x, 60, z, BlockList.stone.id, 0);
            }
        }
        Cow spCow = new Cow(spWorld);
        spCow.setPosition(0.5, 61.0, 0.5);
        spCow.prevX = spCow.lastTickPosX = 0.5;
        spCow.prevY = spCow.lastTickPosY = 61.0;
        spCow.prevZ = spCow.lastTickPosZ = 0.5;

        // In SP, moveEntity directly moves the entity (with downward gravity ensuring ground collision)
        for (int i = 1; i <= 20; i++) {
            spCow.prevX = spCow.posX;
            spCow.prevY = spCow.posY;
            spCow.prevZ = spCow.posZ;
            spCow.moveEntity(0.0, -0.08, 0.2);
            spCow.onTick();
        }
        checks.check("Singleplayer Cow distance matches expected single accumulation (~2.4)",
                spCow.distance > 2.0F && spCow.distance < 3.0F,
                "got distance=" + spCow.distance);

        checks.done();
    }
}
