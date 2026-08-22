package net.minecraft.client;

import java.awt.Canvas;
import java.awt.EventQueue;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.Profiler;
import net.minecraft.SharedConstants;
import net.minecraft.client.commands.ClientCommands;
import net.minecraft.client.gamemode.GameMode;
import net.minecraft.client.gui.ScreenRescaler;
import net.minecraft.client.gui.overlay.HeadsUpDisplay;
import net.minecraft.client.gui.screens.GuiScreen;
import net.minecraft.client.gui.screens.game.DeathScreen;
import net.minecraft.client.gui.screens.game.ModeSwitcher;
import net.minecraft.client.gui.screens.game.PauseScreen;
import net.minecraft.client.gui.screens.multiplayer.ChatScreen;
import net.minecraft.client.gui.screens.multiplayer.SleepScreen;
import net.minecraft.client.gui.screens.title.LoadingScreen;
import net.minecraft.client.gui.screens.title.SplashScreen;
import net.minecraft.client.gui.screens.title.TitleScreen;
import net.minecraft.client.localization.Font;
import net.minecraft.client.localization.LanguageList;
import net.minecraft.client.network.ClientHandler;
import net.minecraft.client.network.player.NetworkPlayer;
import net.minecraft.client.options.KeyBind;
import net.minecraft.client.options.OptionValues;
import net.minecraft.client.options.OptionsList;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.PlayerController;
import net.minecraft.client.render.Renderer;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.ParticleRenderer;
import net.minecraft.client.render.environment.SkyRenderer;
import net.minecraft.client.render.game.GameRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.texture.TextureLoader;
import net.minecraft.client.render.texture.TextureManager;
import net.minecraft.client.render.texture.pack.PackList;
import net.minecraft.client.render.texture.pack.TexturePack;
import net.minecraft.client.render.world.ChunkRenderer;
import net.minecraft.client.render.world.WorldRenderer;
import net.minecraft.client.sound.SoundDownloader;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.statistics.StatWriter;
import net.minecraft.commands.Commands;
import net.minecraft.game.player.ConfigManager;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.entity.player.PlayerProperties;
import net.minecraft.game.item.staff.GabrielStaff;
import net.minecraft.game.physics.TargetingSystem;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSettings;
import net.minecraft.game.world.chunk.loader.format.AnvilFormat;
import net.minecraft.game.world.chunk.loader.format.SaveFormat;
import net.minecraft.game.world.chunk.loader.handler.SaveHandler;
import net.minecraft.game.world.chunk.loader.handler.SaveHandlerBase;
import net.minecraft.game.world.dimension.Dimension;
import net.minecraft.game.world.generation.features.sky.Cloud;
import net.minecraft.game.world.generation.features.sky.Disk;
import net.minecraft.game.world.util.LightLayer;
import net.minecraft.game.world.util.Position;
import net.minecraft.localization.Translator;
import net.minecraft.statistics.StatList;
import net.minecraft.statistics.achievement.AchievementList;
import net.minecraft.util.logging.Logging;
import net.minecraft.util.logging.LoggingUtils;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;

public class Minecraft implements Runnable {
    private static Minecraft instance;
    public List<Runnable> mainThreadEvents = Collections.synchronizedList(new ArrayList<>());
    private static File directory;
    public ScreenRescaler rescaler;
    public OptionsList options;
    private SoundDownloader downloadResourcesThread;
    public SoundManager sndManager = new SoundManager();
    private ConfigManagerClient configManager = new ConfigManagerClient(this);
    private final Commands commands = new Commands();
    public StatWriter statistics;
    private SplashScreen splash;
    public LoadingScreen loadingScreen = new LoadingScreen(this);
    public int displayWidth;
    public int displayHeight;
    public boolean fullscreen = false;
    public boolean inFocus = false;
    public volatile boolean paused = false;
    public volatile boolean running = true;
    public long systemTime = System.currentTimeMillis();
    public GameMode gamemode;
    public World world;
    public LocalPlayer player;
    public Session session = null;
    public Timer timer = new Timer(20.0F);
    private SaveFormat saveLoader;
    public GuiScreen currentScreen = null;
    public HeadsUpDisplay HUD;
    public TextureLoader textureLoader;
    public WorldRenderer worldRenderer;
    public GameRenderer gameRenderer;
    public ParticleRenderer particleRenderer;
    public SkyRenderer skyRenderer;
    public Font fontRenderer;
    public PackList texturePackList;
    public LanguageList langPackList;
    public TargetingSystem selectedObject = null;
    public MouseHelper mouseHelper;
    public static Logger logger;
    public String debugString = "";
    private long prevTime = System.currentTimeMillis();
    private int fps = 0;
    private boolean preventExit = false;
    private static final boolean USE_APPDATA;
    public static final boolean NO_DOWNLOAD_RESOURCES_ON_START;
    private static final boolean DEV_CRASHSCREEN_BYPASS;
    private static final boolean DISABLE_GLL_ERR_CHECKS;
    static final int DEFAULT_WIDTH = 854;
    static final int DEFAULT_HEIGHT = 480;

