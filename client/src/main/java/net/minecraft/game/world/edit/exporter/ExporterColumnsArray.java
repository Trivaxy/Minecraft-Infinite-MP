package net.minecraft.game.world.edit.exporter;

import java.io.File;
import java.io.FileWriter;
import net.minecraft.game.physics.VectorBasic;
import net.minecraft.localization.Translator;

public class ExporterColumnsArray extends ExporterBase {
    @Override
    public String getName() {
        return "columnsArray";
    }

    @Override
    public boolean export(int[][][] buffer, VectorBasic dims, String fileName, String arg) {
        boolean doRotations = "rotations".equals(arg);
        FileWriter fileWriter = null;
        int[] columnBuffer = new int[dims.y];

        try {
            File fileOut = new File(fileName);
            fileWriter = new FileWriter(fileOut);
            if (doRotations) {
                fileWriter.write("// Rotation 1 (x, z)\n");
            }

            fileWriter.write("public static final short[][][] array" + (doRotations ? "1" : "") + " = new short [][][] {\n");

            for (int x = 0; x < dims.x; x++) {
                fileWriter.write("\t{\n");

                for (int z = 0; z < dims.z; z++) {
                    for (int y = 0; y < dims.y; y++) {
                        columnBuffer[y] = buffer[x][z][y];
                    }

                    this.writeFullArrayColumn(columnBuffer, fileWriter);
                }

                fileWriter.write("\t},\n");
            }

            fileWriter.write("};\n");
            if (doRotations) {
                fileWriter.write("// Rotation 2 (z, -x)\n");
                fileWriter.write("public static final short[][][] array2 = new short [][][] {\n");

                for (int z = 0; z < dims.z; z++) {
                    fileWriter.write("\t{\n");

                    for (int x = dims.x - 1; x >= 0; x--) {
                        for (int y = 0; y < dims.y; y++) {
                            columnBuffer[y] = buffer[x][z][y];
                        }

                        this.writeFullArrayColumn(columnBuffer, fileWriter);
                    }

                    fileWriter.write("\t},\n");
                }

                fileWriter.write("};\n");
                fileWriter.write("// Rotation 3 (-x, -z)\n");
                fileWriter.write("public static final short[][][] array3 = new short [][][] {\n");

                for (int x = dims.x - 1; x >= 0; x--) {
                    fileWriter.write("\t{\n");

                    for (int z = dims.z - 1; z >= 0; z--) {
                        for (int y = 0; y < dims.y; y++) {
                            columnBuffer[y] = buffer[x][z][y];
                        }

                        this.writeFullArrayColumn(columnBuffer, fileWriter);
                    }

                    fileWriter.write("\t},\n");
                }

                fileWriter.write("};\n");
                fileWriter.write("// Rotation 4 (-z, x)\n");
                fileWriter.write("public static final short[][][] array4 = new short [][][] {\n");

                for (int z = dims.z - 1; z >= 0; z--) {
                    fileWriter.write("\t{\n");

                    for (int x = 0; x < dims.x; x++) {
                        for (int y = 0; y < dims.y; y++) {
                            columnBuffer[y] = buffer[x][z][y];
                        }

                        this.writeFullArrayColumn(columnBuffer, fileWriter);
                    }

                    fileWriter.write("\t},\n");
                }

                fileWriter.write("};\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                fileWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Critical failure!");
            }
        }

        return true;
    }

    @Override
    public String getHelp() {
        return Translator.getInstance().translateKey("commands.export.columns");
    }

    static String toId(int a) {
        return a < 4096 ? "" + a : (a & 4095) + " | (" + (a >> 16) + " << 12)";
    }

    public void writeFullArrayColumn(int[] buffer, FileWriter fileWriter) throws Exception {
        fileWriter.write("\t\t{ ");

        for (int i = 0; i < buffer.length; i++) {
            fileWriter.write(toId(buffer[i]));
            if (i < buffer.length - 1) {
                fileWriter.write(", ");
            }
        }

        fileWriter.write("}, \n");
    }
}
