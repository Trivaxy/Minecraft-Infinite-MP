package net.minecraft.client.localization;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.text.Bidi;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.options.OptionsList;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.texture.TextureLoader;
import net.minecraft.client.render.texture.pack.TexturePack;
import org.lwjgl.opengl.GL11;

public class Font {
    private static final Pattern formatCodeRegex = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");
    public Random randomizer;
    private final TextureLoader renderer;
    public BufferedImage fontImage;
    public String imageName;
    private final String colorCodes = "0123456789abcdefgh";
    private TexturePack texturePack;
    private final int[] charWidth = new int[SharedConstants.VALID_TEXT_CHARACTERS.length()];
    private final int[] glyphTextureIds;
    private final int[] colorCode;
    public int[] fontTextureIds;
    public int[] imageSpacing;
    private final byte[] glyphWidth;
    private int res;
    public int fontTextureId = 0;
    public int fontHeight = 9;
    public int textureWidth;
    public int textureHeight;
    private int boundTextureId;
    private float posX;
    private float posY;
    private float initR;
    private float initG;
    private float initB;
    private float initA;
    private boolean unicode;
    private boolean bidi;

    public Font(OptionsList settings, TexturePack texturePack, TextureLoader renderer) {
        this.randomizer = new Random();
        this.glyphWidth = new byte[65536];
        this.fontTextureIds = new int[SharedConstants.VALID_TEXT_CHARACTERS.length() / 256 + 1];
        this.imageName = "";
        this.glyphTextureIds = new int[256];
        this.colorCode = new int["0123456789abcdefgh".length() * 2];
        this.renderer = renderer;
        this.texturePack = texturePack;

        try {
            InputStream inputstream = TextureLoader.class.getResourceAsStream("/font/glyph_sizes.bin");
            inputstream.read(this.glyphWidth);
        } catch (IOException ioexception) {
            throw new RuntimeException(ioexception);
        }

        this.updateFontImage(0, this.renderer, texturePack);

        for (int character = 0; character < SharedConstants.VALID_TEXT_CHARACTERS.length(); character++) {
            this.updateFontImage(character, this.renderer, texturePack);
            int wrappedk = character % 256;
            int imgWidth = wrappedk % 16;
            int imgHeight = wrappedk / 16;

            int widthOffset;
            for (widthOffset = this.textureWidth / 16 - 1; widthOffset >= 0; widthOffset--) {
                int widthSpacing = imgWidth * (this.textureWidth / 16) + widthOffset;
                boolean widthTooMuch = true;

                for (int totalWidth = 0; totalWidth < this.textureWidth / 16; totalWidth++) {
                    int heightSpacing = (imgHeight * (this.textureWidth / 16) + totalWidth) * this.textureWidth;
                    int finalSpacing = this.imageSpacing[widthSpacing + heightSpacing] & 0xFF;
                    if (finalSpacing > 0) {
                        widthTooMuch = false;
                        break;
                    }
                }

                if (!widthTooMuch) {
                    break;
                }
            }

            if (character == 32) {
                widthOffset = this.textureWidth / 64 + this.textureWidth / 128 - 1;
            }

            this.charWidth[character] = (widthOffset + 2 + (this.res / 8 - 1)) / (this.res / 8);
        }

        for (int colorCode = 0; colorCode < "0123456789abcdefgh".length() * 2; colorCode++) {
            int maximum = -1;
            if (colorCode >= 32) {
                maximum = colorCode - colorCode / 16 * 16;
            }

            int customPosition = maximum == -1 ? -1 : maximum % (("0123456789abcdefgh".length() - 16) / 2 + 1);
            if (customPosition != 0) {
                int alpha = (colorCode >> 3 & 1) * 85;
                int red = (colorCode >> 2 & 1) * 170 + alpha;
                int green = (colorCode >> 1 & 1) * 170 + alpha;
                int blue = (colorCode >> 0 & 1) * 170 + alpha;
                if (colorCode == 6) {
                    red += 85;
                }

                if (customPosition == 1) {
                    red = 255;
                    green = 255;
                    blue = 160;
                }

                boolean shadow = colorCode < 32 ? colorCode >= 16 : colorCode - 32 > ("0123456789abcdefgh".length() - 16) / 2;
                if (shadow) {
                    red /= 4;
                    green /= 4;
                    blue /= 4;
                }

                this.colorCode[colorCode] = (red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF;
            }
        }
    }

    public void updateFontImage(int imgWidth, TextureLoader renderengine, TexturePack tex) {
        String prevPngName = this.imageName;
        if (imgWidth >= 256) {
            this.imageName = "font_" + String.format("%02X", (byte)(imgWidth / 256));
        } else {
            this.imageName = "default";
        }

        if (!prevPngName.equals(this.imageName)) {
            try {
                InputStream stream = tex.getResourceAsStream("/font/" + this.imageName + ".png");
                if (stream == null) {
                    Logger.getLogger("Minecraft").warning("Image \"" + this.imageName + ".png\" was null!");
                    return;
                }

                BufferedImage image = ImageIO.read(stream);
                if (image != null) {
                    this.fontImage = image;
                }
            } catch (IOException ioexception) {
                throw new RuntimeException(ioexception);
            }

            this.textureWidth = this.fontImage.getWidth();
            this.textureHeight = this.fontImage.getHeight();
            this.res = this.textureWidth / 16;

            for (int size = 0; size < this.textureWidth * this.textureHeight; size++) {
                int x = size % this.textureWidth;
                int y = size / this.textureHeight;
                Color color = new Color(this.fontImage.getRGB(x, y), true);
                if (color.getAlpha() <= 2) {
                    this.fontImage.setRGB(x, y, 0);
                }
            }

            this.imageSpacing = new int[this.textureWidth * this.textureHeight];
            this.imageSpacing = this.fontImage.getRGB(0, 0, this.textureWidth, this.textureHeight, this.imageSpacing, 0, this.textureWidth);
        }
    }

    public int getTextureId(int size, TextureLoader renderengine) {
        int id = size / 256;
        if (this.fontTextureIds[id] == 0) {
            this.fontTextureIds[id] = renderengine.allocateTexture(this.fontImage);
        }

        return this.fontTextureIds[id];
    }

    private float renderChar(int glyph, char unicodeGlyph, boolean italics) {
        if (unicodeGlyph == ' ') {
            return 4.0F;
        } else {
            return glyph > 0 && !this.unicode ? this.renderChar(glyph, italics) : this.renderGlyph(unicodeGlyph, italics);
        }
    }

    private float renderChar(int glyph, boolean italics) {
        float x = glyph % 16 * 8;
        float y = glyph / 16 * 8;
        float skew = italics ? 1.0F : 0.0F;
        if (this.fontTextureIds[glyph / 256] == 0) {
            this.updateFontImage(glyph, this.renderer, this.texturePack);
        }

        this.fontTextureId = this.getTextureId(glyph, this.renderer);
        if (this.boundTextureId != this.fontTextureId) {
            GL11.glBindTexture(3553, this.fontTextureId);
            this.boundTextureId = this.fontTextureId;
        }

        float xEnd = this.charWidth[glyph] - 0.01F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.begin(5);
        tessellator.addTexturedVertex(this.posX + skew, this.posY, 0.0, x / 128.0F, y / 128.0F);
        tessellator.addTexturedVertex(this.posX - skew, this.posY + 7.99F, 0.0, x / 128.0F, (y + 7.99F) / 128.0F);
        tessellator.addTexturedVertex(this.posX + xEnd + skew, this.posY, 0.0, (x + xEnd) / 128.0F, y / 128.0F);
        tessellator.addTexturedVertex(this.posX + xEnd - skew, this.posY + 7.99F, 0.0, (x + xEnd) / 128.0F, (y + 7.99F) / 128.0F);
        tessellator.draw();
        return this.charWidth[glyph];
    }

    private void loadGlyphTexture(int id) {
        String s = String.format("/font/unicode_page_%02X.png", id);

        BufferedImage bufferedimage;
        try {
            bufferedimage = ImageIO.read(TextureLoader.class.getResourceAsStream(s));
        } catch (IOException ioexception) {
            throw new RuntimeException(ioexception);
        }

        this.glyphTextureIds[id] = this.renderer.allocateTexture(bufferedimage);
        this.boundTextureId = this.glyphTextureIds[id];
    }

    private float renderGlyph(char glyph, boolean italics) {
        if (this.glyphWidth[glyph] == 0) {
            return 0.0F;
        }

        int id = glyph / 256;
        if (this.glyphTextureIds[id] == 0) {
            this.loadGlyphTexture(id);
        }

        if (this.boundTextureId != this.glyphTextureIds[id]) {
            GL11.glBindTexture(3553, this.glyphTextureIds[id]);
            this.boundTextureId = this.glyphTextureIds[id];
        }

        int minX = this.glyphWidth[glyph] >>> 4;
        int maxX = this.glyphWidth[glyph] & 15;
        float maxXExtended = maxX + 1;
        float x = (float)(glyph % 16 * 16) + minX;
        float y = (glyph & 255) / 16 * 16;
        float max = maxXExtended - minX - 0.02F;
        float skew = italics ? 1.0F : 0.0F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.begin(5);
        tessellator.addTexturedVertex(this.posX + skew, this.posY, 0.0, x / 256.0F, y / 256.0F);
        tessellator.addTexturedVertex(this.posX - skew, this.posY + 7.99F, 0.0, x / 256.0F, (y + 15.98F) / 256.0F);
        tessellator.addTexturedVertex(this.posX + max / 2.0F + skew, this.posY, 0.0, (x + max) / 256.0F, y / 256.0F);
        tessellator.addTexturedVertex(this.posX + max / 2.0F - skew, this.posY + 7.99F, 0.0, (x + max) / 256.0F, (y + 15.98F) / 256.0F);
        tessellator.draw();
        return (maxXExtended - minX) / 2.0F + 1.0F;
    }

    public void drawString(String text, int x, int y, int color) {
        if (this.bidi) {
            text = this.bidiReorder(text);
        }

        this.renderString(text, x, y, color, false);
    }

    public int drawStringWithShadow(String text, int x, int y, int color) {
        if (this.bidi) {
            text = this.bidiReorder(text);
        }

        int i = this.renderString(text, x + 1, y + 1, color, true);
        return Math.max(i, this.renderString(text, x, y, color, false));
    }

    public int drawStringWithOutline(String text, int x, int y, int color) {
        if (this.bidi) {
            text = this.bidiReorder(text);
        }

        this.renderString(text, x + 1, y, color, true);
        this.renderString(text, x - 1, y, color, true);
        this.renderString(text, x, y - 1, color, true);
        this.renderString(text, x, y + 1, color, true);
        return this.renderString(text, x, y, color, false);
    }

    public int drawStringWithBackground(String text, int x, int y, int z) {
        if (this.bidi) {
            text = this.bidiReorder(text);
        }

        GuiComponent.drawRectangle(x - 1, y - 1, x + this.getStringWidth(text) + 1, y + 9, Integer.MIN_VALUE);
        return this.renderString(text, x + 1, y + 1, z, false);
    }

    public void drawSplitString(String text, int x, int y, int width, int color, boolean shadow, boolean bg, int height) {
        if (this.bidi) {
            text = this.bidiReorder(text);
        }

        this.renderSplitString(text, x, y, width, color, shadow, bg, height);
    }

    private String bidiReorder(String text) {
        if (text != null && Bidi.requiresBidi(text.toCharArray(), 0, text.length())) {
            Bidi bidi = new Bidi(text, -2);
            byte[] bidiLength = new byte[bidi.getRunCount()];
            String[] newString = new String[bidiLength.length];

            for (int i = 0; i < bidiLength.length; i++) {
                int start = bidi.getRunStart(i);
                int end = bidi.getRunLimit(i);
                int totalLength = bidi.getRunLevel(i);
                String string = text.substring(start, end);
                bidiLength[i] = (byte)totalLength;
                newString[i] = string;
            }

            String[] clonedString = (String[])newString.clone();
            Bidi.reorderVisually(bidiLength, 0, newString, 0, bidiLength.length);
            StringBuilder stringbuilder = new StringBuilder();

            for (int l = 0; l < newString.length; l++) {
                byte strLength = bidiLength[l];

                for (int lght = 0; lght < clonedString.length; lght++) {
                    if (clonedString[lght].equals(newString[l])) {
                        strLength = bidiLength[lght];
                        break;
                    }
                }

                if ((strLength & 1) == 0) {
                    stringbuilder.append(newString[l]);
                } else {
                    for (int pos = newString[l].length() - 1; pos >= 0; pos--) {
                        char c = newString[l].charAt(pos);
                        if (c == '(') {
                            c = ')';
                        } else if (c == ')') {
                            c = '(';
                        }

                        stringbuilder.append(c);
                    }
                }
            }

            return stringbuilder.toString();
        } else {
            return text;
        }
    }

    private void applyStringFormatting(String text, boolean shadow) {
        boolean obfuscated = false;
        boolean bold = false;
        boolean italics = false;
        boolean underline = false;
        boolean strikethrough = false;

        for (int pos = 0; pos < text.length(); pos++) {
            char unicodeGlyph = text.charAt(pos);
            if (unicodeGlyph == 167 && pos + 1 < text.length()) {
                int codeIndex = ("0123456789abcdefgh" + "klmnor").indexOf(text.toLowerCase().charAt(pos + 1));
                if (codeIndex < "0123456789abcdefgh".length()) {
                    obfuscated = false;
                    bold = false;
                    strikethrough = false;
                    underline = false;
                    italics = false;
                    float r = 0.0F;
                    float g = 0.0F;
                    float b = 0.0F;
                    if (text.toLowerCase().charAt(pos + 1) == 'g') {
                        int rainbow = Color.HSBtoRGB((float)(System.currentTimeMillis() % 10000L) / 10000.0F, 1.0F, 1.0F) & 16777215;
                        if (shadow) {
                            int l = rainbow & 0xFF000000;
                            rainbow = (rainbow & 16579836) >> 2;
                            rainbow |= l;
                        }

                        r = (rainbow >> 16 & 0xFF) / 255.0F;
                        g = (rainbow >> 8 & 0xFF) / 255.0F;
                        b = (rainbow & 0xFF) / 255.0F;
                    }

                    if (codeIndex < 0 || codeIndex > "0123456789abcdefgh".length() - 1) {
                        codeIndex = 15;
                    }

                    if (codeIndex > 15) {
                        codeIndex = 32 + codeIndex - codeIndex / 16 * 16;
                    }

                    if (shadow) {
                        codeIndex += codeIndex > 15 ? ("0123456789abcdefgh".length() - 16) / 2 + 1 : 16;
                    }

                    int l = this.colorCode[codeIndex];
                    GL11.glColor4f((l >> 16) / 255.0F, (l >> 8 & 0xFF) / 255.0F, (l & 0xFF) / 255.0F, this.initA);
                    if (text.toLowerCase().charAt(pos + 1) == 'g') {
                        GL11.glColor3f(r, g, b);
                    }
                } else if (codeIndex == "0123456789abcdefgh".length()) {
                    obfuscated = true;
                } else if (codeIndex == "0123456789abcdefgh".length() + 1) {
                    bold = true;
                } else if (codeIndex == "0123456789abcdefgh".length() + 2) {
                    strikethrough = true;
                } else if (codeIndex == "0123456789abcdefgh".length() + 3) {
                    underline = true;
                } else if (codeIndex == "0123456789abcdefgh".length() + 4) {
                    italics = true;
                } else if (codeIndex == "0123456789abcdefgh".length() + 5) {
                    obfuscated = false;
                    bold = false;
                    strikethrough = false;
                    underline = false;
                    italics = false;
                    GL11.glColor4f(this.initR, this.initG, this.initB, this.initA);
                }

                pos++;
            } else {
                int glyph = SharedConstants.VALID_TEXT_CHARACTERS.indexOf(unicodeGlyph);
                if (obfuscated && glyph != -1 && unicodeGlyph != ' ') {
                    int charLen = this.getCharacterWidth(unicodeGlyph);

                    char randChar;
                    do {
                        int randCharIndex = this.randomizer.nextInt(SharedConstants.VALID_TEXT_CHARACTERS.length());
                        randChar = SharedConstants.VALID_TEXT_CHARACTERS.charAt(randCharIndex);
                    } while (charLen != this.getCharacterWidth(randChar) || SharedConstants.VALID_TEXT_CHARACTERS.indexOf(randChar) == -1 || randChar == '�');

                    unicodeGlyph = randChar;
                    glyph = SharedConstants.VALID_TEXT_CHARACTERS.indexOf(randChar);
                }

                float glyphPosOffset = this.renderChar(glyph, unicodeGlyph, italics);
                if (bold) {
                    this.posX++;
                    this.renderChar(glyph, unicodeGlyph, italics);
                    this.posX--;
                    glyphPosOffset++;
                }

                Tessellator tessellator = Tessellator.instance;
                if (strikethrough) {
                    GL11.glDisable(3553);
                    tessellator.begin();
                    tessellator.addVertex(this.posX, this.posY + this.fontHeight / 2, 0.0);
                    tessellator.addVertex(this.posX + glyphPosOffset, this.posY + this.fontHeight / 2, 0.0);
                    tessellator.addVertex(this.posX + glyphPosOffset, this.posY + this.fontHeight / 2 - 1.0F, 0.0);
                    tessellator.addVertex(this.posX, this.posY + this.fontHeight / 2 - 1.0F, 0.0);
                    tessellator.draw();
                    GL11.glEnable(3553);
                }

                if (underline) {
                    GL11.glDisable(3553);
                    tessellator.begin();
                    int indent = underline ? -1 : 0;
                    tessellator.addVertex(this.posX + indent, this.posY + this.fontHeight, 0.0);
                    tessellator.addVertex(this.posX + glyphPosOffset, this.posY + this.fontHeight, 0.0);
                    tessellator.addVertex(this.posX + glyphPosOffset, this.posY + this.fontHeight - 1.0F, 0.0);
                    tessellator.addVertex(this.posX + indent, this.posY + this.fontHeight - 1.0F, 0.0);
                    tessellator.draw();
                    GL11.glEnable(3553);
                }

                this.posX += glyphPosOffset;
            }
        }
    }

    public int renderString(String text, int x, int y, int colorValue, boolean shadow) {
        if (text != null) {
            this.boundTextureId = 0;
            if ((colorValue & -67108864) == 0) {
                colorValue |= -16777216;
            }

            if (shadow) {
                if (colorValue == -16777216) {
                    colorValue = -11184811;
                } else {
                    colorValue = (colorValue & 16579836) >> 2 | colorValue & 0xFF000000;
                }
            }

            this.initR = (colorValue >> 16 & 0xFF) / 255.0F;
            this.initG = (colorValue >> 8 & 0xFF) / 255.0F;
            this.initB = (colorValue & 0xFF) / 255.0F;
            this.initA = (colorValue >> 24 & 0xFF) / 255.0F;
            GL11.glColor4f(this.initR, this.initG, this.initB, this.initA);
            this.posX = x;
            this.posY = y;
            this.applyStringFormatting(text, shadow);
            this.initR = 1.0F;
            this.initB = 1.0F;
            this.initG = 1.0F;
            GL11.glColor4f(this.initR, this.initG, this.initB, this.initA);
            return (int)this.posX;
        } else {
            return 0;
        }
    }

    public int getStringWidth(String text) {
        if (text == null) {
            return 0;
        }

        int width = 0;
        boolean flag = false;

        for (int pos = 0; pos < text.length(); pos++) {
            char c = text.charAt(pos);
            int charWidth = this.getCharacterWidth(c);
            if (charWidth < 0 && pos < text.length() - 1) {
                char currChar = text.charAt(++pos);
                if (currChar == 'l' || currChar == 'L') {
                    flag = true;
                } else if (currChar == 'r' || currChar == 'R') {
                    flag = false;
                }

                charWidth = 0;
            }

            width += charWidth;
            if (flag) {
                width++;
            }
        }

        return width;
    }

    public int getCharacterWidth(char character) {
        if (character == 167) {
            return -1;
        }

        if (character == ' ') {
            return 4;
        }

        int index = SharedConstants.VALID_TEXT_CHARACTERS.indexOf(character);
        if (index >= 0 && !this.unicode) {
            return this.charWidth[index];
        }

        if (this.glyphWidth[character] != 0) {
            int width = this.glyphWidth[character] >>> 4;
            int height = this.glyphWidth[character] & 15;
            if (height > 7) {
                height = 15;
                width = 0;
            }

            height++;
            return (height - width) / 2 + 1;
        } else {
            return 0;
        }
    }

    public String trimStringToWidth(String text, int maxWidth, boolean fullString) {
        StringBuilder stringbuilder = new StringBuilder();
        int width = 0;
        int startingWidth = fullString ? text.length() - 1 : 0;
        byte byte0 = (byte)(fullString ? -1 : 1);
        boolean invalidLength = false;
        boolean ensureSpacing = false;

        for (int pos = startingWidth; pos >= 0 && pos < text.length() && width < maxWidth; pos += byte0) {
            char c = text.charAt(pos);
            int charWidth = this.getCharacterWidth(c);
            if (invalidLength) {
                invalidLength = false;
                if (c == 'l' || c == 'L') {
                    ensureSpacing = true;
                } else if (c == 'r' || c == 'R') {
                    ensureSpacing = false;
                }
            } else if (charWidth < 0) {
                invalidLength = true;
            } else {
                width += charWidth;
                if (ensureSpacing) {
                    width++;
                }
            }

            if (width > maxWidth) {
                break;
            }

            if (fullString) {
                stringbuilder.insert(0, c);
            } else {
                stringbuilder.append(c);
            }
        }

        return stringbuilder.toString();
    }

    private void renderSplitString(String text, int x, int y, int width, int color, boolean shadow, boolean bg, int height) {
        String[] strings = text.split("\n");
        if (strings.length > 1) {
            for (String string : strings) {
                this.renderSplitString(string, x, y, width, color, shadow, bg, height);
                y += this.getMaximumStringWidth(string, width) + height;
            }
        } else {
            String[] words = text.split(" ");
            int count = 0;
            String newString = "";

            while (count < words.length) {
                String tempString = newString + words[count++] + " ";

                while (count < words.length && this.getStringWidth(tempString + words[count]) < width) {
                    tempString = tempString + words[count++] + " ";
                }

                while (this.getStringWidth(tempString) > width) {
                    int sub = 0;

                    while (this.getStringWidth(tempString.substring(0, sub + 1)) <= width) {
                        sub++;
                    }

                    if (tempString.substring(0, sub).trim().length() > 0) {
                        String finalString = tempString.substring(0, sub);
                        if (finalString.lastIndexOf("§") >= 0) {
                            newString = "§" + finalString.charAt(finalString.lastIndexOf("§") + 1);
                        }

                        if (bg) {
                            GuiComponent.drawRectangle(x - 1, y - 1, x + this.getStringWidth(finalString) + 1, y + 9, Integer.MIN_VALUE);
                        }

                        this.renderString(finalString, x, y, color, shadow);
                        y += this.fontHeight + height;
                    }

                    tempString = newString + tempString.substring(sub);
                }

                if (this.getStringWidth(tempString.trim()) > 0) {
                    if (tempString.lastIndexOf("§") >= 0) {
                        newString = "§" + tempString.charAt(tempString.lastIndexOf("§") + 1);
                    }

                    if (bg) {
                        GuiComponent.drawRectangle(x - 1, y - 1, x + this.getStringWidth(tempString) + 1, y + 9, Integer.MIN_VALUE);
                    }

                    this.renderString(tempString, x, y, color, shadow);
                    y += this.fontHeight + height;
                }
            }
        }
    }

    public int getMaximumStringWidth(String text, int width) {
        String[] strings = text.split("\n");
        if (strings.length > 1) {
            int newWidth = 0;

            for (String string : strings) {
                newWidth += this.getMaximumStringWidth(string, width);
            }

            return newWidth;
        } else {
            String[] words = text.split(" ");
            int count = 0;
            int y = 0;

            while (count < words.length) {
                String word = words[count++] + " ";

                while (count < words.length && this.getStringWidth(word + words[count]) < width) {
                    word = word + words[count++] + " ";
                }

                while (this.getStringWidth(word) > width) {
                    int id = 0;

                    while (this.getStringWidth(word.substring(0, id + 1)) <= width) {
                        id++;
                    }

                    if (!word.substring(0, id).trim().isEmpty()) {
                        y += this.fontHeight;
                    }

                    word = word.substring(id);
                }

                if (!word.trim().isEmpty()) {
                    y += this.fontHeight;
                }
            }

            if (y < this.fontHeight) {
                y += this.fontHeight;
            }

            return y;
        }
    }

    public List<String> getStringAsList(String text, int maxLength) {
        return Arrays.asList(this.getFullString(text, maxLength).split("\n"));
    }

    private String getFullString(String text, int maxLength) {
        int length = this.getFixedCharLength(text, maxLength);
        if (text.length() <= length) {
            return text;
        }

        String newString = text.substring(0, length);
        String tempString = getAppendedString(newString) + text.substring(length + (text.charAt(length) != ' ' ? 0 : 1));
        return newString + "\n" + this.getFullString(tempString, maxLength);
    }

    private int getFixedCharLength(String text, int length) {
        int strLength = text.length();
        int newWidth = 0;
        int tempWidth = 0;
        int finalWidth = -1;
        boolean exception = false;

        while (tempWidth < strLength) {
            char character = text.charAt(tempWidth);
            switch (character) {
                case ' ':
                    finalWidth = tempWidth;
                default:
                    newWidth += this.getCharacterWidth(character);
                    if (exception) {
                        newWidth++;
                    }
                    break;
                case '§':
                    if (tempWidth != strLength) {
                        char selectedChar = text.charAt(++tempWidth);
                        if (selectedChar == 'l' || selectedChar == 'L') {
                            exception = true;
                        } else if (selectedChar == 'r' || selectedChar == 'R') {
                            exception = false;
                        }
                    }
            }

            if (character == '\n') {
                finalWidth = ++tempWidth;
                break;
            }

            if (newWidth > length) {
                break;
            }

            tempWidth++;
        }

        return tempWidth != strLength && finalWidth != -1 && finalWidth < tempWidth ? finalWidth : tempWidth;
    }

    private static boolean isCharColorCoded(char character) {
        return character >= '0' && character <= '9' || character >= 'a' && character <= 'h' || character >= 'A' && character <= 'H';
    }

    private static boolean isCharCoded(char character) {
        return character >= 'k' && character <= 'o' || character >= 'K' && character <= 'O' || character == 'r' || character == 'R';
    }

    private static String getAppendedString(String text) {
        String appendedString = "";
        int index = -1;
        int textLength = text.length();

        while ((index = text.indexOf(167, index + 1)) != -1) {
            if (index < textLength - 1) {
                char character = text.charAt(index + 1);
                if (isCharColorCoded(character)) {
                    appendedString = "§" + character;
                } else if (isCharCoded(character)) {
                    appendedString = appendedString + "§" + character;
                }
            }
        }

        return appendedString;
    }

    public static String removeFormattingCodesFromString(String string) {
        return formatCodeRegex.matcher(string).replaceAll("");
    }
}
