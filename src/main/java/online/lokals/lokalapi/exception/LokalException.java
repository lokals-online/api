package online.lokals.lokalapi.exception;

public abstract class LokalException extends RuntimeException {

    private LokalException() {
    }

    public LokalException(String message) {
        super(message);
    }

    public LokalException(String message, Throwable cause) {
        super(message, cause);
    }

}
