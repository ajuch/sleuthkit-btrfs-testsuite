package cc.juch.btrfs_testsuite.common.image;

/**
 * Created by andreas on 12/6/13.
 */
public class MountException extends Exception {
    public MountException() {
        super();
    }

    public MountException(String message) {
        super(message);
    }

    public MountException(String message, Throwable cause) {
        super(message, cause);
    }

    public MountException(Throwable cause) {
        super(cause);
    }
}
