import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author lhy
 **/
public class HandlerTwo implements Runnable {

    private String file;

    public HandlerTwo(String file) {
        this.file = file;
    }

    @Override
    public void run() {

        Lock lock = getLock(file, true);
        lock.lock();

        System.out.println("Get Write Lock!!");
        System.out.println(Thread.currentThread().getName());
        System.out.println();
        try {
            TimeUnit.HOURS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lock.unlock();
    }


    public Lock getWriteLock(String file) {
        return getLock(file, true);
    }

    public Lock getReadLock(String file) {
        return getLock(file, false);
    }
    public Lock getLock(String file, boolean write) {
        ReadWriteLock lock = FileServer.MAP.get(file);
        if (lock == null) {
            synchronized (FileServer.MAP) {
                lock = FileServer.MAP.get(file);
                if (lock == null) {
                    FileServer.MAP.put(file, lock = new ReentrantReadWriteLock());
                }
            }
        }
        if (write) {
            return lock.writeLock();
        } else {
            return lock.readLock();
        }
    }
}
