package net.minecraft.util.logging;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

public class Logging {
    public static final String currentFileDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

    public static void init(Logger logger, File workDir, final boolean client) {
        Formatter formatter = new Formatter() {
            private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            @Override
            public String format(LogRecord record) {
                StringBuilder sb = new StringBuilder();
                sb.append(this.format.format(record.getMillis()));
                Level level = record.getLevel();
                if (level == Level.FINEST) {
                    sb.append(" [FINEST] ");
                } else if (level == Level.FINER) {
                    sb.append(" [FINER] ");
                } else if (level == Level.FINE) {
                    sb.append(" [FINE] ");
                } else if (level == Level.INFO) {
                    sb.append(" [INFO] ");
                } else if (level == Level.WARNING) {
                    sb.append(" [WARNING] ");
                } else if (level == Level.SEVERE) {
                    sb.append(" [SEVERE] ");
                }

                if (client) {
                    sb.append("[CLIENT] ");
                }

                sb.append(record.getMessage());
                sb.append('\n');
                Throwable throwable = record.getThrown();
                if (throwable != null) {
                    StringWriter sw = new StringWriter();
                    throwable.printStackTrace(new PrintWriter(sw));
                    sb.append(sw.toString());
                }

                return sb.toString();
            }
        };
        logger.setUseParentHandlers(false);
        StreamHandler shit = new StreamHandler(System.out, formatter) {
            @Override
            public synchronized void publish(LogRecord record) {
                super.publish(record);
                super.flush();
            }
        };
        logger.addHandler(shit);
        String form = currentFileDate;
        String filename = "server_" + form + ".log";
        if (client) {
            filename = "client_" + form + ".log";
        }

        try {
            File logDir = new File(workDir, "logs");
            if (!logDir.exists() && !logDir.mkdirs()) {
                throw new IOException("Failed to create logs directory");
            }

            File logfile = new File(logDir, filename);
            FileHandler fh = new FileHandler(logfile.getPath(), true);
            fh.setFormatter(formatter);
            logger.addHandler(fh);
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Failed to log to " + filename, ex);
        }
    }
}
