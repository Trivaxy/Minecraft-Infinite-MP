package net.minecraft.game.world.chunk.loader.handler;

import com.mojang.nbt.tag.special.CompoundTag;
import net.minecraft.game.entity.player.Player;

public interface PlayerFileData {
    void writePlayerData(Player var1);

    void readPlayerData(Player var1);

    CompoundTag getPlayerData(String var1);
}
