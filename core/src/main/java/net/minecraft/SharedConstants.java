package net.minecraft;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class SharedConstants {
    public static final String VERSION_STRING = "v1.0";
    public static final String PREFIX = "Minecraft Infinite";
    public static final String FULL_VER_STRING = "Minecraft Infinite v1.0";
    public static final int PROTOCOL_VERSION = 20260814;
    public static final char COLOR_CODE = '§';
    public static final String COLOR_CODE_STR = "§";
    public static final String COLOR_CHARS = "0123456789abcdefgh";
    public static final String TEXT_EXTENSIONS = "klmnor";
    public static final String VALID_TEXT_CHARACTERS = validChars();
    public static final char[] INVALID_FILENAME_CHARS = new char[]{'/', '\n', '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '"', ':'};
    public static final String[] ILLEGAL_WORLD_NAMES = new String[]{
        "CON",
        "COM",
        "PRN",
        "AUX",
        "CLOCK$",
        "NUL",
        "COM1",
        "COM2",
        "COM3",
        "COM4",
        "COM5",
        "COM6",
        "COM7",
        "COM8",
        "COM9",
        "LPT1",
        "LPT2",
        "LPT3",
        "LPT4",
        "LPT5",
        "LPT6",
        "LPT7",
        "LPT8",
        "LPT9"
    };
    public static final String BLACK = "§0";
    public static final String BLUE = "§1";
    public static final String GREEN = "§2";
    public static final String CYAN = "§3";
    public static final String RED = "§4";
    public static final String PURPLE = "§5";
    public static final String DARKYELLOW = "§6";
    public static final String GRAY = "§7";
    public static final String DARKGRAY = "§8";
    public static final String LIGHTBLUE = "§9";
    public static final String LIGHTGREEN = "§a";
    public static final String AQUA = "§b";
    public static final String BRIGHTRED = "§c";
    public static final String PINK = "§d";
    public static final String YELLOW = "§e";
    public static final String WHITE = "§f";
    public static final String RGB = "§g";
    public static final String LIGHT_YEL = "§h";
    public static final String OBFUSCATED = "§k";
    public static final String BOLD = "§l";
    public static final String STRIKETHRU = "§m";
    public static final String UNDERLINE = "§n";
    public static final String ITALICS = "§o";
    public static final String RESET = "§r";

    private SharedConstants() {
    }

    private static String validChars() {
        String string = "";

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(SharedConstants.class.getResourceAsStream("/font.txt"), "UTF-8"));
            String newString = "";

            while ((newString = reader.readLine()) != null) {
                if (!newString.startsWith("#")) {
                    string = string + newString;
                }
            }

            reader.close();
        } catch (Exception var3) {
        }

        return string;
    }

    public static boolean isValidCharacter(char c) {
        return c != 167 && (VALID_TEXT_CHARACTERS.indexOf(c) >= 0 || c > ' ');
    }

    public static String filterAllowedCharacters(String string) {
        StringBuilder strBuilder = new StringBuilder();

        for (char c : string.toCharArray()) {
            if (isValidCharacter(c)) {
                strBuilder.append(c);
            }
        }

        return strBuilder.toString();
    }

    public static String deleteColorCodes(String input) {
        char[] chars = input.toCharArray();
        String out = "";

        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == 167) {
                i++;
            } else {
                out = out + chars[i];
            }
        }

        return out;
    }

    public static ByteBuffer[] loadDisplayIcons(URL... urls) throws IOException {
        int arglen = urls.length;
        ByteBuffer[] bufs = new ByteBuffer[arglen];

        for (int i = 0; i < arglen; i++) {
            URL url = urls[i];
            if (url == null) {
                throw new IOException("url == null");
            }

            BufferedImage image = ImageIO.read(url);
            int width = image.getWidth();
            int height = image.getHeight();
            int capacity = width * height;
            ByteBuffer buffer = ByteBuffer.allocateDirect(capacity * 4);
            buffer.order(ByteOrder.nativeOrder());
            int[] pixels = new int[capacity];
            image.getRGB(0, 0, width, height, pixels, 0, width);

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pixel = pixels[y * width + x];
                    buffer.put((byte)(pixel >> 16 & 0xFF));
                    buffer.put((byte)(pixel >> 8 & 0xFF));
                    buffer.put((byte)(pixel & 0xFF));
                    buffer.put((byte)(pixel >> 24 & 0xFF));
                }
            }

            ((Buffer)buffer).flip();
            bufs[i] = buffer;
        }

        return bufs;
    }

    public static List<Image> loadAWTIcons(URL... urls) throws IOException {
        ArrayList<Image> al = new ArrayList<>();

        for (int i = 0; i < urls.length; i++) {
            URL url = urls[i];
            if (url == null) {
                throw new IOException("url == null");
            }

            Image img = ImageIO.read(url);
            al.add(img);
        }

        return al;
    }
}
