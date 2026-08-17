package net.minecraft.commands;

public interface CommandOutput {
    CommandOutput NONE = new CommandOutput() {
        @Override
        public void sendCommandMessage(String message) {
        }
    };

    void sendCommandMessage(String message);
}
