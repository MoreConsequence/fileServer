import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author lhy
 **/
public class FileServer {

    public static final Map<String, ReadWriteLock> MAP = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException, InterruptedException {

        String fileName = "pom.xml";
        Path filePath = Paths.get(fileName);

        final ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(new HandlerTwo(fileName));
        executorService.submit(new HandlerTwo(fileName));




    }
}
