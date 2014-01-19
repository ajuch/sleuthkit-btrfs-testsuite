package cc.juch.btrfs_testsuite.common.filesystem;

/**
 * Created by andreas on 12/8/13.
 */
public class FormatException extends Exception {
    public FormatException() {
        super();
    }

    public FormatException(String message) {
        super(message);
    }

    public FormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public FormatException(Throwable cause) {
        super(cause);
    }
}
