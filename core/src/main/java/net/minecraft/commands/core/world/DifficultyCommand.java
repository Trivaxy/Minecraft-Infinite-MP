package net.minecraft.commands.core.world;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.game.world.World;
import net.minecraft.localization.Translator;

public class DifficultyCommand extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        World world = ca.getWorld(ca.demandPlayer(src).dimension);
        if (args.length == 0) {
            src.print(
                Translator.getInstance().translateKey("commands.difficulty.current")
                    + ": "
                    + DifficultyCommand.Difficulty.fromId(world.getSettings().getDifficulty()).toString()
            );
        } else {
            if (args.length == 1) {
                try {
                    world.getSettings().setDifficulty((byte)DifficultyCommand.Difficulty.fromName(args[0].toUpperCase()).id);
                    src.print(
                        Translator.getInstance().translateKey("commands.difficulty.set")
                            + ": "
                            + DifficultyCommand.Difficulty.fromId(world.getSettings().getDifficulty()).toString()
                    );
                } catch (Exception ex) {
                    byte i = (byte)parseInt(args[0]);
                    world.getSettings().setDifficulty(i);
                    src.print(Translator.getInstance().translateKey("commands.difficulty.set") + ": " + DifficultyCommand.Difficulty.fromId(i).toString());
                }
            } else {
                Translator.getInstance().translateKey("commands.difficulty.invalid");
            }
        }
    }

    @Override
    public String name() {
        return "difficulty";
    }

    @Override
    public Command.UserType requiredUserType() {
        return Command.UserType.OPERATOR;
    }

    @Override
    public Command.EnvType envType() {
        return Command.EnvType.BOTH;
    }

    @Override
    public String help() {
        return "<difficulty> " + Translator.getInstance().translateKey("commands.difficulty");
    }

    @Override
    public List<String[]> suggestions() {
        List<String[]> list = new ArrayList<>();
        list.add(new String[]{"peaceful", "easy", "normal", "hard"});
        return list;
    }

    public enum Difficulty {
        PEACEFUL(0),
        EASY(1),
        NORMAL(2),
        HARD(3);

        public final int id;

        Difficulty(int id) {
            this.id = id;
        }

        public static DifficultyCommand.Difficulty fromId(int id) {
            for (DifficultyCommand.Difficulty difficulty : values()) {
                if (difficulty.id == id) {
                    return difficulty;
                }
            }

            return null;
        }

        public static DifficultyCommand.Difficulty fromName(String name) {
            for (DifficultyCommand.Difficulty difficulty : values()) {
                if (difficulty.name().equalsIgnoreCase(name)) {
                    return difficulty;
                }
            }

            return null;
        }
    }
}
