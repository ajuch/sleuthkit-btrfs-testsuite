package cc.juch.btrfs_testsuite.common.image;

/**
 * Created by andreas on 12/8/13.
 */
public class ResetException extends Exception {
    public ResetException() {
        super();
    }

    public ResetException(String message) {
        super(message);
    }

    public ResetException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResetException(Throwable cause) {
        super(cause);
    }
}
