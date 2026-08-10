package net.minecraft.client;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.List;
import org.lwjgl.Sys;

public class ClientUtils {
    private ClientUtils() {
    }

    public static void openFile(File file) {
        try {
            Desktop.getDesktop().open(file);
        } catch (Throwable e) {
            try {
                URL u = file.toURI().toURL();
                Sys.openURL(u.toExternalForm());
            } catch (Throwable var3) {
            }
        }
    }

    public static void openURI(URI uri) {
        try {
            Desktop.getDesktop().browse(uri);
        } catch (Throwable e) {
            try {
                URL u = uri.toURL();
                Sys.openURL(u.toExternalForm());
            } catch (Throwable var3) {
            }
        }
    }

    public static <T> T getArgument(List<T> list, T key) {
        int idx = list.indexOf(key);
        return idx != -1 ? list.get(idx + 1) : null;
    }
}
