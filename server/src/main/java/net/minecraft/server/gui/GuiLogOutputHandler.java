package net.minecraft.server.gui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javax.swing.JTextArea;

public class GuiLogOutputHandler extends Handler {
    private int[] messages = new int[1024];
    private int message = 0;
    Formatter formatter = new Formatter() {
        @Override
        public String format(LogRecord logRecord1) {
            StringBuilder stringBuilder2 = new StringBuilder();
            Level level3 = logRecord1.getLevel();
            if (level3 == Level.FINEST) {
                stringBuilder2.append("[FINEST] ");
            } else if (level3 == Level.FINER) {
                stringBuilder2.append("[FINER] ");
            } else if (level3 == Level.FINE) {
                stringBuilder2.append("[FINE] ");
            } else if (level3 == Level.INFO) {
                stringBuilder2.append("[INFO] ");
            } else if (level3 == Level.WARNING) {
                stringBuilder2.append("[WARNING] ");
            } else if (level3 == Level.SEVERE) {
                stringBuilder2.append("[SEVERE] ");
            } else if (level3 == Level.SEVERE) {
                stringBuilder2.append("[" + level3.getLocalizedName() + "] ");
            }

            stringBuilder2.append(logRecord1.getMessage());
            stringBuilder2.append('\n');
            Throwable throwable4 = logRecord1.getThrown();
            if (throwable4 != null) {
                StringWriter stringWriter5 = new StringWriter();
                throwable4.printStackTrace(new PrintWriter(stringWriter5));
                stringBuilder2.append(stringWriter5.toString());
            }

            return stringBuilder2.toString();
        }
    };
    private JTextArea textArea;

    public GuiLogOutputHandler(JTextArea jTextArea1) {
        this.setFormatter(this.formatter);
        this.textArea = jTextArea1;
    }

    @Override
    public void close() {
    }

    @Override
    public void flush() {
    }

    @Override
    public void publish(LogRecord logRecord1) {
        int i2 = this.textArea.getDocument().getLength();
        this.textArea.append(this.formatter.format(logRecord1));
        this.textArea.setCaretPosition(this.textArea.getDocument().getLength());
        int i3 = this.textArea.getDocument().getLength() - i2;
        if (this.messages[this.message] != 0) {
            this.textArea.replaceRange("", 0, this.messages[this.message]);
        }

        this.messages[this.message] = i3;
        this.message = (this.message + 1) % 1024;
    }
}
