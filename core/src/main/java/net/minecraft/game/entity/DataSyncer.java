package net.minecraft.game.entity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.util.Position;
import net.minecraft.util.io.IOUtil;

public class DataSyncer {
    private static final HashMap<Class<?>, Integer> dataTypes = new HashMap<>();
    private final Map<Integer, DataSyncer.DataItem> watchedObjects = new HashMap<>();
    private boolean objectChanged;
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public void addObject(int id, Object object) {
        int val = dataTypes.get(object.getClass());
        if (val > 6 || val < 0) {
            throw new IllegalArgumentException("Unknown data type: " + object.getClass());
        }

        if (id > 31) {
            throw new IllegalArgumentException("Data value id is too big with " + id + "! (Max is " + 31 + ")");
        }

        if (this.watchedObjects.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate id value for " + id + "!");
        }

        DataSyncer.DataItem watchedObj = new DataSyncer.DataItem(val, id, object);
        this.watchedObjects.put(id, watchedObj);
    }

    private DataSyncer.DataItem getWatchedObject(int id) {
        this.lock.readLock().lock();
        DataSyncer.DataItem obj = this.watchedObjects.get(id);
        this.lock.readLock().unlock();
        return obj;
    }

    public byte getObjectByte(int index) {
        return (Byte)this.getWatchedObject(index).getObject();
    }

    public int getObjectInt(int index) {
        DataSyncer.DataItem object = this.getWatchedObject(index);
        if (object == null && index == 8) {
            return 0;
        }

        Object obj = object.getObject();
        return (Integer)obj;
    }

    public String getObjectString(int index) {
        return (String)this.getWatchedObject(index).getObject();
    }

    public ItemStack getObjectStack(int index) {
        return (ItemStack)this.getWatchedObject(index).getObject();
    }

    public void setState(int index) {
        this.getWatchedObject(index).isWatching = true;
        this.objectChanged = true;
    }

    public void updateObject(int index, Object obj) {
        DataSyncer.DataItem watchedObject = this.getWatchedObject(index);
        if (obj == null) {
            if (watchedObject != null) {
                watchedObject.setObject(null);
                watchedObject.setWatching(true);
                this.objectChanged = true;
            }
        } else {
            if (watchedObject == null) {
                this.addObject(index, obj);
            } else if (!obj.equals(watchedObject.getObject())) {
                watchedObject.setObject(obj);
                watchedObject.setWatching(true);
                this.objectChanged = true;
            }
        }
    }

    public static void writeObjects(List<DataSyncer.DataItem> list, DataOutputStream dataStream) throws IOException {
        if (list != null) {
            for (DataSyncer.DataItem obj : list) {
                writeObject(dataStream, obj);
            }
        }

        dataStream.writeByte(127);
    }

    public ArrayList<DataSyncer.DataItem> getChangedObjects() {
        ArrayList<DataSyncer.DataItem> objList = null;
        if (this.objectChanged) {
            for (DataSyncer.DataItem watchableObject3 : this.watchedObjects.values()) {
                if (watchableObject3.getWatching()) {
                    watchableObject3.setWatching(false);
                    if (objList == null) {
                        objList = new ArrayList<>();
                    }

                    objList.add(watchableObject3);
                }
            }
        }

        this.objectChanged = false;
        return objList;
    }

    public void writeWatchableObjects(DataOutputStream dataStream) throws IOException {
        for (DataSyncer.DataItem obj : this.watchedObjects.values()) {
            writeObject(dataStream, obj);
        }

        dataStream.writeByte(127);
    }

