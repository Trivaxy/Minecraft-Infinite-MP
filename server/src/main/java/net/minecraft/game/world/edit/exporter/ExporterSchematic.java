package net.minecraft.game.world.edit.exporter;

import com.mojang.nbt.tag.Tag;
import com.mojang.nbt.tag.special.CompoundTag;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import net.minecraft.game.physics.VectorBasic;
import net.minecraft.localization.Translator;

public class ExporterSchematic extends ExporterBase {
    @Override
    public String getName() {
        return "schematic";
    }

    @Override
    public boolean export(int[][][] buffer, VectorBasic dims, String fileName, String arg) {
        DataOutputStream out = null;

        try {
            out = new DataOutputStream(new FileOutputStream(fileName));
            CompoundTag schematic = new CompoundTag();
            CompoundTag entities = new CompoundTag();
            CompoundTag tileEntities = new CompoundTag();
            schematic.setCompoundTag("Entities", entities);
            schematic.setCompoundTag("TileEntities", tileEntities);
            int w = dims.x;
            int h = dims.y;
            int l = dims.z;
            schematic.setShort("Width", (short)w);
            schematic.setShort("Height", (short)h);
            schematic.setShort("Length", (short)l);
            schematic.setString("Materials", "Alpha");
            schematic.setString("Client", "Infdev+");
            byte[] data = new byte[w * h * l];
            byte[] meta = new byte[w * h * l];
            byte[] addBlocks = new byte[w * h * l / 2];
            int idx = 0;

            for (int y = 0; y < h; y++) {
                for (int z = 0; z < l; z++) {
                    for (int x = 0; x < w; x++) {
                        int encoded = buffer[x][z][y];
                        int blockID = encoded & 65535;
                        int metadata = encoded >> 16;
                        data[idx] = (byte)(blockID & 0xFF);
                        meta[idx] = (byte)(metadata & 0xFF);
                        if ((idx & 1) == 0) {
                            addBlocks[idx >> 1] = (byte)((blockID & 3840) >> 4);
                        } else {
                            addBlocks[idx >> 1] = (byte)(addBlocks[idx >> 1] | (byte)((blockID & 3840) >> 8));
                        }

                        idx++;
                    }
                }
            }

            schematic.setByteArray("Blocks", data);
            schematic.setByteArray("Data", meta);
            schematic.setByteArray("AddBlocks", addBlocks);
            Tag.writeTag(schematic, out);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    @Override
    public String getHelp() {
        return Translator.getInstance().translateKey("commands.export.schematic");
    }
}
