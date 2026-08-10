package net.minecraft.client.sound;

import paulscode.sound.SoundSystemLogger;

public class SoundLogger extends SoundSystemLogger {
    @Override
    public void importantMessage(String message, int indent) {
        if (message.length() > 0) {
            SoundManager.logger.info(message);
        }
    }

    @Override
    public void message(String message, int indent) {
        if (message.length() > 0) {
            SoundManager.logger.info(message);
        }
    }

    @Override
    public void errorMessage(String classname, String message, int indent) {
        SoundManager.logger.severe("Error in class '" + classname + "'");
        SoundManager.logger.severe(message);
    }
}
