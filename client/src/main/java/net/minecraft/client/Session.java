package net.minecraft.client;

public class Session {
    public String username;
    public String sessionId;

    public Session(String name, String session) {
        this.username = name;
        this.sessionId = session;
    }
}
