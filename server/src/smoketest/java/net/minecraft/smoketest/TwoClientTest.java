package net.minecraft.smoketest;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.network.packet.misc.ChatPacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.player.PlayerInfoPacket;
import net.minecraft.network.packet.player.PlayerSpawnPacket;

/**
 * Headless two-client multiplayer test. Covers the parts of baseline tests B1/B3 that
 * don't need a rendered window:
 *
 *   B1  both clients complete login concurrently
 *   B3  chat from one client is relayed to the other and to the console
 *   +   each client is told about the other (PlayerInfoPacket / PlayerSpawnPacket)
 *
 * Run against an ALREADY-RUNNING server:  ./gradlew :server:smokeTest
 *
 * IMPORTANT: each bot runs in its own JVM, and that is not incidental. Several packet
 * classes (UpdateChunkPacket, UpdateSectionPacket) decode through a *static* scratch
 * buffer, so two clients decoding chunks concurrently inside one JVM corrupt each
 * other's data and fail with "Bad compressed data format". One client per process is
 * the arrangement the real game uses, and the only safe one.
 */
public class TwoClientTest {

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0].equals("--bot")) {
            // args: --bot <name> <host> <port> <chatAfterMs|-1> <runForMs>
            runBot(args[1], args[2], Integer.parseInt(args[3]),
                    Long.parseLong(args[4]), Long.parseLong(args[5]));
            return;
        }

        String host = args.length > 0 ? args[0] : "127.0.0.1";
        String port = args.length > 1 ? args[1] : "25565";

        // Alice joins first and chats at t+6s. Bob joins at t+2s and just listens.
        Proc alice = Proc.spawn("Alice", host, port, 6000, 11000);
        Thread.sleep(2000);
        Proc bob = Proc.spawn("Bob", host, port, -1, 9000);

        alice.await();
        bob.await();

        System.out.println("---- Alice ----");
        for (String l : alice.lines) System.out.println("  " + l);
        System.out.println("---- Bob ----");
        for (String l : bob.lines) System.out.println("  " + l);
        System.out.println("---------------");

        String sent = alice.value("SENT_CHAT");
        Checks checks = new Checks();

        checks.check("B1  both clients logged in",
                alice.value("ENTITY_ID") != null && bob.value("ENTITY_ID") != null
                        && !alice.value("ENTITY_ID").equals(bob.value("ENTITY_ID")),
                "alice=" + alice.value("ENTITY_ID") + " bob=" + bob.value("ENTITY_ID"));

        checks.check("B1  both received chunk data",
                "true".equals(alice.value("GOT_CHUNKS")) && "true".equals(bob.value("GOT_CHUNKS")),
                "alice=" + alice.value("GOT_CHUNKS") + " bob=" + bob.value("GOT_CHUNKS"));

        checks.check("B3  Bob received Alice's chat",
                sent != null && bob.has("CHAT", sent), "sent=" + sent);

        checks.check("    Bob sees Alice in the player list",
                bob.has("PLAYER_INFO", "Alice") || bob.has("PLAYER_SPAWN", "Alice"));

        checks.check("    Alice sees Bob join",
                alice.has("PLAYER_INFO", "Bob") || alice.has("PLAYER_SPAWN", "Bob"));

        checks.check("    join message broadcast to Alice",
                alice.has("CHAT", "Bob") && alice.has("CHAT", "joined"));

        checks.check("    no decode errors",
                alice.value("ERROR") == null && bob.value("ERROR") == null,
                "alice=" + alice.value("ERROR") + " bob=" + bob.value("ERROR"));

        checks.done();
    }

    // ---------------------------------------------------------------- bot subprocess

    /** One bot, one JVM. Emits "KEY<TAB>value" lines on stdout for the parent to assert on. */
    private static void runBot(String name, String host, int port, long chatAfterMs, long runForMs)
            throws Exception {
        Bot bot;
        try {
            bot = Bot.join(host, port, name);
        } catch (Exception e) {
            emit("ERROR", e.getMessage() == null ? e.toString() : e.getMessage());
            return;
        }

        emit("SERVER_ID", bot.serverId);
        emit("ENTITY_ID", String.valueOf(bot.entityId));
        emit("DIMENSION", String.valueOf(bot.login.dimension));
        emit("MAX_SECTIONS", String.valueOf(bot.login.maxSections));

        long start = System.currentTimeMillis();
        long deadline = start + runForMs;
        boolean chatted = false;
        boolean gotChunks = false;

        while (System.currentTimeMillis() < deadline) {
            if (!chatted && chatAfterMs >= 0 && System.currentTimeMillis() - start >= chatAfterMs) {
                String msg = "hello from " + name + " " + System.currentTimeMillis();
                bot.chat(msg);
                emit("SENT_CHAT", msg);
                chatted = true;
            }

            Packet next;
            try {
                next = bot.read();
            } catch (Exception e) {
                emit("ERROR", e.toString());
                break;
            }
            if (bot.kickReason != null) {
                emit("ERROR", "kicked: " + bot.kickReason);
                break;
            }
            if (next == null) {
                continue;
            }
            if (next instanceof ChatPacket) {
                emit("CHAT", ((ChatPacket) next).message);
            } else if (next instanceof PlayerInfoPacket) {
                PlayerInfoPacket pi = (PlayerInfoPacket) next;
                if (pi.isConnected) emit("PLAYER_INFO", pi.playerName);
            } else if (next instanceof PlayerSpawnPacket) {
                emit("PLAYER_SPAWN", ((PlayerSpawnPacket) next).name);
            } else {
                String n = next.getClass().getSimpleName();
                if (n.equals("UpdateChunkPacket") || n.equals("PrepChunkPacket")) gotChunks = true;
            }
        }
        emit("GOT_CHUNKS", String.valueOf(gotChunks));
        emit("DONE", name);
        bot.close();
    }

    private static void emit(String key, String value) {
        System.out.println(key + "\t" + value.replace('\n', ' '));
        System.out.flush();
    }

    // ---------------------------------------------------------------- process helper

    private static class Proc {
        final Process process;
        final List<String> lines = new ArrayList<String>();
        Thread pump;

        private Proc(Process process) {
            this.process = process;
        }

        static Proc spawn(String name, String host, String port, long chatAfterMs, long runForMs)
                throws Exception {
            String java = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
            List<String> cmd = new ArrayList<String>(Arrays.asList(
                    java, "-cp", System.getProperty("java.class.path"),
                    TwoClientTest.class.getName(),
                    "--bot", name, host, port,
                    String.valueOf(chatAfterMs), String.valueOf(runForMs)));
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true);
            final Proc proc = new Proc(pb.start());
            proc.pump = new Thread(new Runnable() {
                public void run() {
                    try {
                        BufferedReader r = new BufferedReader(
                                new InputStreamReader(proc.process.getInputStream()));
                        String line;
                        while ((line = r.readLine()) != null) {
                            synchronized (proc.lines) { proc.lines.add(line); }
                        }
                    } catch (Exception ignored) {
                    }
                }
            });
            proc.pump.setDaemon(true);
            proc.pump.start();
            return proc;
        }

        void await() throws Exception {
            process.waitFor();
            pump.join(2000);
        }

        String value(String key) {
            synchronized (lines) {
                for (String l : lines) {
                    if (l.startsWith(key + "\t")) return l.substring(key.length() + 1);
                }
            }
            return null;
        }

        boolean has(String key, String substring) {
            synchronized (lines) {
                for (String l : lines) {
                    if (l.startsWith(key + "\t") && l.contains(substring)) return true;
                }
            }
            return false;
        }
    }
}
