package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import net.minecraft.game.entity.DataSyncer;
import net.minecraft.network.NetworkHandler;

public class EntityMetadataPacket extends Packet {
    public int entityId;
    private List<DataSyncer.DataItem> objects;

    public EntityMetadataPacket() {
    }

    public EntityMetadataPacket(int i1, DataSyncer dataWatcher2) {
        this.entityId = i1;
        this.objects = dataWatcher2.getChangedObjects();
    }

    @Override
    public void readPacketData(DataInputStream dataInputStream1) throws IOException {
        this.entityId = dataInputStream1.readInt();
        this.objects = DataSyncer.readObjects(dataInputStream1);
    }

    @Override
    public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.writeInt(this.entityId);
        DataSyncer.writeObjects(this.objects, dataOutputStream1);
    }

    @Override
    public void processPacket(NetworkHandler netHandler1) {
        netHandler1.handleEntityMetadata(this);
    }

    @Override
    public int getPacketSize() {
        return 5;
    }

    public List<DataSyncer.DataItem> getObjects() {
        return this.objects;
    }
}
