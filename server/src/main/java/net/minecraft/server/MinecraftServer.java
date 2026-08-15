package net.minecraft.server;

import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.SharedConstants;
import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandHolder;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.ConfigManager;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSettings;
import net.minecraft.game.world.WorldType;
import net.minecraft.game.world.chunk.loader.handler.AnvilSaveHandler;
import net.minecraft.game.world.util.Position;
import net.minecraft.network.packet.world.UpdateTimePacket;
import net.minecraft.network.packet.world.WeatherPacket;
import net.minecraft.server.commands.CommandUseTimer;
import net.minecraft.server.commands.ServerCommandFactory;
import net.minecraft.server.entity.EntityTracker;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.server.gui.ServerGUI;
import net.minecraft.server.network.NetworkListener;
import net.minecraft.server.world.WorldManager;
import net.minecraft.server.world.WorldServer;
import net.minecraft.util.ProgressBar;
import net.minecraft.util.logging.Logging;
import net.minecraft.util.logging.LoggingUtils;
import net.minecraft.util.property.PropertiesConfig;
import net.minecraft.util.property.PropertySource;

public class MinecraftServer implements Runnable, CommandAccessor, CommandSource {
    // Overworld=0, Hell=1, Sky=2, Void=3, Ocean=4. Dense and fixed, so every per-dimension
    // collection below is a plain array indexed straight by dimension id.
    public static final int DIMENSION_COUNT = 5;
    public static File workingDirectory = new File(System.getProperty("user.dir", "."));
    public static Logger logger;
    public NetworkListener networkListener;
    public PropertiesConfig properties;
    public WorldServer[] worlds;
    public ServerConfigurationManager configManager;
    private boolean serverRunning = true;
    public boolean serverStopped = false;
    int tickTime = 0;
    public String currentTask;
    public int percentDone;
    private List<IUpdatePlayerListBox> updatePlayerList = new ArrayList<>();
    private List<CommandHolder> commands = Collections.synchronizedList(new ArrayList<>());
    public EntityTracker[] entityTracker = new EntityTracker[DIMENSION_COUNT];
    public boolean onlineMode;
    public boolean spawnPeacefulMobs;
    public boolean pvpOn;
    public boolean allowFlight;
    public boolean allowDimensions;
    public boolean quitOnException = false;
    public int spawnProtectionRadius;
    public String serverName;
    public String motd;
    public int serverPort;
    public int autosaveinterval;
    public List<Runnable> mainThreadEvents = Collections.synchronizedList(new ArrayList<>());

