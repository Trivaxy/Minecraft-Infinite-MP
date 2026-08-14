package net.minecraft.client;

public class Session {
    public String username;
    public String sessionId;
    public String accessToken;
    public String uuid;

    public Session(String name, String session) {
        this.username = name;
        this.sessionId = session;
        // prism/multimc hand legacy instances their microsoft token as token:<accessToken>:<uuid>
        if (session != null && session.startsWith("token:")) {
            String[] parts = session.split(":");
            if (parts.length >= 3) {
                this.accessToken = parts[1];
                this.uuid = parts[2].replace("-", "");
            }
        }
    }

    public boolean canAuthenticate() {
        return this.accessToken != null && this.uuid != null;
    }
}
