package me.pqpo.plocklib;

import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 *
 * PLock is a simple and efficient cross-process lock, also support read-write lock.
 *
 * Created by pqpo on 2018/4/28.
 */
public class PLock {

    private static final String TAG = "PLock";

    static {
        System.loadLibrary("plock-lib");
    }

    private static volatile PLock sPLock = null;

    public static void setDefault(PLock pLock) {
        if (pLock != null) {
            sPLock = pLock;
        }
    }

    public static PLock getDefault() {
        if (sPLock == null) {
            synchronized (PLock.class) {
                sPLock = new PLock();
            }
        }
        return sPLock;
    }

    public static void releaseDefault() {
        if (sPLock != null) {
            sPLock.release();
        }
        sPLock = null;
    }

    private long ptr = 0;

    public PLock() {
        this(getDefaultFile());
    }

    public PLock(String file) {
        ptr = initPLockNative(file);
    }

    /**
     * Acquires the block lock.
     */
    public boolean lock() {
        return writeLock();
    }

    /**
     * Acquires the lock.
     */
    public boolean tryLock() {
        return tryWriteLock();
    }

    /**
     * Acquires the read block lock.
     */
    public boolean readLock() {
        if (ptr != 0) {
            try {
                return readLockNative(ptr);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
        return false;
    }

    /**
     * Acquires the write block lock.
     */
    public boolean writeLock() {
        if (ptr != 0) {
            try {
                return writeLockNative(ptr);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
        return false;
    }

    /**
     * Acquires the read lock.
     */
    public boolean tryReadLock() {
        if (ptr != 0) {
            try {
                return tryReadLockNative(ptr);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
        return false;
    }

    /**
     * Acquires the write lock.
     */
    public boolean tryWriteLock() {
        if (ptr != 0) {
            try {
                return tryWriteLockNative(ptr);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
        return false;
    }

    /**
     * unlock
     * @return always return true except unlockNative throws errors
     */
    public boolean unlock() {
        if (ptr != 0) {
            try {
                return unlockNative(ptr);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
        return false;
    }

    public void release() {
        if (ptr != 0) {
            try {
                releaseNative(ptr);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
        ptr = 0;
    }

    private native long initPLockNative(String file);
    private native boolean readLockNative(long ptr);
    private native boolean writeLockNative(long ptr);
    private native boolean tryReadLockNative(long ptr);
    private native boolean tryWriteLockNative(long ptr);
    private native boolean unlockNative(long ptr);
    private native void releaseNative(long ptr);

    private static String getDefaultFile() {
        File file = new File(Environment.getExternalStorageDirectory(), ".PLockDefault");
        return file.getAbsolutePath();
    }

}
