package net.minecraft.client.gui.screens.game;

import com.mojang.nbt.tag.special.CompoundTag;
import com.mojang.nbt.tag.special.ListTag;
import com.mojang.nbt.tag.special.StringTag;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.button.Button;
import net.minecraft.client.gui.button.PageButton;
import net.minecraft.client.gui.screens.GuiScreen;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.localization.Translator;
import net.minecraft.network.packet.container.CustomPayloadPacket;
import net.minecraft.util.io.IOUtil;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class BookScreen extends GuiScreen {
    private static final int MAX_PAGES = 100;
    private ListTag<StringTag> pages;
    private final Player author;
    private final ItemStack book;
    private Button doneButton;
    private Button signButton;
    private Button finalizeButton;
    private Button cancelButton;
    private PageButton nextPage;
    private PageButton prevPage;
    private String bookTitle = "";
    private final boolean isEditable;
    private boolean editingTitle;
    private int updateCount;
    private int bookImageWidth = 192;
    private int bookImageHeight = 192;
    private int pageCount = 1;
    private int currPage;
    private int cursorPos;

    public BookScreen(Player author, ItemStack book, boolean signed) {
        this.author = author;
        this.book = book;
        this.isEditable = signed;
        if (book.hasTag()) {
            CompoundTag pages = book.getTag();
            this.pages = pages.getTagList("pages");
            if (this.pages != null) {
                this.pages = this.pages.copy();
                this.pageCount = this.pages.size();
                if (this.pageCount < 1) {
                    this.pageCount = 1;
                }
            }
        }

        if (this.pages == null && signed) {
            this.pages = new ListTag<>();
            this.pages.add(new StringTag(""));
            this.pageCount = 1;
        }

        if (signed) {
            this.currPage = this.pageCount - 1;
        }

        this.cursorPos = this.getText().length() - 1;
    }

    @Override
    public void update() {
        this.updateCount++;
    }

    @Override
    public void initGui() {
        this.buttons.clear();
        Keyboard.enableRepeatEvents(true);
        if (this.isEditable) {
            this.buttons
                .add(
                    this.finalizeButton = new Button(
                        5, this.width / 2 - 100, 4 + this.bookImageHeight, 98, 20, Translator.getInstance().translateKey("gui.book.close")
                    )
                );
            this.buttons
                .add(
                    this.signButton = new Button(
                        3, this.width / 2 - 100, 4 + this.bookImageHeight, 98, 20, Translator.getInstance().translateKey("gui.book.sign")
                    )
                );
            this.buttons
                .add(this.doneButton = new Button(0, this.width / 2 + 2, 4 + this.bookImageHeight, 98, 20, Translator.getInstance().translateKey("gui.done")));
            this.buttons
                .add(
                    this.cancelButton = new Button(4, this.width / 2 + 2, 4 + this.bookImageHeight, 98, 20, Translator.getInstance().translateKey("gui.cancel"))
                );
        } else {
            this.buttons
                .add(
                    this.doneButton = new Button(0, this.width / 2 - 100, 4 + this.bookImageHeight, 200, 20, Translator.getInstance().translateKey("gui.done"))
                );
        }

        int x = (this.width - this.bookImageWidth) / 2;
        this.buttons.add(this.nextPage = new PageButton(1, x + 120, 156, true));
        this.buttons.add(this.prevPage = new PageButton(2, x + 38, 156, false));
        this.updateButtons();
    }

    @Override
    public void onClose() {
        super.onClose();
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private void updateButtons() {
        this.nextPage.visible = !this.editingTitle && (this.currPage < this.pageCount - 1 || this.isEditable);
        this.prevPage.visible = !this.editingTitle && this.currPage > 0;
        this.doneButton.visible = !this.isEditable || !this.editingTitle;
        if (this.isEditable) {
            this.signButton.visible = !this.editingTitle;
            this.cancelButton.visible = this.editingTitle;
            this.finalizeButton.visible = this.editingTitle;
            this.finalizeButton.enabled = this.bookTitle.trim().length() > 0;
        }
    }

    private void updateBookContents(boolean sign) {
        if (this.isEditable && this.pages != null) {
            while (this.pages.size() > 1) {
                StringTag page = this.pages.get(this.pages.size() - 1);
                if (page.string != null && page.string.length() != 0) {
                    break;
                }

                this.pages.remove(this.pages.size() - 1);
            }

            if (this.book.hasTag()) {
                CompoundTag page = this.book.getTag();
                page.setTag("pages", this.pages);
            } else {
                this.book.setTagInfo("pages", this.pages);
            }

            String packetID = "MC|BEdit";
            if (sign) {
                packetID = "MC|BSign";
                this.book.setTagInfo("author", new StringTag(this.author.username));
                this.book.setTagInfo("title", new StringTag(this.bookTitle.trim()));
                this.book.itemID = ItemList.writtenBook.id;
            }

            if (this.mc.isMultiplayerWorld()) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(outputStream);

                try {
                    IOUtil.writeItemStack(this.book, out);
                    this.mc.getSendQueue().sendPacket(new CustomPayloadPacket(packetID, outputStream.toByteArray()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void buttonClicked(Button button) {
        if (button.enabled) {
            if (button == this.doneButton) {
                this.mc.setScreen(null);
                this.updateBookContents(false);
            } else if (button == this.signButton && this.isEditable) {
                this.editingTitle = true;
            } else if (button == this.nextPage) {
                this.nextPage();
            } else if (button == this.prevPage) {
                this.prevPage();
            } else if (button == this.finalizeButton && this.editingTitle) {
                this.updateBookContents(true);
                this.mc.setScreen(null);
            } else if (button == this.cancelButton && this.editingTitle) {
                this.editingTitle = false;
            }

            this.updateButtons();
        }
    }

    private void nextPage() {
        if (this.currPage < this.pageCount - 1) {
            this.currPage++;
        } else if (this.isEditable) {
            this.addNewPage();
            if (this.currPage < this.pageCount - 1) {
                this.currPage++;
            }
        }

        this.cursorPos = this.getText().length() - 1;
    }

    private void prevPage() {
        if (this.currPage > 0) {
            this.currPage--;
        }

        this.mc.sndManager.playSoundFX("random.page", 1.0F, 1.0F);
        this.cursorPos = this.getText().length() - 1;
    }

    private void addNewPage() {
        if (this.pages != null && this.pages.size() < 100) {
            this.pages.add(new StringTag(""));
            this.pageCount++;
        }
    }

    @Override
    protected void keyTyped(char character, int key) {
        if (key == 1) {
            this.updateBookContents(false);
            this.mc.setScreen(null);
            this.mc.setFocus();
        }

        if (this.isEditable) {
            if (this.editingTitle) {
                this.editTitle(character, key);
            } else {
                this.keyTypedInBook(character, key);
            }
        }
    }

    private List<String> wrapLines(String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        String[] newLines = text.split("\n");
        if (newLines.length > 1) {
            for (String string : newLines) {
                lines.addAll(this.wrapLines(string, maxWidth));
            }

            return lines;
        } else {
            String[] words = text.split(" ");
            int count = 0;

            while (count < words.length) {
                String tempString = words[count++] + " ";

                while (count < words.length && this.fontRenderer.getStringWidth(tempString + words[count]) < maxWidth) {
                    tempString = tempString + words[count++] + " ";
                }

                while (this.fontRenderer.getStringWidth(tempString) > maxWidth) {
                    int sub = 0;

                    while (this.fontRenderer.getStringWidth(tempString.substring(0, sub + 1)) <= maxWidth) {
                        sub++;
                    }

                    String finalString = tempString.substring(0, sub).trim();
                    if (!finalString.isEmpty()) {
                        lines.add(finalString);
                    }

                    tempString = tempString.substring(sub);
                }

                if (!tempString.isEmpty()) {
                    lines.add(tempString);
                }
            }

            return lines;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        if (button == 0 && this.isEditable && !this.editingTitle) {
            String pageText = this.getText();
            int textX = (this.width - this.bookImageWidth) / 2 + 36;
            int textY = 34;
            int lineHeight = 9;
            List<String> lines = this.wrapLines(pageText, 116);
            int relativeY = mouseY - textY;
            int clickedLine = relativeY / lineHeight;
            if (clickedLine >= 0 && clickedLine < lines.size()) {
                String line = lines.get(clickedLine);
                int relativeX = mouseX - textX;
                int charIndex = 0;
                int widthSoFar = 0;

                while (charIndex < line.length()) {
                    char c = line.charAt(charIndex);
                    int charWidth = this.fontRenderer.getCharacterWidth(c);
                    if (widthSoFar + charWidth / 2 >= relativeX) {
                        break;
                    }

                    widthSoFar += charWidth;
                    charIndex++;
                }

                int globalIndex = 0;

                for (int i = 0; i < clickedLine; i++) {
                    globalIndex += lines.get(i).length();
                }

                globalIndex += charIndex;
                this.cursorPos = Math.min(globalIndex, pageText.length() - 1);
            } else {
                this.cursorPos = pageText.length() - 1;
            }
        }

        super.mouseClicked(mouseX, mouseY, button);
    }

    private void keyTypedInBook(char character, int key) {
        switch (character) {
            case '\u0003':
                GuiScreen.setClipboardString(this.getText());
                return;
            case '\u0016':
                this.pasteString(GuiScreen.getClipboardString());
                return;
            default:
                String text = this.getText();
                StringBuilder sb = new StringBuilder(text);
                switch (key) {
                    case 14:
                        if (text.length() > 0 && this.cursorPos >= 0) {
                            sb.deleteCharAt(this.cursorPos);
                            this.cursorPos--;
                            this.signBook(sb.toString());
                        }

                        return;
                    case 28:
                        this.typeCharacter("\n");
                        return;
                    case 200:
                        if (this.cursorPos > 0) {
                            this.moveCursorVertically(false);
                        }

                        return;
                    case 201:
                        this.nextPage();
                        this.mc.sndManager.playSoundFX("random.page", 1.0F, 1.0F);
                        return;
                    case 203:
                        if (this.cursorPos >= 0) {
                            this.cursorPos--;
                        }

                        return;
                    case 205:
                        if (this.cursorPos < this.getText().length() - 1) {
                            this.cursorPos++;
                        }

                        return;
                    case 208:
                        if (this.cursorPos > 0) {
                            this.moveCursorVertically(true);
                        }

                        return;
                    case 209:
                        this.prevPage();
                        this.mc.sndManager.playSoundFX("random.page", 1.0F, 1.0F);
                        return;
                    case 211:
                        if (text.length() > 0 && this.cursorPos < text.length() - 1) {
                            sb.deleteCharAt(this.cursorPos + 1);
                            this.signBook(sb.toString());
                        }

                        return;
                    default:
                        if (SharedConstants.isValidCharacter(character)) {
                            this.typeCharacter(Character.toString(character));
                        }
                }
        }
    }

    private void moveCursorVertically(boolean reverse) {
        List<String> lines = this.wrapLines(this.getText(), 116);
        List<Integer> lineStartIndices = new ArrayList<>();
        int absoluteIndex = 0;

        for (String line : lines) {
            lineStartIndices.add(absoluteIndex);
            absoluteIndex += line.length();
        }

        int currentLine = 0;

        for (int i = 0; i < lineStartIndices.size(); i++) {
            int lineStart = lineStartIndices.get(i);
            int lineEnd = i + 1 < lineStartIndices.size() ? lineStartIndices.get(i + 1) : this.getText().length();
            if (this.cursorPos >= lineStart && this.cursorPos <= lineEnd) {
                currentLine = i;
                break;
            }
        }

        int lineStart = lineStartIndices.get(currentLine);
        int pixelOffset = this.fontRenderer.getStringWidth(this.getText().substring(lineStart, this.cursorPos));
        if (reverse) {
            if (currentLine + 1 < lineStartIndices.size()) {
                int nextLineStart = lineStartIndices.get(currentLine + 1);
                String nextLineText = lines.get(currentLine + 1);
                int bestIndex = 0;
                int bestDiff = Integer.MAX_VALUE;

                for (int i = 0; i <= nextLineText.length(); i++) {
                    int width = this.fontRenderer.getStringWidth(nextLineText.substring(0, i));
                    int diff = Math.abs(width - pixelOffset);
                    if (diff < bestDiff) {
                        bestDiff = diff;
                        bestIndex = i;
                    }
                }

                if (bestIndex > nextLineText.length() - 1) {
                    this.cursorPos = nextLineStart + nextLineText.length() - 1;
                } else {
                    this.cursorPos = nextLineStart + bestIndex;
                }

                if (this.cursorPos > this.getText().length() - 1) {
                    this.cursorPos = this.getText().length() - 1;
                }
            }
        } else if (currentLine > 0) {
            int prevLineStart = lineStartIndices.get(currentLine - 1);
            String prevLineText = lines.get(currentLine - 1);
            int bestIndex = 0;
            int bestDiff = Integer.MAX_VALUE;

            for (int i = 0; i <= prevLineText.length(); i++) {
                int width = this.fontRenderer.getStringWidth(prevLineText.substring(0, i));
                int diff = Math.abs(width - pixelOffset);
                if (diff < bestDiff) {
                    bestDiff = diff;
                    bestIndex = i;
                }
            }

            if (bestIndex > prevLineText.length() - 1) {
                this.cursorPos = prevLineStart + prevLineText.length() - 1;
            } else {
                this.cursorPos = prevLineStart + bestIndex;
            }
        }
    }

    private void editTitle(char character, int key) {
        switch (key) {
            case 14:
                if (this.bookTitle.length() > 0) {
                    this.bookTitle = this.bookTitle.substring(0, this.bookTitle.length() - 1);
                    this.updateButtons();
                }

                return;
            case 28:
                if (this.bookTitle.length() > 0) {
                    this.updateBookContents(true);
                    this.mc.setScreen((GuiScreen)null);
                }

                return;
            default:
                if (this.bookTitle.length() < 16 && SharedConstants.isValidCharacter(character)) {
                    this.bookTitle = this.bookTitle + Character.toString(character);
                    this.updateButtons();
                }
        }
    }

    private String getText() {
        if (this.pages != null && this.currPage >= 0 && this.currPage < this.pages.size()) {
            StringTag data = this.pages.get(this.currPage);
            return data.toString();
        } else {
            return "";
        }
    }

    private void signBook(String author) {
        if (this.pages != null && this.currPage >= 0 && this.currPage < this.pages.size()) {
            StringTag saveData = this.pages.get(this.currPage);
            saveData.string = author;
        }
    }

    private void typeCharacter(String newCharacter) {
        String data = this.getText();
        String temp = data + newCharacter;
        int maxWidth = this.fontRenderer.getMaximumStringWidth(temp + '§' + "0_", 118);
        if (maxWidth <= 118 && temp.length() < 256) {
            StringBuilder sb = new StringBuilder(data);
            sb.insert(this.cursorPos + 1, newCharacter.charAt(0));
            temp = sb.toString();
            this.cursorPos++;
            this.signBook(temp);
        }
    }

    private void pasteString(String newCharacter) {
        String data = this.getText();
        String temp = data + newCharacter;
        int maxWidth = this.fontRenderer.getMaximumStringWidth(temp + '§' + "0_", 118);
        if (maxWidth <= 118 && temp.length() < 256) {
            this.signBook(temp);
            this.cursorPos = temp.length() - 1;
        }
    }

    @Override
    public void drawScreen(int width, int height, float tickDelta) {
        this.drawBackground();
        int tex = this.mc.textureLoader.getTexture("/gui/book.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.textureLoader.bindTexture(tex);
        int x = (this.width - this.bookImageWidth) / 2;
        int y = 2;
        this.drawGraphic(x, y, 0, 0, this.bookImageWidth, this.bookImageHeight);
        if (this.editingTitle) {
            String title = this.bookTitle;
            if (this.isEditable) {
                if (this.updateCount / 6 % 2 == 0) {
                    title = title + '§' + "0_";
                } else {
                    title = title + '§' + "7_";
                }
            }

            String text = Translator.getInstance().translateKey("gui.book.title") + ":";
            int textWidth = this.fontRenderer.getStringWidth(text);
            this.drawStringNoShadow(this.fontRenderer, text, x + 36 + (116 - textWidth) / 2, y + 16 + 16, 0);
            int titleWidth = this.fontRenderer.getStringWidth(title);
            this.drawStringNoShadow(this.fontRenderer, title, x + 36 + (116 - titleWidth) / 2, y + 48, 0);
            String itemName = Translator.getInstance().translateKey("gui.book.by") + " " + this.author.username;
            int nameWidth = this.fontRenderer.getStringWidth(itemName);
            this.drawStringNoShadow(this.fontRenderer, "§8" + itemName, x + 36 + (116 - nameWidth) / 2, y + 48 + 10, 0);
            String warning = Translator.getInstance().translateKey("gui.book.warn");
            this.drawSplitString(this.fontRenderer, warning, x + 36, y + 80, 116, 0, false, 0);
        } else {
            String title = Translator.getInstance().translateKey("gui.book.page")
                + " "
                + (this.currPage + 1)
                + " "
                + Translator.getInstance().translateKey("gui.book.of")
                + " "
                + this.pageCount;
            String text = "";
            if (this.pages != null && this.currPage >= 0 && this.currPage < this.pages.size()) {
                StringTag data = this.pages.get(this.currPage);
                text = data.toString();
            }

            if (this.isEditable) {
                if (this.cursorPos == this.getText().length() - 1) {
                    if (this.updateCount / 6 % 2 == 0) {
                        text = text + '§' + "0_";
                    } else {
                        text = text + '§' + "7_";
                    }
                } else {
                    List<String> lines = this.wrapLines(this.getText(), 116);
                    int totalIndex = 0;
                    int cursorX = 0;
                    int cursorY = 0;
                    int lineLength = 0;

                    for (int lineNum = 0; lineNum < lines.size(); lineNum++) {
                        String line = lines.get(lineNum);
                        lineLength = line.length();
                        if (this.cursorPos < totalIndex + lineLength) {
                            int relativeIndex = this.cursorPos - totalIndex;
                            String sub = "";
                            int offset = 0;
                            if (this.cursorPos >= 0) {
                                sub = line.substring(0, Math.min(relativeIndex, line.length()));
                                offset = this.fontRenderer.getCharacterWidth(text.charAt(this.cursorPos)) - 1;
                            }

                            cursorX = x + 36 + this.fontRenderer.getStringWidth(sub) + offset;
                            cursorY = y + 32 + lineNum * 9;
                            break;
                        }

                        totalIndex += lineLength;
                    }

                    if (this.updateCount / 6 % 2 == 0) {
                        drawRectangle(cursorX, cursorY, cursorX + 1, cursorY + 8, -16777216);
                    }
                }
            }

            int textWidth = this.fontRenderer.getStringWidth(title);
            this.drawStringNoShadow(this.fontRenderer, title, x - textWidth + this.bookImageWidth - 44, y + 16, 0);
            this.drawSplitString(this.fontRenderer, text, x + 36, y + 32, 116, 0, false, 0);
        }

        super.drawScreen(width, height, tickDelta);
    }
}
