package net.minecraft.server.player.bans;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class BanEntry {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    public static Logger loggerBanEntry = Logger.getLogger("Minecraft");
    private final String username;
    private Date banStartDate = new Date();
    private String bannedBy = "(Unknown)";
    private Date banEndDate = null;
    private String reason = "Banned by an operator.";

    public BanEntry(String par1Str) {
        this.username = par1Str;
    }

    public String getBannedUsername() {
        return this.username;
    }

    public Date banStart() {
        return this.banStartDate;
    }

    public void setBanStartDate(Date par1Date) {
        this.banStartDate = par1Date != null ? par1Date : new Date();
    }

    public String getBannedBy() {
        return this.bannedBy;
    }

    public void setBannedBy(String par1Str) {
        this.bannedBy = par1Str;
    }

    public Date getBanEndDate() {
        return this.banEndDate;
    }

    public void setBanEndDate(Date par1Date) {
        this.banEndDate = par1Date;
    }

    public boolean hasBanExpired() {
        return this.banEndDate != null && this.banEndDate.before(new Date());
    }

    public String getBanReason() {
        return this.reason;
    }

    public void setBanReason(String par1Str) {
        this.reason = par1Str;
    }

    public String buildBanString() {
        return this.getBannedUsername()
            + "|"
            + dateFormat.format(this.banStart())
            + "|"
            + this.getBannedBy()
            + "|"
            + (this.getBanEndDate() == null ? "Forever" : dateFormat.format(this.getBanEndDate()))
            + "|"
            + this.getBanReason();
    }

    public static BanEntry parse(String par0Str) {
        if (par0Str.trim().length() < 2) {
            return null;
        }

        String[] var1 = par0Str.trim().split(Pattern.quote("|"), 5);
        BanEntry var2 = new BanEntry(var1[0].trim());
        byte var3 = 0;
        int var7 = var3 + 1;
        if (var1.length > var7) {
            try {
                var2.setBanStartDate(dateFormat.parse(var1[var7].trim()));
            } catch (ParseException var6) {
                loggerBanEntry.log(
                    Level.WARNING, "Could not read creation date format for ban entry '" + var2.getBannedUsername() + "' (was: '" + var1[var7] + "')", var6
                );
            }

            if (var1.length > ++var7) {
                var2.setBannedBy(var1[var7].trim());
                if (var1.length > ++var7) {
                    try {
                        String var4 = var1[var7].trim();
                        if (!var4.equalsIgnoreCase("Forever") && !var4.isEmpty()) {
                            var2.setBanEndDate(dateFormat.parse(var4));
                        }
                    } catch (ParseException var5) {
                        loggerBanEntry.log(
                            Level.WARNING,
                            "Could not read expiry date format for ban entry '" + var2.getBannedUsername() + "' (was: '" + var1[var7] + "')",
                            var5
                        );
                    }

                    if (var1.length > ++var7) {
                        var2.setBanReason(var1[var7].trim());
                    }
                }
            }
        }

        return var2;
    }
}
