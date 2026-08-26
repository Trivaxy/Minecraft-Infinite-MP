package net.minecraft.smoketest;

import net.minecraft.client.network.world.NetworkWorld;
import net.minecraft.game.world.WorldType;
import net.minecraft.network.packet.misc.LoginPacket;
import net.minecraft.network.packet.world.WeatherPacket;

/**
 * B-25 - weather never reached multiplayer clients. updateWeather() runs only out of
 * World.tick(), NetworkWorld.tick() deliberately does not call super.tick(), and nothing on the
 * wire carried the raining/thundering flags, so rainingStrength sat at 0 on every client for the
 * life of the session while the server ran a real weather cycle. No rain, no storm darkening,
 * while the block changes it causes (snow, ice) arrived normally - weather under a clear sky.
 *
 * Two halves, and each one goes red on its own if you revert its half of the fix:
 *  1. The wire. A WeatherPacket lands at login, and again when /weather flips the server's state.
 *     Red if MinecraftServer/ServerConfigurationManager stop sending it.
 *  2. The client ramp. NetworkWorld.tick() advances rainingStrength off those flags at the same
 *     0.01/tick World.updateWeather() uses. Red if the updateWeatherStrength() call is dropped
 *     out of NetworkWorld.tick().
 *
 * Needs a running server with `weatherbot` in run/server/ops.txt - /weather is operator-only, and
 * an unopped bot gets a permission error and no state change, which looks like a protocol bug.
 */
public class WeatherSmokeTest {

    /** World.updateWeather's ramp rate, and so how many ticks 0 -> full takes. */
    private static final int RAMP_TICKS = 100;

    public static void main(String[] args) throws Exception {
        Bot bot = Bot.join(args, "weatherbot");
        try {
            Checks checks = new Checks();

            checkWire(bot, checks);
            checkClientRamp(bot.login, checks);

            checks.done();
        } finally {
            bot.close();
        }
    }

    private static void checkWire(Bot bot, Checks checks) throws Exception {
        // Awaited before settling: the login weather packet arrives in the first burst, and a
        // settle() would read past it.
        WeatherPacket atLogin = bot.await(WeatherPacket.class, 5000);
        if (!checks.check("a WeatherPacket arrives at login", atLogin != null)) {
            checks.abort("no weather on the wire at all - is weatherbot in run/server/ops.txt "
                    + "and was the server restarted after adding it?");
        }
        checks.note("login weather: raining=" + atLogin.raining + " thundering=" + atLogin.thundering);
        bot.settle(1000);

        // Known baseline, then flip it. /weather clear on an already-clear world is a no-op that
        // sends nothing, which is correct and is why only the rain leg is asserted on.
        bot.chat("/weather clear");
        bot.drain(1500);

        bot.chat("/weather rain");
        WeatherPacket afterRain = bot.await(WeatherPacket.class, p -> p.raining, 5000);
        checks.check("/weather rain reaches the client as raining=true", afterRain != null);

        bot.chat("/weather clear");
        WeatherPacket afterClear = bot.await(WeatherPacket.class, p -> !p.raining, 5000);
        checks.check("/weather clear reaches the client as raining=false", afterClear != null);
    }

    /**
     * Ticks a client world the way the game does. NetworkWorld.tick() ends in
     * connection.process(), which throws on this connectionless probe world - the weather ramp
     * runs before that, so the throw is caught and the strength is what is under test. If the
     * ramp is not wired into tick() the strength never leaves 0 and the checks below go red.
     */
    private static void checkClientRamp(LoginPacket login, Checks checks) {
        NetworkWorld world =
            new NetworkWorld(null, login.mapSeed, login.dimension, WorldType.fromNumber(login.worldType), null);

        checks.check("a fresh client world starts dry", world.getRainStrength(1.0F) == 0.0F,
            "was " + world.getRainStrength(1.0F));

        world.getSettings().setIsRaining(true);
        float afterOne = tick(world, 1);
        checks.check("one tick of raining ramps the strength up by 0.01",
            Math.abs(afterOne - 0.01F) < 0.001F, "was " + afterOne);

        float ramped = tick(world, RAMP_TICKS);
        checks.check("the strength reaches full rain within " + RAMP_TICKS + " ticks",
            ramped == 1.0F, "was " + ramped);

        // The way up clamps at 1.0 so it lands exact; the way down does not, and 200 accumulated
        // additions of 0.01 leave ~7e-7 behind. Singleplayer has always drifted the same way.
        world.getSettings().setIsRaining(false);
        float cleared = tick(world, RAMP_TICKS);
        checks.check("clearing the flag ramps the strength back to dry",
            cleared < 0.001F, "was " + cleared);
    }

    private static float tick(NetworkWorld world, int ticks) {
        for (int i = 0; i < ticks; i++) {
            try {
                world.tick();
            } catch (NullPointerException expected) {
                // connection.process() on a probe world with no connection; the ramp already ran
            }
        }

        return world.getRainStrength(1.0F);
    }
}
