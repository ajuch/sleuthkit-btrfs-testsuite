package cc.juch.btrfs_testsuite.common.image;

/**
 * Created by andreas on 12/8/13.
 */
public class PrepareException extends Exception {
    public PrepareException() {
        super();
    }

    public PrepareException(String message) {
        super(message);
    }

    public PrepareException(String message, Throwable cause) {
        super(message, cause);
    }

    public PrepareException(Throwable cause) {
        super(cause);
    }
}
