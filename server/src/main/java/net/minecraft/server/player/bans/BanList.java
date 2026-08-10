package net.minecraft.server.player.bans;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BanList {
    private final BanList.LowerStringMap<BanEntry> theBanList = new BanList.LowerStringMap<>();
    private final File fileName;
    private boolean listActive = true;

    public BanList(File par1File) {
        this.fileName = par1File;
    }

    public boolean isListActive() {
        return this.listActive;
    }

    public void setListActive(boolean par1) {
        this.listActive = par1;
    }

    public Map<String, BanEntry> getBannedList() {
        this.removeExpiredBans();
        return this.theBanList;
    }

    public boolean isBanned(String par1Str) {
        if (!this.isListActive()) {
            return false;
        }

        this.removeExpiredBans();
        return this.theBanList.containsKey(par1Str);
    }

    public void put(BanEntry par1BanEntry) {
        this.theBanList.put(par1BanEntry.getBannedUsername(), par1BanEntry);
        this.saveToFileWithHeader();
    }

    public void remove(String par1Str) {
        this.theBanList.remove(par1Str);
        this.saveToFileWithHeader();
    }

    public void removeExpiredBans() {
        this.theBanList.values().removeIf(BanEntry::hasBanExpired);
    }

    public void loadBanList() {
        if (this.fileName.isFile()) {
            BufferedReader var1;
            try {
                var1 = new BufferedReader(new FileReader(this.fileName));
            } catch (FileNotFoundException var4) {
                throw new Error();
            }

            String var2;
            try {
                while ((var2 = var1.readLine()) != null) {
                    if (!var2.startsWith("#")) {
                        BanEntry var3 = BanEntry.parse(var2);
                        if (var3 != null) {
                            this.theBanList.put(var3.getBannedUsername(), var3);
                        }
                    }
                }
            } catch (IOException var5) {
                Logger.getLogger("Minecraft").log(Level.SEVERE, "Could not load ban list", var5);
            }
        }
    }

    public void saveToFileWithHeader() {
        this.saveToFile(true);
    }

    public void saveToFile(boolean par1) {
        this.removeExpiredBans();

        try {
            PrintWriter var2 = new PrintWriter(new FileWriter(this.fileName, false));
            if (par1) {
                var2.println("# Updated " + new SimpleDateFormat().format(new Date()) + " by " + "Minecraft Infinite v1.0");
                var2.println("# victim name | ban date | banned by | banned until | reason");
                var2.println();
            }

            for (BanEntry e : this.theBanList.values()) {
                var2.println(e.buildBanString());
            }

            var2.close();
        } catch (IOException var5) {
            Logger.getLogger("Minecraft").log(Level.SEVERE, "Could not save ban list", var5);
        }
    }

    public class LowerStringMap<V> implements Map<String, V> {
        private final Map<String, V> internalMap = new LinkedHashMap<>();

        @Override
        public int size() {
            return this.internalMap.size();
        }

        @Override
        public boolean isEmpty() {
            return this.internalMap.isEmpty();
        }

        @Override
        public boolean containsKey(Object par1Obj) {
            return this.internalMap.containsKey(par1Obj.toString().toLowerCase());
        }

        @Override
        public boolean containsValue(Object par1Obj) {
            return this.internalMap.containsValue(par1Obj);
        }

        @Override
        public V get(Object par1Obj) {
            return this.internalMap.get(par1Obj.toString().toLowerCase());
        }

        public V put(String par1Str, V par2Obj) {
            return this.internalMap.put(par1Str.toLowerCase(), par2Obj);
        }

        @Override
        public V remove(Object par1Obj) {
            return this.internalMap.remove(par1Obj.toString().toLowerCase());
        }

        @Override
        public void putAll(Map<? extends String, ? extends V> par1Map) {
            for (Entry<? extends String, ? extends V> o : par1Map.entrySet()) {
                this.put(o.getKey(), (V)o.getValue());
            }
        }

        @Override
        public void clear() {
            this.internalMap.clear();
        }

        @Override
        public Set<String> keySet() {
            return this.internalMap.keySet();
        }

        @Override
        public Collection<V> values() {
            return this.internalMap.values();
        }

        @Override
        public Set<Entry<String, V>> entrySet() {
            return this.internalMap.entrySet();
        }
    }
}