    public MinecraftServer() {
        ServerCommandFactory.init();
        new Thread("Timer hack thread") {
            {
                this.setDaemon(true);
                this.start();
            }

            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(2147483647L);
                    } catch (InterruptedException var2) {
                    }
                }
            }
        };
    }

    private boolean startServer() throws UnknownHostException {
        Thread thred = new Thread() {
            @Override
            public void run() {
                BufferedReader bufferedReader1 = new BufferedReader(new InputStreamReader(System.in));
                String string2 = null;

                try {
                    while (!MinecraftServer.this.serverStopped && MinecraftServer.this.serverRunning && (string2 = bufferedReader1.readLine()) != null) {
                        if (string2.startsWith("/")) {
                            string2 = string2.substring(1);
                        }

                        MinecraftServer.this.addCommand(string2, MinecraftServer.this);
                    }
                } catch (IOException iOException4) {
                    iOException4.printStackTrace();
                }
            }
        };
        thred.setDaemon(true);
        thred.start();
        logger.info("Starting minecraft server version " + SharedConstants.FULL_VER_STRING);
        if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L) {
            logger.warning("**** NOT ENOUGH RAM!");
            logger.warning("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
        }

        logger.info("Loading properties");
        File propertiesFile = new File("server.properties");
        boolean freshInstall = !propertiesFile.exists();
        this.properties = new PropertiesConfig(propertiesFile, "Minecraft server properties");
        this.serverName = this.properties.get("server-name", "An Infdev+ Server");
        this.motd = this.properties.get("motd", "Yup... still an infdev+ server");
        this.quitOnException = this.properties.get("quit-server-on-exception", false);
        this.spawnProtectionRadius = this.properties.get("spawn-protection-radius", -1);
        this.autosaveinterval = this.properties.get("autosave-interval-ticks", 256);
        String string2 = this.properties.get("server-ip", "");
        this.onlineMode = this.properties.get("online-mode", true);
        if (freshInstall) {
            this.properties.set("online-mode", this.onlineMode);
        }
        this.spawnPeacefulMobs = this.properties.get("spawn-animals", true);
        this.pvpOn = this.properties.get("pvp", true);
        this.allowFlight = this.properties.get("allow-flight", false);
        // Renamed from "allow-nether": this mod has four non-overworld dimensions, not one.
        // The old key is still honoured so existing server.properties files keep working.
        // Note this gates *travel into* other dimensions, not whether they tick — all five
        // worlds always tick, so a player whose save puts them in Hell isn't stranded in a
        // frozen world with no obvious cause.
        this.allowDimensions = this.properties.get("allow-dimensions", this.properties.get("allow-nether", true));
        InetAddress inetAddress3 = null;
        if (string2.length() > 0) {
            inetAddress3 = InetAddress.getByName(string2);
        }

        this.serverPort = this.properties.get("server-port", 25565);
        logger.info("Starting Minecraft server on " + (string2.length() == 0 ? "*" : string2) + ":" + this.serverPort);

        try {
            this.networkListener = new NetworkListener(this, inetAddress3, this.serverPort);
        } catch (IOException iOException13) {
            logger.warning("**** FAILED TO BIND TO PORT!");
            logger.log(Level.WARNING, "The exception was: " + iOException13.toString());
            logger.warning("Perhaps a server is already running on that port?");
            if (this.quitOnException) {
                this.serverRunning = false;
            }

            return false;
        }

        if (!this.onlineMode) {
            logger.warning("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
            logger.warning("The server will make no attempt to authenticate usernames. Beware.");
            logger.warning(
                "While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose."
            );
            logger.warning("To change this, set \"online-mode\" to \"true\" in the server.properties file.");
        }

        this.configManager = new ServerConfigurationManager(this);

        // Must stay after configManager: EntityTracker's constructor asks it for the max
        // tracking distance.
        for (int d = 0; d < this.entityTracker.length; d++) {
            this.entityTracker[d] = new EntityTracker(this, d);
        }

        long j5 = System.nanoTime();
        String string7 = this.properties.get("level-name", "world");
        String string8 = this.properties.get("level-seed", "");
        long j9 = new Random().nextLong();
        if (string8.length() > 0) {
            try {
                j9 = Long.parseLong(string8);
            } catch (NumberFormatException numberFormatException12) {
                j9 = string8.hashCode();
            }
        }

        logger.info("Preparing level \"" + string7 + "\"");
        this.initWorld(string7, j9);
        logger.info("Done (" + (System.nanoTime() - j5) + "ns)! For help, type \"help\" or \"?\"");
        return true;
    }

    private void initWorld(String string2, long j3) {
        this.worlds = new WorldServer[DIMENSION_COUNT];
        WorldSettings ws = new WorldSettings();
        WorldSettings.gamemode = this.properties.get("gamemode", 0).byteValue();
        WorldSettings.cheats = true;
        WorldSettings.structures = this.properties.get("generate-structures", true);
        WorldSettings.heightInt = this.properties.get("full-height", true) ? 20 : 12;
        WorldType wt = WorldType.fromString(this.properties.get("world-type", "DEFAULT"));
        if (wt == null) {
            wt = WorldType.DEFAULT;
            logger.warning("The specified world type was invalid, using DEFAULT instead");
        }

        WorldSettings.worldType = wt;
        byte difficulty = 0;
        difficulty = this.properties.get("difficulty-setting", 1).byteValue();
        boolean monsters = this.properties.get("spawn-monsters", true);
        if (monsters && difficulty == 0) {
            monsters = false;
        }

        WorldSettings.difficulty = difficulty;
        // ONE save handler for all five worlds. SaveHandlerBase's constructor writes its own
        // timestamp into <world>/session.lock and checkSessionLock() compares against that
        // instance's field, so five handlers would mean the last one wins and the other four
        // die with LevelConflictException on their first chunk save. AnvilSaveHandler already
        // routes per-dimension via Dimension.getSaveFolder() ("" / DIM1..DIM4), so a single
        // handler serves every world correctly.
        AnvilSaveHandler save = new AnvilSaveHandler(workingDirectory, string2, true);
        this.worlds[0] = new WorldServer(this, save, string2, 0, j3, ws);
        // Flush level.dat before dimensions 1..4 read it. World's constructor treats a missing
        // level.dat as "brand new world" and runs the expensive spawn search; without this,
        // a fresh server would run that search five times and land on five different spawns.
        this.worlds[0].saveData();

        for (int d = 1; d < this.worlds.length; d++) {
            this.worlds[d] = new WorldServer(this, save, string2, d, j3, ws);
            // Every World constructor builds its own MapStorage, and MapStorage keeps a private
            // id counter that getUniqueId() rewrites into one shared data/idcounts.dat. Five of
            // them over one directory hand out colliding map ids and clobber each other's file.
            this.worlds[d].mapStorage = this.worlds[0].mapStorage;
        }

        for (int d = 0; d < this.worlds.length; d++) {
            this.worlds[d].addListener(new WorldManager(this, this.worlds[d]));
            this.worlds[d].setAllowedSpawnTypes(monsters, this.spawnPeacefulMobs);
            this.worlds[d].setMaxSections(WorldSettings.heightInt);
        }

        this.configManager.setPlayerNbtStorage(this.worlds);
        // Only the Overworld is preloaded. Constructing the other four is nearly free — one
        // level.dat read plus a chunk provider — because none of them generate terrain until
        // someone actually stands in them.
        this.preloadSpawnRegion(0);
        this.clearCurrentTask();
    }

    private void preloadSpawnRegion(int dimension) {
        logger.info("Preparing start region for level " + dimension);
        short s18 = 196;
        long j7 = System.currentTimeMillis();
        WorldServer worldServer10 = this.worlds[dimension];
        Position chunkCoordinates11 = worldServer10.getSpawnPoint();

        for (int i12 = -s18; i12 <= s18 && this.serverRunning; i12 += 16) {
            for (int i13 = -s18; i13 <= s18 && this.serverRunning; i13 += 16) {
                long j14 = System.currentTimeMillis();
                if (j14 < j7) {
                    j7 = j14;
                }

                if (j14 > j7 + 1000L) {
                    int i16 = (s18 * 2 + 1) * (s18 * 2 + 1);
                    int i17 = (i12 + s18) * (s18 * 2 + 1) + i13 + 1;
                    this.outputPercentRemaining("Preparing spawn area", i17 * 100 / i16);
                    j7 = j14;
                }

                worldServer10.chunkProviderServer.loadChunk(chunkCoordinates11.x + i12 >> 4, chunkCoordinates11.z + i13 >> 4);
            }
        }
    }

    private void outputPercentRemaining(String string1, int i2) {
        this.currentTask = string1;
        this.percentDone = i2;
        logger.info(string1 + ": " + i2 + "%");
    }

    private void clearCurrentTask() {
        this.currentTask = null;
        this.percentDone = 0;
    }

    private void saveServerWorld() {
        logger.info("Saving chunks");

        WorldServer lastSaved = null;

        for (int i1 = 0; i1 < this.worlds.length; i1++) {
            WorldServer worldServer2 = this.worlds[i1];
            if (worldServer2 != null) {
                worldServer2.save(true, (ProgressBar)null);
                lastSaved = worldServer2;
            }
        }

        // postSaveCleanup() drains the shared IO thread and clears the global RegionFileCache;
        // it isn't per-world, so it runs once at the end rather than five times.
        if (lastSaved != null) {
            lastSaved.postSaveCleanup();
        }
    }

    private void stopServer() {
        logger.info("Stopping server");
        if (this.configManager != null) {
            this.configManager.savePlayerStates();
        }

        // saveServerWorld() already loops every world, so calling it once per world saved
        // each world N times. Harmless at N=1, quadratic once there are five dimensions.
        if (this.worlds != null) {
            this.saveServerWorld();
        }
    }

    @Override
    public void initiateShutdown() {
        this.serverRunning = false;
    }

    @Override
    public void run() {
        try {
            if (this.startServer()) {
                long j1 = System.currentTimeMillis();
                long j3 = 0L;

                while (this.serverRunning) {
                    long j5 = System.currentTimeMillis();
                    long j7 = j5 - j1;
                    if (j7 > 2000L) {
                        logger.warning("Can't keep up! Did the system time change, or is the server overloaded?");
                        j7 = 2000L;
                    }

                    if (j7 < 0L) {
                        logger.warning("Time ran backwards! Did the system time change?");
                        j7 = 0L;
                    }

                    j3 += j7;
                    j1 = j5;
                    if (this.worlds[0].playersSleeping()) {
                        this.doTick();
                        j3 = 0L;
                    } else {
                        while (j3 > 50L) {
                            j3 -= 50L;
                            this.doTick();
                        }
                    }

                    Thread.sleep(1L);
                }
            } else {
                while (this.serverRunning) {
                    this.commandLineParser();

                    try {
                        Thread.sleep(10L);
                    } catch (InterruptedException var57) {
                    }
                }
            }
        } catch (Throwable throwable58) {
            logger.log(Level.SEVERE, "Unexpected exception", throwable58);
            if (this.quitOnException) {
                this.serverRunning = false;
            }

            while (this.serverRunning) {
                this.commandLineParser();

                try {
                    Thread.sleep(10L);
                } catch (InterruptedException var56) {
                }
            }
        } finally {
            try {
                this.stopServer();
                this.serverStopped = true;
            } catch (Throwable throwable54) {
                LoggingUtils.warnThrowable(logger, "Unexpected exception while stopping server", throwable54);
            } finally {
                System.exit(0);
            }
        }
    }

    private void doTick() {
        if (this.mainThreadEvents.size() > 0) {
            Runnable r = this.mainThreadEvents.remove(0);
            r.run();
        }

        this.tickTime++;

        for (int i6 = 0; i6 < this.worlds.length; i6++) {
            WorldServer worldServer7 = this.worlds[i6];
            if (this.tickTime % 20 == 0) {
                this.configManager.sendPacketToAllDimension(new UpdateTimePacket(worldServer7.getWorldTime()), i6);
            }

            worldServer7.tickEntities();
            worldServer7.tick();
            if (worldServer7.weatherChanged()) {
                this.configManager
                    .sendPacketToAllDimension(
                        new WeatherPacket(worldServer7.getSettings().getIsRaining(), worldServer7.getSettings().getIsThundering()), i6
                    );
            }
        }

        // Every world advances its own clock, so without this Hell drifts away from the
        // Overworld. Done after the loop, not before: worlds[0] has already ticked by now, and
        // copying its time in beforehand would leave the others incrementing on top of an
        // already-incremented value and running permanently one tick ahead.
        long worldTime = this.worlds[0].getWorldTime();

        for (int d = 1; d < this.worlds.length; d++) {
            this.worlds[d].settings.setWorldTime(worldTime);
        }

        CommandUseTimer.tick();
        this.networkListener.handleNetworkListenThread();
        this.configManager.onTick();

        for (int var5 = 0; var5 < this.entityTracker.length; var5++) {
            this.entityTracker[var5].updateTrackedEntities();
        }

        for (int var6 = 0; var6 < this.updatePlayerList.size(); var6++) {
            this.updatePlayerList.get(var6).update();
        }

        try {
            this.commandLineParser();
        } catch (Exception exception5) {
            logger.log(Level.WARNING, "Unexpected exception while parsing console command", exception5);
        }
    }

    public void addCommand(String string1, CommandSource cs) {
        this.commands.add(new CommandHolder(string1, cs));
    }

    public void commandLineParser() {
        while (this.commands.size() > 0) {
            CommandHolder ch = this.commands.remove(0);
            ServerCommandFactory.instance.runCommand(ch, this);
        }
    }

    public void addPlayerToList(IUpdatePlayerListBox iUpdatePlayerListBox1) {
        this.updatePlayerList.add(iUpdatePlayerListBox1);
    }

    public static void main(String[] args) {
        try {
            MinecraftServer srv = new MinecraftServer();
            if (!GraphicsEnvironment.isHeadless() && (args.length <= 0 || !args[0].equals("nogui"))) {
                ServerGUI.initGui(srv);
            }

            Thread thred = new Thread(srv, "Server Thread");
            thred.setPriority(10);
            thred.start();
        } catch (Exception exception2) {
            logger.log(Level.SEVERE, "Failed to start the minecraft server", exception2);
        }
    }

    public File getFile(String string1) {
        return new File(string1);
    }

    @Override
    public void print(String string1) {
        logger.info(SharedConstants.deleteColorCodes(string1));
    }

    public void logWarning(String string1) {
        logger.warning(string1);
    }

    @Override
    public String getUsername() {
        return "CONSOLE";
    }

    // Both accessors clamp instead of throwing. Dimension ids reach them from player NBT and
    // from packet payloads, so a corrupt id should drop the player into the Overworld, not
    // take the server thread down from inside a packet handler.
    public WorldServer getWorldManager(int dimension) {
        return this.worlds[dimension >= 0 && dimension < this.worlds.length ? dimension : 0];
    }

    public EntityTracker getEntityTracker(int dimension) {
        return this.entityTracker[dimension >= 0 && dimension < this.entityTracker.length ? dimension : 0];
    }

    @Override
    public World getWorld(int dimension) {
        return this.getWorldManager(dimension);
    }

    @Override
    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    @Override
    public boolean isServer() {
        return true;
    }

    @Override
    public Command.UserType getType() {
        return Command.UserType.ADMINISTRATOR;
    }

    @Override
    public boolean canRunCommand(Command c) {
        return true;
    }

    @Override
    public int getWorldCount() {
        return this.worlds.length;
    }

    @Override
    public PropertySource getProperties() {
        return this.properties;
    }

    static {
        Logging.init(logger = Logger.getLogger("Minecraft"), workingDirectory, false);
    }
}
