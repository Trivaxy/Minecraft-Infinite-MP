package net.minecraft.client.gui.screens.multiplayer;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.ClientUtils;
import net.minecraft.client.gui.button.Button;
import net.minecraft.client.gui.button.TextField;
import net.minecraft.client.gui.overlay.ChatOverlay;
import net.minecraft.client.gui.screens.DialogueScreen;
import net.minecraft.client.gui.screens.GuiScreen;
import net.minecraft.client.localization.Font;
import net.minecraft.client.network.ClientHandler;
import net.minecraft.client.network.player.NetworkPlayer;
import net.minecraft.commands.Command;
import net.minecraft.commands.CommandsManager;
import net.minecraft.localization.Translator;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class ChatScreen extends GuiScreen {
    private final List<ClientHandler.PlayerInfo> onlinePlayers;
    private List<String> currentSuggestions = new ArrayList<>();
    private String chatText;
    private String lastCommandSuggestion = null;
    private URI clickedURI;
    protected TextField inputField;
    private int chatHistory;
    private int playerCursor;
    private int activeSuggestion;
    private boolean hasFoundUsername;

    public ChatScreen() {
        this.chatText = "";
        this.chatHistory = -1;
        this.hasFoundUsername = false;
        this.onlinePlayers = new ArrayList<>();
        this.clickedURI = null;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.chatHistory = this.mc.HUD.chatOverlay.getChatHistory().size();
        this.inputField = new TextField(this.fontRenderer, 4, this.height - 12, this.width - 4, 12);
        this.inputField.setMaxStringLength(100);
        this.inputField.setRenderBackground(false);
        this.inputField.setFocused(true);
        this.currentSuggestions.clear();
    }

    @Override
    public void onClose() {
        Keyboard.enableRepeatEvents(false);
        this.mc.HUD.chatOverlay.resetScroll();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void update() {
        this.inputField.updateCursor();
    }

    @Override
    protected void keyTyped(char character, int key) {
        if (key == 15) {
            String msg = this.inputField.getText();
            if (msg.startsWith("/")) {
                this.autofillCommand(msg);
            } else {
                this.autofillPlayerName();
            }
        } else {
            this.hasFoundUsername = false;
        }

        if (key == 57 || key == 14) {
            this.currentSuggestions.clear();
        }

        if (key == 1) {
            this.mc.setScreen(null);
        } else if (key == 28) {
            String message = this.inputField.getText().trim();
            if (!message.isEmpty() && !this.mc.lineIsCommand(message)) {
                this.mc.player.sendChatMessage(message);
            }

            this.mc.setScreen(null);
        } else if (key == 200) {
            this.getMessageFromHistory(-1);
        } else if (key == 208) {
            this.getMessageFromHistory(1);
        } else if (key == 201) {
            this.mc.HUD.chatOverlay.scrollChat(19);
        } else if (key == 209) {
            this.mc.HUD.chatOverlay.scrollChat(-19);
        } else {
            this.inputField.textboxKeyTyped(character, key);
        }
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0) {
            if (wheel > 1) {
                wheel = 1;
            }

            if (wheel < -1) {
                wheel = -1;
            }

            if (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54)) {
                wheel *= 7;
            }

            this.mc.HUD.chatOverlay.scrollChat(wheel);
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int click) {
        if (click == 0) {
            ChatScreen.ChatClickData chatclickdata = this.openLink(Mouse.getX(), Mouse.getY());
            if (chatclickdata != null) {
                URI uri = chatclickdata.createURIFromChat();
                if (uri != null) {
                    this.clickedURI = uri;
                    ChatScreen.OpenLinkDialogue openLink = new ChatScreen.OpenLinkDialogue(this, chatclickdata.getFoundURL(), 0);
                    openLink.setClipboard();
                    this.mc.setScreen(openLink);
                    return;
                }
            }
        }

        this.inputField.mouseClicked(x, y, click);
        super.mouseClicked(x, y, click);
    }

    @Override
    public void confirmClicked(boolean confirm, int click) {
        if (click == 0) {
            if (confirm) {
                ClientUtils.openURI(this.clickedURI);
            }

            this.clickedURI = null;
            this.mc.setScreen(this);
        }
    }

    private void autofillPlayerName() {
        if (this.mc.isMultiplayerWorld()) {
            if (this.hasFoundUsername) {
                this.inputField
                    .deleteFromCursor(
                        this.inputField.addOrRemoveCharacter(-1, this.inputField.getCursorPosition(), false) - this.inputField.getCursorPosition()
                    );
                if (this.playerCursor >= this.onlinePlayers.size()) {
                    this.playerCursor = 0;
                }
            } else {
                int index = this.inputField.addOrRemoveCharacter(-1, this.inputField.getCursorPosition(), false);
                if (this.inputField.getCursorPosition() - index < 1) {
                    return;
                }

                this.onlinePlayers.clear();
                String textSubstringAtCursor = this.inputField.getText().substring(index);
                String lowerCaseTextSubstringAtCursor = textSubstringAtCursor.toLowerCase();

                for (ClientHandler.PlayerInfo player : ((NetworkPlayer)this.mc.player).connection.playerNames) {
                    if (player.nameStartsWith(lowerCaseTextSubstringAtCursor)) {
                        this.onlinePlayers.add(player);
                    }
                }

                if (this.onlinePlayers.isEmpty()) {
                    return;
                }

                this.hasFoundUsername = true;
                this.playerCursor = 0;
                this.inputField.deleteFromCursor(index - this.inputField.getCursorPosition());
            }

            if (this.onlinePlayers.size() > 1) {
                StringBuilder stringbuilder = new StringBuilder();

                for (ClientHandler.PlayerInfo player : this.onlinePlayers) {
                    if (stringbuilder.length() > 0) {
                        stringbuilder.append(", ");
                    }

                    stringbuilder.append(player.name);
                }

                this.mc.HUD.chatOverlay.addChatMessage(stringbuilder.toString());
            }

            this.inputField.writeText(this.onlinePlayers.get(this.playerCursor++).name);
        }
    }

    private void autofillCommand(String msg) {
        if (msg != null) {
            int firstSpace = msg.indexOf(32);
            String firstToken = firstSpace == -1 ? msg : msg.substring(0, firstSpace);
            String command = firstToken.replace("/", "");
            int spaces = firstSpace == -1 ? 0 : msg.length() - msg.replace(" ", "").length();
            if (spaces == 0) {
                this.autocompleteCommandName(msg);
            } else {
                int argIndex = spaces - 1;
                int lastSpace = msg.lastIndexOf(32);
                String prefix;
                if (lastSpace == -1) {
                    prefix = "";
                } else if (msg.endsWith(" ")) {
                    prefix = "";
                } else {
                    prefix = msg.substring(lastSpace + 1);
                }

                for (Command cmd : CommandsManager.COMMANDS) {
                    if (cmd != null && command.equalsIgnoreCase(cmd.name())) {
                        List<String[]> suggestions = cmd.suggestions();
                        if (suggestions == null) {
                            return;
                        }

                        if (argIndex >= 0 && argIndex < suggestions.size()) {
                            String[] rawSug = suggestions.get(argIndex);
                            if (rawSug != null && rawSug.length != 0) {
                                if (prefix.equalsIgnoreCase(msg.substring(lastSpace + 1))) {
                                    prefix = "";
                                }

                                List<String> matches = new ArrayList<>();
                                String lowerPrefix = prefix.toLowerCase();

                                for (String s : rawSug) {
                                    if (s != null && (lowerPrefix.isEmpty() || s.toLowerCase().startsWith(lowerPrefix))) {
                                        matches.add(s);
                                    }
                                }

                                if (matches.isEmpty()) {
                                    Collections.addAll(matches, rawSug);
                                }

                                this.currentSuggestions = matches;
                                this.activeSuggestion = (this.activeSuggestion + 1) % matches.size();
                                String pick = matches.get(this.activeSuggestion);
                                String base;
                                if (lastSpace == -1) {
                                    base = "";
                                } else {
                                    base = msg.substring(0, lastSpace + 1);
                                }

                                this.inputField.setText(base + pick);
                                return;
                            }

                            return;
                        }

                        return;
                    }
                }
            }
        }
    }

    private void autocompleteCommandName(String msg) {
        String prefix = msg.startsWith("/") ? msg.substring(1) : msg;
        if (this.lastCommandSuggestion != null && prefix.equalsIgnoreCase(this.lastCommandSuggestion)) {
            prefix = "";
        }

        List<String> matches = new ArrayList<>();
        String lowerPrefix = prefix.toLowerCase();

        for (Command cmd : CommandsManager.COMMANDS) {
            if (cmd != null && this.mc.player.canRunCommand(cmd)) {
                String name = cmd.name();
                if (prefix.isEmpty() || name.toLowerCase().startsWith(lowerPrefix)) {
                    matches.add(name);
                }
            }
        }

        if (!matches.isEmpty()) {
            this.currentSuggestions = matches;
            this.activeSuggestion = (this.activeSuggestion + 1) % matches.size();
            String pick = matches.get(this.activeSuggestion);
            this.lastCommandSuggestion = pick;
            this.inputField.setText("/" + pick);
        }
    }

    private void getMessageFromHistory(int scrollAmount) {
        int currMessage = this.chatHistory + scrollAmount;
        int chatListSize = this.mc.HUD.chatOverlay.getChatHistory().size();
        currMessage = MathHelper.clamp(currMessage, 0, chatListSize);
        if (currMessage != this.chatHistory) {
            if (currMessage == chatListSize) {
                this.chatHistory = chatListSize;
                this.inputField.setText(this.chatText);
            } else {
                if (this.chatHistory == chatListSize) {
                    this.chatText = this.inputField.getText();
                }

                this.inputField.setText(this.mc.HUD.chatOverlay.getChatHistory().get(currMessage));
                this.chatHistory = currMessage;
            }
        }
    }

    private void drawSuggestions() {
        int cursorPos = this.inputField.getCursorPosition();
        String lastWord = this.inputField.getText().substring(0, cursorPos);
        int cursorX = this.inputField.x + 4 + this.fontRenderer.getStringWidth(lastWord);
        int lastSpace = lastWord.lastIndexOf(32);
        String currArg = lastSpace == -1 ? lastWord : lastWord.substring(lastSpace + 1);
        int offset = this.fontRenderer.getStringWidth(currArg) + 4;
        int previewX = cursorX - offset;
        int previewY = this.height - 26;
        int maxShown = Math.min(3, this.currentSuggestions.size());

        for (int i = 0; i < maxShown; i++) {
            int index = (this.activeSuggestion + i) % this.currentSuggestions.size();
            String text = this.currentSuggestions.get(index);
            int color = i == 0 ? 16777215 : 11184810;
            this.fontRenderer.drawStringWithBackground(text, previewX, previewY - i * 11, color);
        }
    }

    @Override
    public void drawScreen(int x, int y, float deltaTick) {
        drawRectangle(2, this.height - 14, this.width - 2, this.height - 2, Integer.MIN_VALUE);
        this.inputField.drawTextBox();
        this.drawSuggestions();
        super.drawScreen(x, y, deltaTick);
    }

    public ChatScreen.ChatClickData openLink(int x, int y) {
        if (!this.mc.HUD.chatOverlay.isChatOpen()) {
            return null;
        }

        y = y / this.mc.rescaler.scaleFactor - 40;
        x = x / this.mc.rescaler.scaleFactor - 3;
        if (x >= 0 && y >= 0) {
            int offset = Math.min(20, this.mc.HUD.chatOverlay.activeChatMessages.size());
            if (x <= 320 && y < this.mc.fontRenderer.fontHeight * offset + offset) {
                int msg = y / (this.mc.fontRenderer.fontHeight + 1) + this.mc.HUD.chatOverlay.chatScrollAmount;
                return new ChatScreen.ChatClickData(this.mc.fontRenderer, this.mc.HUD.chatOverlay.activeChatMessages.get(msg), x);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static class ChatClickData {
        public static final Pattern urlRegex = Pattern.compile("^(?:(https?)://)?([-\\w_\\.]{2,}\\.[a-z]{2,3})(/\\S*)?$");
        private final ChatOverlay.ChatMessage message;
        private final String trimmedString;
        private final String foundURL;

        public ChatClickData(Font fontRenderer, ChatOverlay.ChatMessage chatLine, int strWidth) {
            this.message = chatLine;
            this.trimmedString = fontRenderer.trimStringToWidth(chatLine.message, strWidth, false);
            this.foundURL = this.findURLInMessage();
        }

        public String getFoundURL() {
            return this.foundURL;
        }

        public URI createURIFromChat() {
            String theURL = this.getFoundURL();
            if (theURL == null) {
                return null;
            }

            Matcher matcher = urlRegex.matcher(theURL);
            if (matcher.matches()) {
                try {
                    String string = matcher.group(0);
                    if (matcher.group(1) == null) {
                        string = "http://" + string;
                    }

                    return new URI(string);
                } catch (URISyntaxException urisyntaxexception) {
                    Logger.getLogger("Minecraft").log(Level.SEVERE, "Couldn't create URI from chat", urisyntaxexception);
                }
            }

            return null;
        }

        private String findURLInMessage() {
            int stringEnd = this.trimmedString.lastIndexOf(" ") + 1;
            if (stringEnd < 0) {
                stringEnd = 0;
            }

            int stringLength = this.message.message.indexOf(" ", stringEnd);
            if (stringLength < 0) {
                stringLength = this.message.message.length();
            }

            return Font.removeFormattingCodesFromString(this.message.message.substring(stringEnd, stringLength));
        }
    }

    class OpenLinkDialogue extends DialogueScreen {
        private String copy;
        private String URL;

        public OpenLinkDialogue(GuiScreen screenTitle, String url, int slot) {
            super(
                screenTitle,
                Translator.getInstance().translateKey("chat.link.confirm"),
                url,
                Translator.getInstance().translateKey("gui.yes"),
                Translator.getInstance().translateKey("gui.no"),
                slot
            );
            Translator stringtranslate = Translator.getInstance();
            this.copy = stringtranslate.translateKey("chat.copy");
            this.URL = url;
        }

        @Override
        public void initGui() {
            this.buttons.add(new Button(0, this.width / 3 - 83 + 0, this.height / 6 + 96, 100, 20, this.confirm));
            this.buttons.add(new Button(2, this.width / 3 - 83 + 105, this.height / 6 + 96, 100, 20, this.copy));
            this.buttons.add(new Button(1, this.width / 3 - 83 + 210, this.height / 6 + 96, 100, 20, this.deny));
        }

        @Override
        protected void buttonClicked(Button button) {
            if (button.id == 2) {
                this.setClipboard();
                super.buttonClicked(this.buttons.get(1));
            } else {
                super.buttonClicked(button);
            }
        }

        @Override
        public void drawScreen(int x, int y, float click) {
            super.drawScreen(x, y, click);
            this.drawCenteredString(this.fontRenderer, Translator.getInstance().translateKey("chat.link.warning"), this.width / 2, 110, 16764108);
        }

        public void setClipboard() {
            setClipboardString(this.URL);
        }
    }
}
