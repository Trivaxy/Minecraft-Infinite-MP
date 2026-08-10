package net.minecraft.game.world;

import net.minecraft.game.ParticleType;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.player.Player;

public interface WorldListener {
    int STEP_SOUND = 2001;
    int POTION_EXPLOSION = 2002;
    int PARTICLE = 2003;

    void changeBlocks(int var1, int var2, int var3);

    void markBlocksDirty(int var1, int var2, int var3, int var4, int var5, int var6);

    void playSound(String var1, double var2, double var4, double var6, float var8, float var9);

    void spawnParticle(ParticleType var1, double var2, double var4, double var6, double var8, double var10, double var12);

    void obtainEntitySkin(Entity var1);

    void releaseEntitySkin(Entity var1);

    void updateAllRenderers();

    void playMobSound(String var1, int var2, double var3, double var5, double var7, float var9, float var10);

    void playBlockSound(String var1, double var2, double var4, double var6, float var8, float var9);

    void playAmbienceSound(String var1, float var2, float var3);

    void sendTileEntity(int var1, int var2, int var3, TileEntity var4);

    void worldEvent(Player var1, int var2, int var3, int var4, int var5, int var6);

    void playRecord(String var1, int var2, int var3, int var4, boolean var5);

    void showString(String var1);
}
