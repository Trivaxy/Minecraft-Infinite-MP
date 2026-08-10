package net.minecraft.util.property;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class PropertiesConfig implements PropertySource {
    protected static Logger logger = Logger.getLogger("Minecraft");
    protected Properties props = new Properties();
    protected File file;
    protected String comments;
    protected int failcount;

    public PropertiesConfig(File configFile) {
        this(configFile, "Properties File");
    }

    public PropertiesConfig(File configfile, String comments) {
        this.file = configfile;
        this.comments = comments;
        this.load();
    }

    @Override
    public void load() {
        if (this.file.exists()) {
            try {
                this.props.load(new FileInputStream(this.file));
            } catch (Exception e) {
                logger.warning("Failed to create " + this.file);
                this.recreate();
            }
        }
    }

    @Override
    public void recreate() {
        this.failcount++;
        if (this.failcount == 5) {
            logger.warning("Failed to create config after 5 retries!");
        }

        logger.warning("Creating new " + this.file);
        this.save();
    }

    @Override
    public void save() {
        try {
            this.props.store(new FileOutputStream(this.file), this.comments);
        } catch (Exception e) {
            logger.warning("Failed to save " + this.file + ": " + e);
            this.recreate();
        }
    }

    @Override
    public String setComments(String comments) {
        return this.comments = comments;
    }

    @Override
    public <T> T get(String key, T defaultValue) {
        String value = this.props.getProperty(key);
        if (value == null) {
            return defaultValue;
        }

        try {
            if (defaultValue instanceof Boolean) {
                return (T)Boolean.valueOf(value);
            }

            if (defaultValue instanceof Integer) {
                return (T)Integer.valueOf(value);
            }

            if (defaultValue instanceof Float) {
                return (T)Float.valueOf(value);
            }

            if (defaultValue instanceof Double) {
                return (T)Double.valueOf(value);
            }

            if (defaultValue instanceof Long) {
                return (T)Long.valueOf(value);
            }

            if (defaultValue instanceof String) {
                return (T)value;
            }
        } catch (Exception e) {
            logger.warning("Invalid value for key '" + key + "': " + value + " , using default.");
        }

        return defaultValue;
    }

    @Override
    public <T> void set(String key, T value) {
        this.props.setProperty(key, String.valueOf(value));
        this.save();
    }
}
