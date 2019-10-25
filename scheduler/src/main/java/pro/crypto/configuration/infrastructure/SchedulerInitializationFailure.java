package pro.crypto.configuration.infrastructure;

public class SchedulerInitializationFailure extends RuntimeException {

    public SchedulerInitializationFailure(String message, Throwable cause) {
        super(message, cause);
    }

}
