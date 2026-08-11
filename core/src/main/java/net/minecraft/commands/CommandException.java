package net.minecraft.commands;

public class CommandException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CommandException() {
    }

    public CommandException(String message) {
        super(message);
    }

    public CommandException(Throwable cause) {
        super(cause);
    }

    public CommandException(String message, Throwable cause) {
        super(message, cause);
    }

    public static class InvalidNumber extends CommandException {
        private static final long serialVersionUID = 1L;

        public InvalidNumber() {
        }

        public InvalidNumber(String message) {
            super(message);
        }

        public InvalidNumber(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class NotEnoughArgs extends CommandException {
        private static final long serialVersionUID = 1L;

        public NotEnoughArgs() {
        }

        public NotEnoughArgs(String message) {
            super(message);
        }

        public NotEnoughArgs(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
