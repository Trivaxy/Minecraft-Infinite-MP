package net.minecraft.client.gui.overlay;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.multiplayer.ChatScreen;
import net.minecraft.client.localization.Font;
import org.lwjgl.opengl.GL11;

public class ChatOverlay extends GuiComponent {
    private static final int MAX_CHAT_SIZE = 100;
    public List<ChatOverlay.ChatMessage> activeChatMessages = new ArrayList<>();
    private List<String> chatHistory = new ArrayList<>();
    private Minecraft mc;
    public int chatScrollAmount;

    public ChatOverlay(Minecraft mc) {
        this.mc = mc;
    }

    public void updateChat(Font font) {
        byte offset = 10;
        boolean chatOpen = false;
        int msgCount = 0;
        int totalMessages = this.activeChatMessages.size();
        if (totalMessages > 0) {
            if (this.isChatOpen()) {
                offset = 20;
                chatOpen = true;
            }

            for (int msg = 0; msg + this.chatScrollAmount < this.activeChatMessages.size() && msg < offset; msg++) {
                if (this.activeChatMessages.get(msg).updateCounter < 200 || chatOpen) {
                    ChatOverlay.ChatMessage chatline = this.activeChatMessages.get(msg + this.chatScrollAmount);
                    double counter = chatline.updateCounter / 200.0;
                    counter = 1.0 - counter;
                    counter *= 10.0;
                    if (counter < 0.0) {
                        counter = 0.0;
                    }

                    if (counter > 1.0) {
                        counter = 1.0;
                    }

                    counter *= counter;
                    int alphaValue = (int)(255.0 * counter);
                    if (chatOpen) {
                        alphaValue = 255;
                    }

                    msgCount++;
                    if (alphaValue > 2) {
                        byte xOffset = 3;
                        int yOffset = -msg * 10;
                        String s = chatline.message;
                        drawRectangle(xOffset - 3, yOffset - 1, xOffset + 320 + 4, yOffset + 9, alphaValue / 2 << 24);
                        GL11.glEnable(3042);
                        font.drawStringWithShadow(s, xOffset, yOffset, 16777215 + (alphaValue << 24));
                    }
                }
            }

            if (chatOpen) {
                GL11.glTranslatef(0.0F, font.fontHeight, 0.0F);
                int msgList = totalMessages * font.fontHeight + totalMessages;
                int messagesTotal = msgCount * font.fontHeight + msgCount;
                int visibleMessages = this.chatScrollAmount * messagesTotal / totalMessages;
                int msgOffset = messagesTotal * messagesTotal / msgList;
                if (msgList != messagesTotal) {
                    char character = (char)(visibleMessages <= 0 ? 96 : 170);
                    drawRectangle(0, -visibleMessages, 2, -visibleMessages - msgOffset, 13421772 + (character << 24));
                }
            }
        }
    }

    public void clearChatMessages() {
        this.activeChatMessages.clear();
        this.chatHistory.clear();
    }

    public void addChatMessage(String message) {
        this.splitMessageIntoSubmessages(message);
        Minecraft.logger.info(SharedConstants.deleteColorCodes(message));
    }

    public void splitMessageIntoSubmessages(String msg) {
        int maxWidth = 320;
        Font font = this.mc.fontRenderer;

        while (font.getStringWidth(msg) > maxWidth) {
            int cut = -1;
            int len = msg.length();

            for (int i = 1; i <= len && font.getStringWidth(msg.substring(0, i)) <= maxWidth; i++) {
                if (msg.charAt(i - 1) == ' ') {
                    cut = i - 1;
                }
            }

            if (cut == -1) {
                int i = 1;

                while (i < msg.length() && font.getStringWidth(msg.substring(0, i + 1)) <= maxWidth) {
                    i++;
                }

                cut = i;
            }

            String line = msg.substring(0, cut).trim();
            this.activeChatMessages.add(0, new ChatOverlay.ChatMessage(line));
            msg = msg.substring(cut).trim();
        }

        this.activeChatMessages.add(0, new ChatOverlay.ChatMessage(msg));

        while (this.activeChatMessages.size() > 100) {
            this.activeChatMessages.remove(this.activeChatMessages.size() - 1);
        }
    }

    public List<String> getChatHistory() {
        return this.chatHistory;
    }

    public void resetScroll() {
        this.chatScrollAmount = 0;
    }

    public void scrollChat(int amountScrolled) {
        this.chatScrollAmount += amountScrolled;
        int messageAmount = this.activeChatMessages.size();
        if (this.chatScrollAmount > messageAmount - 20) {
            this.chatScrollAmount = messageAmount - 20;
        }

        if (this.chatScrollAmount <= 0) {
            this.chatScrollAmount = 0;
        }
    }

    public boolean isChatOpen() {
        return this.mc.currentScreen instanceof ChatScreen;
    }

    public class ChatMessage {
        public String message;
        public int updateCounter;

        public ChatMessage(String msg) {
            this.message = msg;
            this.updateCounter = 0;
        }
    }
}
