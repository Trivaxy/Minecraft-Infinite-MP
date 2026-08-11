package net.minecraft.commands.core.world;

import java.util.Arrays;
import java.util.List;
import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.World;
import net.minecraft.game.world.chunk.Chunk;
import net.minecraft.localization.Translator;

public class ChunkInfoCommand extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        if (args.length == 3) {
            int x = parseInt(args[1]);
            int z = parseInt(args[2]);
            this.info(x, z, 0, false, ca, src);
        } else {
            Player p = ca.getConfigManager().getPlayer(src.getUsername());
            if (p != null) {
                int x = (int)p.posX;
                int z = (int)p.posZ;
                this.info(x, z, p.dimension, true, ca, src);
            }
        }
    }

    void info(int x, int z, int dim, boolean blockcoords, CommandAccessor ca, CommandSource src) {
        World w = ca.getWorld(dim);
        Chunk c;
        if (blockcoords) {
            c = w.getChunkAt(x, z);
        } else {
            c = w.getChunk(x, z);
        }

        if (c != null) {
            src.printf(Translator.getInstance().translateKey("commands.chunk.pos"), c.xPosition, c.zPosition);
            src.printf(Translator.getInstance().translateKey("commands.chunk.section"), c.getSections().length);
            src.printf(Translator.getInstance().translateKey("commands.chunk.heightmap"), c.heightMap.length);
            src.printf(Translator.getInstance().translateKey("commands.chunk.percipation"), c.precipitationMap.length);
            src.printf(Translator.getInstance().translateKey("commands.chunk.light"), c.skylightColumns.size());
            src.printf(Translator.getInstance().translateKey("commands.chunk.tileentities"), c.tileEntities.size());
            src.printf(Translator.getInstance().translateKey("commands.chunk.entities"), c.entities.length);
            src.printf(Translator.getInstance().translateKey("commands.chunk.loaded"), c.isLoaded);
            src.printf(Translator.getInstance().translateKey("commands.chunk.modified"), c.isModified);
            src.printf(Translator.getInstance().translateKey("commands.chunk.rendered"), c.isRendered);
            src.printf(Translator.getInstance().translateKey("commands.chunk.decorated"), c.beingDecorated);
            src.printf(Translator.getInstance().translateKey("commands.chunk.populated"), c.isPopulated);
            src.printf(Translator.getInstance().translateKey("commands.chunk.save"), c.neverSave);
            src.printf(Translator.getInstance().translateKey("commands.chunk.savetime"), c.lastSaveTime);
        } else {
            src.printf(Translator.getInstance().translateKey("commands.chunk.none"), x, z);
        }
    }

    @Override
    public String name() {
        return "chunkinfo";
    }

    @Override
    public Command.UserType requiredUserType() {
        return Command.UserType.USER;
    }

    @Override
    public Command.EnvType envType() {
        return Command.EnvType.BOTH;
    }

    @Override
    public List<String> additionalHelp() {
        return Arrays.asList("<x> <z> " + Translator.getInstance().translateKey("commands.chunk.coords"));
    }

    @Override
    public String help() {
        return "<chunkinfo> " + Translator.getInstance().translateKey("commands.chunk.info");
    }
}