    public Minecraft(Canvas ignored, int width, int height, boolean fullscreen) {
        this.displayWidth = width;
        this.displayHeight = height;
        this.fullscreen = fullscreen;
        instance = this;
        StatList.init();
        TextureManager.init();
        ClientCommands.register(this.commands.getDispatcher());
        Commands.setActive(this.commands);
    }

    public static void main(String[] argv) {
        List<String> args = Arrays.asList(argv);
        Minecraft minecraft = new Minecraft(null, 854, 480, false);
        if (args.size() == 1) {
            minecraft.session = new Session(args.get(0), "-");
        } else if (args.size() >= 2) {
            minecraft.session = new Session(args.get(0), args.get(1));

            try {
                String wid = ClientUtils.getArgument(args, "--width");
                String hei = ClientUtils.getArgument(args, "--height");
                minecraft.fullscreen = args.contains("--fullscreen");
                if (wid != null && hei != null) {
                    minecraft.setDisplayResolution(Integer.parseInt(wid), Integer.parseInt(hei));
                }
            } catch (Exception e) {
                LoggingUtils.warnThrowable(logger, "Argument parsing failed for main(), ignoring", e);
            }
        } else {
            minecraft.session = new Session("Player" + System.currentTimeMillis() % 1000L, "-");
        }

        Thread thred = new Thread(minecraft, "Minecraft main thread");
        thred.setPriority(10);
        thred.start();
    }

    public void startGame() throws LWJGLException {
        this.options = new OptionsList(directory);
        this.fullscreen = OptionValues.fullScreen;
        if (this.fullscreen) {
            Display.setDisplayMode(Display.getDesktopDisplayMode());
            Display.setFullscreen(true);
            this.displayWidth = Display.getDisplayMode().getWidth();
            this.displayHeight = Display.getDisplayMode().getHeight();
            if (this.displayWidth <= 0) {
                this.displayWidth = 1;
            }

            if (this.displayHeight <= 0) {
                this.displayHeight = 1;
            }
        } else {
            Display.setDisplayMode(new DisplayMode(this.displayWidth, this.displayHeight));
        }

        Display.setTitle(SharedConstants.FULL_VER_STRING);

        try {
            Display.setIcon(SharedConstants.loadDisplayIcons(Minecraft.class.getResource("/icon32.png"), Minecraft.class.getResource("/icon16.png")));
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to set icon: " + e, e);
        }

        Display.setResizable(true);

        try {
            Display.create(new PixelFormat().withDepthBits(24));
        } catch (LWJGLException e) {
            logger.log(Level.WARNING, "Failed to set bit depth: " + e, e);

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException var4) {
            }

            Display.create();
        }

        this.saveLoader = new AnvilFormat(new File(directory, "saves"));
        this.rescaler = new ScreenRescaler(this.displayWidth, this.displayHeight);
        this.texturePackList = new PackList(this, directory);
        this.langPackList = new LanguageList(this, directory);
        Translator.resetLanguage(this.langPackList.selectedLanguagePack);
        this.textureLoader = new TextureLoader(this.options);
        this.splash = new SplashScreen(this);
        this.splash.displaySplashScreen();
        this.splash.setLoadingProgress(10);
        this.fontRenderer = new Font(this.options, (TexturePack)this.texturePackList.selectedPack, this.textureLoader);
        this.splash.setLoadingProgress(20);
        EntityRenderManager.instance.itemRenderer = new ItemRenderer(this);
        this.splash.setLoadingProgress(30);
        this.statistics = new StatWriter(directory);
        this.splash.setLoadingProgress(40);
        this.gameRenderer = new GameRenderer(this);
        this.splash.setLoadingProgress(50);
        this.mouseHelper = new MouseHelper();
        this.splash.setLoadingProgress(60);
        Keyboard.create();
        Mouse.create();
        this.splash.setLoadingProgress(70);
        if (OptionValues.mouseCursor) {
            this.mouseHelper.setCursor(1);
        }

        this.splash.setLoadingProgress(80);
        if (OptionValues.limitFramerate) {
            Display.setVSyncEnabled(true);
        } else {
            Display.setVSyncEnabled(false);
        }

        this.splash.setLoadingProgress(90);
        this.checkGLError("Pre startup");
        GL11.glEnable(3553);
        GL11.glShadeModel(7425);
        GL11.glClearDepth(1.0);
        GL11.glEnable(2929);
        GL11.glDepthFunc(515);
        GL11.glEnable(3008);
        GL11.glAlphaFunc(516, 0.1F);
        GL11.glCullFace(1029);
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(5888);
        this.checkGLError("Startup");
        this.splash.setLoadingProgress(100);
        this.splash.setLoadingProgress(120);
        this.sndManager.loadSoundSettings(this.options);
        this.splash.setLoadingProgress(140);
        this.worldRenderer = new WorldRenderer(this, this.textureLoader);
        this.splash.setLoadingProgress(160);
        this.textureLoader.refreshTextureMaps();
        this.splash.setLoadingProgress(180);
        GL11.glViewport(0, 0, this.displayWidth, this.displayHeight);
        this.particleRenderer = new ParticleRenderer(this.world, this.textureLoader);
        this.splash.setLoadingProgress(200);
        this.skyRenderer = new SkyRenderer(this, this.textureLoader);
        this.splash.setLoadingProgress(220);

