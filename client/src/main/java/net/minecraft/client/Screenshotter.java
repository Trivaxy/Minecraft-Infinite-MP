package net.minecraft.client;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import net.minecraft.util.io.ImageTransferable;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class Screenshotter {
    private static DateFormat screenshotDate = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
    private static ByteBuffer pixelBuffer;
    private static byte[] pixels;
    private static int[] rgb;

    public static String takeScreenshot(File workdir, int width, int height) {
        try {
            File folder = new File(workdir, "screenshots");
            folder.mkdir();
            String timestamp = "" + screenshotDate.format(new Date());
            int imgCount = 1;

            File filename;
            while ((filename = new File(folder, timestamp + (imgCount == 1 ? "" : "_" + imgCount) + ".png")).exists()) {
                imgCount++;
            }

            BufferedImage image = new BufferedImage(width, height, 1);
            storePixels(image, width, height);

            try {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new ImageTransferable(image), null);
            } catch (Exception var8) {
            }

            ImageIO.write(image, "png", filename);
            return "Saved screenshot as " + filename.getName();
        } catch (Exception ex) {
            ex.printStackTrace();
            return "Failed to save: " + ex;
        }
    }

    public static String createIcon(File workdir, int width, int height) {
        try {
            File worldFolder = new File(workdir, "saves/" + Minecraft.getMinecraft().world.settings.saveFolder);
            if (!worldFolder.exists()) {
                worldFolder.mkdir();
            }

            File filename = new File(worldFolder, "icon.png");
            if (filename.exists() && !filename.canWrite()) {
                return "Can't override file, read only!";
            }

            BufferedImage image = new BufferedImage(width, height, 1);
            storePixels(image, width, height);
            BufferedImage newImage = createResizedCopy(image, 256, 256, true);
            ImageIO.write(newImage, "png", filename);
            Minecraft.getMinecraft().textureLoader.textureMap.remove("&&saves/" + Minecraft.getMinecraft().world.settings.saveFolder + "/icon.png");
            return "Saved icon for " + Minecraft.getMinecraft().world.settings.saveFolder;
        } catch (Exception ex) {
            ex.printStackTrace();
            return "Failed to save: " + ex;
        }
    }

    private static void storePixels(BufferedImage image, int width, int height) {
        if (pixelBuffer == null || pixelBuffer.capacity() < width * height * 3) {
            pixelBuffer = BufferUtils.createByteBuffer(width * height * 3);
            pixels = new byte[width * height * 3];
            rgb = new int[width * height];
        }

        GL11.glPixelStorei(3333, 1);
        GL11.glPixelStorei(3317, 1);
        ((Buffer)pixelBuffer).clear();
        GL11.glReadPixels(0, 0, width, height, 6407, 5121, pixelBuffer);
        ((Buffer)pixelBuffer).clear();
        pixelBuffer.get(pixels);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int size = x + (height - y - 1) * width;
                int red = pixels[size * 3 + 0] & 255;
                int green = pixels[size * 3 + 1] & 255;
                int blue = pixels[size * 3 + 2] & 255;
                int pixelColor = 0xFF000000 | red << 16 | green << 8 | blue;
                rgb[x + y * width] = pixelColor;
            }
        }

        image.setRGB(0, 0, width, height, rgb, 0, width);
    }

    static BufferedImage createResizedCopy(Image originalImage, int scaledWidth, int scaledHeight, boolean preserveAlpha) {
        int imageType = preserveAlpha ? 1 : 2;
        BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, imageType);
        Graphics2D graphic = scaledBI.createGraphics();
        if (preserveAlpha) {
            graphic.setComposite(AlphaComposite.Src);
        }

        graphic.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
        graphic.dispose();
        return scaledBI;
    }
}
