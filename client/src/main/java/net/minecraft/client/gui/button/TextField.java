package net.minecraft.client.gui.button;

import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.GuiScreen;
import net.minecraft.client.localization.Font;
import net.minecraft.client.render.Tessellator;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class TextField extends GuiComponent {
    private final Font fontRenderer;
    private String text = "";
    private String flavorText = "";
    public final int x;
    public final int y;
    private final int width;
    private final int height;
    private int maxStringLength = 32;
    private int cursorCounter;
    private int prevCursorPos = 0;
    private int cursorPosition = 0;
    private int selectionEnd = 0;
    private int textColor = 14737632;
    private boolean visible = true;
    private boolean enabled = true;
    public boolean focused = false;
    private boolean renderBackground = true;

    public TextField(Font font, int x, int y, int width, int height) {
        this.fontRenderer = font;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void updateCursor() {
        this.cursorCounter++;
    }

    public void setText(String text) {
        if (text.length() > this.maxStringLength) {
            this.text = text.substring(0, this.maxStringLength);
        } else {
            this.text = text;
        }

        this.setCursorPosition(this.text.length());
    }

    public void setFlavorText(String title) {
        this.flavorText = title;
    }

    public String getText() {
        return this.text;
    }

    public String getSelectedtext() {
        int start = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
        int end = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
        return this.text.substring(start, end);
    }

    public void writeText(String text) {
        String newText = "";
        String filteredText = SharedConstants.filterAllowedCharacters(text);
        int textEnd = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
        int textStart = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
        int currPos = this.maxStringLength - this.text.length() - (textEnd - this.selectionEnd);
        if (this.text.length() > 0) {
            newText = newText + this.text.substring(0, textEnd);
        }

        int newPos;
        if (currPos < filteredText.length()) {
            newText = newText + filteredText.substring(0, currPos);
            newPos = currPos;
        } else {
            newText = newText + filteredText;
            newPos = filteredText.length();
        }

        if (this.text.length() > 0 && textStart < this.text.length()) {
            newText = newText + this.text.substring(textStart);
        }

        this.text = newText;
        this.moveCursorBy(textEnd - this.selectionEnd + newPos);
    }

    public void deleteWords(int pos) {
        if (this.text.length() != 0) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                this.deleteFromCursor(this.addOrRemoveCharacter(pos, this.getCursorPosition(), true) - this.cursorPosition);
            }
        }
    }

    public void deleteFromCursor(int pos) {
        if (this.text.length() != 0) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                boolean end = pos < 0;
                int textEnd = end ? this.cursorPosition + pos : this.cursorPosition;
                int textStart = end ? this.cursorPosition : this.cursorPosition + pos;
                String textDelete = "";
                if (textEnd >= 0) {
                    textDelete = this.text.substring(0, textEnd);
                }

                if (textStart < this.text.length()) {
                    textDelete = textDelete + this.text.substring(textStart);
                }

                this.text = textDelete;
                if (end) {
                    this.moveCursorBy(pos);
                }
            }
        }
    }

    public int addOrRemoveCharacter(int start, int end, boolean deleting) {
        boolean isAtEdge = start < 0;
        int txtFull = Math.abs(start);

        for (int pos = 0; pos < txtFull; pos++) {
            if (!isAtEdge) {
                int textLength = this.text.length();
                end = this.text.indexOf(32, end);
                if (end == -1) {
                    end = textLength;
                } else {
                    while (deleting && end < textLength && this.text.charAt(end) == ' ') {
                        end++;
                    }
                }
            } else {
                while (deleting && end > 0 && this.text.charAt(end - 1) == ' ') {
                    end--;
                }

                while (end > 0 && this.text.charAt(end - 1) != ' ') {
                    end--;
                }
            }
        }

        return end;
    }

    public void moveCursorBy(int offset) {
        this.setCursorPosition(this.selectionEnd + offset);
    }

    public void setCursorPosition(int pos) {
        this.cursorPosition = pos;
        int txtLength = this.text.length();
        if (this.cursorPosition < 0) {
            this.cursorPosition = 0;
        }

        if (this.cursorPosition > txtLength) {
            this.cursorPosition = txtLength;
        }

        this.setSelectionPos(this.cursorPosition);
    }

    public boolean textboxKeyTyped(char character, int keyCode) {
        if (this.enabled && this.focused) {
            switch (character) {
                case '\u0001':
                    this.setCursorPosition(this.text.length());
                    this.setSelectionPos(0);
                    return true;
                case '\u0003':
                    GuiScreen.setClipboardString(this.getSelectedtext());
                    return true;
                case '\u0016':
                    this.writeText(GuiScreen.getClipboardString());
                    return true;
                case '\u0018':
                    GuiScreen.setClipboardString(this.getSelectedtext());
                    this.writeText("");
                    return true;
                default:
                    switch (keyCode) {
                        case 14:
                            if (isCtrlKeyDown()) {
                                this.deleteWords(-1);
                            } else {
                                this.deleteFromCursor(-1);
                            }

                            return true;
                        case 199:
                            if (isShiftKeyDown()) {
                                this.setSelectionPos(0);
                            } else {
                                this.setCursorPosition(0);
                            }

                            return true;
                        case 203:
                            if (isShiftKeyDown()) {
                                if (isCtrlKeyDown()) {
                                    this.setSelectionPos(this.addOrRemoveCharacter(-1, this.selectionEnd, true));
                                } else {
                                    this.setSelectionPos(this.selectionEnd - 1);
                                }
                            } else if (isCtrlKeyDown()) {
                                this.setCursorPosition(this.addOrRemoveCharacter(-1, this.getCursorPosition(), true));
                            } else {
                                this.moveCursorBy(-1);
                            }

                            return true;
                        case 205:
                            if (isShiftKeyDown()) {
                                if (isCtrlKeyDown()) {
                                    this.setSelectionPos(this.addOrRemoveCharacter(1, this.selectionEnd, true));
                                } else {
                                    this.setSelectionPos(this.selectionEnd + 1);
                                }
                            } else if (isCtrlKeyDown()) {
                                this.setCursorPosition(this.addOrRemoveCharacter(1, this.getCursorPosition(), true));
                            } else {
                                this.moveCursorBy(1);
                            }

                            return true;
                        case 207:
                            if (isShiftKeyDown()) {
                                this.setSelectionPos(this.text.length());
                            } else {
                                this.setCursorPosition(this.text.length());
                            }

                            return true;
                        case 211:
                            if (isCtrlKeyDown()) {
                                this.deleteWords(1);
                            } else {
                                this.deleteFromCursor(1);
                            }

                            return true;
                        default:
                            if (SharedConstants.isValidCharacter(character)) {
                                this.writeText(Character.toString(character));
                                return true;
                            } else {
                                return false;
                            }
                    }
            }
        } else {
            return false;
        }
    }

    public void mouseClicked(int x, int y, int click) {
        boolean inBounds = x >= this.x && x < this.x + this.width && y >= this.y && y < this.y + this.height;
        this.setFocused(this.enabled && inBounds);
        if (this.focused && click == 0) {
            int offset = x - this.x;
            if (this.renderBackground) {
                offset -= 4;
            }

            String text = this.fontRenderer.trimStringToWidth(this.text.substring(this.prevCursorPos), this.getWidth(), false);
            this.setCursorPosition(this.fontRenderer.trimStringToWidth(text, offset, false).length() + this.prevCursorPos);
        }
    }

    public void drawTextBox() {
        if (this.visible) {
            if (this.renderBackground) {
                int col = -8355711;
                if (this.focused) {
                    col = -1;
                }

                drawRectangle(this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, col);
                drawRectangle(this.x, this.y, this.x + this.width, this.y + this.height, -16777216);
            }

            int color = this.textColor;
            int pos = this.cursorPosition - this.prevCursorPos;
            int txtLength = this.selectionEnd - this.prevCursorPos;
            String text = this.fontRenderer.trimStringToWidth(this.text.substring(this.prevCursorPos), this.getWidth(), false);
            boolean isInBounds = pos >= 0 && pos <= text.length();
            boolean isActive = this.focused && this.cursorCounter / 6 % 2 == 0 && isInBounds;
            int x = this.renderBackground ? this.x + 4 : this.x;
            int y = this.renderBackground ? this.y + (this.height - 8) / 2 : this.y;
            int stringOffset = x;
            if (txtLength > text.length()) {
                txtLength = text.length();
            }

            if (text.length() > 0) {
                String str = isInBounds ? text.substring(0, pos) : text;
                stringOffset = this.fontRenderer.drawStringWithShadow(str, x, y, color);
            }

            boolean cursorMoved = this.cursorPosition < this.text.length() || this.text.length() >= this.maxStringLength;
            int finalOffset = stringOffset;
            if (!isInBounds) {
                finalOffset = pos > 0 ? x + this.width : x;
            } else if (cursorMoved) {
                finalOffset = stringOffset - 1;
                stringOffset--;
            }

            if (text.length() > 0 && isInBounds && pos < text.length()) {
                this.drawString(this.fontRenderer, text.substring(pos), stringOffset, y, color);
            }

            if (isActive) {
                if (cursorMoved) {
                    drawRectangle(finalOffset, y - 1, finalOffset + 1, y + 1 + this.fontRenderer.fontHeight, -3092272);
                } else {
                    this.drawString(this.fontRenderer, "_", finalOffset, y, color);
                }
            }

            if (!this.focused && (this.text == "" || this.text.isEmpty()) && !this.flavorText.isEmpty()) {
                this.drawString(this.fontRenderer, this.flavorText, x, y, 14737632);
            }

            if (txtLength != pos) {
                int newX = x + this.fontRenderer.getStringWidth(text.substring(0, txtLength));
                this.drawCursor(finalOffset, y - 1, newX - 1, y + 1 + this.fontRenderer.fontHeight);
            }
        }
    }

    private void drawCursor(int startX, int startY, int endX, int endY) {
        if (startX < endX) {
            int savedValue = startX;
            startX = endX;
            endX = savedValue;
        }

        if (startY < endY) {
            int savedValue = startY;
            startY = endY;
            endY = savedValue;
        }

        Tessellator tess = Tessellator.instance;
        GL11.glColor4f(0.0F, 0.0F, 255.0F, 255.0F);
        GL11.glDisable(3553);
        GL11.glEnable(3058);
        GL11.glLogicOp(5387);
        tess.begin();
        tess.addVertex(startX, endY, 0.0);
        tess.addVertex(endX, endY, 0.0);
        tess.addVertex(endX, startY, 0.0);
        tess.addVertex(startX, startY, 0.0);
        tess.draw();
        GL11.glDisable(3058);
        GL11.glEnable(3553);
    }

    public void setMaxStringLength(int length) {
        this.maxStringLength = length;
        if (this.text.length() > length) {
            this.text = this.text.substring(0, length);
        }
    }

    public void setSelectionPos(int pos) {
        int textLength = this.text.length();
        if (pos > textLength) {
            pos = textLength;
        }

        if (pos < 0) {
            pos = 0;
        }

        this.selectionEnd = pos;
        if (this.fontRenderer != null) {
            if (this.prevCursorPos > textLength) {
                this.prevCursorPos = textLength;
            }

            int textboxWidth = this.getWidth();
            String text = this.fontRenderer.trimStringToWidth(this.text.substring(this.prevCursorPos), textboxWidth, false);
            int maximum = text.length() + this.prevCursorPos;
            if (pos == this.prevCursorPos) {
                this.prevCursorPos = this.prevCursorPos - this.fontRenderer.trimStringToWidth(this.text, textboxWidth, true).length();
            }

            if (pos > maximum) {
                this.prevCursorPos += pos - maximum;
            } else if (pos <= this.prevCursorPos) {
                this.prevCursorPos = this.prevCursorPos - (this.prevCursorPos - pos);
            }

            if (this.prevCursorPos < 0) {
                this.prevCursorPos = 0;
            }

            if (this.prevCursorPos > textLength) {
                this.prevCursorPos = textLength;
            }
        }
    }

    public int getWidth() {
        return this.renderBackground ? this.width - 8 : this.width;
    }

    public void setTextColor(int col) {
        this.textColor = col;
    }

    public void setRenderBackground(boolean state) {
        this.renderBackground = state;
    }

    public void setFocused(boolean state) {
        if (state && !this.focused) {
            this.cursorCounter = 0;
        }

        this.focused = state;
    }

    public void setEnabled(boolean state) {
        this.enabled = state;
    }

    public void setVisible(boolean state) {
        this.visible = state;
    }

    public boolean isFocused() {
        return this.focused;
    }

    public int getCursorPosition() {
        return this.cursorPosition;
    }

    public static boolean isCtrlKeyDown() {
        boolean var0 = Keyboard.isKeyDown(28) && Keyboard.getEventCharacter() == 0;
        return Keyboard.isKeyDown(29)
            || Keyboard.isKeyDown(157)
            || Minecraft.getOs() == Minecraft.EnumOS.macos && (var0 || Keyboard.isKeyDown(219) || Keyboard.isKeyDown(220));
    }

    public static boolean isShiftKeyDown() {
        return Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
    }
}
