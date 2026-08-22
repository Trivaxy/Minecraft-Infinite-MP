package net.minecraft.client.network;

import java.util.Hashtable;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

public final class ServerAddress {
    public static final int DEFAULT_PORT = 25565;

    private final String host;
    private final int port;

    private ServerAddress(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public static ServerAddress of(String host, int port) {
        return new ServerAddress(host, port);
    }

    public static ServerAddress resolve(String host) {
        String[] record = lookupSrv(host);
        return record == null ? new ServerAddress(host, DEFAULT_PORT) : new ServerAddress(record[0], parsePort(record[1]));
    }

    private static String[] lookupSrv(String host) {
        if (host.isEmpty() || host.indexOf('.') < 0 || isLiteralAddress(host)) {
            return null;
        }

        try {
            Hashtable<String, String> env = new Hashtable<>();
            env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            env.put("java.naming.provider.url", "dns:");
            env.put("com.sun.jndi.dns.timeout.initial", "3000");
            env.put("com.sun.jndi.dns.timeout.retries", "1");
            DirContext context = new InitialDirContext(env);

            try {
                Attributes attributes = context.getAttributes("_minecraft._tcp." + host, new String[]{"SRV"});
                Attribute attribute = attributes.get("SRV");
                if (attribute == null || attribute.size() == 0) {
                    return null;
                }

                String[] parts = attribute.get().toString().split(" ", 4);
                if (parts.length < 4) {
                    return null;
                }

                String target = trimTrailingDot(parts[3]);
                return target.isEmpty() ? null : new String[]{target, parts[2]};
            } finally {
                context.close();
            }
        } catch (Throwable throwable) {
            return null;
        }
    }

    private static boolean isLiteralAddress(String host) {
        if (host.indexOf(':') >= 0) {
            return true;
        }

        int dots = 0;

        for (int index = 0; index < host.length(); index++) {
            char character = host.charAt(index);
            if (character == '.') {
                dots++;
            } else if (character < '0' || character > '9') {
                return false;
            }
        }

        return dots == 3;
    }

    private static String trimTrailingDot(String target) {
        return target.endsWith(".") ? target.substring(0, target.length() - 1) : target;
    }

    private static int parsePort(String port) {
        try {
            int value = Integer.parseInt(port.trim());
            return value > 0 && value <= 65535 ? value : DEFAULT_PORT;
        } catch (NumberFormatException numberFormatException) {
            return DEFAULT_PORT;
        }
    }
}
