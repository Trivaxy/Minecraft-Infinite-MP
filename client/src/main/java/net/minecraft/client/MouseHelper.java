package net.minecraft.client;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.Buffer;
import java.nio.IntBuffer;
import javax.imageio.ImageIO;
import net.minecraft.util.logging.LoggingUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class MouseHelper {
    public int deltaX;
    public int deltaY;
    private boolean fail = false;
    private Cursor cursor;

    public MouseHelper() {
        try {
            this.cursor = new Cursor(32, 32, 0, 31, 1, loadCursorTexture(MouseHelper.class.getResource("/gui/cursor.png")), null);
        } catch (Exception e) {
            LoggingUtils.warnf(Minecraft.logger, e, "Failed to load icon: %s", e);
            this.fail = true;
        }
    }

    private static IntBuffer loadCursorTexture(URL url) throws IOException {
        if (url == null) {
            throw new IOException("url == null");
        }

        BufferedImage image = ImageIO.read(url);
        int width = image.getWidth();
        int height = image.getHeight();
        int capacity = width * height;
        IntBuffer buffer = BufferUtils.createIntBuffer(capacity);
        int[] pixels = new int[capacity];
        image.getRGB(0, 0, width, height, pixels, 0, width);
        buffer.put(pixels);
        ((Buffer)buffer).flip();
        int numImages = 1;

        for (int i = 0; i < numImages; i++) {
            int start_index = i * width * height;

            for (int y = 0; y < height >> 1; y++) {
                int index_y_1 = y * width + start_index;
                int index_y_2 = (height - y - 1) * width + start_index;

                for (int x = 0; x < width; x++) {
                    int index1 = index_y_1 + x;
                    int index2 = index_y_2 + x;
                    int temp_pixel = buffer.get(index1 + buffer.position());
                    buffer.put(index1, buffer.get(index2 + buffer.position()));
                    buffer.put(index2, temp_pixel);
                }
            }
        }

        return buffer;
    }

    public void setCursor(int cursorType) {
        if (!this.fail) {
            try {
                switch (cursorType) {
                    case 0:
                        Mouse.setNativeCursor(null);
                        break;
                    case 1:
                        Mouse.setNativeCursor(this.cursor);
                }
            } catch (LWJGLException e) {
                LoggingUtils.warnf(Minecraft.logger, e, "Failed to set cursor: %s", e);
            }
        }
    }

    public void grabMouse() {
        Mouse.setGrabbed(true);
        this.deltaX = 0;
        this.deltaY = 0;
    }

    public void releaseMouse() {
        Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
        Mouse.setGrabbed(false);
    }

    public void moveMouse() {
        this.deltaX = Mouse.getDX();
        this.deltaY = Mouse.getDY();
    }
}
