package net.minecraft.smoketest;

import com.mojang.nbt.NBTReader;
import com.mojang.nbt.tag.special.CompoundTag;
import java.io.File;
import java.nio.file.Files;

/**
 * Rewrites the {@code Dimension} tag in a player's save file.
 *
 * <p>This exists to test the server's multi-world handling without any travel code: set a player to
 * dimension 1, log in, and you should arrive in Hell with Hell terrain, Hell sky and Hell mobs. It is
 * the shortest end-to-end proof that per-dimension worlds, chunk managers, entity trackers and the
 * login handshake all agree on which world a player is in.
 *
 * <p>It reads and writes through the game's own NBT classes, so it cannot drift from the save format.
 *
 * <p>The server must be stopped first — it rewrites player files on shutdown and would overwrite this.
 *
 * <pre>
 * java -cp &lt;smoketest runtime classpath&gt; net.minecraft.smoketest.SetPlayerDimension \
 *      run/server/world/players/Alice.dat 1
 * </pre>
 */
public final class SetPlayerDimension {
    private SetPlayerDimension() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("usage: SetPlayerDimension <player.dat> <dimension>");
            System.exit(2);
        }

        File file = new File(args[0]);
        int dimension = Integer.parseInt(args[1]);
        if (!file.isFile()) {
            System.err.println("no such file: " + file.getAbsolutePath());
            System.exit(1);
        }

        CompoundTag tag = NBTReader.readCompressed(Files.newInputStream(file.toPath()));
        System.out.println("before: Dimension=" + tag.getInteger("Dimension") + " Pos=" + tag.getTagList("Pos"));
        tag.setInteger("Dimension", dimension);
        NBTReader.writeCompressed(tag, Files.newOutputStream(file.toPath()));

        CompoundTag reread = NBTReader.readCompressed(Files.newInputStream(file.toPath()));
        System.out.println("after:  Dimension=" + reread.getInteger("Dimension"));
        if (reread.getInteger("Dimension") != dimension) {
            System.err.println("write did not take");
            System.exit(1);
        }
    }
}
