package net.minecraft.smoketest;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.packet.player.PlayerStatePacket;

/**
 * Checks crawl on an ALREADY-RUNNING server; no op needed.
 *
 * What the player-visible bug was: setCrawling(false) restored 1.8m height and
 * hopped +1.3 blocks no matter what was overhead, so standing up in a 1-block
 * crawl hole wedged the box into the ceiling. On the next movement packet the
 * server resolved that fall onto the roof instead ("Expected ..." in the log
 * sits exactly one stance above the reported Y), found the position off by
 * more than the 0.0625 threshold, and teleported the client back up - the
 * fall / teleport-back loop the player reported. Server and client also never
 * shared the true crawl state: handlePlayerState stored the raw packet value
 * before setCrawling ran, so a refused toggle was still broadcast as fact.
 *
 * This test holds the mechanism on the wire, not the whole experience (that
 * still needs a human playing):
 *   phase 1 - a client's PlayerStatePacket(2, true/false) is applied to its
 *             server entity and broadcast to other players,
 *   phase 2 - a start/stop pair broadcasts exactly one true and one false.
 *
 * Run: ./gradlew :server:smokeTest -PsmokeClass=net.minecraft.smoketest.CrawlSmokeTest
 */
public class CrawlSmokeTest {

    private static final int CRAWL = 2;

    public static void main(String[] args) throws Exception {
        Checks checks = new Checks();
        broadcastCheck(args, checks);
        refusedUncrawlCheck(args, checks);
        checks.done();
    }

    // A watcher must see the same crawl state the server entity ends up in.
    private static void broadcastCheck(String[] args, Checks checks) throws Exception {
        Bot crawler = Bot.join(args, "Crawler");
        Bot watcher = Bot.join(args, "CrawlWatcher");

        try {
            crawler.settle(4000);

            if (!checks.check("watcher sees the crawler spawn", watcher.awaitSpawnOf(crawler, 15000),
                    watcher.kickReason != null ? "watcher kicked: " + watcher.kickReason
                            : "no PlayerSpawnPacket for entity " + crawler.entityId + " within 15s")) {
                return;
            }

            crawler.sendState(CRAWL, true);
            PlayerStatePacket started = awaitCrawlState(watcher, crawler);
            checks.check("crawl start broadcast to nearby players", started != null && started.state,
                    started == null ? "no PlayerStatePacket(type=2) within 10s" : "broadcast said false");

            crawler.sendState(CRAWL, false);
            PlayerStatePacket stopped = awaitCrawlState(watcher, crawler);
            checks.check("crawl stop broadcast to nearby players", stopped != null && !stopped.state,
                    stopped == null ? "no PlayerStatePacket(type=2) within 10s" : "broadcast said true");
        } finally {
            crawler.close();
            watcher.close();
        }
    }

    // The headroom refusal in setCrawling has no bot-reachable ground truth -
    // a script would have to stand on land, dig a ceiling and flip setblocks,
    // which needs op. This phase holds the other half that keeps the two sides
    // coherent, read straight off the wire: sendStatePacket only fires on a
    // state change, so a repeated toggle must produce nothing extra. Standing
    // up out of the hole is the play test.
    private static void refusedUncrawlCheck(String[] args, Checks checks) throws Exception {
        Bot crawler = Bot.join(args, "Crawler");
        Bot watcher = Bot.join(args, "CrawlWatcher");

        try {
            if (!checks.check("watcher sees the crawler spawn", watcher.awaitSpawnOf(crawler, 15000),
                    "no PlayerSpawnPacket for entity " + crawler.entityId + " within 15s")) {
                return;
            }

            crawler.sendState(CRAWL, true);
            crawler.sendState(CRAWL, true);
            crawler.sendState(CRAWL, false);
            crawler.sendState(CRAWL, false);

            List<PlayerStatePacket> broadcast = watcher.awaitAll(PlayerStatePacket.class,
                    p -> p.entityId == crawler.entityId && p.type == CRAWL, 4000);

            List<Boolean> states = new ArrayList<Boolean>();
            for (PlayerStatePacket packet : broadcast) {
                states.add(packet.state);
            }
            boolean singlePair = states.size() == 2 && states.get(0) && !states.get(1);
            checks.check("crawl broadcasts exactly one start then one stop", singlePair,
                    "saw " + states + " - a duplicate or extra state reached other players");
        } finally {
            crawler.close();
            watcher.close();
        }
    }

    private static PlayerStatePacket awaitCrawlState(Bot watcher, Bot crawler) throws Exception {
        return watcher.await(PlayerStatePacket.class,
                p -> p.entityId == crawler.entityId && p.type == CRAWL, 10000);
    }
}
