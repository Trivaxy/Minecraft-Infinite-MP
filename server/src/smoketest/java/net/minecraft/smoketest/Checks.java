package net.minecraft.smoketest;

/**
 * Pass/fail tally with the output format the smoke suite prints.
 *
 * A check is only worth writing if you have watched it go red - revert the fix,
 * run it, see the failure, put the fix back. A green check that has never been
 * red proves nothing about the code, only about itself.
 */
public final class Checks {

    private int failures;

    public boolean check(String name, boolean ok) {
        return check(name, ok, "");
    }

    public boolean check(String name, boolean ok, String detail) {
        System.out.println("[test] " + (ok ? "PASS  " : "FAIL  ") + name
                + (ok || detail == null || detail.isEmpty() ? "" : "   -> " + detail));
        if (!ok) {
            failures++;
        }
        return ok;
    }

    public void note(String message) {
        System.out.println("[test] " + message);
    }

    public void skip(String name) {
        System.out.println("[test] SKIP  " + name);
    }

    public int failures() {
        return failures;
    }

    public void add(int moreFailures) {
        failures += moreFailures;
    }

    /** Prints the verdict, drops every bot and exits 1 if anything failed. */
    public void done() {
        System.out.println(failures == 0 ? "[test] RESULT: ALL PASS" : "[test] RESULT: " + failures + " FAILURE(S)");
        Bot.closeAll();
        System.exit(failures == 0 ? 0 : 1);
    }

    /** For a setup failure that makes the rest of the run meaningless. */
    public void abort(String reason) {
        System.out.println("[test] RESULT: FAIL - " + reason);
        Bot.closeAll();
        System.exit(1);
    }
}
