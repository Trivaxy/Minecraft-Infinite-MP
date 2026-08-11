package net.minecraft.commands;

import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.World;
import net.minecraft.localization.Translator;
import net.minecraft.util.property.PropertySource;

public interface CommandAccessor {
    int getWorldCount();

    World getWorld(int var1);

    ConfigManager getConfigManager();

    PropertySource getProperties();

    boolean isServer();

    void initiateShutdown();

    default void clientSetTimer(float tps) {
        throw new UnsupportedOperationException();
    }

    default float clientGetTimer() {
        throw new UnsupportedOperationException();
    }

    default void clientSetGamemode(int gm) {
        throw new UnsupportedOperationException();
    }

    default Player getPlayer(String name) {
        return this.getConfigManager().getPlayer(name);
    }

    default Player getPlayer(CommandSource src) {
        return this.getConfigManager().getPlayer(src.getUsername());
    }

    default Player demandPlayer(CommandSource src) {
        Player player = this.getConfigManager().getPlayer(src.getUsername());
        if (player == null) {
            throw new CommandException(Translator.getInstance().translateKey("commands.base.error.player"));
        } else {
            return player;
        }
    }
}
