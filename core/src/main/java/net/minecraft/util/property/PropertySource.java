package net.minecraft.util.property;

public interface PropertySource {
    void load();

    void recreate();

    void save();

    String setComments(String var1);

    <T> T get(String var1, T var2);

    <T> void set(String var1, T var2);

    class Empty implements PropertySource {
        @Override
        public void load() {
        }

        @Override
        public void recreate() {
        }

        @Override
        public void save() {
        }

        @Override
        public String setComments(String comments) {
            return comments;
        }

        @Override
        public <T> T get(String key, T defaultValue) {
            return defaultValue;
        }

        @Override
        public <T> void set(String key, T value) {
        }
    }
}