    private static void writeObject(DataOutputStream dataStream, DataSyncer.DataItem obj) throws IOException {
        int val = (obj.getType() << 5 | obj.getId() & 31) & 0xFF;
        dataStream.writeByte(val);
        switch (obj.getType()) {
            case 0:
                dataStream.writeByte((Byte)obj.getObject());
                break;
            case 1:
                dataStream.writeShort((Short)obj.getObject());
                break;
            case 2:
                dataStream.writeInt((Integer)obj.getObject());
                break;
            case 3:
                dataStream.writeFloat((Float)obj.getObject());
                break;
            case 4:
                IOUtil.writeUtf16String((String)obj.getObject(), dataStream);
                break;
            case 5:
                IOUtil.writeItemStack((ItemStack)obj.getObject(), dataStream);
                break;
            case 6:
                Position pos = (Position)obj.getObject();
                dataStream.writeInt(pos.x);
                dataStream.writeInt(pos.y);
                dataStream.writeInt(pos.z);
        }
    }

    public static List<DataSyncer.DataItem> readObjects(DataInputStream dataStream) throws IOException {
        ArrayList<DataSyncer.DataItem> objList = null;

        for (byte data = dataStream.readByte(); data != 127; data = dataStream.readByte()) {
            if (objList == null) {
                objList = new ArrayList<>();
            }

            int valHigher = (data & 224) >> 5;
            int valLower = data & 31;
            DataSyncer.DataItem watchedObj = null;
            switch (valHigher) {
                case 0:
                    watchedObj = new DataSyncer.DataItem(valHigher, valLower, dataStream.readByte());
                    break;
                case 1:
                    watchedObj = new DataSyncer.DataItem(valHigher, valLower, dataStream.readShort());
                    break;
                case 2:
                    watchedObj = new DataSyncer.DataItem(valHigher, valLower, dataStream.readInt());
                    break;
                case 3:
                    watchedObj = new DataSyncer.DataItem(valHigher, valLower, dataStream.readFloat());
                    break;
                case 4:
                    watchedObj = new DataSyncer.DataItem(valHigher, valLower, IOUtil.readUtf16String(dataStream));
                    break;
                case 5:
                    ItemStack stack = IOUtil.readItemStack(dataStream);
                    watchedObj = new DataSyncer.DataItem(valHigher, valLower, stack);
                    break;
                case 6:
                    int x = dataStream.readInt();
                    int y = dataStream.readInt();
                    int z = dataStream.readInt();
                    watchedObj = new DataSyncer.DataItem(valHigher, valLower, new Position(x, y, z));
            }

            objList.add(watchedObj);
        }

        return objList;
    }

    public void updateObjects(List<DataSyncer.DataItem> objList) {
        for (DataSyncer.DataItem obj : objList) {
            DataSyncer.DataItem watchedObj = this.getWatchedObject(obj.getId());
            if (watchedObj != null) {
                watchedObj.setObject(obj.getObject());
            }
        }
    }

    public boolean hasObjectChanged() {
        return this.objectChanged;
    }

    public void addObjectData(int upper, int lower) {
        DataSyncer.DataItem obj = new DataSyncer.DataItem(lower, upper, null);
        this.lock.writeLock().lock();
        this.watchedObjects.put(upper, obj);
        this.lock.writeLock().unlock();
    }

    static {
        dataTypes.put(Byte.class, 0);
        dataTypes.put(Short.class, 1);
        dataTypes.put(Integer.class, 2);
        dataTypes.put(Float.class, 3);
        dataTypes.put(String.class, 4);
        dataTypes.put(ItemStack.class, 5);
        dataTypes.put(Position.class, 6);
    }

    public static class DataItem {
        private final int type;
        private final int id;
        private Object watchedObject;
        private boolean isWatching;

        public DataItem(int typ, int id, Object object) {
            this.id = id;
            this.watchedObject = object;
            this.type = typ;
            this.isWatching = true;
        }

        public int getId() {
            return this.id;
        }

        public void setObject(Object object) {
            this.watchedObject = object;
        }

        public Object getObject() {
            return this.watchedObject;
        }

        public int getType() {
            return this.type;
        }

        public boolean getWatching() {
            return this.isWatching;
        }

        public void setWatching(boolean state) {
            this.isWatching = state;
        }
    }
}
