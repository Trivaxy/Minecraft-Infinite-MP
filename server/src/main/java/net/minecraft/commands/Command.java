package net.minecraft.commands;

import java.util.List;
import java.util.logging.Logger;
import net.minecraft.game.item.ItemList;
import net.minecraft.localization.Translator;

public abstract class Command {
    public static Logger logger = Logger.getLogger("Minecraft");

    public void init() {
    }

    public abstract void action(String[] var1, CommandAccessor var2, CommandSource var3);

    public void errorAction(String message, CommandSource cs) {
        cs.print(Translator.getInstance().translateKey("commands.base.error") + " " + message);
    }

    public abstract String name();

    public abstract Command.UserType requiredUserType();

    public abstract Command.EnvType envType();

    public abstract String help();

    public List<String> additionalHelp() {
        return null;
    }

    public List<String> getAliases() {
        return null;
    }

    public List<String[]> suggestions() {
        return null;
    }

    @Override
    public String toString() {
        return "Command{name=" + this.name() + "}";
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj instanceof Command) {
            Command cmd = (Command)obj;
            return cmd.name().equals(this.name());
        } else {
            return false;
        }
    }

    public static void sendNoticeToOps(CommandAccessor ca, CommandSource src, String message) {
        String msg = src.getUsername() + ": " + message;
        ConfigManager cm = ca.getConfigManager();
        boolean server = ca.isServer();
        if (cm != null) {
            cm.sendMessageToOps(message);
        } else if (server) {
            src.print("[" + msg + "]");
        }

        if (server) {
            logger.info(message);
        }
    }

    public static void demandArgCountOf(int length, String[] args) {
        if (args.length < length) {
            throw new CommandException.NotEnoughArgs(Translator.getInstance().translateKey("commands.base.error.notenoughargs"));
        }
    }

    public static String joinArgArray(String[] args, int startPos) {
        StringBuilder sb = new StringBuilder();

        for (int i = startPos; i < args.length; i++) {
            String string = args[i];
            sb.append(' ');
            sb.append(string);
        }

        sb.deleteCharAt(0);
        return sb.toString();
    }

    public static boolean parseBool(String input) {
        if (input.equalsIgnoreCase("true")) {
            return true;
        } else if (input.equalsIgnoreCase("false")) {
            return false;
        } else {
            throw new CommandException.InvalidNumber(Translator.getInstance().translateKey("commands.base.error.invalidbool") + " " + input);
        }
    }

    public static int parseInt(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException nfe) {
            throw new CommandException.InvalidNumber(Translator.getInstance().translateKey("commands.base.error.invalidnumber") + " " + input);
        }
    }

    public static double parseRelativeCoords(String input, double dir) {
        try {
            if (input.equals("~")) {
                return dir;
            } else if (input.contains("~")) {
                return dir + Double.parseDouble(input.replaceAll("~", ""));
            } else {
                double d = Double.parseDouble(input);
                if (Double.isNaN(d)) {
                    throw new CommandException.InvalidNumber(Translator.getInstance().translateKey("commands.base.error.nan"));
                } else if (Double.isInfinite(d)) {
                    throw new CommandException.InvalidNumber(Translator.getInstance().translateKey("commands.base.error.infinity"));
                } else {
                    return d;
                }
            }
        } catch (NumberFormatException nfe) {
            throw new CommandException.InvalidNumber(Translator.getInstance().translateKey("commands.base.error.invalidnumber") + " " + input);
        }
    }

    public static long parseLong(String input) {
        try {
            return Long.parseLong(input);
        } catch (NumberFormatException nfe) {
            throw new CommandException.InvalidNumber(Translator.getInstance().translateKey("commands.base.error.invalidnumber") + " " + input);
        }
    }

    public static float parseFloat(String input) {
        try {
            float f = Float.parseFloat(input);
            if (Float.isNaN(f)) {
                throw new CommandException.InvalidNumber(Translator.getInstance().translateKey("commands.base.error.nan"));
            } else if (Float.isInfinite(f)) {
                throw new CommandException.InvalidNumber(Translator.getInstance().translateKey("commands.base.error.infinity"));
            } else {
                return f;
            }
        } catch (NumberFormatException nfe) {
            throw new CommandException.InvalidNumber(Translator.getInstance().translateKey("commands.base.error.invalidfloat") + " " + input);
        }
    }

    public static double parseDouble(String input) {
        try {
            double d = Double.parseDouble(input);
            if (Double.isNaN(d)) {
                throw new CommandException.InvalidNumber(Translator.getInstance().translateKey("commands.base.error.nan"));
            } else if (Double.isInfinite(d)) {
                throw new CommandException.InvalidNumber(Translator.getInstance().translateKey("commands.base.error.infinity"));
            } else {
                return d;
            }
        } catch (NumberFormatException nfe) {
            throw new CommandException.InvalidNumber(Translator.getInstance().translateKey("commands.base.error.invalidfloat") + " " + input);
        }
    }

    public static boolean checkItemId(int id, int data) {
        boolean invalidmeta = false;
        boolean nullBlock = false;
        boolean overflow = id < 0 || id > ItemList.items.length;
        if (!overflow) {
            nullBlock = ItemList.items[id] == null;
            if (!nullBlock) {
                invalidmeta = data > ItemList.items[id].getMaxDamage();
            }
        }

        return overflow || nullBlock || invalidmeta;
    }

    public enum EnvType {
        SERVER,
        CLIENT,
        BOTH;
    }

    public enum UserType {
        ADMINISTRATOR,
        OPERATOR,
        USER;
    }
}
