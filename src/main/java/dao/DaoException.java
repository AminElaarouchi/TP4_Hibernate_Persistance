package dao;

/** Signale une opération de persistance annulée par rollback. */
public class DaoException extends RuntimeException {
    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