        try {
            this.downloadResourcesThread = new SoundDownloader(directory, this);
            this.downloadResourcesThread.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        this.loadingScreen.updateTips();
        this.checkGLError("Post startup");
        this.HUD = new HeadsUpDisplay(this);
        this.setScreen(new TitleScreen());
    }

    public void initiateShutdown() {
        this.running = false;
    }

    public void showCrashReport(final String description, final Throwable exception) {
        LoggingUtils.severef(logger, exception, "%s: %s", description, exception.toString());
        if (!DEV_CRASHSCREEN_BYPASS) {
            this.preventExit = true;
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    ErrorDialog.showDlg(description, exception);
                }
            });
        }
    }

    public void setDisplayResolution(int width, int height) {
        if (width != 854 && height != 480) {
            this.displayWidth = width;
            this.displayHeight = height;
        }
    }

    public static File getMinecraftDir() {
        if (directory == null) {
            if (USE_APPDATA) {
                directory = getAppDir("minecraft");
            } else {
                directory = new File(System.getProperty("user.dir"));
            }
        }

        return directory;
    }

    public static File getAppDir(String path) {
        String defaultPath = System.getProperty("user.home", ".");
        File directory;
        switch (getOs()) {
            case linux:
            case solaris:
                directory = new File(defaultPath, '.' + path + '/');
                break;
            case windows:
                String string3 = System.getenv("APPDATA");
                if (string3 != null) {
                    directory = new File(string3, "." + path + '/');
                } else {
                    directory = new File(defaultPath, '.' + path + '/');
                }
                break;
            case macos:
                directory = new File(defaultPath, "Library/Application Support/" + path);
                break;
            default:
                directory = new File(defaultPath, path + '/');
        }

        if (!directory.exists() && !directory.mkdirs()) {
            throw new RuntimeException("The working directory could not be created: " + directory);
        } else {
            return directory;
        }
    }

    public static Minecraft.EnumOS getOs() {
        String OSName = System.getProperty("os.name").toLowerCase();
        return OSName.contains("win")
            ? Minecraft.EnumOS.windows
            : (
                OSName.contains("mac")
                    ? Minecraft.EnumOS.macos
                    : (
                        OSName.contains("solaris")
                            ? Minecraft.EnumOS.solaris
                            : (
                                OSName.contains("sunos")
                                    ? Minecraft.EnumOS.solaris
                                    : (
                                        OSName.contains("linux")
                                            ? Minecraft.EnumOS.linux
                                            : (OSName.contains("unix") ? Minecraft.EnumOS.linux : Minecraft.EnumOS.unknown)
                                    )
                            )
                    )
            );
    }

    public SaveFormat getSaveLoader() {
        return this.saveLoader;
    }

    public void setScreen(GuiScreen newScreen) {
        if (this.currentScreen != null) {
            this.currentScreen.onClose();
            this.currentScreen.releaseViewportTexture();
        }

        this.statistics.syncStats();
        if (newScreen == null && this.world == null) {
            newScreen = new TitleScreen();
        } else if (newScreen == null && this.player != null && this.player.health <= 0) {
            newScreen = new DeathScreen();
        }

        this.currentScreen = newScreen;
        if (newScreen != null) {
            this.loseFocus();
            this.rescaler = new ScreenRescaler(this.displayWidth, this.displayHeight);
            newScreen.setResolution(this, this.rescaler.getScaledWidth(), this.rescaler.getScaledHeight());
        } else {
            this.setFocus();
        }
    }

    public void checkGLError(String location) {
        if (!DISABLE_GLL_ERR_CHECKS) {
            int gl_error = GL11.glGetError();
            if (gl_error != 0) {
                String gl_error_string = GLU.gluErrorString(gl_error);
                logger.severe("########## GL ERROR ##########");
                LoggingUtils.severef(logger, "@ %s", location);
                LoggingUtils.severef(logger, "%d: %s", gl_error, gl_error_string);
            }
        }
    }

    public void gameShutdown() {
        try {
            if (this.downloadResourcesThread != null) {
                this.downloadResourcesThread.closeMinecraft();
            }
        } catch (Exception var7) {
        }

        try {
            logger.info("Stopping!");
            this.world.saveAll();
            this.clearWorld();

            try {
                Renderer.clearDisplayLists();
            } catch (Exception var6) {
            }

            this.sndManager.close();
            Mouse.destroy();
            Keyboard.destroy();
        } finally {
            Display.destroy();
            if (!this.preventExit) {
                System.exit(0);
            }
        }

        System.gc();
    }

    @Override
    public void run() {
        this.running = true;

        try {
            this.startGame();
        } catch (Throwable e) {
            this.showCrashReport("Failed to start game", e);
        }

        try {
            while (this.running) {
                this.runLoop();
            }
        } catch (Throwable e) {
            this.clearWorld();
            this.showCrashReport("Exception in game loop", e);
        } finally {
            this.gameShutdown();
        }
    }

    private void runLoop() {
        Profiler.startSection("root");
        if (Display.isCloseRequested()) {
            this.initiateShutdown();
        }

        if (this.world != null && this.paused) {
            float ticks = this.timer.renderPartialTicks;
            this.timer.updateTimer();
            this.timer.renderPartialTicks = ticks;
        } else {
            this.timer.updateTimer();
        }

        long time = System.nanoTime();
        Profiler.startSection("tick");

        for (int ticks = 0; ticks < this.timer.elapsedTicks; ticks++) {
            this.runTick();
        }

        Profiler.endSection();
        long timeOffset = System.nanoTime() - time;
        this.checkGLError("Pre render");
        Profiler.startSection("sound");
        this.sndManager.setListener(this.gameRenderer.cameraMob, this.timer.renderPartialTicks);
        Profiler.endSection();
        Profiler.startSection("render");
        Profiler.startSection("display");
        GL11.glEnable(3553);
        Display.update();
        Profiler.endSection();
        Profiler.startSection("gameMode");
        if (this.gamemode != null) {
            this.gamemode.setBlockBreakTime(this.timer.renderPartialTicks);
        }

        Profiler.endStartSection("gameRenderer");
        this.gameRenderer.render(this.timer.renderPartialTicks);
        Profiler.endSection();
        Profiler.endSection();
        Profiler.endSection();
        if (OptionValues.advCoords && this.currentScreen == null && this.world != null) {
            if (!Profiler.profilingEnabled) {
                Profiler.clearProfiling();
            }

            Profiler.profilingEnabled = true;
            this.HUD.renderProfiler(timeOffset);
        } else {
            Profiler.profilingEnabled = false;
            this.HUD.prevFrameTime = System.nanoTime();
        }

        Profiler.startSection("root");
        Thread.yield();
        if (!this.fullscreen && (Display.getWidth() != this.displayWidth || Display.getHeight() != this.displayHeight)) {
            this.displayWidth = Display.getWidth();
            this.displayHeight = Display.getHeight();
            if (this.displayWidth <= 0) {
                this.displayWidth = 1;
            }

            if (this.displayHeight <= 0) {
                this.displayHeight = 1;
            }

            this.resizeScreen(this.displayWidth, this.displayHeight);
        }

        this.checkGLError("Post render");
        this.fps++;

        for (this.paused = !this.isMultiplayerWorld() && this.currentScreen != null && this.currentScreen.shouldPause();
            System.currentTimeMillis() >= this.prevTime + 1000L;
            this.fps = 0
        ) {
            this.debugString = this.fps + " fps" + (OptionValues.limitFramerate ? " vsync" : "") + ", " + ChunkRenderer.chunksUpdated + " chunk updates ";
            ChunkRenderer.chunksUpdated = 0;
            this.prevTime += 1000L;
        }

        Profiler.endSection();
    }

    public void setFocus() {
        if (Display.isActive() && !this.inFocus) {
            this.inFocus = true;
            this.mouseHelper.grabMouse();
            this.setScreen(null);
            OptionValues.showHUD = true;
        }
    }

    public void loseFocus() {
        if (this.inFocus) {
            KeyBind.unpressAll();
            this.inFocus = false;
            this.mouseHelper.releaseMouse();
        }
    }

    public void displayPauseScreen() {
        if (this.currentScreen == null) {
            OptionValues.showHUD = false;
            this.setScreen(new PauseScreen());
        }
    }

    public void toggleFullscreen() {
        try {
            this.fullscreen = !this.fullscreen;
            if (this.fullscreen) {
                Display.setDisplayMode(Display.getDesktopDisplayMode());
                Display.setResizable(false);
                this.displayWidth = Display.getDisplayMode().getWidth();
                this.displayHeight = Display.getDisplayMode().getHeight();
                if (this.displayWidth <= 0) {
                    this.displayWidth = 1;
                }

                if (this.displayHeight <= 0) {
                    this.displayHeight = 1;
                }
            } else {
                if (this.displayWidth <= 0) {
                    this.displayWidth = 1;
                }

                if (this.displayHeight <= 0) {
                    this.displayHeight = 1;
                }

                Display.setDisplayMode(new DisplayMode(this.displayWidth, this.displayHeight));
                Display.setResizable(true);
            }

            if (this.fullscreen) {
                this.setFocus();
            }

            if (this.currentScreen != null) {
                this.loseFocus();
                this.resizeScreen(this.displayWidth, this.displayHeight);
            }

            OptionValues.fullScreen = this.fullscreen;
            this.options.saveOptions();
            Display.setFullscreen(this.fullscreen);
            Display.update();
            LoggingUtils.infof(logger, "Size: %d, %d", this.displayWidth, this.displayHeight);
        } catch (Exception e) {
            logger.log(Level.WARNING, "ToggleFullscreen failed: " + e.toString(), e);
        }
    }

    private void resizeScreen(int newWidth, int newHeight) {
        if (newWidth <= 0) {
            newWidth = 1;
        }

        if (newHeight <= 0) {
            newHeight = 1;
        }

        this.displayWidth = newWidth;
        this.displayHeight = newHeight;
        if (this.currentScreen != null) {
            this.rescaler = new ScreenRescaler(newWidth, newHeight);
            this.currentScreen.setResolution(this, this.rescaler.getScaledWidth(), this.rescaler.getScaledHeight());
        }
    }

    public void runTick() {
        if (!this.mainThreadEvents.isEmpty()) {
            Runnable r = this.mainThreadEvents.remove(0);
            r.run();
        }

        if (this.sndManager != null && this.world != null && this.sndManager.loaded && !this.paused) {
            Profiler.startSection("ambience");
            this.sndManager.playRegionalAmbience(this.gameRenderer.cameraMob, this.world);
            Profiler.endSection();
        }

        Profiler.startSection("hud");
        this.HUD.update();
        Profiler.endSection();
        Profiler.startSection("gameMode");
        if (!this.paused && this.world != null && this.player != null) {
            this.gamemode.onUpdate();
        }

        Profiler.endSection();
        GL11.glBindTexture(3553, this.textureLoader.getTexture("/terrain.png"));
        Profiler.startSection("textures");
        if (!this.paused) {
            this.textureLoader.updateAnimatedTextures();
        }

        Profiler.endSection();
        if (this.currentScreen == null && this.player != null) {
            if (this.player.health <= 0) {
                this.setScreen(null);
            } else if (this.player.isPlayerSleeping() && this.world != null) {
                this.setScreen(new SleepScreen());
            }
        } else if (this.currentScreen instanceof SleepScreen && (this.player == null || !this.player.isPlayerSleeping())) {
            this.setScreen(null);
        }

        if (this.currentScreen == null) {
            Profiler.startSection("mouse");

            while (Mouse.next()) {
                KeyBind.setState(Mouse.getEventButton() - 100, Mouse.getEventButtonState());
                if (Mouse.getEventButtonState()) {
                    KeyBind.tick(Mouse.getEventButton() - 100);
                }

                long var5 = System.currentTimeMillis() - this.systemTime;
                if (var5 <= 200L) {
                    int whlValue = Mouse.getEventDWheel();
                    if (whlValue != 0 && this.currentScreen == null && this.player != null) {
                        if (OptionValues.scrollInvert) {
                            whlValue *= -1;
                        }

                        this.player.inventory.changeCurrentItem(whlValue);
                    }

                    if (this.currentScreen == null) {
                        if (!this.inFocus && Mouse.getEventButtonState()) {
                            this.setFocus();
                        }
                    } else if (this.currentScreen != null) {
                        this.currentScreen.handleMouseInput();
                    }
                }
            }

            Profiler.endSection();
            Profiler.startSection("keyboard");

            do {
                if (!Keyboard.next()) {
                    while (this.options.fullscreenKey.pressed()) {
                        this.toggleFullscreen();
                    }

                    if (this.currentScreen != null) {
                        this.currentScreen.handleKeyboardInput();
                        break;
                    }

                    while (this.options.pauseKey.pressed()) {
                        this.displayPauseScreen();
                    }

                    while (this.options.chatKey.pressed()) {
                        this.setScreen(new ChatScreen());
                    }

                    while (this.options.changeModeKey.pressed()) {
                        if (this.world.settings.getCheats() || this.world.isOnline) {
                            this.setScreen(new ModeSwitcher());
                        }
                    }
                    break;
                }

                KeyBind.setState(Keyboard.getEventKey(), Keyboard.getEventKeyState());
                if (Keyboard.getEventKeyState()) {
                    KeyBind.tick(Keyboard.getEventKey());
                }
            } while (!Keyboard.getEventKeyState());

            Profiler.endSection();
        }

        if (this.currentScreen != null) {
            this.currentScreen.update();
            if (this.currentScreen != null) {
                this.currentScreen.handleInput();
            }
        }

        if (this.world != null && this.player != null && !this.paused) {
            if (this.player.properties.getPermadeath()) {
                this.world.settings.setDifficulty((byte)3);
                this.world.settings.difficultyLock = true;
            }

            Profiler.startSection("gameRenderer");
            this.gameRenderer.update();
            Profiler.endStartSection("levelRenderer");
            this.skyRenderer.updateClouds();
            Profiler.endStartSection("level");
            if (this.world.lightningStrikeTime > 0) {
                this.world.lightningStrikeTime--;
            }

            this.world.tickEntities();
            this.world.setAllowedSpawnTypes(this.world.settings.getDifficulty() > 0 && !this.world.blueMoon, true);
            this.world.tick();
            // NetworkWorld.tick() drains the packet queue, so a kick, a lost connection or the
            // server shutting down all land *here*, in the middle of this block. ClientHandler
            // handles them by calling changeWorld(null), which nulls both world and player while
            // we are still on the stack - and the rest of this block would then dereference
            // them. This is why being kicked crashed the client instead of showing the
            // disconnect screen.
            if (this.world != null && this.player != null) {
                Profiler.endStartSection("animateTick");
                this.world
                    .tickAnimations(
                        MathHelper.floor_double(this.player.posX), MathHelper.floor_double(this.player.posY), MathHelper.floor_double(this.player.posZ)
                    );
                Profiler.endStartSection("particles");
                this.particleRenderer.updateParticles();
            }
        }

        Profiler.endSection();
        this.systemTime = System.currentTimeMillis();
    }

    public boolean isMultiplayerWorld() {
        return this.world != null && this.world.isOnline;
    }

    public void startWorld(String filename, String worldname, long seed, WorldSettings settings) {
        this.clearWorld();
        System.gc();
        if (this.saveLoader.isOldMapFormat(filename)) {
            this.convertMapFormat(filename, worldname);
        } else {
            SaveHandler handler = this.saveLoader.getSaveLoader(filename, false);
            this.statistics = new StatWriter(((SaveHandlerBase)handler).saveDirectory);
            World world = new World(handler, worldname, seed, settings);
            world.settings.saveFolder = filename;
            world.settings.setVersion(SharedConstants.VERSION_STRING);
            String str = "gui.loadlvl";
            if (world.newlyGenerated) {
                str = "gui.generatelvl";
            }

            this.changeWorld(world, Translator.getInstance().translateKey(str));
        }
    }

    public void changeWorld(World world, String name) {
        if (this.world != null) {
            this.world.restoreBlockEmittance(MathHelper.floor(this.player.posX), MathHelper.floor(this.player.posY), MathHelper.floor(this.player.posZ));
            this.world.forceSave(this.loadingScreen);
            this.world.dropOldChunks();
            this.statistics.syncStats();
        }

        this.world = world;
        if (world != null) {
            if (!world.isOnline) {
                this.gamemode = new GameMode(this);
            }

            this.gamemode.onWorldChange(world);
            if (!world.isOnline) {
                this.preloadWorld(name);
            }

            if (this.player == null) {
                this.player = (LocalPlayer)this.gamemode.createPlayer(world);
                this.player.properties.setGamemode(this.world.getSettings().getGamemode());
                this.player.spawn(world.newlyGenerated);
                this.gamemode.flipPlayer(this.player);
            }

            this.player.controller = new PlayerController(this, this.options);
            if (this.worldRenderer != null) {
                this.worldRenderer.changeWorld(world);
            }

            if (this.gameRenderer != null) {
                this.skyRenderer.setWorld(world);
                this.gameRenderer.blocks.setWorldInstance(world);
            }

            if (this.particleRenderer != null) {
                this.particleRenderer.clearParticles(world);
            }

            this.gamemode.onRespawn(this.player);
            world.spawnPlayer(this.player);
            if (world.newlyGenerated) {
                this.player.setProperties(world);
                world.forceSave(this.loadingScreen);
            }

            this.player.properties.setGamemode(this.world.getSettings().getGamemode());
            this.world.setBaseSkyColor(8961023L);
            this.world.setBaseFogColor(12638463L);
            this.world.simDistance = OptionValues.simDistance;
        } else {
            this.player = null;
            this.sndManager.sndSystem.stop("streaming");
            this.sndManager.sndSystem.stop("ambienceTrack");
        }

        this.gameRenderer.cameraMob = this.player;
        this.sndManager.sndSystem.stop("BgMusic");
        this.HUD.chatOverlay.clearChatMessages();
        GabrielStaff.canUseStaff = true;
        System.gc();
        this.systemTime = 0L;
    }

    public void clearWorld() {
        this.changeWorld(null, "");
    }

    private void convertMapFormat(String saveFileName, String saveName) {
        this.loadingScreen.displayProgressMessage(Translator.getInstance().translateKey("gui.convert") + this.saveLoader.getFormatName());
        this.loadingScreen.displayLoadingString(Translator.getInstance().translateKey("gui.wait"));
        this.saveLoader.convertMapFormat(saveFileName, this.loadingScreen);
        this.startWorld(saveFileName, saveName, 0L, null);
    }

    private void preloadWorld(String worldName) {
        this.loadingScreen.displayProgressMessage(worldName);
        this.loadingScreen.displayLoadingString(Translator.getInstance().translateKey("gui.build"));
        short defaultHeight = 128;
        int x = 0;
        int y = defaultHeight * 2 / 16 + 1;
        y *= y;
        Position pos = this.world.getSpawnPoint();

        for (int attempt = -defaultHeight; attempt <= defaultHeight; attempt += 16) {
            if (this.player != null) {
                pos.x = (int)this.player.posX;
                pos.z = (int)this.player.posZ;
            }

            for (int i = -defaultHeight; i <= defaultHeight; i += 16) {
                this.loadingScreen.setLoadingProgress(x++ * 100 / y);
                this.world.getBlockId(pos.x + attempt, this.world.getWorldHeight() / 2, pos.z + i);
            }
        }

        this.loadingScreen.displayLoadingString(Translator.getInstance().translateKey("gui.simulate"));
        short threshold = 2000;

        for (int var9 = 0; var9 < threshold; var9++) {
            this.world.tickPending(true);
        }

        if (!this.world.isOnline) {
            this.setGamemode(this.world.getSettings().getGamemode());
        }
    }

    public void switchDimension(int dimension) {
        double x = this.player.posX;
        double z = this.player.posZ;
        Player player = this.player;
        World world = new World(this.world, Dimension.getDimension(dimension));
        String message = Translator.getInstance().translateKey("gui.dim_" + dimension);
        this.changeWorld(world, message);
        this.player.clone(player, true);
        this.player.world = world;
        // todo: enterNether/enterAether/enterVoid score is client-only — dimension transfer isn't server-authoritative yet
        switch (dimension) {
            case 1:
                this.player.triggerAchievement(AchievementList.enterNether);
                this.player.addStat(StatList.slipVisits, 1);
                this.sndManager.playAmbience("random.hell", 0.25F, 1.0F);
                break;
            case 2:
                this.player.triggerAchievement(AchievementList.enterAether);
                this.player.addStat(StatList.skyVisits, 1);
                this.sndManager.playAmbience("random.sky", 0.25F, 1.0F);
                break;
            case 3:
                if (this.world.settings.border > 0) {
                    x = 0.0;
                    z = 0.0;
                } else if (this.world.rand.nextBoolean()) {
                    x = 1024 + this.world.rand.nextInt(1024);
                    z = 1024 + this.world.rand.nextInt(1024);
                } else {
                    x = -1024 - this.world.rand.nextInt(1024);
                    z = -1024 - this.world.rand.nextInt(1024);
                }

                this.player.triggerAchievement(AchievementList.enterVoid);
                this.player.addStat(StatList.voidVisits, 1);
                this.sndManager.playAmbience("random.void", 0.25F, 1.0F);
        }

        double y = this.world.getTopTileSafe(MathHelper.floor_double(x), MathHelper.floor_double(z));
        if (this.player.dimension != 1 && dimension == 0) {
            if (this.player.getSpawn(0) != null) {
                x = this.player.getSpawn(0).x + 0.5;
                z = this.player.getSpawn(0).z + 0.5;
                y = this.player.getSpawn(0).y + 1;
            } else {
                x = this.world.getSpawnPoint().x + 0.5;
                z = this.world.getSpawnPoint().z + 0.5;
                y = this.world.getSpawnPoint().y + 1;
            }
        }

        this.player.setPosAndRot(x, y, z, this.player.yaw, this.player.pitch);
        if (dimension == 1
            && world.getBlockMaterial(MathHelper.floor_double(x), MathHelper.floor_double(this.player.posY - 2.0), MathHelper.floor_double(z))
                == MaterialList.lava) {
            Disk.PLATFORM
                .place(this.world, this.world.rand, MathHelper.floor_double(x), MathHelper.floor_double(this.player.posY - 2.0), MathHelper.floor_double(z));
        }

        if (dimension == 2 && world.getBlock(MathHelper.floor_double(x), MathHelper.floor_double(this.player.posY - 2.0), MathHelper.floor_double(z)) == null) {
            Cloud.SMALL_CLOUD
                .place(this.world, this.world.rand, MathHelper.floor_double(x), MathHelper.floor_double(this.player.posY - 2.0), MathHelper.floor_double(z));
        }

        if (this.player.canClimb(BlockList.endPortal)) {
            this.world.setBlockWithNotify(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z), 0);
        }

        this.player.spawnInDimension(dimension);
        this.world.tickEntity(this.player, false);
        this.sndManager.sndSystem.stop("streaming");
        this.sndManager.sndSystem.stop("music");
        GabrielStaff.canUseStaff = true;
    }

    public void respawn(boolean isMultiplayer) {
        // switchDimension builds a local World with its own chunk provider, which would replace
        // the NetworkWorld and sever the connection. The `dimension == 3` clause used to sit
        // outside the !isOnline guard, so dying in the Void on a server did exactly that.
        if (!this.world.isOnline
            && (!this.world.currDim.canRespawnHere() && this.player.getSpawn(this.player.dimension) == null
                || this.player.dimension == 3)) {
            this.switchDimension(0);
        }

        Player player = this.player;
        Position playerSpawn = null;
        Position bedPosition = null;
        int dim = 0;
        boolean bed = false;
        if (this.player != null && !isMultiplayer) {
            playerSpawn = this.player.getSpawn(this.player.dimension);
            dim = this.player.dimension;
            if (playerSpawn != null) {
                bed = true;
                bedPosition = Player.wakeUpPlayer(this.world, playerSpawn, dim);
                if (bedPosition == null && !this.player.forceSpawn) {
                    this.player.addChatMessage(Translator.getInstance().translateKey("tile.bed.notValid"));
                }
            }
        }

        if (bedPosition == null && !this.player.forceSpawn) {
            bedPosition = this.world.getSpawnPoint();
        }

        this.world.setSpawn();
        this.world.tickEntities();
        int id = 0;
        if (this.player != null) {
            id = this.player.entityId;
            this.world.kill(this.player);
        }

        this.player = (LocalPlayer)this.gamemode.createPlayer(this.world);
        this.player.spawn(false);
        this.player.clone(player, false);
        if (bed || this.player.forceSpawn) {
            this.player.setSpawn(playerSpawn, dim, this.player.forceSpawn);
            if (this.player.forceSpawn) {
                this.player.setPosAndRot(playerSpawn.x + 0.5F, playerSpawn.y + 0.1F, playerSpawn.z + 0.5F, 0.0F, 0.0F);
            } else {
                this.player.setPosAndRot(bedPosition.x + 0.5F, bedPosition.y + 0.1F, bedPosition.z + 0.5F, 0.0F, 0.0F);
            }

            this.player.dimension = dim;
        }

        this.gamemode.flipPlayer(this.player);
        this.world.spawnPlayer(this.player);
        this.player.controller = new PlayerController(this, this.options);
        this.player.entityId = id;
        this.gamemode.onRespawn(this.player);
        this.player.score /= 2;
        this.preloadWorld(Translator.getInstance().translateKey("gui.respawn"));
        if (this.currentScreen instanceof DeathScreen) {
            this.setScreen(null);
        }
    }

    public void installResource(String resourceName, File folder) {
        int subFolderAmount = resourceName.indexOf("/");
        String folderName = resourceName.substring(0, subFolderAmount);
        resourceName = resourceName.substring(subFolderAmount + 1);
        if (!folderName.equalsIgnoreCase("sound") && !folderName.equalsIgnoreCase("newsound") && !folderName.equalsIgnoreCase("oldsound")) {
            this.sndManager.addMusic(folderName, resourceName, folder);
        } else {
            this.sndManager.addSound(resourceName, folder);
        }
    }

    public String debugInfoRenders() {
        return this.worldRenderer.chunkRenderersInfo();
    }

    public String debugInfoEntities() {
        return this.worldRenderer.entityRenderersInfo() + ", T: " + this.world.tickSet.size();
    }

    public String debugInfoLight() {
        int x = MathHelper.floor_double(this.player.posX);
        int y = MathHelper.floor_double(this.player.posY);
        int z = MathHelper.floor_double(this.player.posZ);
        return "Light: "
            + this.world.getBlockBrightness(x, y, z)
            + ", Block: "
            + this.world.getLayerBrightness(LightLayer.Block, x, y, z)
            + ", Sky: "
            + this.world.getLayerBrightness(LightLayer.Sky, x, y, z);
    }

    public String debugChunkProviderInfo() {
        return this.world.getProviderName();
    }

    public static boolean isGuiEnabled() {
        return instance != null && OptionValues.showHUD;
    }

    public static boolean isDebugScreenFull() {
        return instance != null && OptionValues.advCoords;
    }

    public void setGamemode(int gm) {
        if (this.world != null) {
            this.world.getSettings().setGamemode((byte)gm);
        }

        if (this.player != null) {
            this.player.properties.setGamemode(gm);
            this.player.noClip = gm == PlayerProperties.SPECTATOR;
            this.player.flying = gm == PlayerProperties.SPECTATOR;
        }
    }

    public static Minecraft getMinecraft() {
        return instance;
    }

    public ClientHandler getSendQueue() {
        return this.player instanceof NetworkPlayer ? ((NetworkPlayer)this.player).connection : null;
    }

    public boolean lineIsCommand(String string4) {
        return false;
    }

    public World getWorld(int dimension) {
        return this.world;
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public Commands getCommands() {
        return this.commands;
    }

    public boolean isServer() {
        return false;
    }

    static {
        if (System.getProperty("java.util.Arrays.useLegacyMergeSort") == null) {
            System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        }

        USE_APPDATA = Boolean.parseBoolean(System.getProperty("minecraft.useAppData", "false"));
        NO_DOWNLOAD_RESOURCES_ON_START = Boolean.parseBoolean(System.getProperty("minecraft.noDownloadResourcesOnStart", "false"));
        DEV_CRASHSCREEN_BYPASS = Boolean.parseBoolean(System.getProperty("minecraft.noCrashScreen", "false"));
        DISABLE_GLL_ERR_CHECKS = false;
        Logging.init(logger = Logger.getLogger("Minecraft"), getMinecraftDir(), true);
    }

    public enum EnumOS {
        linux,
        solaris,
        windows,
        macos,
        unknown;
    }
}
