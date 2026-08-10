package net.minecraft.util.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingUtils {
    private LoggingUtils() {
    }

    public static void infoThrowable(Logger logger, String message, Throwable e) {
        logger.log(Level.INFO, message, e);
    }

    public static void warnThrowable(Logger logger, String message, Throwable e) {
        logger.log(Level.WARNING, message, e);
    }

    public static void severeThrowable(Logger logger, String message, Throwable e) {
        logger.log(Level.SEVERE, message, e);
    }

    public static void infof(Logger logger, Throwable e, String format, Object... args) {
        logger.log(Level.INFO, String.format(format, args), e);
    }

    public static void warnf(Logger logger, Throwable e, String format, Object... args) {
        logger.log(Level.WARNING, String.format(format, args), e);
    }

    public static void severef(Logger logger, Throwable e, String format, Object... args) {
        logger.log(Level.SEVERE, String.format(format, args), e);
    }

    public static void infof(Logger logger, String format, Object... args) {
        logger.log(Level.INFO, String.format(format, args));
    }

    public static void warnf(Logger logger, String format, Object... args) {
        logger.log(Level.WARNING, String.format(format, args));
    }

    public static void severef(Logger logger, String format, Object... args) {
        logger.log(Level.SEVERE, String.format(format, args));
    }
}
