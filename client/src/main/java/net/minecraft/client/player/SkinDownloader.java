package net.minecraft.client.player;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Logger;
import net.minecraft.game.entity.player.Player;
import net.minecraft.util.logging.LoggingUtils;
import org.json.JSONObject;

public class SkinDownloader extends Thread {
    private static Logger logger = Logger.getLogger("Minecraft");
    private final Player player;
    private final String playerName;
    private String decodedJSON;

    public SkinDownloader(Player player) {
        this.setName("Skin getting thread");
        this.setDaemon(true);
        this.player = player;
        this.playerName = player.username;
    }

    @Override
    public void run() {
        try {
            URL nameToUUIDUrl = new URL("https://api.minecraftservices.com/minecraft/profile/lookup/name/" + this.playerName);
            HttpURLConnection connection = (HttpURLConnection)nameToUUIDUrl.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.connect();
            if (connection.getResponseCode() != 200) {
                throw new Exception("Failed to get UUID: Server responded with non-200 HTTP code.");
            }

            InputStream is = connection.getInputStream();
            byte[] out = new byte[is.available()];
            is.read(out, 0, is.available());
            String s = new String(out, StandardCharsets.UTF_8);
            JSONObject root = new JSONObject(s);
            String uuid = root.getString("id");
            URL profileURL = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
            connection = (HttpURLConnection)profileURL.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.connect();
            if (connection.getResponseCode() != 200) {
                throw new Exception("Failed to get skin: Server responded with non-200 HTTP code.");
            }

            is = connection.getInputStream();
            out = new byte[is.available()];
            is.read(out, 0, is.available());
            s = new String(out, StandardCharsets.UTF_8);
            root = new JSONObject(s);

            for (Object object : root.getJSONArray("properties")) {
                if (((JSONObject)object).has("name") && ((JSONObject)object).getString("name").equals("textures")) {
                    this.decodedJSON = new String(Base64.getDecoder().decode(((JSONObject)object).getString("value")), StandardCharsets.UTF_8);
                }
            }

            root = new JSONObject(this.decodedJSON);

            try {
                JSONObject empty = new JSONObject();
                String skinURL = root.optJSONObject("textures", empty).optJSONObject("SKIN", empty).optString("url", "");
                String capeURL = root.optJSONObject("textures", empty).optJSONObject("CAPE", empty).optString("url", "");
                String modelType = root.optJSONObject("textures", empty).optJSONObject("SKIN", empty).optJSONObject("metadata", empty).optString("model", "");
                if (!skinURL.isEmpty()) {
                    this.player.skinUrl = skinURL;
                }

                if (!capeURL.isEmpty()) {
                    this.player.cloakUrl = capeURL;
                }

                if (modelType.equals("slim")) {
                    this.player.isSlim = true;
                }

                if (this.player != null) {
                    this.player.foundSkin = true;
                }

                assert this.player != null;
                this.player.world.obtainEntitySkin(this.player);
            } catch (Exception e) {
                LoggingUtils.warnThrowable(logger, "Failed to apply skin?", e);
            }
        } catch (Exception e) {
            LoggingUtils.warnf(logger, e, "Failed to get skin for %s", this.playerName);
        }
    }
}
